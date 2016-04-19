package com.zj.mapall;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import android.app.Activity;
import android.app.Service;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * ��λ
 * 
 * @author ys
 *
 */
public class LocationActivity extends Activity implements OnClickListener {

	private MapView mapview;
	private BaiduMap bdMap;

	private LocationClient locationClient;
	private BDLocationListener locationListener;
	private BDNotifyListener notifyListener;

	private double longitude;// ����
	private double latitude;// ά��
	private float radius;// ��λ���Ȱ뾶����λ����
	private String addrStr;// ���������
	private String province;// ʡ����Ϣ
	private String city;// ������Ϣ
	private String district;// ������Ϣ
	private float direction;// �ֻ�������Ϣ

	private int locType;

	// ��λ��ť
	private Button locateBtn;
	// ��λģʽ ����ͨ-����-���̣�
	private MyLocationConfiguration.LocationMode currentMode;
	// ��λͼ������
	private BitmapDescriptor currentMarker = null;
	// ��¼�Ƿ��һ�ζ�λ
	private boolean isFirstLoc = true;
	
	//�����豸
	private Vibrator mVibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);

		mapview = (MapView) findViewById(R.id.bd_mapview);
		bdMap = mapview.getMap();
		locateBtn = (Button) findViewById(R.id.locate_btn);
		locateBtn.setOnClickListener(this);
		currentMode = MyLocationConfiguration.LocationMode.NORMAL;
		locateBtn.setText("��ͨ");
		mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
		init();
	}

	/**
	 * 
	 */
	private void init() {
		bdMap.setMyLocationEnabled(true);
		// 1. ��ʼ��LocationClient��
		locationClient = new LocationClient(getApplicationContext());
		// 2. ����LocationListener��
		locationListener = new MyLocationListener();
		// 3. ע���������
		locationClient.registerLocationListener(locationListener);
		// 4. ���ò���
		LocationClientOption locOption = new LocationClientOption();
		locOption.setLocationMode(LocationMode.Hight_Accuracy);// ���ö�λģʽ
		locOption.setCoorType("bd09ll");// ���ö�λ�������
		locOption.setScanSpan(20000);// ���÷���λ����ļ��ʱ��,ms
		locOption.setIsNeedAddress(true);// ���صĶ�λ���������ַ��Ϣ
		locOption.setNeedDeviceDirect(true);// ���÷��ؽ�������ֻ��ķ���

		locationClient.setLocOption(locOption);
		// 5. ע��λ�����Ѽ����¼�
		notifyListener = new MyNotifyListener();
		notifyListener.SetNotifyLocation(longitude, latitude, 3000, "bd09ll");//���ȣ�ά�ȣ���Χ����������
		locationClient.registerNotify(notifyListener);
		// 6. ����/�ر� ��λSDK
		locationClient.start();
		// locationClient.stop();
		// ����λ���첽��ȡ��ǰλ�ã���Ϊ���첽�ģ������������أ�������������
		// ��λ�Ľ����ReceiveListener�ķ���onReceive�����Ĳ����з��ء�
		// ����λSDK�Ӷ�λ�����ж���λ�ú���һ��û�����仯��������һ�ζ�λ�������ʱ���򲻻ᷢ���������󣬶��Ƿ�����һ�εĶ�λ�����
		// ����ֵ��0�����������˶�λ 1��serviceû������ 2��û�м�������
		// 6����������ʱ��̫�̣�ǰ����������λʱ��������С��1000ms��
		/*
		 * if (locationClient != null && locationClient.isStarted()) {
		 * requestResult = locationClient.requestLocation(); } else {
		 * Log.d("LocSDK5", "locClient is null or not started"); }
		 */

	}

	/**
	 * 
	 * @author ys
	 *
	 */
	class MyLocationListener implements BDLocationListener {
		// �첽���صĶ�λ���
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			locType = location.getLocType();
			Toast.makeText(LocationActivity.this, "��ǰ��λ�ķ���ֵ�ǣ�"+locType, Toast.LENGTH_SHORT).show();
			longitude = location.getLongitude();
			latitude = location.getLatitude();
			if (location.hasRadius()) {// �ж��Ƿ��ж�λ���Ȱ뾶
				radius = location.getRadius();
			}
			if (locType == BDLocation.TypeGpsLocation) {//
				Toast.makeText(
						LocationActivity.this,
						"��ǰ�ٶ��ǣ�" + location.getSpeed() + "~~��λʹ������������"
								+ location.getSatelliteNumber(),
						Toast.LENGTH_SHORT).show();
			} else if (locType == BDLocation.TypeNetWorkLocation) {
				addrStr = location.getAddrStr();// ��ȡ���������(���������ĵ�ַ)
				Toast.makeText(LocationActivity.this, addrStr,
						Toast.LENGTH_SHORT).show();
			}
			direction = location.getDirection();// ��ȡ�ֻ����򣬡�0~360�㡿,�ֻ��������泯��Ϊ0��
			province = location.getProvince();// ʡ��
			city = location.getCity();// ����
			district = location.getDistrict();// ����
			Toast.makeText(LocationActivity.this,
					province + "~" + city + "~" + district, Toast.LENGTH_SHORT)
					.show();
			// ���춨λ����
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(radius)//
					.direction(direction)// ����
					.latitude(latitude)//
					.longitude(longitude)//
					.build();
			// ���ö�λ����
			bdMap.setMyLocationData(locData);
			LatLng ll = new LatLng(latitude, longitude);
			MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(ll);
			bdMap.animateMapStatus(msu);

		}
	}

	/**
	 * λ�����Ѽ�����
	 * @author ys
	 *
	 */
	class MyNotifyListener extends BDNotifyListener {
		@Override
		public void onNotify(BDLocation bdLocation, float distance) {
			super.onNotify(bdLocation, distance);
			mVibrator.vibrate(1000);//�������ѵ��趨λ�ø���
	    	Toast.makeText(LocationActivity.this, "������", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.locate_btn:// ��λ
			switch (currentMode) {
			case NORMAL:
				locateBtn.setText("����");
				currentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
				break;
			case FOLLOWING:
				locateBtn.setText("����");
				currentMode = MyLocationConfiguration.LocationMode.COMPASS;
				break;
			case COMPASS:
				locateBtn.setText("��ͨ");
				currentMode = MyLocationConfiguration.LocationMode.NORMAL;
				break;
			}
			bdMap.setMyLocationConfigeration(new MyLocationConfiguration(
					currentMode, true, currentMarker));
			break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mapview.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapview.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapview.onDestroy();
		locationClient.unRegisterLocationListener(locationListener);
		//ȡ��λ������
		locationClient.removeNotifyEvent(notifyListener);
		locationClient.stop();
	}
}
