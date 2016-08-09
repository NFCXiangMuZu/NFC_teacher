package com.example.compaq.nfc_teacher;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.compaq.nfc_teacher.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class FileAdapter extends BaseAdapter{
	private LayoutInflater mLayoutInflater;
	private List<String> mFileList;
	private int mLayoutId;
	// 用来控制CheckBox的选中状况
	private static HashMap<Integer, Boolean> isSelected;

	public FileAdapter(Context context, List<String> fileList, int layoutId){
		this.mLayoutInflater = LayoutInflater.from(context);
		this.mFileList = fileList;
		this.mLayoutId = layoutId;
		isSelected = new HashMap<Integer, Boolean>();
		initDate();
	}

	// 初始化isSelected的数据
	private void initDate() {
		for (int i = 0; i < mFileList.size(); i++) {
			getIsSelected().put(i, false);
		}
	}



	@Override
	public int getCount() {
		return mFileList.size();
	}

	@Override
	public Object getItem(int position) {
		return mFileList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mFileList.get(position).hashCode();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		MyFile myFile = null;
		String filename = mFileList.get(position).substring(mFileList.get(position).lastIndexOf("/")+1,mFileList.get(position).length());;
		convertView = null;
		if(null == convertView){
			myFile = new MyFile();
			convertView = mLayoutInflater.inflate(mLayoutId, null);
			myFile.mFileCheckbox = (CheckBox) convertView.findViewById(R.id.file_list_window_listview_item_checkbox);
			myFile.mFiletext = (TextView) convertView.findViewById(R.id.file_list_window_listview_item_text);
			myFile.mFiletext.setText(filename);
			convertView.setTag(myFile);
		}else {
			myFile = (MyFile) convertView.getTag();
		}

		myFile.mFileCheckbox.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				if (isSelected.get(position)) {
					isSelected.put(position, false);
					setIsSelected(isSelected);
				} else {
					isSelected.put(position, true);
					setIsSelected(isSelected);
				}

			}
		});
		// 根据isSelected来设置checkbox的选中状况
		myFile.mFileCheckbox.setChecked(getIsSelected().get(position));

		return convertView;
	}

	public static HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		FileAdapter.isSelected = isSelected;
	}

}
