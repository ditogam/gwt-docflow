package com.docflowdroid.comp;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;

import com.docflowdroid.R;
import com.docflowdroid.helper.ICameraResult;
import com.docflowdroid.helper.camera.CameraHelper;

public class ImageItem extends StaticTextItem {

	/**
	 * 
	 */

	public static final String CAMERA_RESULT_FIELD = "CAMERA_RESULT_FIELD";

	@SuppressLint("ParcelCreator")
	private class CameraResult implements ICameraResult {
		@Override
		public void setResult(Map<Long, String> files) {
			ImageItem.this.setResult(files);

		}

	}

	private Object value;
	private ImageButton ibShowSet;
	private CameraResult cameraResult;

	public ImageItem(Context context) {
		super(context);
		cameraResult = new CameraResult();
	}

	@Override
	public void setValue(final Object value) {
		if (Looper.getMainLooper().equals(Looper.myLooper())) {
			this.value = value;
			int img = R.drawable.display_image;
			if (value == null || value.toString().trim().isEmpty())
				img = R.drawable.folder_open_16;
			ibShowSet.setImageResource(img);
		} else {
			((Activity) this.getContext()).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					setValue(value);
				}
			});
		}

	}

	@Override
	public String getValue() {
		return value == null ? null : value.toString();
	}

	@Override
	public String getDisplayValue() {
		return super.getValue();
	}

	@Override
	public void setDisplayValue(String value) {
		super.setValue(value);
	}

	@Override
	public void doAfterCreation() {
		ibShowSet = new ImageButton(getContext());

		OnClickListener l = new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (v.equals(ibShowSet)) {
					if (value == null) {
						panel.currentCameraResult = cameraResult;
						CameraHelper.startCameraCapture(panel.context);
					} else {
						try {
							CameraHelper.showFile(
									Integer.parseInt(value.toString().trim()),
									getDisplayValue(), panel.context);
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}

			}
		};
		ibShowSet.setOnClickListener(l);

		ArrayList<ImageButton> buttons = new ArrayList<ImageButton>();
		buttons.add(ibShowSet);

		ImageButton[] oldButtons = getIcons();
		if (oldButtons != null) {
			for (ImageButton imgButton : oldButtons) {
				buttons.add(imgButton);
			}
		}
		setIcons(buttons.toArray(new ImageButton[] {}));
	}

	public void setResult(final Map<Long, String> files) {
		if (Looper.getMainLooper().equals(Looper.myLooper())) {
			Set<Long> keys = files.keySet();
			for (Long key : keys) {
				setValue(key);
				setDisplayValue(files.get(key));
				break;
			}
		} else {
			((Activity) this.getContext()).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					setResult(files);
				}
			});
		}

	}

	public CameraResult getCameraResult() {
		return cameraResult;
	}
}
