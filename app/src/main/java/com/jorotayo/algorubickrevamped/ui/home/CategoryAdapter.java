package com.jorotayo.algorubickrevamped.ui.home;

import static com.jorotayo.algorubickrevamped.ui.home.Fragment_NewAlgorithm.newInstance;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jorotayo.algorubickrevamped.ObjectBox;
import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Algorithm;
import com.jorotayo.algorubickrevamped.data.Algorithm_;
import com.jorotayo.algorubickrevamped.data.Category;
import com.jorotayo.algorubickrevamped.ui.timer.StatisticsRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends ArrayAdapter {
    private ArrayList<Category> categories;

    TextView category_spinner_label;
    ImageView dropdown_delete;
    private OnCategoryListener onCategoryListener;

    public CategoryAdapter(Context context, int textViewResourceId, ArrayList<Category> categories, OnCategoryListener categoryListener) {
        super(context, textViewResourceId, categories);
        this.categories = categories;
        this.onCategoryListener = categoryListener;
    }


    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int i) {
        return categories.get(i);
    }

    @Override
    public long getItemId(int i) {
        return categories.indexOf(categories.get(i));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_spinner_dropdown_item, parent, false);
        TextView category_spinner_label = rowView.findViewById(R.id.category_spinner_label);
        ImageView dropdown_delete = rowView.findViewById(R.id.dropdown_delete);
        dropdown_delete.setVisibility(View.GONE);
        category_spinner_label.setText(categories.get(position).category_name);
        return rowView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView, ViewGroup parent) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_spinner_dropdown_item, parent, false);
        category_spinner_label = row.findViewById(R.id.category_spinner_label);
        dropdown_delete = row.findViewById(R.id.dropdown_delete);

        Spinner parentSpinner = parent.findViewById(R.id.new_alg_category_spinner);

        category_spinner_label.setText(categories.get(position).category_name);
        category_spinner_label.setTag(categories.get(position).id);
        dropdown_delete.setTag(categories.get(position).id);
//       category_spinner_label.setOnClickListener(v -> parentSpinner.setSelection(0) );
       category_spinner_label.setOnClickListener(v -> onClick(v));
       /* dropdown_delete.setOnClickListener(v -> {
            selectedView = v;
            confirmDeleteDialog.show();
        });*/
        dropdown_delete.setOnClickListener(v -> onClick(v));
//        Toast.makeText(getContext(), categories.get(position).category_name + " deleted", Toast.LENGTH_SHORT).show());
        return row;

    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.category_spinner_label) {
            onCategoryListener.categoryLabelClick((Long) view.getTag());
        }else {
            onCategoryListener.categoryDeleteClick((Long) view.getTag());
        }
    }

    public interface OnCategoryListener {
        void categoryLabelClick(long position);

        void categoryDeleteClick(long position);
    }
}