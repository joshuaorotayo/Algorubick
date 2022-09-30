package com.jorotayo.algorubickrevamped.ui.solution_guide;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Constraints;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Solution;
import com.jorotayo.algorubickrevamped.data.Steps;
import com.jorotayo.algorubickrevamped.utils.UtilMethods;

import java.util.ArrayList;

public class StepsRecyclerAdapter extends Adapter<StepsRecyclerAdapter.ViewHolder> {
    private final OnStepListener mOnStepListener;
    private ArrayList<Steps> mSteps = new ArrayList();
    private Solution solution;
private Context context;

    public StepsRecyclerAdapter(ArrayList<Steps> mSteps, OnStepListener mOnStepListener) {
        this.mSteps = mSteps;
        this.mOnStepListener = mOnStepListener;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_step_view, parent, false), this.mOnStepListener);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        Steps step = this.mSteps.get(position);
        holder.stepName.setText(step.getStepName());
        holder.stepDescription.setText(step.getStepDescription());
        holder.stepAlgorithm.setText(step.getStepAlgorithm());
        UtilMethods.LoadStepIcon(context,holder.stepImageStart, step.stepImageStart);
        UtilMethods.LoadStepIcon(context,holder.stepImageEnd, step.stepImageEnd);
//        holder.stepImageStart.setImageResource(R.drawable.cfop);
//        holder.stepImageEnd.setImageResource(R.drawable.cfop);
    }

    public int getItemCount() {
        return this.mSteps.size();
    }

    public interface OnStepListener {
        void onStepClick(int i);

        void onStepImageClick(int i, View view);
    }

    public class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder implements OnClickListener {
        OnStepListener mOnStepListener;
        TextView stepAlgorithm;
        CardView stepCard;
        TextView stepDescription;
        ImageView stepImageEnd;
        ImageView stepImageStart;
        TextView stepName;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public ViewHolder(View view, OnStepListener onStepListener) {
            super(view);
            this.stepName = view.findViewById(R.id.view_step_name);
            this.stepAlgorithm = view.findViewById(R.id.view_step_algorithm);
            this.stepDescription = view.findViewById(R.id.view_step_description);
            this.stepImageStart = view.findViewById(R.id.view_step_start_image);
            this.stepImageEnd = view.findViewById(R.id.view_step_end_image);
            CardView cardView = view.findViewById(R.id.view_step_card);
            this.stepCard = cardView;
            this.mOnStepListener = onStepListener;
            cardView.setOnClickListener(this);
        }

        public void onClick(View view) {
            if (view.getId() == R.id.view_step_card) {
                this.mOnStepListener.onStepClick(getAdapterPosition());
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onClick: ");
                stringBuilder.append(getAdapterPosition());
                Log.d(Constraints.TAG, stringBuilder.toString());
            }
        }
    }
}
