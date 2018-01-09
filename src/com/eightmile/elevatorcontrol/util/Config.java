package com.eightmile.elevatorcontrol.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import android.content.pm.ActivityInfo;
import android.os.Environment;

public class Config {
	private static String path	= android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/config.cfg";
	private static Hashtable<String, String> configs = new Hashtable<String, String>();
	static {
		System.err.println(path);
		configs.put("rotate", Integer.toString(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
        configs.put("bckey.x", "450");
        configs.put("bckey.y", "850");
        configs.put("fkey.x", "490");
        configs.put("fkey.y", "-830");
        configs.put("ad.root", "/udisk");
        configs.put("ad.image", "|bmp|jpg|png|");
        configs.put("ad.image.intv", "5");    // seconds
        configs.put("ad.video", "|mp4|avi|");
        configs.put("apk.hots", "com.android.mms|com.android.browser|com.android.contacts|com.android.settings");
        configs.put("apk.blacklist", "com.android.*|com.example.testMD5|com.freescale.*|com.qihoo*|com.fsl.ethernet|com.estrongs.*|com.example.audioroute");
        configs.put("apk.icon.width", "130");
        configs.put("apk.icon.height", "130");
        configs.put("dial.tty", "/dev/ttymxc3");
        configs.put("dial.band", "115200");
        configs.put("weather.apikey", "2de143494c0b295cca9337e1e96b00e0");
        configs.put("weather.cityname", "Suzhou,China");
        configs.put("url.getLayout", "/index.php?m=Api&c=Layout&a=getLayout");
        configs.put("url.getAdList", "/index.php?m=Api&c=Motherboard&a=getAd&mid=");
        configs.put("url.getApps", "/index.php?m=Api&c=Layout&a=getApps");
        configs.put("url.getTime", "/index.php?m=Api&c=Public&a=getTime");
        configs.put("url.getEmergency", "/index.php?m=Api&c=Emergency&a=getInfo");
        configs.put("url.getSwitch", "/index.php?m=Api&c=Motherboard&a=getSwithchs");
        configs.put("url.getContent", "/index.php?m=Api&c=Motherboard&a=getContent");
        configs.put("url.sendDeviceInfo", "/index.php?m=Api&c=Motherboard&a=statusLog");
        configs.put("domain", "139.196.212.252:80");
        configs.put("host", "139.196.212.252");
        configs.put("mac", "c8:00:00:00:73:17");
        configs.put("layoutPath", Environment.getExternalStorageDirectory() + "/DBoothLayout");
        configs.put("adPath", Environment.getExternalStorageDirectory() + "/DBoothADs");
        configs.put("updateUrl", "/index.php?m=Api&c=PUpdate&a=getSelf");
        configs.put("checkTime", "2016-01-01 00:00:00");
        configs.put("downloads", get("adPath") + File.separator + "ads");
        configs.put("database", get("adPath") + File.separator + "sqlite.db");
        configs.put("progress", "1");
	}

	public static String	get(String key) {
		File cfg = new File(path);
		if (cfg.isFile() && cfg.exists()) {
			try {
				FileInputStream fis = new FileInputStream(path);
				try {
					Properties p = new Properties();
					p.load(fis);
					String s = p.getProperty(key);
					if (null != s) {
						return s;
					}
				} finally {
					fis.close();
				}
			} catch (Exception e) {
			}
		}
		return configs.get(key);
	}
	public static int		getInt(String key) {
		String s = get(key);
		if (null != s) {
			try {
				return Integer.parseInt(s);
			} catch (Exception e) {}
		}
		return -1;
	}
	public static String[]	getArray(String key) {
		String s = get(key);
		if (null != s) {
			return s.split("\\|");
		}
		return null;
	}
	// 获取mac地址
	public static String getLocalMacAddress() {
		String mac = null;
		FileInputStream fis_name=null;
		FileInputStream fis=null;
		try {
			String path = "sys/class/net/eth0/address";
			 fis_name = new FileInputStream(path);
			byte[] buffer_name = new byte[1024 * 8];
			int byteCount_name = fis_name.read(buffer_name);
			if (byteCount_name > 0) {
				mac = new String(buffer_name, 0, byteCount_name, "utf-8");
			}

			if (mac.length() == 0 || mac == null) {
				path = "sys/class/net/eth0/wlan0";
				fis = new FileInputStream(path);
				byte[] buffer = new byte[1024 * 8];
				int byteCount = fis.read(buffer);
				if (byteCount > 0) {
					mac = new String(buffer, 0, byteCount, "utf-8");
				}
			}

			if (mac.length() == 0 || mac == null) {
				return "";
			}
		} catch (Exception io) {

		}finally{
			try {
				fis_name.close();
				//fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(mac.trim());
		return mac.trim();
	}
}
