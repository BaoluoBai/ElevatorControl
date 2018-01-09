package com.eightmile.elevatorcontrol.util;

public interface DownloadListener {
	void onDownloadFailed();
	
	void onDownloading(int progress);
	
	void onDownloadSuccess();
}
