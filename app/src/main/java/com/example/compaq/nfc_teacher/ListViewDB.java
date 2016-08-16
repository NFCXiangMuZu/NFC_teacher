package com.example.compaq.nfc_teacher;

/**
 * 显示数据库中对应班级的点名情况
 */

import java.sql.Timestamp;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
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
	//layout元素定义
	public ListView listview;
	public TextView ListDB_Title;
	//数据库操作对象获取
	public SQLiteManager myhelper=new SQLiteManager(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题
		setContentView(R.layout.listviewdatabase);

        //先判断是否选择点名班级，没选则返回主界面
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

			//向列表对象中加载数据
			Load_listview();

			//添加长按选中事件
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
					//初始化layout
					Button LDL_PlusButton_chuxi = (Button) listview_dialog.findViewById(R.id.LDL_PlusButton_chuxi);
					Button LDL_PlusButton_quexi = (Button) listview_dialog.findViewById(R.id.LDL_PlusButton_quexi);
					Button LDL_PlusButton_qingjia = (Button) listview_dialog.findViewById(R.id.LDL_PlusButton_qingjia);
					Button LDL_MinusButton_chuxi = (Button)listview_dialog.findViewById(R.id.LDL_MinusButton_chuxi);
					Button LDL_MinusButton_quexi = (Button)listview_dialog.findViewById(R.id.LDL_MinusButton_quexi);
					Button LDL_MinusButton_qingjia = (Button)listview_dialog.findViewById(R.id.LDL_MinusButton_qingjia);
					final TextView LDL_PlusNum_chuxi = (TextView)listview_dialog.findViewById(R.id.LDL_PlusNum_chuxi);
					final TextView LDL_PlusNum_quexi = (TextView)listview_dialog.findViewById(R.id.LDL_PlusNum_quexi);
					final TextView LDL_PlusNum_qingjia = (TextView)listview_dialog.findViewById(R.id.LDL_PlusNum_qingjia);
					//为按钮绑定监听器
					LDL_PlusButton_chuxi.setOnClickListener(new View.OnClickListener(){
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							LDL_PlusNum_chuxi.setText(""+(Integer.parseInt(LDL_PlusNum_chuxi.getText().toString())+1));
						}
					});
					LDL_MinusButton_chuxi.setOnClickListener(new View.OnClickListener(){
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							LDL_PlusNum_chuxi.setText(""+(Integer.parseInt(LDL_PlusNum_chuxi.getText().toString())-1));
						}
					});

					LDL_PlusButton_quexi.setOnClickListener(new View.OnClickListener(){
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							LDL_PlusNum_quexi.setText(""+(Integer.parseInt(LDL_PlusNum_quexi.getText().toString())+1));
						}
					});
					LDL_MinusButton_quexi.setOnClickListener(new View.OnClickListener(){
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							LDL_PlusNum_quexi.setText(""+(Integer.parseInt(LDL_PlusNum_quexi.getText().toString())-1));
						}
					});

					LDL_PlusButton_qingjia.setOnClickListener(new View.OnClickListener(){
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							LDL_PlusNum_qingjia.setText(""+(Integer.parseInt(LDL_PlusNum_qingjia.getText().toString())+1));
						}
					});
					LDL_MinusButton_qingjia.setOnClickListener(new View.OnClickListener(){
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							LDL_PlusNum_qingjia.setText(""+(Integer.parseInt(LDL_PlusNum_qingjia.getText().toString())-1));
						}
					});

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
							//从数据库中获取该名学生的签到信息
							int[] old_atten_infor = new int[3];
							old_atten_infor = SQLiteManager.query_all(StaticValue.MY_TABLE_NAME,str_xuehao);




							//出席情况
							int change_chuxi = Integer.parseInt(LDL_PlusNum_chuxi.getText().toString());
							int_chuxi = old_atten_infor[0]+change_chuxi;

							//缺席情况
							int change_quexi = Integer.parseInt(LDL_PlusNum_quexi.getText().toString());
							int_quexi = old_atten_infor[1]+change_quexi;

							//请假情况
							int change_qingjia = Integer.parseInt(LDL_PlusNum_qingjia.getText().toString());
							int_qingjia = old_atten_infor[2]+change_qingjia;


							Timestamp now = new Timestamp(System.currentTimeMillis());//获取系统当前时间
							//更新数据库中对应学生的数据
							SQLiteManager.updateDataInNamelist(StaticValue.MY_TABLE_NAME, str_name, str_xuehao, int_chuxi, int_quexi, int_qingjia, now);
							//刷新列表数据
							Load_listview();

						}


					});
					alertdialog.show();
					return false;
				}

			});
		}

	}


	/**
	 * 物理按键返回主界面
	 * @param keyCode
	 * @param event
     * @return
     */
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


	/**
	 * 向列表中加载数据
	 */
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

	/**
	 * 回到主界面按钮监听器
	 */
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
