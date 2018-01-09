package com.eightmile.elevatorcontrol.manager;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.eightmile.elevatorcontrol.activity.MyApplication;
import com.eightmile.elevatorcontrol.customui.AutoScroll;
import com.eightmile.elevatorcontrol.util.Config;
import com.eightmile.elevatorcontrol.util.DownloadListener;
import com.eightmile.elevatorcontrol.util.DownloadUtil;
import com.eightmile.elevatorcontrol.util.LogUtil;
import com.eightmile.elevatorcontrol.util.Tools;
import com.example.elevatorcontrol.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class LayoutManager {
public static final String TAG = "LayoutManager";
	
	String savePath = "";
	
	private Activity activity;
	
	int uri_index = 0;
	
	Uri uri = null;
	
	ArrayList<Uri> url_list = new ArrayList<Uri>();
	
	private RelativeLayout out_layout;
	
//	List<DownloadBean> downloadPicList;
	
	public LayoutManager(){
		this.activity = MyApplication.currentActivity();
		this.out_layout = (RelativeLayout) MyApplication.currentActivity().findViewById(R.id.outerFrame);
//		this.downloadPicList = new ArrayList<DownloadBean>();
		this.savePath = Config.get("layoutPath");
	}
	
	public void createLayout(String layout){
		activity = MyApplication.currentActivity();
//		out_layout = (RelativeLayout) activity.findViewById(R.id.outerFrame);
		
		JSONObject jsonObject = JSONObject.parseObject(layout);
		JSONObject data = jsonObject.getJSONObject("data");
//		DownloadBean download = new DownloadBean("out_background", data.getString("background"), "out");    
//		downloadPicList.add(download);
		
		String address_last = data.getString("background");
		final String backFileName = Tools.getNameFromUrl(address_last);
		LogUtil.d(TAG, "backFileName:"+backFileName);
		String address = Config.get("domain")+address_last;
		if(Tools.fileIsExist(savePath+"/"+backFileName)){
			LogUtil.d(TAG, "背景图片已经存在");
			MyApplication.currentActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Drawable bg=Drawable.createFromPath(savePath+"/"+backFileName);
					out_layout.setBackground(bg);
				}
			});
			
		}else{
			DownloadUtil.get().download("http://"+address, savePath, new DownloadListener() {
				
				@Override
				public void onDownloading(int progress) {
					// TODO Auto-generated method stub
					LogUtil.i(TAG, "正在下载外部布局背景图片...");
				}
				
				@Override
				public void onDownloadSuccess() {
					// TODO Auto-generated method stub
					LogUtil.i(TAG, "背景图片下载完成");
					MyApplication.currentActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Drawable bg=Drawable.createFromPath(savePath+"/"+backFileName);
							out_layout.setBackground(bg);
						}
					});
				}
				
				@Override
				public void onDownloadFailed() {
					// TODO Auto-generated method stub
					
				}
			});
		}
		
		final JSONArray modules = data.getJSONArray("modules");
		addModules(out_layout, modules);		
	}
	
	public void addModules(RelativeLayout relativeLayout, JSONArray jsonArray){
		JSONObject module;
		for(int i = 0; i<jsonArray.size(); i++){
			try {
				module = (JSONObject) jsonArray.get(i);
				String mtype = module.getString("mtype");
				if(mtype.equals("floor")){
					addFloorView(relativeLayout, module);
				}
				if(mtype.equals("ad")){
					addAdView(relativeLayout, module);
				}
				if(mtype.equals("content")){
					addContentView(relativeLayout, module);
				}
				if(mtype.equals("btns")){
					addControlView(relativeLayout, module);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 加载用于显示应用程序的布局
	 * @param Rlayout 父布局文件
	 * @param object 应用程序布局数据
	 */
	private void addFloorView(final RelativeLayout Rlayout,final JSONObject object){
		Log.e("layout: ","add app module");
		MyApplication.currentActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				int height = 0;
		    	int width = 0;
		    	int marginleft = 0;
		    	int margintop = 0;
		    	height = object.getIntValue("height");
		    	width = object.getIntValue("width");
		    	marginleft = object.getIntValue("pleft");
		    	margintop = object.getIntValue("ptop");
		    	RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(width,height);
		        rp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		        rp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		        rp.setMargins(marginleft, margintop, 0, 0);
		        // 获取需要添加的布局  
		        RelativeLayout layout = 
		        		(RelativeLayout) View
		        			.inflate(activity,R.layout.floorview, null)
		        			.findViewById(R.id.floorview);
		        // 将布局加入到当前布局中  
		        Rlayout.addView(layout,rp);
			}
		});
    	
    	
    }
	
	
	/**
     * 动态插入视频区
     * @param object
     */
    private void addAdView(final RelativeLayout Rlayout,final JSONObject object){
    	MyApplication.currentActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				LogUtil.i("layout: ","add ad module");
		    	int height = 0;
		    	int width = 0;
		    	int marginleft = 0;
		    	int margintop = 0;
		    	try {
		    		height = object.getIntValue("height");
				} catch (JSONException e) {
					e.printStackTrace();
				}
		    	try {
		    		width = object.getIntValue("width");
				} catch (JSONException e) {
					e.printStackTrace();
				}
		    	try {
		    		marginleft = object.getIntValue("pleft");
				} catch (JSONException e) {
					e.printStackTrace();
				}
		    	try {
		    		margintop = object.getIntValue("ptop");
				} catch (JSONException e) {
					e.printStackTrace();
				}
		    	RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(width,height);
		        rp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		        rp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		        rp.setMargins(marginleft, margintop, 0, 0);
		        // 获取需要添加的布局  
		        
		    	RelativeLayout layout = 
		    			(RelativeLayout) View
		    				.inflate(activity,R.layout.adview, null)
		    				.findViewById(R.id.adview);
		    	//将布局加入到当前布局中
		        Rlayout.addView(layout,rp);
			}
		});
		
    }
    
    private void addControlView(final RelativeLayout Rlayout,final JSONObject object){
    	Log.e("layout: ","add app module");
    	MyApplication.currentActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				int height = 0;
		    	int width = 0;
		    	int marginleft = 0;
		    	int margintop = 0;
		    	height = object.getIntValue("height");
		    	width = object.getIntValue("width");
		    	marginleft = object.getIntValue("pleft");
		    	margintop = object.getIntValue("ptop");
		    	RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(width,height);
		        rp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		        rp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		        rp.setMargins(marginleft, margintop, 0, 0);
		        // 获取需要添加的布局  
		        RelativeLayout layout = 
		        		(RelativeLayout) View
		        			.inflate(activity,R.layout.controlview, null)
		        			.findViewById(R.id.controlview);
		        // 将布局加入到当前布局中  
		        Rlayout.addView(layout,rp);
			}
		});
    	
    	
    }
    
    private void addContentView(final RelativeLayout Rlayout,final JSONObject object){
    	
    	LogUtil.d(TAG,"add content module");
    	MyApplication.currentActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				int width = 0;
		    	int height = 0;
		    	int pleft = 0;
		    	int ptop = 0;
		    	String background = "";
		    	int fontSize = 0;
		    	try {
					width = object.getIntValue("width");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	
		    	try {
		    		height = object.getIntValue("height");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	try {
		    		pleft = object.getIntValue("pleft");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	try {
		    		ptop = object.getIntValue("ptop");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(width,height);
		    	rp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		        rp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		        rp.setMargins(pleft, ptop, 0, 0);

		    	RelativeLayout layout = 
		    			(RelativeLayout) View
		    				.inflate(activity,R.layout.contentview, null)
		    				.findViewById(R.id.contentView);
		    	
		        Rlayout.addView(layout,rp);
		        AutoScroll as = (AutoScroll) activity.findViewById(R.id.tv_content);
		    	try {
					fontSize = object.getIntValue("fontsize");
				} catch (JSONException e) {
					e.printStackTrace();
				}
		    	if(fontSize!=0){
		    		as.setTextSize(fontSize);
		    	}
		    	try {
					background = object.getString("color").trim();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	if(!background.equals("")&&!background.isEmpty()){
		    		String[] ss = splitcolor(background);
		        	as.setPaint(fontSize, ss);
		    	}
			}
		});
    }
    
    public static String[] splitcolor(String str){
   	 	if(str!=null||(!str.equals(""))){
   	 		String [] s=str.split("\\(");
   	 		String [] s1=s[1].split("\\)");
   	 		String [] s2=s1[0].split(",");
   	 		return s2;
   	 	}else{
   	 		return null;
   	 	}   
    } 
}
