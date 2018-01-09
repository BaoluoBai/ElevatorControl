package com.eightmile.elevatorcontrol.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;

import android.text.TextUtils;

public class Tools {
	public static boolean fileIsExist(String url){
		 try{
            File f=new File(url);
            if(!f.exists()){
            	return false;
            }
            
		 }catch (Exception e) {
            // TODO: handle exception
            return false;
		 }
		 return true;
	}
	
	
	public static String getNameFromUrl(String url) {
   	
       return url.substring(url.lastIndexOf("/") + 1);
   }
	
	
	/**
	 * 写数据到文件
	 * @param str 写入的字符串
	 * @param name 文件名
	 * @param path 文件路径
	 * @param supplements 是否补充添加
	 * @return
	 * @throws IOException
	 */
	public static Boolean writeStrToFile(String str, String name, String path, Boolean supplements) {
   	String old_str;
   	File file_path = new File(path);
   	if (!file_path.exists()) {
   		file_path.mkdirs();
 		}
   	File file = new File(path+name);
   	if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				return false;
			}
		}
		try {
			FileWriter fileWritter = new FileWriter(file);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			if(supplements){
				String old_data;
				try {
					old_data = getStrFromFile(name,path);
					if(TextUtils.isEmpty(old_data)){
						old_str  = old_data;
					}else{
						old_str = "";
					}
					str = old_str + str;
				} catch (JSONException e) {
					bufferWritter.close();
					return false;
				}
			}
			bufferWritter.write(str);
			bufferWritter.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	
	/**
    * 读取文件值
    * @param name
    * @param path
    * @return
    * @throws JSONException
    */
   public static String getStrFromFile(String name, String path) throws JSONException{
		String result = "";
		File file = new File(path+name);
		if (!file.exists()) {
			result = "0";
		}else{
			try {  
				InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");  
				BufferedReader br = new BufferedReader(isr);    
				String str = "";     
				String mimeTypeLine = null ;  
				while ((mimeTypeLine = br.readLine()) != null) {  
					str = str+mimeTypeLine;  
				}
				br.close();
				result = str;
			} catch (Exception e) {
				result = "0";
			}
		}
		return result;
	}
}
