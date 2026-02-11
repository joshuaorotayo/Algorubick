package com.jorotayo.algorubickrevamped.ui.timer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.core.view.MenuProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jorotayo.algorubickrevamped.ObjectBox;
import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Solve;
import com.jorotayo.algorubickrevamped.data.Solve_;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import io.objectbox.Box;
import io.objectbox.query.QueryBuilder;

public class TimerFragment extends Fragment implements CubeSizeAdapter.OnCubeSizeListener, OnClickListener, OnLongClickListener {

    // -------------------- Fields --------------------
    private final ArrayList<String> faceMoves = new ArrayList<>(Arrays.asList(
            "R","L","U","D","F","B","R'","L'","U'","D'","F'","B'",
            "R2","L2","U2","D2","F2","B2"
    ));
    private final Handler mIncomingHandler = new Handler(new IncomingHandlerCallback(this));
    private final ArrayList<String> scramble = new ArrayList<>();

    private CardView command_bar, cube_timer_options;
    private Spinner cube_size_spinner;
    private String cube_size_text;
    private ImageButton delete_solve_btn, dnf_solve_btn, plus_2_btn;
    private int milliseconds, mins, secs, solve_milliseconds, solved_count;
    private View root;
    private boolean running;
    private TextView scrambleSpace, solve_time;
    private LinearLayout scramble_card, statistics_container;
    private Box<Solve> solveBox;
    private long startTime;
    private ArrayList<String> cube_size = new ArrayList<>();
    private SharedPreferences settings;
    private Dialog addCubeSizeDialog;
    private EditText cubeSize_editText;
    private Button cancel_btn, confirm_btn;
    private CubeSizeAdapter cubeSizeAdapter;
    private AlertDialog cubeConfirmDeleteDialog;

    private final Runnable updateTimerThread = new Runnable() {
        public void run() {
            try {
                long updatedTime = SystemClock.uptimeMillis() - startTime;
                secs = (int) (updatedTime / 1000);
                mins = secs / 60;
                secs %= 60;
                milliseconds = (int) (updatedTime % 100);
                solve_time.setTextColor(-16711936);
                solve_time.setText(String.format(Locale.getDefault(), "%02d:%02d.%02d", mins, secs, milliseconds));
                mIncomingHandler.postDelayed(this, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public TimerFragment() {}

    // -------------------- Lifecycle --------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_timer, container, false);

        // Find views
        solve_time = root.findViewById(R.id.solve_time);
        delete_solve_btn = root.findViewById(R.id.delete_solve_btn);
        dnf_solve_btn = root.findViewById(R.id.dnf_solve_btn);
        plus_2_btn = root.findViewById(R.id.plus_2_btn);
        cube_size_spinner = root.findViewById(R.id.cube_size_spinner);
        scrambleSpace = root.findViewById(R.id.scramble_text);
        command_bar = root.findViewById(R.id.command_bar);
        cube_timer_options = root.findViewById(R.id.cube_timer_options);
        scramble_card = root.findViewById(R.id.scramble_card);
        statistics_container = root.findViewById(R.id.statistics_container);

        command_bar.setVisibility(View.INVISIBLE);
        solve_time.setOnClickListener(this);
        delete_solve_btn.setOnClickListener(this);
        dnf_solve_btn.setOnClickListener(this);
        plus_2_btn.setOnClickListener(this);

        ImageButton save_solve_btn = root.findViewById(R.id.save_solve_btn);
        save_solve_btn.setOnClickListener(this);

        // SharedPreferences
        settings = requireActivity().getSharedPreferences("PREFS_NAME", 0);
        boolean cubeSizesSet = settings.getBoolean("CUBE_SIZES_SET", false);
        if (!cubeSizesSet) loadDefaultCubeSizes();
        else getCubeSizes();

        cube_size_spinner.setSelection(0, false);
        cubeSizeAdapter = new CubeSizeAdapter(requireContext(), R.id.cube_size_spinner_label, cube_size, this);
        cube_size_spinner.setAdapter(cubeSizeAdapter);

        cube_size_text = cube_size_spinner.getSelectedItem().toString();
        scrambleSpace.setOnClickListener(v -> {
            createScramble();
            Toast.makeText(getContext(), "New Scramble", Toast.LENGTH_SHORT).show();
        });

        solveBox = ObjectBox.getBoxStore().boxFor(Solve.class);
        setUpStatistics();

        if (addCubeSizeDialog == null) createCubeSizeDialog();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // MenuProvider
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                menuInflater.inflate(R.menu.actionbar, menu);
                MenuItem searchItem = menu.findItem(R.id.actionbar_search);
                if (searchItem != null) searchItem.setVisible(false);

                MenuItem statsItem = menu.findItem(R.id.actionbar_statistics);
                if (statsItem != null) {
                    statsItem.setVisible(true);
                    statsItem.setOnMenuItemClickListener(item -> {
                        startActivity(new Intent(getContext(), StatisticsActivity.class));
                        return true;
                    });
                }
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner());
    }

    // -------------------- Click Listeners --------------------
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.solve_time: timer(); break;
            case R.id.delete_solve_btn: deleteSolve(); break;
            case R.id.dnf_solve_btn: dnfSolve(); break;
            case R.id.plus_2_btn: plus2Solve(); break;
            case R.id.save_solve_btn: saveSolve(); break;
        }
    }

