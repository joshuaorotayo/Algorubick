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
    private final long timeSwapBuff = 0;
    HashMap<String, Integer> algImageMap = new HashMap();
    ArrayList<ImageView> algImages = new ArrayList();
    int correct = 0;
    int counter;
    Algorithm currentAlgorithm;
    Handler mIncomingHandler = new Handler(new IncomingHandlerCallback());
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
    private final Runnable updateTimerThread = new Runnable() {
        public void run() {
            String str = "%02d";
            try {
                PracticeAlgorithmFragment.this.timeInMilliseconds = SystemClock.uptimeMillis() - PracticeAlgorithmFragment.this.startTime;
                PracticeAlgorithmFragment.this.updatedTime = PracticeAlgorithmFragment.this.timeSwapBuff + PracticeAlgorithmFragment.this.timeInMilliseconds;
                PracticeAlgorithmFragment.this.secs = (int) (PracticeAlgorithmFragment.this.updatedTime / 1000);
                PracticeAlgorithmFragment.this.mins = PracticeAlgorithmFragment.this.secs / 60;
                PracticeAlgorithmFragment.this.secs %= 60;
                PracticeAlgorithmFragment.this.milliseconds = (int) (PracticeAlgorithmFragment.this.updatedTime % 100);
                TextView access$500 = PracticeAlgorithmFragment.this.practiceAlgorithmTimer;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("");
                stringBuilder.append(String.format(Locale.getDefault(), str, Integer.valueOf(PracticeAlgorithmFragment.this.mins)));
                stringBuilder.append(":");
                stringBuilder.append(String.format(Locale.getDefault(), str, Integer.valueOf(PracticeAlgorithmFragment.this.secs)));
                stringBuilder.append(".");
                stringBuilder.append(String.format(Locale.getDefault(), str, Integer.valueOf(PracticeAlgorithmFragment.this.milliseconds)));
                access$500.setText(stringBuilder.toString());
                PracticeAlgorithmFragment.this.mIncomingHandler.postDelayed(this, 0);
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
            this.mParam1 = integerArrayList;
            Iterator it = integerArrayList.iterator();
            while (it.hasNext()) {
                this.mParam2.add(((long) ((Integer) it.next()).intValue()) + 1);
            }
        }
        Box boxFor = ObjectBox.getBoxStore().boxFor(Algorithm.class);
        this.algorithmBox = boxFor;
        boxFor.getAll();
        this.algorithmArrayList = (ArrayList) this.algorithmBox.get(this.mParam2);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_algorithm_practice, container, false);
        ((StudyAlgorithmActivity) getActivity()).getSupportActionBar().setTitle("Practice Algorithm");
        this.learn_alg_inputspace = this.view.findViewById(R.id.learn_alg_inputspace);
        this.learn_alg_name = this.view.findViewById(R.id.practice_alg_name);
        this.learn_alg_inputspace.setOnClickListener(this);
        this.learn_alg_correct_practiced_number = this.view.findViewById(R.id.learn_alg_correct_practiced_number);
        this.practiceAlgorithmTimer = this.view.findViewById(R.id.practice_alg_timer);
        this.checkAlg = this.view.findViewById(R.id.check_alg);
        this.numbers_section = this.view.findViewById(R.id.numbers_section);
        this.practiceAlgTimerSection = this.view.findViewById(R.id.practice_alg_timer_section);
        setupHashmap();
        setupImageViews();
        this.checkAlg.setOnClickListener(this);
        setupKeyboard();
        setupDialogs();
        setupSession();
        startSession();
        startTimer();
        return this.view;
    }

    private void setupKeyboard() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(R.id.learn_alg_keyboard_space, new KeyboardFragment(this.learn_alg_inputspace));
        ft.commit();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.check_alg) {
            checkCorrect();
        }
    }

    private void checkCorrect() {
        if (this.learn_alg_inputspace.getText().toString().matches(this.currentAlgorithm.getAlg())) {
            this.correctDialog.show();
            this.correct++;
        } else {
            this.incorrectDialog.show();
        }
        clearPracticeSpace();
        int i = this.practiced_count + 1;
        this.practiced_count = i;
        this.currentAlgorithm.setPracticed_number_int(i);
        this.currentAlgorithm.setPracticed_correctly_int(this.correct);
        this.algorithmBox.put(this.currentAlgorithm);
        startSession();
    }

    private void setupDialogs() {
        this.incorrectDialog = new MaterialAlertDialogBuilder(getContext()).setMessage("Incorrect Algorithm Inputted. Try Again").setTitle("Incorrect").setIcon(R.drawable.incorrect_48_r).setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == 4) {
                    dialog.dismiss();
                }
                return true;
            }
        }).setCancelable(true);
        this.correctDialog = new MaterialAlertDialogBuilder(getContext()).setMessage("Correct Algorithm Inputted. Keep it up").setTitle("Correct").setIcon(R.drawable.correct_48_g).setCancelable(true);
    }

    private void setupSession() {
        if (this.mParam2.size() == 1) {
            this.sessionLength = 5;
        }
        for (int i = 0; i < this.sessionLength; i++) {
            Iterator it = this.mParam2.iterator();
            while (it.hasNext()) {
                Algorithm algorithm = (Algorithm) this.algorithmBox.get(((Long) it.next()).longValue());
                this.currentAlgorithm = algorithm;
                this.session.add(algorithm);
            }
        }
        Collections.shuffle(this.session);
        startSession();
    }

    private void startSession() {
        if (this.sessionPosition >= this.session.size()) {
            Toast.makeText(getContext(), "Do you want to start this session again", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
            return;
        }
        Algorithm algorithm = this.session.get(this.sessionPosition);
        this.currentAlgorithm = algorithm;
        this.currentAlgorithm = (Algorithm) this.algorithmBox.get(algorithm.id);
        ((StudyAlgorithmActivity) getActivity()).getSupportActionBar().setSubtitle(this.currentAlgorithm.getAlg_name());
        this.learn_alg_name.setText(this.currentAlgorithm.alg_name);
        this.correct = this.currentAlgorithm.getPracticed_correctly_int();
        this.practiced_count = this.currentAlgorithm.getPracticed_number_int();
        TextView textView = this.learn_alg_correct_practiced_number;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.correct);
        stringBuilder.append(" / ");
        stringBuilder.append(this.practiced_count);
        textView.setText(stringBuilder.toString());
        setupAlgImages();
        startTimer();
        this.sessionPosition++;
    }

    private void clearPracticeSpace() {
        this.learn_alg_inputspace.setText("");
        this.mIncomingHandler.removeCallbacks(this.updateTimerThread);
        this.practiceAlgorithmTimer.setText("00:00.00");
        this.mins = 0;
        this.secs = 0;
        this.milliseconds = 0;
    }

    private void startTimer() {
        this.startTime = SystemClock.uptimeMillis();
        this.mIncomingHandler.postDelayed(this.updateTimerThread, 1000);
    }

    private void setupHashmap() {
        this.algImageMap.put("R", Integer.valueOf(R.drawable.clockwise_r));
        this.algImageMap.put("L", Integer.valueOf(R.drawable.clockwise_l));
        this.algImageMap.put("F", Integer.valueOf(R.drawable.clockwise_f));
        this.algImageMap.put("B", Integer.valueOf(R.drawable.clockwise_b));
        this.algImageMap.put("U", Integer.valueOf(R.drawable.clockwise_u));
        this.algImageMap.put("D", Integer.valueOf(R.drawable.clockwise_d));
        this.algImageMap.put("R'", Integer.valueOf(R.drawable.anticlockwise_r));
        this.algImageMap.put("L'", Integer.valueOf(R.drawable.anticlockwise_l));
        this.algImageMap.put("F'", Integer.valueOf(R.drawable.anticlockwise_f));
        this.algImageMap.put("B'", Integer.valueOf(R.drawable.anticlockwise_b));
        this.algImageMap.put("U'", Integer.valueOf(R.drawable.anticlockwise_u));
        this.algImageMap.put("D'", Integer.valueOf(R.drawable.anticlockwise_d));
        this.algImageMap.put("R2", Integer.valueOf(R.drawable.double_r));
        this.algImageMap.put("L2", Integer.valueOf(R.drawable.double_l));
        this.algImageMap.put("F2", Integer.valueOf(R.drawable.double_f));
        this.algImageMap.put("B2", Integer.valueOf(R.drawable.double_b));
        this.algImageMap.put("U2", Integer.valueOf(R.drawable.double_u));
        this.algImageMap.put("D2", Integer.valueOf(R.drawable.double_d));
        this.algImageMap.put("r", Integer.valueOf(R.drawable.two_right));
        this.algImageMap.put("l", Integer.valueOf(R.drawable.two_left));
        this.algImageMap.put("f", Integer.valueOf(R.drawable.two_front));
        this.algImageMap.put("b", Integer.valueOf(R.drawable.two_back));
        this.algImageMap.put("u", Integer.valueOf(R.drawable.two_up));
        this.algImageMap.put("d", Integer.valueOf(R.drawable.two_down));
        this.algImageMap.put("r'", Integer.valueOf(R.drawable.dbl_r_prime));
        this.algImageMap.put("l'", Integer.valueOf(R.drawable.dbl_l_prime));
        this.algImageMap.put("f'", Integer.valueOf(R.drawable.dbl_f_prime));
        this.algImageMap.put("b'", Integer.valueOf(R.drawable.dbl_b_prime));
        this.algImageMap.put("u'", Integer.valueOf(R.drawable.dbl_u_prime));
        this.algImageMap.put("d'", Integer.valueOf(R.drawable.dbl_d_prime));
        this.algImageMap.put("r2", Integer.valueOf(R.drawable.dbl_r_two));
        this.algImageMap.put("l2", Integer.valueOf(R.drawable.dbl_l_two));
        this.algImageMap.put("f2", Integer.valueOf(R.drawable.dbl_f_two));
        this.algImageMap.put("b2", Integer.valueOf(R.drawable.dbl_b_two));
        this.algImageMap.put("u2", Integer.valueOf(R.drawable.dbl_u_two));
        this.algImageMap.put("d2", Integer.valueOf(R.drawable.dbl_d_two));
        this.algImageMap.put("X", Integer.valueOf(R.drawable.x_rotation));
        this.algImageMap.put("Y", Integer.valueOf(R.drawable.y_rotation));
        this.algImageMap.put("Z", Integer.valueOf(R.drawable.z_rotation));
        this.algImageMap.put("X'", Integer.valueOf(R.drawable.x_prime_rotation));
        this.algImageMap.put("Y'", Integer.valueOf(R.drawable.y_prime_rotation));
        this.algImageMap.put("Z'", Integer.valueOf(R.drawable.z_prime_rotation));
        this.algImageMap.put("E", Integer.valueOf(R.drawable.e_slice));
        this.algImageMap.put("S", Integer.valueOf(R.drawable.s_slice));
        this.algImageMap.put("M", Integer.valueOf(R.drawable.m_slice));
        this.algImageMap.put("E2", Integer.valueOf(R.drawable.e2_slice));
        this.algImageMap.put("S2", Integer.valueOf(R.drawable.s2_slice));
        this.algImageMap.put("M2", Integer.valueOf(R.drawable.m2_slice));
        this.algImageMap.put("E'", Integer.valueOf(R.drawable.e_prime));
        this.algImageMap.put("S'", Integer.valueOf(R.drawable.s_prime));
        this.algImageMap.put("M'", Integer.valueOf(R.drawable.m_prime));
    }

    private void setupImageViews() {
        ImageView algImage1 = this.view.findViewById(R.id.algImage1);
        ImageView algImage2 = this.view.findViewById(R.id.algImage2);
        ImageView algImage3 = this.view.findViewById(R.id.algImage3);
        ImageView algImage4 = this.view.findViewById(R.id.algImage4);
        ImageView algImage5 = this.view.findViewById(R.id.algImage5);
        ImageView algImage6 = this.view.findViewById(R.id.algImage6);
        ImageView algImage7 = this.view.findViewById(R.id.algImage7);
        ImageView algImage8 = this.view.findViewById(R.id.algImage8);
        ImageView algImage9 = this.view.findViewById(R.id.algImage9);
        ImageView algImage10 = this.view.findViewById(R.id.algImage10);
        ImageView algImage11 = this.view.findViewById(R.id.algImage11);
        ImageView algImage12 = this.view.findViewById(R.id.algImage12);
        ImageView algImage13 = this.view.findViewById(R.id.algImage13);
        ImageView algImage14 = this.view.findViewById(R.id.algImage14);
        ImageView algImage15 = this.view.findViewById(R.id.algImage15);
        ImageView algImage16 = this.view.findViewById(R.id.algImage16);
        ImageView algImage17 = this.view.findViewById(R.id.algImage17);
        ImageView algImage18 = this.view.findViewById(R.id.algImage18);
        ImageView algImage19 = this.view.findViewById(R.id.algImage19);
        ImageView algImage20 = this.view.findViewById(R.id.algImage20);
        ImageView algImage21 = this.view.findViewById(R.id.algImage21);
        ImageView algImage22 = this.view.findViewById(R.id.algImage22);
        ImageView algImage23 = this.view.findViewById(R.id.algImage23);
        ImageView algImage24 = this.view.findViewById(R.id.algImage24);
        ImageView algImage25 = this.view.findViewById(R.id.algImage25);
        ImageView algImage26 = this.view.findViewById(R.id.algImage26);
        ImageView algImage27 = this.view.findViewById(R.id.algImage27);
        ImageView algImage28 = this.view.findViewById(R.id.algImage28);
        ImageView algImage29 = this.view.findViewById(R.id.algImage29);
        ImageView algImage30 = this.view.findViewById(R.id.algImage30);
        ImageView algImage31 = this.view.findViewById(R.id.algImage31);
        ImageView algImage32 = this.view.findViewById(R.id.algImage32);
        ImageView algImage33 = this.view.findViewById(R.id.algImage33);
        ImageView algImage34 = this.view.findViewById(R.id.algImage34);
        ImageView algImage35 = this.view.findViewById(R.id.algImage35);
        ImageView algImage36 = this.view.findViewById(R.id.algImage36);
        this.algImages.addAll(Arrays.asList(algImage1, algImage2, algImage3, algImage4, algImage5, algImage6, algImage7, algImage8, algImage9, algImage10, algImage11, algImage12, algImage13, algImage14, algImage15, algImage16, algImage17, algImage18, algImage19, algImage20, algImage21, algImage22, algImage23, algImage24, algImage25, algImage26, algImage27, algImage28, algImage29, algImage30, algImage31, algImage32, algImage33, algImage34, algImage35, algImage36));
    }

    private void setupAlgImages() {
        int i;
        String[] steps = this.currentAlgorithm.alg.split(",");
        ArrayList<Integer> stepIDs = new ArrayList();
        for (String step : steps) {
            stepIDs.add(this.algImageMap.get(step));
        }
        for (i = 0; i < stepIDs.size(); i++) {
            ImageView newImage = this.algImages.get(i);
            newImage.setImageResource(stepIDs.get(i).intValue());
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
            PracticeAlgorithmFragment.this.practiceAlgorithmTimer.setTextColor(-16711936);
            PracticeAlgorithmFragment.this.practiceAlgorithmTimer.setText(PracticeAlgorithmFragment.this.counter);
            return true;
        }
    }
}
