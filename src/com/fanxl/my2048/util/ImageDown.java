package com.fanxl.my2048.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class ImageDown {
	
	public static String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Game248";
	
	
	/**
	 * 从网络下载图片
	 * @param url
	 * @return Bitmap
	 */
	@SuppressWarnings("resource")
	public static Bitmap getImage(String imgUrl){
		
		HttpURLConnection con = null;
		InputStream is = null; 
		OutputStream os = null;
		File dir = new File(PATH);
		if(!dir.exists())dir.mkdir();
		try {
			URL url = new URL(imgUrl);
			con = (HttpURLConnection) url.openConnection();
			con.setReadTimeout(3000);
			con.setRequestMethod("GET");
			
			if(con.getResponseCode() == HttpStatus.SC_OK){
				is = con.getInputStream();
				os = new FileOutputStream(new File(dir, "image.png"));
				byte[] buffer = new byte[1024];
				int length = 0;
				while((length = is.read(buffer)) != -1){
					os.write(buffer, 0 ,length);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				os.close();
				is.close();
				con.disconnect();
			} catch (Exception e2) {
			}
		}
		Bitmap bitmap = BitmapFactory.decodeFile(PATH+"/image.png");
		return bitmap;
	}

}