    // -------------------- Solve Actions --------------------
    private void deleteSolve() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Solve")
                .setMessage("Are you sure you want to delete your current solve time?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    solve_time.setText("00:00.00");
                    command_bar.setVisibility(View.INVISIBLE);
                    dialog.cancel();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .show();
    }

    private void dnfSolve() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("DNF Solve")
                .setMessage("Mark as DNF?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    solve_time.setText("DNF");
                    solve_time.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    dnf_solve_btn.setVisibility(View.INVISIBLE);
                    dialog.cancel();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .show();
    }

    private void plus2Solve() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Solve Penalty")
                .setMessage("Add +2 seconds penalty to this solve?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    secs += 2;

                    String stringSecs = secs <= 9 ? "0" + secs : String.valueOf(secs);
                    String stringMins = mins <= 9 ? "0" + mins : String.valueOf(mins);

                    solve_time.setText(String.format(Locale.getDefault(), "%s:%s.%02d", stringMins, stringSecs, milliseconds));
                    Toast.makeText(getContext(), "+2 Penalty applied", Toast.LENGTH_SHORT).show();
                    plus_2_btn.setVisibility(View.INVISIBLE);
                    dialog.cancel();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .show();
    }

    private void saveSolve() {
        CharSequence date = DateFormat.format("HH:mm:ss dd-MM-yyyy", new Date().getTime());
        Solve currentSolve = new Solve();
        currentSolve.setSolve_cube_size(cube_size_text);
        currentSolve.setSolve_date(date.toString());

        String formattedTime;
        if (solve_time.getText().equals("DNF")) {
            formattedTime = "DNF";
        } else {
            String stringSecs = String.valueOf(secs);
            if (mins > 0 && secs <= 9) stringSecs = "0" + secs;

            if (mins == 0) {
                formattedTime = String.format(Locale.getDefault(), "%s.%02ds", stringSecs, milliseconds);
            } else {
                formattedTime = String.format(Locale.getDefault(), "%dm %s.%02ds", mins, stringSecs, milliseconds);
            }
        }

        currentSolve.setSolve_time(formattedTime);
        currentSolve.setSolve_milliseconds(getMilliseconds(mins, secs, milliseconds));
        currentSolve.setSolve_scramble(scrambleSpace.getText().toString());
        solveBox.put(currentSolve);

        cube_size_text = cube_size_spinner.getSelectedItem().toString();
        Toast.makeText(getContext(), String.format("Saved time: %s for %s", formattedTime, cube_size_text), Toast.LENGTH_SHORT).show();
        setUpStatistics();
        command_bar.setVisibility(View.INVISIBLE);
    }

    // Utility for converting mins, secs, ms to total milliseconds
    private int getMilliseconds(int mins, int secs, int milliseconds) {
        solve_milliseconds = (mins * 60000) + (secs * 1000) + milliseconds;
        return solve_milliseconds;
    }


    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    // -------------------- Timer --------------------
    private void timer() {
        if (running) {
            requireActivity().getWindow().clearFlags(128 | 1024);
            mIncomingHandler.removeCallbacks(updateTimerThread);
            solve_time.setTextColor(ViewCompat.MEASURED_STATE_MASK);
            running = false;
            solveViewShow();
            createScramble();
            return;
        }
        requireActivity().getWindow().setFlags(128 | 1024, 128 | 1024);
        command_bar.setVisibility(View.VISIBLE);
        dnf_solve_btn.setVisibility(View.VISIBLE);
        startTime = SystemClock.uptimeMillis();
        solveViewHide();
        mIncomingHandler.postDelayed(updateTimerThread, 0);
        running = true;
    }

