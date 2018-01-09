package com.eightmile.elevatorcontrol.activity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.eightmile.elevatorcontrol.util.LogUtil;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

public class MyApplication extends Application{
	private static final String TAG = "AdApplication";

	private static Context context;
	
	private static List<Activity> mActivitys = Collections
            .synchronizedList(new LinkedList<Activity>());
	
	public static Context getContext(){
    	return context;
    }
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		context = getApplicationContext();
		registerActivityListener();
	}

	/**
	 * 将活动添加到activity管理的List中
	 * @param activity 活动实例
	 * @author zc
	 */
	public void pushActivity(Activity activity) {
        mActivitys.add(activity);
        LogUtil.d(TAG,"activityList:size:"+mActivitys.size());
    }
	
	/**
	 * 将活动从activity管理的List中删除
	 * @param activity
	 * @author zc
	 */
	public void popActivity(Activity activity) {
        mActivitys.remove(activity);
        LogUtil.d(TAG,"activityList:size:"+mActivitys.size());
    }
	
	/**
	 * 获取当前在栈顶的活动
	 * @return 活动实例
	 * @author zc
	 */
	public static Activity currentActivity() {
	    if (mActivitys == null||mActivitys.isEmpty()) {
	        return null;
	    }
	    Activity activity = mActivitys.get(mActivitys.size()-1);
	    return activity;
	}
	
	/**
	 * 结束当前的活动
	 */
	public static void finishCurrentActivity() {
        if (mActivitys == null||mActivitys.isEmpty()) {
            return;
        }
        Activity activity = mActivitys.get(mActivitys.size()-1);
        finishActivity(activity);
    }
	
	/**
	 * 结束指定的活动
	 * @param activity 活动实例
	 * @author zc
	 */
	public static void finishActivity(Activity activity) {
		if (mActivitys == null||mActivitys.isEmpty()) {
	    	return;
	    }
	    if (activity != null) {
	        mActivitys.remove(activity);
	        activity.finish();
	        activity = null;
	    }
	 }
	 
	
	/**
	 * 根据类名结束活动
	 * @param cls 要结束活动的类名
	 * @author zc
	 */
	 public static void finishActivity(Class<?> cls) {
		 if (mActivitys == null||mActivitys.isEmpty()) {
			 return;
		 }
	     for (Activity activity : mActivitys) {
	    	 if (activity.getClass().equals(cls)) {
	    		 finishActivity(activity);
	    	 }
	     }
	 }
	 
	 /**
	  * 根据类名找到指定的活动实例
	  * @param cls 要找到的活动的类名	
	  * @return 指定活动的实例
	  */
	 public static Activity findActivity(Class<?> cls) {
		 Activity targetActivity = null;
		 if (mActivitys != null) {
			 for (Activity activity : mActivitys) {
				 if (activity.getClass().equals(cls)) {
					 targetActivity = activity;
					 break;
				 }
			 }
		 }
		 return targetActivity;
	 }
	 
	 
	 /**
	  * 获取当前在栈顶的活动
	  * @return 找到的活动的实例
	  */
	 public Activity getTopActivity() {
		 Activity mBaseActivity = null;
		 synchronized (mActivitys) {
			 final int size = mActivitys.size() - 1;
			 if (size < 0) {
				 return null;
			 }
			 mBaseActivity = mActivitys.get(size);
		 }
		 return mBaseActivity;

	 }
	 
	 /**
	  * 获取在栈顶的活动的类名
	  * @return 活动的类名
	  */
	 public String getTopActivityName() {
		 Activity mBaseActivity = null;
		 synchronized (mActivitys) {
			 final int size = mActivitys.size() - 1;
			 if (size < 0) {
				 return null;
			 }
			 mBaseActivity = mActivitys.get(size);
		 }
		 return mBaseActivity.getClass().getName();
	 }
	 
	 /**
	  * 结束所有的活动
	  */
	 public static void finishAllActivity() {
		 if (mActivitys == null) {
			 return;
		 }
		 for (Activity activity : mActivitys) {
			 activity.finish();
		 }
		 mActivitys.clear();
	 }
	 
	 /**
	  * 退出应用程序
	  */
	 public  static void appExit() {
		 try {
			 LogUtil.i(TAG,"app exit");
			 finishAllActivity();
		 } catch (Exception e) {
		 }
	 }
	

	private void registerActivityListener() {
		// TODO Auto-generated method stub
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH){
			registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
				
				@Override
				public void onActivityStopped(Activity activity) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onActivityStarted(Activity activity) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onActivityResumed(Activity activity) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onActivityPaused(Activity activity) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onActivityDestroyed(Activity activity) {
					// TODO Auto-generated method stub
					 if (null==mActivitys&&mActivitys.isEmpty()){
	                        return;
	                    }
	                    if (mActivitys.contains(activity)){
	                        /**
	                         *  监听到 Activity销毁事件 将该Activity 从list中移除
	                         */
	                        popActivity(activity);
	                    }
				}
				
				@Override
				public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
					// TODO Auto-generated method stub
					pushActivity(activity);
				}
			});
		}
	}
}
