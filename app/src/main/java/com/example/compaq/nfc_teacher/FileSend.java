package com.example.compaq.nfc_teacher;
import java.io.IOException;
import java.io.OutputStream;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.example.compaq.nfc_teacher.TransmitBean;
import com.example.compaq.nfc_teacher.BluetoothCommunSocket;
import com.example.compaq.nfc_teacher.ClsUtils;
import com.example.compaq.nfc_teacher.R;
import com.example.compaq.nfc_teacher.StaticValue;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.widget.Toast;


public class FileSend extends Activity {

	public static final int RESULT_CODE = 1000;    //选择文件   请求码
	public static final String SEND_FILE_NAME = "sendFileName";
	BluetoothCommunSocket communsocket;
	Thread sendthread;
	NfcAdapter nfcadapter;
	PendingIntent pendingintent;
	Handler handler;
	public String result_macaddress;
	BluetoothDevice bluetoothDevice;
	BluetoothSocket bluetoothSocket;
	BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private final UUID MY_UUID = UUID.fromString("db764ac8-4b08-7f25-aafe-59d03c27bae3");
	private OutputStream os;
	private final String NAME = "Bluetooth_Socket";
	private final String targetFilename = Environment
			.getExternalStorageDirectory().getPath()
			+ "/123.txt";



	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_sent_layout);

		System.out.println(targetFilename);
		//Toast.makeText(this, targetFilename, Toast.LENGTH_LONG).show();

		nfcadapter=NfcAdapter.getDefaultAdapter(this);
		pendingintent=PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		IntentFilter intentfilter=new IntentFilter();
		intentfilter.addAction(BluetoothTools.ACTION_FILE_SEND_PERCENT);
		registerReceiver(receiver, intentfilter);


	}




	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}




	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("----onResume----");
		if (this.nfcadapter == null)
			return;
		if (!this.nfcadapter.isEnabled()) {
			System.out.println("请在系统设置中先启用NFC功能");
		}
		this.nfcadapter.enableForegroundDispatch(this, pendingintent, null, null);

	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	@SuppressLint("NewApi")
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		System.out.println("----onPause----");
		if(NfcAdapter.getDefaultAdapter(this)!=null){
			NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);

		}
		System.out.println("----onPause----");
	}

	@SuppressLint("NewApi")
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		System.out.println("----onNewIntent----");
		setIntent(intent);
		try {
			resolveIntent(intent);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	protected void resolveIntent(Intent intent) throws UnsupportedEncodingException, FormatException {
		// 得到是否TAG触发
		System.out.println("----resolveIntent----");
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
				|| NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))
		{
			//autowrite(intent);
			// 处理该intent
			//Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			Parcelable[] rawMsgs =
					intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			// only one message sent during the beam
			NdefMessage msg = (NdefMessage)rawMsgs[0];
			// 获取id数组
			//byte[] bytesId = tag.getId();
			System.out.println("自动写入成功,接收的数据长度为："+msg.getRecords().length);
			result_macaddress=new String(msg.getRecords()[0].getPayload(),"GBK").substring(1);
			System.out.println("+++++++++" + result_macaddress);
			Toast.makeText(this, result_macaddress, Toast.LENGTH_LONG).show();
			bluetoothDevice=bluetoothAdapter.getRemoteDevice(result_macaddress);
			if(bluetoothDevice!=null){
				System.out.println("==获取成功==");
				System.out.println("地址是："+bluetoothDevice.getName());
			}

			try {
				ClsUtils.setPin(bluetoothDevice.getClass(), bluetoothDevice, "0000"); // 手机和蓝牙采集器配对
				ClsUtils.createBond(bluetoothDevice.getClass(), bluetoothDevice);
				ClsUtils.cancelPairingUserInput(bluetoothDevice.getClass(), bluetoothDevice);
				ClsUtils.pair(result_macaddress, "0000");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println("地址是："+bluetoothDevice.getName());
			Thread thead=new connectThread(bluetoothDevice);
			thead.start();


		}
	}

	public class connectThread extends Thread{
		BluetoothSocket mySocket;
		BluetoothDevice mydevice;
		@SuppressLint("NewApi")
		public connectThread(BluetoothDevice device) {
			BluetoothSocket tmp=null;
			try{
				tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
			}catch (Exception e){

			}
			mySocket=tmp;
		}

		public void run(){
			try {
				bluetoothAdapter.cancelDiscovery();
				mySocket.connect();
				//doSendFileByBluetooth(handler, StaticValue.path, bluetoothDevice, mySocket);
				if(mySocket!=null){
					System.out.println("连接成功！");
					StaticValue.socket=mySocket;
					FileSend.this.startService(new Intent(FileSend.this,SendFileService.class));
					Thread sendtext=new sendThread(mySocket);
					sendtext.start();
				}

			}catch (Exception e){

			}
		}


	}

	private class sendThread extends Thread {

		BluetoothSocket socket;

		public sendThread(BluetoothSocket mysocket){

			socket=mysocket;

		}

		public void run() {

			TransmitBean transmit = new TransmitBean();
			String path=StaticValue.select_filename;
			String filename=path.substring(path.lastIndexOf("/")+1,path.length());
			transmit.setFilename(filename);
			transmit.setFilepath(path);
			Intent sendDataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_SERVICE);
			sendDataIntent.putExtra(BluetoothTools.DATA, transmit);
			sendBroadcast(sendDataIntent);
			System.out.println("广播成功！！！！");
			//Toast.makeText(Bluetooth_Text.this, buffer.toString(), Toast.LENGTH_LONG).show();
			//showtext.setText(buffer.toString());
		}
	}
//	@Override
//	public void onBackPressed() {
//	// 这里处理逻辑代码，注意：该方法仅适用于2.0或更新版的sdk
//		//关闭后台Service
//		Intent stopService = new Intent(BluetoothTools.ACTION_STOP_SERVICE);
//		sendBroadcast(stopService);
//		unregisterReceiver(broadcastReceiver);
//		super.onBackPressed();
//	}

	BroadcastReceiver receiver=new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			// TODO Auto-generated method stub

			System.out.println("广播收到！");
			String action = arg1.getAction();
			if (BluetoothTools.ACTION_FILE_SEND_PERCENT.equals(action)) {
				class MyRunnable implements Runnable{
					public void run(){
						System.out.println("=====接收成功！=======");
					}
				}
				Thread t=new Thread(new MyRunnable());
				t.start();
			}


		}
	};

	@Override
	protected void onStop() {
		super.onStop();
	}



}

