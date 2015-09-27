package com.socarmap.ui;

import org.oscim.core.GeoPoint;
import org.oscim.core.MercatorProjection;
import org.oscim.view.MapView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.socarmap.BuildingCustomers;
import com.socarmap.CustomerSearch;
import com.socarmap.DemageActivity;
import com.socarmap.MainActivity;
import com.socarmap.R;
import com.socarmap.helper.ActivityHelper;
import com.socarmap.helper.GeoPointHelper;
import com.socarmap.proxy.beans.CusMeter;
import com.socarmap.proxy.beans.CusShort;
import com.socarmap.proxy.beans.NewBuilding;

public class MyInfoWindow extends InfoWindow {
	private TextView tvContent;
	private ImageButton bMoveLeft;
	private ImageButton bMoveRight;
	private ImageButton bMoveUp;
	private ImageButton bMoveDown;
	private ImageButton bRemove;
	private ImageButton bInfo;

	private MarkersOverlay markersOverlay;
	private MainActivity mainActivity;

	public MyInfoWindow(int layoutResId, MapView mapView) {
		super(layoutResId, mapView);

		tvContent = (TextView) mView.findViewById(R.id.m_iw_text);
		bMoveLeft = (ImageButton) mView.findViewById(R.id.iw_arrow_left);
		bMoveRight = (ImageButton) mView.findViewById(R.id.iw_arrow_right);
		bMoveUp = (ImageButton) mView.findViewById(R.id.iw_arrow_up);
		bMoveDown = (ImageButton) mView.findViewById(R.id.iw_arrow_down);
		bRemove = (ImageButton) mView.findViewById(R.id.iw_remove);
		bInfo = (ImageButton) mView.findViewById(R.id.iw_info);
		tvContent.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				close();

			}
		});
	}

	@Override
	public void onClose() {
	}

	@Override
	public void onOpen(final ExtendedOverlayItem item) {
		if (!(item instanceof MyExtendedOverlayItem)) {
			close();
			return;
		}
		MyExtendedOverlayItem myitem = (MyExtendedOverlayItem) item;
		final CusMeter cusMeter = myitem.getCusMeter();
		final NewBuilding newBuilding = myitem.getNewBuilding();
		if (cusMeter == null && newBuilding == null)
			return;
		if (tvContent != null)
			tvContent.setText(item.getTitle());
		byte zoom = mMapView.getMapPosition().getMapPosition().zoomLevel;
		int diff_pixel = 50;
		GeoPoint ngp0 = new GeoPoint(MercatorProjection.pixelYToLatitude(0,
				zoom), MercatorProjection.pixelXToLongitude(0, zoom));
		GeoPoint ngp3 = new GeoPoint(MercatorProjection.pixelYToLatitude(
				diff_pixel, zoom), MercatorProjection.pixelXToLongitude(
				diff_pixel, zoom));
		final int diff = Math.abs(ngp3.latitudeE6 - ngp0.latitudeE6);
		View.OnClickListener list = new View.OnClickListener() {

			@Override
			public void onClick(View paramView) {
				if (paramView.equals(bRemove)) {
					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case DialogInterface.BUTTON_POSITIVE:

								try {

									if (cusMeter == null)
										mainActivity
												.deleteNewBuilding(newBuilding);
									else
										mainActivity.updateCusMeter(cusMeter,
												CusMeter.ACTION_DELETE);
									markersOverlay
											.removeItem((MyExtendedOverlayItem) item);
									markersOverlay.populateItems();

								} catch (Throwable e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								close();
								break;

							case DialogInterface.BUTTON_NEGATIVE:
								// No button clicked
								break;
							}
						}
					};
					AlertDialog.Builder builder = new AlertDialog.Builder(
							mMapView.getContext());
					builder.setMessage("Are you sure you want to delete?")
							.setPositiveButton("Yes", dialogClickListener)
							.setNegativeButton("No", dialogClickListener)
							.show();
					return;
				}
				if (paramView.equals(bInfo)) {
					if (cusMeter != null) {
						if (cusMeter.getType() == CusMeter.CUSTOMER
								|| cusMeter.getType() == CusMeter.METTER)
							try {
								CustomerSearch.showCustomerDetails(null,
										new CusShort(
												(long) cusMeter.getCusid(), "",
												0L, 0l),
										getView().getContext(), null, -10000);
							} catch (Exception e) {
								ActivityHelper.showAlert(
										getView().getContext(), e);
							}
						if (cusMeter.getType() == CusMeter.DEMAGE) {
							int id = cusMeter.getMeterid();
							Intent i = new Intent(mainActivity,
									DemageActivity.class);
							i.putExtra(DemageActivity.DEMAGE_DESCRIPTION, id);
							mainActivity.startActivity(i);
						}
					} else {
						Intent intent = new Intent(mainActivity,
								BuildingCustomers.class);
						intent.putExtra(BuildingCustomers.BC_REGION_ID,
								(long) newBuilding.getPpcityid());
						intent.putExtra(BuildingCustomers.BC_SUBREGION_ID,
								(long) newBuilding.getPcityid());
						intent.putExtra(BuildingCustomers.BC_BUILDING_ADD_ID,
								newBuilding.getBuilding_add_id());
						intent.putExtra(BuildingCustomers.BC_IS_NEW_BUILDING,
								false);
						intent.putExtra(MapSelectionOverlay.REQUEST_POINT,
								GeoPointHelper.toGeoPoint(newBuilding
										.getLocation()));
						intent.putExtra(MapSelectionOverlay.REQUEST_CODE,
								MapSelectionOverlay.REQUEST_CODE_ADD_BUILDING);
						mainActivity.startActivityForResult(intent,
								MapSelectionOverlay.REQUEST_CODE_ADD_BUILDING);
					}
					return;
				}
				int lonAdd = 0;
				int latAdd = 0;
				if (paramView.equals(bMoveLeft))
					lonAdd = -1 * diff;
				if (paramView.equals(bMoveRight))
					lonAdd = diff;
				if (paramView.equals(bMoveUp))
					latAdd = diff;
				if (paramView.equals(bMoveDown))
					latAdd = -1 * diff;

				GeoPoint gp = item.getPoint();
				GeoPoint oldLocation = new GeoPoint(gp.latitudeE6,
						gp.longitudeE6);

				gp.latitudeE6 += latAdd;
				gp.longitudeE6 += lonAdd;
				try {
					if (cusMeter != null) {
						cusMeter.setLocation(GeoPointHelper.toMGeoPoint(gp));
						mainActivity.updateCusMeter(cusMeter,
								CusMeter.ACTION_UPDATE);
					} else {
						newBuilding.setLocation(GeoPointHelper.toMGeoPoint(gp));
						mainActivity.updateNewBuilding(newBuilding);
					}
					markersOverlay.populateItems();
				} catch (Throwable e) {
					gp.latitudeE6 = oldLocation.latitudeE6;
					gp.longitudeE6 = oldLocation.longitudeE6;
					cusMeter.setLocation(GeoPointHelper.toMGeoPoint(gp));
				}

			}
		};

		bMoveLeft.setOnClickListener(list);
		bMoveRight.setOnClickListener(list);
		bMoveUp.setOnClickListener(list);
		bMoveDown.setOnClickListener(list);
		bRemove.setOnClickListener(list);
		bInfo.setOnClickListener(list);
	}

	public void setMarkersOverlay(MarkersOverlay markersOverlay,
			MainActivity mainActivity) {
		this.markersOverlay = markersOverlay;
		this.mainActivity = mainActivity;
	}
}