    private void solveViewHide() {
        scramble_card.setVisibility(View.INVISIBLE);
        command_bar.setVisibility(View.INVISIBLE);
        statistics_container.setVisibility(View.INVISIBLE);
        cube_timer_options.setVisibility(View.INVISIBLE);
    }

    private void solveViewShow() {
        scramble_card.setVisibility(View.VISIBLE);
        command_bar.setVisibility(View.VISIBLE);
        delete_solve_btn.setVisibility(View.VISIBLE);
        dnf_solve_btn.setVisibility(View.VISIBLE);
        plus_2_btn.setVisibility(View.VISIBLE);
        statistics_container.setVisibility(View.VISIBLE);
        cube_timer_options.setVisibility(View.VISIBLE);
    }

    // -------------------- Scramble --------------------
    private void createScramble() {
        scramble.clear();
        String lastLetter = "";
        Random random = new Random();
        while (scramble.size() < 25) {
            String move = faceMoves.get(random.nextInt(faceMoves.size()));
            if (!lastLetter.isEmpty() && move.charAt(0) == lastLetter.charAt(0)) continue;
            scramble.add(move);
            lastLetter = move;
        }
        StringBuilder sb = new StringBuilder();
        for (String s : scramble) sb.append(s).append(" ");
        scrambleSpace.setText(sb.toString().trim());
    }

    // -------------------- Statistics --------------------
    private void setUpStatistics() {
        if (root == null) return;
        TextView count_time_time = root.findViewById(R.id.count_time_time);
        TextView best_time_time = root.findViewById(R.id.best_time_time);
        TextView worst_time_time = root.findViewById(R.id.worst_time_time);
        TextView mean_time_time = root.findViewById(R.id.mean_time_time);
        TextView avg_5 = root.findViewById(R.id.avg_5);
        TextView avg_12 = root.findViewById(R.id.avg_12);
        TextView avg_50 = root.findViewById(R.id.avg_50);
        TextView avg_100 = root.findViewById(R.id.avg_100);

        cube_size_text = cube_size_spinner.getSelectedItem().toString();
        ArrayList<Solve> solves = getSolvesByCubeSize();
        ArrayList<Integer> solve_times = new ArrayList<>();
        for (Solve solve : solves) solve_times.add(solve.solve_milliseconds);

        int total_time = 0;
        for (Solve solve : solves) if (!solve.solve_time.equals("DNF")) total_time += solve.solve_milliseconds;
        solved_count = solves.size();

        if (solves.isEmpty()) {
            String na = getString(R.string.not_applicable);
            best_time_time.setText(na);
            worst_time_time.setText(na);
            mean_time_time.setText(na);
            count_time_time.setText(na);
            avg_5.setText(na);
            avg_12.setText(na);
            avg_50.setText(na);
            avg_100.setText(na);
            return;
        }

        best_time_time.setText(getFastestSolveByCubeSize(solve_times));
        worst_time_time.setText(getWorstSolveByCubeSize(solve_times));
        mean_time_time.setText(getMeanTimeByCubeSize(total_time, solves.size()));
        count_time_time.setText(String.format(Locale.getDefault(), "%d Solves", solves.size()));
        avg_5.setText(solveAvg(total_time, 5));
        avg_12.setText(solveAvg(total_time, 12));
        avg_50.setText(solveAvg(total_time, 50));
        avg_100.setText(solveAvg(total_time, 100));
    }

