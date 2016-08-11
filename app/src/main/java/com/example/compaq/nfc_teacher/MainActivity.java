package com.example.compaq.nfc_teacher;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.tools.ant.Main;

public class MainActivity extends SlidingFragmentActivity  implements View.OnClickListener{

	ImageButton reflect_infor_button;
	ImageButton folder_button;
	ImageButton attendence_button;
	public static final int RESULT_CODE = 1000;    //选择文件   请求码
	public static final String SEND_FILE_NAME = "sendFileName";
	private SlidingMenu menu;
	private Fragment mContent;
	PopupMenu folder_menu=null;
	TextView reflect_infor_menu_title;

	//文件格式选择窗口
	PopupWindow file_choose_window = null;
	Button file_choose_window_close_button;
	Button file_choose_window_text_button;
	Button file_choose_window_ZIP_button;
	Button file_choose_window_music_button;
	Button file_choose_window_video_button;

	//文件列举窗口
	PopupWindow file_list_window = null;
	TextView file_list_window_title;
	Button file_list_window_close_button;
	ListView file_list_window_listview;
	Button file_list_window_left_button;
	Button file_list_window_confirm_button;
	List<String> file_list = new ArrayList<>();

	View contentView = null;



	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题
		setContentView(R.layout.activity_main);
		setBehindContentView(R.layout.sliding_manu);

		if(StaticValue.select_filename==null){//判断是否选择发送文件
			Toast.makeText(MainActivity.this,"请选择下发文件",Toast.LENGTH_SHORT).show();
		}

		FileHelper.mkDir(StaticValue.SDPATH+"/NFC—课堂点名/");//创建新文件夹
		FileHelper.mkDir(StaticValue.SDPATH+"/NFC—课堂点名/待发送文件/");//创建新文件夹


		initSlidingMenu(savedInstanceState);//初始化侧滑菜单

		init_layout();//初始化layout里面的控件

