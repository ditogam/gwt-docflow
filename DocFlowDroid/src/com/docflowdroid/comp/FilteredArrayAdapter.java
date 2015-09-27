package com.docflowdroid.comp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

/**
 * Simplified custom filtered ArrayAdapter override keepObject with your test
 * for filtering
 * 
 * Based on gist https://gist.github.com/tobiasschuerg/3554252/raw/30634
 * bf9341311ac6ad6739ef094222fc5f07fa8/FilteredArrayAdapter.java by Tobias
 * Schürg
 * 
 * Created on 9/17/13.
 * 
 * @author mgod
 */

public class FilteredArrayAdapter<T> extends ArrayAdapter<T> {

	private List<T> originalObjects;
	private Filter filter;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            The current context.
	 * @param resource
	 *            The resource ID for a layout file containing a TextView to use
	 *            when instantiating views.
	 * @param objects
	 *            The objects to represent in the ListView.
	 */
	public FilteredArrayAdapter(Context context, int resource, T[] objects) {
		this(context, resource, 0, objects);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            The current context.
	 * @param resource
	 *            The resource ID for a layout file containing a layout to use
	 *            when instantiating views.
	 * @param textViewResourceId
	 *            The id of the TextView within the layout resource to be
	 *            populated
	 * @param objects
	 *            The objects to represent in the ListView.
	 */
	public FilteredArrayAdapter(Context context, int resource,
			int textViewResourceId, T[] objects) {
		this(context, resource, textViewResourceId, new ArrayList<T>(
				Arrays.asList(objects)));
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            The current context.
	 * @param resource
	 *            The resource ID for a layout file containing a TextView to use
	 *            when instantiating views.
	 * @param objects
	 *            The objects to represent in the ListView.
	 */
	@SuppressWarnings("unused")
	public FilteredArrayAdapter(Context context, int resource, List<T> objects) {
		this(context, resource, 0, objects);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            The current context.
	 * @param resource
	 *            The resource ID for a layout file containing a layout to use
	 *            when instantiating views.
	 * @param textViewResourceId
	 *            The id of the TextView within the layout resource to be
	 *            populated
	 * @param objects
	 *            The objects to represent in the ListView.
	 */
	public FilteredArrayAdapter(Context context, int resource,
			int textViewResourceId, List<T> objects) {
		super(context, resource, textViewResourceId, new ArrayList<T>(objects));
		this.originalObjects = objects;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void notifyDataSetChanged() {
		((AppFilter) getFilter()).setSourceObjects(this.originalObjects);
		super.notifyDataSetChanged();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void notifyDataSetInvalidated() {
		((AppFilter) getFilter()).setSourceObjects(this.originalObjects);
		super.notifyDataSetInvalidated();
	}

	@Override
	public Filter getFilter() {
		if (filter == null)
			filter = new AppFilter(originalObjects);
		return filter;
	}

	/**
	 * Class for filtering Adapter, relies on keepObject in FilteredArrayAdapter
	 * 
	 * based on gist by Tobias Schürg in turn inspired by inspired by Alxandr
	 * (http://stackoverflow.com/a/2726348/570168)
	 */
	private class AppFilter extends Filter {

		private ArrayList<T> sourceObjects;

		public AppFilter(List<T> objects) {
			setSourceObjects(objects);
		}

		public void setSourceObjects(List<T> objects) {
			synchronized (this) {
				sourceObjects = new ArrayList<T>(objects);
			}
		}

		@Override
		protected FilterResults performFiltering(CharSequence chars) {
			FilterResults result = new FilterResults();
			if (chars != null && chars.length() > 0) {
				String mask = chars.toString().toLowerCase();
				ArrayList<T> keptObjects = new ArrayList<T>();

				for (T object : sourceObjects) {
					if (object.toString().contains(mask))
						keptObjects.add(object);
				}
				result.count = keptObjects.size();
				result.values = keptObjects;
			} else {
				// add all objects
				result.values = sourceObjects;
				result.count = sourceObjects.size();
			}
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			clear();
			if (results.count > 0) {
				FilteredArrayAdapter.this.addAll((Collection) results.values);
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}
}