package com.socarmap;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;

import org.oscim.core.GeoPoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore.MediaColumns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.socarmap.db.DBLoader;
import com.socarmap.db.DBSettingsLoader;
import com.socarmap.helper.ActivityHelper;
import com.socarmap.helper.BitmapHelper;
import com.socarmap.helper.ConnectionHelper;
import com.socarmap.proxy.beans.DemageDescription;
import com.socarmap.proxy.beans.IDValue;
import com.socarmap.proxy.beans.SocarException;
import com.socarmap.ui.IDValueAdapter;
import com.socarmap.ui.MapSelectionOverlay;

public class DemageActivity extends Activity {

	public class PicAdapter extends BaseAdapter {

		// use the default gallery background image
		int defaultItemBackground;

		// gallery context
		private Context galleryContext;

		// array to store bitmaps to display
		ArrayList<Bitmap> imageBitmaps;

		// placeholder bitmap for empty spaces in gallery

		// constructor
		public PicAdapter(Context c) {

			// instantiate context
			galleryContext = c;
			imageBitmaps = new ArrayList<Bitmap>();
			// create bitmap array

			// get the styling attributes - use default Andorid system resources
			TypedArray styleAttrs = galleryContext
					.obtainStyledAttributes(R.styleable.PicGallery);
			// get the background resource
			defaultItemBackground = styleAttrs.getResourceId(
					R.styleable.PicGallery_android_galleryItemBackground, 0);
			// recycle attributes
			styleAttrs.recycle();
		}

		// BaseAdapter methods

		// helper method to add a bitmap to the gallery when the user chooses
		// one
		public void addPic(Bitmap newPic) {
			// set at currently selected index
			imageBitmaps.add(newPic);
		}

		// return number of data items i.e. bitmap images
		@Override
		public int getCount() {
			return imageBitmaps.size();
		}

		// return item at specified position
		@Override
		public Object getItem(int position) {
			return position;
		}

		// return item ID at specified position
		@Override
		public long getItemId(int position) {
			return position;
		}

		// custom methods for this app

		// return bitmap at specified position for larger display
		public Bitmap getPic(int posn) {
			// return bitmap at posn index
			return imageBitmaps.get(posn);
		}

