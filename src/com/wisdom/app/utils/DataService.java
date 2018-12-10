package com.wisdom.app.utils;

import java.util.ArrayList;
import java.util.List;

import com.wisdom.app.activity.DataCheckActivity;
import com.wisdom.bean.BoXingBean;
import com.wisdom.bean.DianbiaoZouZiBean;
import com.wisdom.bean.TaitiCeLiangShuJuBean;
import com.wisdom.bean.XieBoDuquBean;

import android.util.Log;
import android.widget.Toast;

/**
 * @author JinJingYun
 * 2017��11��28��
 * �������ݵĳ���
 * */
public class DataService {
	private static int    [] iRxBuf = new int[2048];
    /**
     * ���ĵ�ǰ����
     * */
    private static int       iRxCurLen=0;
    /**
     * �ֽڿ�������ʼλ��
     * */
    private static int       iRxOkLen=0;
    /**
     * ������ʵ����
     * */
    private static int       iBaoWnLen=0;
	private UnpackFrame uf=new UnpackFrame();
	
	/**
	 * ֱ�Ӹ��ݱ��Ĺ�������н������������ݼ���
	 * @param str 16���Ƶ��ַ�����ʽ
	 * @param iLen �ַ�������
	 * */
    public List<String> fnGetData(String str,int iLen)
    {
    	List<String> list=new ArrayList<String>();
    	byte[] data=this.fnReceiveData(str, iLen);
    	if(data!=null)
    	{
    		if((data[2]&0x7f)==0x06)
			{
    			list.add(uf.fnGetClock(data));
    			return list;
			}
    		else if((data[2]&0x7f)==0x61)//ʵʱ̨���������
    		{
    			
    		}
    	}
    	return null;
    }
    /**
	 * ��ȡʵʱ̨���������
	 * @param str 16���Ƶ��ַ�����ʽ
	 * @param iLen �ַ�������
	 * */
    public TaitiCeLiangShuJuBean fnGetTaitiData(String str,int iLen)
    {
    	TaitiCeLiangShuJuBean bean=null;
    	byte[] data=this.fnReceiveData(str, iLen);
    	if(data!=null)
    	{
    		Log.i("DataService","��������֡��"+ByteUtil.BinaryToHexString(data));
    		if((data[2]&0x7f)==0x61)//ʵʱ̨���������
    		{
    			bean=uf.fnGetTaiTiCeLiangShuJu(data);
    			return bean;
    		}
    	}
    	return null;
    }
    /**
	 * ��ȡ������ֲ�������
	 * @param str 16���Ƶ��ַ�����ʽ
	 * @param iLen �ַ�������
	 * */
    public DianbiaoZouZiBean fnGetZouZiData(String str,int iLen)
    {
    	DianbiaoZouZiBean bean=null;
    	byte[] data=this.fnReceiveData(str, iLen);
    	if(data!=null)
    	{
    		Log.i("DataService","��������֡��"+ByteUtil.BinaryToHexString(data));
    		if((data[2]&0x7f)==0x7D)//������ֲ���
    		{
    			bean=uf.fnGetZouZiShuJu(data);
    			return bean;
    		}
    	}
    	return null;
    }
    /**
	 * ��ȡʵʱ̨���������
	 * @param str 16���Ƶ��ַ�����ʽ
	 * @param iLen �ַ�������
	 * */
    public TaitiCeLiangShuJuBean fnGetTaitiData(byte[] data)
    {
    	TaitiCeLiangShuJuBean bean=null;
    	if(data!=null)
    	{
    		Log.i("DataService","��������֡��"+ByteUtil.BinaryToHexString(data));
    		if((data[2]&0x7f)==0x61)//ʵʱ̨���������
    		{
    			bean=uf.fnGetTaiTiCeLiangShuJu(data);
    			return bean;
    		}
    	}
    	return null;
    }
    /**
	 * г����ȡ
	 * @param str 16���Ƶ��ַ�����ʽ
	 * @param iLen �ַ�������
	 * */
    public XieBoDuquBean fnGetXieBoData(byte[] data)
    {
    	XieBoDuquBean bean=null;
    	if(data!=null)
    	{
    		Log.i("DataService","��������֡��"+ByteUtil.BinaryToHexString(data));
    		if((data[2]&0x7f)==0x7D)
    		{
    			bean=uf.fnGetXieBoShuJu(data);
    			return bean;
    		}
    	}
    	return null;
    }
    /**
   	 * ���ζ�ȡ
   	 * @param str 16���Ƶ��ַ�����ʽ
   	 * @param iLen �ַ�������
   	 * */
    public BoXingBean fnGetBoXingData(byte[] data)
    {
    	BoXingBean bean=null;
       	if(data!=null)
       	{
       		Log.i("DataService","��������֡��"+ByteUtil.BinaryToHexString(data));
       		if((data[2]&0x7f)==0x68)
       		{
       			bean=uf.fnGetBoXingShuJu(data);
       			return bean;
       		}
       	}
       	return null;
    }
    /**
	 * ��ȡ������ֲ�������
	 * @param str 16���Ƶ��ַ�����ʽ
	 * @param iLen �ַ�������
	 * */
    public DianbiaoZouZiBean fnGetZouZiData(byte[] data)
    {
    	DianbiaoZouZiBean bean=null;
    	if(data!=null)
    	{
    		Log.i("DataService","��������֡��"+ByteUtil.BinaryToHexString(data));
    		if((data[2]&0x7f)==0x6B)//������ֲ���
    		{
    			bean=uf.fnGetZouZiShuJu(data);
    			return bean;
    		}
    	}
    	return null;
    }
    /**
	 * �������ݷ��뻺�棬������������֡
	 * */
	public byte[] fnReceiveData(String str,int iLen)
	{
		int [] iBuf= new int[50]; 
    	int i=0,iCheck=0;
    	int iCode=0;
    	 iBuf = ByteUtil.hexStringToInt(str);
    	 iRxCurLen = iLen/2;
    	 //����20���ַ�����һ֡����
    	 if ((iRxOkLen == 0)&&(iBaoWnLen==0))
    	 {
    		 //����֡
    		 if(iRxCurLen < 7)
    			 return null;
    		 if( (iBuf[0]!= 0xEB)||(iBuf[1]!= 0x90) )
    		     return null;	 
    		 iCode = iBuf[2];
    		 iCode = iCode&0x80;
    		 //������ ���� ���� �����б��Ĳ���ʾ
    		 if (iCode ==0 )
    		 {
  	  			 iRxOkLen = 0;
  	  			 iBaoWnLen = 0; 
    			 return null;
    		 }
    		 //������ʵ����
    		 iBaoWnLen = iBuf[4]*0x100 + iBuf[5];
    	 }
    	 
    	 System.arraycopy(iBuf,0,iRxBuf,iRxOkLen,iRxCurLen);
  		 iRxOkLen += iRxCurLen;
  		 //���˱��ĳ���
  		 if(iRxOkLen>= iBaoWnLen)
  		 {
  			 //��У��λ
  			 for(i=0; i< (iBaoWnLen-1);i++)
  			 {
  				iCheck += iRxBuf[i];
  				iCheck %= 0x100;	
  			 }
  			//System.out.println("K:"+iRxOkLen+"|"+iBaoWnLen);    
  			 
  			 //��У�鲻�Ծ�����
  			 if(iCheck!= iRxBuf[i])
  			 {
  	  	    	iRxCurLen = 0;
  	  	    	iRxOkLen  = 0;
  	  	    	iBaoWnLen = 0;
  				 return null;  
  			 }
  			//System.out.println("E:"+iRxOkLen+"|"+iBaoWnLen); 
  			String strHex = ByteUtil.int2hex(iRxBuf,iBaoWnLen);
  			
 			iRxOkLen = 0;
 			iBaoWnLen = 0;
 			return ByteUtil.hexStr2Bytes(strHex);
  		 }
		return null;
	}
}
