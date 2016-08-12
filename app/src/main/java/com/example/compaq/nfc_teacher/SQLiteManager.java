package com.example.compaq.nfc_teacher;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteManager {
	private static NameListDbHelper myhelper=null;

	public SQLiteManager(Context context){
		myhelper=new NameListDbHelper(context);


	}

	//创建文件传输相关的两个数据表
	public static void Create_Database_For_Filesend(){

		SQLiteDatabase db = myhelper.getWritableDatabase();

		db.execSQL("create table if not exists 文件分片传输记录表 (" +
				"_id integer primary key,"
				+"filename text,"
				+"fenpian_order integer,"
				+"if_end integer"
				+");"
		);

		db.execSQL("create table if not exists 文件传输记录表 (" +
				"_id integer primary key,"
				+"filename text,"
				+"if_finish integer"
				+");"
		);

	}



	//创建数据库
	public static  void CreateDatabase(String name){

		System.out.println("==============创建数据库"+name+"============");

		SQLiteDatabase db=myhelper.getWritableDatabase();

		db.execSQL("create database "+name+";");

	}

	//创建新表
	public static void CreateTable(String name){

		System.out.println("==============创建新表"+name+"============");

		SQLiteDatabase db=myhelper.getWritableDatabase();
		db.execSQL("create table if not exists "+name+" (" +
				"_id integer primary key,"
				+"name text,"
				+"xuehao text,"
				+"chuxi integer,"
				+"quexi integer,"
				+"qingjia integer,"
				+"time TimeStamp NOT NULL DEFAULT (datetime('now','localtime')));"
		);

		System.out.println("==============创建新表"+name+"结束============");

	}

	//删除一个表
	public static void drop_table(String name)
	{
		System.out.println("==============删除表"+name+"============");
		SQLiteDatabase db=myhelper.getWritableDatabase();
		db.execSQL("drop table "+name+";");
		System.out.println("==============删除表"+name+"结束============");
	}

	//向数据库中文件传输记录表插入数据
	public static void insertDataTo_FileStatusList(String filename,int status){

		//获取数据库对象
		System.out.println("开始向数据库中插入数据！");
		SQLiteDatabase db=myhelper.getWritableDatabase();
		//向表中插入数据
		ContentValues values=new ContentValues();
		values.put("filename",filename);
		values.put("if_finish",status);
		db.insert("文件传输记录表", null, values);
		db.close();

	}

	//向数据库中班级名单表插入数据
	public static void insertDataToNamelist(String TableName,String n,String x,int x1,int x2,int x3,Timestamp now){
		//获取数据库对象
		System.out.println("开始向数据库中插入数据！");
		SQLiteDatabase db=myhelper.getWritableDatabase();
		//向表中插入数据
		//execSQL方法
		//db.execSQL("insert into namelist(name,xuehao) values(n,x)");
		ContentValues values=new ContentValues();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式，不显示毫秒
		values.put("name", n);
		values.put("xuehao", x);
		values.put("chuxi", x1);
		values.put("quexi", x2);
		values.put("qingjia", x3);
		values.put("time", df.format(now));
		db.insert(TableName, null, values);
		Cursor cursor = db.query(TableName, null, null, null, null, null, "_id asc");
		int idindex=cursor.getColumnIndex("_id");
		cursor.moveToLast();
		int id_new=cursor.getInt(idindex);
		System.out.println("新插入的数据的id为："+id_new);
		db.close();
	}

	//更新文件传输记录表的数据
	public static void updateDataIn_FileStatusList(String filename,int status){

		System.out.println("更新数据！");
		//获得数据库对象
		SQLiteDatabase db=myhelper.getWritableDatabase();
		//更新数据
		ContentValues values=new ContentValues();
		values.put("filename", filename);
		values.put("if_finish", status);
		db.update("文件传输记录表", values, "filename = '"+filename+"'", null);
		db.close();

	}

	//更新数据库数据
	public static void updateDataInNamelist(String TableName,String n,String x,int x1,int x2,int x3,Timestamp now){
		System.out.println("更新数据！");
		//获得数据库对象
		SQLiteDatabase db=myhelper.getWritableDatabase();
		//更新数据
		ContentValues values=new ContentValues();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式，不显示毫秒
		values.put("name", n);
		values.put("xuehao", x);
		values.put("chuxi", x1);
		values.put("quexi", x2);
		values.put("qingjia", x3);
		values.put("time", df.format(now));
		db.update(TableName, values, "xuehao="+x, null);
		db.close();
	}

	public static int[] query_all(String TableName,String xuehao){
		System.out.println("==========查询全部情况=============");
		int[] result=new int[3];

		//获得数据库对象
		SQLiteDatabase db = myhelper.getReadableDatabase();
		//查询数据库中数据
		Cursor cursor = db.query(TableName, null, null, null, null, null, "_id asc");
		//获取id的索引
		int idindex=cursor.getColumnIndex("_id");
		//获取name列的索引
		int nameindex=cursor.getColumnIndex("name");
		//获取xuehao列的索引
		int xuehaoindex=cursor.getColumnIndex("xuehao");
		//获取该学生的出席情况
		int chuxiindex=cursor.getColumnIndex("chuxi");
		int quexiindex=cursor.getColumnIndex("quexi");
		int qingjiaindex=cursor.getColumnIndex("qingjia");
		cursor.moveToFirst();
		do{
			System.out.println(cursor.getString(xuehaoindex));
			if(cursor.getString(xuehaoindex).equals(xuehao)){
				System.out.println("学号为："+cursor.getString(xuehaoindex)+"；姓名为"
						+cursor.getString(nameindex)+"出席情况为："
						+cursor.getInt(chuxiindex)+" ; "
						+cursor.getInt(quexiindex)+" ; "
						+cursor.getInt(qingjiaindex)+".");
				result[0]=cursor.getInt(chuxiindex);
				result[1]=cursor.getInt(quexiindex);
				result[2]=cursor.getInt(qingjiaindex);
				System.out.println("结果是："+result[0]+result[1]+result[2]);
				System.out.println("==========notice=============");
				break;
			}

		}while(cursor.moveToNext());
		db.close();
		return result;
	}

	//从数据库中查询数据
	public static String query_xuehao(String TableName,int x){
		System.out.println("查询学号");
		String result="";
		//获得数据库对象
		SQLiteDatabase db = myhelper.getReadableDatabase();
		//查询数据库中数据
		Cursor cursor = db.query(TableName, null, null, null, null, null, "_id asc");
		//获取id的索引
		int idindex=cursor.getColumnIndex("_id");
		//获取name列的索引
		int nameindex=cursor.getColumnIndex("name");
		//获取xuehao列的索引
		int xuehaoindex=cursor.getColumnIndex("xuehao");
		//获取该学生的出席情况
		int chuxiindex=cursor.getColumnIndex("chuxi");
		int quexiindex=cursor.getColumnIndex("quexi");
		int qingjiaindex=cursor.getColumnIndex("qingjia");
		cursor.moveToFirst();
		while(!(cursor.isAfterLast())){
			if(cursor.getInt(idindex)==x){
				System.out.println("学号为："+cursor.getString(xuehaoindex)+"；姓名为"
						+cursor.getString(nameindex)+"出席情况为："
						+cursor.getInt(chuxiindex)+" ; "
						+cursor.getInt(quexiindex)+" ; "
						+cursor.getInt(qingjiaindex)+".");
				break;
			}
			cursor.moveToNext();
		}
		result=cursor.getString(xuehaoindex);
		cursor.close();
		db.close();
		return result;
	}

	public static String query_name(String TableName,int x){
		System.out.println("查询姓名");
		String result="";
		//获得数据库对象
		SQLiteDatabase db = myhelper.getReadableDatabase();
		//查询数据库中数据
		Cursor cursor = db.query(TableName, null, null, null, null, null, "_id asc");
		//获取id的索引
		int idindex=cursor.getColumnIndex("_id");
		//获取name列的索引
		int nameindex=cursor.getColumnIndex("name");
		//获取xuehao列的索引
		int xuehaoindex=cursor.getColumnIndex("xuehao");
		//获取该学生的出席情况
		int chuxiindex=cursor.getColumnIndex("chuxi");
		int quexiindex=cursor.getColumnIndex("quexi");
		int qingjiaindex=cursor.getColumnIndex("qingjia");
		cursor.moveToFirst();
		while(!(cursor.isAfterLast())){
			if(cursor.getInt(idindex)==x){
				System.out.println("学号为："+cursor.getString(xuehaoindex)+"；姓名为"
						+cursor.getString(nameindex)+"出席情况为："
						+cursor.getInt(chuxiindex)+" ; "
						+cursor.getInt(quexiindex)+" ; "
						+cursor.getInt(qingjiaindex)+".");
				break;
			}
			cursor.moveToNext();
		}
		result=cursor.getString(nameindex);
		cursor.close();
		db.close();
		return result;
	}

	public static int query_chuxi(String TableName,int x){
		System.out.println("查询出席情况");
		int result=0;
		//获得数据库对象
		SQLiteDatabase db = myhelper.getReadableDatabase();
		//查询数据库中数据
		Cursor cursor = db.query(TableName, null, null, null, null, null, "_id asc");
		//获取id的索引
		int idindex=cursor.getColumnIndex("_id");
		//获取name列的索引
		int nameindex=cursor.getColumnIndex("name");
		//获取xuehao列的索引
		int xuehaoindex=cursor.getColumnIndex("xuehao");
		//获取该学生的出席情况
		int chuxiindex=cursor.getColumnIndex("chuxi");
		cursor.moveToFirst();
		do{
			if(cursor.getInt(idindex)==x){
				System.out.println("学号为："+cursor.getString(xuehaoindex)+"；姓名为"
						+cursor.getString(nameindex)+"出席情况为："
						+cursor.getInt(chuxiindex)+" 。 "
				);
				break;
			}

		}while(cursor.moveToNext());
		result=cursor.getInt(chuxiindex);
		cursor.close();
		db.close();
		return result;
	}

	public static int query_quexi(String TableName,int x){
		System.out.println("查询缺席情况");
		int result=0;
		//获得数据库对象
		SQLiteDatabase db = myhelper.getReadableDatabase();
		//查询数据库中数据
		Cursor cursor = db.query(TableName, null, null, null, null, null, "_id asc");
		//获取id的索引
		int idindex=cursor.getColumnIndex("_id");
		//获取name列的索引
		int nameindex=cursor.getColumnIndex("name");
		//获取xuehao列的索引
		int xuehaoindex=cursor.getColumnIndex("xuehao");
		//获取该学生的缺席情况
		int quexiindex=cursor.getColumnIndex("quexi");
		cursor.moveToFirst();
		while(!(cursor.isAfterLast())){
			if(cursor.getInt(idindex)==x){
				System.out.println("学号为："+cursor.getString(xuehaoindex)+"；姓名为"
						+cursor.getString(nameindex)+"缺席情况为："
						+cursor.getInt(quexiindex)+" ; ");
				break;
			}
			cursor.moveToNext();
		}
		result=cursor.getInt(quexiindex);
		cursor.close();
		db.close();
		return result;
	}

	public static int query_qingjia(String TableName,int x){
		System.out.println("查询请假情况");
		int result=0;
		//获得数据库对象
		SQLiteDatabase db = myhelper.getReadableDatabase();
		//查询数据库中数据
		Cursor cursor = db.query(TableName, null, null, null, null, null, "_id asc");
		//获取id的索引
		int idindex=cursor.getColumnIndex("_id");
		//获取name列的索引
		int nameindex=cursor.getColumnIndex("name");
		//获取xuehao列的索引
		int xuehaoindex=cursor.getColumnIndex("xuehao");
		//获取该学生的请假情况
		int qingjiaindex=cursor.getColumnIndex("qingjia");
		cursor.moveToFirst();
		while(!(cursor.isAfterLast())){
			if(cursor.getInt(idindex)==x){
				System.out.println("学号为："+cursor.getString(xuehaoindex)+"；姓名为"
						+cursor.getString(nameindex)+"出席情况为："
						+cursor.getInt(qingjiaindex)+".");
				break;
			}
			cursor.moveToNext();
		}
		result=cursor.getInt(qingjiaindex);
		cursor.close();
		db.close();
		return result;
	}

	//从数据库中查询数据
	public static String query_time(String TableName,String x){
		System.out.println("查询时间戳");
		String result="";
		//获得数据库对象
		SQLiteDatabase db = myhelper.getReadableDatabase();
		//查询数据库中数据
		Cursor cursor = db.query(TableName, null, null, null, null, null, "_id asc");
		//获取id的索引
		int idindex=cursor.getColumnIndex("_id");
		//获取name列的索引
		int nameindex=cursor.getColumnIndex("name");
		//获取xuehao列的索引
		int xuehaoindex=cursor.getColumnIndex("xuehao");
		//获取该学生的出席情况
		int chuxiindex=cursor.getColumnIndex("chuxi");
		int quexiindex=cursor.getColumnIndex("quexi");
		int qingjiaindex=cursor.getColumnIndex("qingjia");
		//获取时间戳
		int timeindex=cursor.getColumnIndex("time");
		cursor.moveToFirst();
		while(!(cursor.isAfterLast())){
			if((cursor.getString(xuehaoindex)).equals(x)){
				System.out.println("学号为："+cursor.getString(xuehaoindex)+"；姓名为"
						+cursor.getString(nameindex)+"出席情况为："
						+cursor.getInt(chuxiindex)+" ; "
						+cursor.getInt(quexiindex)+" ; "
						+cursor.getInt(qingjiaindex)+".");
				break;
			}
			cursor.moveToNext();
		}
		result=cursor.getString(timeindex);
		cursor.close();
		db.close();
		return result;
	}

	//从数据库删除所有数据
	public static void clearall(String TableName){
		System.out.println("先清空！");
		//获得数据库对象
		SQLiteDatabase db=myhelper.getWritableDatabase();
		//清空数据库
		db.delete(TableName, null, null);
	}

	//删除单条记录
	public static void del_one(String TableName,String x){
		System.out.println("删除某个学生的记录！");
		//获得数据库对象
		SQLiteDatabase db=myhelper.getWritableDatabase();
		//删除
		db.delete(TableName, "xuehao="+x, null);
		System.out.println("删除成功");
	}

	public List<NameList> getScrolldata(int startresult,int maxresult){
		List<NameList> namelists=new ArrayList<NameList>();
		SQLiteDatabase db=myhelper.getReadableDatabase();
		Cursor cursor=db.rawQuery("select * from "+StaticValue.MY_TABLE_NAME+" limit ?,?", new String[]{String.valueOf(startresult),String.valueOf(maxresult)});
		while(cursor.moveToNext()){
			namelists.add(new NameList(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4),cursor.getInt(0)));

		}
		return namelists;
	}

	public Cursor getCursorScrolldata(int startresult,int maxresult){
		SQLiteDatabase db=myhelper.getReadableDatabase();
		return db.rawQuery("select _id as _id,name,xuehao,chuxi,quexi,qingjia from "+StaticValue.MY_TABLE_NAME+" limit ?,?", new String[]{String.valueOf(startresult),String.valueOf(maxresult)});
	}

	//遍历所有表名
	public Cursor get_tablename() {
		SQLiteDatabase db=myhelper.getReadableDatabase();
		return db.rawQuery("select name from sqlite_master where type='table' order by name;",
				null);
	}

	public Cursor query(String tablename, List<String> parent,
						Map<String, List<String>> map) {
		// TODO Auto-generated method stub
		return null;
	}



}
