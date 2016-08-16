package com.example.compaq.nfc_teacher;

/**
 * 正常拍卡签到功能实现
 */

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

@SuppressLint("NewApi")
public class NormalAttendence extends Activity{

    public String result_macaddress;
    BluetoothDevice bluetoothDevice;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    NfcAdapter nfcadapter;
    PendingIntent pendingintent;
    ImageButton backTomain_button;
    Button pause_button;
    Button finish_button;
    Button ChouDian_button;
    Button User_icon_button;

    //进度条对话框
    ProgressDialog file_send_dialog;

    //签到学生信息
    String result_strname;
    String result_strxuehao;
    String result_strreflect_infor;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题
        setContentView(R.layout.normalattendence);

        init_layout();//初始化layout

        //启动接收文件发送信号的服务
        NormalAttendence.this.startService(new Intent(NormalAttendence.this,SendFileService.class));

        //获取NFC适配器
        nfcadapter=NfcAdapter.getDefaultAdapter(this);

        //判断设备NFC是否可用
        if(nfcadapter==null){
            Toast.makeText(this,"您的爱机不支持NFC",Toast.LENGTH_LONG).show();
        }else{

            if(!nfcadapter.isEnabled()){
                Toast.makeText(this,"您的爱机还没开启NFC",Toast.LENGTH_LONG).show();
            }else{

                pendingintent=PendingIntent.getActivity(this, 0,
                        new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                if(StaticValue.MY_TABLE_NAME==null){
                    Toast.makeText(NormalAttendence.this,"请先选择点名班级",Toast.LENGTH_SHORT).show();
                }

                //注册接收文件传输信息的广播
                IntentFilter intentfilter=new IntentFilter();
                intentfilter.addAction(BluetoothTools.ACTION_FILE_SEND_SUCCESS);
                intentfilter.addAction(BluetoothTools.ACTION_FILE_SEND_PERCENT);
                registerReceiver(receiver, intentfilter);

                //实现屏幕常亮
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            }
        }
    }

    /**
     * 初始化layout
     */
    public void init_layout(){

        backTomain_button = (ImageButton)findViewById(R.id.NA_backToMain_button);
        pause_button = (Button)findViewById(R.id.NA_pause_button);
        finish_button = (Button)findViewById(R.id.NA_finish_button);
        ChouDian_button = (Button)findViewById(R.id.NA_ChouDian_button);
        User_icon_button = (Button)findViewById(R.id.NA_user_icon_button);

        backTomain_button.setOnClickListener(new listener());
        pause_button.setOnClickListener(new listener());
        finish_button.setOnClickListener(new listener());
        ChouDian_button.setOnClickListener(new listener());
        User_icon_button.setOnClickListener(new listener());

    }

