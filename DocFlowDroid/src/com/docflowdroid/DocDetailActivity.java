package com.docflowdroid;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;

import com.common.shared.ClSelectionItem;
import com.docflow.shared.CustomerShort;
import com.docflow.shared.PermissionNames;
import com.docflow.shared.common.DocTypeMapping;
import com.docflow.shared.common.DocumentFile;
import com.docflow.shared.common.FormDefinition;
import com.docflow.shared.docflow.DocType;
import com.docflow.shared.docflow.DocumentLong;
import com.docflow.shared.docflow.DocumentShort;
import com.docflowdroid.common.BooleanCallback;
import com.docflowdroid.common.DocStatusPanel;
import com.docflowdroid.common.SC;
import com.docflowdroid.common.VDocumentFiles;
import com.docflowdroid.common.process.IProcess;
import com.docflowdroid.common.process.ProcessExecutor;
import com.docflowdroid.comp.FormDefinitionPanel;
import com.docflowdroid.helper.ICameraResult;
import com.docflowdroid.helper.camera.CameraHelper;

public class DocDetailActivity extends FragmentActivity implements
		ActionBar.TabListener {
	public static final String DOC_ID = "DOC_ID";
	public static final String DOC_TYPE_ID = "DOC_TYPE_ID";
	public static final String DOC_TYPE_TAB_ID = "DOC_TYPE_TAB_ID";
	private long doc_id = 0;
	private FormDefinitionPanel definitionPanel;
	private View[] tabs;
	private ActionBar actionBar;
	private VDocumentFiles documentFiles;
	private DocumentLong myDoc;
	private DocType myDocType;
	private DocStatusPanel statusPanel = null;
	private Tab tabStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doc_detail);
		DocFlow.setActivityLandscape(this);

		Bundle bundle = this.getIntent().getExtras();
		doc_id = bundle.getLong(DOC_ID, -1);
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		ProcessExecutor.execute(new IProcess() {

			@Override
			public void execute() throws Exception {

				try {

					long m_doc_id = doc_id;
					DocTypeMapping mapping = MainActivity.instance.docTypeMapping;
					mapping = mapping == null ? DocFlow.docFlowService
							.getDocumentWithMapping((int) m_doc_id,
									DocFlow.language_id, DocFlow.user_id)
							: mapping;
					FormDefinition formd = new FormDefinition();
					formd.setXml(mapping.getDocType().getDoc_template());

					DocumentLong document = mapping.getDocument();
					if (doc_id <= 0) {
						document.setCancelary_nom(MainActivity.instance.cancelary_no);
					}
					myDoc = document;

					DocType dt = mapping.getDocType();
					ArrayList<DocumentFile> files = mapping.getFiles();
					myDocType = dt;

					HashMap<String, String> displayValues = mapping
							.getDisplayValues();

					HashMap<String, String> values = mapping.getValues();

					final HashMap<String, ArrayList<ClSelectionItem>> result = mapping
							.getSelections();

					showDocument(formd, document, dt, files, displayValues,
							values, result);

				} catch (Exception e) {
					final Exception ex = e;
					finish();
					MainActivity.instance.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							ActivityHelper.showAlert(MainActivity.instance, ex);

						}
					});
				}

			}
		}, this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.doc_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {

		case R.id.action_doc_save:
			saveStatusOrDoc();
			return true;
		case R.id.action_doc_close:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void saveStatusOrDoc() {

		if (tabStatus != null && tabStatus.equals(actionBar.getSelectedTab())) {
			statusPanel.saveStatus(this);
		} else
			saveDocument();
	}

	private Long getLongValue(HashMap<String, Object> data, String keyname) {
		try {
			return Long.parseLong(getStringValue(data, keyname, 0));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0L;
	}

	private String getStringValue(HashMap<String, Object> data, String keyname) {
		return getStringValue(data, keyname, 1);
	}

	private String getStringValue(HashMap<String, Object> data, String keyname,
			int index) {
		Object value = data.get(keyname);
		try {
			if (value instanceof String[])
				return ((String[]) (value))[index];
			else
				return value.toString();
		} catch (Exception e) {

		}
		return "";
	}

	private void setDefaultCustomerAttributer(DocumentLong doc,
			HashMap<String, Object> data) {
		try {
			doc.setStreet_id(getLongValue(data, "streetid").intValue());
			doc.setStreenname(getStringValue(data, "streetid"));
			doc.setCityid(getLongValue(data, "cityId").intValue());
			doc.setCityname(getStringValue(data, "cityId"));
			doc.setSubregionid(getLongValue(data, "subregionId").intValue());
			doc.setSubregionname(getStringValue(data, "subregionId"));
			doc.setRegionid(getLongValue(data, "regionId").intValue());
			doc.setRegionname(getStringValue(data, "regionId"));
			doc.setCustomer_name(getStringValue(data, "cusname"));
			doc.setCzona(getLongValue(data, "zone"));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void saveDocument() {
		try {
			if (!definitionPanel.validate())
				return;

			int delayStatus = 0;

			if (doc_id < 0) {
				myDoc.setDoc_flow_num("asdas");
				myDoc.setDoc_status_id(DocFlow.user_obj
						.getInitial_status(myDocType.getSystem_id()));
				myDoc.setUser_id(DocFlow.user_obj.getUser().getUser_id());
				myDoc.setDoc_type_id(myDocType.getId());
				myDoc.setDoc_date(System.currentTimeMillis());
				myDoc.setTransaction_date(System.currentTimeMillis());
			}
			int ds = definitionPanel.validateDate(new Date(myDoc
					.getTransaction_date()));
			if (ds == -1) {
				String error = "თარიღი მითითებულია მომავალში!!!";
				SC.say(this, "Error!!!", error, new BooleanCallback() {

					@Override
					public void execute(Boolean value) {

					}
				});
				return;
			}
			if (ds == -2) {
				String error = "თარიღი ძალიან ძველია!!!";
				SC.say(this, "Error!!!", error, new BooleanCallback() {

					@Override
					public void execute(Boolean value) {

					}
				});
				return;
			}
			if (myDocType.getDatefield().equals("@docdate"))
				delayStatus = ds;

			if (!myDocType.getDatefield().equals("@docdate"))
				delayStatus = definitionPanel.getDelayInterval();

			final int docdelaystatuse = delayStatus;

			ProcessExecutor.execute(new IProcess() {

				@Override
				public void execute() throws Exception {
					try {

						final ArrayList<DocumentFile> files = documentFiles
								.saveData();
						HashMap<String, Object> data = definitionPanel
								.getData();
						final String content = getXml(data);
						myDoc.setContent_xml(content);
						DocumentShort ds = null;
						if (doc_id <= 0) {
							if (!myDocType.isApplied_customer()) {
								setDefaultCustomerAttributer(myDoc, data);
							}
							ds = DocFlow.docFlowService.saveDocument(myDoc,
									files, DocFlow.language_id);
						} else
							ds = DocFlow.docFlowService.documentCorrection(
									(int) doc_id, content, DocFlow.user_obj
											.getUser().getUser_id(), myDoc
											.getTransaction_date(), files,
									DocFlow.language_id, docdelaystatuse);
						if (ds == null)
							throw new Exception("sdfsdfsdf");
						final DocumentShort docShort = ds;
						final long _old_doc_id = doc_id;
						doc_id = ds.getId();
						DocDetailActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								MainActivity.instance.documentSaved(docShort,
										_old_doc_id);
								doAfterSave(docShort.getDoc_status_id());
							}
						});

					} catch (final Throwable e) {
						DocDetailActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ActivityHelper.showAlert(
										DocDetailActivity.this, e);

							}
						});
					}

				}
			}, this);
		} catch (final Throwable e) {
			DocDetailActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ActivityHelper.showAlert(DocDetailActivity.this, e);

				}
			});
		}
	}

	private String getXml(HashMap<String, Object> data) throws Exception {
		String ret = "";
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory
				.newDocumentBuilder();
		Document doc = documentBuilder.newDocument();

		// Root Element
		Element rootElem = doc.createElement("DocDef");
		doc.appendChild(rootElem);
		Set<String> keys = data.keySet();
		for (String key : keys) {
			Element val = doc.createElement("Val");
			val.setAttribute("key", key);
			Object obj = data.get(key);
			String value = "";
			String text = null;
			if (obj == null) {
				value = "";
			} else if (obj instanceof String[]) {
				String[] strings = (String[]) obj;
				if (strings.length > 0) {
					value = strings[0];
					if (strings.length > 1) {
						text = strings[1];
					}
				}
			} else {
				value = obj.toString();
			}
			val.setAttribute("value", value);
			if (text != null)
				val.setAttribute("text", text);
			rootElem.appendChild(val);
		}
		DOMSource domSource = new DOMSource(doc);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(domSource, result);
		ret = writer.toString();
		return ret;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (definitionPanel.getCurrentCameraResult() != null
				&& CameraHelper.isCameraResult(this, requestCode, resultCode)) {
			ICameraResult currentCameraResult = definitionPanel
					.getCurrentCameraResult();
			definitionPanel.setCurrentCameraResult(null);
			CameraHelper.executeCameraResult(this, requestCode, resultCode,
					data, currentCameraResult);

		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		Fragment fragment = new DummySectionFragment();
		Bundle args = new Bundle();
		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER,
				tab.getPosition() + 1);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	private String getCustomerHint(CustomerShort customerShort) {
		if (customerShort == null)
			return null;
		String result = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></head><body>";
		result += "<b>Customer</b>:" + customerShort.getCusid() + "<br>";
		result += "<b>Name</b>:" + customerShort.getCusname() + "<br>";
		result += "<b>Region</b>:" + customerShort.getRegion() + "<br>";
		result += "<b>District</b>:" + customerShort.getRaion() + "<br>";
		result += "<b>City</b>:" + customerShort.getCityname() + "<br>";
		result += "<b>Zone</b>:" + customerShort.getZone() + "<br>";
		result += "<b>Street</b>:" + customerShort.getStreetname() + "<br>";
		result += "<b>Home</b>:" + customerShort.getHome().trim() + "<br>";
		result += "<b>Flat</b>:" + customerShort.getFlat().trim() + "<br>";
		result += "<b>Scope</b>:" + customerShort.getScopename();
		if (customerShort.getLoan() != null)
			result += "<br><b>Loan</b>:" + customerShort.getLoan();

		result = (result + "</body><html>").toString();
		return result;
	}

	public void showDocument(final FormDefinition formd,
			final DocumentLong document, final DocType dt,
			final ArrayList<DocumentFile> files,
			final HashMap<String, String> displayValues,
			final HashMap<String, String> values,
			final HashMap<String, ArrayList<ClSelectionItem>> result) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				try {
					ScrollView sv = new ScrollView(DocDetailActivity.this);
					sv.setLayoutParams(new LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT));
					definitionPanel = new FormDefinitionPanel(
							DocDetailActivity.this, formd, dt, document);
					definitionPanel.setRegionPermitions(values,
							document.getCust_id(), displayValues);
					definitionPanel.setFieldLists(result, values, true);
					definitionPanel.setListeners();
					ArrayList<View> tabs = new ArrayList<View>();

					boolean status_panel = (DocFlow
							.hasPermition(PermissionNames.CHANGE_STATUS) || DocFlow
							.hasPermition(PermissionNames.CAN_VIEW_DOC_STATUSE));

					actionBar.addTab(actionBar.newTab()
							.setText(R.string.doc_data)
							.setTabListener(DocDetailActivity.this));

					documentFiles = new VDocumentFiles(DocDetailActivity.this,
							(int) doc_id, files, definitionPanel);

					boolean isolddocument = document.getId() > 0;
					if (status_panel) {
						tabStatus = actionBar.newTab();

					}
					actionBar.addTab(actionBar.newTab()
							.setText(R.string.doc_files)
							.setTabListener(DocDetailActivity.this));
					sv.addView(definitionPanel);
					tabs.add(sv);
					tabs.add(documentFiles.getMy_view());

					actionBar.addTab(actionBar.newTab()
							.setText(R.string.customer_info)
							.setTabListener(DocDetailActivity.this));
					WebView vView = new WebView(DocDetailActivity.this);
					WebSettings settings = vView.getSettings();
					settings.setDefaultTextEncodingName("utf-8");
					settings.setJavaScriptEnabled(false);
					vView.loadData(
							getCustomerHint(document.getCustomerShort()),
							"text/html; charset=utf-8", "UTF-8");
					tabs.add(vView);
					if (status_panel) {
						ArrayList<ClSelectionItem> statuses = DocFlow.user_obj
								.getStatusTree().get(DocFlow.system);
						View v = getLayoutInflater().inflate(
								R.layout.activity_document_status, null);
						DocDetailActivity.this.statusPanel = new DocStatusPanel(
								DocDetailActivity.this,
								v,
								statuses,
								document,
								!DocFlow.hasPermition(PermissionNames.CHANGE_STATUS));
						tabs.add(v);
						if (isolddocument)
							addStatusTab();
					}

					DocDetailActivity.this.tabs = tabs.toArray(new View[] {});
				} catch (Throwable e) {
					ActivityHelper.showAlert(DocDetailActivity.this, e);
				}
			}
		});
	}

	public void addStatusTab() {
		actionBar.addTab(tabStatus.setText(R.string.doc_status).setTabListener(
				DocDetailActivity.this));
		statusPanel.setVisible(true);
	}

	public void doAfterSave(int statusid) {
		if (tabStatus != null && !statusPanel.isVisible()) {
			addStatusTab();
			statusPanel.setDoc_id(doc_id);
		}
		if (tabStatus != null)
			statusPanel.setStatusId(statusid);

	}

	@SuppressLint("ValidFragment")
	public class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			int index = getArguments().getInt(ARG_SECTION_NUMBER);
			View v = null;
			try {
				v = tabs[index - 1];
				if (v.getParent() != null) {
					((ViewGroup) v.getParent()).removeView(v);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			return v;
		}
	}
}
