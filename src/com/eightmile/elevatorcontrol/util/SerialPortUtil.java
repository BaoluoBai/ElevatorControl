package com.eightmile.elevatorcontrol.util;
 
import java.io.File;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.OutputStream;  

import org.winplus.serial.utils.SerialPort;

  
/** 
 * 串口操作类 
 *  
 * @author Jerome 
 *  
 */  
public class SerialPortUtil {  
	private String TAG = SerialPortUtil.class.getSimpleName();
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private String path = "/dev/ttymxc2";
    private int baudrate = 9600;
    private static SerialPortUtil portUtil;
    private OnDataReceiveListener onDataReceiveListener = null;
    private boolean isStop = false;

    public interface OnDataReceiveListener {
        public void onDataReceive(byte[] buffer, int size);
    }

    public void setOnDataReceiveListener(
            OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }

    public SerialPortUtil(String path, int baudrate) {
    	try {
			mSerialPort = new SerialPort(new File(path), baudrate, 0);
			mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            isStop = false;
            mReadThread = new ReadThread();
            mReadThread.start();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
   

    /**
     * 发送指令到串口
     * 
     * @param cmd
     * @return
     */
    public boolean sendCmds(String cmd) {
        boolean result = true;
        byte[] mBuffer = cmd.getBytes();
        try {
            if (mOutputStream != null) {
                mOutputStream.write(mBuffer);
            } else {
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    public boolean sendBuffer(byte[] mBuffer) {
        boolean result = true;
        String tail = "";
        byte[] tailBuffer = tail.getBytes();
        byte[] mBufferTemp = new byte[mBuffer.length+tailBuffer.length];
        System.arraycopy(mBuffer, 0, mBufferTemp, 0, mBuffer.length);
        System.arraycopy(tailBuffer, 0, mBufferTemp, mBuffer.length, tailBuffer.length);
        try {
            if (mOutputStream != null) {
                mOutputStream.write(mBufferTemp);
            } else {
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isStop && !isInterrupted()) {
                int size;
                try {
                    if (mInputStream == null)
                        return;
                    byte[] buffer = new byte[20];
                    size = mInputStream.read(buffer);
                    if (size > 0) {
//                          String str = new String(buffer, 0, size);
//                          Logger.d("length is:"+size+",data is:"+new String(buffer, 0, size));
                        if (null != onDataReceiveListener) {
                            onDataReceiveListener.onDataReceive(buffer, size);
                        }
                    }
//                    Thread.sleep(2);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort() {
        isStop = true;
        if (mReadThread != null) {
            mReadThread.interrupt();
        }
        if (mSerialPort != null) {
            mSerialPort.close();
        }
    }
    
    public void openSerialPort(){
    	isStop = false;
    	if (mReadThread != null) {
            mReadThread.run();
        }
    	if(mSerialPort == null){
    		try {
				mSerialPort = new SerialPort(new File(path), baudrate, 0);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

}