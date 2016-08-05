package com.example.compaq.nfc_teacher;

/**
 * Created by Compaq on 2016/6/8.
 */

import java.io.File;
import java.io.IOException;


import java.nio.channels.WritableByteChannel;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    //导入Excel文件
    @SuppressWarnings("static-access")
    public static void myExcel(Context context,Uri uri) throws Exception{


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
        SQLiteManager helper=new SQLiteManager(context);
        System.out.println("==========="+StaticValue.MY_TABLE_NAME+"============");
        helper.CreateTable(StaticValue.MY_TABLE_NAME);

        //NameListS ervice namelist= new NameListService(this);
        //NameListDbHelper myhelper=namelist.getdbhelper();
        helper.clearall(StaticValue.MY_TABLE_NAME);
        //自定义数据插入时间
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date date11 = df1.parse("2000-1-1 00:00:00.0");
        String time  = df1.format(date11);
        Timestamp now = Timestamp.valueOf(time);
        for(int i=2;i<row_num;i++){
            helper.insertData(StaticValue.MY_TABLE_NAME,
                    sheet.getCell(2, i).getContents(),
                    sheet.getCell(1,i).getContents(), 0, 0, 0,now);
        }
        Toast.makeText(context, "数据成功插入数据库!", Toast.LENGTH_SHORT).show();

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
            myExcel(this,uri);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //System.out.println("回调结果为:"+uri);
        //startActivity(intent);
    }
    //加载名单列表
    public static void load_namelist(Context context,Vector<String> db_list_str,ListView listview)
    {
        //添加适配器
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(context,
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
