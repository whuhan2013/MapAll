package com.zj.mapall;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.ArcOptions;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapDoubleClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * ��Ӹ�����(marker��polygon��text��polyline��dot��circle��arc��ground��)
 * ��ͼ�ĵ����¼���˫���¼���marker����ק�¼�    +  ��������뷴�������
 * 
 * @author ys
 *
 */
public class AddOverlayActivity extends Activity implements OnClickListener {
	// �ٶȵ�ͼ�ؼ�
	private MapView mMapView = null;
	// �ٶȵ�ͼ����
	private BaiduMap bdMap;
	// �����ﰴť
	private Button overlayBtn;
	// marker
	private Marker marker1;
	// �����ʾ�ڼ��������� 1->marker 2->polygon 3->text 4->GroundOverlay(����ͼͼ��) 5->dot
	// 6->circle 7->arc 8->polyline
	private int overlayIndex = 0;
	// ��γ��
	private double latitude = 39.9401752;
	private double longitude = 116.400244;

	// ��ʼ��ȫ�� bitmap ��Ϣ������ʱ��ʱ recycle
	// ����markerͼ��
	BitmapDescriptor bitmap = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_marka);
	// GroundOptions
	BitmapDescriptor bitmap2 = BitmapDescriptorFactory
			.fromResource(R.drawable.csdn_blog);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_overlay);
		mMapView = (MapView) findViewById(R.id.bmapview);

		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		bdMap = mMapView.getMap();
		bdMap.setMapStatus(msu);

		// ��marker��������ӵ���¼�
		bdMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker arg0) {
				if (arg0 == marker1) {
					final LatLng latLng = arg0.getPosition();
					// ����γ��ת������Ļ�ϵĵ�
					// Point point =
					// bdMap.getProjection().toScreenLocation(latLng);
					Toast.makeText(AddOverlayActivity.this, latLng.toString(),
							Toast.LENGTH_SHORT).show();
				}
				return false;
			}
		});

		overlayBtn = (Button) findViewById(R.id.overlay_btn);
		overlayBtn.setOnClickListener(this);

		/**
		 * ��ͼ�����¼�
		 */
		bdMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}

			@Override
			public void onMapClick(LatLng latLng) {
				displayInfoWindow(latLng);
			}
		});

		/**
		 * ��ͼ˫���¼�
		 */
		bdMap.setOnMapDoubleClickListener(new OnMapDoubleClickListener() {
			@Override
			public void onMapDoubleClick(LatLng arg0) {

			}
		});

		/**
		 * Marker��ק�¼�
		 */
		bdMap.setOnMarkerDragListener(new OnMarkerDragListener() {
			@Override
			public void onMarkerDragStart(Marker arg0) {

			}

			@Override
			public void onMarkerDragEnd(Marker arg0) {
				Toast.makeText(
						AddOverlayActivity.this,
						"��ק��������λ�ã�" + arg0.getPosition().latitude + ", "
								+ arg0.getPosition().longitude,
						Toast.LENGTH_LONG).show();
				reverseGeoCode(arg0.getPosition());
			}

			@Override
			public void onMarkerDrag(Marker arg0) {

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.overlay_btn:
			switch (overlayIndex) {
			case 0:
				overlayBtn.setText("��ʾ����θ�����");
				addMarkerOverlay();
				break;
			case 1:
				overlayBtn.setText("��ʾ���ָ�����");
				addPolygonOptions();
				break;
			case 2:
				overlayBtn.setText("��ʾ����ͼͼ�㸲����");
				addTextOptions();
				break;
			case 3:
				overlayBtn.setText("��ʾ���߸�����");
				addGroundOverlayOptions();
				break;
			case 4:
				overlayBtn.setText("��ʾԲ�㸲����");
				addPolylineOptions();
				break;
			case 5:
				overlayBtn.setText("��ʾԲ�����ģ�������");
				addDotOptions();
				break;
			case 6:
				overlayBtn.setText("��ʾ���߸�����");
				addCircleOptions();
				break;
			case 7:
				overlayBtn.setText("��ʾmarker������");
				addArcOptions();
				break;
			}
			overlayIndex = (overlayIndex + 1) % 8;
			break;
		}
	}

	/**
	 * ��ӱ�ע������
	 */
	private void addMarkerOverlay() {
		bdMap.clear();
		// ����marker�����
		LatLng point = new LatLng(latitude, longitude);

		// ����markerOption�������ڵ�ͼ�����marker
		OverlayOptions options = new MarkerOptions()//
				.position(point)// ����marker��λ��
				.icon(bitmap)// ����marker��ͼ��
				.zIndex(9)// �O��marker�����ڌӼ�
				.draggable(true);// ����������ק
		// �ڵ�ͼ�����marker������ʾ
		marker1 = (Marker) bdMap.addOverlay(options);
	}

	/**
	 * ��Ӷ���θ�����
	 */
	private void addPolygonOptions() {
		bdMap.clear();
		// �������ε��������
		LatLng pt1 = new LatLng(latitude + 0.02, longitude);
		LatLng pt2 = new LatLng(latitude, longitude - 0.03);
		LatLng pt3 = new LatLng(latitude - 0.02, longitude - 0.01);
		LatLng pt4 = new LatLng(latitude - 0.02, longitude + 0.01);
		LatLng pt5 = new LatLng(latitude, longitude + 0.03);
		List<LatLng> points = new ArrayList<LatLng>();
		points.add(pt1);
		points.add(pt2);
		points.add(pt3);
		points.add(pt4);
		points.add(pt5);
		//
		PolygonOptions polygonOptions = new PolygonOptions();
		polygonOptions.points(points);
		polygonOptions.fillColor(0xAAFFFF00);
		polygonOptions.stroke(new Stroke(2, 0xAA00FF00));
		bdMap.addOverlay(polygonOptions);
	}

	/**
	 * ������ָ�����
	 */
	private void addTextOptions() {
		bdMap.clear();
		LatLng latLng = new LatLng(latitude, longitude);
		TextOptions textOptions = new TextOptions();
		textOptions.bgColor(0xAAFFFF00) // �O�����ָ��w�ﱳ���ɫ
				.fontSize(28) // ���������С
				.fontColor(0xFFFF00FF)// ����������ɫ
				.text("�������ﰡ��������") // ��������
				.rotate(-30) // �������ֵ���ת�Ƕ�
				.position(latLng);// ����λ��
		bdMap.addOverlay(textOptions);
	}

	/**
	 * ��ӵ���ͼͼ��
	 */
	private void addGroundOverlayOptions() {
		bdMap.clear();
		LatLng southwest = new LatLng(latitude - 0.01, longitude - 0.012);// ����
		LatLng northeast = new LatLng(latitude + 0.01, longitude + 0.012);// ����
		LatLngBounds bounds = new LatLngBounds.Builder().include(southwest)
				.include(northeast).build();// �õ�һ������Χ����

		GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions();
		groundOverlayOptions.image(bitmap2);// ��ʾ��ͼƬ
		groundOverlayOptions.positionFromBounds(bounds);// ��ʾ��λ��
		groundOverlayOptions.transparency(0.7f);// ��ʾ��͸����
		bdMap.addOverlay(groundOverlayOptions);
	}

	/**
	 * ������߸�����
	 */
	private void addPolylineOptions() {
		bdMap.clear();
		// ��
		LatLng pt1 = new LatLng(latitude + 0.01, longitude);
		LatLng pt2 = new LatLng(latitude, longitude - 0.01);
		LatLng pt3 = new LatLng(latitude - 0.01, longitude - 0.01);
		LatLng pt5 = new LatLng(latitude, longitude + 0.01);
		List<LatLng> points = new ArrayList<LatLng>();
		points.add(pt1);
		points.add(pt2);
		points.add(pt3);
		points.add(pt5);
		//
		PolylineOptions polylineOptions = new PolylineOptions();
		polylineOptions.points(points);
		polylineOptions.color(0xFF000000);
		polylineOptions.width(4);// �����߿�
		bdMap.addOverlay(polylineOptions);
	}

	/**
	 * ���Բ�㸲����
	 */
	private void addDotOptions() {
		bdMap.clear();
		DotOptions dotOptions = new DotOptions();
		dotOptions.center(new LatLng(latitude, longitude));// ����Բ������
		dotOptions.color(0XFFfaa755);// ��ɫ
		dotOptions.radius(25);// ���ð뾶
		bdMap.addOverlay(dotOptions);
	}

	/**
	 * ���Բ�����ģ�������
	 */
	private void addCircleOptions() {
		bdMap.clear();
		CircleOptions circleOptions = new CircleOptions();
		circleOptions.center(new LatLng(latitude, longitude));// ����Բ������
		circleOptions.fillColor(0XFFfaa755);// Բ�������ɫ
		circleOptions.radius(150);// ���ð뾶
		circleOptions.stroke(new Stroke(5, 0xAA00FF00));// ���ñ߿�
		bdMap.addOverlay(circleOptions);
	}

	/**
	 * ��ӻ��߸�����
	 */
	private void addArcOptions() {
		bdMap.clear();
		LatLng pt1 = new LatLng(latitude, longitude - 0.01);
		LatLng pt2 = new LatLng(latitude - 0.01, longitude - 0.01);
		LatLng pt3 = new LatLng(latitude, longitude + 0.01);
		ArcOptions arcOptions = new ArcOptions();
		arcOptions.points(pt1, pt2, pt3);// ���û��ߵ���㡢�е㡢�յ�����
		arcOptions.width(5);// �߿�
		arcOptions.color(0xFF000000);
		bdMap.addOverlay(arcOptions);
	}

	/**
	 * ��ʾ�������ڸ�����
	 */
	private void displayInfoWindow(final LatLng latLng) {
		// ����infowindowչʾ��view
		Button btn = new Button(getApplicationContext());
		btn.setBackgroundResource(R.drawable.popup);
		btn.setText("���ҵ���~");
		btn.setTextColor(0xAA000000);
		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
				.fromView(btn);
		// infowindow����¼�
		OnInfoWindowClickListener infoWindowClickListener = new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick() {
				reverseGeoCode(latLng);
				// ����InfoWindow
				bdMap.hideInfoWindow();
			}
		};
		// ����infowindow
		InfoWindow infoWindow = new InfoWindow(bitmapDescriptor, latLng, -47,
				infoWindowClickListener);

		// ��ʾInfoWindow
		bdMap.showInfoWindow(infoWindow);
	}

	/**
	 * ���������õ���ַ��Ϣ
	 */
	private void reverseGeoCode(LatLng latLng) {
		// ��������������ʵ��
		GeoCoder geoCoder = GeoCoder.newInstance();
		//
		OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
			// ����������ѯ����ص�����
			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
				if (result == null
						|| result.error != SearchResult.ERRORNO.NO_ERROR) {
					// û�м�⵽���
					Toast.makeText(AddOverlayActivity.this, "��Ǹ��δ���ҵ����",
							Toast.LENGTH_LONG).show();
				}
				Toast.makeText(AddOverlayActivity.this,
						"λ�ã�" + result.getAddress(), Toast.LENGTH_LONG).show();
			}

			// ��������ѯ����ص�����
			@Override
			public void onGetGeoCodeResult(GeoCodeResult result) {
				if (result == null
						|| result.error != SearchResult.ERRORNO.NO_ERROR) {
					// û�м�⵽���
				}
			}
		};
		// ���õ���������������
		geoCoder.setOnGetGeoCodeResultListener(listener);
		//
		geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
		// �ͷŵ���������ʵ��
		// geoCoder.destroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
		// ����bitmip��Դ
		bitmap.recycle();
		bitmap2.recycle();
	}
}
