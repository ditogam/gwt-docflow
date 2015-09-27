package com.docflowdroid.comp.adapter;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.docflow.shared.docflow.DocumentShort;
import com.docflowdroid.DocFlow;
import com.docflowdroid.R;
import com.docflowdroid.comp.StatusBox;

public class Doc_List_item_Helper {

	private static final String REC_NO = "REC_NO";
	// private static final SimpleDateFormat formatter = new SimpleDateFormat(
	// "dd/MM/yyyy HH:mm:ss");
	private static final SimpleDateFormat formatter = new SimpleDateFormat(
			"dd/MM/yy");
	private static final int[] comp_ids = { R.id.dl_no, R.id.dl_id,
			R.id.dl_status, R.id.dl_fc, R.id.dl_vers, R.id.dl_date_time,
			R.id.dl_cust, R.id.dl_cust_name, R.id.dl_doc_type, R.id.dl_zone,
			R.id.dl_region, R.id.dl_subregion, R.id.dl_user, R.id.dl_stade,
			R.id.dl_controller };

	private static final Hashtable<Integer, String> comp_methodes = new Hashtable<Integer, String>();
	private static final Hashtable<Integer, String> comp_titles = new Hashtable<Integer, String>();
	// private static final Hashtable<Integer, Class> comp_classes = new
	// Hashtable<Integer, Class>();

	static {
		generateMethodes();
	}

	private Activity activity;
	private boolean is_title;
	private int rec_no;
	private DocumentShort documentShort;

	public void increeseRecNo() {
		this.rec_no++;
		my_view = null;
	}

	public long getId() {
		return documentShort.getId();

	}

	public Doc_List_item_Helper(Activity activity, boolean is_title,
			int rec_no, DocumentShort documentShort) {
		super();
		this.activity = activity;
		this.is_title = is_title;
		this.rec_no = rec_no;
		this.documentShort = documentShort;
	}

	private View my_view;

	public void replace(DocumentShort documentShort) {
		this.documentShort = documentShort;
		my_view = null;
	}

	public View getMy_view(boolean setup_field_values) {
		if (my_view == null) {
			setup_field_values = true;
			try {
				my_view = activity.getLayoutInflater().inflate(
						R.layout.document_list_row, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (is_title) {
				try {
					for (int comp_id : comp_ids) {
						int id = comp_id;
						TextView tv = null;
						if (comp_id == R.id.dl_status) {
							id = R.id.dl_status_title;

							(my_view.findViewById(R.id.dl_status_box))
									.setVisibility(View.GONE);

						}
						tv = (TextView) my_view.findViewById(id);
						if (tv == null)
							continue;
						tv.setTypeface(null, Typeface.BOLD);
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		if (setup_field_values) {
			for (int comp_id : comp_ids) {
				TextView tv = null;
				int id = comp_id;
				if (comp_id == R.id.dl_status) {
					id = R.id.dl_status_title;
					if (!is_title) {
						((TextView) my_view.findViewById(id))
								.setVisibility(View.GONE);
						((StatusBox) my_view.findViewById(R.id.dl_status_box))
								.setColor(DocFlow.getStatusColor(documentShort
										.getDoc_status_id()));
						continue;

					} else {
						(my_view.findViewById(R.id.dl_status_box))
								.setVisibility(View.GONE);
					}
				}
				tv = (TextView) my_view.findViewById(id);
				if (tv == null)
					continue;
				String comp_methode = comp_methodes.get(comp_id);
				// Class comp_class = comp_classes.get(comp_id);
				String comp_title = comp_titles.get(comp_id);
				if (is_title)
					tv.setText(comp_title);
				else if (comp_id == R.id.dl_no)
					tv.setText(rec_no + "");
				else {
					String val = null;
					if (comp_methode != null) {
						try {
							Method meth = DocumentShort.class
									.getMethod(comp_methode);

							if (meth != null) {
								Object obj = meth.invoke(documentShort);
								if (obj != null) {
									if (comp_id == R.id.dl_date_time) {
										val = formatter.format(new Date(
												(Long) obj));
									} else {
										val = obj.toString();
									}
								}
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					tv.setText(val);
				}
			}
		}
		return my_view;
	}

	private static void generateMethodes() {
		comp_methodes.put(R.id.dl_no, REC_NO);
		comp_methodes.put(R.id.dl_id, "getId");
		comp_methodes.put(R.id.dl_fc, "getFilecount");
		comp_methodes.put(R.id.dl_vers, "getVersion_id");
		comp_methodes.put(R.id.dl_date_time, "getTransaction_date");
		comp_methodes.put(R.id.dl_cust, "getCust_id");
		comp_methodes.put(R.id.dl_cust_name, "getCustomer_name");
		comp_methodes.put(R.id.dl_doc_type, "getDoctype");
		comp_methodes.put(R.id.dl_zone, "getCzona");
		comp_methodes.put(R.id.dl_region, "getRegionname");
		comp_methodes.put(R.id.dl_subregion, "getSubregionname");
		comp_methodes.put(R.id.dl_user, "getUser_name");
		comp_methodes.put(R.id.dl_status, "getDoc_status_id");
		comp_methodes.put(R.id.dl_stade, "getTdocdelaystatus");
		comp_methodes.put(R.id.dl_controller, "getController_name");

		comp_titles.put(R.id.dl_no, "#");
		comp_titles.put(R.id.dl_id, "ID");
		comp_titles.put(R.id.dl_fc, "FC");
		comp_titles.put(R.id.dl_vers, "ვერს.");
		comp_titles.put(R.id.dl_date_time, "თარიღი");
		comp_titles.put(R.id.dl_cust, "აბონენტი");
		comp_titles.put(R.id.dl_cust_name, "აბონენტის სახელი");
		comp_titles.put(R.id.dl_doc_type, "ტიპი");
		comp_titles.put(R.id.dl_zone, "ზონა");
		comp_titles.put(R.id.dl_region, "რეგიონი");
		comp_titles.put(R.id.dl_subregion, "რაიონი");
		comp_titles.put(R.id.dl_user, "User");
		comp_titles.put(R.id.dl_status, "სტ");
		comp_titles.put(R.id.dl_stade, "სტადია");
		comp_titles.put(R.id.dl_controller, "კონტ");

	}

}