    /**
     * 按钮监听器集合
     */
    class listener implements View.OnClickListener{

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            switch(arg0.getId()){
                case R.id.NA_backToMain_button://回到主页
                    //删除传输中间文件
                    if(FileHelper.deleteFile(StaticValue.select_filename)){
                        System.out.println("删除成功");
                    }else{
                        System.out.println("删除失败");
                    }

                    StaticValue.select_filename = null;

                    Intent intent_backTomain=new Intent();
                    intent_backTomain.setClass(NormalAttendence.this,MainActivity.class );
                    NormalAttendence.this.startActivity(intent_backTomain);
                    finish();
                    break;
                case R.id.NA_ChouDian_button://开启抽点功能
                    Intent intent_test_sensor=new Intent();
                    intent_test_sensor.setClass(NormalAttendence.this,ChouDianActivity.class );
                    NormalAttendence.this.startActivity(intent_test_sensor);
                    finish();
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        System.out.println("----onResume----");
        if (this.nfcadapter == null)
            return;
        if (!this.nfcadapter.isEnabled()) {
            System.out.println("请在系统设置中先启用NFC功能");
        }
        this.nfcadapter.enableForegroundDispatch(this, pendingintent, null, null);

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        System.out.println("----onPause----");
        if(NfcAdapter.getDefaultAdapter(this)!=null){
            NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        unregisterReceiver(receiver);
        super.onDestroy();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        System.out.println("----onNewIntent----");
        setIntent(intent);
        try {
            resolveIntent(intent);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 处理通过拍卡接收的来自学生端的数据
     * @param intent
     * @throws UnsupportedEncodingException
     * @throws FormatException
     */
    protected void resolveIntent(Intent intent) throws UnsupportedEncodingException, FormatException {
        // 得到是否TAG触发
        System.out.println("----resolveIntent----");
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))
        {
            Parcelable[] rawMsgs =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if(rawMsgs!=null) {
                // only one message sent during the beam
                NdefMessage msg = (NdefMessage) rawMsgs[0];

                //循环处理接收到的一组NdefRecord
                for (int i = 0; i < msg.getRecords().length; i += 4) {
                    result_macaddress = new String(msg.getRecords()[i].getPayload(), "GBK").substring(1);
                    result_strname = new String(msg.getRecords()[i + 1].getPayload(), "UTF-8");
                    result_strxuehao = new String(msg.getRecords()[i + 2].getPayload(), "UTF-8").substring(1, 11);
                    result_strreflect_infor = new String(msg.getRecords()[i + 3].getPayload(), "UTF-8");
                    StaticValue.reflect_information.add(result_strreflect_infor);
                    FileHelper.writeSDFile(result_strreflect_infor, StaticValue.MY_TABLE_NAME + ".txt");

                    //获取数据库中原本的出勤数据
                    int[] result = new int[3];
                    result = SQLiteManager.query_all(StaticValue.MY_TABLE_NAME, result_strxuehao);

                    //计算时间间隔，避免单次课多次拍卡
                    long hours = 3;
                    Timestamp now = new Timestamp(System.currentTimeMillis());//获取系统当前时间
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式，不显示毫秒
                    String str = df.format(now);
                    String time = SQLiteManager.query_time(StaticValue.MY_TABLE_NAME, result_strxuehao);
                    Timestamp SQL_time = Timestamp.valueOf(time);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        java.util.Date date = format.parse(time);
                        java.util.Date date2 = format.parse(str);
                        //计算时间间隔
                        Calendar c1 = Calendar.getInstance();
                        c1.setTime(date);
                        Calendar c2 = Calendar.getInstance();
                        c2.setTime(date2);
                        long l1 = c1.getTimeInMillis();
                        long l2 = c2.getTimeInMillis();
                        hours = Math.abs((l2 - l1) / (3600000));
                    } catch (ParseException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    int int_chuxi;
                    int int_quexi = result[1];
                    int int_qingjia = result[2];
                    if (hours >= 2) {//默认两个小时内不能重复签到
                        int_chuxi = result[0] + 1;//每一次接触都会让出席记录+1，其他不变
                        SQLiteManager.updateDataInNamelist(StaticValue.MY_TABLE_NAME, result_strname,
                                result_strxuehao, int_chuxi, int_quexi, int_qingjia, now);
                        Toast.makeText(this, result_strname + "信息被修改" , Toast.LENGTH_LONG).show();
                        User_icon_button.setText(result_strname+"\n"+"已签到");
                    } else {
                        Toast.makeText(this, "已经重复签到啦！！！", Toast.LENGTH_LONG).show();
                        int_chuxi = result[0];
                    }

                    StaticValue.macaddress = result_macaddress;
                    bluetoothDevice = bluetoothAdapter.getRemoteDevice(result_macaddress);
                    if (bluetoothDevice != null) {
                        System.out.println("==获取成功==");
                        System.out.println("地址是：" + bluetoothDevice.getName());
                    }

                    try {
                        //蓝牙配对连接
                        ClsUtils.cancelPairingUserInput(bluetoothDevice.getClass(), bluetoothDevice);
                        ClsUtils.setPin(bluetoothDevice.getClass(), bluetoothDevice, "0000");
                        ClsUtils.createBond(bluetoothDevice.getClass(), bluetoothDevice);
                        System.out.println("配对成功！！");
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (StaticValue.select_filename != null) {
                        Thread thead = new sendThread();
                        thead.start();
                        //文件传输进度条对话框显示
                        file_send_dialog = new ProgressDialog(NormalAttendence.this);
                        file_send_dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        file_send_dialog.setTitle("文件发送中");
                        file_send_dialog.setCancelable(true);
                        file_send_dialog.show();
                    } else {
                        System.out.println("无文件可发！");
                    }
                }
            }else{
                System.out.println("收到空数据！");
            }


        }
    }

    /**
     * 发送开始文件传输的广播
     */
    private class sendThread extends Thread {

        public sendThread(){}

        public void run() {

            TransmitBean transmit = new TransmitBean();
            String path=StaticValue.select_filename;
            String filename=path.substring(path.lastIndexOf("/")+1,path.length());
            StaticValue.send_filename = filename;
            transmit.setFilename(filename);
            transmit.setFilepath(path);
            Intent sendDataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_SERVICE);
            sendDataIntent.putExtra(BluetoothTools.DATA, transmit);
            sendBroadcast(sendDataIntent);
            SQLiteManager.insertDataTo_FileStatusList(StaticValue.select_filename,0);
            System.out.println("广播成功");

        }
    }

    /**
     * 接收文件传输过程信息的广播
     */
    BroadcastReceiver receiver=new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {

            // TODO Auto-generated method stub
            System.out.println("文件传输成功！！");
            String action = arg1.getAction();
            if (BluetoothTools.ACTION_FILE_SEND_SUCCESS.equals(action)) {
                file_send_dialog.cancel();
                Toast.makeText(NormalAttendence.this, "文件发送成功", Toast.LENGTH_LONG).show();
                System.out.println("发送时间为："+StaticValue.file_send_time);
                //修改数据库中文件传输记录表中对应文件状态
                SQLiteManager.updateDataIn_FileStatusList(StaticValue.select_filename,1);
            }else if(BluetoothTools.ACTION_FILE_SEND_PERCENT.equals(action)){
                file_send_dialog.setMax(StaticValue.file_send_length);
                file_send_dialog.setProgress(StaticValue.file_send_percent);
            }

        }
    };


}

