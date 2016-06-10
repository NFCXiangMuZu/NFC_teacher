package com.example.compaq.nfc_teacher;

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
import com.example.compaq.nfc_teacher.BluetoothCommunSocket;
import com.example.compaq.nfc_teacher.StaticValue;

public class SendFileService extends Service {


	//蓝牙通讯
	private BluetoothCommunSocket communSocket;

	BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();

	BluetoothSocket socket=null;
	BluetoothDevice device;

	//控制信息广播的接收器
	BroadcastReceiver controlReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("广播收到！");
			device=adapter.getRemoteDevice(StaticValue.macaddress);
			if(device!=null){
				System.out.println("==获取成功==");
				System.out.println("地址是："+device.getName());
			}
			try {
				socket = device.createRfcommSocketToServiceRecord(BluetoothTools.PRIVATE_UUID);
				socket.connect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String action = intent.getAction();
			if (BluetoothTools.ACTION_DATA_TO_SERVICE.equals(action)) {
				communSocket = new BluetoothCommunSocket(handler,socket);
				final TransmitBean transmit = (TransmitBean)intent.getExtras().getSerializable(BluetoothTools.DATA);
				if (communSocket != null) {
					class MyRunnable implements Runnable{
						public void run(){
							communSocket.write(transmit);
							System.out.println("=====发送成功！=======");
						}
					}
					Thread t=new Thread(new MyRunnable());
					t.start();
				}
			}
		}
	};

	//接收其他线程消息的Handler
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
					//	communSocket = new BluetoothCommunSocket(handler, (BluetoothSocket)msg.obj);
					//	communSocket.start();
					//发送连接成功广播
//						Intent succIntent = new Intent(BluetoothTools.ACTION_CONNECT_SUCCESS);
//						sendBroadcast(succIntent);
					break;
				case BluetoothTools.MESSAGE_READ_OBJECT://读取到对象
					//发送数据广播（包含数据对象）
					Intent dataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_GAME);
					dataIntent.putExtra(BluetoothTools.DATA, (Serializable)msg.obj);
					sendBroadcast(dataIntent);
					break;
				case BluetoothTools.FILE_SEND_PERCENT://文件发送百分比
					//发送文件传输百分比广播，实现进度条用
					System.out.println("=====文件传输中====");
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
					System.out.println("====+++++====文件发送成功啦！！！！===++++=====");
					//unregisterReceiver(controlReceiver);
					Intent file_send_success_Intent = new Intent(BluetoothTools.ACTION_FILE_SEND_SUCCESS);
					sendBroadcast(file_send_success_Intent);
			}
			super.handleMessage(msg);
		}
	};





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
