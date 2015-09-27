package com.spatialite.helpers;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.bonuspack.overlays.ExtendedOverlayItem;
import org.osmdroid.bonuspack.overlays.InfoWindow;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.spatialite.R;
import com.spatialite.utilities.MyExtendedOverlayItem;

public class MyInfoWindow extends InfoWindow {
	private TextView tvContent;
	private ImageButton bMoveLeft;
	private ImageButton bMoveRight;
	private ImageButton bMoveUp;
	private ImageButton bMoveDown;
	private ImageButton bRemove;

	private MarkersOverlay markersOverlay;

	public MyInfoWindow(int layoutResId, MapView mapView) {
		super(layoutResId, mapView);
		tvContent = (TextView) mView.findViewById(R.id.m_iw_text);
		bMoveLeft = (ImageButton) mView.findViewById(R.id.iw_arrow_left);
		bMoveRight = (ImageButton) mView.findViewById(R.id.iw_arrow_right);
		bMoveUp = (ImageButton) mView.findViewById(R.id.iw_arrow_up);
		bMoveDown = (ImageButton) mView.findViewById(R.id.iw_arrow_down);
		bRemove = (ImageButton) mView.findViewById(R.id.iw_remove);
		tvContent.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				close();

			}
		});
	}

	@Override
	public void onClose() {
	}

	public void onOpen(final ExtendedOverlayItem item) {
		if (tvContent != null)
			tvContent.setText(item.getPoint().toString());
		Projection p = mMapView.getProjection();
		IGeoPoint ngp0 = p.fromPixels(0, 0);
		IGeoPoint ngp3 = p.fromPixels(10, 10);
		final int diff = Math.abs(ngp3.getLatitudeE6() - ngp0.getLatitudeE6());
		View.OnClickListener list = new View.OnClickListener() {

			public void onClick(View paramView) {
				if (paramView.equals(bRemove)) {
					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								markersOverlay
										.removeItem((MyExtendedOverlayItem) item);
								markersOverlay.populateItems();
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
				gp.setCoordsE6(gp.getLatitudeE6() + latAdd, gp.getLongitudeE6()
						+ lonAdd);
				markersOverlay.populateItems();

			}
		};

		bMoveLeft.setOnClickListener(list);
		bMoveRight.setOnClickListener(list);
		bMoveUp.setOnClickListener(list);
		bMoveDown.setOnClickListener(list);
		bRemove.setOnClickListener(list);

	}

	public void setMarkersOverlay(MarkersOverlay markersOverlay) {
		this.markersOverlay = markersOverlay;
	}
}
