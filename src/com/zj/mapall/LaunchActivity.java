package com.zj.mapall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * 程序启动引导页，选择不同的功能进入不同的界面
 * 
 * @author ys
 *
 */
public class LaunchActivity extends Activity {

	private ListView listview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		listview = (ListView) findViewById(R.id.activity_listview);
		init();
	}

	/**
	 * 初始化listview列表
	 */
	private void init() {
		final Class[] clazz = { BasisMapActivity.class,AddOverlayActivity.class, MapControllActivity.class
				,LocationActivity.class,PoiSearchActivity.class,BusLineSearchActivity.class,RoutePlanningActivity.class};
		String arr[] = { "地图图层展示" ,"添加覆盖物" ,"地图控制 ","定位","POI检索","公交线路查询","路线规划"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, arr);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				startActivity(new Intent(LaunchActivity.this, clazz[position]));
			}
		});
	}
}