    private ArrayList<Solve> getSolvesByCubeSize() {
        return (ArrayList<Solve>) solveBox.query()
                .equal(Solve_.solve_cube_size, cube_size_text, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                .build().find();
    }

    private String getFastestSolveByCubeSize(ArrayList<Integer> times) {
        int min = times.get(0);
        for (int t : times) if (t < min) min = t;
        return formatTime(min);
    }

    private String getWorstSolveByCubeSize(ArrayList<Integer> times) {
        int max = times.get(0);
        for (int t : times) if (t > max) max = t;
        return formatTime(max);
    }

    private String getMeanTimeByCubeSize(int total_time, int total_solves) {
        return formatTime(total_time / total_solves);
    }

    private String solveAvg(int total_time, int avg_of) {
        if (solved_count < avg_of) return getString(R.string.not_applicable);
        return formatTime(total_time / avg_of);
    }

    private String formatTime(int ms) {
        int seconds = ms / 1000;
        int minutes = seconds / 60;
        seconds %= 60;
        int ms_part = ms % 100;
        return String.format(Locale.getDefault(), "%02d:%02d.%02d", minutes, seconds, ms_part);
    }

    // -------------------- Cube Size --------------------
    private void loadDefaultCubeSizes() {
        Gson gson = new Gson();
        cube_size = new ArrayList<>(Arrays.asList(
                "3x3","2x2","4x4","5x5","6x6","7x7","8x8","9x9","Megaminx","Pyraminx","Add Cube Size +"
        ));
        settings.edit().putString("CUBE_SIZES", gson.toJson(cube_size))
                .putBoolean("CUBE_SIZES_SET", true).apply();
    }

    private void getCubeSizes() {
        Gson gson = new Gson();
        String json = settings.getString("CUBE_SIZES", null);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        cube_size = gson.fromJson(json, type);
    }

    public void createCubeSizeDialog() {
        addCubeSizeDialog = new Dialog(requireContext());
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.add_cube_size_dialog, (ViewGroup) getView(), false);
        addCubeSizeDialog.setContentView(dialogView);

        cubeSize_editText = dialogView.findViewById(R.id.cube_size_edit_text);
        cancel_btn = dialogView.findViewById(R.id.add_cube_size_cancel_btn);
        confirm_btn = dialogView.findViewById(R.id.add_cube_size_confirm_btn);

        cancel_btn.setOnClickListener(v -> addCubeSizeDialog.dismiss());
        confirm_btn.setOnClickListener(v -> {
            if (StringUtils.isNotBlank(cubeSize_editText.getText())) {
                verifyCubeSize(cubeSize_editText.getText().toString());
                addCubeSizeDialog.dismiss();
            }
        });

        addCubeSizeDialog.create();
    }

    public void verifyCubeSize(String newCube) {
        boolean duplicate = false;
        for (String s : cube_size) if (StringUtils.containsIgnoreCase(s, newCube)) duplicate = true;

        if (duplicate) Toast.makeText(getContext(), newCube + " already present", Toast.LENGTH_SHORT).show();
        else addCubeSize(newCube);
    }

    public void addCubeSize(String newCube) {
        Gson gson = new Gson();
        cube_size.add(cube_size.size() - 1, newCube);
        settings.edit().putString("CUBE_SIZES", gson.toJson(cube_size)).apply();
        cubeSize_editText.setText("");
    }

    @Override
    public void cubeSizeLabelClick(int position) {
        String selected = cube_size.get(position);
        if (selected.equals("Add Cube Size +")) addCubeSizeDialog.show();
        else {
            cube_size_spinner.setSelection(position);
            setUpStatistics();
        }
        hideSpinnerDropDown(cube_size_spinner);
    }

    @Override
    public void cubeSizeDeleteClick(int position) {
        if (cube_size.size() > 1) {
            buildCubeSizeDeleteDialog(position);
            cubeConfirmDeleteDialog.show();
        }
    }

    public void buildCubeSizeDeleteDialog(int position) {
        cubeConfirmDeleteDialog = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.cube_size_category_delete_title))
                .setMessage(getString(R.string.spinner_delete_confirmation, cube_size.get(position)))
                .setPositiveButton("Yes", (dialog, which) -> {
                    cube_size.remove(position);
                    settings.edit().putString("CUBE_SIZES", new Gson().toJson(cube_size)).apply();
                    cube_size_spinner.setSelection(0);
                    hideSpinnerDropDown(cube_size_spinner);
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create();
    }

    public static void hideSpinnerDropDown(Spinner spinner) {
        try {
            Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
            method.setAccessible(true);
            method.invoke(spinner);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // -------------------- Handler --------------------
    private static class IncomingHandlerCallback implements Callback {
        private final TimerFragment fragment;
        public IncomingHandlerCallback(TimerFragment fragment) { this.fragment = fragment; }
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            fragment.solve_time.setTextColor(fragment.getResources().getColor(R.color.colorAccent));
            return true;
        }
    }
}
