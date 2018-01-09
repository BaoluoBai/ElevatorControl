package com.eightmile.elevatorcontrol.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {
	public static void sendOkHttpRequest(final String address, final okhttp3.Callback callback){
		// TODO Auto-generated method stub
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(address).build();
		client.newCall(request).enqueue(callback);
	}
}
