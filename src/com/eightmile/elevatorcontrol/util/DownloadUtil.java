package com.eightmile.elevatorcontrol.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DownloadUtil {
	public static final String TAG = "DownloadUtil";
	private static DownloadUtil downloadUtil;
    private final OkHttpClient okHttpClient;

    public static DownloadUtil get() {
        if (downloadUtil == null) {
            downloadUtil = new DownloadUtil();
        }
        return downloadUtil;
    }

    private DownloadUtil() {
        okHttpClient = new OkHttpClient();
    }

    /**
     * @param url 下载连接
     * @param saveDir 储存下载文件的SDCard目录
     * @param listener 下载监听
     */
    public void download(final String url, final String saveDir, final DownloadListener listener) {
        LogUtil.i(TAG, url);
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Request request = new Request.Builder().url(url).build();
		        okHttpClient.newCall(request).enqueue(new Callback() {
					@Override
					public void onFailure(Call arg0, IOException arg1) {
						// TODO Auto-generated method stub
						// 下载失败
		                listener.onDownloadFailed();
					}
					@Override
					public void onResponse(Call arg0, Response response) throws IOException {
						// TODO Auto-generated method stub
						InputStream is = null;
		                byte[] buf = new byte[2048];
		                int len = 0;
		                FileOutputStream fos = null;
		                // 储存下载文件的目录
		                String savePath = isExistDir(saveDir);
		                LogUtil.d(TAG, "存储目录："+savePath);
//		                String savePath = isExistDir(saveDir, fileName)
		                try {
		                    is = response.body().byteStream();
		                    long total = response.body().contentLength();
		                    LogUtil.d(TAG, "文件大小："+total);
		                    File file = new File(saveDir, getNameFromUrl(url));
		                    fos = new FileOutputStream(file);
		                    long sum = 0;
		                    while ((len = is.read(buf)) != -1) {
		                        fos.write(buf, 0, len);
		                        sum += len;
		                        int progress = (int) (sum * 1.0f / total * 100);
		                        // 下载中
		                        listener.onDownloading(progress);
		                    }
		                    fos.flush();
		                    // 下载完成
		                    listener.onDownloadSuccess();
		                } catch (Exception e) {
		                    listener.onDownloadFailed();
		                    LogUtil.e(TAG, "下载错误："+e);
		                } finally {
		                    try {
		                        if (is != null)
		                            is.close();
		                    } catch (IOException e) {
		                    }
		                    try {
		                        if (fos != null)
		                            fos.close();
		                    } catch (IOException e) {
		                    }
		                }
					}
		        });
			}
		}).start();
    	
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException
     * 判断下载目录是否存在
     */
    private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(saveDir);
        if (!downloadFile.mkdirs()) {
        	LogUtil.d(TAG, "文件已存在");
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * @param url
     * @return
     * 从下载连接中解析出文件名
     */
    private String getNameFromUrl(String url) {
    	
        return url.substring(url.lastIndexOf("/") + 1);
    }

}
