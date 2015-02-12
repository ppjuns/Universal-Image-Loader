package com.exe.android_universal_image_loader;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Window;

import com.exe.android_universal_image_loader.widget.YWListView;
import com.exe.android_universal_image_loader.widget.YWListView.IXListViewListener;

public class MainActivity extends Activity implements IXListViewListener {
	private Context context = MainActivity.this;
	private Type[] data = {};
	private List<Info> list = new ArrayList<Info>();
	private InfoAdapter adapter;
	private YWListView listview;
	SharedPreferences share;
	boolean isFrist;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		listview = (YWListView) findViewById(R.id.listView1);
		listview.setPullLoadEnable(true);

		listview.setXListViewListener(this);
		adapter = new InfoAdapter(context, R.layout.blog_item, list);
		initdata();
		listview.setAdapter(adapter);

	}

	private void initdata() {

		Info info1 = new Info(
				"http://static.acfun.mm111.net/dotnet/artemis/u/cms/www/201502/041028421kdc.jpg",
				"acfun", "2015年2月12日16:36:11", "来自iPhone客户端", "哈哈哈哈哈哈");
		list.add(info1);

		Info info2 = new Info(
				"http://static.acfun.mm111.net/dotnet/artemis/u/cms/www/201502/12110535u56y.jpg",
				"bibibi", "2015年2月12日16:36:50", "来自iPhone客户端", "asdasdasda");
		list.add(info2);

		share = context.getSharedPreferences("question_data",
				Activity.MODE_PRIVATE);
		Editor edit = share.edit();
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
		String d = format.format(date);
		edit.putString("refresh_time", d);
		edit.commit();

		listview.stopRefresh();
		listview.stopLoadMore();
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		if (share == null)
			share = context.getSharedPreferences("question_data",
					Activity.MODE_PRIVATE);
		listview.setRefreshTime(share.getString("refresh_time", ""));
		initdata();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		initdata();
		adapter.notifyDataSetChanged();
	}
}