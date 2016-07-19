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

        adapter.add(new SampleItem("新建名单", android.R.drawable.sym_action_email));

        adapter.add(new SampleItem("选择名单", android.R.drawable.sym_action_email));

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
