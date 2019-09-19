package org.nerdslot.Foundation.Helper;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;

import java.util.List;

public class MaterialSpinnerAdapter<T> extends ArrayAdapter<T> {
    private Filter filter = new SpinnerNoFilter();
    public List<T> items;

    public MaterialSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
        Log.v("log-tag", "Adapter created " + filter);
        items = objects;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    private class SpinnerNoFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults result = new FilterResults();
            result.values = items;
            result.count = items.size();
            return result;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            notifyDataSetChanged();
        }
    }
}
