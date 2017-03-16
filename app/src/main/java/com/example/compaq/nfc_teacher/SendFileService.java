package com.example.compaq.nfc_teacher;

/**
 * 接收开始文件发送广播的服务
 */

import java.io.IOException;
import java.io.Serializable;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class SendFileService extends Service {


	//自定义蓝牙通信socket
	private BluetoothCommunSocket communSocket;
    //获取蓝牙适配器
	BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();

	BluetoothSocket socket=null;
	BluetoothDevice device;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		System.out.println("服务开启！");
		IntentFilter intentfilter=new IntentFilter();
		intentfilter.addAction(BluetoothTools.ACTION_DATA_TO_SERVICE);
		registerReceiver(controlReceiver, intentfilter);
		System.out.println("注册成功！！");
		super.onCreate();
	}

	/**
	 * 接收开始问价发送广播的接收器
	 */
	BroadcastReceiver controlReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("开始文件传输");
			//先获取远程蓝牙设备
			device=adapter.getRemoteDevice(StaticValue.macaddress);
			if(device!=null){
				System.out.println("==获取成功==");
			}
			try {
				//连接到远程蓝牙设备
				socket = device.createRfcommSocketToServiceRecord(BluetoothTools.PRIVATE_UUID);
				socket.connect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(socket!=null){
				System.out.println("socket获取成功！");
				String action = intent.getAction();
				if (BluetoothTools.ACTION_DATA_TO_SERVICE.equals(action)) {
					communSocket = new BluetoothCommunSocket(handler,socket);
					final TransmitBean transmit = (TransmitBean)intent.getExtras().getSerializable(BluetoothTools.DATA);
					if (communSocket != null) {
						class MyRunnable implements Runnable{
							public void run(){
								//开始文件发送
								communSocket.write(transmit);
							}
						}
						Thread t=new Thread(new MyRunnable());
						t.start();
					}
				}
			}else{
				System.out.println("socket获取失败！");
			}
		}
	};

	/**
	 * 接收蓝牙通信信息的handler
	 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//处理消息
			switch (msg.what) {
				case BluetoothTools.MESSAGE_CONNECT_ERROR://连接错误
					//发送连接错误广播
					Intent errorIntent = new Intent(BluetoothTools.ACTION_CONNECT_ERROR);
					sendBroadcast(errorIntent);
					break;
				case BluetoothTools.MESSAGE_CONNECT_SUCCESS://连接成功
					//开启通讯线程
					break;
				case BluetoothTools.MESSAGE_READ_OBJECT://读取到对象
					//发送数据广播（包含数据对象）
					Intent dataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_GAME);
					dataIntent.putExtra(BluetoothTools.DATA, (Serializable)msg.obj);
					sendBroadcast(dataIntent);
					break;
				case BluetoothTools.FILE_SEND_PERCENT://文件发送百分比
					//发送文件传输百分比广播，实现进度条用
					System.out.println("文件传输中");
					Intent flieIntent = new Intent(BluetoothTools.ACTION_FILE_SEND_PERCENT);
					flieIntent.putExtra(BluetoothTools.DATA, (Serializable)msg.obj);
					sendBroadcast(flieIntent);
					break;
				case BluetoothTools.FILE_RECIVE_PERCENT://文件接收百分比
					//接收文件传输百分比广播，实现进度条用
					Intent flieIntent1 = new Intent(BluetoothTools.ACTION_FILE_RECIVE_PERCENT);
					flieIntent1.putExtra(BluetoothTools.DATA, (Serializable)msg.obj);
					sendBroadcast(flieIntent1);
					break;
				case BluetoothTools.FILE_SEND_SUCCESS:
					//文件发送成功信号
					System.out.println("文件发送成功");
					Intent file_send_success_Intent = new Intent(BluetoothTools.ACTION_FILE_SEND_SUCCESS);
					sendBroadcast(file_send_success_Intent);//发送文件传输成功的广播信息
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(controlReceiver);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
