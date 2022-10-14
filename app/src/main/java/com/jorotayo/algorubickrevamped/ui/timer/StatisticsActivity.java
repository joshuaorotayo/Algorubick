package com.jorotayo.algorubickrevamped.ui.timer;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jorotayo.algorubickrevamped.ObjectBox;
import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Solve;
import com.jorotayo.algorubickrevamped.ui.timer.StatisticsRecyclerAdapter.OnSolveListener;

import java.util.ArrayList;

import io.objectbox.Box;

public class StatisticsActivity extends AppCompatActivity implements OnSolveListener {
    LinearLayout noSolvesAlert;
    ArrayList<Solve> solveArrayList = new ArrayList<>();
    Box<Solve> solveBox;
    RecyclerView statisticsRecycler;
    StatisticsRecyclerAdapter statisticsRecyclerAdapter;

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        this.statisticsRecycler = findViewById(R.id.solve_grid);
        Box<Solve> boxFor = ObjectBox.getBoxStore().boxFor(Solve.class);
        this.solveBox = boxFor;
        this.solveArrayList = (ArrayList<Solve>) boxFor.getAll();
        this.noSolvesAlert = findViewById(R.id.no_solves_alert);
        if (!this.solveArrayList.isEmpty()) {
            this.noSolvesAlert.setVisibility(View.GONE);
            StatisticsRecyclerAdapter statisticsRecyclerAdapter = new StatisticsRecyclerAdapter(this.solveArrayList, this);
            this.statisticsRecyclerAdapter = statisticsRecyclerAdapter;
            this.statisticsRecycler.setAdapter(statisticsRecyclerAdapter);
           /* this.statisticsRecycler.setLayoutManager(new LinearLayoutManager(this));
            this.statisticsRecycler.setHasFixedSize(true);*/
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onSolveClick(int position) {
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void onSolveDeleteClick(final int position) {
        String str = "No";
        new Builder(this).setTitle("Delete Solve").setMessage("Are you sure you want to delete the current Solve").setPositiveButton("Yes", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                StatisticsActivity.this.DeleteSolve(position);
            }
        }).setNegativeButton(str, (dialog, which) -> dialog.dismiss()).setIcon(R.drawable.incorrect_48_r).setCancelable(true).show();
    }

    public void DeleteSolve(int position) {
        this.solveBox.remove(this.solveArrayList.get(position));
        this.solveArrayList.remove(position);
        this.statisticsRecyclerAdapter.notifyItemRemoved(position);
        this.statisticsRecyclerAdapter.notifyItemRangeChanged(position, this.solveArrayList.size());
        if (this.solveArrayList.isEmpty()) {
            this.noSolvesAlert.setVisibility(View.VISIBLE);
        }
    }
}
