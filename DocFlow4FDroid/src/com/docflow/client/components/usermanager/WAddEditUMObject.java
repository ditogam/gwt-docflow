package com.docflow.client.components.usermanager;

import java.util.ArrayList;
import java.util.HashMap;

import com.common.client.WindowResultObject;
import com.common.shared.ClSelectionItem;
import com.common.shared.model.UMObject;
import com.docflow.client.DocFlow;
import com.docflow.client.components.common.AddressComponent;
import com.docflow.shared.ClSelection;
import com.docflow.shared.common.BFUMObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.HLayout;

public class WAddEditUMObject extends Window {
	private static void showIt(final UMObject umObject,
			WindowResultObject resultObject,
			HashMap<Integer, ArrayList<ClSelectionItem>> addresses) {
		WAddEditUMObject w = new WAddEditUMObject(umObject, addresses,
				resultObject);
		w.show();
		w.tiName.selectValue();
		w.tiName.focusInItem();

	}

	public static void showWindow(final UMObject umObject,
			final WindowResultObject resultObject) {
		HashMap<Integer, ArrayList<ClSelectionItem>> addresses = null;
		if (umObject.getType() == UMObject.USER) {
			DocFlow.docFlowService
					.getUserAddress(
							0,
							0,
							(int) umObject.getIdVal(),
							new AsyncCallback<HashMap<Integer, ArrayList<ClSelectionItem>>>() {

								@Override
								public void onFailure(Throwable caught) {
									SC.say(caught.getMessage());

								}

								@Override
								public void onSuccess(
										HashMap<Integer, ArrayList<ClSelectionItem>> result) {
									showIt(umObject, resultObject, result);
								}
							});
		} else
			showIt(umObject, resultObject, addresses);
	}

	private TextItem tiName;
	private WindowResultObject resultObject;
	private UMObject umObject;
	private PasswordItem piPassword;
	private PasswordItem piConfirmPassword;
	private CheckboxItem cbiSetPassword;

	private SelectItem siRegion;

	private SelectItem siSubRegion;

