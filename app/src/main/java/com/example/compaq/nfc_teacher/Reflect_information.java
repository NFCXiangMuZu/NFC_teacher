package com.example.compaq.nfc_teacher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class Reflect_information extends Activity{

	ListView reflect_infor_list;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reflect_infor_layout);

		String[] strs = new String[] {
				"first", "second", "third", "fourth", "fifth"
		};

		reflect_infor_list=(ListView)findViewById(R.id.reflect_infor_list);

		if(StaticValue.MY_TABLE_NAME==null){
			//弹出框定义
			AlertDialog.Builder alertdialog=new AlertDialog.Builder(Reflect_information.this);
			alertdialog.setTitle("请选择点名班级");
			alertdialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					final Vector<String> db_list_str_2=new Vector<String>();
					CreateNameList.select_namelist(getPackageName().toString(),
							Reflect_information.this,db_list_str_2);
					load_listview();
				}

			});
			alertdialog.setNegativeButton("取消", null);
			alertdialog.show();
		}
		else{
			load_listview();
		}
		System.out.println(FileHelper.readSDFile(StaticValue.MY_TABLE_NAME+".txt"));
		//添加选中事件
		reflect_infor_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@SuppressWarnings("static-access")
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
										   final int arg2, long arg3) {
				// TODO Auto-generated method stub

				System.out.println("点击的Item是:"+arg2);
				Toast.makeText(Reflect_information.this,
						"点击的Item是:"+arg2, Toast.LENGTH_SHORT).show();
				//弹出框定义

				AlertDialog.Builder alertdialog=new AlertDialog.Builder(Reflect_information.this);
				alertdialog.setTitle("是否删除此条目");
				alertdialog.setPositiveButton("确认删除",null);
				alertdialog.setNegativeButton("取消", null);
				alertdialog.show();

				return false;



			}

		});
	}

	public void load_listview(){
		reflect_infor_list.setAdapter(new ArrayAdapter<String>(this,
				R.layout.reflect_infor_listview,
				FileHelper.readSDFile(StaticValue.MY_TABLE_NAME+".txt")));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "回到主页", Toast.LENGTH_LONG).show();
		Intent intent=new Intent();
		intent.setClass(Reflect_information.this,MainActivity.class);
		Reflect_information.this.startActivity(intent);
		finish();
		return super.onKeyDown(keyCode, event);
	}
}
