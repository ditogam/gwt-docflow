package com.docflowdroid;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.common.shared.ClSelectionItem;
import com.docflow.shared.GregorianCalendar;
import com.docflow.shared.ListSizes;
import com.docflow.shared.common.DocTypeMapping;
import com.docflow.shared.docflow.DocTypeWithDocList;
import com.docflow.shared.docflow.DocumentShort;
import com.docflowdroid.common.VNewDocument;
import com.docflowdroid.common.process.IProcess;
import com.docflowdroid.common.process.ProcessExecutor;
import com.docflowdroid.comp.CurrentTimeItem;
import com.docflowdroid.comp.adapter.Doc_List_Adapter;
import com.docflowdroid.comp.adapter.Doc_List_item_Helper;
import com.docflowdroid.comp.adapter.Statis_List_Adapter;
import com.docflowdroid.comp.adapter.UnsortedIDValueAdapter;
import com.docflowdroid.newversionhandler.ProgramUpdater;

public class MainActivity extends Activity implements Observer {
	private LinearLayout content;
	public static MainActivity instance;
	private CurrentTimeItem startDate;
	private CurrentTimeItem endDate;
	private Spinner spDocTypes;
	private Spinner spStatuses;
	private TextView tvFound;
	private static Intent timerService;
	private ArrayList<ClSelectionItem> remote_statuses;
	private int position = -1;
	public DocTypeMapping docTypeMapping;
	public String cancelary_no;