	public WAddEditUMObject(UMObject umObject,
			HashMap<Integer, ArrayList<ClSelectionItem>> addresses,
			WindowResultObject resultObject) {
		super();
		setMembersMargin(5);
		setTitle("ფორმა");
		this.resultObject = resultObject;
		this.umObject = umObject;
		setWidth(400);

		tiName = new TextItem("_name", "სახელი");
		tiName.setWidth(250);
		tiName.setValue(umObject.getTextVal());
		tiName.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				// event.cancel();

			}
		});

		DynamicForm df = new DynamicForm();
		df.setAlign(Alignment.LEFT);

		df.setWidth100();

		if (umObject.getType() == UMObject.USER) {
			cbiSetPassword = new CheckboxItem("_setPassword",
					"პაროლის დაყენება");
			cbiSetPassword.setValue(true);
			piPassword = new PasswordItem("_password", "პაროლი");
			piConfirmPassword = new PasswordItem("_confpassword",
					"გაიმეორეთ პაროლი");

			cbiSetPassword.addChangedHandler(new ChangedHandler() {

				@Override
				public void onChanged(ChangedEvent event) {
					boolean disabled = !cbiSetPassword.getValueAsBoolean();
					piPassword.setDisabled(disabled);
					piConfirmPassword.setDisabled(disabled);

				}
			});
			AddressComponent ac = new AddressComponent(false, false,
					addresses.get(ClSelection.T_REGION));
			siRegion = ac.getSiRegion();
			siSubRegion = ac.getSiSubregion();
			ClSelectionItem addd = addresses.get(-1).get(0);
			if (addd.getParentId() > -1) {
				ArrayList<ClSelectionItem> subregions = addresses
						.get(ClSelection.T_SUBREGION);
				if (subregions != null) {
					siRegion.setValue("" + addd.getParentId());
					ac.setSelectItems(siSubRegion, subregions);
					if (addd.getId() > 0)
						siSubRegion.setValue("" + addd.getId());
				}
			}

			df.setItems(tiName, cbiSetPassword, piPassword, piConfirmPassword,
					siRegion, siSubRegion);
			setHeight(312);
		} else {
			df.setItems(tiName);
			setHeight(112);
		}
		df.setAutoHeight();
		this.addItem(df);
		HLayout hl = new HLayout();
		hl.setAlign(Alignment.RIGHT);
		IButton bSave = new IButton("Save", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				saveData();
			}

		});
		IButton bCancel = new IButton("Cancel");
		bCancel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				destroy();
			}
		});
		hl.addMember(bSave);
		hl.addMember(bCancel);
		hl.setWidth100();
		hl.setMembersMargin(10);
		hl.setHeight("30");
		Label l = new Label();
		l.setTitle("");
		l.setContents("");
		l.setWidth(0);
		hl.addMember(l);
		this.addItem(hl);

		this.setCanDragResize(true);
		this.setShowMinimizeButton(false);
		// this.setShowCloseButton(false);
		this.setIsModal(true);
		this.setShowFooter(true);
		// this.setShowModalMask(true);
		this.centerInPage();
	}

	private void saveData() {
		BFUMObject bfumo = new BFUMObject();
		bfumo.setType(umObject.getType());
		bfumo.setIdVal(umObject.getIdVal());

		String otext = null;
		try {
			otext = tiName.getValue().toString();
		} catch (Exception e) {
			// TODO: handle exception
		}

		if (otext == null || otext.length() == 0) {
			SC.say("გთხოვთ შეიყვანოთ ტექსტი!!!", new BooleanCallback() {
				@Override
				public void execute(Boolean value) {
					tiName.focusInItem();
				}
			});
			return;
		}

		if (umObject.getType() == UMObject.USER) {
			if (cbiSetPassword.getValueAsBoolean()) {
				Object objPwd = piPassword.getValue();
				if (objPwd == null || objPwd.toString().length() == 0) {
					SC.say("გთხოვთ შეიყვანოთ პაროლი!!!", new BooleanCallback() {
						@Override
						public void execute(Boolean value) {
							piPassword.focusInItem();
						}
					});
					return;
				}
				Object objConfPwd = piConfirmPassword.getValue();
				if (objConfPwd == null || objConfPwd.toString().length() == 0) {
					SC.say("გთხოვთ გაიმეორეთ პაროლი!!!", new BooleanCallback() {
						@Override
						public void execute(Boolean value) {
							piConfirmPassword.focusInItem();
						}
					});
					return;
				}
				if (!objPwd.toString().equals(objConfPwd.toString())) {
					SC.say("პაროლი არ  ემთხვევა!!!", new BooleanCallback() {
						@Override
						public void execute(Boolean value) {
							piConfirmPassword.focusInItem();
						}
					});
					return;
				}
				bfumo.setPwdApplyed(true);
				bfumo.setPwd(objPwd.toString());
			}
		}

		bfumo.setTextVal(otext);
		int regionid = -1;
		try {
			regionid = Integer.parseInt(siRegion.getValue().toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		int subregionid = -1;
		try {
			subregionid = Integer.parseInt(siSubRegion.getValue().toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		DocFlow.docFlowService.saveUsermanagerObject(bfumo, regionid,
				subregionid, new AsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						setResult(caught);
					}

					@Override
					public void onSuccess(Integer result) {
						umObject.setIdVal(result.longValue());
						setResult(null);
					}
				});

	}

	private void setResult(Throwable caught) {
		if (caught != null) {
			SC.say("შეცდომა", caught.getMessage(), new BooleanCallback() {
				@Override
				public void execute(Boolean value) {
					tiName.focusInItem();
				}
			});
			return;
		} else {
			resultObject.setResult(umObject);
			destroy();
		}
	}
}
