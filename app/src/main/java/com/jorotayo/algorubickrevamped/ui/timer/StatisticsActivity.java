package com.jorotayo.algorubickrevamped.ui.timer;

import android.app.AlertDialog.Builder;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.jorotayo.algorubickrevamped.ObjectBox;
import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Solve;
import com.jorotayo.algorubickrevamped.data.Solve_;
import com.jorotayo.algorubickrevamped.ui.timer.StatisticsRecyclerAdapter.OnSolveListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.objectbox.Box;

public class StatisticsActivity extends AppCompatActivity implements View.OnClickListener, OnSolveListener, AdapterView.OnItemSelectedListener {
    LinearLayout noSolvesAlert;
    ArrayList<Solve> solveArrayList = new ArrayList<>();
    ArrayList<String> filterList = new ArrayList<>();
    Box<Solve> solveBox;
    RecyclerView statisticsRecycler;
    Spinner statisticsSpinner;
    ImageView reverseSort;
    StatisticsRecyclerAdapter statisticsRecyclerAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        this.statisticsRecycler = findViewById(R.id.solve_grid);
        setupSortSpinner();
        Box<Solve> boxFor = ObjectBox.getBoxStore().boxFor(Solve.class);
        this.reverseSort = findViewById(R.id.reverse_sort);
        reverseSort.setOnClickListener(this);
        this.solveBox = boxFor;
        this.solveArrayList = (ArrayList<Solve>) boxFor.getAll();
        this.noSolvesAlert = findViewById(R.id.no_solves_alert);
        if (!this.solveArrayList.isEmpty()) {
            this.noSolvesAlert.setVisibility(View.GONE);
            StatisticsRecyclerAdapter statisticsRecyclerAdapter = new StatisticsRecyclerAdapter(this.solveArrayList, this);
            this.statisticsRecyclerAdapter = statisticsRecyclerAdapter;
            this.statisticsRecycler.setAdapter(statisticsRecyclerAdapter);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupSortSpinner() {
        this.statisticsSpinner = findViewById(R.id.statistics_spinner);
        List<String> solve_filter = Arrays.asList("Solved Date", "Cube Size", "Solve Time");
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, solve_filter);
        statisticsSpinner.setAdapter(sortAdapter);
        statisticsSpinner.setSelection(0, false);
        TextView spinnerTextView = (TextView) statisticsSpinner.getSelectedView();
        spinnerTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        spinnerTextView.setTextSize(20);
        spinnerTextView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        statisticsSpinner.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        String selected = statisticsSpinner.getSelectedItem().toString();
        switch (selected) {
            case "Solved Date":
                sortBySolvedDate();
                Toast.makeText(this, "Sort By Solved date", Toast.LENGTH_SHORT).show();
                break;
            case "Cube Size":
                sortByCubeSize();
                Toast.makeText(this, "Sort by Cube size", Toast.LENGTH_SHORT).show();
                break;
            case "Solve Time":
                sortBySolveTime();
                Toast.makeText(this, "Sort by Solved time", Toast.LENGTH_SHORT).show();
                break;
        }
        reverseSort.clearColorFilter();
        StatisticsRecyclerAdapter statisticsRecyclerAdapter = new StatisticsRecyclerAdapter(solveArrayList, this);
        statisticsRecycler.setAdapter(statisticsRecyclerAdapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onSolveClick(int i) {
    }

    private void sortBySolvedDate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            solveArrayList.sort(new Solve.CompareSolvedDate());
        }
    }

    private void sortByCubeSize() {
        this.solveArrayList = (ArrayList<Solve>) this.solveBox.query().order(Solve_.solve_cube_size).build().find();
    }

    public void sortBySolveTime() {
        this.solveArrayList = (ArrayList<Solve>) this.solveBox.query().order(Solve_.solve_milliseconds).build().find();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void onSolveDeleteClick(final int position) {
        String str = "No";
        new Builder(this)
                .setTitle("Delete Solve")
                .setMessage("Are you sure you want to delete the current Solve")
                .setPositiveButton("Yes", (dialog, which) -> StatisticsActivity.this.DeleteSolve(position))
                .setNegativeButton(str, (dialog, which) -> dialog.dismiss()).setIcon(R.drawable.incorrect_48_r).setCancelable(true).show();
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.reverse_sort) {
            reverseSort();
        }
    }

    public void reverseSort(){
        ArrayList<Solve> reversedList = solveArrayList;
        if (reverseSort.getColorFilter() == null) {
            reverseSort.setColorFilter(getResources().getColor(R.color.colorAccent));
            Toast.makeText(this, "Sort order reversed", Toast.LENGTH_SHORT).show();
        } else {
            reverseSort.clearColorFilter();
            Toast.makeText(this, "Sort order cleared", Toast.LENGTH_SHORT).show();
        }
        Collections.reverse(reversedList);
        solveArrayList = reversedList;
        StatisticsRecyclerAdapter statisticsRecyclerAdapter = new StatisticsRecyclerAdapter(solveArrayList, this);
        statisticsRecycler.setAdapter(statisticsRecyclerAdapter);
    }
}
