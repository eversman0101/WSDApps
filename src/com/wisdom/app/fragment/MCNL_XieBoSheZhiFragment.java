package com.wisdom.app.fragment;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.wisdom.app.activity.R;
import com.wisdom.app.utils.ALTEK;
import com.wisdom.app.utils.Blue;
import com.wisdom.app.utils.BluetoothLeService;
import com.wisdom.app.utils.Comm;
import com.wisdom.app.utils.DataService;
import com.wisdom.bean.XieBoDuquBean;
import com.wisdom.layout.TitleLayout;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
/**
 * @author JinJingYun
 * 虚负荷手动校验-谐波设置
 * */
public class MCNL_XieBoSheZhiFragment extends Fragment{
	private String TAG="678";
	@Bind(R.id.title_MCNL_xiebo_shezhi)TitleLayout title;
	@Bind(R.id.xb_u0)EditText et_u2;
	@Bind(R.id.xb_i0)EditText et_i2;
	
	@Bind(R.id.xb_u1)EditText et_u3;
	@Bind(R.id.xb_i1)EditText et_i3;
	
	@Bind(R.id.xb_u2)EditText et_u4;
	@Bind(R.id.xb_i2)EditText et_i4;
	
	@Bind(R.id.xb_u3)EditText et_u5;
	@Bind(R.id.xb_i3)EditText et_i5;
	
	@Bind(R.id.xb_u4)EditText et_u6;
	@Bind(R.id.xb_i4)EditText et_i6;
	
	@Bind(R.id.xb_u5)EditText et_u7;
	@Bind(R.id.xb_i5)EditText et_i7;
	
	@Bind(R.id.xb_u6)EditText et_u8;
	@Bind(R.id.xb_i6)EditText et_i8;
	
	@Bind(R.id.xb_u7)EditText et_u9;
	@Bind(R.id.xb_i7)EditText et_i9;
	
	@Bind(R.id.xb_u8)EditText et_u10;
	@Bind(R.id.xb_i8)EditText et_i10;
	
	@Bind(R.id.xb_u9)EditText et_u11;
	@Bind(R.id.xb_i9)EditText et_i11;
	
	@Bind(R.id.xb_u10)EditText et_u12;
	@Bind(R.id.xb_i10)EditText et_i12;
	
	@Bind(R.id.xb_u11)EditText et_u13;
	@Bind(R.id.xb_i11)EditText et_i13;
	
	@Bind(R.id.xb_u12)EditText et_u14;
	@Bind(R.id.xb_i12)EditText et_i14;
	
	@Bind(R.id.xb_u13)EditText et_u15;
	@Bind(R.id.xb_i13)EditText et_i15;
	
	@Bind(R.id.xb_u14)EditText et_u16;
	@Bind(R.id.xb_i14)EditText et_i16;
	
	@Bind(R.id.xb_u15)EditText et_u17;
	@Bind(R.id.xb_i15)EditText et_i17;
	
	@Bind(R.id.xb_u16)EditText et_u18;
	@Bind(R.id.xb_i16)EditText et_i18;
	
	@Bind(R.id.xb_u17)EditText et_u19;
	@Bind(R.id.xb_i17)EditText et_i19;
	
	@Bind(R.id.xb_u18)EditText et_u20;
	@Bind(R.id.xb_i18)EditText et_i20;
	
	@Bind(R.id.xb_u19)EditText et_u21;
	@Bind(R.id.xb_i19)EditText et_i21;
	
	//@Bind(R.id.xb_u20)EditText et_u22;
	//@Bind(R.id.xb_i20)EditText et_i22;
	
	//@Bind(R.id.xb_u21)EditText et_u23;
	//@Bind(R.id.xb_i21)EditText et_i23;
	
	
	
	//@Bind(R.id.xb_cishu)EditText et_cishu;
	@Bind(R.id.xb_startup)Button btn_start;
	@Bind(R.id.btn_reset)Button btn_reset;
	
	ViewGroup root;
	Vector vector=new Vector();

