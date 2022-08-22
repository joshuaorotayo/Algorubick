package com.jorotayo.algorubickrevamped.ui.solution_guide;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Constraints;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Solution;

import java.util.ArrayList;

public class SolutionRecyclerAdapter extends Adapter<SolutionRecyclerAdapter.ViewHolder> {
    private final OnSolutionListener mOnSolutionListener;
    private ArrayList<Solution> mSolutions;

    public SolutionRecyclerAdapter(ArrayList<Solution> mSolutions, OnSolutionListener mOnSolutionListener) {
        this.mSolutions = mSolutions;
        this.mOnSolutionListener = mOnSolutionListener;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_solution, parent, false), this.mOnSolutionListener);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        Solution solution = this.mSolutions.get(position);
        holder.solutionName.setText(solution.getSolutionName());
        holder.solutionCreator.setText(solution.getSolutionCreator());
        holder.solutionDescription.setText(solution.getSolutionDescription());
        holder.solutionImage.setImageResource(R.drawable.rubiks_cube_scrambled);
    }

    public int getItemCount() {
        return this.mSolutions.size();
    }

    public interface OnSolutionListener {
        void onSolutionClick(int i);

        void onSolutionImageClick(int i, View view);
    }

    public static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder implements OnClickListener {
        OnSolutionListener mOnSolutionListener;
        LinearLayout solutionCard;
        TextView solutionCreator;
        TextView solutionDescription;
        ImageView solutionImage;
        TextView solutionName;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public ViewHolder(View view, OnSolutionListener onSolutionListener) {
            super(view);
            this.solutionName = view.findViewById(R.id.solution_card_solution_name);
            this.solutionCreator = view.findViewById(R.id.solution_card_solution_creator);
            this.solutionDescription = view.findViewById(R.id.solution_card_solution_description);
            this.solutionImage = view.findViewById(R.id.solution_card_image);
            LinearLayout linearLayout = view.findViewById(R.id.solution_card);
            this.solutionCard = linearLayout;
            this.mOnSolutionListener = onSolutionListener;
            linearLayout.setOnClickListener(this);
            this.solutionImage.setOnClickListener(this);
        }

        public void onClick(View view) {
            int id = view.getId();
            String str = Constraints.TAG;
            StringBuilder stringBuilder;
            switch (id) {
                case R.id.solution_card /*2131362320*/:
                    this.mOnSolutionListener.onSolutionClick(getAdapterPosition());
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("onClick: ");
                    stringBuilder.append(getAdapterPosition());
                    Log.d(str, stringBuilder.toString());
                    return;
                case R.id.solution_card_image /*2131362321*/:
                    this.mOnSolutionListener.onSolutionImageClick(getAdapterPosition(), view);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("onImageClick: ");
                    stringBuilder.append(getAdapterPosition());
                    Log.d(str, stringBuilder.toString());
                    return;
                default:
            }
        }
    }
}
