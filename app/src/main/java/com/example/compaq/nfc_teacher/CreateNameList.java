package com.example.compaq.nfc_teacher;

/**
 * Created by Compaq on 2016/6/8.
 */

import java.io.File;
import java.io.IOException;


import java.nio.channels.WritableByteChannel;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jxl.Sheet;
import jxl.Workbook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.SyncStateContract.Helpers;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

@SuppressLint("SdCardPath")
public class CreateNameList extends Activity{


    Button create;
    Button select;
    Button back_main;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set);

		/*//选择是否清空数据库
		AlertDialog.Builder alertdialog=new AlertDialog.Builder(CreateNameList.this);
		alertdialog.setTitle("是否清空清空数据库？");
		alertdialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				NameListDbHelper.clearall(myhelper);
			}
		});
		alertdialog.setNegativeButton("取消", null);
		alertdialog.show();
		*/
        create=(Button)findViewById(R.id.create_button);
        select=(Button)findViewById(R.id.select_button);
        //back_main=(Button)findViewById(R.id.back_main);
        create.setOnClickListener(new create_listener());
        select.setOnClickListener(new select_listener());
        //back_main.setOnClickListener(new back_listener());

        //手势识别
		/*GestureOverlayView gestures=(GestureOverlayView)findViewById(R.id.gestures);
		gestures.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {

			@Override
			public void onGesturePerformed(GestureOverlayView arg0, Gesture arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(CreateNameList.this, "手势识别成功", Toast.LENGTH_LONG).show();
			}
		});
		*/
        //final GestureLibrary library=GestureLibraries.fromFile("");
    }

    //导入Excel文件
    @SuppressWarnings("static-access")
    public void myExcel(Uri uri) throws Exception{


        //获取文件
        File file=new File(uri.getPath());

        //读取数据
        Workbook workbook=Workbook.getWorkbook(file);
        Sheet sheet=workbook.getSheet(0);
        int row_num=sheet.getRows();
        int column_num=sheet.getColumns();
        StaticValue.MAX=row_num;
        System.out.println("开始读入数据！表的列数为："+column_num+"行数为:"+row_num);
        System.out.println("本点名表是"+sheet.getCell(0, 0).getContents());
        StaticValue.MY_TABLE_NAME=sheet.getCell(0, 0).getContents();
        //StaticValue.INNERNAMELIST=StaticValue.INNERNAMELIST+sheet.getCell(0, 0).getContents();
        //创建数据库
        System.out.println(StaticValue.MY_TABLE_NAME);
        SQLiteManager helper=new SQLiteManager(this);
        System.out.println("==========="+StaticValue.MY_TABLE_NAME+"============");
        helper.CreateTable(StaticValue.MY_TABLE_NAME);

        //NameListS ervice namelist= new NameListService(this);
        //NameListDbHelper myhelper=namelist.getdbhelper();
        helper.clearall(StaticValue.MY_TABLE_NAME);
        Timestamp now = new Timestamp(System.currentTimeMillis());//获取系统当前时间
        for(int i=2;i<row_num;i++){
            helper.insertData(StaticValue.MY_TABLE_NAME,
                    sheet.getCell(2, i).getContents(),
                    sheet.getCell(1,i).getContents(), 0, 0, 0,now);
        }
        Toast.makeText(this, "数据成功插入数据库!", Toast.LENGTH_SHORT).show();

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        //Intent intent=new Intent("android.intent.action.VIEW");
        //intent.addCategory("android.intent.category.DEFAULT");
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri=data.getData();
        System.out.println("回调结果为:"+uri);
        //intent.setDataAndType(uri, "application/*");
        try {
            //将数据读取入数据库
            myExcel(uri);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //System.out.println("回调结果为:"+uri);
        //startActivity(intent);
    }



    //新建名单的的监听器
    class create_listener implements View.OnClickListener{

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            Toast.makeText(getApplicationContext(), "新建名单", Toast.LENGTH_LONG).show();

            //弹出框定义
            AlertDialog.Builder alertdialog=new AlertDialog.Builder(CreateNameList.this);
            alertdialog.setTitle("请选择点名文件");
            alertdialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    try{
                        startActivityForResult(Intent.createChooser(intent, "请选择点名文件"),1);
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
        }

    }

    //在文件目录中选择名单文件的监听器
    class select_listener implements View.OnClickListener{

        @SuppressWarnings("null")
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            //Toast.makeText(getApplicationContext(), "选择名单", Toast.LENGTH_LONG).show();

            //获取listview对象
            final ListView CL_listview;

            final Vector<String> db_list_str_2=new Vector<String>();
            select_namelist(getPackageName().toString(),CreateNameList.this,db_list_str_2);


            //展示文件dialog实现
            AlertDialog.Builder alertdialog=new AlertDialog.Builder(CreateNameList.this);
            LayoutInflater inflater=LayoutInflater.from(CreateNameList.this);
            final View dblist_dialog=inflater.inflate(R.layout.chooselist, null);
            alertdialog.setView(dblist_dialog);
            CL_listview=(ListView)dblist_dialog.findViewById(R.id.chooselist_ListView);

            load_namelist(db_list_str_2,CL_listview);



            //添加选中事件
            CL_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
            {

                @Override
                public boolean onItemLongClick(AdapterView<?> arg0,
                                               View arg1, final int arg2, long arg3) {
                    // TODO Auto-generated method stub
                    System.out.println("你点击了:"+db_list_str_2.elementAt(arg2));
                    AlertDialog.Builder alertdialog_long=new AlertDialog.Builder(CreateNameList.this);
                    alertdialog_long.setTitle("是否选择导入"+db_list_str_2.elementAt(arg2));
                    alertdialog_long.setNegativeButton("删除该班级", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // TODO Auto-generated method stub
                            SQLiteManager myhelper = new SQLiteManager(CreateNameList.this);
                            myhelper.drop_table(db_list_str_2.elementAt(arg2));
                            select_namelist(getPackageName().toString(),CreateNameList.this,db_list_str_2);
                            load_namelist(db_list_str_2,CL_listview);
                        }
                    });
                    alertdialog_long.setPositiveButton("确认导入", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // TODO Auto-generated method stub
                            StaticValue.MY_TABLE_NAME=db_list_str_2.elementAt(arg2);
                            Toast.makeText(CreateNameList.this,
                                    StaticValue.MY_TABLE_NAME ,
                                    Toast.LENGTH_LONG).show();
                            StaticValue.MAX=44;
                        }
                    });
                    alertdialog_long.show();
                    return false;
                }

            });


            alertdialog.setTitle("请从新建记录选择点名班级");
            alertdialog.setNegativeButton("返回", null);
            alertdialog.show();

            //Toast.makeText(CreateNameList.this, path, Toast.LENGTH_LONG).show();


        }

    }

    //加载名单列表
    public void load_namelist(Vector<String> db_list_str,ListView listview)
    {
        //添加适配器
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(CreateNameList.this,
                R.layout.chooselistview,db_list_str);
        listview.setAdapter(adapter);
    }


    //选择名单的功能实现函数
    public static void select_namelist(String package_name,final Context class_name,Vector<String> db_list_str_2)
    {

        final String path="/data/data/"+package_name;
        File db=new File(path+"/databases");
        System.out.println("文件读取准备！");
        if(!db.exists()){
            System.out.println("文件读取失败！");
            Toast.makeText(class_name,"文件读取失败！", Toast.LENGTH_LONG).show();
        }
        else{
            File[] db_list=db.listFiles();
            System.out.println(db_list.length);
			/*final String[] db_list_str = new String[db_list.length];

			//初始化String数组
			for(int j=0;j<db_list.length;j++){
				db_list_str[j]=" ";
			}
			*/

            //遍历表名
            SQLiteManager myhelper = new SQLiteManager(class_name);
            Cursor cursor=myhelper.get_tablename();
            int i=0;
            if(cursor.moveToFirst()){

                do{
                    System.out.println("======遍历======"+cursor.getString(0)+"=======表名======");
                    //db_list_str[i]=cursor.getString(0);
                    if(i>1){
                        db_list_str_2.add(cursor.getString(0));
                        try {
                            System.out.println(cursor.getString(0)+".txt");
                            FileHelper.createSDFile(cursor.getString(0)+".txt");

                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    i++;
                }while(cursor.moveToNext());
            }
            System.out.println("遍历结束");

			/*//转化为String数组
			final String[] db_list_str=new String[str.size()];
			for(int j=0;j<str.size();j++){
				db_list_str[j]=str.elementAt(j);
			}
			*/





        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        Toast.makeText(getApplicationContext(), "回到主页", Toast.LENGTH_LONG).show();
        Intent intent=new Intent();
        intent.setClass(CreateNameList.this,MainActivity.class);
        CreateNameList.this.startActivity(intent);
        finish();
        return super.onKeyDown(keyCode, event);
    }



    //回到主页
    class back_listener implements View.OnClickListener{

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            Toast.makeText(getApplicationContext(), "回到主页", Toast.LENGTH_LONG).show();
            Intent intent=new Intent();
            intent.setClass(CreateNameList.this,MainActivity.class);
            CreateNameList.this.startActivity(intent);
            finish();
        }

    }
}
