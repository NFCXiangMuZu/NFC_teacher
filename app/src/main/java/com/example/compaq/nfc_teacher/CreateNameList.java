package com.example.compaq.nfc_teacher;

/**
 * 建立班级点名单工具类
 *
 */

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Vector;
import jxl.Sheet;
import jxl.Workbook;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

@SuppressLint("SdCardPath")
public class CreateNameList{

    /**
     * 导入班级学生在exl文件中信息到数据库对应数据表中
     * @param context
     * @param uri 文件URI路径
     * @throws Exception
     */
    @SuppressWarnings("static-access")
    public static void myExcel(Context context,Uri uri) throws Exception{

        //获取文件
        File file=new File(uri.getPath());

        //读取exl文件中数据
        Workbook workbook=Workbook.getWorkbook(file);
        Sheet sheet=workbook.getSheet(0);
        int row_num=sheet.getRows();
        int column_num=sheet.getColumns();
        StaticValue.MAX=40;
        System.out.println("开始读入数据！表的列数为："+column_num+"行数为:"+row_num);
        StaticValue.MY_TABLE_NAME=sheet.getCell(0, 0).getContents();
        //创建数据表
        SQLiteManager helper=new SQLiteManager(context);
        helper.CreateTable(StaticValue.MY_TABLE_NAME);
        helper.clearall(StaticValue.MY_TABLE_NAME);//清除之前在数据库中的同名数据表中数据
        //自定义数据插入时间
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date date11 = df1.parse("2000-1-1 00:00:00.0");
        String time  = df1.format(date11);
        Timestamp now = Timestamp.valueOf(time);
        for(int i=2;i<42;i++){
            helper.insertDataToNamelist(StaticValue.MY_TABLE_NAME,
                    sheet.getCell(2, i).getContents(),
                    sheet.getCell(1,i).getContents(), 0, 0, 0,now);
        }
        Toast.makeText(context, "数据成功插入数据库!", Toast.LENGTH_SHORT).show();

    }


    /**
     * 加载新建名单历史列表
     * @param context
     * @param db_list_str 存储新建名单历史列表的字符串数组
     * @param listview 显示列表的目标ListView
     */
    public static void load_namelist(Context context,Vector<String> db_list_str,ListView listview)
    {
        //添加适配器
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(context,
                R.layout.chooselistview,db_list_str);
        listview.setAdapter(adapter);
    }


    /**
     * 遍历数据库中所有点民班级数据表表名
     * @param package_name 包名
     * @param class_name
     * @param db_list_str_2 存储新建名单历史列表的字符串数组
     */
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

            //遍历表名
            SQLiteManager myhelper = new SQLiteManager(class_name);
            Cursor cursor=myhelper.get_tablename();
            int i=0;
            if(cursor.moveToFirst()){

                do{
                    System.out.println("======遍历======"+cursor.getString(0)+"=======表名======");
                    //db_list_str[i]=cursor.getString(0);
                    if(i>3){
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

        }
    }
}
