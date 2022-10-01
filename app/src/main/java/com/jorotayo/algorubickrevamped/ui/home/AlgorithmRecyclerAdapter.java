package com.jorotayo.algorubickrevamped.ui.home;

import static android.content.ContentValues.TAG;
import static com.jorotayo.algorubickrevamped.ui.home.AlgorithmHomeFragment.actionMode;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.Constraints;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Algorithm;
import com.jorotayo.algorubickrevamped.utils.UtilMethods;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmRecyclerAdapter extends Adapter<AlgorithmRecyclerAdapter.ViewHolder> implements Filterable {

    private final Context ctx;
    private final ArrayList<Algorithm> mAlgorithms;
    private final ArrayList<Algorithm> mAlgorithmsAll;

    Filter filter = new Filter() {
        /* Access modifiers changed, original: protected */
        public FilterResults performFiltering(CharSequence text) {
            ArrayList<Algorithm> filteredAlgorithms = new ArrayList();
            if (text == null || text.length() == 0) {
                filteredAlgorithms.addAll(mAlgorithmsAll);
            } else {
                String filterPattern = text.toString().toLowerCase().trim();
                for (Algorithm item : mAlgorithmsAll) {
                    String algorithm = item.alg;
                    String description = item.alg_description.toLowerCase();
                    String name = item.alg_name.toLowerCase();
                    if (algorithm.contains(filterPattern) || description.contains(filterPattern) || name.contains(filterPattern)) {
                        filteredAlgorithms.add(item);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredAlgorithms;
            return filterResults;
        }

        /* Access modifiers changed, original: protected */
        public void publishResults(CharSequence charSequence, FilterResults filterResults) {
            AlgorithmRecyclerAdapter.this.mAlgorithms.clear();
            AlgorithmRecyclerAdapter.this.mAlgorithms.addAll((ArrayList) filterResults.values);
            AlgorithmRecyclerAdapter.this.notifyDataSetChanged();
        }
    };
    SparseBooleanArray selected_items;
    private int current_selected = -1;
    private OnAlgorithmListener mOnAlgorithmListener;

    public AlgorithmRecyclerAdapter(ArrayList<Algorithm> mAlgorithms, OnAlgorithmListener mOnAlgorithmListener, Context ctx) {
        this.mAlgorithms = mAlgorithms;
        this.mAlgorithmsAll = new ArrayList(mAlgorithms);
        this.mOnAlgorithmListener = mOnAlgorithmListener;
        this.selected_items = new SparseBooleanArray();
        this.ctx = ctx;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_algorithm, parent, false), this.mOnAlgorithmListener);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        Algorithm algorithmItem = this.mAlgorithms.get(position);
        holder.algorithmName.setText(algorithmItem.getAlg_name());
        holder.algorithm.setText(algorithmItem.getAlg());
        holder.algorithmCategory.setText(algorithmItem.getCategory());
        String practicedCorrect = String.format("Correct/Practiced: %s / %s", algorithmItem.getPracticed_correctly_int(), algorithmItem.getPracticed_number_int());
        holder.practicedCorrect.setText(practicedCorrect);
        UtilMethods.LoadAlgorithmIcon(ctx.getApplicationContext(), holder.algorithmIcon, algorithmItem);
        holder.favourite_checkbox.setChecked(algorithmItem.isFavourite_alg());
        holder.learnt_checkbox.setChecked(algorithmItem.isLearnt());
        toggleSelectedRow(holder, position);
    }

    private void toggleSelectedRow(ViewHolder holder, int position) {
        if (this.selected_items.get(position, false)) {
            holder.algorithmCard.setBackgroundResource(R.color.colorPrimaryDark);
            holder.algorithmName.setTextColor(Color.rgb(255, 255, 255));
            holder.algorithmCategory.setTextColor(Color.rgb(255, 255, 255));
            holder.algorithm.setTextColor(Color.rgb(255, 255, 255));
            holder.practicedCorrect.setTextColor(Color.rgb(255, 255, 255));
            holder.algorithm_item_checks.setVisibility(View.GONE);
            holder.algorithm_selected_checks.setVisibility(View.VISIBLE);
            holder.algorithm_selected.setChecked(true);
            return;
        }
        holder.algorithmCard.setBackgroundResource(R.color.white);
        holder.algorithmName.setTextColor(Color.rgb(0, 0, 0));
        holder.algorithmCategory.setTextColor(Color.rgb(0, 0, 0));
        holder.algorithm.setTextColor(Color.rgb(0, 0, 0));
        holder.practicedCorrect.setTextColor(Color.rgb(0, 0, 0));
        holder.algorithm_item_checks.setVisibility(View.VISIBLE);
        holder.algorithm_selected_checks.setVisibility(View.GONE);
        holder.algorithm_selected.setChecked(false);

    }

    public void clearSelected() {
        this.selected_items.clear();
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return this.mAlgorithms.size();
    }

    public int getSelectedItemCount() {
        return this.selected_items.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList(this.selected_items.size());
        for (int i = 0; i < this.selected_items.size(); i++) {
            items.add(this.selected_items.keyAt(i));
        }
        return items;
    }

    public Filter getFilter() {
        return this.filter;
    }

    public void setFilter(ArrayList<Algorithm> algorithmArrayList) {
        this.mAlgorithms.clear();
        this.mAlgorithms.addAll(algorithmArrayList);
        notifyDataSetChanged();
    }

    public interface OnAlgorithmListener {
        void onAlgorithmClick(int i);

        void onAlgorithmFavouriteClick(int i, View view);

        void onAlgorithmImageClick(int i, View view);

        void onAlgorithmLearntClick(int i, View view);

        void onAlgorithmLongClick(int i, View view);
    }

    public class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder implements OnClickListener, OnLongClickListener {
        LinearLayout algorithmCard, algorithm_item_checks, algorithm_selected_checks;
        ImageView algorithmIcon;
        TextView algorithmName, algorithmCategory, algorithm, practicedCorrect;
        CheckBox algorithm_selected, favourite_checkbox, learnt_checkbox;


        public ViewHolder(View view, OnAlgorithmListener onAlgorithmListener) {
            super(view);
            this.algorithmName = view.findViewById(R.id.algorithm_item_name_textview);
            this.algorithm = view.findViewById(R.id.algorithm_item_algorithm);
            this.algorithmCategory = view.findViewById(R.id.algorithm_category_textview);
            this.practicedCorrect = view.findViewById(R.id.algorithm_item_practiced_vs_correct);
            this.algorithmCard = view.findViewById(R.id.algorithm_card);
            this.algorithmIcon = view.findViewById(R.id.algorithm_icon);
            this.algorithm_selected = view.findViewById(R.id.algorithm_selected);
            this.algorithm_item_checks = view.findViewById(R.id.algorithm_item_checks);
            this.algorithm_selected_checks = view.findViewById(R.id.algorithm_selected_checks);
            this.favourite_checkbox = view.findViewById(R.id.favourite_checkbox);
            this.learnt_checkbox = view.findViewById(R.id.learnt_checkbox);
            AlgorithmRecyclerAdapter.this.mOnAlgorithmListener = onAlgorithmListener;
            this.favourite_checkbox.setOnClickListener(this);
            this.learnt_checkbox.setOnClickListener(this);
            this.algorithmCard.setOnClickListener(this);
            this.algorithmCard.setOnLongClickListener(this);
            this.algorithmIcon.setOnClickListener(this);
        }

        public void onClick(View view) {
            int id = view.getId();
            if (actionMode != null) actionMode.finish();
            switch (id) {
                case R.id.algorithm_card /*2131361910*/:
                    AlgorithmRecyclerAdapter.this.mOnAlgorithmListener.onAlgorithmClick(getAdapterPosition());
                    return;
                case R.id.algorithm_icon /*2131361912*/:
                    AlgorithmRecyclerAdapter.this.mOnAlgorithmListener.onAlgorithmImageClick(getAdapterPosition(), view);
                    return;
                case R.id.favourite_checkbox /*2131362073*/:
                    AlgorithmRecyclerAdapter.this.mOnAlgorithmListener.onAlgorithmFavouriteClick(getAdapterPosition(), view);
                    return;
                case R.id.learnt_checkbox /*2131362144*/:
                    AlgorithmRecyclerAdapter.this.mOnAlgorithmListener.onAlgorithmLearntClick(getAdapterPosition(), view);
                    return;
                default:
            }
        }

        public boolean onLongClick(View v) {
            AlgorithmRecyclerAdapter.this.current_selected = getAdapterPosition();
            if (AlgorithmRecyclerAdapter.this.selected_items.get(current_selected, false)) {
                AlgorithmRecyclerAdapter.this.selected_items.delete(getAdapterPosition());
            } else {
                AlgorithmRecyclerAdapter.this.selected_items.put(getAdapterPosition(), true);
            }
            AlgorithmRecyclerAdapter.this.notifyItemChanged(getAdapterPosition());
            AlgorithmRecyclerAdapter.this.mOnAlgorithmListener.onAlgorithmLongClick(getAdapterPosition(), v);
            return true;
        }
    }
}
