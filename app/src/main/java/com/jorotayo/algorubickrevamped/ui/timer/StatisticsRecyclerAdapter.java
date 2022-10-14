package com.jorotayo.algorubickrevamped.ui.timer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Solve;

import java.util.ArrayList;

public class StatisticsRecyclerAdapter extends Adapter<StatisticsRecyclerAdapter.ViewHolder> {
    private OnSolveListener mOnSolveListener;
    private ArrayList<Solve> mSolve;

    public StatisticsRecyclerAdapter(ArrayList<Solve> mSolve, OnSolveListener mOnSolveListener) {
        this.mSolve = mSolve;
        this.mOnSolveListener = mOnSolveListener;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_solve_view2, parent, false), this.mOnSolveListener);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        Solve solve = this.mSolve.get(position);
        holder.solveCubeSize.setText(solve.getSolve_cube_size());
        holder.solveTime.setText(solve.getSolve_time());
        holder.solveDate.setText(solve.getSolve_date());
    }

    public int getItemCount() {
        return this.mSolve.size();
    }

    public interface OnSolveListener {
        void onSolveClick(int i);

        void onSolveDeleteClick(int i);
    }

    public class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder implements OnClickListener {
        TextView solveCubeSize;
        TextView solveDate;
        ImageButton solveDeleteIcon;
        TextView solveTime;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public ViewHolder(View view, OnSolveListener onSolveListener) {
            super(view);
            this.solveCubeSize = view.findViewById(R.id.item_solve_cube_size);
            this.solveTime = view.findViewById(R.id.item_solve_time);
            this.solveDate = view.findViewById(R.id.item_solve_date);
            this.solveDeleteIcon = view.findViewById(R.id.item_solve_delete_icon);
            StatisticsRecyclerAdapter.this.mOnSolveListener = onSolveListener;
            this.solveDeleteIcon.setOnClickListener(this);

//            this.solvePosition = view.findViewById(R.id.item_solve_position);
//            this.solveRow = view.findViewById(R.id.solve_row);
//            this.solveRow.setOnClickListener(this);
        }

        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.item_solve_delete_icon) {
                StatisticsRecyclerAdapter.this.mOnSolveListener.onSolveDeleteClick(getAdapterPosition());
            }
        }
    }
}
