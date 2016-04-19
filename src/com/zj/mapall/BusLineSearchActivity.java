package com.zj.mapall;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.overlayutil.BusLineOverlay;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * ������·��ѯ
 * 
 * @author ys
 *
 */
public class BusLineSearchActivity extends Activity implements OnClickListener {

	private EditText cityEt;
	private EditText buslineEt;
	private Button searchBtn;
	private Button nextlineBtn;

	private MapView mapView;
	private BaiduMap bdMap;

	private String city;// ����
	private String busline;// ����·��
	private List<String> buslineIdList;// �洢������·��uid
	private int buslineIndex = 0;// ��ǵڼ���·��

	private PoiSearch poiSearch;
	private BusLineSearch busLineSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_busline_search);
		init();
	}

	/**
	 * ��ʼ������
	 */
	private void init() {
		mapView = (MapView) findViewById(R.id.mapview);
		bdMap = mapView.getMap();

		cityEt = (EditText) findViewById(R.id.city_et);
		buslineEt = (EditText) findViewById(R.id.searchkey_et);
		searchBtn = (Button) findViewById(R.id.busline_search_btn);
		nextlineBtn = (Button) findViewById(R.id.nextline_btn);
		searchBtn.setOnClickListener(this);
		nextlineBtn.setOnClickListener(this);

		buslineIdList = new ArrayList<String>();

		poiSearch = PoiSearch.newInstance();
		poiSearch.setOnGetPoiSearchResultListener(poiSearchResultListener);

		busLineSearch = BusLineSearch.newInstance();
		busLineSearch
				.setOnGetBusLineSearchResultListener(busLineSearchResultListener);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.busline_search_btn:
			city = cityEt.getText().toString();
			busline = buslineEt.getText().toString();
			poiSearch.searchInCity((new PoiCitySearchOption()).city(city)
					.keyword(busline));
			break;
		case R.id.nextline_btn:
			searchBusline();
			break;
		}
	}

	private void searchBusline() {
		if (buslineIndex >= buslineIdList.size()) {
			buslineIndex = 0;
		}
		if (buslineIndex >= 0 && buslineIndex < buslineIdList.size()
				&& buslineIdList.size() > 0) {
			boolean flag = busLineSearch
					.searchBusLine((new BusLineSearchOption().city(city)
							.uid(buslineIdList.get(buslineIndex))));
			if (flag) {
				Toast.makeText(BusLineSearchActivity.this, "�����ɹ�~", 1000)
						.show();
			} else {
				Toast.makeText(BusLineSearchActivity.this, "����ʧ��~", 1000)
						.show();
			}
			buslineIndex++;
		}
	}

	/**
	 * POI�������������
	 */
	OnGetPoiSearchResultListener poiSearchResultListener = new OnGetPoiSearchResultListener() {
		@Override
		public void onGetPoiResult(PoiResult poiResult) {
			if (poiResult == null
					|| poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// û���ҵ��������
				Toast.makeText(BusLineSearchActivity.this, "δ�ҵ����",
						Toast.LENGTH_LONG).show();
				return;
			}

			if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {// ���������������
				// ��������poi���ҵ�����Ϊ������·��poi
				buslineIdList.clear();
				for (PoiInfo poi : poiResult.getAllPoi()) {
					if (poi.type == PoiInfo.POITYPE.BUS_LINE
							|| poi.type == PoiInfo.POITYPE.SUBWAY_LINE) {
						buslineIdList.add(poi.uid);
					}
				}
				searchBusline();
			}
		}

		@Override
		public void onGetPoiDetailResult(PoiDetailResult arg0) {

		}
	};

	/**
	 * ������Ϣ��ѯ���������
	 */
	OnGetBusLineSearchResultListener busLineSearchResultListener = new OnGetBusLineSearchResultListener() {

		@Override
		public void onGetBusLineResult(BusLineResult busLineResult) {
			if (busLineResult.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(BusLineSearchActivity.this, "��Ǹ��δ�ҵ����",
						Toast.LENGTH_SHORT).show();
			} else {
				bdMap.clear();
				BusLineOverlay overlay = new MyBuslineOverlay(bdMap);// ������ʾһ��������������Overlay
				overlay.setData(busLineResult);
				overlay.addToMap();// ��overlay��ӵ���ͼ��
				overlay.zoomToSpan();// ���ŵ�ͼ��ʹ����overlay���ں��ʵ���Ұ��Χ��
				bdMap.setOnMarkerClickListener(overlay);
				// ������·����
				Toast.makeText(BusLineSearchActivity.this,
						busLineResult.getBusLineName(), Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	class MyBuslineOverlay extends BusLineOverlay {

		public MyBuslineOverlay(BaiduMap arg0) {
			super(arg0);
		}

		/**
		 * վ�����¼�
		 */
		@Override
		public boolean onBusStationClick(int arg0) {
			MarkerOptions options = (MarkerOptions) getOverlayOptions().get(arg0);
			bdMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(options.getPosition()));
			return true;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		poiSearch.destroy();// �ͷż���������Դ
		busLineSearch.destroy();// �ͷż���������Դ
		mapView.onDestroy();
	}

}