		//实现屏幕常亮
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


	}



	/**
	 * 初始化layout资源
	 */
	public void init_layout(){

		reflect_infor_button = (ImageButton)findViewById(R.id.reflect_infor_button);
		folder_button = (ImageButton)findViewById(R.id.folderbutton);
		reflect_infor_menu_title = (TextView)findViewById(R.id.reflect_infor_menu_title);
		attendence_button = (ImageButton)findViewById(R.id.attendence_button);

		reflect_infor_button.setOnClickListener(new listener());
		folder_button.setOnClickListener(new listener());
		attendence_button.setOnClickListener(new listener());


	}

	/**
	 * 初始化侧边栏
	 */

	private void initSlidingMenu(Bundle savedInstanceState) {
		// 如果保存的状态不为空则得到之前保存的Fragment，否则实例化MyFragment
		if (savedInstanceState != null) {
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		}

		if (mContent == null) {
			mContent = new SampleListFragment();
		}

		// 设置左侧滑动菜单
		setBehindContentView(R.layout.sliding_manu);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new SampleListFragment()).commit();

		// 实例化滑动菜单对象
		SlidingMenu sm = getSlidingMenu();
		// 设置可以左右滑动的菜单
		//sm.setMode(SlidingMenu.LEFT);
		// 设置滑动阴影的宽度
		//sm.setShadowWidthRes(R.dimen.shadow_width);
		// 设置滑动菜单阴影的图像资源
		sm.setShadowDrawable(null);
		// 设置滑动菜单视图的宽度
		WindowManager wm = this.getWindowManager();
		int width = (2*wm.getDefaultDisplay().getWidth())/3;
		sm.setBehindWidth(width);
		// 设置渐入渐出效果的值
		sm.setFadeDegree(0.35f);
		// 设置触摸屏幕的模式,这里设置为全屏
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		// 设置下方视图的在滚动时的缩放比例
		sm.setBehindScrollScale(0.0f);

	}

	/*
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}
    */
	/**
	 * 切换Fragment
	 *
	 * @param
	 **/
    /*
	public void switchConent(Fragment fragment, String title) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		getSlidingMenu().showContent();
		nb.setBarTitle(title);
	}
    */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			default:
				break;
		}
	}


	/**
	 * 显示文件格式选择窗口
	****/

	private void show_file_choose_window(){

		contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.file_choose_window_layout, null);
		//sign_in_window = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		file_choose_window = new PopupWindow(contentView,600, 700);
		file_choose_window.setFocusable(true);

		//初始化layout
		file_choose_window_close_button = (Button)contentView.findViewById(R.id.file_choose_window_close_button);
		file_choose_window_text_button = (Button)contentView.findViewById(R.id.file_choose_window_text_button);
		file_choose_window_ZIP_button = (Button)contentView.findViewById(R.id.file_choose_window_ZIP_button);
		file_choose_window_music_button = (Button)contentView.findViewById(R.id.file_choose_window_music_button);
		file_choose_window_video_button = (Button)contentView.findViewById(R.id.file_choose_window_video_button);

		file_choose_window_close_button.setOnClickListener(new listener());
		file_choose_window_text_button.setOnClickListener(new listener());
		file_choose_window_ZIP_button.setOnClickListener(new listener());
		file_choose_window_music_button.setOnClickListener(new listener());
		file_choose_window_video_button.setOnClickListener(new listener());

		//显示PopupWindow
		View rootview = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
		file_choose_window.showAtLocation(rootview, Gravity.CENTER, 0, 0);

	}

	private void show_file_list_window(String[] file_type){

		file_choose_window.dismiss();

		ArrayList<HashMap<String, Object>> file_list_window_listitem = new ArrayList<HashMap<String, Object>>();
		contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.file_list_window_layout, null);
		file_list_window = new PopupWindow(contentView,600, 700);
		file_list_window.setFocusable(true);

		//初始化layout
		file_list_window_close_button = (Button)contentView.findViewById(R.id.file_list_window_close_button);
		file_list_window_left_button = (Button)contentView.findViewById(R.id.file_list_window_left_button);
		file_list_window_confirm_button = (Button)contentView.findViewById(R.id.file_list_window_confirm_button);
		file_list_window_title = (TextView)contentView.findViewById(R.id.file_list_window_title);
		file_list_window_listview = (ListView)contentView.findViewById(R.id.file_list_window_listview);


		file_list_window_close_button.setOnClickListener(new listener());
		file_list_window_left_button.setOnClickListener(new listener());
		file_list_window_confirm_button.setOnClickListener(new listener());

		//获取文件列表
		file_list = FileHelper.getSpecificTypeOfFile(MainActivity.this,file_type);
		if(file_list!=null){
			FileAdapter file_list_window_adapter = new FileAdapter(MainActivity.this,file_list,R.layout.file_list_window_listview_item);
			file_list_window_listview.setAdapter(file_list_window_adapter);

		}else{
			Toast.makeText(MainActivity.this,"查询文件失败",Toast.LENGTH_SHORT).show();
		}




		//显示PopupWindow
		View rootview = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
		file_list_window.showAtLocation(rootview, Gravity.CENTER, 0, 0);

	}

	class listener implements View.OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId()){
				case R.id.file_choose_window_text_button:
					show_file_list_window(new String[]{".doc",".ppt",".pdf",".xls",".txt",".docx",".pptx",".xlsx"});
                    //Toast.makeText(MainActivity.this,"文档",Toast.LENGTH_SHORT).show();
					break;
				case R.id.file_choose_window_ZIP_button:
					show_file_list_window(new String[]{".rar",".zip",".7z"});
					//Toast.makeText(MainActivity.this,"压缩包",Toast.LENGTH_SHORT).show();
					break;
				case R.id.file_choose_window_music_button:
					show_file_list_window(new String[]{".mp3",".wav",".cda",".wma",".ra",".midi",".ogg",".ape",".flac",".aac"});
					//Toast.makeText(MainActivity.this,"音频",Toast.LENGTH_SHORT).show();
					break;
				case R.id.file_choose_window_video_button:
					show_file_list_window(new String[]{".mp4",".rmvb",".mkv",".avi",".rm",".mov",".asf",".wmv",".3gp",".dvd"});
					//Toast.makeText(MainActivity.this,"视频",Toast.LENGTH_SHORT).show();
					break;
				case R.id.file_choose_window_close_button:
					file_choose_window.dismiss();
					break;
				case R.id.file_list_window_close_button:
					file_list_window.dismiss();
					break;
				case R.id.file_list_window_left_button:
					file_list_window.dismiss();
					show_file_choose_window();
					break;
				case R.id.file_list_window_confirm_button:

					String save_send_file_path = null;//压缩文件存储位置

					//获取Listview点击状态
					HashMap<Integer, Boolean> status_map = FileAdapter.getIsSelected();
					Timestamp now = new Timestamp(System.currentTimeMillis());//获取系统当前时间
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");//定义格式，不显示毫秒
					String str = df.format(now);
					System.out.println(StaticValue.SDPATH+"/NFC—课堂点名/待发送文件/"+str+"/");

					if(FileHelper.mkDir(StaticValue.SDPATH+"/NFC—课堂点名/待发送文件/"+str+"/"))//创建新文件夹
					{
						System.out.println("文件创建成功");

						List<String> send_file_list = new ArrayList<>();//存储要发送的文件路径

						for(Map.Entry<Integer, Boolean> entry : status_map.entrySet()){
							if(entry.getValue()==true){
								//System.out.println(entry.getKey()+" = "+entry.getValue());
								System.out.println("要插入的发送文件路径 = "+file_list.get(entry.getKey()));
								send_file_list.add(file_list.get(entry.getKey()));

							}
						}

						for(int j=0;j<send_file_list.size();j++){
							System.out.println("发送的文件名 = "+send_file_list.get(j));
						}

						save_send_file_path ="/storage/sdcard0/NFC—课堂点名/待发送文件/"+str+"/传输中间文件.zip";

						try {
							//FileHelper.zip(send_file_list,save_send_file_path);
							ZipControl.writeByApacheZipOutputStream(send_file_list,save_send_file_path,"hello");
							//ZipControl.readByApacheZipFile(save_send_file_path,path);
							StaticValue.select_filename = save_send_file_path;
							System.out.println("要发送的文件路径为："+StaticValue.select_filename);
							Toast.makeText(MainActivity.this,"文件选择成功！",Toast.LENGTH_SHORT).show();
							file_list_window.dismiss();
						}catch (IOException e){
							System.out.println("压缩失败！");
							Toast.makeText(MainActivity.this,"文件选择失败！",Toast.LENGTH_SHORT).show();
							file_list_window.dismiss();
						}


					}else{
						System.out.println("文件创建失败");
					}




					break;
				case R.id.reflect_infor_button:
					toggle();
					break;
				case R.id.attendence_button:
					Intent intent_attendence=new Intent();
					intent_attendence.setClass(MainActivity.this,NormalAttendence.class );
					MainActivity.this.startActivity(intent_attendence);
					finish();
					break;
				case R.id.folderbutton:
					//创建popupmenu对象
					folder_menu = new PopupMenu(MainActivity.this,folder_button);
					//加载menu资源
					getMenuInflater().inflate(R.menu.folder_button_item,folder_menu.getMenu());
					//绑定点击事件
					folder_menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){

						public boolean onMenuItemClick(MenuItem item){

							switch (item.getItemId())
							{
								case R.id.open_nfc_item:
									new OpenNFC(MainActivity.this);
									break;
								case R.id.statistic_of_atten_item:
									Intent intent_listview=new Intent();
									intent_listview.setClass(MainActivity.this,ListViewDB.class );
									MainActivity.this.startActivity(intent_listview);
									finish();
									break;
								case R.id.new_namelist_item:
									Toast.makeText(MainActivity.this, "新建名单", Toast.LENGTH_LONG).show();
									//弹出框定义
									AlertDialog.Builder alertdialog=new AlertDialog.Builder(MainActivity.this);
									alertdialog.setTitle("请选择点名文件");
									alertdialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){

										@Override
										public void onClick(DialogInterface arg0, int arg1) {
											// TODO Auto-generated method stub
											Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
											intent.setType("application/*");
											intent.addCategory(Intent.CATEGORY_OPENABLE);
											try{
												startActivityForResult(Intent.createChooser(intent, "请选择点名文件"),1);
											}catch(android.content.ActivityNotFoundException ex){
												Toast.makeText(getApplicationContext(), "请安装文件选择器", Toast.LENGTH_LONG).show();
											}
											catch (NullPointerException e) {
												// TODO: handle exception
												e.printStackTrace();
											}
										}

									});
									alertdialog.setNegativeButton("取消", null);
									alertdialog.show();
									break;
								case R.id.choose_namelist_item:
										//获取listview对象
										final ListView CL_listview;

										final Vector<String> db_list_str_2=new Vector<String>();
										CreateNameList.select_namelist(getPackageName().toString(),MainActivity.this,db_list_str_2);


										//展示文件dialog实现
										AlertDialog.Builder choose_namelist_alertdialog=new AlertDialog.Builder(MainActivity.this);
										LayoutInflater inflater=LayoutInflater.from(MainActivity.this);
										final View dblist_dialog=inflater.inflate(R.layout.chooselist, null);
										choose_namelist_alertdialog.setView(dblist_dialog);
										CL_listview=(ListView)dblist_dialog.findViewById(R.id.chooselist_ListView);

										CreateNameList.load_namelist(MainActivity.this,db_list_str_2,CL_listview);



										//添加选中事件
										CL_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
										{

											@Override
											public boolean onItemLongClick(AdapterView<?> arg0,
																		   View arg1, final int arg2, long arg3) {
												// TODO Auto-generated method stub
												System.out.println("你点击了:"+db_list_str_2.elementAt(arg2));
												AlertDialog.Builder alertdialog_long=new AlertDialog.Builder(MainActivity.this);
												alertdialog_long.setTitle("是否选择导入"+db_list_str_2.elementAt(arg2));
												alertdialog_long.setNegativeButton("删除该班级", new DialogInterface.OnClickListener() {

													@Override
													public void onClick(DialogInterface arg0, int arg1) {
														// TODO Auto-generated method stub
														SQLiteManager myhelper = new SQLiteManager(MainActivity.this);
														myhelper.drop_table(db_list_str_2.elementAt(arg2));
														CreateNameList.select_namelist(getPackageName().toString(),MainActivity.this,db_list_str_2);
														CreateNameList.load_namelist(MainActivity.this,db_list_str_2,CL_listview);
													}
												});
												alertdialog_long.setPositiveButton("确认导入", new DialogInterface.OnClickListener() {

													@Override
													public void onClick(DialogInterface arg0, int arg1) {
														// TODO Auto-generated method stub
														StaticValue.MY_TABLE_NAME=db_list_str_2.elementAt(arg2);
														//更新Slidingmenu中的内容
														getSupportFragmentManager().beginTransaction()
																.replace(R.id.menu_frame, new SampleListFragment()).commit();
														reflect_infor_menu_title.setText(StaticValue.MY_TABLE_NAME+"课堂反馈信息");
														Toast.makeText(MainActivity.this,
																StaticValue.MY_TABLE_NAME ,
																Toast.LENGTH_LONG).show();
														StaticValue.MAX=42;
													}
												});
												alertdialog_long.show();
												return false;
											}

										});


										choose_namelist_alertdialog.setTitle("请从新建记录选择点名班级");
										choose_namelist_alertdialog.setNegativeButton("返回", null);
										choose_namelist_alertdialog.show();
									break;
								case R.id.choose_file_item:
									System.out.println("======选择文件开始======");
									//弹出框定义
									show_file_choose_window();
									/*
									AlertDialog.Builder choose_file_alertdialog=new AlertDialog.Builder(MainActivity.this);
									choose_file_alertdialog.setTitle("请选择要下发的文件");
									choose_file_alertdialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){

										@Override
										public void onClick(DialogInterface arg0, int arg1) {
											// TODO Auto-generated method stub
											try{
												//startActivityForResult(Intent.createChooser(intent, "请选择点名文件"),1);
												//Intent intent_selectfile = new Intent(MainActivity.this, com.example.compaq.nfc_teacher.SelectFileActivity.class);
												//startActivityForResult(intent_selectfile, com.example.compaq.nfc_teacher.SelectFileActivity.RESULT_CODE);
											}catch(android.content.ActivityNotFoundException ex){
												Toast.makeText(getApplicationContext(), "请安装文件选择器", Toast.LENGTH_LONG).show();
											}
											catch (NullPointerException e) {
												// TODO: handle exception
												e.printStackTrace();
											}
										}

									});
									choose_file_alertdialog.setNegativeButton("取消", null);
									choose_file_alertdialog.show();
									*/
									break;
								default:
									break;
							}

							return false;

						}

					});

					folder_menu.show();

					default:
						break;
			}
		}

	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == RESULT_CODE){
			//请求为 "选择文件"
			try {
				//取得选择的文件名
				String sendFileName = data.getStringExtra(SEND_FILE_NAME);
				StaticValue.select_filename=sendFileName;
				System.out.println("选择的文件是："+StaticValue.select_filename);
			} catch (Exception e) {
			}
		}
		Uri uri=data.getData();
		System.out.println("回调结果为:"+uri);
		//Toast.makeText(this,"回调结果为："+uri,Toast.LENGTH_LONG).show();
		//intent.setDataAndType(uri, "application/*");
		try {
			//将数据读取入数据库
			CreateNameList.myExcel(this,uri);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.menu_frame, new SampleListFragment()).commit();
			reflect_infor_menu_title.setText(StaticValue.MY_TABLE_NAME+"课堂反馈信息");
			Toast.makeText(this,StaticValue.MY_TABLE_NAME+"=======",Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		//menu.add(0, 1, 1, R.string.exit);
		//menu.add(0, 2, 2, R.string.about);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		//return super.onCreateOptionsMenu(menu);
		return true;
	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		AlertDialog.Builder alertdialog_long=new AlertDialog.Builder(MainActivity.this);
		alertdialog_long.setTitle("是否退出我们的软件");
		alertdialog_long.setNegativeButton("暂时不用", null);
		alertdialog_long.setPositiveButton("确认退出", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		alertdialog_long.show();
		return super.onKeyDown(keyCode, event);
	}

	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}



}
