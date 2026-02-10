package com.jorotayo.algorubickrevamped.ui.home;

import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jorotayo.algorubickrevamped.KeyboardFragment;
import com.jorotayo.algorubickrevamped.ObjectBox;
import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import io.objectbox.Box;

public class Fragment_PracticeAlgorithm extends Fragment implements OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private final ArrayList<Long> mParam2 = new ArrayList();
    private final long timeSwapBuff = 0;
    HashMap<String, Integer> algImageMap = new HashMap();
    int correct = 0;
    int counter;
    Algorithm currentAlgorithm;
    private final Handler handler = new Handler(Looper.getMainLooper());
    int milliseconds;
    int mins;
    LinearLayout numbers_section;
    LinearLayout practiceAlgTimerSection;
    int practiced_count = 0;
    int secs;
    ArrayList<Algorithm> session = new ArrayList();
    int sessionLength = 2;
    int sessionPosition = 0;
    private ArrayList algorithmArrayList = new ArrayList();
    private Box algorithmBox;
    private Button checkAlg;
    private MaterialAlertDialogBuilder correctDialog;
    private MaterialAlertDialogBuilder incorrectDialog;
    private TextView learn_alg_correct_practiced_number;
    private EditText learn_alg_inputspace;
    private TextView learn_alg_name;
    private ArrayList<Integer> mParam1;
    private TextView practiceAlgorithmTimer;
    private long startTime;
    private long timeInMilliseconds = 0;
    private long updatedTime = 0;
    private AlgStepAdapter stepAdapter;
    private static final long TIMER_INTERVAL = 100L;

    private final Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsed = SystemClock.uptimeMillis() - startTime;

            int totalSeconds = (int) (elapsed / 1000);
            int mins = totalSeconds / 60;
            int secs = totalSeconds % 60;
            int millis = (int) (elapsed % 1000) / 10;

            practiceAlgorithmTimer.setText(
                    String.format(Locale.getDefault(),
                            "%02d:%02d.%02d", mins, secs, millis)
            );

            handler.postDelayed(this, TIMER_INTERVAL);
        }
    };

    private View view;

    public static Fragment_PracticeAlgorithm newInstance(ArrayList<Integer> param1) {
        Fragment_PracticeAlgorithm fragment = new Fragment_PracticeAlgorithm();
        Bundle args = new Bundle();
        args.putIntegerArrayList(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ArrayList integerArrayList = getArguments().getIntegerArrayList(ARG_PARAM1);
            mParam1 = integerArrayList;
            Iterator it = integerArrayList.iterator();
            while (it.hasNext()) {
                mParam2.add(((long) ((Integer) it.next()).intValue()) + 1);
            }
        }
        Box boxFor = ObjectBox.getBoxStore().boxFor(Algorithm.class);
        algorithmBox = boxFor;
        boxFor.getAll();
        algorithmArrayList = (ArrayList) algorithmBox.get(mParam2);

        setupHashmap();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_algorithm_practice, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Activity_StudyAlgorithm activity = (Activity_StudyAlgorithm) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Practice Algorithm");
        }

        learn_alg_inputspace = view.findViewById(R.id.learn_alg_inputspace);
        learn_alg_name = view.findViewById(R.id.practice_alg_name);
        learn_alg_inputspace.setOnClickListener(this);
        learn_alg_correct_practiced_number = view.findViewById(R.id.learn_alg_correct_practiced_number);
        practiceAlgorithmTimer = view.findViewById(R.id.practice_alg_timer);
        checkAlg = view.findViewById(R.id.check_alg);
        numbers_section = view.findViewById(R.id.numbers_section);
        practiceAlgTimerSection = view.findViewById(R.id.practice_alg_timer_section);

        RecyclerView recyclerView = view.findViewById(R.id.alg_steps_recycler);

