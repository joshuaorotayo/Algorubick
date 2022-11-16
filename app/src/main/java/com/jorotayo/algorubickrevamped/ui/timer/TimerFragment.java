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
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuProvider;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

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

public class TimerFragment extends Fragment implements CubeSizeAdapter.OnCubeSizeListener, OnClickListener, OnLongClickListener, MenuProvider {
    private final ArrayList<String> faceMoves = new ArrayList<>(Arrays.asList("R", "L", "U", "D", "F", "B", "R'", "L'", "U'", "D'", "F'", "B'", "R2", "L2", "U2", "D2", "F2", "B2", "R2", "L2", "U2", "D2", "F2", "B2"));
    private final Handler mIncomingHandler = new Handler(new IncomingHandlerCallback(this, null));
    private final ArrayList<String> scramble = new ArrayList<>();
    private CardView command_bar;
    private Spinner cube_size_spinner;
    private String cube_size_text, counter;
    private CardView cube_timer_options;
    private ImageButton delete_solve_btn, dnf_solve_btn;
    private int milliseconds, mins, secs, solve_milliseconds, solved_count;
    private ImageButton plus_2_btn;
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
                long updatedTime = 0 + (SystemClock.uptimeMillis() - TimerFragment.this.startTime);
                TimerFragment.this.secs = (int) (updatedTime / 1000);
                TimerFragment.this.mins = TimerFragment.this.secs / 60;
                TimerFragment.this.secs = TimerFragment.this.secs % 60;
                TimerFragment.this.milliseconds = (int) (updatedTime % 100);
                TimerFragment.this.solve_time.setTextColor(-16711936);
                TextView solve_time_view = TimerFragment.this.solve_time;
                String standardTime = "%02d:%02d.%02d";
                String formattedStandardTime = String.format(Locale.getDefault(), standardTime, mins, secs, milliseconds);
                solve_time_view.setText(formattedStandardTime);
                TimerFragment.this.mIncomingHandler.postDelayed(this, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public TimerFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.fragment_timer, container, false);
        requireActivity().addMenuProvider(this);
        this.solve_time = this.root.findViewById(R.id.solve_time);
        this.delete_solve_btn = this.root.findViewById(R.id.delete_solve_btn);
        this.dnf_solve_btn = this.root.findViewById(R.id.dnf_solve_btn);
        this.plus_2_btn = this.root.findViewById(R.id.plus_2_btn);
        this.cube_size_spinner = this.root.findViewById(R.id.cube_size_spinner);
        this.scrambleSpace = this.root.findViewById(R.id.scramble_text);
        this.command_bar = this.root.findViewById(R.id.command_bar);
        this.cube_timer_options = this.root.findViewById(R.id.cube_timer_options);
        this.scramble_card = this.root.findViewById(R.id.scramble_card);
        this.statistics_container = this.root.findViewById(R.id.statistics_container);
        this.command_bar.setVisibility(View.INVISIBLE);
        this.solve_time.setOnClickListener(this);

        ImageButton save_solve_btn = this.root.findViewById(R.id.save_solve_btn);
        save_solve_btn.setOnClickListener(this);

        this.delete_solve_btn.setOnClickListener(this);
        this.dnf_solve_btn.setOnClickListener(this);
        this.plus_2_btn.setOnClickListener(this);

        this.settings = getActivity().getSharedPreferences("PREFS_NAME", 0);
        boolean cubeSizesSet = settings.getBoolean("CUBE_SIZES_SET", false);
        if (!cubeSizesSet) {
            loadDefaultCubeSizes(settings);
        } else {
            getCubeSizes(settings);
        }
        cube_size_spinner.setSelection(0, false);
        cubeSizeAdapter = new CubeSizeAdapter(requireContext(), R.id.cube_size_spinner_label, cube_size, this);
        cube_size_spinner.setAdapter(cubeSizeAdapter);

        this.cube_size_text = this.cube_size_spinner.getSelectedItem().toString();
        createScramble();
        this.scrambleSpace.setOnClickListener(v -> {
            TimerFragment.this.createScramble();
            Toast.makeText(TimerFragment.this.getContext(), "New Scramble", Toast.LENGTH_SHORT).show();
        });
        Box<Solve> boxFor = ObjectBox.getBoxStore().boxFor(Solve.class);
        this.solveBox = boxFor;
        ArrayList<Solve> solves = (ArrayList<Solve>) boxFor.getAll();
        setUpStatistics();

