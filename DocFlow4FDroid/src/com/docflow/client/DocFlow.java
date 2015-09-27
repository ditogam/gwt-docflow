package com.docflow.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.TreeMap;

import org.gwtopenmaps.openlayers.client.util.JSObject;

import com.common.client.usermanager.WLoginForm;
import com.common.client.usermanager.WLoginForm.LogOnEvent;
import com.common.client.usermanager.WLoginForm.LogOnHandler;
import com.common.shared.ClSelectionItem;
import com.common.shared.LSystem;
import com.common.shared.Language;
import com.docflow.client.components.CardLayoutCanvas;
import com.docflow.client.components.NavigationSectionStack;
import com.docflow.client.components.common.SimpleFieldDefinitionListValue;
import com.docflow.client.components.corector.PCorector;
import com.docflow.client.components.docflow.DocFlowSectionStack;
import com.docflow.client.components.docflow.PBankLive;
import com.docflow.client.components.docflow.PCoeficient;
import com.docflow.client.components.docflow.PPlomb;
import com.docflow.client.components.docflow.PReaderList;
import com.docflow.client.components.docflow.PZoneChange;
import com.docflow.client.components.gasmonitor.PMonitor;
import com.docflow.client.components.hr.PJob_Position;
import com.docflow.client.components.hr.PStructure;
import com.docflow.client.components.map.GMapPanel;
import com.docflow.client.components.usermanager.PUserManager;
import com.docflow.shared.ClSelection;
import com.docflow.shared.PermissionNames;
import com.docflow.shared.SCSystem;
import com.docflow.shared.UserObject;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.core.RefDataClass;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DocFlow implements EntryPoint, LogOnHandler, SCSystem {

	public static TreeMap<Integer, String> cancelaryMappings = new TreeMap<Integer, String>();

	static {
		cancelaryMappings.put(S_DOCFLOW, "Cancelary Number");
		cancelaryMappings.put(S_CALL_CENTER, "Phone number");
		cancelaryMappings.put(S_ECCIDENT_CONTROLL, "Phone number");
	}

	public static String getCancelaryName() {
		String ret = cancelaryMappings.get(system_id);
		if (ret == null)
			ret = "Cancelary Number";
		return ret;
	}

	public static boolean setCancelaryPhoneNumber() {
		return system_id == S_CALL_CENTER || system_id == S_ECCIDENT_CONTROLL;
	}

	public static final DocFlowServiceAsync docFlowService = GWT
			.create(DocFlowService.class);

	public static final ArrayList<Language> langs = new ArrayList<Language>();
	public static int user_id;
	public static String user_name;
	public static UserObject user_obj = null;
	public static int language_id;
	public static int system_id;
	public static int panelheight;
	private HLayout aboutCanvas;

	public static DocFlow docFlow;

	public CardLayoutCanvas card = new CardLayoutCanvas();

	public static TreeMap<Integer, String> captions = new TreeMap<Integer, String>();

	public static Window doclistWindow = null;

	public static long currenttime = 0;

	public native void setFunctions()/*-{
		$wnd._setselectitempickerlist = function(field, dsName, valueField,
				displayField, operationId, pickerFields, criteria) {
			@com.docflow.client.DocFlow::setSelectItemPickerList(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(field,dsName,valueField,displayField,operationId,pickerFields,criteria);
		};
		$wnd._setselectitemcriteria = function(field, criteria) {
			@com.docflow.client.DocFlow::setCriteria(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(field,criteria);
		};
		$wnd._setselectitemdatasource = function(field, dsName, valueField,
				displayField, operationId, criteria) {
			@com.docflow.client.DocFlow::setSelectItemDatasource(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(field,dsName,valueField,displayField,operationId,criteria);
		};

	}-*/;

	private static boolean isFormItemSelection(FormItem filteredSelect) {
		return (filteredSelect instanceof SelectItem || filteredSelect instanceof ComboBoxItem);
	}

	public static void setSelectItemDatasource(JavaScriptObject field,
			String dsName, String valueField, String displayField,
			String operationId, JavaScriptObject criteria) {
		try {
			FormItem filteredSelect = (FormItem) SelectItem.getRef(field);
			if (!isFormItemSelection(filteredSelect))
				return;
			if (filteredSelect instanceof SelectItem
					&& ((SelectItem) filteredSelect).getOptionDataSource() != null)
				return;
			if (filteredSelect instanceof ComboBoxItem
					&& ((ComboBoxItem) filteredSelect).getOptionDataSource() != null)
				return;

			filteredSelect.setOptionDataSource(DataSource.get(dsName));
			filteredSelect.setDisplayField(displayField);
			filteredSelect.setValueField(valueField);

			if (operationId != null && !operationId.trim().isEmpty())
				filteredSelect.setOptionOperationId(operationId.trim());
			setCriteria(field, criteria);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void setSelectItemPickerList(JavaScriptObject field,
			String dsName, String valueField, String displayField,
			String operationId, JavaScriptObject pickerFields,
			JavaScriptObject criteria) {
		try {
			FormItem filteredSelect = (FormItem) SelectItem.getRef(field);
			if (!isFormItemSelection(filteredSelect))
				return;
			if (filteredSelect.getAttributeAsObject("pickListFields") != null)
				return;

			setSelectItemDatasource(field, dsName, valueField, displayField,
					operationId, criteria);

			String propertyNames = SimpleFieldDefinitionListValue
					.getPropertyNames(pickerFields);
			String[] propertyNamesArr = propertyNames.split(",");
			ListGridField[] pickerList = new ListGridField[propertyNamesArr.length];
			for (int i = 0; i < pickerList.length; i++) {
				String fieldName = propertyNamesArr[i];
				JavaScriptObject attr = JSOHelper
						.getAttributeAsJavaScriptObject(pickerFields, fieldName);
				ListGridField lgField = new ListGridField(fieldName);
				pickerList[i] = lgField;
				String fieldTitle = JSOHelper.getAttribute(attr, "title");
				Integer fieldWidth = JSOHelper.getAttributeAsInt(attr, "width");
				if (fieldTitle != null)
					lgField.setTitle(fieldTitle);
				if (fieldWidth != null)
					lgField.setWidth(fieldWidth);

			}
			ListGrid pickListProperties = new ListGrid();
			pickListProperties.setShowFilterEditor(true);

			filteredSelect.setOptionDataSource(DataSource.get(dsName));
			filteredSelect.setDisplayField(displayField);
			filteredSelect.setValueField(valueField);
			filteredSelect.setAttribute("pickListFields", pickerList);

			JavaScriptObject configJS = pickListProperties.getConfig();
			if (configJS != null) {
				configJS = JSOHelper.cleanProperties(configJS, true);
				JSOHelper.deleteAttribute(configJS, "alternateRecordStyles");
			}
			filteredSelect.setAttribute("pickListProperties", configJS);

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void setCriteria(JavaScriptObject field,
			JavaScriptObject criteria) {
		String propertyNames;
		String[] propertyNamesArr;
		try {

			FormItem filteredSelect = (FormItem) RefDataClass.getRef(field);
			if (criteria != null) {
				propertyNames = SimpleFieldDefinitionListValue
						.getPropertyNames(criteria);
				propertyNamesArr = propertyNames.split(",");
				Criteria cr = null;
				for (String prop : propertyNamesArr) {
					Object val = SimpleFieldDefinitionListValue.getProperty(
							criteria, prop);
					if (val == null)
						continue;
					if (cr == null)
						cr = new Criteria();
					cr.setAttribute(prop, val);
				}
				if (cr != null)
					filteredSelect.setOptionCriteria(cr);
			}
		} catch (Throwable e) {
			// TODO: handle exception
		}

	}

	public static void showFile(String id) {
		String _url = "FileDownload.jsp?id=" + id;
		com.google.gwt.user.client.Window.open(_url, "yourWindowName",
				"location=yes,resizable=yes,scrollbars=yess,status=yes");
	}

	public static DataSource getDataSource(String name) {
		if (user_obj != null && user_obj.getDatasourceNames() != null
				&& user_obj.isDebug_ds()
				&& user_obj.getDatasourceNames().contains(name)) {
			name = ClSelection.DEV_PREFIX + name;
		}
		return DataSource.get(name);
	}

	public static native JSObject selectFeature(JSObject selectFeature,
			JSObject feature, int select) /*-{

		if (select == 1)
			selectFeature.selectControl.select(feature);
		else
			selectFeature.selectControl.unselect(feature);
	}-*/;

	public static String getCaption(int captionid) {
		return getCaption(captionid, "");
	}

	public static String getCaption(int captionid, String defaultVal) {
		String caption = captions.get(captionid);
		return caption == null ? defaultVal : caption;
	}

	public static Date getCurrentDate() {
		return new Date(currenttime);
	}

	public static boolean hasPermition(int permitionId) {
		return user_obj.getUser().getPermitionIds().contains(permitionId);
	}

	public static boolean hasPermition(String permitionname) {
		return user_obj.getUser().getPermitionNames().contains(permitionname);
	}

	public static void refreshServerTime() {
		docFlowService.getServerTime(new AsyncCallback<Long>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(Long result) {
				currenttime = result;
			}
		});
	}

	public DocFlow() {
		docFlow = this;
	}

	private void compileJsp(String jsp) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, jsp);
		try {
			builder.sendRequest(null, new RequestCallback() {

				@Override
				public void onError(Request request, Throwable exception) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onResponseReceived(Request request,
						Response response) {
					// TODO Auto-generated method stub

				}
			});
		} catch (RequestException e) {

			e.printStackTrace();
		}
	}

	private void createLayout(Layout mainLayout, int system) {
		if (system <= S_HR) {
			NavigationSectionStack navigationPanel = new NavigationSectionStack(
					system);
			navigationPanel.setWidth("15%");
			navigationPanel.setShowResizeBar(true);
			mainLayout.addMember(navigationPanel);
			card.setWidth("85%");
			if (system < S_HR) {
				DocFlowSectionStack pDocFlow = new DocFlowSectionStack();
				card.addCard(CardLayoutCanvas.DOCFLOW_PANEL, pDocFlow);

				card.addCard(CardLayoutCanvas.LIVE_PANEL, new PBankLive());
				if (DocFlow.hasPermition(PermissionNames.READER_LIST_CHANGE))
					card.addCard(CardLayoutCanvas.READER_LIST_PANEL,
							new PReaderList());
				card.addCard(CardLayoutCanvas.JOB_POSSITION_PANEL,
						new PJob_Position());

				card.addCard(CardLayoutCanvas.ZONECHANGE_PANEL,
						new PZoneChange());

				card.addCard(CardLayoutCanvas.COEF_PANEL, new PCoeficient());
				card.showCard(CardLayoutCanvas.DOCFLOW_PANEL);

			}
			if (system == S_HR) {
				card.addCard(CardLayoutCanvas.DEPARTMENT_PANEL,
						new PStructure());
				card.showCard(CardLayoutCanvas.DEPARTMENT_PANEL);
			}
			mainLayout.addMember(card);
		}
		if (system == S_USER_MANAGER) {
			PUserManager um = new PUserManager();
			um.refresh();
			um.setWidth100();
			um.setHeight100();
			mainLayout.addMember(um);
		}

		if (system == S_GASMONITOR) {
			PMonitor um = new PMonitor();
			um.setWidth100();
			um.setHeight100();
			mainLayout.addMember(um);
		}

		if (system == S_PLOMBS) {
			PPlomb um = new PPlomb();
			um.setWidth100();
			um.setHeight100();
			mainLayout.addMember(um);
		}
		if (system == S_CORECTOR) {
			PCorector mb = new PCorector();
			mb.setWidth100();
			mb.setHeight100();
			mainLayout.addMember(mb);
		}

	}

	@Override
	public void onLogOn(LogOnEvent logOnEvent) {

		final WLoginForm loginForm = logOnEvent.getLoginForm();
		language_id = logOnEvent.getLanguage();
		system_id = logOnEvent.getSystem();
		user_name = logOnEvent.getUsername().trim();
		docFlowService.loginUser(logOnEvent.getUsername().trim(),
				logOnEvent.getPassword(), logOnEvent.getLanguage(), system_id,
				new AsyncCallback<UserObject>() {

					@Override
					public void onFailure(Throwable caught) {
						loginForm.setError(caught, WLoginForm.ERRFIELD_UN);
					}

					@Override
					public void onSuccess(UserObject result) {

						currenttime = result.getServerTime();
						user_obj = result;
						user_id = user_obj.getUser().getUser_id();
						system_id = result.getInitial_system();
						JavascriptInjector.inject(user_obj.getJavascript());
						JavascriptInjector
								.injectPath("sc/DataSourceLoader?dataSource="
										+ user_obj.getFullDS());
						if (system_id == S_USER_MANAGER
								&& !DocFlow
										.hasPermition(PermissionNames.CAN_VIEW_USRMANAGER)) {
							loginForm
									.setError(
											new Exception(
													"You do not have usermanager privilegies!!!!"),
											WLoginForm.ERRFIELD_SYSTEM);
							return;

						}

						if (system_id == S_HR
								&& !DocFlow.hasPermition(PermissionNames.HR)) {
							loginForm.setError(new Exception(
									"You do not have HR privilegies!!!!"),
									WLoginForm.ERRFIELD_SYSTEM);
							return;

						}

						if (system_id == S_CORECTOR
								&& !DocFlow
										.hasPermition(PermissionNames.CORECTOR)) {
							loginForm.setError(new Exception(
									"You do not have MODBAS privilegies!!!!"),
									WLoginForm.ERRFIELD_SYSTEM);
							return;

						}

						if (system_id == S_CORECTOR
								&& !DocFlow
										.hasPermition(PermissionNames.CORECTOR)) {
							loginForm.setError(new Exception(
									"You do not have MODBAS privilegies!!!!"),
									WLoginForm.ERRFIELD_SYSTEM);
							return;

						}

						if (system_id == S_GASMONITOR
								&& !DocFlow
										.hasPermition(PermissionNames.GASMONITOR)) {
							loginForm
									.setError(
											new Exception(
													"You do not have GASMONITOR privilegies!!!!"),
											WLoginForm.ERRFIELD_SYSTEM);
							return;

						}

						if (system_id == S_MAP
								&& !DocFlow.hasPermition(PermissionNames.MAP)) {
							loginForm.setError(new Exception(
									"You do not have MAP privilegies!!!!"),
									WLoginForm.ERRFIELD_SYSTEM);
							return;

						}
						loginForm.done();
						ArrayList<ClSelectionItem> captions = user_obj
								.getCaptions();

						Collections.sort(captions,
								new Comparator<ClSelectionItem>() {
									public int compare(ClSelectionItem o1,
											ClSelectionItem o2) {
										ClSelectionItem p1 = (ClSelectionItem) o1;
										ClSelectionItem p2 = (ClSelectionItem) o2;
										return p1.getValue()
												.compareToIgnoreCase(
														p2.getValue());
									}

								});
						for (ClSelectionItem clSelectionItem : captions) {
							DocFlow.captions.put((int) clSelectionItem.getId(),
									clSelectionItem.getValue());
						}

						panelheight = aboutCanvas.getHeight();
						Layout mainLayout = system_id == S_MAP ? new GMapPanel()
								: new HLayout();
						mainLayout.setWidth100();
						mainLayout.setHeight100();

						mainLayout.setWidth100();
						mainLayout.setHeight100();
						mainLayout.setShowEdges(true);
						createLayout(mainLayout, system_id);
						aboutCanvas.destroy();
						// RootPanel.get().remove(aboutCanvas);
						mainLayout.draw();
						// RootPanel.get().add(mainLayout);
					}
				});

	}

	public void onModuleLoad() {
		setFunctions();
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			public void onUncaughtException(Throwable e) {
				e.printStackTrace();
			}
		});
		currenttime = new Date().getTime();
		for (int i = 0; i < 5; i++) {

			compileJsp("doctypetree.jsp?lang=0");
			compileJsp("reportgenerator.jsp");
			compileJsp("FileDownload.jsp");
			compileJsp("js.jsp");
			compileJsp("images/FileDownload.jsp");

		}
		aboutCanvas = new HLayout();
		aboutCanvas.setWidth100();
		aboutCanvas.setHeight100();
		aboutCanvas.setShowEdges(true);
		aboutCanvas.draw();
		// RootPanel.get().add(aboutCanvas);
		DocFlow.docFlowService.getTopType(ClSelection.T_LANGUAGE,
				new AsyncCallback<ArrayList<ClSelectionItem>>() {

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();

					}

					@Override
					public void onSuccess(ArrayList<ClSelectionItem> result) {

						for (ClSelectionItem clSelectionItem : result) {
							Language lang = new Language();
							lang.setId((int) clSelectionItem.getId());
							lang.setLanguage_name(clSelectionItem.getValue());
							langs.add(lang);
						}

						ArrayList<LSystem> systems = new ArrayList<LSystem>();
						systems.add(new LSystem(S_DOCFLOW, "DocFlow"));
						systems.add(new LSystem(S_CC_AND_ECCIDENT,
								"Service Centre"));
						systems.add(new LSystem(S_HR, "HR"));
						systems.add(new LSystem(S_MAP, "MAP"));
						systems.add(new LSystem(S_CORECTOR, "CORECTOR"));
						systems.add(new LSystem(S_PLOMBS, "PLOMBS"));
						systems.add(new LSystem(S_GASMONITOR, "GASMONITOR"));
						systems.add(new LSystem(S_USER_MANAGER, "User Manager"));
						WLoginForm wl = new WLoginForm(langs, systems,
								DocFlow.this);
						wl.show();
					}
				});
	}

}
