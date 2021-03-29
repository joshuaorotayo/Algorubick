package com.jorotayo.algorubickrevamped.ui.timer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jorotayo.algorubickrevamped.ObjectBox;
import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Solve;
import com.jorotayo.algorubickrevamped.data.Solve_;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import io.objectbox.Box;

public class TimerFragment extends Fragment implements OnClickListener, OnLongClickListener, OnItemSelectedListener {
    private final ArrayList<String> faceMoves = new ArrayList(Arrays.asList("R", "L", "U", "D", "F", "B", "R'", "L'", "U'", "D'", "F'", "B'", "R2", "L2", "U2", "D2", "F2", "B2", "R2", "L2", "U2", "D2", "F2", "B2", "r", "l", "u", "d", "f", "b"));
    private final Handler mIncomingHandler = new Handler(new IncomingHandlerCallback(this, null));
    private final ArrayList<String> scramble = new ArrayList();
    private CardView command_bar;
    private int counter;
    private Spinner cube_size_spinner;
    private String cube_size_text;
    private CardView cube_timer_options;
    private ImageButton delete_solve_btn;
    private ImageButton dnf_solve_btn;
    private int milliseconds;
    private int mins;
    private ImageButton plus_2_btn;
    private View root;
    private boolean running;
    private TextView scrambleSpace;
    private LinearLayout scramble_card;
    private int secs;
    private Box<Solve> solveBox;
    private int solve_milliseconds;
    private TextView solve_time;
    private int solved_count;
    private long startTime;
    private final Runnable updateTimerThread = new Runnable() {
        public void run() {
            String str = "%02d";
            try {
                long updatedTime = 0 + (SystemClock.uptimeMillis() - TimerFragment.this.startTime);
                TimerFragment.this.secs = (int) (updatedTime / 1000);
                TimerFragment.this.mins = TimerFragment.this.secs / 60;
                TimerFragment.this.secs = TimerFragment.this.secs % 60;
                TimerFragment.this.milliseconds = (int) (updatedTime % 100);
                TimerFragment.this.solve_time.setTextColor(-16711936);
                TextView access$600 = TimerFragment.this.solve_time;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("");
                stringBuilder.append(String.format(Locale.getDefault(), str, Integer.valueOf(TimerFragment.this.mins)));
                stringBuilder.append(":");
                stringBuilder.append(String.format(Locale.getDefault(), str, Integer.valueOf(TimerFragment.this.secs)));
                stringBuilder.append(".");
                stringBuilder.append(String.format(Locale.getDefault(), str, Integer.valueOf(TimerFragment.this.milliseconds)));
                access$600.setText(stringBuilder.toString());
                TimerFragment.this.mIncomingHandler.postDelayed(this, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private LinearLayout statistics_container;

    public TimerFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.fragment_timer, container, false);
        setHasOptionsMenu(true);
        this.solve_time = this.root.findViewById(R.id.solve_time);
        ImageButton save_solve_btn = this.root.findViewById(R.id.save_solve_btn);
        this.delete_solve_btn = this.root.findViewById(R.id.delete_solve_btn);
        this.dnf_solve_btn = this.root.findViewById(R.id.dnf_solve_btn);
        this.plus_2_btn = this.root.findViewById(R.id.plus_2_btn);
        this.cube_size_spinner = this.root.findViewById(R.id.cube_size_spinner);
        this.scrambleSpace = this.root.findViewById(R.id.scramble_text);
        TextView scrambleDescription = this.root.findViewById(R.id.scramble_header);
        this.command_bar = this.root.findViewById(R.id.command_bar);
        this.cube_timer_options = this.root.findViewById(R.id.cube_timer_options);
        this.scramble_card = this.root.findViewById(R.id.scramble_card);
        this.statistics_container = this.root.findViewById(R.id.statistics_container);
        this.command_bar.setVisibility(View.INVISIBLE);
        this.solve_time.setOnClickListener(this);
        save_solve_btn.setOnClickListener(this);
        this.delete_solve_btn.setOnClickListener(this);
        this.dnf_solve_btn.setOnClickListener(this);
        this.plus_2_btn.setOnClickListener(this);
        this.cube_size_spinner.setOnItemSelectedListener(this);
        List<String> cube_size = new ArrayList();
        cube_size.add("3x3");
        cube_size.add("2x2");
        cube_size.add("4x4");
        cube_size.add("5x5");
        cube_size.add("6x6");
        cube_size.add("7x7");
        cube_size.add("8x8");
        cube_size.add("9x9");
        cube_size.add("Megaminx");
        cube_size.add("Pyraminx");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, cube_size);
        cube_size_spinner.setAdapter(dataAdapter);
        this.cube_size_spinner.setAdapter(dataAdapter);
        this.cube_size_text = this.cube_size_spinner.getSelectedItem().toString();
        createScramble();
        this.scrambleSpace.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                TimerFragment.this.createScramble();
                Toast.makeText(TimerFragment.this.getContext(), "New Scramble", Toast.LENGTH_SHORT).show();
            }
        });
        Box boxFor = ObjectBox.getBoxStore().boxFor(Solve.class);
        this.solveBox = boxFor;
        ArrayList<Solve> solves = (ArrayList) boxFor.getAll();
        setUpStatistics();
        return this.root;
    }

    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.actionbar_statistics).setVisible(true).setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                TimerFragment.this.startActivity(new Intent(TimerFragment.this.getContext(), StatisticsActivity.class));
                return true;
            }
        });
        super.onPrepareOptionsMenu(menu);
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
                return;
        }
    }

    private void saveSolve() {
        CharSequence s = DateFormat.format("dd-MM-yyyy hh:mm:ss", new Date().getTime());
        Solve currentSolve = new Solve();
        currentSolve.setSolve_cube_size(this.cube_size_text);
        currentSolve.setSolve_date(s.toString());
        currentSolve.setSolve_time(this.solve_time.getText().toString());
        currentSolve.setSolve_milliseconds(getMilliseconds(this.mins, this.secs, this.milliseconds));
        currentSolve.setSolve_scramble(this.scrambleSpace.getText().toString());
        StringBuilder stringBuilder = new StringBuilder();
        String str = "saveSolve: ";
        stringBuilder.append(str);
        stringBuilder.append(this.cube_size_text);
        String stringBuilder2 = stringBuilder.toString();
        String str2 = Constraints.TAG;
        Log.d(str2, stringBuilder2);
        stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.append(s.toString());
        Log.d(str2, stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.append(this.solve_time.getText().toString());
        Log.d(str2, stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.append(this.solve_milliseconds);
        Log.d(str2, stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.append(this.scrambleSpace.getText().toString());
        Log.d(str2, stringBuilder.toString());
        this.solveBox.put(currentSolve);
        this.cube_size_text = this.cube_size_spinner.getSelectedItem().toString();
        stringBuilder2 = this.solve_time.getText().toString();
        Context context = getContext();
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append("Saved time: ");
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append(" for ");
        stringBuilder3.append(this.cube_size_text);
        Toast.makeText(context, stringBuilder3.toString(), Toast.LENGTH_SHORT).show();
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
        new MaterialAlertDialogBuilder(requireContext()).setTitle("Delete Solve").setMessage("Are you sure you want to delete your current solve time?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                TimerFragment.this.solve_time.setText("00:00.00");
                TimerFragment.this.command_bar.setVisibility(View.INVISIBLE);
                dialog.cancel();
            }
        }).setNegativeButton(charSequence, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }

    private void dnfSolve() {
        CharSequence charSequence = "No";
        new MaterialAlertDialogBuilder(requireContext()).setTitle("DNF Solve").setMessage("Mark as DNF?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                TimerFragment.this.solve_time.setText("DNF");
                TimerFragment.this.solve_time.setGravity(17);
                TimerFragment.this.dnf_solve_btn.setVisibility(View.INVISIBLE);
                dialog.cancel();
            }
        }).setNegativeButton(charSequence, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }

    private void plus2Solve() {
        CharSequence charSequence = "No";
        new MaterialAlertDialogBuilder(requireContext()).setTitle("Solve Penalty").setMessage("Add Penalty to solve?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                TimerFragment timerFragment = TimerFragment.this;
                timerFragment.secs = timerFragment.secs + 2;
                TextView access$600 = TimerFragment.this.solve_time;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("");
                String str = "%02d";
                stringBuilder.append(String.format(Locale.getDefault(), str, Integer.valueOf(TimerFragment.this.mins)));
                stringBuilder.append(":");
                stringBuilder.append(String.format(Locale.getDefault(), str, Integer.valueOf(TimerFragment.this.secs)));
                stringBuilder.append(".");
                stringBuilder.append(String.format(Locale.getDefault(), "%03d", Integer.valueOf(TimerFragment.this.milliseconds)));
                access$600.setText(stringBuilder.toString());
                Toast.makeText(TimerFragment.this.getContext(), "+2 Penalty", Toast.LENGTH_SHORT).show();
                TimerFragment.this.plus_2_btn.setVisibility(View.INVISIBLE);
                dialog.cancel();
            }
        }).setNegativeButton(charSequence, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
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

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.cube_size_text = parent.getItemAtPosition(position).toString();
        Context context = parent.getContext();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Selected: ");
        stringBuilder.append(this.cube_size_text);
        Toast.makeText(context, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
        setUpStatistics();
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

    public void onNothingSelected(AdapterView<?> adapterView) {
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
        ArrayList<Solve> solves = getSolvesByCubeSize();
        ArrayList<Integer> solve_times = new ArrayList();
        Iterator it = solves.iterator();
        while (it.hasNext()) {
            solve_times.add(Integer.valueOf(((Solve) it.next()).solve_milliseconds));
        }
        int total_time = 0;
        Iterator it2 = solves.iterator();
        while (it2.hasNext()) {
            total_time += ((Solve) it2.next()).solve_milliseconds;
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
        StringBuilder stringBuilder = new StringBuilder();
        String str = "";
        stringBuilder.append(str);
        stringBuilder.append(getMeanTimeByCubeSize(total_time, solves.size()));
        mean_time_time.setText(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.append(getSolvesByCubeSize().size());
        stringBuilder.append(" Solves");
        count_time_time.setText(stringBuilder.toString());
        avg_5.setText(solveAvg(total_time, 5));
        avg_12.setText(solveAvg(total_time, 12));
        avg_50.setText(solveAvg(total_time, 50));
        avg_100.setText(solveAvg(total_time, 100));
    }

    private ArrayList<Solve> getSolvesByCubeSize() {
       return (ArrayList) this.solveBox.query().equal(Solve_.solve_cube_size, this.cube_size_text).build().find();
    }

    private String getFastestSolveByCubeSize(ArrayList<Integer> solve_times) {
        String fastest_time = "";
        int temp = solve_times.get(0).intValue();
        for (int i = 1; i < solve_times.size(); i++) {
            if (temp > solve_times.get(i).intValue()) {
                temp = solve_times.get(i).intValue();
            }
        }
        return formatTime(temp);
    }

    private String getWorstSolveByCubeSize(ArrayList<Integer> solve_times) {
        String worst_time = "";
        int temp = solve_times.get(0).intValue();
        for (int i = 1; i < solve_times.size(); i++) {
            if (temp < solve_times.get(i).intValue()) {
                temp = solve_times.get(i).intValue();
            }
        }
        return formatTime(temp);
    }

    private String getMeanTimeByCubeSize(int total_time, int total_solves) {
        String mean_time = "";
        return formatTime(total_time / total_solves);
    }

    private String solveAvg(int total_time, int avg_of) {
        String avg = "";
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
        stringBuilder.append(String.format(Locale.getDefault(), getString(R.string.two_decimals), Integer.valueOf(stats_mins)));
        stringBuilder.append(":");
        stringBuilder.append(String.format(Locale.getDefault(), getString(R.string.two_decimals), Integer.valueOf(stats_secs)));
        stringBuilder.append(".");
        stringBuilder.append(String.format(Locale.getDefault(), getString(R.string.two_decimals), Integer.valueOf(stats_ms)));
        return stringBuilder.toString();
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
