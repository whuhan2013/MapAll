package com.zj.mapall;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.SnapshotReadyCallback;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * ��ͼ����demo�������˫�������������š���ת�����ӣ� + ��λ
 * 
 * @author ys
 *
 */
public class MapControllActivity extends Activity implements OnClickListener {
	// ��ͼ�ؼ�����
	private MapView mapView;
	// �ٶȵ�ͼ����
	private BaiduMap bdMap;
	// ��γ��
	private double latitude, longitude;
	// ��С
	private Button zoomOutBtn;
	// �Ŵ�
	private Button zoomInBtn;
	// ��ת
	private Button rotateBtn;
	// ����
	private Button overlookBtn;
	// ��ͼ
	private Button screenShotBtn;

	// ����Ƿ��Ѿ��Ŵ���������С����С����
	private boolean isMaxOrMin = false;

	private float maxZoom = 0.0f;
	private float minZoom = 0.0f;
	// ��¼��ǰ��ͼ�����ż���
	private float currentZoom = 0.0f;
	// ������ͼ״̬��Ҫ������״̬
	private MapStatusUpdate msu;
	// �������ɵ�ͼ��Ҫ�����ı仯
	private MapStatusUpdateFactory msuFactory;
	// �����ͼ״̬
	private MapStatus mapStatus;

	// ��ת�Ƕ�
	private float rotateAngle = 0.0f;
	// ���ӽǶ� ��0 ~ -45�㣩
	private float overlookAngle = 0.0f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_controll);
		init();
	}

	/**
	 * 
	 */
	private void init() {
		mapView = (MapView) findViewById(R.id.bd_mapview);
		bdMap = mapView.getMap();

		mapView.showZoomControls(false);// ����ʾĬ�ϵ����ſؼ�
		mapView.showScaleControl(false);// ����ʾĬ�ϱ����߿ؼ�

		maxZoom = bdMap.getMaxZoomLevel();// ��õ�ͼ��������ż���
		minZoom = bdMap.getMinZoomLevel();// ��õ�ͼ����С���ż���

		zoomInBtn = (Button) findViewById(R.id.zoom_in_btn);
		zoomOutBtn = (Button) findViewById(R.id.zoom_out_btn);
		rotateBtn = (Button) findViewById(R.id.rotate_btn);
		overlookBtn = (Button) findViewById(R.id.overlook_btn);
		screenShotBtn = (Button) findViewById(R.id.screen_shot_btn);

		zoomInBtn.setOnClickListener(this);
		zoomOutBtn.setOnClickListener(this);
		rotateBtn.setOnClickListener(this);
		overlookBtn.setOnClickListener(this);
		screenShotBtn.setOnClickListener(this);

		bdMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				// ���õ�ͼ�����ĵ�
				msu = msuFactory.newLatLng(arg0);
				bdMap.animateMapStatus(msu);
				Toast.makeText(MapControllActivity.this,
						"��ͼ���ĵ��ƶ�����" + arg0.toString(), Toast.LENGTH_SHORT)
						.show();
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.zoom_out_btn:// ��С
			msu = msuFactory.zoomOut();
			bdMap.animateMapStatus(msu);
			currentZoom = bdMap.getMapStatus().zoom;
			Toast.makeText(MapControllActivity.this,
					"��ǰ��ͼ�����ż����ǣ�" + currentZoom, Toast.LENGTH_SHORT).show();
			break;
		case R.id.zoom_in_btn:// �Ŵ�
			msu = msuFactory.zoomIn();
			bdMap.animateMapStatus(msu);
			currentZoom = bdMap.getMapStatus().zoom;
			Toast.makeText(MapControllActivity.this,
					"��ǰ��ͼ�����ż����ǣ�" + currentZoom, Toast.LENGTH_SHORT).show();
			break;
		case R.id.rotate_btn:// ��ת
			mapStatus = new MapStatus.Builder(bdMap.getMapStatus()).rotate(
					rotateAngle += 30).build();
			msu = msuFactory.newMapStatus(mapStatus);
			bdMap.animateMapStatus(msu);
			break;
		case R.id.overlook_btn:// ����
			mapStatus = new MapStatus.Builder(bdMap.getMapStatus()).overlook(
					overlookAngle -= 10).build();
			msu = msuFactory.newMapStatus(mapStatus);
			bdMap.animateMapStatus(msu);
			break;
		case R.id.screen_shot_btn:// ��ͼ
			bdMap.snapshot(new SnapshotReadyCallback() {
				@Override
				public void onSnapshotReady(Bitmap bitmap) {
					File file = new File("/mnt/sdcard/test.png");
					FileOutputStream out;
					try {
						out = new FileOutputStream(file);
						if (bitmap
								.compress(Bitmap.CompressFormat.PNG, 100, out)) {
							out.flush();
							out.close();
						}
						Toast.makeText(MapControllActivity.this,
								"��Ļ��ͼ�ɹ���ͼƬ����: " + file.toString(),
								Toast.LENGTH_SHORT).show();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			break;
		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

}