		// get view specifies layout and display options for each thumbnail in
		// the gallery
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			// create the view
			ImageView imageView = new ImageView(galleryContext);
			// specify the bitmap at this position in the array
			imageView.setImageBitmap(imageBitmaps.get(position));
			// set layout options
			imageView.setLayoutParams(new Gallery.LayoutParams(300, 200));
			// scale type within view area
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			// set default gallery item background
			imageView.setBackgroundResource(defaultItemBackground);
			// return the view
			return imageView;
		}
	}

	private final int PICKER = 1;
	// variable to store the currently selected image
	// adapter for gallery view
	private PicAdapter imgAdapt;
	// gallery object
	private Gallery picGallery;

	// image view for larger display
	private ImageView picView;
	private Spinner spDemageType;
	private Calendar currentTime;
	private TextView tvDemageTime;
	private Button selectTime;
	private EditText etComment;
	private Button btAdd;
	private Button btRemove;

	private Button btSave;
	public static final String DEMAGE_DESCRIPTION = "DEMAGE_DESCRIPTION";

	// private ImageView selectedImageView;

	public static final String DEMAGE_DESCRIPTION_TYPE_NAME = "DEMAGE_DESCRIPTION_TYPE_NAME";
	private GeoPoint geoPoint;

	private ArrayList<File> files;

	public void addPicture(Bitmap pic) {
		imgAdapt.addPic(pic);
		// redraw the gallery thumbnails to reflect the new addition
		picGallery.setAdapter(imgAdapt);

		// display the newly selected image at larger size
		picView.setImageBitmap(pic);
		// scale options
		picView.setScaleType(ImageView.ScaleType.FIT_CENTER);
	}

	protected void createDialog() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.datetime, null);
		// text_entry is an Layout XML file containing two text field to display
		// in alert dialog
		final DatePicker date = (DatePicker) textEntryView
				.findViewById(R.id.datePicker1);
		final TimePicker time = (TimePicker) textEntryView
				.findViewById(R.id.timePicker1);
		time.setIs24HourView(true);
		date.setMaxDate(new GregorianCalendar().getTimeInMillis());
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.MONTH, -1);
		date.setMinDate(cal.getTimeInMillis());
		date.updateDate(currentTime.get(Calendar.YEAR),
				currentTime.get(Calendar.MONTH),
				currentTime.get(Calendar.DAY_OF_MONTH));
		time.setCurrentHour(currentTime.get(Calendar.HOUR));
		time.setCurrentMinute(currentTime.get(Calendar.MINUTE));
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setIcon(R.drawable.icon)
				.setTitle("Enter the Text:")
				.setView(textEntryView)
				.setPositiveButton("Save",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								currentTime.set(Calendar.YEAR, date.getYear());
								currentTime.set(Calendar.MONTH, date.getMonth());
								currentTime.set(Calendar.DAY_OF_MONTH,
										date.getDayOfMonth());
								currentTime.set(Calendar.HOUR,
										time.getCurrentHour());
								currentTime.set(Calendar.MINUTE,
										time.getCurrentMinute());
								setTime();
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						});
		alert.show();

	}

	/**
	 * Handle returning from gallery or file manager image selection - import
	 * the image bitmap
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			// check if we are returning from picture selection
			if (requestCode == PICKER) {

				// the returned picture URI
				Uri pickedUri = data.getData();

				// declare the bitmap
				Bitmap pic = null;
				// declare the path string
				String imgPath = "";

				// retrieve the string using media data
				String[] medData = { MediaColumns.DATA };
				// query the data
				Cursor picCursor = managedQuery(pickedUri, medData, null, null,
						null);
				if (picCursor != null) {
					// get the path string
					int index = picCursor
							.getColumnIndexOrThrow(MediaColumns.DATA);
					picCursor.moveToFirst();
					imgPath = picCursor.getString(index);
				} else
					imgPath = pickedUri.getPath();

				// if and else handle both choosing from gallery and from file
				// manager

				// if we have a new URI attempt to decode the image bitmap
				if (pickedUri != null) {

					// set the width and height we want to use as maximum
					// display
					int targetWidth = 600;
					int targetHeight = 400;

					// sample the incoming image to save on memory resources
					files.add(new File(imgPath));
					pic = BitmapHelper.resizePicture(imgPath, targetWidth,
							targetHeight);

					// pass bitmap to ImageAdapter to add to array
					addPicture(pic);
				}
			}
		}
		// superclass method
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Bundle bundle = this.getIntent().getExtras();
		Parcelable pa = bundle.getParcelable(MapSelectionOverlay.REQUEST_POINT);
		files = new ArrayList<File>();
		geoPoint = (GeoPoint) pa;
		currentTime = Calendar.getInstance();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demage_demage);
		spDemageType = (Spinner) findViewById(R.id.sp_demage_type);
		tvDemageTime = (TextView) findViewById(R.id.ti_demage_datetime);
		selectTime = (Button) findViewById(R.id.bi_select_demage_time);
		etComment = (EditText) findViewById(R.id.demage_descript);
		btAdd = (Button) findViewById(R.id.bt_add_demage);
		btSave = (Button) findViewById(R.id.bt_save_demage);
		btRemove = (Button) findViewById(R.id.bt_remove_demage);
		picGallery = (Gallery) findViewById(R.id.gallery);
		picView = (ImageView) findViewById(R.id.picture);

		imgAdapt = new PicAdapter(this);
		// set the gallery adapter
		picGallery.setAdapter(imgAdapt);

		// set the click listener for each item in the thumbnail gallery
		picGallery.setOnItemClickListener(new OnItemClickListener() {
			// handle clicks
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// set the larger image view to display the chosen bitmap
				// calling method of adapter class
				picView.setImageBitmap(imgAdapt.getPic(position));
			}
		});

		btRemove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				removeImage();

			}
		});
		btSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveData();

			}
		});
		btAdd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT,
						null);
				galleryintent.setType("image/*");

				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

				Intent chooser = new Intent(Intent.ACTION_CHOOSER);
				chooser.putExtra(Intent.EXTRA_INTENT, galleryintent);
				chooser.putExtra(Intent.EXTRA_TITLE, "Choose image");

				Intent[] intentArray = { cameraIntent };
				chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
				startActivityForResult(chooser, PICKER);

			}
		});
		setTime();

		CustomerSearch.setupSpinnerFromMap(spDemageType, DBLoader.demage_types,
				null);
		selectTime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View paramView) {
				createDialog();

			}
		});

		Bundle b = getIntent().getExtras();
		try {
			int id = b.getInt(DEMAGE_DESCRIPTION);
			DemageDescription dd = DBSettingsLoader.getInstance()
					.getDemageDescription(id);
			if (dd != null) {
				IDValueAdapter adapter = (IDValueAdapter) spDemageType
						.getAdapter();
				Integer pos = adapter.getPosition(dd.getDemage_type());
				if (pos != null)
					spDemageType.setSelection(pos);
				currentTime = new GregorianCalendar();
				currentTime.setTimeInMillis(dd.getTime());
				setTime();
				selectTime.setVisibility(View.GONE);
				etComment.setText(dd.getDescription());
				((View) btAdd.getParent()).setVisibility(View.GONE);
				btSave.setVisibility(View.GONE);
				ArrayList<byte[]> bytes = dd.getBytes();
				for (byte[] cs : bytes) {
					int targetWidth = 600;
					int targetHeight = 400;
					Bitmap bm = BitmapHelper.resizePicture(cs, targetWidth,
							targetHeight);
					addPicture(bm);
				}
			}
		} catch (Throwable e) {

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.demage_demage, menu);
		return true;
	}

	protected void removeImage() {
		final int currentPic = picGallery.getSelectedItemPosition();
		if (currentPic < 0)
			return;

		ActivityHelper.askAlert(this, "Do you want to remove picture?",
				"Remove picture", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							int newPos = currentPic - 1;
							files.remove(currentPic);
							imgAdapt.imageBitmaps.remove(currentPic);
							imgAdapt.notifyDataSetChanged();
							if (newPos >= 0) {
								picGallery.setSelection(newPos);
							} else
								picView.setImageBitmap(null);
						} catch (Throwable e) {

						}
					}
				});

	}

	protected void saveData() {
		try {
			Object obj = spDemageType.getSelectedItem();
			if (obj == null) {
				ActivityHelper.showAlert(getApplication(),
						"Please enter type!!!");
				return;
			}
			IDValue g = (IDValue) obj;
			String text = etComment.getText().toString();
			text = text == null ? "" : text.trim();
			if (text.length() == 0) {
				ActivityHelper.showAlert(this, "Please enter description!!!");
				return;

			}

			String uId = null;
			uId = UUID.randomUUID().toString();

			DemageDescription dd = new DemageDescription();

			dd.setDemage_type((int) g.getId());
			dd.setDescription(text);
			dd.setTime(currentTime.getTimeInMillis());
			dd.setPx(geoPoint.longitudeE6 / 1E6);
			dd.setPy(geoPoint.latitudeE6 / 1E6);

			if (dd.getBytes() == null)
				dd.setBytes(new ArrayList<byte[]>());
			if (DBSettingsLoader.getInstance() == null) {
				String dbSettingsPath = getString(R.string.settings_db);
				DBSettingsLoader.initInstance(dbSettingsPath);
			}

			for (File file : files) {
				int targetWidth = 900;
				int targetHeight = 600;
				byte[] bt = BitmapHelper.resizeBitmapAndEncode(
						file.getAbsolutePath(), targetWidth, targetHeight);
				if (uId != null) {
					try {
						ConnectionHelper.uploadFile(uId, bt);
						// ConnectionHelper.getConnection().transferFile(uId,
						// bt);
					} catch (Throwable e) {
						uId = null;
					}
				}
				dd.getBytes().add(bt);
			}

			int id = -1;
			try {
				if (uId != null)
					id = ConnectionHelper.getConnection()
							.saveDemageDescription(
									uId,
									dd,
									ConnectionHelper.userContext
											.getShort_value());
			} catch (SocarException e) {
				throw e;
			} catch (Throwable e) {

			}
			dd.setId(id);
			dd.setFiles(files);
			DBSettingsLoader.getInstance().saveDemageDescription(dd);
			dd.setFiles(null);

			Intent intent = new Intent();

			// GeoPoint geoPoint = new GeoPoint(dd.getPy() * 1E6, dd.getPx() *
			// 1E6);
			intent.putExtra(MapSelectionOverlay.REQUEST_POINT, geoPoint);
			intent.putExtra(DEMAGE_DESCRIPTION, dd.getId().intValue());
			intent.putExtra(DEMAGE_DESCRIPTION_TYPE_NAME, g.getValue());

			if (getParent() == null) {
				setResult(Activity.RESULT_OK, intent);
			} else {
				getParent().setResult(Activity.RESULT_OK, intent);
			}

			finish();

		} catch (Throwable e) {
			ActivityHelper.showAlert(this, e);
		}
		files.clear();
		System.gc();
	}

	private void setTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				Locale.US);

		tvDemageTime.setText(sdf.format(currentTime.getTime()));

	}

}
