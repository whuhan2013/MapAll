package com.zj.mapall;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption.DrivingPolicy;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRoutePlanOption.TransitPolicy;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * ·�߹滮
 * 
 * @author ys
 * 
 */
public class RoutePlanningActivity extends Activity implements OnClickListener {

	private MapView mapView;
	private BaiduMap bdMap;

	private EditText startEt;
	private EditText endEt;

	private String startPlace;// ��ʼ�ص�
	private String endPlace;// �����ص�

	private Button driveBtn;// �ݳ�
	private Button walkBtn;// ����
	private Button transitBtn;// ���� ��������
	private Button nextLineBtn;

	private Spinner drivingSpinner, transitSpinner;

	private RoutePlanSearch routePlanSearch;// ·���滮�����ӿ�

	private int index = -1;
	private int totalLine = 0;// ��¼ĳ���������ķ�������
	private int drivintResultIndex = 0;// �ݳ�·�߷���index
	private int transitResultIndex = 0;// ����·�߷���index

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_planning);
		init();
	}

	/**
	 * 
	 */
	private void init() {
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.showZoomControls(false);
		bdMap = mapView.getMap();

		startEt = (EditText) findViewById(R.id.start_et);
		endEt = (EditText) findViewById(R.id.end_et);
		driveBtn = (Button) findViewById(R.id.drive_btn);
		transitBtn = (Button) findViewById(R.id.transit_btn);
		walkBtn = (Button) findViewById(R.id.walk_btn);
		nextLineBtn = (Button) findViewById(R.id.nextline_btn);
		nextLineBtn.setEnabled(false);
		driveBtn.setOnClickListener(this);
		transitBtn.setOnClickListener(this);
		walkBtn.setOnClickListener(this);
		nextLineBtn.setOnClickListener(this);

		drivingSpinner = (Spinner) findViewById(R.id.driving_spinner);
		String[] drivingItems = getResources().getStringArray(
				R.array.driving_spinner);
		ArrayAdapter<String> drivingAdapter = new ArrayAdapter<>(this,
				android.R.layout.simple_spinner_item, drivingItems);
		drivingSpinner.setAdapter(drivingAdapter);
		drivingSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (index == 0) {
					drivintResultIndex = 0;
					drivingSearch(drivintResultIndex);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		transitSpinner = (Spinner) findViewById(R.id.transit_spinner);
		String[] transitItems = getResources().getStringArray(
				R.array.transit_spinner);
		ArrayAdapter<String> transitAdapter = new ArrayAdapter<>(this,
				android.R.layout.simple_spinner_item, transitItems);
		transitSpinner.setAdapter(transitAdapter);
		transitSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (index == 1) {
					transitResultIndex = 0;
					transitSearch(transitResultIndex);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		routePlanSearch = RoutePlanSearch.newInstance();
		routePlanSearch
				.setOnGetRoutePlanResultListener(routePlanResultListener);
	}

	/**
	 * ·�߹滮����ص�
	 */
	OnGetRoutePlanResultListener routePlanResultListener = new OnGetRoutePlanResultListener() {

		/**
		 * ����·�߽���ص�
		 */
		@Override
		public void onGetWalkingRouteResult(
				WalkingRouteResult walkingRouteResult) {
			bdMap.clear();
			if (walkingRouteResult == null
					|| walkingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(RoutePlanningActivity.this, "��Ǹ��δ�ҵ����",
						Toast.LENGTH_SHORT).show();
			}
			if (walkingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// TODO
				return;
			}
			if (walkingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
				WalkingRouteOverlay walkingRouteOverlay = new WalkingRouteOverlay(
						bdMap);
				walkingRouteOverlay.setData(walkingRouteResult.getRouteLines()
						.get(drivintResultIndex));
				bdMap.setOnMarkerClickListener(walkingRouteOverlay);
				walkingRouteOverlay.addToMap();
				walkingRouteOverlay.zoomToSpan();
				totalLine = walkingRouteResult.getRouteLines().size();
				Toast.makeText(RoutePlanningActivity.this,
						"����ѯ��" + totalLine + "��������������·", 1000).show();
				if (totalLine > 1) {
					nextLineBtn.setEnabled(true);
				}
			}
		}

		/**
		 * ����·�߽���ص�
		 */
		@Override
		public void onGetTransitRouteResult(
				TransitRouteResult transitRouteResult) {
			bdMap.clear();
			if (transitRouteResult == null
					|| transitRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(RoutePlanningActivity.this, "��Ǹ��δ�ҵ����",
						Toast.LENGTH_SHORT).show();
			}
			if (transitRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// ���յ��;�����ַ����壬ͨ�����½ӿڻ�ȡ�����ѯ��Ϣ
				// drivingRouteResult.getSuggestAddrInfo()
				return;
			}
			if (transitRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
				TransitRouteOverlay transitRouteOverlay = new TransitRouteOverlay(
						bdMap);
				transitRouteOverlay.setData(transitRouteResult.getRouteLines()
						.get(drivintResultIndex));// ����һ���ݳ�·�߷���
				bdMap.setOnMarkerClickListener(transitRouteOverlay);
				transitRouteOverlay.addToMap();
				transitRouteOverlay.zoomToSpan();
				totalLine = transitRouteResult.getRouteLines().size();
				Toast.makeText(RoutePlanningActivity.this,
						"����ѯ��" + totalLine + "��������������·", 1000).show();
				if (totalLine > 1) {
					nextLineBtn.setEnabled(true);
				}
				// ͨ��getTaxiInfo()���Եõ��ܶ���ڴ򳵵���Ϣ
				Toast.makeText(
						RoutePlanningActivity.this,
						"��·�ߴ���·��"
								+ transitRouteResult.getTaxiInfo()
										.getDistance(), 1000).show();
			}
		}

		/**
		 * �ݳ�·�߽���ص� ��ѯ�Ľ�����ܰ��������ݳ�·�߷���
		 */
		@Override
		public void onGetDrivingRouteResult(
				DrivingRouteResult drivingRouteResult) {
			bdMap.clear();
			Log.i("test", "test1");
			if (drivingRouteResult == null
					|| drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(RoutePlanningActivity.this, "��Ǹ��δ�ҵ����",
						Toast.LENGTH_SHORT).show();
			}
			Log.i("test", "test2");
			if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// ���յ��;�����ַ����壬ͨ�����½ӿڻ�ȡ�����ѯ��Ϣ
				// drivingRouteResult.getSuggestAddrInfo()
				return;
			}
			if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
				DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
						bdMap);
				Log.i("test", "test3");
				drivingRouteOverlay.setData(drivingRouteResult.getRouteLines()
						.get(drivintResultIndex));// ����һ���ݳ�·�߷���
				bdMap.setOnMarkerClickListener(drivingRouteOverlay);
				drivingRouteOverlay.addToMap();
				drivingRouteOverlay.zoomToSpan();
				totalLine = drivingRouteResult.getRouteLines().size();
				Toast.makeText(RoutePlanningActivity.this,
						"����ѯ��" + totalLine + "��������������·", 1000).show();
				if (totalLine > 1) {
					nextLineBtn.setEnabled(true);
				}
				
				//Log.i("test",drivingRouteResult.getTaxiInfo().getDistance()+"");
				// ͨ��getTaxiInfo()���Եõ��ܶ���ڴ򳵵���Ϣ
				//Toast.makeText(RoutePlanningActivity.this,"��·�ߴ���·��"+ drivingRouteResult.getTaxiInfo().getDistance(), 1000).show();
			}
		}

		@Override
		public void onGetBikingRouteResult(BikingRouteResult arg0) {
			// TODO Auto-generated method stub
			
			Log.i("test", "test5");
			
		}
	};

	/**
	 * �ݳ���·��ѯ
	 */
	private void drivingSearch(int index) {
		DrivingRoutePlanOption drivingOption = new DrivingRoutePlanOption();
		drivingOption.policy(DrivingPolicy.values()[drivingSpinner
				.getSelectedItemPosition()]);// ���üݳ�·�߲���
		drivingOption.from(PlanNode.withCityNameAndPlaceName("����", startPlace));// �������
		drivingOption.to(PlanNode.withCityNameAndPlaceName("����", endPlace));// �����յ�
		routePlanSearch.drivingSearch(drivingOption);// ����ݳ�·�߹滮
	}

	/**
	 * ����·�߲�ѯ
	 */
	private void transitSearch(int index) {
		TransitRoutePlanOption transitOption = new TransitRoutePlanOption();
		transitOption.city("����");// ���û���·�߹滮���У����յ��еĳ��н��ᱻ����
		transitOption.from(PlanNode.withCityNameAndPlaceName("����", startPlace));
		transitOption.to(PlanNode.withCityNameAndPlaceName("����", endPlace));
		transitOption.policy(TransitPolicy.values()[transitSpinner
				.getSelectedItemPosition()]);// ���û��˲���
		routePlanSearch.transitSearch(transitOption);
	}

	/**
	 * ����·�߲�ѯ
	 */
	private void walkSearch() {
		WalkingRoutePlanOption walkOption = new WalkingRoutePlanOption();
		walkOption.from(PlanNode.withCityNameAndPlaceName("����", startPlace));
		walkOption.to(PlanNode.withCityNameAndPlaceName("����", endPlace));
		routePlanSearch.walkingSearch(walkOption);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.drive_btn:// �ݳ�
			index = 0;
			drivintResultIndex = 0;
			startPlace = startEt.getText().toString();
			endPlace = endEt.getText().toString();
			driveBtn.setEnabled(false);
			transitBtn.setEnabled(true);
			walkBtn.setEnabled(true);
			nextLineBtn.setEnabled(false);
			drivingSearch(drivintResultIndex);
			break;
		case R.id.transit_btn:// ����
			index = 1;
			transitResultIndex = 0;
			startPlace = startEt.getText().toString();
			endPlace = endEt.getText().toString();
			transitBtn.setEnabled(false);
			driveBtn.setEnabled(true);
			walkBtn.setEnabled(true);
			nextLineBtn.setEnabled(false);
			transitSearch(transitResultIndex);
			break;
		case R.id.walk_btn:// ����
			index = 2;
			startPlace = startEt.getText().toString();
			endPlace = endEt.getText().toString();
			walkBtn.setEnabled(false);
			driveBtn.setEnabled(true);
			transitBtn.setEnabled(true);
			nextLineBtn.setEnabled(false);
			walkSearch();
			break;
		case R.id.nextline_btn:// ��һ��
			switch (index) {
			case 0:
				drivingSearch(++drivintResultIndex);
				break;
			case 1:
				transitSearch(transitResultIndex);
				break;
			case 2:

				break;
			}
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
		routePlanSearch.destroy();// �ͷż���ʵ��
		mapView.onDestroy();
	}

}
