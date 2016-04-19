package com.zj.mapall;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.overlayutil.PoiOverlay;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.share.OnGetShareUrlResultListener;
import com.baidu.mapapi.search.share.ShareUrlResult;
import com.baidu.mapapi.search.share.ShareUrlSearch;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * POI���� 1.�ܱ߼��� 2. ��Χ���� 3. ���м��� 4.��ϸ����
 * 
 * @author ys
 *
 */
public class PoiSearchActivity extends Activity implements OnClickListener {

	private MapView mapView;
	private BaiduMap bdMap;
	//
	private PoiSearch poiSearch;
	
	private ShareUrlSearch shareUrlSearch;

	private EditText editCityEt, editSearchKeyEt;

	// ���м���������������ܱ߼�������һ������ ��ť
	private Button citySearchBtn, boundSearchBtn, nearbySearchBtn, nextDataBtn;

	// ��¼��������
	private int type;
	// ��¼ҳ��
	private int page = 1;
	private int totalPage = 0;

	private double latitude = 39.9361752;
	private double longitude = 116.400244;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poi_search);
		init();
	}

	private void init() {
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.showZoomControls(false);
		bdMap = mapView.getMap();
		// ʵ����PoiSearch����
		poiSearch = PoiSearch.newInstance();
		// ���ü���������
		poiSearch.setOnGetPoiSearchResultListener(poiSearchListener);
		editCityEt = (EditText) findViewById(R.id.city);
		editSearchKeyEt = (EditText) findViewById(R.id.searchkey);

		citySearchBtn = (Button) findViewById(R.id.city_search_btn);
		boundSearchBtn = (Button) findViewById(R.id.bound_search_btn);
		nearbySearchBtn = (Button) findViewById(R.id.nearby_search_btn);
		nextDataBtn = (Button) findViewById(R.id.next_data_btn);
		nextDataBtn.setEnabled(false);
		citySearchBtn.setOnClickListener(this);
		boundSearchBtn.setOnClickListener(this);
		nearbySearchBtn.setOnClickListener(this);
		nextDataBtn.setOnClickListener(this);

		editSearchKeyEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				citySearchBtn.setEnabled(true);
				boundSearchBtn.setEnabled(true);
				nearbySearchBtn.setEnabled(true);
				nextDataBtn.setEnabled(false);
				page = 1;
				totalPage = 0;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		
		shareUrlSearch = ShareUrlSearch.newInstance();

	}

	/**
	 * 
	 */
	OnGetPoiSearchResultListener poiSearchListener = new OnGetPoiSearchResultListener() {
		@Override
		public void onGetPoiResult(PoiResult poiResult) {
			if (poiResult == null
					|| poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// û���ҵ��������
				Toast.makeText(PoiSearchActivity.this, "δ�ҵ����",
						Toast.LENGTH_LONG).show();
				return;
			}

			if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {// ���������������
				bdMap.clear();
				MyPoiOverlay poiOverlay = new MyPoiOverlay(bdMap);
				poiOverlay.setData(poiResult);// ����POI����
				bdMap.setOnMarkerClickListener(poiOverlay);
				poiOverlay.addToMap();// �����е�overlay��ӵ���ͼ��
				poiOverlay.zoomToSpan();
				//
				totalPage = poiResult.getTotalPageNum();// ��ȡ�ܷ�ҳ��
				Toast.makeText(
						PoiSearchActivity.this,
						"�ܹ��鵽" + poiResult.getTotalPoiNum() + "����Ȥ��, ��Ϊ"
								+ totalPage + "ҳ", Toast.LENGTH_SHORT).show();

			}
		}

		@Override
		public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
			if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(PoiSearchActivity.this, "��Ǹ��δ�ҵ����",
						Toast.LENGTH_SHORT).show();
			} else {// �������ؽ����ʱ�򣬴˴����Ի�úܶ������Ϣ
				Toast.makeText(
						PoiSearchActivity.this,
						poiDetailResult.getName() + ": "
								+ poiDetailResult.getAddress(),
						Toast.LENGTH_LONG).show();
			}
		}
	};
	
	/**
	 * �̴�����������
	 */
	OnGetShareUrlResultListener shareUrlResultListener = new OnGetShareUrlResultListener() {
		
		@Override
		public void onGetPoiDetailShareUrlResult(ShareUrlResult arg0) {
			
		}
		
		@Override
		public void onGetLocationShareUrlResult(ShareUrlResult arg0) {
			
		}

		@Override
		public void onGetRouteShareUrlResult(ShareUrlResult arg0) {
			// TODO Auto-generated method stub
			
		}
	};

	class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap arg0) {
			super(arg0);
		}

		@Override
		public boolean onPoiClick(int arg0) {
			super.onPoiClick(arg0);
			PoiInfo poiInfo = getPoiResult().getAllPoi().get(arg0);
			poiSearch.searchPoiDetail(new PoiDetailSearchOption()
					.poiUid(poiInfo.uid));
			return true;
		}

	}

	/**
	 * ����������
	 */
	private void citySearch(int page) {
		// ���ü�������
		PoiCitySearchOption citySearchOption = new PoiCitySearchOption();
		citySearchOption.city(editCityEt.getText().toString());// ����
		citySearchOption.keyword(editSearchKeyEt.getText().toString());// �ؼ���
		citySearchOption.pageCapacity(15);// Ĭ��ÿҳ10��
		citySearchOption.pageNum(page);// ��ҳ���
		// �����������
		poiSearch.searchInCity(citySearchOption);
	}

	/**
	 * ��Χ����
	 */
	private void boundSearch(int page) {
		PoiBoundSearchOption boundSearchOption = new PoiBoundSearchOption();
		LatLng southwest = new LatLng(latitude - 0.01, longitude - 0.012);// ����
		LatLng northeast = new LatLng(latitude + 0.01, longitude + 0.012);// ����
		LatLngBounds bounds = new LatLngBounds.Builder().include(southwest)
				.include(northeast).build();// �õ�һ������Χ����
		boundSearchOption.bound(bounds);// ����poi������Χ
		boundSearchOption.keyword(editSearchKeyEt.getText().toString());// �����ؼ���
		boundSearchOption.pageNum(page);
		poiSearch.searchInBound(boundSearchOption);// ����poi��Χ��������
	}

	/**
	 * ��������
	 */
	private void nearbySearch(int page) {
		PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
		nearbySearchOption.location(new LatLng(latitude, longitude));
		nearbySearchOption.keyword(editSearchKeyEt.getText().toString());
		nearbySearchOption.radius(1000);// �����뾶����λ����
		nearbySearchOption.pageNum(page);
		poiSearch.searchNearby(nearbySearchOption);// ���𸽽���������
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.city_search_btn:
			type = 0;
			page = 1;
			citySearchBtn.setEnabled(false);
			boundSearchBtn.setEnabled(true);
			nearbySearchBtn.setEnabled(true);
			nextDataBtn.setEnabled(true);
			bdMap.clear();
			citySearch(page);
			break;
		case R.id.bound_search_btn:
			type = 1;
			page = 1;
			citySearchBtn.setEnabled(true);
			boundSearchBtn.setEnabled(false);
			nearbySearchBtn.setEnabled(true);
			nextDataBtn.setEnabled(true);
			bdMap.clear();
			boundSearch(page);
			break;
		case R.id.nearby_search_btn:
			type = 2;
			page = 1;
			citySearchBtn.setEnabled(true);
			boundSearchBtn.setEnabled(true);
			nearbySearchBtn.setEnabled(false);
			nextDataBtn.setEnabled(true);
			bdMap.clear();
			nearbySearch(page);
			break;
		case R.id.next_data_btn:
			switch (type) {
			case 0:
				if (++page <= totalPage) {
					citySearch(page);
				} else {
					Toast.makeText(PoiSearchActivity.this, "�Ѿ��鵽�����һҳ~",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 1:
				if (++page <= totalPage) {
					boundSearch(page);
				} else {
					Toast.makeText(PoiSearchActivity.this, "�Ѿ��鵽�����һҳ~",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 2:
				if (++page <= totalPage) {
					nearbySearch(page);
				} else {
					Toast.makeText(PoiSearchActivity.this, "�Ѿ��鵽�����һҳ~",
							Toast.LENGTH_SHORT).show();
				}
				break;
			}
			break;
		default:
			break;
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
		poiSearch.destroy();// �ͷ�poi��������
		mapView.onDestroy();
	}

}
