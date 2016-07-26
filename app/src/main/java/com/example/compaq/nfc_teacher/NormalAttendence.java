package com.example.compaq.nfc_teacher;

        import java.io.IOException;

        import java.io.OutputStream;

        import java.io.UnsupportedEncodingException;
        import java.nio.charset.Charset;
        import java.sql.Date;
        import java.sql.NClob;
        import java.sql.Timestamp;
        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.Arrays;
        import java.util.Calendar;
        import java.util.UUID;
        import java.util.Vector;

        import com.example.compaq.nfc_teacher.BluetoothTools;
        import com.example.compaq.nfc_teacher.FileSend;
        import com.example.compaq.nfc_teacher.SendFileService;
        import com.example.compaq.nfc_teacher.TransmitBean;
        import com.example.compaq.nfc_teacher.FileSend.connectThread;
        import android.annotation.SuppressLint;
        import android.annotation.TargetApi;
        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.PendingIntent;
        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.bluetooth.BluetoothSocket;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.IntentFilter;
        import android.content.DialogInterface.OnClickListener;
        import android.content.Intent;
        import android.net.Uri;
        import android.nfc.FormatException;
        import android.nfc.NdefMessage;
        import android.nfc.NdefRecord;
        import android.nfc.NfcAdapter;
        import android.nfc.NfcAdapter.CreateBeamUrisCallback;
        import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
        import android.nfc.NfcEvent;
        import android.nfc.NfcAdapter.CreateNdefMessageCallback;
        import android.nfc.Tag;
        import android.nfc.tech.MifareUltralight;
        import android.nfc.tech.Ndef;
        import android.nfc.tech.NdefFormatable;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.os.Parcelable;
        import android.text.format.DateFormat;
        import android.text.style.IconMarginSpan;
        import android.view.View;
        import android.view.Window;
        import android.view.WindowManager;
        import android.widget.Button;
        import android.widget.ImageButton;
        import android.widget.Switch;
        import android.widget.TextView;
        import android.widget.Toast;

