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
 * 2017年11月28日
 * 接收数据的池子
 * */
public class DataService {
	private static int    [] iRxBuf = new int[2048];
    /**
     * 报文当前长度
     * */
    private static int       iRxCurLen=0;
    /**
     * 字节拷贝的起始位置
     * */
    private static int       iRxOkLen=0;
    /**
     * 报文真实长度
     * */
    private static int       iBaoWnLen=0;
	private UnpackFrame uf=new UnpackFrame();
	
	/**
	 * 直接根据报文功能码进行解析，返回数据集合
	 * @param str 16进制的字符串格式
	 * @param iLen 字符串长度
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
    		else if((data[2]&0x7f)==0x61)//实时台体测量数据
    		{
    			
    		}
    	}
    	return null;
    }
    /**
	 * 获取实时台体测量数据
	 * @param str 16进制的字符串格式
	 * @param iLen 字符串长度
	 * */
    public TaitiCeLiangShuJuBean fnGetTaitiData(String str,int iLen)
    {
    	TaitiCeLiangShuJuBean bean=null;
    	byte[] data=this.fnReceiveData(str, iLen);
    	if(data!=null)
    	{
    		Log.i("DataService","完整数据帧："+ByteUtil.BinaryToHexString(data));
    		if((data[2]&0x7f)==0x61)//实时台体测量数据
    		{
    			bean=uf.fnGetTaiTiCeLiangShuJu(data);
    			return bean;
    		}
    	}
    	return null;
    }
    /**
	 * 获取电表走字测量数据
	 * @param str 16进制的字符串格式
	 * @param iLen 字符串长度
	 * */
    public DianbiaoZouZiBean fnGetZouZiData(String str,int iLen)
    {
    	DianbiaoZouZiBean bean=null;
    	byte[] data=this.fnReceiveData(str, iLen);
    	if(data!=null)
    	{
    		Log.i("DataService","完整数据帧："+ByteUtil.BinaryToHexString(data));
    		if((data[2]&0x7f)==0x7D)//电表走字测试
    		{
    			bean=uf.fnGetZouZiShuJu(data);
    			return bean;
    		}
    	}
    	return null;
    }
    /**
	 * 获取实时台体测量数据
	 * @param str 16进制的字符串格式
	 * @param iLen 字符串长度
	 * */
    public TaitiCeLiangShuJuBean fnGetTaitiData(byte[] data)
    {
    	TaitiCeLiangShuJuBean bean=null;
    	if(data!=null)
    	{
    		Log.i("DataService","完整数据帧："+ByteUtil.BinaryToHexString(data));
    		if((data[2]&0x7f)==0x61)//实时台体测量数据
    		{
    			bean=uf.fnGetTaiTiCeLiangShuJu(data);
    			return bean;
    		}
    	}
    	return null;
    }
    /**
	 * 谐波读取
	 * @param str 16进制的字符串格式
	 * @param iLen 字符串长度
	 * */
    public XieBoDuquBean fnGetXieBoData(byte[] data)
    {
    	XieBoDuquBean bean=null;
    	if(data!=null)
    	{
    		Log.i("DataService","完整数据帧："+ByteUtil.BinaryToHexString(data));
    		if((data[2]&0x7f)==0x7D)
    		{
    			bean=uf.fnGetXieBoShuJu(data);
    			return bean;
    		}
    	}
    	return null;
    }
    /**
   	 * 波形读取
   	 * @param str 16进制的字符串格式
   	 * @param iLen 字符串长度
   	 * */
    public BoXingBean fnGetBoXingData(byte[] data)
    {
    	BoXingBean bean=null;
       	if(data!=null)
       	{
       		Log.i("DataService","完整数据帧："+ByteUtil.BinaryToHexString(data));
       		if((data[2]&0x7f)==0x68)
       		{
       			bean=uf.fnGetBoXingShuJu(data);
       			return bean;
       		}
       	}
       	return null;
    }
    /**
	 * 获取电表走字测量数据
	 * @param str 16进制的字符串格式
	 * @param iLen 字符串长度
	 * */
    public DianbiaoZouZiBean fnGetZouZiData(byte[] data)
    {
    	DianbiaoZouZiBean bean=null;
    	if(data!=null)
    	{
    		Log.i("DataService","完整数据帧："+ByteUtil.BinaryToHexString(data));
    		if((data[2]&0x7f)==0x6B)//电表走字测试
    		{
    			bean=uf.fnGetZouZiShuJu(data);
    			return bean;
    		}
    	}
    	return null;
    }
    /**
	 * 接收数据放入缓存，返回完整数据帧
	 * */
	public byte[] fnReceiveData(String str,int iLen)
	{
		int [] iBuf= new int[50]; 
    	int i=0,iCheck=0;
    	int iCode=0;
    	 iBuf = ByteUtil.hexStringToInt(str);
    	 iRxCurLen = iLen/2;
    	 //超过20个字符，第一帧报文
    	 if ((iRxOkLen == 0)&&(iBaoWnLen==0))
    	 {
    		 //判首帧
    		 if(iRxCurLen < 7)
    			 return null;
    		 if( (iBuf[0]!= 0xEB)||(iBuf[1]!= 0x90) )
    		     return null;	 
    		 iCode = iBuf[2];
    		 iCode = iCode&0x80;
    		 //判下行 上行 报文 ；下行报文不显示
    		 if (iCode ==0 )
    		 {
  	  			 iRxOkLen = 0;
  	  			 iBaoWnLen = 0; 
    			 return null;
    		 }
    		 //报文真实长度
    		 iBaoWnLen = iBuf[4]*0x100 + iBuf[5];
    	 }
    	 
    	 System.arraycopy(iBuf,0,iRxBuf,iRxOkLen,iRxCurLen);
  		 iRxOkLen += iRxCurLen;
  		 //到了报文长度
  		 if(iRxOkLen>= iBaoWnLen)
  		 {
  			 //判校验位
  			 for(i=0; i< (iBaoWnLen-1);i++)
  			 {
  				iCheck += iRxBuf[i];
  				iCheck %= 0x100;	
  			 }
  			//System.out.println("K:"+iRxOkLen+"|"+iBaoWnLen);    
  			 
  			 //若校验不对就重置
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
