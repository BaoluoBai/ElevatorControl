package com.eightmile.elevatorcontrol.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.eightmile.elevatorcontrol.db.ElevatorDB;
import com.eightmile.elevatorcontrol.manager.AdManager;
import com.eightmile.elevatorcontrol.model.Ad;
import com.eightmile.elevatorcontrol.util.Config;
import com.eightmile.elevatorcontrol.util.HttpUtil;
import com.eightmile.elevatorcontrol.util.LogUtil;
import com.eightmile.elevatorcontrol.util.SerialPortUtil;
import com.eightmile.elevatorcontrol.util.SerialPortUtil.OnDataReceiveListener;
import com.eightmile.elevatorcontrol.util.Utility;
import com.example.elevatorcontrol.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends Activity implements OnClickListener{
	private static final String TAG = "MainActivity";
	
	public static final String LAYOUT_API = "layout_api";
	
	public static final String ADLIST_API = "adlist_api";
	
	private ElevatorDB elevatorDB;
	
	private Activity activity = this;
	
	private ImageView iv_arrow, iv_elevator_status, 
		iv_btn_one, iv_btn_two, iv_btn_three, iv_btn_four, iv_btn_open, iv_btn_close,
		iv_btn_call;
	private TextView tv_floor_display;
	private static final String PORT_DISPLAY = "/dev/ttymxc1";
//	private static final String PORT_PHONE = "/dev/ttyUSB2";
	private static final int BAUDRATE = 9600;
	//电梯显示
	public SerialPortUtil serialPortOne = null;
	//拨打电话
	public SerialPortUtil serialPortTwo = null;
	public int serialPort_index_one = 0;
	public boolean isReceive = false;
	int display_id = 0;
	List<Integer> list_message = new ArrayList<Integer>();
	StringBuffer sbu = new StringBuffer();
	public boolean isLockReceive = false;
	public boolean isLockMessage = false;
	String ip = Config.get("domain");
	String path = Config.get("url.getLayout");
	String mac = Config.get("mac");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window _window;

        /**

         * 隐藏pad底部虚拟键

         */

        _window = getWindow();
        WindowManager.LayoutParams params = _window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE;
        _window.setAttributes(params);
        elevatorDB = ElevatorDB.getInstance(this);
        String address = "http://"+ip+path+"&mid="+mac;
        queryFromServer(address, LAYOUT_API);
        Timer time = new Timer();
        time.schedule(init, 2000);
    }
    
    /**
     * 从服务器查询数据，包括布局、版本、APP、广告列表、及时消息、定时开关机等
     * @param address 请求服务器的地址
     * @param type	请求的类型
     */
    private void queryFromServer(final String address, final String type){
    	
//    	LogUtil.i(TAG, "执行加载布局");
    	HttpUtil.sendOkHttpRequest(address, new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				LogUtil.d(TAG, "服务器相应的回调");
				if("lastest_api".equals(type)){
					
				}else if("init_api".equals(type)){
					
				}else if("layout_api".equals(type)){
					LogUtil.d("TAG", "layout_api");
					Utility.handleLayoutResponse(elevatorDB, arg1);
					String serverpath = "http://"+Config.get("domain")+Config.get("url.getAdList")+Config.get("mac");
					queryFromServer(serverpath, ADLIST_API);
				}else if("applist_api".equals(type)){
					
				}else if("adlist_api".equals(type)){
					final List<Ad> download_list = Utility.handleAdResponse(elevatorDB, arg1);
					if((download_list.size()!=0)&&(download_list!=null)){
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								AdManager admanager = new AdManager(activity);
								admanager.downloads(download_list);
							}
						});
					}
				}else if("instantmessage_api".equals(type)){
					
				}else if("switchs_api".equals(type)){
					
				}else if("volume_api".equals(type)){
					
				}else if("emergency_api".equals(type)){
					
				}
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				LogUtil.d(TAG, "网络错误");
			}
		});
    }
    
    private void initView() {
		// TODO Auto-generated method stub
    	iv_arrow = (ImageView) findViewById(R.id.iv_arrow);
    	tv_floor_display = (TextView) findViewById(R.id.tv_floor_display);
    	iv_elevator_status = (ImageView) findViewById(R.id.iv_elevator_status); 
		iv_btn_one = (ImageView) findViewById(R.id.iv_btn_one);
		iv_btn_one.setOnClickListener(this);
		iv_btn_two = (ImageView) findViewById(R.id.iv_btn_two);
		iv_btn_two.setOnClickListener(this);
		iv_btn_three = (ImageView) findViewById(R.id.iv_btn_three);
		iv_btn_three.setOnClickListener(this);
		iv_btn_four = (ImageView) findViewById(R.id.iv_btn_four);
		iv_btn_four.setOnClickListener(this);
		iv_btn_open = (ImageView) findViewById(R.id.iv_btn_open);
		iv_btn_open.setOnClickListener(this);
		iv_btn_close = (ImageView) findViewById(R.id.iv_btn_close);
		iv_btn_close.setOnClickListener(this);
		iv_btn_call = (ImageView) findViewById(R.id.iv_btn_call);
		iv_btn_call.setOnClickListener(this);
		initSerialPort();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_btn_one:
			LogUtil.d(TAG, "iv_btn_one");
			iv_btn_one.setBackgroundResource(R.drawable.btn_one_1);
			break;
		case R.id.iv_btn_two:
			LogUtil.d(TAG, "iv_btn_two");
			iv_btn_two.setBackgroundResource(R.drawable.btn_two_1);
			break;
		case R.id.iv_btn_three:
			LogUtil.d(TAG, "iv_btn_three");
			iv_btn_three.setBackgroundResource(R.drawable.btn_three_1);
			break;
		case R.id.iv_btn_four:
			LogUtil.d(TAG, "iv_btn_four");
			iv_btn_four.setBackgroundResource(R.drawable.btn_four_1);
			break;
		case R.id.iv_btn_open:
			LogUtil.d(TAG, "iv_btn_open");
			iv_btn_open.setBackgroundResource(R.drawable.btn_open_1);
			break;
		case R.id.iv_btn_close:
			LogUtil.d(TAG, "iv_btn_close");
			iv_btn_close.setBackgroundResource(R.drawable.btn_close_1);
			break;
		case R.id.iv_btn_call:
			LogUtil.d(TAG, "iv_btn_call");
//			iv_btn_one.setBackgroundResource(R.drawable.btn_one_1);
			break;
		default:
			break;
		}
	}
	
	public void initSerialPort(){
		serialPortOne = new SerialPortUtil(PORT_DISPLAY, BAUDRATE);
//		serialPortTwo = new SerialPortUtil(PORT_PHONE, BAUDRATE);
		serialPortOne.setOnDataReceiveListener(new OnDataReceiveListener() {
			
			@Override
			public void onDataReceive(byte[] buffer, int size) {
				// TODO Auto-generated method stub
				if(!isLockReceive){
					int i = buffer[0] & 0xFF;
					LogUtil.d(TAG, i+"");
					if(i==170){
						isReceive = true;
						serialPort_index_one = 0;
						list_message.clear();
					}
					if(isReceive){
						isLockMessage = true;
						list_message.add(i);
						serialPort_index_one++;
						if(serialPort_index_one>7){
							handleMessageDisplay(list_message);
							isReceive = false;
							isLockMessage = false;
						}
					}
				}
			}
		});
	}
	
	public boolean checkMessage(List<Integer> message){
		boolean isCorrect = false;
		if(message.size()==8){
			int sum = 0;
			for(int i = 1; i<message.size()-1; i++){
				sum = sum+message.get(i);
			}
			LogUtil.d(TAG, "sum:"+sum);
			LogUtil.d(TAG, "message:"+message.get(7));
			LogUtil.d(TAG, "取余："+sum%256);
			if((sum%256)==message.get(7)){
				isCorrect = true;
			}
		}
		return isCorrect;
	}
	
	public void handleMessageDisplay(List<Integer> list){
		if(checkMessage(list)){
//			serialPortOne.closeSerialPort();
//			handleMessageDisplay(list_message);
			int data1 = list.get(1) & 0X07;
			switch (data1) {
			case 1:
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						iv_arrow.setVisibility(View.GONE);
					}
				});
				break;
			case 2:
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						iv_arrow.setVisibility(View.VISIBLE);
						iv_arrow.setBackgroundResource(R.drawable.arrow_up);
					}
				});
				break;
				
			case 4:
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						iv_arrow.setVisibility(View.VISIBLE);
						iv_arrow.setBackgroundResource(R.drawable.arrow_down);
					}
				});
				break;
			default:
				break;
			}
			int data2, data3, data5;
			data2 = list.get(2);
			data3 = list.get(3);
			data5 = list.get(5);