@SuppressLint("NewApi")
public class NormalAttendence extends Activity
        implements OnNdefPushCompleteCallback{

    public static final int RESULT_CODE = 1000;    //选择文件   请求码
    public static final String SEND_FILE_NAME = "sendFileName";
    int sign=0;
    BluetoothCommunSocket communsocket;
    Thread sendthread;
    Handler handler;
    public String result_macaddress;
    BluetoothSocket mySocket;
    BluetoothDevice mydevice;
    BluetoothDevice bluetoothDevice;
    BluetoothSocket bluetoothSocket;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final UUID MY_UUID = UUID.fromString("db764ac8-4b08-7f25-aafe-59d03c27bae3");
    private OutputStream os;
    private final String NAME = "Bluetooth_Socket";

    protected static final int MESSAGE_SENT = 0;
    NfcAdapter nfcadapter;
    PendingIntent pendingintent;
    ImageButton backTomain_button;
    Button pause_button;
    Button finish_button;
    Button ChouDian_button;
    Button User_icon_button;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题
        setContentView(R.layout.normalattendence);

        init_layout();//初始化layout

        NormalAttendence.this.startService(new Intent(NormalAttendence.this,SendFileService.class));


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

                //android Beam功能使用 开始
                //here a callback is generated
                NdefMessage ndefmeg = null;
                try {
                    ndefmeg = getNoteAsNdef();
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //nfcadapter.setNdefPushMessage(ndefmeg, this);

                if(StaticValue.MY_TABLE_NAME==null){
                    //弹出框定义
                    AlertDialog.Builder alertdialog=new AlertDialog.Builder(NormalAttendence.this);
                    alertdialog.setTitle("请选择点名班级");
                    alertdialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // TODO Auto-generated method stub
                            final Vector<String> db_list_str_2=new Vector<String>();
                            CreateNameList.select_namelist(getPackageName().toString(),
                                    NormalAttendence.this,db_list_str_2);
                        }

                    });
                    alertdialog.setNegativeButton("取消", null);
                    alertdialog.show();
                }

                //注册接收发送成功信息的广播
                IntentFilter intentfilter=new IntentFilter();
                intentfilter.addAction(BluetoothTools.ACTION_FILE_SEND_SUCCESS);
                registerReceiver(receiver, intentfilter);

                //实现屏幕常亮
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            }

        }




    }

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



    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SENT:
                    //Toast.makeText(getApplicationContext(), "传输成功", Toast.LENGTH_LONG).show();
                    //弹出框定义

                    AlertDialog.Builder alertdialog=new AlertDialog.Builder(NormalAttendence.this);
                    if(StaticValue.status==1){
                        alertdialog.setTitle("                 签到完成");
                    }
                    else{
                        alertdialog.setTitle("                 签到未完成");
                    }
                    alertdialog.setPositiveButton("回到主页",new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // TODO Auto-generated method stub
                            Intent intent=new Intent();
                            intent.setClass(NormalAttendence.this,MainActivity.class);
                            NormalAttendence.this.startActivity(intent);
                            finish();
                        }

                    });
                    alertdialog.setNegativeButton("继续签到", null);
                    alertdialog.show();
                    break;
            }
        }
    };

    @Override
    public void onNdefPushComplete(NfcEvent arg0) {
        // TODO Auto-generated method stub
        StaticValue.status=1;
        System.out.println("----------status="+StaticValue.status);
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();

    }

	/*
	@Override
	public NdefMessage createNdefMessage(NfcEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("=====createNdefMessage======");

		//NdefMessage ndefmeg = new NdefMessage(new NdefRecord[]{NdefRecord.createApplicationRecord("com.example.nfc_student")});

		NdefMessage ndefmeg = null;
		try {
			ndefmeg = getNoteAsNdef();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ndefmeg;
	}


	@Override
	public Uri[] createBeamUris(NfcEvent arg0) {
		// TODO Auto-generated method stub
		Uri[] uris = new Uri[1];
		uris[0] = file_path;
		return uris;
	}
	*/



    class listener implements View.OnClickListener{

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            switch(arg0.getId()){
                case R.id.NA_backToMain_button:
                    Intent intent_backTomain=new Intent();
                    intent_backTomain.setClass(NormalAttendence.this,MainActivity.class );
                    NormalAttendence.this.startActivity(intent_backTomain);
                    finish();
                    break;
            }
        }
    }
	/*
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		file_path=data.getData();
		System.out.println("文件路径为："+file_path.toString());
		Toast.makeText(this,"你选择的文件名为："+FileUtils.getFilename(file_path.toString())
				+"\n"+"文件所在的路径为："+file_path.toString(),
				Toast.LENGTH_SHORT).show();
	}*/

    @SuppressWarnings("deprecation")
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
        try {
            nfcadapter.enableForegroundNdefPush(this,getNoteAsNdef());
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    @SuppressLint("NewApi")
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

    protected void resolveIntent(Intent intent) throws UnsupportedEncodingException, FormatException {
        // 得到是否TAG触发
        System.out.println("----resolveIntent----");
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))
        {
            //autowrite(intent);
            // 处理该intent
            //Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Parcelable[] rawMsgs =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if(rawMsgs!=null) {
                // only one message sent during the beam
                NdefMessage msg = (NdefMessage) rawMsgs[0];
                // 获取id数组
                //byte[] bytesId = tag.getId();
                System.out.println("自动写入成功,接收的数据长度为：" + msg.getRecords().length);

                for (int i = 0; i < msg.getRecords().length; i += 4) {
                    result_macaddress = new String(msg.getRecords()[i].getPayload(), "GBK").substring(1);
                    String result_strname = new String(msg.getRecords()[i + 1].getPayload(), "UTF-8");
                    String result_strxuehao = new String(msg.getRecords()[i + 2].getPayload(), "UTF-8").substring(1, 11);
                    String result_strreflect_infor = new String(msg.getRecords()[i + 3].getPayload(), "UTF-8");
                    StaticValue.reflect_information.add(result_strreflect_infor);
                    FileHelper.writeSDFile(result_strreflect_infor, StaticValue.MY_TABLE_NAME + ".txt");
                    //Toast.makeText(this, result_strname+result_strxuehao, Toast.LENGTH_LONG).show();

                    //获取数据库中原本的出勤数据
                    int[] result = new int[3];
                    result = SQLiteManager.query_all(StaticValue.MY_TABLE_NAME, result_strxuehao);

                    //计算时间间隔避免重复签到
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
                        //System.out.println("时间间隔为："+l2+"-"+l1+"="+hours);
                        //System.out.println("===数据库中的日期是："+date);
                        //System.out.println("====现在的日期是："+date2);
                    } catch (ParseException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    //System.out.println("数据库中时间为："+time);


                    int int_chuxi;
                    int int_quexi = result[1];
                    int int_qingjia = result[2];
                    if (hours >= 2) {//默认两个小时内不能重复签到
                        int_chuxi = result[0] + 1;//每一次接触都会让出席记录+1，其他不变

                        SQLiteManager.updateData(StaticValue.MY_TABLE_NAME, result_strname,
                                result_strxuehao, int_chuxi, int_quexi, int_qingjia, now);
                        Toast.makeText(this, result_strname + "信息被修改" , Toast.LENGTH_LONG).show();
                        User_icon_button.setText(result_strname+"\n"+"已签到");
                    } else {
                        Toast.makeText(this, "已经重复签到啦！！！", Toast.LENGTH_LONG).show();
                        int_chuxi = result[0];
                    }


                    System.out.println("+++++++++" + result_macaddress);
                    StaticValue.macaddress = result_macaddress;
                    //Toast.makeText(this, result_macaddress, Toast.LENGTH_LONG).show();
                    bluetoothDevice = bluetoothAdapter.getRemoteDevice(result_macaddress);
                    if (bluetoothDevice != null) {
                        System.out.println("==获取成功==");
                        System.out.println("地址是：" + bluetoothDevice.getName());
                    }

                    try {
                        //ClsUtils.removeBond(bluetoothDevice.getClass(), bluetoothDevice);
                        //System.out.println("取消配对！！");
     	            	    	/*
     	            	    	ClsUtils.setPin(bluetoothDevice.getClass(), bluetoothDevice, "0000"); // 手机和蓝牙采集器配对
     	            	    	ClsUtils.createBond(bluetoothDevice.getClass(), bluetoothDevice);
     	            	    	ClsUtils.cancelPairingUserInput(bluetoothDevice.getClass(), bluetoothDevice);
     	            	    	*/
                        ClsUtils.cancelPairingUserInput(bluetoothDevice.getClass(), bluetoothDevice);
                        ClsUtils.setPin(bluetoothDevice.getClass(), bluetoothDevice, "0000");
                        ClsUtils.createBond(bluetoothDevice.getClass(), bluetoothDevice);
                        System.out.println("配对成功！！");
                        //ClsUtils.pair(result_macaddress, "0000");
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    //System.out.println("地址是："+bluetoothDevice.getName());
                    if (StaticValue.select_filename != null) {
                        Thread thead = new sendThread();
                        thead.start();
                        System.out.println("连接线程启动成功！！");
                    } else {
                        System.out.println("无文件可发！！");
                    }
                }
            }else{
                System.out.println("收到空数据！！！！！！！！！！！");
            }


        }
    }

    //NFC数据格式
    private static enum NFCtype{
        UNKNOWN,TEXT,URI,SMART_POSTER,ABSOLUTE_URI
    }

    private NFCtype getTagType(final NdefMessage msg){
        if(msg==null){
            return null;
        }
        for(NdefRecord record:msg.getRecords()){
            if(record.getTnf()==NdefRecord.TNF_WELL_KNOWN){
                if(Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)){
                    System.out.println("Tag的类型是text");
                    return NFCtype.TEXT;
                }
                if(Arrays.equals(record.getType(), NdefRecord.RTD_URI)){
                    System.out.println("Tag的类型是URI");
                    return NFCtype.URI;
                }
                if(Arrays.equals(record.getType(), NdefRecord.RTD_SMART_POSTER)){
                    System.out.println("Tag的类型是智能海报");
                    return NFCtype.SMART_POSTER;
                }
            }
            else if(record.getTnf()==NdefRecord.TNF_ABSOLUTE_URI){
                System.out.println("Tag的类型是ABSOLUTE_URI");
                return NFCtype.ABSOLUTE_URI;
            }
        }
        return null;
    }

    //读取text格式的tag
    private String getText(final byte[] payload){

        System.out.println("----getText----");

        if(payload==null){
            return null;
        }
        try{
            String textencoding=((payload[0]&0200)==0)?"UTF-8":"UTF-16";
            int languageCodeLength=payload[0]&0077;
            return new String(payload,languageCodeLength+1,payload.length-languageCodeLength-1,textencoding);

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //新建一个record
    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {

        System.out.println("----createRecord----");

        // String nameVcard = "BEGIN:VCARD" +"\n"+ "VERSION:2.1" +"\n" + "N:;" + "\n" +"ORG: PlanAyala"+"\n"+ "TEL;HOME:6302421" +"\n"+ "END:VCARD";
        String nameVcard = text;
        byte[] uriField = nameVcard.getBytes();
        byte[] payload = new byte[uriField.length + 1];              //add 1 for the URI Prefix
        //payload[0] = 0x01;                                      //prefixes http://www. to the URI
        System.arraycopy(uriField, 0, payload, 1, uriField.length);  //appends URI to payload

        NdefRecord nfcRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA, "text/vcard".getBytes(), new byte[0], payload);


        return nfcRecord;
    }

    private NdefMessage getNoteAsNdef() throws UnsupportedEncodingException {
        System.out.println("----getNoteAsNdef----");
        String vcard = "黄明";
        String num="1325114014";
        if (vcard.equals("")) {
            return null;
        } else {
            NdefRecord textRecord = createRecord(vcard);
            NdefRecord numRecord = createRecord(num);
            //System.out.println("要写入的text是："+getText(textRecord.getPayload()));
            return new NdefMessage(new NdefRecord[] {textRecord,numRecord});
        }

    }

    private class sendThread extends Thread {

        public sendThread(){


        }

        public void run() {

            TransmitBean transmit = new TransmitBean();
            String path=StaticValue.select_filename;
            String filename=path.substring(path.lastIndexOf("/")+1,path.length());
            transmit.setFilename(filename);
            transmit.setFilepath(path);
            Intent sendDataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_SERVICE);
            sendDataIntent.putExtra(BluetoothTools.DATA, transmit);
            sendBroadcast(sendDataIntent);

            System.out.println("广播成功！！！！");
            //Toast.makeText(Bluetooth_Text.this, buffer.toString(), Toast.LENGTH_LONG).show();
            //showtext.setText(buffer.toString());
        }
    }

    BroadcastReceiver receiver=new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {

            // TODO Auto-generated method stub

            System.out.println("文件传输成功！！");
            String action = arg1.getAction();
            if (BluetoothTools.ACTION_FILE_SEND_SUCCESS.equals(action)) {
                Toast.makeText(NormalAttendence.this, "文件发送成功了！！！", Toast.LENGTH_LONG).show();
            }

        }
    };


}

