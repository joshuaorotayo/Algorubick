package com.jorotayo.algorubickrevamped.ui.timer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;

import com.jorotayo.algorubickrevamped.R;

public class CubeSizeAdapter extends ArrayAdapter {
    private ArrayList<String> cubeSize;
    private TextView cubeSize_spinner_label;
    private ImageView dropdown_delete, dropdown_add;
    private OnCubeSizeListener onCubeSizeListener;

    public CubeSizeAdapter(Context context, int textViewResourceId, ArrayList<String> cubeSize, OnCubeSizeListener cubeSizeListener) {
        super(context, textViewResourceId, cubeSize);
        this.cubeSize = cubeSize;
        this.onCubeSizeListener = cubeSizeListener;
    }

    @Override
    public int getCount() {
        return cubeSize.size();
    }

    @Override
    public Object getItem(int i) {
        return cubeSize.get(i);
    }

    @Override
    public long getItemId(int i) {
        return cubeSize.indexOf(cubeSize.get(i));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cube_size_spinner_dropdown_item, parent, false);
        TextView cubeSize_spinner_label = rowView.findViewById(R.id.cube_size_spinner_label);
        ImageView dropdown_delete = rowView.findViewById(R.id.dropdown_delete);
        dropdown_delete.setVisibility(View.GONE);
        cubeSize_spinner_label.setText(cubeSize.get(position));
        return rowView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView, ViewGroup parent) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.cube_size_spinner_dropdown_item, parent, false);
        cubeSize_spinner_label = row.findViewById(R.id.cube_size_spinner_label);
        dropdown_delete = row.findViewById(R.id.dropdown_delete);
        Spinner parentSpinner = parent.findViewById(R.id.cube_size_spinner);
        cubeSize_spinner_label.setText(cubeSize.get(position));
        cubeSize_spinner_label.setTag(position);
        dropdown_delete.setTag(position);
        cubeSize_spinner_label.setOnClickListener(v -> onClick(v));
        dropdown_delete.setOnClickListener(v -> onClick(v));
        if(position == cubeSize.size()-1){
            dropdown_delete.setVisibility(View.GONE);
        }
        if(cubeSize.size() <= 2){
            dropdown_delete.setVisibility(View.GONE);
            dropdown_delete.setEnabled(false);
        }
        return row;
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.cube_size_spinner_label) {
            onCubeSizeListener.cubeSizeLabelClick(Integer.parseInt(view.getTag().toString()));
        } else {
            onCubeSizeListener.cubeSizeDeleteClick(Integer.parseInt(view.getTag().toString()));
        }
    }

    public interface OnCubeSizeListener {
        void cubeSizeLabelClick(int position);

        void cubeSizeDeleteClick(int position);
    }
}