	int count=20;
	//收发数据
		private BluetoothGattCharacteristic characteristic;
		private ALTEK altek = new ALTEK();
		private DataService ds = new DataService();
		int[] iTempBuf = new int[10];
		int iTxLen = 0;
		int iStart = 0, iEnd = 0, iLen = 0, i = 0;
		String strCurTx = null;
		String str = null;
		private Timer timer;
		private XieBoDuquBean bean;
		private boolean recv_state=false;
		
		
		private Handler handler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	            if (msg.what == 0x001) {
	            	try
	            	{
	            	bean=(XieBoDuquBean) msg.obj;
	            	if(bean!=null)
	            	{
	            	//	tv_thdu.setText(String.valueOf(bean.getTotalUhanliang()));
	            	//	tv_thdi.setText(String.valueOf(bean.getTotalIhanliang()));
	            		Log.i("xiebofenxi","i个数："+String.valueOf(bean.getiHanliang().size()));
	            		Log.i("xiebofenxi","u个数："+String.valueOf(bean.getuHanliang().size()));
	            	}
	            	}catch(Exception ex)
	            	{
	            		ex.printStackTrace();
	            	}
	            }
	            else if(msg.what==0x002)
	            {
	            	Toast.makeText(getActivity(), getText(R.string.set_complete),Toast.LENGTH_SHORT).show();
	            }
	            else if(msg.what==0x003)
	            {
	            	
	            }
	          
	        }
	    };
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		final View view=inflater.inflate(R.layout.fragment_mcnl_xiebo_shezhi, null);
		ButterKnife.bind(this, view);
		root = (ViewGroup) view.findViewById(R.id.rootView);
		title.setTitleText(String.format(getText(R.string.manual_validation_of_virtual_load_placeholder).toString(),getText(R.string.harmonic_setup).toString()));
		
        initData();
        btn_start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				travelTree(root);
				Comm.getInstance().status_one=altek.fnXieBoSheZhiAndXiangWei(count, vector);
				vector=new Vector();
				}
		});
        btn_reset.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				initTree(root,view);
			}
		});
		return view;
	}
	 //递归遍历树
    private void travelTree(View root) {
    	int index=0;
        if (root instanceof ViewGroup) {
            int childCount = ((ViewGroup) root).getChildCount();
            for (int i = 0; i < childCount; i++) {
                travelTree(((ViewGroup) root).getChildAt(i));
            }
        } else if (root instanceof View) {
            if (root instanceof EditText)
            {
            		for(int i=0;i<count;i++)
            		{
            		String xb_u="xb_u"+i;
            		String xb_i="xb_i"+i;
            		String xb_u_p="xb_u_p"+i;
            		String xb_i_p="xb_i_p"+i;
            		int idU = getResources().getIdentifier(xb_u, "id", getActivity().getApplicationContext().getPackageName());
            		int idI = getResources().getIdentifier(xb_i, "id", getActivity().getApplicationContext().getPackageName());
            		int idU_P=getResources().getIdentifier(xb_u_p, "id", getActivity().getApplicationContext().getPackageName());
            		int idI_P=getResources().getIdentifier(xb_i_p, "id", getActivity().getApplicationContext().getPackageName());
            		
            		if(root.getId()==idU)
                	{
                		String str_root=((EditText) root).getText().toString();
                		vector.addElement(str_root);
                		Log.i(TAG,"控件:"+idU+" 值："+str_root);
                	}
            		else if(root.getId()==idU_P)
                	{
            			String str_root=((EditText) root).getText().toString();
                		vector.addElement(str_root);
                		Log.i(TAG,"控件:"+idU+" 值："+str_root);
                	}
            		else if(root.getId()==idI)
                	{
            			String str_root=((EditText) root).getText().toString();
                		vector.addElement(str_root);
                		Log.i(TAG,"控件:"+idU+" 值："+str_root);	
                	}
            		else if(root.getId()==idI_P)
                	{
            			String str_root=((EditText) root).getText().toString();
                		vector.addElement(str_root);
                		Log.i(TAG,"控件:"+idU+" 值："+str_root);}
            		}
            }
        }
    }
	private void initTree(View root,View view){
		int index=0;
        if (root instanceof ViewGroup) {
            int childCount = ((ViewGroup) root).getChildCount();
            for (int i = 0; i < childCount; i++) {
            	initTree(((ViewGroup) root).getChildAt(i),view);
            }
        } else if (root instanceof View) {
            if (root instanceof EditText)
            {
            		for(int i=0;i<count;i++)
            		{
            		String xb_u="xb_u"+i;
            		String xb_i="xb_i"+i;
            		String xb_u_p="xb_u_p"+i;
            		String xb_i_p="xb_i_p"+i;
            		int idU = getResources().getIdentifier(xb_u, "id", getActivity().getApplicationContext().getPackageName());
            		int idI = getResources().getIdentifier(xb_i, "id", getActivity().getApplicationContext().getPackageName());
            		int idU_P=getResources().getIdentifier(xb_u_p, "id", getActivity().getApplicationContext().getPackageName());
            		int idI_P=getResources().getIdentifier(xb_i_p, "id", getActivity().getApplicationContext().getPackageName());
            		
            		if(root.getId()==idU)
                	{
                		//EditText et=(EditText)view.findViewById(idU);
                		//et.setText("0");
            			((EditText)root).setText("0");
                	}
            		else if(root.getId()==idU_P)
                	{
            			//EditText et=(EditText)view.findViewById(idU_P);
                		//et.setText("0");
            			((EditText)root).setText("0");
                	}
            		else if(root.getId()==idI)
                	{
            			//EditText et=(EditText)view.findViewById(idI);
                		//et.setText("0");
            			((EditText)root).setText("0");
                	}
            		else if(root.getId()==idI_P)
                	{
            			//EditText et=(EditText)view.findViewById(idI_P);
                		//et.setText("0");
            			((EditText)root).setText("0");
            		}
            		}
        }
        }
	}
    private void initData()
	{
		try
		{
			Comm.getInstance().init(handler);
		}catch(Exception ex)
		{
			ex.printStackTrace();
			Toast.makeText(getActivity(), getText(R.string.toast_connect_device), Toast.LENGTH_SHORT).show();
		}
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try{
			//getActivity().unregisterReceiver(mGattUpdateReceiver);
			}catch(Exception ex)
			{
				
			}
	}
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (hidden) {
			// 不在最前端显示 相当于调用了onPause();
			Log.i(TAG,"onHidden()");
			try{
				//getActivity().unregisterReceiver(mGattUpdateReceiver);
				}catch(Exception ex)
				{
					
				}
            return;
        }else{ 
        	initData();
        	// 在最前端显示 相当于调用了onResume();
        	Log.i(TAG,"onHiddenShow()");
        	//getActivity().registerReceiver(mGattUpdateReceiver, MCNL_JiBenWuChaFragment.makeGattUpdateIntentFilter());
        }
	}
}
