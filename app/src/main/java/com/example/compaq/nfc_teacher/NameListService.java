package com.example.compaq.nfc_teacher;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NameListService {
    private static final String TAG="PersonService";
    private static SQLiteManager myhelper;
    private SQLiteDatabase db;
    
    public NameListService(Context contect){
    	myhelper=new SQLiteManager(contect);
    }
    
    public static SQLiteManager getdbhelper(){
    	return myhelper;
    }
    
  
}
