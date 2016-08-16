package com.example.compaq.nfc_teacher;

/**
 * 抽点功能实现Activity
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class ChouDianActivity extends Activity {

    private static final String TAG = "ChouDianActivity";
    private static final int SENSOR_SHAKE = 10;

    //传感器参数获取相关变量
    private SensorManager sensorManager;
    private Vibrator vibrator;

    //拍卡签到用到的变量
    NfcAdapter nfcadapter;
    PendingIntent pendingintent;
    BluetoothDevice bluetoothDevice;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    ProgressDialog file_send_dialog;

    //初始化layout元素
    Button back_button;
    ListView random_namelist;

    //显示随机产生的学生信息列表
    ArrayList<HashMap<String, Object>> listitem = new ArrayList<HashMap<String, Object>>();
    List<String> choudian_xuehao_list = new ArrayList<>();
    List<String> choudian_name_list = new ArrayList<>();
    int choudian_student_num = 0;



    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题
        setContentView(R.layout.choudian_activity_layout);

        //初始化layout
        init_layout();

        //获取重力传感器
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        //设置屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        //获取NFC适配器
        nfcadapter = NfcAdapter.getDefaultAdapter(this);

        //判断设备NFC是否可用
        if(nfcadapter==null){
            Toast.makeText(this,"您的爱机不支持NFC",Toast.LENGTH_LONG).show();
            finish();//退出
        }else {

                if (!nfcadapter.isEnabled()) {
                   Toast.makeText(this, "您的爱机还没开启NFC", Toast.LENGTH_LONG).show();
                   BluetoothTools.openBluetooth();//开启蓝牙
                }

                pendingintent = PendingIntent.getActivity(this, 0,
                        new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

                //判断是否选择点名班级
                if (StaticValue.MY_TABLE_NAME == null) {
                    //弹出框提示
                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(ChouDianActivity.this);
                    alertdialog.setTitle("请选择点名班级");
                    alertdialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // TODO Auto-generated method stub
                            final Vector<String> db_list_str_2 = new Vector<String>();
                            CreateNameList.select_namelist(getPackageName().toString(),
                                    ChouDianActivity.this, db_list_str_2);
                        }

                    });
                    alertdialog.setNegativeButton("取消", null);
                    alertdialog.show();

                }
        }

        //注册接收文件传输成功信息的广播
        IntentFilter intentfilter=new IntentFilter();
        intentfilter.addAction(BluetoothTools.ACTION_FILE_SEND_SUCCESS);
        intentfilter.addAction(BluetoothTools.ACTION_FILE_SEND_PERCENT);
        registerReceiver(receiver, intentfilter);

        //启动文件发送控制服务
        ChouDianActivity.this.startService(new Intent(ChouDianActivity.this,SendFileService.class));

    }

    /**
     * 初始化layout元素
     */
    public void init_layout(){

        back_button = (Button)findViewById(R.id.ChouDian_activity_leftbutton);//返回主界面按钮
        random_namelist = (ListView)findViewById(R.id.ChouDian_activity_listview);//显示随机产生的学生信息列表

        back_button.setOnClickListener(new listener());//给返回按钮添加监听器


    }


    /**
     * 界面按钮总监听器
     */
    class listener implements View.OnClickListener {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            switch (arg0.getId()) {
                case R.id.ChouDian_activity_leftbutton:

                    for(int j=0;j<choudian_xuehao_list.size();j++) {

                        //没有前来拍卡的同学要自动更新其数据库中的缺席属性值
                        int[] p;
                        p = SQLiteManager.query_all(StaticValue.MY_TABLE_NAME,choudian_xuehao_list.get(j));
                        Timestamp now = new Timestamp(System.currentTimeMillis());//获取系统当前时间
                        SQLiteManager.updateDataInNamelist(StaticValue.MY_TABLE_NAME,
                                choudian_name_list.get(j),
                                choudian_xuehao_list.get(j),
                                p[0], p[1]+1, p[2], now
                                );

                    }

                    Intent intent_back = new Intent();
                    intent_back.setClass(ChouDianActivity.this, MainActivity.class);
                    ChouDianActivity.this.startActivity(intent_back);
                    finish();
                    break;
            }

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {// 注册监听器
            sensorManager.registerListener(sensorEventListener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (this.nfcadapter == null)
            return;
        if (!this.nfcadapter.isEnabled()) {
            System.out.println("请在系统设置中先启用NFC功能");
        }
        this.nfcadapter.enableForegroundDispatch(this, pendingintent, null, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sensorManager != null) {// 取消监听器
            sensorManager.unregisterListener(sensorEventListener);
        }
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
    public void onDestroy() {
        // TODO Auto-generated method stub
        unregisterReceiver(receiver);//取消广播
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
     * 接收学生拍卡签到实现函数
     * @param intent
     * @throws UnsupportedEncodingException
     * @throws FormatException
     */
    @SuppressLint("NewApi")
    protected void resolveIntent(Intent intent) throws UnsupportedEncodingException, FormatException {
        // 得到是否TAG触发
        System.out.println("----resolveIntent----");

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))
        {
            Parcelable[] rawMsgs =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msg = (NdefMessage)rawMsgs[0];
            //签到学生的学号
            String result_macaddress = new String(msg.getRecords()[0].getPayload(), "GBK").substring(1);
            //签到学生姓名
            String result_strname=new String(msg.getRecords()[1].getPayload(),"UTF-8");
            //签到学生端设备的蓝牙地址
            String result_strxuehao=new String(msg.getRecords()[2].getPayload(),"UTF-8").substring(1,11);
            //学生编辑的课堂反馈信息
            String result_strreflect_infor=new String(msg.getRecords()[3].getPayload(),"UTF-8");

            //找出拍卡学生在listview中的位置
            for(int k=0;k<choudian_xuehao_list.size();k++){

                //删除已签到的学生信息项
                if(choudian_xuehao_list.get(k).equals(result_strxuehao)){
                    listitem.remove(k);
                    choudian_xuehao_list.remove(k);
                    choudian_name_list.remove(k);
                    choudian_student_num--;
                    break;
                }

            }

            //刷新学生信息列表
            SimpleAdapter listitemadapter = new SimpleAdapter(ChouDianActivity.this,
                    listitem,
                    R.layout.choudian_activity_list_item,
                    new String[]{"choudian_activity_listview_xuehao_item", "choudian_activity_listview_name_item"},
                    new int[]{R.id.choudian_activity_listview_xuehao_item, R.id.choudian_activity_listview_name_item}
            );
            //更新ListView显示内容
            random_namelist.setAdapter(listitemadapter);

            //获取蓝牙地址
            StaticValue.macaddress = result_macaddress;
            //获取远程蓝牙设备
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(result_macaddress);
            if (bluetoothDevice != null) {
                System.out.println("地址是：" + bluetoothDevice.getName());
            }
            //进行蓝牙配对
            try {
                ClsUtils.cancelPairingUserInput(bluetoothDevice.getClass(), bluetoothDevice);
                ClsUtils.setPin(bluetoothDevice.getClass(), bluetoothDevice, "0000");
                ClsUtils.createBond(bluetoothDevice.getClass(), bluetoothDevice);
                System.out.println("配对成功！！");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //判断教师端是否选择下发文件
            if (StaticValue.select_filename != null) {
                //启动发送文件线程
                Thread thead = new sendThread();
                thead.start();

                //文件发送进度条对话框显示
                file_send_dialog = new ProgressDialog(ChouDianActivity.this);
                file_send_dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                file_send_dialog.setTitle("文件发送中");
                file_send_dialog.setCancelable(true);
                file_send_dialog.show();


            } else {
                System.out.println("无文件可发！！");
            }

        }
    }

    /**
     * 重力感应监听
     */
    private SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            // 传感器信息改变时执行该方法
            float[] values = event.values;
            float x = values[0]; // x轴方向的重力加速度，向右为正
            float y = values[1]; // y轴方向的重力加速度，向前为正
            float z = values[2]; // z轴方向的重力加速度，向上为正
            int medumValue = 13;//设定阈值为13
            //判断重力系数是否超过阈值，超出则做出相应动作
            if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
                vibrator.vibrate(200);
                Message msg = new Message();
                msg.what = SENSOR_SHAKE;
                handler.sendMessage(msg);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    /**
     * 动作执行
     */
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SENSOR_SHAKE:

                        Log.i(TAG, "检测到摇晃，执行操作！");
                        //更新学生信息列表内容
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        //随机产生一个0~40的数字
                        int i = (int)(1+Math.random()*40);
                        //检索数据库中该序号学生的信息，并插入列表listitem中
                        map.put("choudian_activity_listview_xuehao_item",SQLiteManager.query_xuehao(StaticValue.MY_TABLE_NAME,i) );
                        choudian_xuehao_list.add(SQLiteManager.query_xuehao(StaticValue.MY_TABLE_NAME,i));
                        map.put("choudian_activity_listview_name_item",SQLiteManager.query_name(StaticValue.MY_TABLE_NAME,i) );
                        choudian_name_list.add(SQLiteManager.query_name(StaticValue.MY_TABLE_NAME,i));
                        choudian_student_num++;
                        listitem.add(map);
                        //生成适配器
                        SimpleAdapter listitemadapter = new SimpleAdapter(ChouDianActivity.this,
                                listitem,
                                R.layout.choudian_activity_list_item,
                                new String[]{"choudian_activity_listview_xuehao_item", "choudian_activity_listview_name_item"},
                                new int[]{R.id.choudian_activity_listview_xuehao_item, R.id.choudian_activity_listview_name_item}
                        );
                        //更新列表
                        random_namelist.setAdapter(listitemadapter);

                    break;
            }
        }

    };


    /**
     * 发送开始传输文件广播的线程
     */
    private class sendThread extends Thread {

        public sendThread(){}

        public void run() {

            TransmitBean transmit = new TransmitBean();
            String path=StaticValue.select_filename;
            String filename=path.substring(path.lastIndexOf("/")+1,path.length());
            transmit.setFilename(filename);
            transmit.setFilepath(path);
            Intent sendDataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_SERVICE);
            sendDataIntent.putExtra(BluetoothTools.DATA, transmit);
            sendBroadcast(sendDataIntent);

        }
    }


    /**
     * 接收文件传输反馈信息的广播
     */
    BroadcastReceiver receiver=new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {

            // TODO Auto-generated method stub
            String action = arg1.getAction();
            if (BluetoothTools.ACTION_FILE_SEND_SUCCESS.equals(action)) {

                //文件传输成功反馈信息
                Toast.makeText(ChouDianActivity.this, "文件发送成功！", Toast.LENGTH_LONG).show();
                file_send_dialog.cancel();//关闭文件传输进度显示窗口

            }else if(BluetoothTools.ACTION_FILE_SEND_PERCENT.equals(action)){

                //文件传输过程反馈信息
                file_send_dialog.setMax(StaticValue.file_send_length);
                file_send_dialog.setProgress(StaticValue.file_send_percent);

            }

        }
    };

    /**
     * 物理按键返回
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        Toast.makeText(getApplicationContext(), "回到主页", Toast.LENGTH_LONG).show();
        Intent intent=new Intent();
        intent.setClass(ChouDianActivity.this,MainActivity.class);
        ChouDianActivity.this.startActivity(intent);
        finish();
        return super.onKeyDown(keyCode, event);
    }

}
