package com.jorotayo.algorubickrevamped.ui.timer;

import android.app.AlertDialog.Builder;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.jorotayo.algorubickrevamped.ObjectBox;
import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Solution;
import com.jorotayo.algorubickrevamped.data.Solution_;
import com.jorotayo.algorubickrevamped.data.Solve;
import com.jorotayo.algorubickrevamped.data.Solve_;
import com.jorotayo.algorubickrevamped.ui.solution_guide.SolutionRecyclerAdapter;
import com.jorotayo.algorubickrevamped.ui.timer.StatisticsRecyclerAdapter.OnSolveListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.objectbox.Box;

public class StatisticsActivity extends AppCompatActivity implements OnSolveListener, AdapterView.OnItemSelectedListener {
    LinearLayout noSolvesAlert;
    ArrayList<Solve> solveArrayList = new ArrayList<>();
    ArrayList<String> filterList = new ArrayList<>();
    Box<Solve> solveBox;
    RecyclerView statisticsRecycler;
    Spinner statisticsSpinner;
    StatisticsRecyclerAdapter statisticsRecyclerAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        this.statisticsRecycler = findViewById(R.id.solve_grid);
        setupSortSpinner();
        Box<Solve> boxFor = ObjectBox.getBoxStore().boxFor(Solve.class);
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
        statisticsSpinner.setSelection(0,false);
        statisticsSpinner.setOnItemSelectedListener(this);
    }

  /*  private void loadDefaultCubeSizes(SharedPreferences settings){
        Gson gson = new Gson();
        cube_size = (ArrayList<String>) Arrays.asList("3x3","4x4","5x5","6x6","Megaminx","Pyraminx","Add Cube Size");
        String jsonText = gson.toJson(cube_size);
        settings.edit().putString("FILTER_OPTIONS", jsonText).apply();
        settings.edit().putBoolean("FILTER_OPTIONS_SET", true).apply();
    }*/

    private void addFilter(String newFilter){
     /*   Gson gson = new Gson();
        SharedPreferences prefs = getSharedPreferences("PREFS_NAME", 0);
        String jsonText = prefs.getString("CUBE_SIZES_SET", null);
        filterList.add(newFilter);*/
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        String selected = statisticsSpinner.getSelectedItem().toString();
        switch (selected) {
            case "Solved Date":
                sortBySolvedDate();
                Toast.makeText(this, "solved date", Toast.LENGTH_SHORT).show();
                break;
            case "Cube Size":
                sortByCubeSize();
                Toast.makeText(this, "cube size", Toast.LENGTH_SHORT).show();
                break;
            case "Solve Time":
                sortBySolveTime();
                Toast.makeText(this, "solved time", Toast.LENGTH_SHORT).show();
                break;
        }
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
}
