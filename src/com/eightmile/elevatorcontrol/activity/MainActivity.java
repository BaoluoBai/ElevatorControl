package com.eightmile.elevatorcontrol.activity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.eightmile.elevatorcontrol.db.ElevatorDB;
import com.eightmile.elevatorcontrol.util.Config;
import com.eightmile.elevatorcontrol.util.HttpUtil;
import com.eightmile.elevatorcontrol.util.LogUtil;
import com.eightmile.elevatorcontrol.util.Utility;
import com.example.elevatorcontrol.R;

import android.app.Activity;
import android.os.Bundle;


public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	
	public static final String LAYOUT_API = "layout_api";
	
	public static final String ADLIST_API = "adlist_api";
	
	private ElevatorDB elevatorDB;
	
	private Activity activity = this;
	
	String ip = Config.get("domain");
	String path = Config.get("url.getLayout");
	String mac = Config.get("mac");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        elevatorDB = ElevatorDB.getInstance(this);
        String address = "http://"+ip+path+"&mid="+mac;
        queryFromServer(address, LAYOUT_API);
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
					Utility.handleAdResponse(elevatorDB, arg1);
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
}
