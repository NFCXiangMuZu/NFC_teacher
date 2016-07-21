package com.example.compaq.nfc_teacher;

/**
 * Created by Compaq on 2016/7/18.
 */
import android.content.Context;

import android.os.Bundle;

import android.support.v4.app.ListFragment;

import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import android.widget.ArrayAdapter;

import android.widget.ImageView;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**

 * @author yangyu

 *  功能描述：列表Fragment，用来显示列表视图

 */

public class SampleListFragment extends ListFragment {



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.list, null);

    }



    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        SampleAdapter adapter = new SampleAdapter(getActivity());

        if(StaticValue.MY_TABLE_NAME!=null) {
            List<String> list = new ArrayList<>();
            list = FileHelper.readSDFile(StaticValue.MY_TABLE_NAME + ".txt");
            int width = list.size();

            for (int i = 0; i < width; i++) {
                System.out.println(list.get(i) + "===========");
                adapter.add(new SampleItem(list.get(i), android.R.drawable.sym_action_email));

            }
        }else
        {
            System.out.println("没选择班级！！");
            Toast.makeText(getActivity(),"没选择班级！！",Toast.LENGTH_LONG).show();
        }
        setListAdapter(adapter);

    }



    private class SampleItem {

        public String tag;

        public int iconRes;

        public SampleItem(String tag, int iconRes) {

            this.tag = tag;

            this.iconRes = iconRes;

        }

    }



    public class SampleAdapter extends ArrayAdapter<SampleItem> {



        public SampleAdapter(Context context) {

            super(context, 0);

        }



        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);

            }

            ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);

            icon.setImageResource(getItem(position).iconRes);

            TextView title = (TextView) convertView.findViewById(R.id.row_title);

            title.setText(getItem(position).tag);



            return convertView;

        }



    }

}
