package com.docflowdroid.comp;

import java.util.HashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.docflow.shared.common.FieldDefinition;
import com.docflowdroid.MyDynamicForm;
import com.docflowdroid.R;
import com.docflowdroid.common.comp.IFormItem;
import com.docflowdroid.comp.ds.ListGrid;
import com.docflowdroid.helper.Utils;

public abstract class FormItem extends LinearLayout implements IFormItem {

	protected TextView textView;
	protected FieldDefinition field;
	protected FormDefinitionPanel panel;
	protected RelativeLayout componentPanel;

	protected String dsName;
	protected String valueField;
	protected String displayField;
	protected String operationId;
	protected ListGrid pickerList;
	private Object initialValue;

	private ImageButton[] icons = null;

	public FormItem(Context context) {
		super(context);
		createComp(context);
	}

	private void createComp(Context context) {
		setLayoutParams(new LinearLayout.LayoutParams(Utils.getWidth(150),
				LinearLayout.LayoutParams.WRAP_CONTENT));
		textView = new TextView(context);
	}

	public FormItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		createComp(context);
	}

	public FormItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		createComp(context);
	}

	public void setPanel(FormDefinitionPanel panel) {
		this.panel = panel;
	}

	public abstract View getComponent();

	private View createComponentPanel() {
		componentPanel = new RelativeLayout(getContext());

		View v = getComponent();
		int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
		int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
		ViewGroup.LayoutParams p = v.getLayoutParams();

		if (p != null) {
			width = p.width;
			height = p.height == RelativeLayout.LayoutParams.MATCH_PARENT ? RelativeLayout.LayoutParams.WRAP_CONTENT
					: height;
		}

		componentPanel.setLayoutParams(new RelativeLayout.LayoutParams(width,
				height));

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				width, height);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		v.setLayoutParams(params);
		componentPanel.addView(v);
		return componentPanel;
	}

	public void createView(TitleOrientation orientation) {
		if (orientation.equals(TitleOrientation.TOP))
			setOrientation(LinearLayout.VERTICAL);
		else
			setOrientation(LinearLayout.HORIZONTAL);
		View firstView = needTitle() ? textView : null;
		View secondView = createComponentPanel();
		if (orientation.equals(TitleOrientation.RIGHT)) {
			View tempView = firstView;
			firstView = secondView;
			secondView = tempView;
		}
		addView(firstView);
		addView(secondView);
		if (field.isClearComboValue()) {
			final ImageButton arrase = new ImageButton(getContext());
			arrase.setImageResource(R.drawable.eraser);
			arrase.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					setValue(null);
					setDisplayValue(null);
				}
			});
			setIcons(arrase);
		}
		doAfterCreation();

	}

	@Override
	public void addView(View child) {
		if (child != null) {
			super.addView(child);
		}

	}

	public abstract void setValue(Object value);

	public abstract String getValue();

	public String getDisplayValue() {
		return null;
	}

	public void setDisplayValue(String value) {

	}

	public void doAfterCreation() {

	}

	public boolean needTitle() {
		return true;
	}

	public void setCanEdit(boolean b) {
		setEnabled(b);
	}

	public void setTitle(String title) {
		textView.setText(title + ":");
	}

	public void setWidth(String width) {
		getLayoutParams().width = MyDynamicForm.getLongValue(width);
		setLayoutParams(getLayoutParams());
		View comp = getComponent();
		if (comp == null)
			return;
		if (comp.getLayoutParams() == null)
			comp.setLayoutParams(new LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT));
		comp.getLayoutParams().width = getLayoutParams().width;
	}

	public void setMinimumHeight(String fieldHeight) {
		setMinimumHeight(MyDynamicForm.getLongValue(fieldHeight));
	}

	public void setDisabled(boolean b) {
		setEnabled(!b);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		disableEnableControls(enabled, this);
		if (icons != null && icons.length > 0) {
			for (ImageButton i : icons) {
				i.setEnabled(enabled);
			}
		}
	}

	private void disableEnableControls(boolean enable, ViewGroup vg) {
		for (int i = 0; i < vg.getChildCount(); i++) {
			View child = vg.getChildAt(i);
			if (child instanceof ViewGroup) {
				disableEnableControls(enable, (ViewGroup) child);
			} else {
				child.setEnabled(enable);
			}
		}
	}

	public void setValueMap(HashMap<Long, String> map) {

	}

	public void setOnItemSelectedListener(OnItemSelectedListener listener) {

	}

	public FieldDefinition getField() {
		return field;
	}

	public void setField(FieldDefinition field) {
		this.field = field;
	}

	public static Long getRowValueLong(Object val) {
		return val == null ? null : Long.valueOf(val.toString().trim());
	}

	public void setDataSource(String dsName, String valueField,
			String displayField, String operationId, ListGrid pickerList) {
		this.dsName = dsName;
		this.valueField = valueField;
		this.displayField = displayField;
		this.operationId = operationId;
		this.pickerList = pickerList;

	}

	public void setDataSource(String dsName, String valueField,
			String displayField, String operationId) {
		this.dsName = dsName;
		this.valueField = valueField;
		this.displayField = displayField;
		this.operationId = operationId;

	}

	public ImageButton[] getIcons() {
		return icons;
	}

	public void detectSize() {
		if (this.icons != null && this.icons.length > 0) {
			int width = 0;
			for (ImageButton img : this.icons) {
				if (width > 0)
					width += 2;
				width += img.getWidth();
			}
			this.setWidth((getWidth() + width) + "");
		}
	}

	public void setIcons(ImageButton... icons) {
		if (this.icons != null && this.icons.length > 0)
			for (ImageButton img : this.icons) {
				componentPanel.removeView(img);
			}
		this.icons = icons;
		if (this.icons != null && this.icons.length > 0) {
			int _id = 1000;
			ImageButton img = icons[icons.length - 1];
			img.setId(_id--);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
					RelativeLayout.TRUE);
			img.setLayoutParams(params);
			componentPanel.addView(img);
			for (int i = icons.length - 2; i >= 0; i--) {
				params = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				int id = img.getId();
				params.addRule(RelativeLayout.LEFT_OF, id);
				icons[i].setLayoutParams(params);
				icons[i].setId(_id--);
				img = icons[i];
				componentPanel.addView(img, params);
			}

		}
	}

	public void requestFocusSelf() {

	}

	@Override
	public boolean isDisabled() {
		return !isEnabled();
	}

	public abstract OnFocusChangeListener getOnFocusChangeListener();

	public abstract void setOnFocusChangeListener(OnFocusChangeListener newList);

	@Override
	public void setInitialValue(Object value) {
		this.initialValue = value;
	}

	@Override
	public Object getInitialValue() {
		return initialValue;
	}
}