// Flexbox layout manager
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.CENTER); // center items in each row
        recyclerView.setLayoutManager(layoutManager);

        stepAdapter = new AlgStepAdapter(getContext(), 6, 3);
        recyclerView.setAdapter(stepAdapter);
        recyclerView.setHasFixedSize(true);

        checkAlg.setOnClickListener(this);
        setupKeyboard();
        setupDialogs();
        setupSession();
        startSession();
        startTimer();
    }

    private void setupKeyboard() {
        if (getChildFragmentManager().findFragmentById(R.id.learn_alg_keyboard_space) == null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.learn_alg_keyboard_space, new KeyboardFragment(learn_alg_inputspace))
                    .commit();
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.check_alg) {
            checkCorrect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
    }
    private void checkCorrect() {
        if (learn_alg_inputspace.getText().toString().trim().equals(currentAlgorithm.getAlg())){
            correctDialog.show();
            correct++;
        } else {
            incorrectDialog.show();
        }
        clearPracticeSpace();
        int i = practiced_count + 1;
        practiced_count = i;
        currentAlgorithm.setPracticed_number_int(i);
        currentAlgorithm.setPracticed_correctly_int(correct);
        algorithmBox.put(currentAlgorithm);
        startSession();
    }

    private void setupDialogs() {
        incorrectDialog = new MaterialAlertDialogBuilder(getContext()).setMessage("Incorrect Algorithm Inputted. Try Again").setTitle("Incorrect").setIcon(R.drawable.incorrect_48_r).setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == 4) {
                    dialog.dismiss();
                }
                return true;
            }
        }).setCancelable(true);
        correctDialog = new MaterialAlertDialogBuilder(getContext()).setMessage("Correct Algorithm Inputted. Keep it up").setTitle("Correct").setIcon(R.drawable.correct_48_g).setCancelable(true);
    }

    private void stopTimer() {
        handler.removeCallbacks(updateTimerRunnable);
    }

    private void setupSession() {
        if (mParam2.size() == 1) {
            sessionLength = 5;
        }
        for (int i = 0; i < sessionLength; i++) {
            for (Long aLong : mParam2) {
                Algorithm algorithm = (Algorithm) algorithmBox.get(aLong);
                currentAlgorithm = algorithm;
                session.add(algorithm);
            }
        }
        Collections.shuffle(session);
        startSession();
    }

    private void startSession() {
        if (sessionPosition >= session.size()) {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
            return;
        }

        currentAlgorithm = session.get(sessionPosition++);
        bindAlgorithm(currentAlgorithm);
    }

    private void bindAlgorithm(Algorithm alg) {
        learn_alg_name.setText(alg.getAlg_name());

        correct = alg.getPracticed_correctly_int();
        practiced_count = alg.getPracticed_number_int();

        learn_alg_correct_practiced_number.setText(correct + " / " + practiced_count);

        setupAlgImages();
        clearPracticeSpace();
        startTimer();
    }

    private void clearPracticeSpace() {
        learn_alg_inputspace.setText("");
        handler.removeCallbacks(updateTimerRunnable);
        practiceAlgorithmTimer.setText("00:00.00");
        mins = 0;
        secs = 0;
        milliseconds = 0;
    }

    private void startTimer() {
        startTime = SystemClock.uptimeMillis();
        handler.post(updateTimerRunnable);
    }

    private void setupHashmap() {
        algImageMap.put("R", R.drawable.clockwise_r);
        algImageMap.put("L", R.drawable.clockwise_l);
        algImageMap.put("F", R.drawable.clockwise_f);
        algImageMap.put("B", R.drawable.clockwise_b);
        algImageMap.put("U", R.drawable.clockwise_u);
        algImageMap.put("D", R.drawable.clockwise_d);
        algImageMap.put("R'", R.drawable.anticlockwise_r);
        algImageMap.put("L'", R.drawable.anticlockwise_l);
        algImageMap.put("F'", R.drawable.anticlockwise_f);
        algImageMap.put("B'", R.drawable.anticlockwise_b);
        algImageMap.put("U'", R.drawable.anticlockwise_u);
        algImageMap.put("D'", R.drawable.anticlockwise_d);
        algImageMap.put("R2", R.drawable.double_r);
        algImageMap.put("L2", R.drawable.double_l);
        algImageMap.put("F2", R.drawable.double_f);
        algImageMap.put("B2", R.drawable.double_b);
        algImageMap.put("U2", R.drawable.double_u);
        algImageMap.put("D2", R.drawable.double_d);
        algImageMap.put("r", R.drawable.two_right);
        algImageMap.put("l", R.drawable.two_left);
        algImageMap.put("f", R.drawable.two_front);
        algImageMap.put("b", R.drawable.two_back);
        algImageMap.put("u", R.drawable.two_up);
        algImageMap.put("d", R.drawable.two_down);
        algImageMap.put("r'", R.drawable.dbl_r_prime);
        algImageMap.put("l'", R.drawable.dbl_l_prime);
        algImageMap.put("f'", R.drawable.dbl_f_prime);
        algImageMap.put("b'", R.drawable.dbl_b_prime);
        algImageMap.put("u'", R.drawable.dbl_u_prime);
        algImageMap.put("d'", R.drawable.dbl_d_prime);
        algImageMap.put("r2", R.drawable.dbl_r_two);
        algImageMap.put("l2", R.drawable.dbl_l_two);
        algImageMap.put("f2", R.drawable.dbl_f_two);
        algImageMap.put("b2", R.drawable.dbl_b_two);
        algImageMap.put("u2", R.drawable.dbl_u_two);
        algImageMap.put("d2", R.drawable.dbl_d_two);
        algImageMap.put("X", R.drawable.x_rotation);
        algImageMap.put("Y", R.drawable.y_rotation);
        algImageMap.put("Z", R.drawable.z_rotation);
        algImageMap.put("X'", R.drawable.x_prime_rotation);
        algImageMap.put("Y'", R.drawable.y_prime_rotation);
        algImageMap.put("Z'", R.drawable.z_prime_rotation);
        algImageMap.put("E", R.drawable.e_slice);
        algImageMap.put("S", R.drawable.s_slice);
        algImageMap.put("M", R.drawable.m_slice);
        algImageMap.put("E2", R.drawable.e2_slice);
        algImageMap.put("S2", R.drawable.s2_slice);
        algImageMap.put("M2", R.drawable.m2_slice);
        algImageMap.put("E'", R.drawable.e_prime);
        algImageMap.put("S'", R.drawable.s_prime);
        algImageMap.put("M'", R.drawable.m_prime);
    }

    private void setupAlgImages() {
        String[] steps = currentAlgorithm.getAlg().split(",");
        List<Integer> stepIcons = new ArrayList<>();

        for (String step : steps) {
            Integer icon = algImageMap.get(step.trim());
            if (icon != null) {
                stepIcons.add(icon);
            }
        }

        stepAdapter.submitSteps(stepIcons);
    }
}
