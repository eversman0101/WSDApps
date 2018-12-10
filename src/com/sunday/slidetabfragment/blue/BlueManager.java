package com.sunday.slidetabfragment.blue;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.wisdom.app.activity.R;
import com.wisdom.app.utils.MissionSingleInstance;

import java.util.concurrent.atomic.AtomicInteger;

import static com.sunday.slidetabfragment.blue.Config.MODE_CLASSIC;

/**
 * 蓝牙助手类，单例，代理所有蓝牙操作。主要功能：蓝牙的开关和读写、蓝牙搜索、蓝牙状态广播监测、蓝牙通道连接状态监测。
 * <p>计划支持低功耗蓝牙和经典蓝牙模式，但目前只实现经典蓝牙，低功耗待验证。</p>
 *
 * @author ShiPengHao
 * @date 2017/10/23
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class BlueManager {
    private final String TAG = "BlueManager";
    /**
     * 蓝牙连接状态监控Action
     */
    public static final String BLUE_STATE_ACTION = "BlueToothConnectionState";
    /**
     * 蓝牙连接状态监控IntentFilter
     */
    public static final IntentFilter BLUE_STATE_INTENT_FILTER = new IntentFilter(BLUE_STATE_ACTION);
    /*
     * ctx
     */
    private final Context mContext;
    /**
     * 蓝牙adapter
     */
    private final BluetoothAdapter fBlueAdapter = BluetoothAdapter.getDefaultAdapter();
    /**
     * 目标蓝牙设备mac地址
     */
    private String mRemoteMac;
    /**
     * 扫描设备定时时间
     */
    private long mScanEndMillis;
    /**
     * 蓝牙广播接收器
     */
    private final BroadcastReceiver fBlueReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equalsIgnoreCase(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                LogUtil.i(TAG, "ACTION_FOUND:" + device.getName() + "," + device.getAddress());
                if (mRemoteMac.equalsIgnoreCase(device.getAddress())) {
                    connectDevice(device);
                    fBlueAdapter.cancelDiscovery();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equalsIgnoreCase(action)) {
                LogUtil.i(TAG, "ACTION_DISCOVERY_FINISHED");
                if (fBlueAdapter.isEnabled() && null == mBlueConn) {
                    if (System.currentTimeMillis() > mScanEndMillis) {
                        // 继续扫描
                        scan();
                    } else {
                        fListenerProxy.onDiscoveryFailed(fBlueAdapter.getRemoteDevice(mRemoteMac));
                        fBlueAdapter.cancelDiscovery();
                    }
                }
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equalsIgnoreCase(action)) {
                LogUtil.i(TAG, "ACTION_STATE_CHANGED");
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -100);
                if (BluetoothAdapter.STATE_ON == state) {
                    LogUtil.i(TAG, "STATE_ON");
                    if (null == mBlueConn) {
                        scan();
                    }
                } else if (BluetoothAdapter.STATE_OFF == state) {
                    LogUtil.i(TAG, "STATE_OFF");
                    if (null != mBlueConn) {
                        fListenerProxy.onDisconnect(fBlueAdapter.getRemoteDevice(mRemoteMac));
                    }
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equalsIgnoreCase(action)) {
                LogUtil.i(TAG, "ACTION_BOND_STATE_CHANGED");
                if (null != mBlueBinder && null != mRemoteMac) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -100);
                    LogUtil.i(TAG, "bind state :" + state);
                    if (null != device && mRemoteMac.equalsIgnoreCase(device.getAddress()) && state == BluetoothDevice.BOND_BONDED) {
                        mBlueBinder.setBonded();
                    }
                }
            }
        }

    };

    /**
     * 蓝牙服务连接对象
     */
    private ServiceConnection mBlueConn;
    /**
     * 蓝牙服务binder
     */
    private BlueService.BlueSocketBinder mBlueBinder;
    /**
     * 处理蓝牙连接状态回调的Handler，应该在主线程中初始化
     */
    private final Handler fHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            String msg = (String) message.obj;
            LogUtil.i(TAG, "handler message:" + msg);
            notifyConnState(message.what, msg);
            fStatus.set(message.what);
            if (message.what != STATUS_CONNECT) {
                close();
            }
            return true;
        }
    });

    /**
     * 通知连接状态
     *
     * @param status 状态
     * @param msg    状态信息
     */
    private void notifyConnState(int status, String msg) {
        if (Config.SHOW_STATE_LOCAL_BROADCAST) {
            Intent intent = new Intent(BLUE_STATE_ACTION);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent.putExtra(Config.EXTRA_KEY_DES, msg).putExtra(Config.EXTRA_KEY_STATUS, status));
        }
        if (Config.SHOW_STATE_NOTIFICATION) {
            sendNotification(msg);
        }
        if (Config.SHOW_STATE_TOAST) {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 蓝牙服务监听代理
     */
    private final BlueConnListener fListenerProxy = new BlueConnListener() {

        /**
         * 拿到蓝牙连接状态变化之后，设置状态并弹出通知，并发送广播
         * @param msg 状态变化的消息
         * @param status 状态
         */
        private void onResult(String msg, int status) {
            Message message = Message.obtain();
            message.what = status;
            message.obj = msg;
            fHandler.sendMessage(message);
        }

        @Override
        public void onConnect(BluetoothDevice remoteDevice) {
            //LockStateUtil.reset();
            onResult("蓝牙连接成功！MAC:" + remoteDevice.getAddress(), STATUS_CONNECT);
        }

        @Override
        public void onDisconnect(BluetoothDevice remoteDevice) {
            //LockStateUtil.checkLockState(mContext);
        	MissionSingleInstance.getSingleInstance().setYuan_state(false);
            onResult("连接失败！MAC:" + remoteDevice.getAddress(), STATUS_DISCONNECT);
        }

        @Override
        public void onPairedFailed(BluetoothDevice remoteDevice) {
            onResult("配对失败！MAC:" + remoteDevice.getAddress(), STATUS_PAIRED_ERROR);
        }

        @Override
        public void onDiscoveryFailed(BluetoothDevice remoteDevice) {
            onResult("没有发现设备！MAC:" + remoteDevice.getAddress(), STATUS_PAIRED_ERROR);
        }
    };

    /**
     * 连接状态：正常
     */
    public static final int STATUS_CONNECT = 0;
    /**
     * 连接状态：断开连接
     */
    public static final int STATUS_DISCONNECT = 1;
    /**
     * 连接状态：配对失败
     */
    public static final int STATUS_PAIRED_ERROR = 2;
    /**
     * 连接状态：还未建立连接
     */
    public static final int STATUS_UN_REQUEST = 3;
    /**
     * 状态
     */
    private final AtomicInteger fStatus = new AtomicInteger(STATUS_UN_REQUEST);

    /**
     * 单例
     */
    @SuppressLint("StaticFieldLeak")
    private static BlueManager instance;
    /**
     * 模式：默认经典蓝牙
     */
    @Config.Mode
    private int mMode = MODE_CLASSIC;
    /**
     * 低功耗蓝牙搜索回调
     */
    private final BluetoothAdapter.LeScanCallback fLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            LogUtil.i(TAG, "LeScanCallback::onLeScan, device MAC:" + device.getAddress());
            if (null != mRemoteMac && mRemoteMac.equalsIgnoreCase(device.getAddress())) {
                connectDevice(device);
                fBlueAdapter.stopLeScan(this);
            } else if (System.currentTimeMillis() > mScanEndMillis) {
                fListenerProxy.onDiscoveryFailed(fBlueAdapter.getRemoteDevice(mRemoteMac));
                fBlueAdapter.stopLeScan(this);
            }
        }
    };


    /**
     * 初始化，构造单例，最好放在应用初始化时。基于延迟加载考虑的话也可以后延，但需注意空指针异常。
     *
     * @param context ctx
     */
    @MainThread
    public synchronized static void init(@NonNull Context context) {
        if (null == instance) {
            instance = new BlueManager(context);
        }
    }

    /**
     * 私有构造
     *
     * @param context ctx
     */
    private BlueManager(@NonNull Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * 获取实例
     *
     * @return 单例
     */
    public static BlueManager getInstance() {
        return instance;
    }

    /**
     * 复位蓝牙通道
     *
     * @param mac 目标蓝牙mac
     */
    public synchronized void reset(@NonNull String mac) {
        if (isConnect() && mac.equalsIgnoreCase(mRemoteMac)) {
            return;
        }
        mContext.startActivity(new Intent(mContext, BlueConnActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        close(false, false);
        mRemoteMac = mac.toUpperCase();
        open();
    }

    /**
     * 打开蓝牙通道
     */
    private void open() {
        if (null == mRemoteMac || !mRemoteMac.matches(Config.MAC_PATTERN)) {
            throw new IllegalStateException("蓝牙地址还未设置");
        }
        LogUtil.i(TAG, "open address:" + mRemoteMac);
        registerBTReceiver();
        // 如果蓝牙已经启动，则搜索目标设备，否则开启蓝牙，在状态回调中开始搜索设备
        if (fBlueAdapter.isEnabled()) {
            scan();
        } else {
            fBlueAdapter.enable();
        }
    }

    /**
     * 关闭当前连接通道和蓝牙，不显示通知，通常用于回调或者应用退出
     */
    public void close() {
        close(false, false);
    }

    /**
     * 关闭当前连接通道
     *
     * @param closeBlue 是否关闭蓝牙
     * @param showTip   是否显示通知，用于用户手动关闭蓝牙通道时
     */
    public void close(boolean closeBlue, boolean showTip) {
        if (null == mRemoteMac) {
            fStatus.set(STATUS_UN_REQUEST);
            return;
        }
        if (showTip) {
            LogUtil.i(TAG, "show tip");
            notifyConnState(STATUS_DISCONNECT,"连接失败！MAC:" + mRemoteMac);
        }
        LogUtil.i(TAG, "close address:" + mRemoteMac);
        LogUtil.i(TAG, "unregisterReceiver");
        mContext.unregisterReceiver(fBlueReceiver);
        if (null != mBlueConn) {
            LogUtil.i(TAG, "unbindService");
            mContext.unbindService(mBlueConn);
        }
        if (fBlueAdapter.isDiscovering()) {
            LogUtil.i(TAG, "cancelDiscovery");
            fBlueAdapter.cancelDiscovery();
        }
        LogUtil.i(TAG, "stopLeScan");
        fBlueAdapter.stopLeScan(fLeScanCallback);
        if (closeBlue) {
            LogUtil.i(TAG, "disable blue");
            fBlueAdapter.disable();
        }
        mRemoteMac = null;
        mBlueBinder = null;
        mBlueConn = null;
        mScanEndMillis = 0;
        fStatus.set(STATUS_UN_REQUEST);
        LogUtil.i(TAG, "reset member v");
    }

    /**
     * 查询蓝牙通道状态
     *
     * @return 状态
     */
    public int getStatus() {
        return fStatus.get();
    }

    /**
     * 蓝牙是否连接成功
     *
     * @return true是
     */
    public boolean isConnect() {
        return fStatus.get() == STATUS_CONNECT;
    }

    /**
     * 蓝牙通道写字节码
     *
     * @param bytes 字节码
     * @return 状态码，成功{@link #STATUS_CONNECT}
     */
    @WorkerThread
    public synchronized int write(byte[] bytes) {
        if (null != mBlueBinder && isConnect() && mBlueBinder.write(bytes)) {// 连接正常
            LogUtil.i(TAG, "write:" + DataFormatter.bytes2HexString(bytes));
            return STATUS_CONNECT;
        }
        return fStatus.get();
    }

    /**
     * 马上读取一帧字节码
     *
     * @param protocolSign 协议标识
     * @return 字节码。如果解析数据有误，返回null
     */
    @WorkerThread
    public synchronized byte[] read(ProtocolSign protocolSign) {
        if (null != mBlueBinder && isConnect()) {
            LogUtil.i(TAG, "read");
            byte[] head = mBlueBinder.readBytes(protocolSign.getSignLen(),protocolSign);
            
            if (null == head) {
                if (isConnect()) {
                    //fListenerProxy.onDisconnect(fBlueAdapter.getRemoteDevice(mRemoteMac));
                }
                Log.e(TAG,"head is null");
                return null;
            }
            LogUtil.i(TAG, "head:" + DataFormatter.bytes2HexString(head));
            if (protocolSign.checked(head)) {
                LogUtil.i(TAG, "head checked");
                int len = protocolSign.getBodyLen(head);
                LogUtil.i(TAG, "body len:" + len);
                byte[] body = mBlueBinder.readBytes(len,protocolSign);
                if (null == body) {
                    if (isConnect()) {
                        fListenerProxy.onDisconnect(fBlueAdapter.getRemoteDevice(mRemoteMac));
                    }
                    return null;
                }
                len = head.length + body.length;
                LogUtil.i(TAG, "total length:" + len);
                byte[] result = new byte[len];
                System.arraycopy(head, 0, result, 0, head.length);
                System.arraycopy(body, 0, result, head.length, body.length);
                LogUtil.i(TAG, "read result:" + DataFormatter.bytes2HexString(result));
                if (protocolSign.filterHeartFrame(result)){
                    LogUtil.i(TAG,"this frame is a heart frame, drop it, read next frame");
                    return read(protocolSign);
                }
                else {
                    return result;
                }
            }
            else if(protocolSign.filterModelFrame(head))
            {
            	LogUtil.i(TAG,"this frame is model initial frame, drop it, read next frame");
            	mBlueBinder.readBytes(2,protocolSign);
            	return read(protocolSign);
            }
            else if((head[0]==(byte)0xff)&&(head[1]==(byte)0xff)&&(head[2]==(byte)0xff)&&(head[3]==(byte)0xff)&&(head[4]==(byte)0xff)&&(head[5]==(byte)0xff))
            {
            	//设备无应答，上一层重发。
            	Log.e("BlueManager", "head no respond");
            	return head;
            }
            else if (isConnect()) {
            	//设备有应答，但校验不通过，上一层继续接受
            	Log.e("BlueManager", "head not checked!");
            	return null;
            }
        }
        return null;
    }

    /**
     * 注册蓝牙相关事件广播
     */
    private void registerBTReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.setPriority(Integer.MAX_VALUE);
        mContext.registerReceiver(fBlueReceiver, filter);
    }

    /**
     * 向通知栏发送蓝牙状态通知
     *
     * @param msg 通知内容
     */
    private void sendNotification(String msg) {
        final NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            Notification notification = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.bluetooth)
                    .setTicker("蓝牙连接状态")
                    .setContentTitle(msg.substring(0, msg.indexOf("MAC")))
                    .setContentText(msg.substring(msg.indexOf("MAC")))
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            manager.notify(Config.BLUE_NOTIFICATION_ID, notification);
        }
    }

    /**
     * 清除蓝牙状态的通知
     */
    public void clearNotification() {
        if (Config.SHOW_STATE_NOTIFICATION) {
            NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (null != manager) {
                manager.cancel(Config.BLUE_NOTIFICATION_ID);
            }
        }
    }

    /**
     * 设置蓝牙模式
     *
     * @param mode 模式
     */
    public void setMode(@Config.Mode int mode) {
        this.mMode = mode;
    }

    /**
     * 开始扫描附近蓝牙设备，发现目标设备后停止扫描，开始连接
     */
    private void scan() {
        LogUtil.i(TAG, "start scan, mode:" + mMode);
        if (mScanEndMillis < System.currentTimeMillis()) {
            LogUtil.i(TAG, "set scan limit time");
            mScanEndMillis = System.currentTimeMillis() + Config.SCAN_WAIT_TIME_SECONDS * 1000;
        }
        if (mMode == Config.MODE_CLASSIC) {
            try {
                fBlueAdapter.cancelDiscovery();
            } catch (Exception e) {
                e.printStackTrace();
            }
            fBlueAdapter.startDiscovery();
        } else if (mMode == Config.MODE_LE) {
            try {
                fBlueAdapter.stopLeScan(fLeScanCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
            fBlueAdapter.startLeScan(fLeScanCallback);
        }
    }

    /**
     * 开启与该设备的数据连接，这里交给服务去执行
     *
     * @param device 目标蓝牙设备
     */
    private void connectDevice(final BluetoothDevice device) {
        // 运行期间仅执行一次，连接失败的回调也交给页面处理
        if (null == mBlueConn) {
            mBlueConn = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    if (null == service) {
                        throw new IllegalArgumentException("绑定服务时传递的参数异常");
                    } else {
                        LogUtil.i(TAG, "get service binder");
                        mBlueBinder = (BlueService.BlueSocketBinder) service;
                        mBlueBinder.setListener(fListenerProxy);
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    LogUtil.i(TAG, "onServiceDisconnected");
                    fListenerProxy.onDisconnect(device);
                }
            };
            Intent service = new Intent(mContext, BlueService.class)
                    .putExtra(Config.EXTRA_KEY_MAC, mRemoteMac)
                    .putExtra(Config.EXTRA_KEY_MODE, mMode);
            mContext.bindService(service, mBlueConn, Context.BIND_AUTO_CREATE);
        }
    }

}
