package com.docflowdroid.comp.ds;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.docflowdroid.R;

public class ListGridView {
	private LinearLayout lh_header_view;
	private ListView lh_lv_view;
	public ListGrid grid;
	private ListViewAdapter adapter;
	private View view;

	public static final Long ROWNUM_WIDTH = 150L;

	private int[] buttons;

	ListGridView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		view = inflater.inflate(R.layout.lg_view, null);
		lh_header_view = (LinearLayout) view.findViewById(R.id.lh_header_view);
		lh_lv_view = (ListView) view.findViewById(R.id.lh_lv_view);
		buttons = new int[] { R.id.lgbn_first, R.id.lgbn_prev, R.id.lgbn_next,
				R.id.lgbn_last };
		setNavigationData(0, 0);
		((ImageButton) view.findViewById(R.id.lgbn_go))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						int cp = 0;
						try {
							cp = Integer.parseInt(et.getText().toString()
									.trim());
						} catch (Exception e) {
							// TODO: handle exception
						}
						if (!(cp >= 1 && cp <= pageCount))
							cp = currentPage;
						grid.gotoPage(cp);
					}
				});

		OnClickListener l = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.lgbn_first)
					grid.fetchFirst();
				if (v.getId() == R.id.lgbn_prev)
					grid.fetchPreviews();
				if (v.getId() == R.id.lgbn_next)
					grid.fetchNext();
				if (v.getId() == R.id.lgbn_last)
					grid.gotoPage(pageCount);
			}
		};
		for (int bid : buttons) {
			((ImageButton) view.findViewById(bid)).setOnClickListener(l);
		}

	}

	private void setButtonDisabled(boolean d, int id) {
		((ImageButton) view.findViewById(id)).setEnabled(!d);
	}

	private int pageCount;
	private int currentPage;
	private EditText et;

	public void setNavigationData(long start_row, long total_rows) {
		pageCount = (int) Math.ceil((double) total_rows
				/ (double) ListGrid.FETCH_LENGTH);
		String pgCount = "/" + pageCount + "(" + total_rows + ")";
		currentPage = (int) Math.floor((double) start_row
				/ (double) ListGrid.FETCH_LENGTH) + 1;
		et = ((EditText) view.findViewById(R.id.lgbc_pgn));
		et.setText(currentPage + "");
		et.setFilters(new InputFilter[] { new InputFilterMinMax("1", ""
				+ pageCount) });
		((TextView) view.findViewById(R.id.lgbt_data)).setText(pgCount);

		setButtonDisabled(total_rows == 0, R.id.lgbn_go);

		setButtonDisabled(currentPage == 1 || total_rows == 0, buttons[0]);
		setButtonDisabled(currentPage == 1 || total_rows == 0, buttons[1]);

		setButtonDisabled(pageCount == currentPage || total_rows == 0,
				buttons[2]);
		setButtonDisabled(pageCount == currentPage || total_rows == 0,
				buttons[3]);

	}

	public void setGrid(ListGrid grid) {
		this.grid = grid;
		adapter = new ListViewAdapter(view.getContext(), grid);
		ListGridField[] fields = grid.getFields();
		boolean showFilter = false;
		for (ListGridField lg : fields) {
			if (lg.getShowFilter()) {
				showFilter = true;
				break;
			}
		}

		lh_lv_view.setAdapter(adapter);
		if (grid.isShowRowNum()) {
			ListGridField lg = new ListGridField("_no", "N", ROWNUM_WIDTH);
			lg.setRowNum(true);
			addHeader(lg, showFilter);
		}
		for (ListGridField lg : fields) {
			if (lg.isFieldVisible()) {
				addHeader(lg, showFilter);
			}
		}

	}

	private void addHeader(ListGridField lg, boolean showFilterHeader) {
		lg.creatViewTitle(view.getContext(), showFilterHeader);
		View v = lg.getFieldView().getMyview();
		lh_header_view.addView(v);
	}

	public void setData(List<Map<String, Object>> data, boolean isnewdata) {
		ArrayList<ListGridRecordHelper> arrayList = new ArrayList<ListGridRecordHelper>();
		adapter.clear();
		for (int i = 0; i < data.size(); i++) {
			arrayList.add(new ListGridRecordHelper((int) grid.getStartRow() + i
					+ 1, grid.isShowRowNum(), data.get(i), grid.getFields()));
		}
		adapter.setData(arrayList);
		adapter.notifyDataSetChanged();
		setNavigationData(grid.getStartRow(), grid.getTotalRows());
	}

	public View getView() {
		return view;
	}

}
