package com.example.compaq.nfc_teacher;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.example.compaq.nfc_teacher.BluetoothTools;
import com.example.compaq.nfc_teacher.TransmitBean;
import com.example.compaq.nfc_teacher.FileSend;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends Activity {

	Button set_button;
	Button Open_NFC;
	Button listview_button;
	Button NormalAttendence_button;
	Button NA_File_Chooser;
	Button Reflect_infor;
	public static final int RESULT_CODE = 1000;    //选择文件   请求码
	public static final String SEND_FILE_NAME = "sendFileName";


	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//设置标题不确定性进度条风格
		//requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		//setContentView(R.layout.progressbar);
		//显示标题不确定性进度条
		//setProgressBarIndeterminateVisibility(true);

		set_button=(Button)findViewById(R.id.set_button);
		Open_NFC=(Button)findViewById(R.id.OpenNFC_button);
		listview_button=(Button)findViewById(R.id.listview_button);
		NormalAttendence_button=(Button)findViewById(R.id.NormalAttendence_Button);
		NA_File_Chooser=(Button)findViewById(R.id.NA_FileChooser);
		Reflect_infor=(Button)findViewById(R.id.Reflect_infor);

		NormalAttendence_button.setOnClickListener(new listener());
		set_button.setOnClickListener(new listener());
		listview_button.setOnClickListener(new listener());
		Open_NFC.setOnClickListener(new listener());
		NA_File_Chooser.setOnClickListener(new listener());
		Reflect_infor.setOnClickListener(new listener());

		//实现屏幕常亮
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


	}


	class listener implements View.OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId()){
				case R.id.set_button:
					Intent intent=new Intent();
					intent.setClass(MainActivity.this,CreateNameList.class );
					MainActivity.this.startActivity(intent);
					finish();
					break;
				case R.id.OpenNFC_button:
					new OpenNFC(MainActivity.this);
					break;
				case R.id.listview_button:
					Intent intent_listview=new Intent();
					intent_listview.setClass(MainActivity.this,ListViewDB.class );
					MainActivity.this.startActivity(intent_listview);
					finish();
					break;
				case R.id.NormalAttendence_Button:
					Intent intent_NormalAttendence=new Intent();
					intent_NormalAttendence.setClass(MainActivity.this,NormalAttendence.class );
					MainActivity.this.startActivity(intent_NormalAttendence);
					finish();
					break;
				case R.id.NA_FileChooser:
					System.out.println("======选择文件开始======");
					//弹出框定义
					AlertDialog.Builder alertdialog=new AlertDialog.Builder(MainActivity.this);
					alertdialog.setTitle("请选择要下发的文件");
					alertdialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							try{
								//startActivityForResult(Intent.createChooser(intent, "请选择点名文件"),1);
								Intent intent_selectfile = new Intent(MainActivity.this, com.example.compaq.nfc_teacher.SelectFileActivity.class);
								startActivityForResult(intent_selectfile, com.example.compaq.nfc_teacher.SelectFileActivity.RESULT_CODE);
							}catch(android.content.ActivityNotFoundException ex){
								Toast.makeText(getApplicationContext(), "请安装文件选择器", Toast.LENGTH_LONG).show();
							}
							catch (NullPointerException e) {
								// TODO: handle exception
								e.printStackTrace();
							}
						}

					});
					alertdialog.setNegativeButton("取消", null);
					alertdialog.show();
					break;
				case R.id.Reflect_infor:
					Intent intent_Reflect_infor=new Intent();
					intent_Reflect_infor.setClass(MainActivity.this,Reflect_information.class );
					MainActivity.this.startActivity(intent_Reflect_infor);
					finish();
					break;
			}
		}

	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == RESULT_CODE){
			//请求为 "选择文件"
			try {
				//取得选择的文件名
				String sendFileName = data.getStringExtra(SEND_FILE_NAME);
				StaticValue.select_filename=sendFileName;
				System.out.println("选择的文件是："+StaticValue.select_filename);
			} catch (Exception e) {
			}
		}
	}



	/*
	//设置按钮的监听器
	class MyButtonListener implements View.OnClickListener{
		public void onClick(View v){
			Intent intent=new Intent();
			intent.setClass(MainActivity.this,CreateNameList.class );
			MainActivity.this.startActivity(intent);
			finish();
		}

	}

	//listview显示按钮的监听器
	class ListView_ButtonListener implements View.OnClickListener{
			public void onClick(View v){
				Intent intent=new Intent();
				intent.setClass(MainActivity.this,ListViewDB.class );
				MainActivity.this.startActivity(intent);
				finish();
			}

		}

	//NormalAttendence_button的监听器
	class NormalAttendence_ButtonListener implements View.OnClickListener{
		public void onClick(View v){
			Intent intent=new Intent();
			intent.setClass(MainActivity.this,NormalAttendence.class );
			MainActivity.this.startActivity(intent);
			finish();
		}

	}

	class OpenNFC_Listener implements View.OnClickListener{
		public void onClick(View v){
			new OpenNFC(MainActivity.this);
		}

	}
	*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 1, 1, R.string.exit);
		menu.add(0, 2, 2, R.string.about);
		return super.onCreateOptionsMenu(menu);
	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		AlertDialog.Builder alertdialog_long=new AlertDialog.Builder(MainActivity.this);
		alertdialog_long.setTitle("是否退出我们的软件");
		alertdialog_long.setNegativeButton("暂时不用", null);
		alertdialog_long.setPositiveButton("确认退出", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		alertdialog_long.show();
		return super.onKeyDown(keyCode, event);
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==1){
			finish();
		}
		switch(item.getItemId()){
			case 1:
				finish();
				break;
			case 2:
				Toast.makeText(getApplicationContext(), "我们棒棒哒！", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}



	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
