package com.example.compaq.nfc_teacher;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class FileUtils {

	public static String getPath(Context context,Uri uri){
		if("context".equalsIgnoreCase(uri.getScheme())){
			String[] projection={"_data"};
			Cursor cursor=null;
			try{
				cursor=context.getContentResolver()
						.query(uri, projection, null, null, null);
				int column_index=cursor.getColumnIndexOrThrow("_data");
				if(cursor.moveToFirst()){
					return cursor.getString(column_index);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		else if("file".equalsIgnoreCase(uri.getScheme())){
			return uri.getPath();
		}
		return null;
	}

	public static String getFilename(String pathandname){
		int start=pathandname.lastIndexOf("/");
		int end=pathandname.lastIndexOf(".");
		if(start!=-1||end!=-1){
			return pathandname.substring(start+1,end);
		}
		else{
			return null;
		}
	}

}
