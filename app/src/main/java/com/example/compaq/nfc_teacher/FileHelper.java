package com.example.compaq.nfc_teacher;
/**
 * @Title: FileHelper.java
 * @Package com.tes.textsd
 * @Description: TODO(用一句话描述该文件做什么)
 * @author Alex.Z
 * @date 2013-2-26 下午5:45:40
 * @version V1.0
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
public class FileHelper {
	private Context context;
	/** SD卡是否存在**/
	private boolean hasSD = false;
/** SD卡的路径**/
//private String SDPATH;
	/** 当前程序包的路径**/
	private String FILESPATH;
	public FileHelper(Context context) {
		this.context = context;
		hasSD = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
//SDPATH = Environment.getExternalStorageDirectory().getPath();
		FILESPATH = this.context.getFilesDir().getPath();
	}
	/**
	 * 在SD卡上创建文件
	 *
	 * @throws IOException
	 */
	public static File createSDFile(String fileName) throws IOException {
		File file = new File(StaticValue.SDPATH + "//" + fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}
	/**
	 * 删除SD卡上的文件
	 *
	 * @param fileName
	 */
	public static boolean deleteSDFile(String fileName) {
		File file = new File(StaticValue.SDPATH + "//" + fileName);
		if (file == null || !file.exists() || file.isDirectory())
			return false;
		return file.delete();
	}
	/**
	 * 写入内容到SD卡中的txt文本中
	 * str为内容
	 */
	public static void writeSDFile(String str,String fileName)
	{
		File f = new File(StaticValue.SDPATH + "//" + fileName);
		try {
			//第二个参数意义是说是否以append方式添加内容
			BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
			bw.write(str);
			bw.write("\r\n");
			bw.flush();
			System.out.println("写入成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 读取SD卡中文本文件
	 *
	 * @param fileName
	 * @return
	 */
	public static List<String> readSDFile(String fileName) {

		List<String> list=new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		try {
			File file = new File(Environment.getExternalStorageDirectory(),
					fileName);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String readline = "";

			while ((readline = br.readLine()) != null) {
				System.out.println("readline:" + readline);
				list.add(readline);
				sb.append(readline);
			}
			br.close();
			System.out.println("读取成功：" + sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 删除SD卡中文本文件的某一行内容
	 *
	 * @param fileName
	 * @return
	 */

	public static List<String> Delete_oneFile(String fileName,int del_num) {

		List<String> list=new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		try {
			File file = new File(Environment.getExternalStorageDirectory(),
					fileName);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String readline = "";

			while ((readline = br.readLine()) != null) {
				System.out.println("readline:" + readline);
				list.add(readline);
				sb.append(readline);
			}

			list.remove(del_num);

			System.out.println("读取成功：" + sb.toString());

			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}


	public String getFILESPATH() {
		return FILESPATH;
	}
	public String getSDPATH() {
		return StaticValue.SDPATH;
	}
	public boolean hasSD() {
		return hasSD;
	}
} 
