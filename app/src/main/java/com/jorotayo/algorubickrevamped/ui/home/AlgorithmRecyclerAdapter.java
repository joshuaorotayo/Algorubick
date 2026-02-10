package com.jorotayo.algorubickrevamped.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Algorithm;
import com.jorotayo.algorubickrevamped.utils.UtilMethods;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmRecyclerAdapter
        extends RecyclerView.Adapter<AlgorithmRecyclerAdapter.ViewHolder> {

    /* ---------- callbacks ---------- */

    public interface OnItemClick {
        void onClick(int position);
    }

    public interface OnItemViewClick {
        void onClick(int position, View view);
    }

    public interface OnItemLongClick {
        void onLongClick(int position, View view);
    }

    /* ---------- fields ---------- */

    private final Context ctx;
    private final ArrayList<Algorithm> algorithms;
    private final ArrayList<Algorithm> allAlgorithms;

    private final OnItemClick onCardClick;
    private final OnItemViewClick onImageClick;
    private final OnItemViewClick onFavouriteClick;
    private final OnItemViewClick onLearntClick;
    private final OnItemLongClick onLongClick;

    /* selection */
    private final ArrayList<Integer> selectedItems = new ArrayList<>();

    /* ---------- constructor ---------- */

    public AlgorithmRecyclerAdapter(
            ArrayList<Algorithm> algorithms,
            Context ctx,
            OnItemClick onCardClick,
            OnItemViewClick onImageClick,
            OnItemViewClick onFavouriteClick,
            OnItemViewClick onLearntClick,
            OnItemLongClick onLongClick
    ) {
        this.algorithms = algorithms;
        this.allAlgorithms = new ArrayList<>(algorithms);
        this.ctx = ctx;
        this.onCardClick = onCardClick;
        this.onImageClick = onImageClick;
        this.onFavouriteClick = onFavouriteClick;
        this.onLearntClick = onLearntClick;
        this.onLongClick = onLongClick;
    }

    /* ---------- adapter ---------- */

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_algorithm, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Algorithm alg = algorithms.get(position);

        holder.algorithmName.setText(alg.getAlg_name());
        holder.algorithm.setText(alg.getAlg());
        holder.algorithmCategory.setText(alg.getCategory());

        holder.practicedCorrect.setText(
                "Correct/Practiced: " +
                        alg.getPracticed_correctly_int() + " / " +
                        alg.getPracticed_number_int()
        );

        holder.favourite_checkbox.setChecked(alg.isFavourite_alg());
        holder.learnt_checkbox.setChecked(alg.isLearnt());

        UtilMethods.LoadAlgorithmIcon(ctx, holder.algorithmIcon, alg);

        applySelectionState(holder, position);
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        Glide.with(holder.itemView).clear(holder.algorithmIcon);
    }

    @Override
    public int getItemCount() {
        return algorithms.size();
    }

    /* ---------- selection ---------- */

    private void applySelectionState(ViewHolder h, int position) {
        boolean selected = selectedItems.contains(position);

        if (selected) {
            h.algorithmCard.setBackgroundResource(R.color.colorPrimary);
            setTextColor(h, Color.WHITE);
            h.algorithm_item_checks.setVisibility(View.GONE);
            h.algorithm_selected_checks.setVisibility(View.VISIBLE);
            h.algorithm_selected.setChecked(true);
        } else {
            h.algorithmCard.setBackgroundResource(R.color.white);
            setTextColor(h, Color.BLACK);
            h.algorithm_item_checks.setVisibility(View.VISIBLE);
            h.algorithm_selected_checks.setVisibility(View.GONE);
            h.algorithm_selected.setChecked(false);
        }
    }

    private void setTextColor(ViewHolder h, int color) {
        h.algorithmName.setTextColor(color);
        h.algorithmCategory.setTextColor(color);
        h.algorithm.setTextColor(color);
        h.practicedCorrect.setTextColor(color);
    }

    public void toggleSelection(int position) {
        if (selectedItems.contains(position)) {
            selectedItems.remove((Integer) position);
        } else {
            selectedItems.add(position);
        }
        notifyItemChanged(position);
    }

    public void clearSelection() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        return new ArrayList<>(selectedItems);
    }

    public void setFilter(ArrayList<Algorithm> newList) {
        algorithms.clear();
        algorithms.addAll(newList);
        notifyDataSetChanged();
    }

    /* ---------- ViewHolder ---------- */

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout algorithmCard, algorithm_item_checks, algorithm_selected_checks;
        ImageView algorithmIcon;
        TextView algorithmName, algorithmCategory, algorithm, practicedCorrect;
        CheckBox algorithm_selected, favourite_checkbox, learnt_checkbox;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            algorithmName = itemView.findViewById(R.id.algorithm_item_name_textview);
            algorithm = itemView.findViewById(R.id.algorithm_item_algorithm);
            algorithmCategory = itemView.findViewById(R.id.algorithm_category_textview);
            practicedCorrect = itemView.findViewById(R.id.algorithm_item_practiced_vs_correct);

            algorithmCard = itemView.findViewById(R.id.algorithm_card);
            algorithmIcon = itemView.findViewById(R.id.algorithm_icon);

            algorithm_selected = itemView.findViewById(R.id.algorithm_selected);
            algorithm_item_checks = itemView.findViewById(R.id.algorithm_item_checks);
            algorithm_selected_checks = itemView.findViewById(R.id.algorithm_selected_checks);

            favourite_checkbox = itemView.findViewById(R.id.favourite_checkbox);
            learnt_checkbox = itemView.findViewById(R.id.learnt_checkbox);

            algorithmCard.setOnClickListener(v -> click(onCardClick, v));
            algorithmIcon.setOnClickListener(v -> click(onImageClick, v));
            favourite_checkbox.setOnClickListener(v -> click(onFavouriteClick, v));
            learnt_checkbox.setOnClickListener(v -> click(onLearntClick, v));

            algorithmCard.setOnLongClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return false;

                toggleSelection(pos);

                if (onLongClick != null) {
                    onLongClick.onLongClick(pos, v);
                }
                return true;
            });
        }

        private void click(OnItemViewClick cb, View v) {
            int pos = getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION || cb == null) return;
            cb.onClick(pos, v);
        }

        private void click(OnItemClick cb, View v) {
            int pos = getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION || cb == null) return;
            cb.onClick(pos);
        }
    }
}