//			byte[] asc = {data5, data3, data2};
			sbu.delete(0, sbu.length());			
			sbu.append((char)data3);
			sbu.append((char)data2);
			if(data5!=0){
				sbu.append((char)data5);
			}
			LogUtil.d(TAG, "ASCII:"+sbu.toString());
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					tv_floor_display.setText(sbu.toString());
				}
			});
			switch (list.get(4)) {
			case 0:
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						iv_elevator_status.setVisibility(View.GONE);
					}
				});
				break;
			case 1:
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						iv_elevator_status.setVisibility(View.VISIBLE);
						iv_elevator_status.setBackgroundResource(R.drawable.status_inspection_zh);
					}
				});
				break;
			case 2:
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						iv_elevator_status.setVisibility(View.VISIBLE);
						iv_elevator_status.setBackgroundResource(R.drawable.status_full_zh);
					}
				});
				break;
			case 4:
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						iv_elevator_status.setVisibility(View.VISIBLE);
						iv_elevator_status.setBackgroundResource(R.drawable.status_fire_zh);
					}
				});
				break;
			case 8:
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						iv_elevator_status.setVisibility(View.VISIBLE);
						iv_elevator_status.setBackgroundResource(R.drawable.status_full_zh);
					}
				});
				break;
			case 16:
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						iv_elevator_status.setVisibility(View.VISIBLE);
						iv_elevator_status.setBackgroundResource(R.drawable.status_lock_zh);
					}
				});
				break;

			default:
				break;
			}
		}
	}
	
	TimerTask handleMessage = new TimerTask() {
		
		@Override
		public void run() {
			if(!isLockMessage){
				isLockReceive = true;
				handleMessageDisplay(list_message);
				isLockReceive = false;
			}
			
		}
	};
	
	TimerTask init = new TimerTask() {
		
		@Override
		public void run() {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					initView();
				}
			});
		}
	};
}
