package com.example.compaq.nfc_teacher;

/**
 * 显示选择文件列表的适配器
 */


import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class FileAdapter extends BaseAdapter{

	private LayoutInflater mLayoutInflater;
	private List<String> mFileList;//文件名字符串数组
	private int mLayoutId;
	private static HashMap<Integer, Boolean> isSelected;// 用来控制CheckBox的选中状况的map对象

	public FileAdapter(Context context, List<String> fileList, int layoutId){
		this.mLayoutInflater = LayoutInflater.from(context);
		this.mFileList = fileList;
		this.mLayoutId = layoutId;
		isSelected = new HashMap<Integer, Boolean>();
		initDate();// 初始化isSelected的数据
	}

	/**
	 * 初始化isSelected的数据
	 */
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
		//从文件路径获得文件名
		String filename = mFileList.get(position).substring(mFileList.get(position).lastIndexOf("/")+1,mFileList.get(position).length());;
		//初始化View对象
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
        //设置列表中ChecBox的监听动作
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

	/**
	 * 返回选中状态map列表
	 * @return
     */
	public static HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

    /**
	 * 设置选中状态map列表
	 * @param isSelected
     */
	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		FileAdapter.isSelected = isSelected;
	}



}
