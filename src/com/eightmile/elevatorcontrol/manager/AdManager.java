package com.eightmile.elevatorcontrol.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.alibaba.fastjson.JSONObject;
import com.eightmile.elevatorcontrol.db.ElevatorDB;
import com.eightmile.elevatorcontrol.model.Ad;
import com.eightmile.elevatorcontrol.model.AdInfo;
import com.eightmile.elevatorcontrol.util.Config;
import com.eightmile.elevatorcontrol.util.DownloadListener;
import com.eightmile.elevatorcontrol.util.DownloadUtil;
import com.eightmile.elevatorcontrol.util.HttpUtil;
import com.eightmile.elevatorcontrol.util.LogUtil;
import com.example.elevatorcontrol.R;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.text.TextUtils;
import android.widget.VideoView;

public class AdManager {
	public static final String TAG = "AdManager";
	private VideoView vv_ad = null;
	Activity activity = new Activity();
	ElevatorDB elevatorDB = null;
	int index = 0;
	List<String> playlist = new ArrayList<String>();
	public int video_position = 0;
	public AdManager(Activity main){
		this.activity = main;
		vv_ad = (VideoView) main.findViewById(R.id.vv_ad);
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
	
	public void downloads(final List<Ad> list){
		if(list.size()>0){
			if(index>list.size()-1){
				playAd();
				return;
			}
			if(!list.get(index).getUrl().equals("")){
				DownloadUtil.get().download(list.get(index).getUrl(), list.get(index).getLocation(), new DownloadListener() {
					
					@Override
					public void onDownloading(int progress) {
						// TODO Auto-generated method stub
						LogUtil.d(TAG, "下载进度："+progress);
					}
					
					@Override
					public void onDownloadSuccess() {
						// TODO Auto-generated method stub
						LogUtil.d(TAG, "第"+index+"个广告下载完成");
						playlist.add(list.get(index).getLocation()+"/"+list.get(index).getName());
						index++;
						downloads(list);
					}
					
					@Override
					public void onDownloadFailed() {
						// TODO Auto-generated method stub
						
					}
				});
			}else{
				playlist.add(list.get(index).getLocation()+"/"+list.get(index).getName());
				index++;
				downloads(list);
			}
			
		}
		
		
	}
	public void playAd(){
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				video_position = 0;
				File file=new File(playlist.get(video_position));
				String path = file.getAbsolutePath();
				vv_ad.setVideoPath(path);
				vv_ad.start();
		        vv_ad.setOnPreparedListener(new OnPreparedListener() {
		          
		          @Override
		          public void onPrepared(MediaPlayer arg0) {
		            // TODO Auto-generated method stub
		            arg0.setLooping(false);
		            arg0.setVolume(0.3f, 0.3f);
		          }
		        });
		        vv_ad.setOnCompletionListener(new OnCompletionListener() {
		        	int length = playlist.size();
					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						video_position++;
						if(video_position>=length){
							video_position = 0;
							File vedio=new File(playlist.get(video_position));
							vv_ad.setVideoPath(vedio.getAbsolutePath());
							vv_ad.getHolder().setFixedSize(1080, 600);
					        vv_ad.start();

						}else{
							File vedio=new File(playlist.get(video_position));
							vv_ad.setVideoPath(vedio.getAbsolutePath());
							vv_ad.getHolder().setFixedSize(1080, 600);
					        vv_ad.start();
						}
					}
				});
			}
		});
	}
}
