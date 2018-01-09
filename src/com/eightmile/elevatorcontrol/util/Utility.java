package com.eightmile.elevatorcontrol.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eightmile.elevatorcontrol.activity.MyApplication;
import com.eightmile.elevatorcontrol.db.ElevatorDB;
import com.eightmile.elevatorcontrol.manager.AdManager;
import com.eightmile.elevatorcontrol.manager.LayoutCallback;
import com.eightmile.elevatorcontrol.manager.LayoutManager;
import com.eightmile.elevatorcontrol.model.Ad;
import com.eightmile.elevatorcontrol.model.AdInfo;
import com.eightmile.elevatorcontrol.model.LayoutInfo;

import okhttp3.Response;
import android.text.TextUtils;
import android.widget.Toast;

public class Utility {
public static final String TAG= "Utility";
	
	/**
	 * 用于处理从服务器返回的布局数据	
	 * step1: 获取本地数据库存储的布局，如果为空，则直接加载从服务器请求到的布局，并插入到数据库,如果不为空则进入step2;
	 * step2: 判断本地数据库存储的布局数据与从服务器请求到的布局数据是否一样，如果一样，则直接加载本地布局，如果不一样则进入step3;
	 * step3: 加载从服务器返回的布局数据，并覆盖原先数据库中的布局数据
	 * @param adLauncherDB 数据库操作实例
	 * @param response 需要处理的布局数据
	 * @return boolean
	 */
	public static boolean handleLayoutResponse(ElevatorDB elevatorDB, Response response){
		String body = "";
		try {
			body = response.body().string();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			body = "";
		}
		
		if(!TextUtils.isEmpty(body)){
			LayoutManager layoutManager = new LayoutManager();
			JSONObject jsonObject = JSONObject.parseObject(body);
			String status = jsonObject.getString("status");
			
			if(status.equals("1")){
				
				List<LayoutInfo> list = new ArrayList<LayoutInfo>();
				list = elevatorDB.loadLayout();
				if(list.size() == 0){
					//直接加载从服务器请求的数据，并将数据插入到数据库
					elevatorDB.saveLayout(body);
					layoutManager.createLayout(body);
					return true;
				}else{
					if(list.get(0).getContent().equals(response)){
						//加载本地布局
						layoutManager.createLayout(list.get(0).getContent());
						return true;
					}else{
						//加载从服务器请求的布局，并覆盖原数据库中的数据
						layoutManager.createLayout(body);
						elevatorDB.updateLayout(body, list.get(0).getId());
						return true;
					}
				}
			}else {
				Toast.makeText(MyApplication.getContext(), "请求服务器失败,加载本地布局", Toast.LENGTH_SHORT).show();
				return false;
			}
			
		}
		return false;
	}
	
//	public static boolean handleVersionResponse(String body){
//		boolean isNeedUpdate = false;
//		int currentVersion = 0;
//		FirmwareManager fm = new FirmwareManager();
//		LogUtil.i(TAG, "body:"+body);
//		if(!TextUtils.isEmpty(body)){
//			JSONObject jsonObject = JSONObject.parseObject(body);
//			String status = jsonObject.getString("status");
//			if(status.equals("1")){
//				currentVersion = Integer.parseInt(jsonObject.getJSONObject("data").getString("code"));
////				String address = jsonObject.getJSONObject("data").getString("local");
//				int oleVersion = fm.oldVersion();
//				if(oleVersion < currentVersion){
//					LogUtil.e(TAG, "更新APK并保存版本号");
//					isNeedUpdate = true;
////					fm.updateApp(address, currentVersion);
//				}
//			}else{
//				LogUtil.e(TAG, jsonObject.getString("data"));
//				isNeedUpdate = false;
//			}
//		}
//		return isNeedUpdate;
//	}
	public static List<Ad> handleAdResponse(ElevatorDB elevatorDB, Response response){
		List<Ad> download_list = new ArrayList<Ad>();
		String body = "";
		try {
			body = response.body().string();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			body = "";
		}
		
		if(!TextUtils.isEmpty(body)){
			JSONObject jsonObject = JSONObject.parseObject(body);
			String status = jsonObject.getString("status");
			JSONArray adArray = null;
			if(status.equals("1")){
				List<AdInfo> list = new ArrayList<AdInfo>();
				list = elevatorDB.loadAd();
				if(list.size()==0){
					//本地没有广告数据，从服务器下载，并保存到数据库
					LogUtil.d(TAG, body);
					elevatorDB.saveAd(body);
					try {
						adArray = jsonObject.getJSONArray("data");
					} catch (Exception e) {
						adArray = null;
					}
					if(adArray!= null){
						for(int i = 0; i<adArray.size(); i++){
							Ad bean = new Ad();
							bean.setName(adArray.getJSONObject(i).getString("name"));
							bean.setLocation(Config.get("adPath"));
							bean.setUrl(adArray.getJSONObject(i).getString("url"));
							download_list.add(bean);
						}
					}
					
				}else{
					//本地有服务器数据，与服务器数据比对
					if(!body.equals(list.get(0).getClass())){
						LogUtil.d(TAG, body);
						//加载服务器广告
					}else{
						//加载本地广告
						LogUtil.d(TAG, body);
					}
				}
			}else{
			}
		}else{
		}
		return download_list;
	} 
}
