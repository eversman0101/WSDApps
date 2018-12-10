package com.sunday.slidetabfragment.blue;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.wisdom.app.utils.ByteUtil;

/**
 * <p>蓝牙通讯服务，主要完成配对、通道建立、数据读写。</p>
 * <p>计划支持低功耗蓝牙和经典蓝牙模式，但目前只实现经典蓝牙，低功耗待验证。</p>
 *
 * @author ShiPengHao
 * @date 2017/10/24
 */
public class BlueService extends Service {
    private final String TAG = "BlueService";
    /**
     * 通讯服务binder
     */
    private BlueSocketBinder mBinder;

    /**
     * 通讯服务功能binder
     */
    class BlueSocketBinder extends Binder {
        /**
         * 远程蓝牙设备
         */
        private final BluetoothDevice mRemoteDevice;
        /**
         * 模式：经典、低功耗
         */
        @Config.Mode
        private final int mMode;
        /**
         * 缓存读取到的字节流的队列，不管是哪种模式，都将通道读取到的数据缓存入此队列末尾，都从此队列头部读取
         */
        private final ConcurrentLinkedQueue<Integer> mReadBytes = new ConcurrentLinkedQueue<>();
        /**
         * 运行标记
         */
        private volatile boolean mRunningFlag = true;
        /**
         * 经典蓝牙socket输入流
         */
        private InputStream mInputStream;
        /**
         * 经典蓝牙socket输出流
         */
        private OutputStream mOutputStream;
        /**
         * 蓝牙通道状态监听器
         */
        private BlueConnListener mListener;
        /**
         * 经典蓝牙设备绑定成功标记，保持线程间的可见性
         */
        private volatile boolean mBonded;
        /**
         * 低功耗蓝牙连接
         */
        private BluetoothGatt mLeGATT;
        /**
         * 低功耗蓝牙Characteristic
         */
        private BluetoothGattCharacteristic mGATTCharacter;
        /**
         * 低功耗蓝牙mtu，默认20
         */
        private int mGATTMtu = 20;
        /**
         * 低功耗蓝牙处理接收数据的handler
         */
        private Handler mLeReceiveHandler;

