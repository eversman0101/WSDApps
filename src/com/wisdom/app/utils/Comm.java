package com.wisdom.app.utils;

import com.sunday.slidetabfragment.blue.BlueManager;
import com.sunday.slidetabfragment.blue.ProtocolSignALTEK;

import android.os.Handler;
import android.util.Log;

public class Comm {
	//public static int millseconds=200;
	//用于发送一次性命令
	public static byte[] status_one=null;
	//用于轮训
	public static byte[] status_loop1=null;
	//用于轮训
	public static byte[] status_loop2=null;
	public static Handler handler;
	private static Comm instance=null;
    public static Comm getInstance()
    {
        if(instance==null)
            instance=new Comm();
        return instance;
    }
    
    public boolean loop=false;
    public void startLoop()
    {
    	loop=true;
    	Log.i("Comm","loop start");
    	try{
    		new Thread(new Runnable(){

    			@Override
    			public void run() {
    				while(loop)
    		    	{
    					
    					if(!BlueManager.getInstance().isConnect())
    						continue;
    					if(handler==null)
    					{
    						Log.e("Comm","handler is null!Cancel loop!");
    						return;
    					}
    		    		if(status_one!=null)
    		    		{
    		    			Blue.send(status_one, handler);
    		    			status_one=null;
    		    		}
    		    		if(status_loop1!=null)
    		    		{
    		    			Blue.send(status_loop1, handler);
    		    		}
    		    		if(status_loop2!=null)
    		    		{
    		    			Blue.send(status_loop2, handler);
    		    		}
    		    	}
    				Log.i("Comm","loop stopted!");
    			}}).start();
    	}catch(Exception ex)
    	{
    		ex.printStackTrace();
    		try {
				Thread.sleep(20);
				Log.e("Comm","异常，尝试重新loop");
				startLoop();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
    	}
    }
    /*
     * @param millseconds 发送间隔的时间
     * */
    public void startLoop(final int millseconds)
    {
    	loop=true;
    	Log.i("Comm","loop start");
    	try{
    		new Thread(new Runnable(){

    			@Override
    			public void run() {
    				while(loop)
    		    	{
    					
    					if(!BlueManager.getInstance().isConnect())
    						continue;
    					if(handler==null)
    					{
    						Log.e("Comm","handler is null!Cancel loop!");
    						return;
    					}
    		    		if(status_one!=null)
    		    		{
    		    			Blue.send(status_one, handler);
    		    			status_one=null;
    		    		}
    		    		if(status_loop1!=null)
    		    		{
    		    			Blue.send(status_loop1, handler);
    		    		}
    		    		if(status_loop2!=null)
    		    		{
    		    			Blue.send(status_loop2, handler);
    		    		}
    		    	}
    				Log.i("Comm","loop stopted!");
    			}}).start();
    	}catch(Exception ex)
    	{
    		ex.printStackTrace();
    		try {
				Thread.sleep(20);
				Log.e("Comm","异常，尝试重新loop");
				startLoop();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
    	}
    }
    public void cancelLoop()
    {
    	loop=false;
    	this.handler=null;
    	status_one=null;
    	status_loop1=null;
    	status_loop2=null;
    }
    public void init(Handler handler)
    {
    	this.handler=handler;
    	status_one=null;
    	status_loop1=null;
    	status_loop2=null;
    }
}