	public static final String NM_SESSION_ID = "NM_SESSION_ID";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!DocFlow.mWorkingOffline)
			ProgramUpdater.init(this, DocFlow.PROGRAM_ID, "sdfsdf", this);
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread paramThread, Throwable e) {
				e.printStackTrace();
				System.out.println(e);
			}
		});

		instance = this;
		setContentView(R.layout.activity_main);
		DocFlow.setActivityLandscape(this);

		startDate = new CurrentTimeItem(this);

		((LinearLayout) (findViewById(R.id.loStartDate))).addView(startDate
				.getComponent());
		tvFound = ((TextView) (findViewById(R.id.tvFound)));
		endDate = new CurrentTimeItem(this);
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(DocFlow.getCurrentDate());
		cal.add(GregorianCalendar.DATE, 1);
		endDate.setValue(cal.getTime());
		((LinearLayout) (findViewById(R.id.loEndDate))).addView(endDate
				.getComponent());
		spDocTypes = (Spinner) findViewById(R.id.spDocTypes);
		spStatuses = (Spinner) findViewById(R.id.spStatuses);
		ArrayList<ClSelectionItem> newstatuses = new ArrayList<ClSelectionItem>();
		ClSelectionItem i = new ClSelectionItem();
		i.setId(-1);
		i.setValue("-----");
		newstatuses.add(i);
		try {
			remote_statuses = DocFlow.user_obj.getStatusTree().get(
					DocFlow.system);

			for (ClSelectionItem item : remote_statuses) {
				newstatuses.add(item);
			}
		} catch (Throwable e) {
			// TODO: handle exception
		}
		spStatuses.setAdapter(new Statis_List_Adapter(this, newstatuses));
		spDocTypes.setAdapter(new UnsortedIDValueAdapter(DocFlow.doc_types,
				this));
		content = (LinearLayout) findViewById(R.id.content);
		content.addView(new Doc_List_item_Helper(this, true, -1, null)
				.getMy_view(true));
		// ListGridField[] fields = new ListGridField[] {
		// new ListGridField("combined", "combined", 500L, true) };

		// ListGrid lg = new ListGrid(fields);
		// lg.setDsName("CustomerShortDS");
		// HashMap<String, Object> criteria = new HashMap<String, Object>();
		// criteria.put("raiid", 24);
		// lg.setCriteria(criteria);
		// lg.setAutofetch(true);
		// lg.setShowRowNum(true);
		// View v = lg.createView(this);
		// if (v != null) {
		// content.addView(v);
		// lg.refreshData();
		// }

		((ImageButton) (findViewById(R.id.btnFind)))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						findDocuments(null);

					}
				});

		((ImageButton) (findViewById(R.id.ibtnNewDocument)))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						docTypeMapping = null;
						try {
							new VNewDocument(MainActivity.this,
									(int) spDocTypes.getSelectedItemId());
						} catch (final Throwable e) {

							ActivityHelper.showAlert(MainActivity.this, e);

						}

					}
				});

		if (!DocFlow.mWorkingOffline)
			ProgramUpdater.checkNewVersion(this, new IProcess() {

				public void execute() throws Exception {
					MainActivity.this.runOnUiThread(new Runnable() {

						public void run() {
							if (timerService != null)
								stopService(timerService);

							if (DocFlow.subregion_id > 0
									&& DocFlow.android_check_system_ids != null
									&& !DocFlow.android_check_system_ids
											.isEmpty()) {
								timerService = new Intent(MainActivity.this,
										TimeService.class);
								startService(timerService);
							}
						}
					});

				}
			});
	}

	private View doc_list;

	public ArrayList<String> createCriteria() {
		ArrayList<String> criterias = new ArrayList<String>();
		criterias.add("system_id=" + DocFlow.system);
		long docType = spDocTypes.getSelectedItemId();

		String doctypeFilter = "doc_type_id=" + docType;
		if (docType <= 0)
			doctypeFilter = "group_id=" + Math.abs(docType);

		criterias.add(doctypeFilter);

		return criterias;
	}

	private static final int THRESHOLD_SIZE = 75;
	int threshold = THRESHOLD_SIZE;
	private Doc_List_Adapter adapter;

	ProgressDialog dialog;

	protected void findDocuments(final Long session_id) {

		try {

			Date dt_end = endDate.getValueAsDate();
			Date dt_start = startDate.getValueAsDate();

			long _docType = spDocTypes.getSelectedItemId();
			ClSelectionItem o = (ClSelectionItem) spStatuses.getSelectedItem();
			long _doc_status = o.getId();
			long _end_date = dt_end.getTime();
			long _start_date = dt_start.getTime();

			final ArrayList<String> criteria = createCriteria();
			if (session_id != null) {
				criteria.clear();
				GregorianCalendar c = new GregorianCalendar();
				c.add(Calendar.MONTH, 1);
				_start_date = 0;
				_end_date = c.getTimeInMillis();
				criteria.add("android_session_id=" + session_id);
			} else {

				if (DocFlow.user_obj.getUser().getRegionid() > 0)
					criteria.add("regionid="
							+ DocFlow.user_obj.getUser().getRegionid());
				if (DocFlow.user_obj.getUser().getSubregionid() > 0)
					criteria.add("subregionid="
							+ DocFlow.user_obj.getUser().getSubregionid());
				if (_doc_status > 0)
					criteria.add("doc_status_id=" + _doc_status);
			}
			final long docType = _docType;
			final long end_date = _end_date;
			final long start_date = _start_date;
			final ListSizes ls = new ListSizes();

			final ListView lv = recreateListView();
			ls.setGenerate_sizes(true);

			pullMoreData(lv, ls, (int) docType, start_date, criteria, end_date);
			lv.setOnScrollListener(new OnScrollListener() {
				@Override
				public void onScrollStateChanged(AbsListView view,
						int scrollState) {
					// Do nothing
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {

					int my_th = threshold;
					int last = firstVisibleItem + visibleItemCount;
					if (last == my_th) {

						ls.setGenerate_sizes(false);
						pullMoreData(lv, ls, (int) docType, start_date,
								criteria, end_date);

					}
				}
			});

		} catch (Throwable e) {
			ActivityHelper.showAlert(MainActivity.this, e);
		}

	}

	private ListView recreateListView() {
		if (doc_list != null) {
			content.removeView(doc_list);
			doc_list.destroyDrawingCache();
		}
		dialog = new ProgressDialog(MainActivity.this);
		doc_list = getLayoutInflater().inflate(R.layout.document_list, null);
		final ListView lv = (ListView) doc_list.findViewById(R.id.doc_list);

		lv.setHorizontalScrollBarEnabled(true);
		adapter = new Doc_List_Adapter(MainActivity.this,
				new ArrayList<Doc_List_item_Helper>());
		lv.setAdapter(adapter);

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				docTypeMapping = null;
				MainActivity.this.position = position;
				Intent myIntent = new Intent(MainActivity.this,
						DocDetailActivity.class);
				Doc_List_item_Helper obj = (Doc_List_item_Helper) lv
						.getAdapter().getItem(position);
				myIntent.putExtra(DocDetailActivity.DOC_ID, (long) obj.getId());
				startActivity(myIntent);
				return true;
			}
		});

		content.addView(doc_list);
		return lv;
	}

	protected void pullMoreData(final ListView lv, final ListSizes ls,
			final int doc_type, final long startd_date,
			final ArrayList<String> criteria, final long enddate) {
		ls.setStart_row(ls.getEnd_row());
		final int start_row = ls.getStart_row();
		ls.setEnd_row(ls.getEnd_row() + THRESHOLD_SIZE);
		threshold = ls.getEnd_row();
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.permitAll().build());

		ProcessExecutor.execute(new IProcess() {

			@Override
			public void execute() throws Exception {
				DocTypeWithDocList result = DocFlow.docFlowService
						.getDocListForType(doc_type,
								CurrentTimeItem.trimDate(startd_date),
								CurrentTimeItem.trimDate(enddate),
								DocFlow.language_id, criteria, false, true, ls);
				final long total_tows = result.getTotal_count();
				ArrayList<DocumentShort> list = result.getDocList();
				final ArrayList<Doc_List_item_Helper> items = new ArrayList<Doc_List_item_Helper>();
				for (int i = 0; i < list.size(); i++) {
					items.add(new Doc_List_item_Helper(MainActivity.this,
							false, i + 1 + start_row, list.get(i)));
				}
				runOnUiThread(new Runnable() {
					public void run() {
						adapter.addAll(items);
						adapter.notifyDataSetChanged();
						if (ls.isGenerate_sizes())
							tvFound.setText(total_tows + "");
					}
				});

			}
		}, this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Bundle extras = intent.getExtras();

		Toast.makeText(this,
				"New documentssssssss" + extras.getLong(NM_SESSION_ID),
				Toast.LENGTH_SHORT).show();
		long session_id = extras.getLong(NM_SESSION_ID, -10000);
		if (session_id > 0)
			findDocuments(session_id);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		Intent myI = null;
		switch (id) {
		case R.id.action_logout:
			startActivity(new Intent(MainActivity.this, LoginActivity.class));
			finish();
			return true;
		case R.id.menu_data_traffic:
			myI = new Intent(this, DataTraffic.class);
			startActivity(myI);
			return true;
		case R.id.menu_import_act:
			myI = new Intent(this, ImportDBActivity.class);
			startActivity(myI);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void documentSaved(final DocumentShort docShort, final long doc_id) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (adapter == null)
					recreateListView();
				ArrayList<Doc_List_item_Helper> items = adapter.getItems();
				if (doc_id < 0) {
					for (Doc_List_item_Helper h : items) {
						h.increeseRecNo();
					}
					position = 0;
					items.add(position, new Doc_List_item_Helper(
							MainActivity.this, false, 1, docShort));

				} else {
					Doc_List_item_Helper h = items.get(position);
					h.replace(docShort);

				}
				adapter.notifyDataSetChanged();
			}
		});

	}

	@Override
	public void update(Observable arg0, Object arg1) {
		ProgramUpdater.checkNewVersion(this, null);

	}

}