        /**
         * 创建并开启工作线程，连接设备，读写数据
         *
         * @param mac  目标远程设备mac地址
         * @param mode 模式：经典、低功耗
         */
        private BlueSocketBinder(@NonNull String mac, @Config.Mode int mode) {
            LogUtil.i(TAG, "create BlueSocketBinder");
            mMode = mode;
            // 根据mac地址获取设备
            mRemoteDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mac);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(500);// 等待客户端（BlueManager）设置listener
                    LogUtil.i(TAG, "mode:" + mMode);
                    if (mMode == Config.MODE_LE) {
                        startLE();
                    } else if (mMode == Config.MODE_CLASSIC) {
                        startClassic();
                    }

                }
            }).start();
            if (mMode == Config.MODE_LE) {
                HandlerThread handlerThread = new HandlerThread("LeReceiveHandler");
                handlerThread.start();
                Looper looper = handlerThread.getLooper();
                mLeReceiveHandler = new Handler(looper) {
                    @Override
                    public void handleMessage(Message msg) {
                        LogUtil.i(TAG, "handleMessage");
                        receiveByteFromCharacter((byte[]) msg.obj);
                    }
                };
            }
        }

        /**
         * 设置客户端监听
         *
         * @param listener 客户端监听
         */
        void setListener(BlueConnListener listener) {
            mListener = listener;
        }

        /**
         * 停止工作，释放资源
         */
        private void release() {
            mRunningFlag = false;
            if (mMode == Config.MODE_LE) {
                mLeReceiveHandler.removeCallbacksAndMessages(null);
                mLeReceiveHandler.getLooper().quitSafely();
                mLeGATT.disconnect();
                mLeGATT.close();
                return;
            }
            if (null != mInputStream) {
                try {
                    mInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != mOutputStream) {
                try {
                    mOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 发生异常时，按照断开连接事件来处理
         */
        private void onException() {
            LogUtil.i(TAG, "service exception");
            onConnStatusChanged(false);
        }

        /**
         * 处理连接状态。如果有监听器，调用其回调；如果没有，自己处理
         *
         * @param isConnect 连接true，断开连接false
         */
        private void onConnStatusChanged(boolean isConnect) {
            if (isConnect) {
                if (null != mListener) {
                    // 使用新线程，防止直接在蓝牙连接的线程中进行后续操作
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onConnect(mRemoteDevice);
                        }
                    }).start();
                }
            } else {
                if (null == mListener) {
                    stopSelf();
                } else {
                    mListener.onDisconnect(mRemoteDevice);
                }
            }
        }

        /**
         * 经典蓝牙：从输入流中不停的读取数据，每次1个字节
         */
        private void receiveByteFromStream() throws IOException {
            int readByte;
            while (mRunningFlag) {
                readByte = mInputStream.read();
                if (-1 != readByte) {
                    LogUtil.i(TAG, "classic cache byte:" + DataFormatter.byte2HexString((byte) readByte));
                    mReadBytes.offer(readByte);
                    SystemClock.sleep(5);
                }
            }
        }

        /**
         * 低功耗蓝牙，在蓝牙连接回调中主动调用此方法，读取蓝牙数据区缓存的字节
         *
         * @param value 数据
         */
        private void receiveByteFromCharacter(byte[] value) {
            for (byte readByte : value) {
                LogUtil.i(TAG, "le cache byte:" + DataFormatter.byte2HexString(readByte));
                mReadBytes.offer((int) readByte);
                SystemClock.sleep(5);
            }
        }

        /**
         * 读取指定数量的字节
         *
         * @param count 数量
         * @return 字节数组
         */
        @Nullable
        synchronized byte[] readBytes(int count,ProtocolSign protocolSign) {
            //@IntRange(from = 1, to = 512)
        	Log.e(TAG, "mRunningFlag:"+mRunningFlag);
            if (mRunningFlag) {
                LogUtil.i(TAG, "read bytes count:" + count);
                long deadTime = System.currentTimeMillis() + Config.READ_WAIT_TIME_MILLIS;
                byte[] result = new byte[count];
                if(count==protocolSign.getSignLen())
                {
                	for(int i=0;i<count;i++)
                	{
                		result[i]=(byte)0xff;
                	}
                }
                int index = 0;
                byte readByte;
                while (index < count && System.currentTimeMillis() < deadTime) {
                    LogUtil.i(TAG, "read loop");
                    if (!mRunningFlag) {
                        return null;
                    }
                    Integer integer = mReadBytes.poll();
                    if (null == integer) {
                        SystemClock.sleep(5);
                    } else {
                        readByte = (byte) (integer.intValue());
                        result[index++] = readByte;
                        LogUtil.i(TAG, "read legal byte hex:" + DataFormatter.byte2HexString(readByte));
                    }
                }
                LogUtil.i(TAG, "read bytes over, count=" + index + ":" + DataFormatter.bytes2HexString(result));
                return result;
            } else {
                return null;
            }
        }

        /**
         * 写数据
         *
         * @param bytes 字节码
         */
        synchronized boolean write(byte[] bytes) {
            if (mRunningFlag) {
                LogUtil.i(TAG, "write :" + DataFormatter.bytes2HexString(bytes));
                try {
                    if (mMode == Config.MODE_CLASSIC) {
                        mOutputStream.write(bytes);
                    } else if (mMode == Config.MODE_LE) {
                        final int LENGTH = bytes.length;
                        final int MTU = mGATTMtu - mGATTMtu % 10;
                        LogUtil.i(TAG, String.format("mtu:%s, len:%s", MTU, LENGTH));
                        if (LENGTH <= MTU) {
                            LogUtil.i(TAG, "write once");
                            return mGATTCharacter.setValue(bytes) && mLeGATT.writeCharacteristic(mGATTCharacter);
                        } else {
                            byte[] buffer = null;
                            int count = 0;
                            while (count < LENGTH) {
                                if (!mRunningFlag){
                                    return false;
                                }
                                LogUtil.i(TAG, "write loop , count:" + count);
                                int bufferLen = (LENGTH - count) > MTU ? MTU : (LENGTH - count);
                                if (bufferLen == 0){
                                    break;
                                }
                                buffer = new byte[bufferLen];
                                System.arraycopy(bytes, count, buffer, 0, bufferLen);
                                if (mRunningFlag && mGATTCharacter.setValue(buffer)) {
                                    mLeGATT.writeCharacteristic(mGATTCharacter);
                                    count += bufferLen;
                                    LogUtil.i(TAG, "count:" + count);
                                    SystemClock.sleep(20);
                                } else {
                                    return false;
                                }
                            }
                            return true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onException();
                }
            }
            return false;
        }

        /**
         * 开始低功耗蓝牙连接和通道建立
         */
        private void startLE() {
            LogUtil.i(TAG, "conn: le");
            mLeGATT = mRemoteDevice.connectGatt(BlueService.this, false, new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    if (mRunningFlag) {
                        LogUtil.i(TAG, "BluetoothGattCallback::onConnectionStateChange");
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            if (newState == BluetoothProfile.STATE_CONNECTED) {
                                LogUtil.i(TAG, "BluetoothGattCallback::onConnectionStateChange:connect");
                                if (gatt.discoverServices()) {
                                    LogUtil.i(TAG, "discoverServices");
                                }
                            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                                onConnStatusChanged(false);
                            }
                        }
                    }
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (mRunningFlag && null == mGATTCharacter) {
                        LogUtil.i(TAG, "BluetoothGattCallback::onServicesDiscovered");
                        BluetoothGattService gattService = gatt.getService(Config.UUID_LE_SERVICE);
                        if (null == gattService) {
                            LogUtil.i(TAG, "no such service");
                            onConnStatusChanged(false);
                            return;
                        }
                        mGATTCharacter = gattService.getCharacteristic(Config.UUID_LE_CHARACTERISTIC);
                        if (null == mGATTCharacter) {
                            LogUtil.i(TAG, "no such Characteristic");
                            onConnStatusChanged(false);
                            return;
                        }
                        if (gatt.setCharacteristicNotification(mGATTCharacter, true)) {
                            LogUtil.i(TAG, "BluetoothGattCallback::onServicesDiscovered:getCharacteristic:setCharacteristicNotification:Ok");
                            BluetoothGattDescriptor descriptor = mGATTCharacter.getDescriptor(Config.UUID_LE_DESCRIPTOR_CLIENT);
                            if (null == descriptor) {
                                LogUtil.i(TAG, "no such descriptor");
                                onConnStatusChanged(false);
                                return;
                            }
                            LogUtil.i(TAG, "BluetoothGattCallback::onServicesDiscovered:descriptor uuid:" + descriptor.getUuid().toString());
                            if (descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)) {
                                LogUtil.i(TAG, "write local descriptor value ok");
                                if (gatt.writeDescriptor(descriptor)) {
                                    LogUtil.i(TAG, "write remote descriptor value ok");
                                    LogUtil.i(TAG, "finally connect success");
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        gatt.requestMtu(512);
                                    }
                                    onConnStatusChanged(true);
                                } else {
                                    LogUtil.i(TAG, "write remote descriptor value failed");
                                    onConnStatusChanged(false);
                                }
                            } else {
                                LogUtil.i(TAG, "write local descriptor value failed");
                                onConnStatusChanged(false);
                            }
                        } else {
                            LogUtil.i(TAG, "setCharacteristicNotification failed");
                            onConnStatusChanged(false);
                        }
                    }
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    if (mRunningFlag) {
                        final byte[] value = characteristic.getValue();
                        //Log.e("BlueManager","--"+ByteUtil.byte2HexStr(value)+"--");
                        if (null != value) {
                            Message message = Message.obtain();
                            message.obj = value;
                            mLeReceiveHandler.sendMessage(message);
                        }
                    }
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    if (mRunningFlag) {
                        byte[] value = characteristic.getValue();
                        LogUtil.i(TAG, "BluetoothGattCallback::onCharacteristicWrite:" + DataFormatter.bytes2HexString(value));
                    }
                }

                @Override
                public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                    if (mRunningFlag) {
                        LogUtil.i(TAG, "BluetoothGattCallback::onMtuChanged:" + mtu);
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            synchronized (BlueSocketBinder.class) {
                                mGATTMtu = mtu;
                            }
                        }
                    }
                }
            });
        }

        /**
         * 开始经典蓝牙连接和通道建立
         */
        private void startClassic() {
            LogUtil.i(TAG, "conn: classic");
            try {
                connClassic();
                receiveByteFromStream();
            } catch (IOException e) {
                e.printStackTrace();
                onException();
            }
        }

        /**
         * 连接经典蓝牙
         *
         * @throws IOException 异常
         */
        private void connClassic() throws IOException {
            LogUtil.i(TAG, "conn: classic");
            bindDevice();
            if (mBonded) {
                LogUtil.i(TAG, "bind success");
                // 与目标简历的连接对象
                BluetoothSocket bluetoothSocket = mRemoteDevice.createRfcommSocketToServiceRecord(Config.UUID_CLASSIC_COM);
                // 阻塞
                bluetoothSocket.connect();
                mInputStream = bluetoothSocket.getInputStream();
                mOutputStream = bluetoothSocket.getOutputStream();
                LogUtil.i(TAG, "connected");
                onConnStatusChanged(true);
                mRunningFlag = true;
            } else {
                LogUtil.i(TAG, "bind failed");
                if (null == mListener) {
                    stopSelf();
                } else {
                    mListener.onPairedFailed(mRemoteDevice);
                }
                mRunningFlag = false;
            }
        }

        /**
         * 通知服务，目标设备已经绑定成功
         */
        void setBonded() {
            mBonded = true;
            LogUtil.i(TAG, "received bonded notice");
        }

        /**
         * 绑定蓝牙设备（配对），最长尝试1分钟
         */
        private void bindDevice() {
            if (mRemoteDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                LogUtil.i(TAG, "has bonded");
                mBonded = true;
                return;
            }
            try {
                mRemoteDevice.getClass().getMethod("cancelPairingUserInput", byte[].class).invoke(mRemoteDevice);
                //noinspection PrimitiveArrayArgumentToVarargsMethod
                mRemoteDevice.getClass().getMethod("setPin", byte[].class).invoke(mRemoteDevice, Config.PIN);
                mRemoteDevice.getClass().getMethod("createBond").invoke(mRemoteDevice);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.i(TAG, "bind Exception:" + e.toString());
                return;
            }
            int limit = Config.PAIRED_WAIT_TIME_SECONDS;
            while (limit > 0 && mRunningFlag) {
                if (mBonded) {
                    return;
                }
                LogUtil.i(TAG, "bind limit" + limit);
                limit--;
                if (limit == 0) {
                    LogUtil.i(TAG, "bind failed");
                } else {
                    SystemClock.sleep(1000);
                }
            }
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.i(TAG, "onBind");
        // 获取蓝牙地址mac
        String mac = intent.getStringExtra(Config.EXTRA_KEY_MAC);
        LogUtil.i(TAG, "remote MAC:" + mac);
        // 获取蓝牙模式，默认经典蓝牙
        int mode = intent.getIntExtra(Config.EXTRA_KEY_MODE, Config.MODE_CLASSIC);
        // 校验参数，异常返回null
        if (null == mac || !mac.matches(Config.MAC_PATTERN) || (mode != Config.MODE_CLASSIC && mode != Config.MODE_LE)) {
            return null;
        } else {
            mBinder = new BlueSocketBinder(mac, mode);
            return mBinder;
        }
    }

    @Override
    public void onDestroy() {
        LogUtil.i(TAG, "onDestroy");
        // 释放资源
        if (null != mBinder) {
            mBinder.release();
        }
        super.onDestroy();
    }
}
