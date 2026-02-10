package com.jorotayo.algorubickrevamped.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import io.objectbox.Box;

public class Fragment_LearnAlgorithm extends Fragment implements OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private final ArrayList<Long> mParam2 = new ArrayList<>();
    private final Random random = new Random();
    HashMap<String, Integer> algImageMap = new HashMap();
    Algorithm currentAlgorithm;
    private ArrayList algorithmArrayList = new ArrayList();
    private MaterialAlertDialogBuilder correctDialog;
    private MaterialAlertDialogBuilder incorrectDialog;
    private TextView learn_alg_alg;
    private EditText learn_alg_inputspace;
    private TextView learn_alg_name;
    private View view;
    private AlgStepAdapter stepAdapter;

    public static Fragment_LearnAlgorithm newInstance(ArrayList<Integer> param1) {
        Fragment_LearnAlgorithm fragment = new Fragment_LearnAlgorithm();
        Bundle args = new Bundle();
        args.putIntegerArrayList(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ArrayList<Integer> integerArrayList = getArguments().getIntegerArrayList(ARG_PARAM1);
            for (Integer o : integerArrayList) {
                mParam2.add(((long) o) + 1);
            }
        }
        Box<Algorithm> algorithmBox = ObjectBox.getBoxStore().boxFor(Algorithm.class);
        algorithmBox.getAll();
        algorithmArrayList = (ArrayList) algorithmBox.get(this.mParam2);

        setupHashmap();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_algorithm_learn, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Activity_StudyAlgorithm activity = (Activity_StudyAlgorithm) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Learn Algorithm");
        }

        learn_alg_inputspace = view.findViewById(R.id.learn_alg_inputspace);
        learn_alg_name = view.findViewById(R.id.learn_alg_name);
        learn_alg_alg = view.findViewById(R.id.learn_alg_alg);

        learn_alg_inputspace.setOnClickListener(this);
        view.findViewById(R.id.check_alg).setOnClickListener(this);

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

        Button button = view.findViewById(R.id.check_alg);
        button.setOnClickListener(this);
        setupKeyboard();
        setupDialogs();
        setupAlgorithm(nextAlgorithm());
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

    private Algorithm nextAlgorithm() {
        if (algorithmArrayList.isEmpty()) return null;
        return (Algorithm) algorithmArrayList.get(
                random.nextInt(algorithmArrayList.size())
        );
    }

    private void setupAlgorithm(Algorithm nextAlgorithm) {
        if (nextAlgorithm == null) return;
        currentAlgorithm = nextAlgorithm;

        Activity_StudyAlgorithm activity = (Activity_StudyAlgorithm) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Learn Algorithm");
            activity.getSupportActionBar().setSubtitle(this.currentAlgorithm.getAlg_name());
        }

        learn_alg_name.setText(this.currentAlgorithm.getAlg_name());
        learn_alg_alg.setText(this.currentAlgorithm.getAlg());
        setupAlgImages();
    }

    private void checkCorrect() {
        if (this.learn_alg_inputspace.getText().toString().trim().equals(currentAlgorithm.getAlg())) {
            correctDialog.show();
        } else {
            incorrectDialog.show();
        }
        setupAlgorithm(nextAlgorithm());
        learn_alg_inputspace.setText("");
    }

    private void setupDialogs() {
        if (getContext() == null) return;
        incorrectDialog = new MaterialAlertDialogBuilder(getContext()).setMessage("Incorrect Algorithm Inputted. Try Again").setTitle("Incorrect").setIcon(R.drawable.incorrect_48_r).setCancelable(true);
        correctDialog = new MaterialAlertDialogBuilder(getContext()).setMessage("Correct Algorithm Inputted. Keep it up").setTitle("Correct").setIcon(R.drawable.correct_48_g).setCancelable(true);
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
