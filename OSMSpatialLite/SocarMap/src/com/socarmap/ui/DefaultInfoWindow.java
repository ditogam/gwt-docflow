package com.socarmap.ui;

import org.oscim.view.MapView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Default implementation of InfoWindow. It handles a text and a description. It
 * also handles optionally a sub-description and an image. Clicking on the
 * bubble will close it.
 * 
 * @author M.Kergall
 */
public class DefaultInfoWindow extends InfoWindow {

	// resource ids
	private static int mTitleId = 0, mDescriptionId = 0, mSubDescriptionId = 0,
			mImageId = 0;

	private static void setResIds(Context context) {
		// get application package name
		String packageName = context.getPackageName();
		mTitleId = context.getResources().getIdentifier("id/bubble_title",
				null, packageName);

		mDescriptionId = context.getResources().getIdentifier(
				"id/bubble_description", null, packageName);

		mSubDescriptionId = context.getResources().getIdentifier(
				"id/bubble_subdescription", null, packageName);

		mImageId = context.getResources().getIdentifier("id/bubble_image",
				null, packageName);

		if (mTitleId == 0 || mDescriptionId == 0) {

		}
	}

	public DefaultInfoWindow(int layoutResId, MapView mapView) {
		super(layoutResId, mapView);

		if (mTitleId == 0)
			setResIds(mapView.getContext());

		// default behaviour: close it when clicking on the bubble:
		mView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				close();
			}
		});
	}

	@Override
	public void onClose() {
		// by default, do nothing
	}

	@Override
	public void onOpen(ExtendedOverlayItem item) {
		String title = item.getTitle();
		if (title == null)
			title = "";

		((TextView) mView.findViewById(mTitleId)).setText(title);

		String snippet = item.getDescription();
		if (snippet == null)
			snippet = "";

		((TextView) mView.findViewById(mDescriptionId)).setText(snippet);

		// handle sub-description, hidding or showing the text view:
		TextView subDescText = (TextView) mView.findViewById(mSubDescriptionId);
		String subDesc = item.getSubDescription();
		if (subDesc != null && !("".equals(subDesc))) {
			subDescText.setText(subDesc);
			subDescText.setVisibility(View.VISIBLE);
		} else {
			subDescText.setVisibility(View.GONE);
		}

		// handle image
		ImageView imageView = (ImageView) mView.findViewById(mImageId);
		Drawable image = item.getImage();
		if (image != null) {
			// or setBackgroundDrawable(image)?
			imageView.setImageDrawable(image);
			imageView.setVisibility(View.VISIBLE);
		} else
			imageView.setVisibility(View.GONE);

	}

}
