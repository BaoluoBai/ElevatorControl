package com.eightmile.elevatorcontrol.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.alibaba.fastjson.JSONObject;
import com.eightmile.elevatorcontrol.db.ElevatorDB;
import com.eightmile.elevatorcontrol.model.AdInfo;
import com.eightmile.elevatorcontrol.model.LayoutInfo;
import com.eightmile.elevatorcontrol.util.Config;
import com.eightmile.elevatorcontrol.util.HttpUtil;
import com.eightmile.elevatorcontrol.util.LogUtil;
import com.example.elevatorcontrol.R;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.VideoView;

public class AdManager {
	public static final String TAG = "AdManager";
	private VideoView vv_ad = null;
	Activity activity = new Activity();
	ElevatorDB elevatorDB = null;
	public AdManager(Activity main){
		this.activity = main;
		vv_ad = (VideoView) main.findViewById(R.id.vv_ad);
		queryAdFromServer();
	}
	
	public void queryAdFromServer(){
		String serverpath = "http://"+Config.get("domain")+Config.get("url.getAdList")+Config.get("mac");
		LogUtil.d(TAG, serverpath);
		HttpUtil.sendOkHttpRequest(serverpath, new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				elevatorDB = ElevatorDB.getInstance(activity);
				String body = "";
				try {
					body = arg1.body().string();
				} catch (Exception e) {
					// TODO: handle exception
					body = "";
				}
				if(!TextUtils.isEmpty(body)){
					JSONObject jsonObject = JSONObject.parseObject(body);
					String status = jsonObject.getString("status");
					if(status.equals("1")){
						List<AdInfo> list = new ArrayList<AdInfo>();
						list = elevatorDB.loadAd();
						if(list.size() == 0){
							//直接加载从服务器请求的数据，并将数据插入到数据库
							elevatorDB.saveAd(body);
						}else{
							
						}
					}
				}
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
