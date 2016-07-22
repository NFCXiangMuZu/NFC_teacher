package com.example.compaq.nfc_teacher;

import java.sql.Timestamp;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.Editable.Factory;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListViewDB extends Activity {

	public static final String TAG="ListViewDB";
	public ListView listview;
	public TextView ListDB_Title;
	public SQLiteManager myhelper=new SQLiteManager(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题
		setContentView(R.layout.listviewdatabase);


		if(StaticValue.MY_TABLE_NAME==null){
			//弹出框定义
			AlertDialog.Builder alertdialog=new AlertDialog.Builder(ListViewDB.this);
			alertdialog.setTitle("请选择点名班级");
			alertdialog.setPositiveButton("回到主页",new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					Intent intent=new Intent();
					intent.setClass(ListViewDB.this,MainActivity.class);
					ListViewDB.this.startActivity(intent);
					finish();
				}

			});
			alertdialog.setNegativeButton("取消", null);
			alertdialog.show();
		}
		else {
			listview = (ListView) findViewById(R.id.namelist_ListView);
			ListDB_Title = (TextView) findViewById(R.id.ListDB_title);
			ListDB_Title.setText(StaticValue.MY_TABLE_NAME + "\n学生签到情况");

			System.out.println(StaticValue.MY_TABLE_NAME);

			//获得游标集
			System.out.println("=============" + StaticValue.DATABASE_NAME + "=============");
			System.out.println("=============" + StaticValue.MY_TABLE_NAME + "=============");

			Load_listview();

			//添加选中事件
			listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

				@SuppressWarnings("static-access")
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
											   final int arg2, long arg3) {
					// TODO Auto-generated method stub

					System.out.println("点击的Item是:" + arg2);

					//弹出框定义
					AlertDialog.Builder alertdialog = new AlertDialog.Builder(ListViewDB.this);
					alertdialog.setTitle("请修改[" + SQLiteManager.query_name(StaticValue.MY_TABLE_NAME, arg2 + 1) + "]学生的签到信息");
					LayoutInflater inflater = LayoutInflater.from(ListViewDB.this);
					final View listview_dialog = inflater.inflate(R.layout.listviewdialog, null);
					alertdialog.setView(listview_dialog);
					alertdialog.setNegativeButton("取消修改", null);
					alertdialog.setNeutralButton("删除", null);
					alertdialog.setPositiveButton("确定修改", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							String str_name = SQLiteManager.query_name(StaticValue.MY_TABLE_NAME, arg2 + 1);
							String str_xuehao = SQLiteManager.query_xuehao(StaticValue.MY_TABLE_NAME, arg2 + 1);
							int int_chuxi;
							int int_quexi;
							int int_qingjia;
							EditText et_chuxi = (EditText) listview_dialog.findViewById(R.id.LDL_EditText_chuxi);
							System.out.println("出席情况改为:" + et_chuxi.getText().toString());
							//et_chuxi.setHint(""+SQLiteManager.query_chuxi(StaticValue.MY_TABLE_NAME, arg2));
							/*if(et_chuxi.getText().toString().equals("")){
								//int_chuxi=SQLiteManager.query_chuxi(StaticValue.MY_TABLE_NAME,arg2);
							}else{
								int_chuxi=Integer.parseInt(et_chuxi.getText().toString());
							}*/
							int_chuxi = Integer.parseInt(et_chuxi.getText().toString());

							EditText et_quexi = (EditText) listview_dialog.findViewById(R.id.LDL_EditText_quexi);
							System.out.println(et_quexi.getText().toString());
                            /*if(et_chuxi.getText().toString().equals("")){
                            	//int_quexi=SQLiteManager.query_quexi(StaticValue.MY_TABLE_NAME,arg2);
							}else{
								int_quexi=Integer.parseInt(et_quexi.getText().toString());
							}*/
							int_quexi = Integer.parseInt(et_quexi.getText().toString());

							EditText et_qingjia = (EditText) listview_dialog.findViewById(R.id.LDL_EditText_qingjia);
							System.out.println(et_qingjia.getText().toString());
                            /*if(et_chuxi.getText().toString().equals("")){
                            	//int_qingjia=SQLiteManager.query_qingjia(StaticValue.MY_TABLE_NAME,arg2);
							}else{
								int_qingjia=Integer.parseInt(et_qingjia.getText().toString());
							}*/
							int_qingjia = Integer.parseInt(et_qingjia.getText().toString());

							Timestamp now = new Timestamp(System.currentTimeMillis());//获取系统当前时间
							myhelper.updateData(StaticValue.MY_TABLE_NAME, str_name, str_xuehao, int_chuxi, int_quexi, int_qingjia, now);
							//System.out.println(str);
							//Toast.makeText(ListViewDB.this, e.getText(), Toast.LENGTH_SHORT).show();
							Load_listview();

						}


					});
					alertdialog.show();
					return false;
				}

			});
		}

	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "回到主页", Toast.LENGTH_LONG).show();
		Intent intent=new Intent();
		intent.setClass(ListViewDB.this,MainActivity.class);
		ListViewDB.this.startActivity(intent);
		finish();
		return super.onKeyDown(keyCode, event);
	}


	public void Load_listview(){
		final Cursor cursor=myhelper.getCursorScrolldata(0, 44);

		//添加适配器
		@SuppressWarnings("deprecation")
		SimpleCursorAdapter simplecursoradapter=new SimpleCursorAdapter(this, R.layout.sqliteview, cursor,
				new String[]{"_id","name","chuxi","quexi","qingjia"}, new int[]{R.id.TextView_id,
				R.id.TextView_name,R.id.TextView_chuxi,R.id.TextView_quexi,
				R.id.TextView_qingjia});
		listview.setAdapter(simplecursoradapter);
	}

	//回到主页
	class back_listener implements View.OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "回到主页", Toast.LENGTH_LONG).show();
			Intent intent=new Intent();
			intent.setClass(ListViewDB.this,MainActivity.class);
			ListViewDB.this.startActivity(intent);
			finish();
		}

	}


}
