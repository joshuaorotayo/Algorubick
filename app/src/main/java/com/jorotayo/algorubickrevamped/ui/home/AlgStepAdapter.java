package com.jorotayo.algorubickrevamped.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jorotayo.algorubickrevamped.R;

import java.util.ArrayList;
import java.util.List;

public class AlgStepAdapter extends RecyclerView.Adapter<AlgStepAdapter.ViewHolder> {

    private final List<Integer> stepImages = new ArrayList<>();
    private final Context context;
    private final int maxColumns;
    private final int maxRows;

    public AlgStepAdapter(Context context, int maxColumns, int maxRows) {
        this.context = context;
        this.maxColumns = maxColumns;
        this.maxRows = maxRows;
    }

    public void submitSteps(List<Integer> newSteps) {
        stepImages.clear();
        stepImages.addAll(newSteps);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alg_step, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageResource(stepImages.get(position));

        // Flexbox layout params
        ViewGroup.LayoutParams lp = holder.imageView.getLayoutParams();
        if (!(lp instanceof com.google.android.flexbox.FlexboxLayoutManager.LayoutParams)) {
            lp = new com.google.android.flexbox.FlexboxLayoutManager.LayoutParams(lp);
            holder.imageView.setLayoutParams(lp);
        }
        com.google.android.flexbox.FlexboxLayoutManager.LayoutParams flexLp =
                (com.google.android.flexbox.FlexboxLayoutManager.LayoutParams) holder.imageView.getLayoutParams();

        // Width = maxColumns per row
        flexLp.setFlexBasisPercent(1f / maxColumns);
        flexLp.setMargins(4, 4, 4, 4);

        // Height = if total rows <= maxRows, scale to fill 250dp
        int totalRows = (int) Math.ceil(stepImages.size() / (double) maxColumns);
        if (totalRows <= maxRows) {
            RecyclerView recyclerView = (RecyclerView) holder.itemView.getParent();
            if (recyclerView != null) {
                int recyclerHeight = recyclerView.getHeight() - recyclerView.getPaddingTop() - recyclerView.getPaddingBottom();
                int rowHeight = recyclerHeight / totalRows; // scale each row to fill
                flexLp.height = rowHeight;
            }
        } else {
            // More than maxRows → use WRAP_CONTENT so rows can scroll
            flexLp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        holder.imageView.setLayoutParams(flexLp);
    }

    @Override
    public int getItemCount() {
        return stepImages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.step_image);
        }
    }
}