        if (addCubeSizeDialog == null) {
            createCubeSizeDialog();
        }

        return this.root;
    }

    private void loadDefaultCubeSizes(SharedPreferences settings) {
        Gson gson = new Gson();
        cube_size = new ArrayList<> (Arrays.asList("3x3", "2x2","4x4", "5x5", "6x6","7x7","8x8","9x9", "Megaminx", "Pyraminx", "Add Cube Size +"));
        String jsonText = gson.toJson(cube_size);
        settings.edit().putString("CUBE_SIZES", jsonText).apply();
        settings.edit().putBoolean("CUBE_SIZES_SET", true).apply();
    }

    private void getCubeSizes(SharedPreferences settings) {
        Gson gson = new Gson();
        String jsonText = settings.getString("CUBE_SIZES", null);
        settings.edit().putString("CUBE_SIZES", jsonText).apply();
        Type stringListType = new TypeToken<ArrayList<String>>() {
        }.getType();
        cube_size = gson.fromJson(jsonText, stringListType);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_solve_btn /*2131362028*/:
                deleteSolve();
                return;
            case R.id.dnf_solve_btn /*2131362042*/:
                dnfSolve();
                return;
            case R.id.plus_2_btn /*2131362245*/:
                plus2Solve();
                return;
            case R.id.save_solve_btn /*2131362274*/:
                saveSolve();
                return;
            case R.id.solve_time /*2131362332*/:
                timer();
                return;
            default:
        }
    }

    private void saveSolve() {
        CharSequence s = DateFormat.format("HH:mm:ss dd-MM-yyyy", new Date().getTime());
        Solve currentSolve = new Solve();
        currentSolve.setSolve_cube_size(this.cube_size_text);
        currentSolve.setSolve_date(s.toString());

        String formattedTime;
        if (solve_time.getText().equals("DNF")) {
            formattedTime = "DNF";
        } else {
            String savedTime;
            String stringSecs = "" + secs;

            if (mins > 0) {
                if (secs <= 9) {
                    stringSecs = "0" + secs;
                }
            }

            if (mins == 0) {
                savedTime = "{0}.{1}s";
                formattedTime = java.text.MessageFormat.format(savedTime, stringSecs, milliseconds);
            } else {
                savedTime = "{0}m {1}.{2}s";
                formattedTime = java.text.MessageFormat.format(savedTime, mins, stringSecs, milliseconds);
            }
        }

        currentSolve.setSolve_time(formattedTime);
        currentSolve.setSolve_milliseconds(getMilliseconds(this.mins, this.secs, this.milliseconds));
        currentSolve.setSolve_scramble(this.scrambleSpace.getText().toString());
        this.solveBox.put(currentSolve);
        this.cube_size_text = this.cube_size_spinner.getSelectedItem().toString();
        String formattedToast = "Saved time: {0} for {1}";
        Toast.makeText(getContext(), java.text.MessageFormat.format(formattedToast, formattedTime, cube_size_text), Toast.LENGTH_SHORT).show();
        setUpStatistics();
        this.command_bar.setVisibility(View.INVISIBLE);
    }

    private int getMilliseconds(int mins, int secs, int milliseconds) {
        int i = ((60000 * mins) + (secs * 1000)) + milliseconds;
        this.solve_milliseconds = i;
        return i;
    }

    private void deleteSolve() {
        CharSequence charSequence = "No";
        new MaterialAlertDialogBuilder(requireContext()).setTitle("Delete Solve").setMessage("Are you sure you want to delete your current solve time?").setPositiveButton("Yes", (dialog, which) -> {
            TimerFragment.this.solve_time.setText("00:00.00");
            TimerFragment.this.command_bar.setVisibility(View.INVISIBLE);
            dialog.cancel();
        }).setNegativeButton(charSequence, (dialog, which) -> dialog.cancel()).show();
    }

    private void dnfSolve() {
        CharSequence charSequence = "No";
        new MaterialAlertDialogBuilder(requireContext()).setTitle("DNF Solve").setMessage("Mark as DNF?").setPositiveButton("Yes", (dialog, which) -> {
            TimerFragment.this.solve_time.setText("DNF");
            TimerFragment.this.solve_time.setGravity(17);
            TimerFragment.this.dnf_solve_btn.setVisibility(View.INVISIBLE);
            dialog.cancel();
        }).setNegativeButton(charSequence, (dialog, which) -> dialog.cancel()).show();
    }

    private void plus2Solve() {
        CharSequence charSequence = "No";
        new MaterialAlertDialogBuilder(requireContext()).setTitle("Solve Penalty").setMessage("Add Penalty to solve?").setPositiveButton("Yes", (dialog, which) -> {
            TimerFragment timerFragment = TimerFragment.this;
            timerFragment.secs = timerFragment.secs + 2;
            String plus2SolveTime = "{0}:{1}.{2}";
            String stringSecs, stringMins;

            if (secs <= 9) {
                stringSecs = "0" + secs;
            } else {
                stringSecs = "" + secs;
            }
            if (mins <= 9) {
                stringMins = "0" + mins;
            } else {
                stringMins = "" + mins;
            }
            solve_time.setText(java.text.MessageFormat.format(plus2SolveTime, stringMins, stringSecs, milliseconds));
            Toast.makeText(TimerFragment.this.getContext(), "+2 Penalty", Toast.LENGTH_SHORT).show();
            TimerFragment.this.plus_2_btn.setVisibility(View.INVISIBLE);
            dialog.cancel();
        }).setNegativeButton(charSequence, (dialog, which) -> dialog.cancel()).show();
    }

    public boolean onLongClick(View v) {
        return false;
    }

    private void createScramble() {
        int i = 0;
        String scrambleText = "";
        String lastLetter = "";
        String currentLetter = "";
        while (i < 25) {
            Random randomGenerator = new Random();
            int index = randomGenerator.nextInt(this.faceMoves.size());
            currentLetter = this.faceMoves.get(index);
            if (lastLetter.isEmpty()) {
                currentLetter = this.faceMoves.get(index);
                lastLetter = currentLetter;
                this.scramble.add(currentLetter);
                i++;
            } else if (currentLetter.charAt(0) == lastLetter.charAt(0)) {
                index = randomGenerator.nextInt(this.faceMoves.size());
            } else {
                currentLetter = this.faceMoves.get(index);
                lastLetter = currentLetter;
                this.scramble.add(currentLetter);
                i++;
            }
        }
        this.scrambleSpace.setText(this.scramble.toString());
        this.scramble.clear();
    }

    public void createCubeSizeDialog() {

        addCubeSizeDialog = new Dialog(requireContext());
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.add_cube_size_dialog, (ViewGroup) getView(), false);
        addCubeSizeDialog.setContentView(dialogView);
        final TextInputLayout cubeSizeTIL = dialogView.findViewById(R.id.cube_size_til);
        cubeSize_editText = dialogView.findViewById(R.id.cube_size_edit_text);
        cancel_btn = dialogView.findViewById(R.id.add_cube_size_cancel_btn);
        confirm_btn = dialogView.findViewById(R.id.add_cube_size_confirm_btn);

        cancel_btn.setOnClickListener(v -> {
            addCubeSizeDialog.dismiss();
        });

        confirm_btn.setOnClickListener(v -> {
            if (StringUtils.isNotBlank(cubeSize_editText.getText())) {
                verifyCubeSize(cubeSize_editText.getText().toString());
                addCubeSizeDialog.dismiss();
            }
        });
        addCubeSizeDialog.create();
    }



    private void timer() {
        if (this.running) {
            requireActivity().getWindow().clearFlags(128);
            requireActivity().getWindow().clearFlags(1024);
            this.mIncomingHandler.removeCallbacks(this.updateTimerThread);
            this.solve_time.setTextColor(ViewCompat.MEASURED_STATE_MASK);
            this.running = false;
            solveViewShow();
            createScramble();
            return;
        }
        requireActivity().getWindow().setFlags(128, 128);
        requireActivity().getWindow().setFlags(1024, 1024);
        this.command_bar.setVisibility(View.VISIBLE);
        this.dnf_solve_btn.setVisibility(View.VISIBLE);
        this.startTime = SystemClock.uptimeMillis();
        solveViewHide();
        this.mIncomingHandler.postDelayed(this.updateTimerThread, 0);
        this.running = true;
    }

    private void solveViewHide() {
        this.scramble_card.setVisibility(View.INVISIBLE);
        this.command_bar.setVisibility(View.INVISIBLE);
        this.statistics_container.setVisibility(View.INVISIBLE);
        this.cube_timer_options.setVisibility(View.INVISIBLE);
    }

    private void solveViewShow() {
        this.scramble_card.setVisibility(View.VISIBLE);
        this.command_bar.setVisibility(View.VISIBLE);
        this.delete_solve_btn.setVisibility(View.VISIBLE);
        this.dnf_solve_btn.setVisibility(View.VISIBLE);
        this.plus_2_btn.setVisibility(View.VISIBLE);
        this.statistics_container.setVisibility(View.VISIBLE);
        this.cube_timer_options.setVisibility(View.VISIBLE);
    }

    private void setUpStatistics() {
        TextView count_time_time = this.root.findViewById(R.id.count_time_time);
        TextView best_time_time = this.root.findViewById(R.id.best_time_time);
        TextView worst_time_time = this.root.findViewById(R.id.worst_time_time);
        TextView mean_time_time = this.root.findViewById(R.id.mean_time_time);
        TextView avg_5 = this.root.findViewById(R.id.avg_5);
        TextView avg_12 = this.root.findViewById(R.id.avg_12);
        TextView avg_50 = this.root.findViewById(R.id.avg_50);
        TextView avg_100 = this.root.findViewById(R.id.avg_100);
        cube_size_text = cube_size_spinner.getSelectedItem().toString();
        ArrayList<Solve> solves = getSolvesByCubeSize();
        ArrayList<Integer> solve_times = new ArrayList<>();
        for (Solve solve : solves) {
            solve_times.add(solve.solve_milliseconds);
        }
        int total_time = 0;
        for (Solve solve : solves) {
            if (!solve.solve_time.equals("DNF")) {
                total_time += solve.solve_milliseconds;
            }
        }
        this.solved_count = solves.size();
        if (solves.isEmpty()) {
            best_time_time.setText(getString(R.string.not_applicable));
            worst_time_time.setText(getString(R.string.not_applicable));
            mean_time_time.setText(getString(R.string.not_applicable));
            count_time_time.setText(getString(R.string.not_applicable));
            avg_5.setText(getString(R.string.not_applicable));
            avg_12.setText(getString(R.string.not_applicable));
            avg_50.setText(getString(R.string.not_applicable));
            avg_100.setText(getString(R.string.not_applicable));
            return;
        }
        best_time_time.setText(getFastestSolveByCubeSize(solve_times));
        worst_time_time.setText(getWorstSolveByCubeSize(solve_times));
        mean_time_time.setText(String.format("%s", getMeanTimeByCubeSize(total_time, solves.size())));
        count_time_time.setText(String.format(Locale.getDefault(), "%d Solves", getSolvesByCubeSize().size()));
        avg_5.setText(solveAvg(total_time, 5));
        avg_12.setText(solveAvg(total_time, 12));
        avg_50.setText(solveAvg(total_time, 50));
        avg_100.setText(solveAvg(total_time, 100));
    }

    private ArrayList<Solve> getSolvesByCubeSize() {
        return (ArrayList<Solve>) this.solveBox.query().equal(Solve_.solve_cube_size, this.cube_size_text, QueryBuilder.StringOrder.CASE_INSENSITIVE).build().find();
    }

    private String getFastestSolveByCubeSize(ArrayList<Integer> solve_times) {
        int temp = solve_times.get(0);
        for (int i = 1; i < solve_times.size(); i++) {
            if (temp > solve_times.get(i)) {
                temp = solve_times.get(i);
            }
        }
        return formatTime(temp);
    }

    private String getWorstSolveByCubeSize(ArrayList<Integer> solve_times) {
        int temp = solve_times.get(0);
        for (int i = 1; i < solve_times.size(); i++) {
            if (temp < solve_times.get(i)) {
                temp = solve_times.get(i);
            }
        }
        return formatTime(temp);
    }

    private String getMeanTimeByCubeSize(int total_time, int total_solves) {
        return formatTime(total_time / total_solves);
    }

    private String solveAvg(int total_time, int avg_of) {
        if (this.solved_count < avg_of) {
            return getString(R.string.not_applicable);
        }
        return formatTime(total_time / avg_of);
    }

    private String formatTime(int milliseconds_time) {
        int stats_secs = milliseconds_time / 1000;
        int stats_mins = stats_secs / 60;
        stats_secs %= 60;
        int stats_ms = milliseconds_time % 100;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(String.format(Locale.getDefault(), getString(R.string.two_decimals), stats_mins));
        stringBuilder.append(":");
        stringBuilder.append(String.format(Locale.getDefault(), getString(R.string.two_decimals), stats_secs));
        stringBuilder.append(".");
        stringBuilder.append(String.format(Locale.getDefault(), getString(R.string.two_decimals), stats_ms));
        return String.format(java.util.Locale.getDefault(), "%02d:%02d.%02d", stats_mins, stats_secs, stats_ms);
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menu.findItem(R.id.actionbar_statistics).setVisible(true).setOnMenuItemClickListener(item -> {
            TimerFragment.this.startActivity(new Intent(TimerFragment.this.getContext(), StatisticsActivity.class));
            return true;
        });
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void cubeSizeLabelClick(int position) {
        String selected = cube_size.get(position);
        if (selected.equals("Add Cube Size +")) {
            addCubeSizeDialog.show();
        } else {
            cube_size_spinner.setSelection(position);
            setUpStatistics();
        }
        hideSpinnerDropDown(cube_size_spinner);
    }

    public void verifyCubeSize(String cube_size_text) {
        boolean duplicate = false;
        for (String size : cube_size) {
            if (StringUtils.containsIgnoreCase(size, cube_size_text)) {
                duplicate = true;
            }
        }
        if (duplicate) {
            Toast.makeText(getContext(), cube_size_text + " already present", Toast.LENGTH_SHORT).show();
        } else {
            addCubeSize(cube_size_text);
            cube_size_spinner.setSelection(cube_size.size() - 2);
        }
    }

    public void addCubeSize(String cube_size_text) {
        Gson gson = new Gson();
        int secondToLast = cube_size.size() - 1;
        cube_size.add(secondToLast, cube_size_text);
        String jsonText = gson.toJson(cube_size);
        settings.edit().putString("CUBE_SIZES", jsonText).apply();
        cubeSize_editText.setText("");
    }

    /**
     * Hides a spinner's drop down.
     */
    public static void hideSpinnerDropDown(Spinner spinner) {
        try {
            Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
            method.setAccessible(true);
            method.invoke(spinner);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cubeSizeDeleteClick(int position) {
        if(cube_size.size() > 1){
            buildCubeSizeDeleteDialog(position);
            cubeConfirmDeleteDialog.show();
          }
    }

    public void buildCubeSizeDeleteDialog(int position) {
        cubeConfirmDeleteDialog = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.cube_size_category_delete_title))
                .setMessage(getString(R.string.spinner_delete_confirmation, cube_size.get(position)))
                .setPositiveButton("Yes", (dialog, which) -> {
                    confirmCubeSizeDelete(position);
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create();
    }

    public void confirmCubeSizeDelete(int position){
        Gson gson = new Gson();
        cube_size.remove(position);
        String jsonText = gson.toJson(cube_size);
        settings.edit().putString("CUBE_SIZES", jsonText).apply();
        cube_size_spinner.setSelection(0);
        hideSpinnerDropDown(cube_size_spinner);
    }

    private class IncomingHandlerCallback implements Callback {
        private IncomingHandlerCallback() {
        }

        IncomingHandlerCallback(TimerFragment timerFragment) {
            this();
        }

        public IncomingHandlerCallback(TimerFragment timerFragment, Object o) {
        }

        public boolean handleMessage(Message message) {
            TimerFragment.this.solve_time.setTextColor(getResources().getColor(R.color.colorAccent));
            TimerFragment.this.solve_time.setText(TimerFragment.this.counter);
            return true;
        }
    }
}
