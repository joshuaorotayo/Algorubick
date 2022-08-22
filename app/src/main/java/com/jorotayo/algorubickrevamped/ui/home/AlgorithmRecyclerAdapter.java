package com.jorotayo.algorubickrevamped.ui.home;

import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Constraints;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Algorithm;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmRecyclerAdapter extends Adapter<AlgorithmRecyclerAdapter.ViewHolder> implements Filterable {

    private final Context ctx;
    private final ArrayList<Algorithm> mAlgorithms;
    private final ArrayList<Algorithm> mAlgorithmsAll;
    private final ArrayList<Integer> selectedPos = new ArrayList();

    final Filter filter = new Filter() {
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
    final SparseBooleanArray selected_items;
    private int current_selected = -1;
    private OnAlgorithmListener mOnAlgorithmListener;

    public AlgorithmRecyclerAdapter(ArrayList<Algorithm> mAlgorithms, OnAlgorithmListener mOnAlgorithmListener, Context ctx) {
        this.mAlgorithms = mAlgorithms;
        this.mAlgorithmsAll = new ArrayList(mAlgorithms);
        this.mOnAlgorithmListener = mOnAlgorithmListener;
        this.selected_items = new SparseBooleanArray();
        this.ctx = ctx;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_algorithm, parent, false), this.mOnAlgorithmListener);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        Algorithm algorithmItem = this.mAlgorithms.get(position);
        holder.algorithmName.setText(algorithmItem.getAlg_name());
        holder.algorithm.setText(algorithmItem.getAlg());
        holder.algorithmCategory.setText(algorithmItem.getCategory());
        TextView textView = holder.numberCorrect;
        String stringBuilder = algorithmItem.getPracticed_correctly_int() +
                " / ";
        textView.setText(stringBuilder);
        holder.numberPracticed.setText(String.valueOf(algorithmItem.getPracticed_number_int()));
        setAlgorithmIcon(holder, algorithmItem);
        holder.favourite_checkbox.setChecked(algorithmItem.isFavourite_alg());
        holder.learnt_checkbox.setChecked(algorithmItem.isLearnt());
        toggleCheckedIcon(holder, position);
    }

    private void setAlgorithmIcon(ViewHolder holder, Algorithm algorithmItem) {
        String s = "";
        if (algorithmItem.getAlgorithm_icon() != null) {
            String str = "";
            if (!algorithmItem.getAlgorithm_icon().equals(str)) {
                holder.algorithmIcon.setImageResource(this.ctx.getResources().getIdentifier(algorithmItem.getAlgorithm_icon().replace("R.drawable.", str), "drawable", this.ctx.getPackageName()));
                return;
            }
        }
        holder.algorithmIcon.setImageResource(R.drawable.cfop);
    }

    private void toggleCheckedIcon(ViewHolder holder, int position) {
        if (this.selected_items.get(position, false)) {
            holder.algorithmCard.setBackgroundResource(R.color.colorPrimaryDark);
            holder.algorithm_selected.setVisibility(View.VISIBLE);
            holder.algorithm_selected.setChecked(true);
            return;
        }
        holder.algorithm_selected.setVisibility(View.GONE);
        holder.algorithmCard.setBackgroundResource(R.color.white);
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
        final TextView algorithm;
        final LinearLayout algorithmCard;
        final TextView algorithmCategory;
        final ImageView algorithmIcon;
        final TextView algorithmName;
        final CheckBox algorithm_selected;
        final CheckBox favourite_checkbox;
        final CheckBox learnt_checkbox;
        final TextView numberCorrect;
        final TextView numberPracticed;

        public ViewHolder(View view, OnAlgorithmListener onAlgorithmListener) {
            super(view);
            this.algorithmName = view.findViewById(R.id.algorithm_item_name_textview);
            this.algorithm = view.findViewById(R.id.algorithm_item_algorithm);
            this.algorithmCategory = view.findViewById(R.id.algorithm_category_textview);
            this.numberCorrect = view.findViewById(R.id.algorithm_item_correct);
            this.numberPracticed = view.findViewById(R.id.algorithm_item_practiced);
            this.algorithmCard = view.findViewById(R.id.algorithm_card);
            this.algorithmIcon = view.findViewById(R.id.algorithm_icon);
            this.algorithm_selected = view.findViewById(R.id.algorithm_selected);
            AlgorithmRecyclerAdapter.this.mOnAlgorithmListener = onAlgorithmListener;
            this.favourite_checkbox = view.findViewById(R.id.favourite_checkbox);
            this.learnt_checkbox = view.findViewById(R.id.learnt_checkbox);
            this.favourite_checkbox.setOnClickListener(this);
            this.learnt_checkbox.setOnClickListener(this);
            this.algorithmCard.setOnClickListener(this);
            this.algorithmCard.setOnLongClickListener(this);
            this.algorithmIcon.setOnClickListener(this);
        }

        public void onClick(View view) {
            int id = view.getId();
            String str = "onLearnClick: ";
            String str2 = Constraints.TAG;
            StringBuilder stringBuilder;
            switch (id) {
                case R.id.algorithm_card /*2131361910*/:
                    AlgorithmRecyclerAdapter.this.mOnAlgorithmListener.onAlgorithmClick(getAdapterPosition());
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("onClick: ");
                    stringBuilder.append(getAdapterPosition());
                    Log.d(str2, stringBuilder.toString());
                    return;
                case R.id.algorithm_icon /*2131361912*/:
                    AlgorithmRecyclerAdapter.this.mOnAlgorithmListener.onAlgorithmImageClick(getAdapterPosition(), view);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("onImageClick: ");
                    stringBuilder.append(getAdapterPosition());
                    Log.d(str2, stringBuilder.toString());
                    return;
                case R.id.favourite_checkbox /*2131362073*/:
                    AlgorithmRecyclerAdapter.this.mOnAlgorithmListener.onAlgorithmFavouriteClick(getAdapterPosition(), view);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str);
                    stringBuilder.append(getAdapterPosition());
                    Log.d(str2, stringBuilder.toString());
                    return;
                case R.id.learnt_checkbox /*2131362144*/:
                    AlgorithmRecyclerAdapter.this.mOnAlgorithmListener.onAlgorithmLearntClick(getAdapterPosition(), view);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str);
                    stringBuilder.append(getAdapterPosition());
                    Log.d(str2, stringBuilder.toString());
                    return;
                default:
            }
        }

        public boolean onLongClick(View v) {
            AlgorithmRecyclerAdapter.this.current_selected = getAdapterPosition();
            if (AlgorithmRecyclerAdapter.this.selected_items.get(getAdapterPosition(), false)) {
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
