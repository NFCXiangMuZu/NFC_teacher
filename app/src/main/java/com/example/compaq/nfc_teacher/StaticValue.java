package com.example.compaq.nfc_teacher;

import java.util.ArrayList;

import java.util.List;

import android.bluetooth.BluetoothSocket;
import android.net.Uri;
import android.os.Environment;

public class StaticValue {
	public static int MAX=0;
	public static String inner_namelist;
	public static String INNERNAMELIST;
	public static  String MY_TABLE_NAME=null;
	public static  String DATABASE_NAME=MY_TABLE_NAME+".db";
	public static  int student_num = 40;
	public static  Uri path=null;
	public static String select_filename=null;
	public static String send_filename = null;
	public static int status=0;
	public static List<String> reflect_information=new ArrayList<String>();
	public static String SDPATH = Environment.getExternalStorageDirectory().getPath();
	public static String result_macaddress=null;
	public static BluetoothSocket socket=null;
	public static long file_length=0;
	public static String macaddress=null;//发送文件的目的端蓝牙地址
	public static int file_send_percent = 0;
	public static int file_send_length = 0;
	public static double file_send_time;
	//正在接收文件的学生数目
	public static int receive_file_student_num = 0;
}
