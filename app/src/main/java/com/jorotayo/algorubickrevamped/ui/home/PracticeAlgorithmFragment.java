package com.jorotayo.algorubickrevamped.ui.home;

import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jorotayo.algorubickrevamped.KeyboardFragment;
import com.jorotayo.algorubickrevamped.ObjectBox;
import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import io.objectbox.Box;

public class PracticeAlgorithmFragment extends Fragment implements OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private final ArrayList<Long> mParam2 = new ArrayList();
    final HashMap<String, Integer> algImageMap = new HashMap();
    final ArrayList<ImageView> algImages = new ArrayList();
    int correct = 0;
    int counter;
    Algorithm currentAlgorithm;
    final Handler mIncomingHandler = new Handler(new IncomingHandlerCallback());
    int milliseconds;
    int mins;
    LinearLayout numbers_section;
    LinearLayout practiceAlgTimerSection;
    int practiced_count = 0;
    int secs;
    final ArrayList<Algorithm> session = new ArrayList();
    int sessionLength = 2;
    int sessionPosition = 0;
    private Box algorithmBox;
    private MaterialAlertDialogBuilder correctDialog;
    private MaterialAlertDialogBuilder incorrectDialog;
    private TextView learn_alg_correct_practiced_number;
    private EditText learn_alg_inputspace;
    private TextView learn_alg_name;
    private TextView practiceAlgorithmTimer;
    private long startTime;

    private final Runnable updateTimerThread = new Runnable() {
        public void run() {
            String str = "%02d";
            try {
                long timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
                long timeSwapBuff = 0;
                long updatedTime = timeSwapBuff + timeInMilliseconds;
                secs = (int) (updatedTime / 1000);
                mins = secs / 60;
                secs %= 60;
                milliseconds = (int) (updatedTime % 100);
                TextView access$500 = practiceAlgorithmTimer;
                String stringBuilder = "" +
                        String.format(Locale.getDefault(), str, mins) +
                        ":" +
                        String.format(Locale.getDefault(), str, secs) +
                        "." +
                        String.format(Locale.getDefault(), str, milliseconds);
                access$500.setText(stringBuilder);
                mIncomingHandler.postDelayed(this, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private View view;

    public static PracticeAlgorithmFragment newInstance(ArrayList<Integer> param1) {
        PracticeAlgorithmFragment fragment = new PracticeAlgorithmFragment();
        Bundle args = new Bundle();
        args.putIntegerArrayList(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ArrayList integerArrayList = getArguments().getIntegerArrayList(ARG_PARAM1);
            Iterator it = integerArrayList.iterator();
            while (it.hasNext()) {
                mParam2.add(((long) ((Integer) it.next()).intValue()) + 1);
            }
        }
        Box boxFor = ObjectBox.getBoxStore().boxFor(Algorithm.class);
        algorithmBox = boxFor;
        boxFor.getAll();
        ArrayList algorithmArrayList = (ArrayList) algorithmBox.get(mParam2);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_algorithm_practice, container, false);
        ((StudyAlgorithmActivity) getActivity()).getSupportActionBar().setTitle("Practice Algorithm");
        learn_alg_inputspace = view.findViewById(R.id.learn_alg_inputspace);
        learn_alg_name = view.findViewById(R.id.practice_alg_name);
        learn_alg_inputspace.setOnClickListener(this);
        learn_alg_correct_practiced_number = view.findViewById(R.id.learn_alg_correct_practiced_number);
        practiceAlgorithmTimer = view.findViewById(R.id.practice_alg_timer);
        Button checkAlg = view.findViewById(R.id.check_alg);
        numbers_section = view.findViewById(R.id.numbers_section);
        practiceAlgTimerSection = view.findViewById(R.id.practice_alg_timer_section);
        setupHashmap();
        setupImageViews();
        checkAlg.setOnClickListener(this);
        setupKeyboard();
        setupDialogs();
        setupSession();
        startSession();
        startTimer();
        return view;
    }

    private void setupKeyboard() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(R.id.learn_alg_keyboard_space, new KeyboardFragment(learn_alg_inputspace));
        ft.commit();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.check_alg) {
            checkCorrect();
        }
    }

    private void checkCorrect() {
        if (learn_alg_inputspace.getText().toString().matches(currentAlgorithm.getAlg())) {
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

    private void setupSession() {
        if (mParam2.size() == 1) {
            sessionLength = 5;
        }
        for (int i = 0; i < sessionLength; i++) {
            Iterator it = mParam2.iterator();
            while (it.hasNext()) {
                Algorithm algorithm = (Algorithm) algorithmBox.get(((Long) it.next()).longValue());
                currentAlgorithm = algorithm;
                session.add(algorithm);
            }
        }
        Collections.shuffle(session);
        startSession();
    }

    private void startSession() {
        if (sessionPosition >= session.size()) {
            Toast.makeText(getContext(), "Do you want to start this session again", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
            return;
        }
        Algorithm algorithm = session.get(sessionPosition);
        currentAlgorithm = algorithm;
        currentAlgorithm = (Algorithm) algorithmBox.get(algorithm.id);
        ((StudyAlgorithmActivity) getActivity()).getSupportActionBar().setSubtitle(currentAlgorithm.getAlg_name());
        learn_alg_name.setText(currentAlgorithm.alg_name);
        correct = currentAlgorithm.getPracticed_correctly_int();
        practiced_count = currentAlgorithm.getPracticed_number_int();
        TextView textView = learn_alg_correct_practiced_number;
        String stringBuilder = correct +
                " / " +
                practiced_count;
        textView.setText(stringBuilder);
        setupAlgImages();
        startTimer();
        sessionPosition++;
    }

    private void clearPracticeSpace() {
        learn_alg_inputspace.setText("");
        mIncomingHandler.removeCallbacks(updateTimerThread);
        practiceAlgorithmTimer.setText("00:00.00");
        mins = 0;
        secs = 0;
        milliseconds = 0;
    }

    private void startTimer() {
        startTime = SystemClock.uptimeMillis();
        mIncomingHandler.postDelayed(updateTimerThread, 1000);
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

    private void setupImageViews() {
        ImageView algImage1 = view.findViewById(R.id.algImage1);
        ImageView algImage2 = view.findViewById(R.id.algImage2);
        ImageView algImage3 = view.findViewById(R.id.algImage3);
        ImageView algImage4 = view.findViewById(R.id.algImage4);
        ImageView algImage5 = view.findViewById(R.id.algImage5);
        ImageView algImage6 = view.findViewById(R.id.algImage6);
        ImageView algImage7 = view.findViewById(R.id.algImage7);
        ImageView algImage8 = view.findViewById(R.id.algImage8);
        ImageView algImage9 = view.findViewById(R.id.algImage9);
        ImageView algImage10 = view.findViewById(R.id.algImage10);
        ImageView algImage11 = view.findViewById(R.id.algImage11);
        ImageView algImage12 = view.findViewById(R.id.algImage12);
        ImageView algImage13 = view.findViewById(R.id.algImage13);
        ImageView algImage14 = view.findViewById(R.id.algImage14);
        ImageView algImage15 = view.findViewById(R.id.algImage15);
        ImageView algImage16 = view.findViewById(R.id.algImage16);
        ImageView algImage17 = view.findViewById(R.id.algImage17);
        ImageView algImage18 = view.findViewById(R.id.algImage18);
        ImageView algImage19 = view.findViewById(R.id.algImage19);
        ImageView algImage20 = view.findViewById(R.id.algImage20);
        ImageView algImage21 = view.findViewById(R.id.algImage21);
        ImageView algImage22 = view.findViewById(R.id.algImage22);
        ImageView algImage23 = view.findViewById(R.id.algImage23);
        ImageView algImage24 = view.findViewById(R.id.algImage24);
        ImageView algImage25 = view.findViewById(R.id.algImage25);
        ImageView algImage26 = view.findViewById(R.id.algImage26);
        ImageView algImage27 = view.findViewById(R.id.algImage27);
        ImageView algImage28 = view.findViewById(R.id.algImage28);
        ImageView algImage29 = view.findViewById(R.id.algImage29);
        ImageView algImage30 = view.findViewById(R.id.algImage30);
        ImageView algImage31 = view.findViewById(R.id.algImage31);
        ImageView algImage32 = view.findViewById(R.id.algImage32);
        ImageView algImage33 = view.findViewById(R.id.algImage33);
        ImageView algImage34 = view.findViewById(R.id.algImage34);
        ImageView algImage35 = view.findViewById(R.id.algImage35);
        ImageView algImage36 = view.findViewById(R.id.algImage36);
        algImages.addAll(Arrays.asList(algImage1, algImage2, algImage3, algImage4, algImage5, algImage6, algImage7, algImage8, algImage9, algImage10, algImage11, algImage12, algImage13, algImage14, algImage15, algImage16, algImage17, algImage18, algImage19, algImage20, algImage21, algImage22, algImage23, algImage24, algImage25, algImage26, algImage27, algImage28, algImage29, algImage30, algImage31, algImage32, algImage33, algImage34, algImage35, algImage36));
    }

    private void setupAlgImages() {
        int i;
        String[] steps = currentAlgorithm.alg.split(",");
        ArrayList<Integer> stepIDs = new ArrayList();
        for (String step : steps) {
            stepIDs.add(algImageMap.get(step));
        }
        for (i = 0; i < stepIDs.size(); i++) {
            ImageView newImage = algImages.get(i);
            newImage.setImageResource(stepIDs.get(i));
            newImage.setVisibility(View.VISIBLE);
        }
    }

    private class IncomingHandlerCallback implements Callback {
        private IncomingHandlerCallback() {
        }

        /* synthetic */ IncomingHandlerCallback(PracticeAlgorithmFragment practiceAlgorithmFragment) {
            this();
        }

        public boolean handleMessage(Message message) {
            practiceAlgorithmTimer.setTextColor(-16711936);
            practiceAlgorithmTimer.setText(counter);
            return true;
        }
    }
}
