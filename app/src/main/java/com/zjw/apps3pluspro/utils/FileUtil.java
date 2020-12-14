package com.zjw.apps3pluspro.utils;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class FileUtil {
	/**
	 * 加载byte数组，并写入本地文件
	 * 
	 * @param data 数据流
	 * @param filePath 文件地址
	 * @return
	 */
	public static void ByteToFile(byte[] data, String filePath){
		File file = new File(filePath);
		ByteArrayInputStream bis = null;
		FileOutputStream fos = null;
		int len = -1;
		byte[] buffer = new byte[1024];
		try {
			bis = new ByteArrayInputStream(data);
			fos = new FileOutputStream(file);
			while ((len = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			try {
				fos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 加载本地文件，并转换为byte数组
	 * 
	 * @param filePath 文件地址
	 * @return
	 * @throws Exception 
	 */
	public static byte[] FileToByte(String filePath) throws Exception {
		File file = new File(filePath);

		FileInputStream fis = null;
		ByteArrayOutputStream baos = null;
		byte[] data = null;

		try {
			fis = new FileInputStream(file);
			baos = new ByteArrayOutputStream((int) file.length());

			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = fis.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			baos.flush();
			data = baos.toByteArray();
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (fis != null) {
					fis.close();
					fis = null;
				}
				if (baos != null) {
					baos.close();
					baos = null;
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return data;
	}
	

	public static void copyAssetFileToFiles(Context context, String filename)
			throws IOException {
		InputStream is = context.getAssets().open(filename);
		byte[] buffer = new byte[is.available()];
		is.read(buffer);
		is.close();

		File of = new File(context.getFilesDir() + "/" + filename);
		if(of.exists()){
			return;
		}
		of.createNewFile();
		FileOutputStream os = new FileOutputStream(of);
		os.write(buffer);
		os.close();
	}

	public static void write(String path, String content) throws IOException{
		try {
			File f = new File(path);
			if(f.exists()){
				f.delete();
			}
			f.createNewFile();
			BufferedWriter output = new BufferedWriter(new FileWriter(f));
			output.write(content);
			output.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * base64转为bitmap
	 *
	 * @param base64Data
	 * @return
	 */
	public static Bitmap base64ToBitmap(String base64Data) {
		byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}


	/**
	 * bitmap转为base64
	 *
	 * @param bitmap
	 * @return
	 */
	public static String bitmapToBase64(Bitmap bitmap) {

		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static File CreateFile(String path, String name) {

		File file = null;

		//创建文件夹
		File new_fload = new File(path);
		if (!new_fload.exists()) {
			new_fload.mkdirs();// 创建文件夹
		}

		//创建文件
		file = null;
		try {
			file = new File(path + name);
			if (file.createNewFile()) {
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return file;

	}

	/**
	 * 向文件中添加内容
	 */
	public static long NewwriteToFile(File subfile, byte[] data) {

		RandomAccessFile raf = null;
		try {
			/**   构造函数 第二个是读写方式    */
			raf = new RandomAccessFile(subfile, "rw");
			/**  将记录指针移动到该文件的最后  */
			raf.seek(subfile.length());
			/** 向文件末尾追加内容  */
			raf.write(data);

			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return subfile.length();
	}
	
}
