package com.jorotayo.algorubickrevamped.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jorotayo.algorubickrevamped.KeyboardFragment;
import com.jorotayo.algorubickrevamped.ObjectBox;
import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import io.objectbox.Box;

public class Fragment_LearnAlgorithm extends Fragment implements OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private final ArrayList<Long> mParam2 = new ArrayList();
    HashMap<String, Integer> algImageMap = new HashMap();
    ArrayList<ImageView> algImages = new ArrayList();
    Algorithm currentAlgorithm;
    private ArrayList<Algorithm> algorithmArrayList = new ArrayList();
    private Button checkAlg;
    private MaterialAlertDialogBuilder correctDialog;
    private MaterialAlertDialogBuilder incorrectDialog;
    private TextView learn_alg_alg;
    private EditText learn_alg_inputspace;
    private TextView learn_alg_name;
    private ArrayList<Integer> mParam1;
    private View view;

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
            ArrayList integerArrayList = getArguments().getIntegerArrayList(ARG_PARAM1);
            mParam1 = integerArrayList;
            Iterator it = integerArrayList.iterator();
            while (it.hasNext()) {
                mParam2.add(((long) ((Integer) it.next()).intValue()) + 1);
            }
        }
        Box<Algorithm> algorithmBox = ObjectBox.getBoxStore().boxFor(Algorithm.class);
        algorithmBox.getAll();
        algorithmArrayList = (ArrayList) algorithmBox.get(this.mParam2);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_algorithm_learn, container, false);
        ((Activity_StudyAlgorithm) getActivity()).getSupportActionBar().setTitle("Learn Algorithm");
        ((Activity_StudyAlgorithm) getActivity()).getSupportActionBar().setSubtitle("Algorithm Name");
        learn_alg_inputspace = view.findViewById(R.id.learn_alg_inputspace);
        learn_alg_name = view.findViewById(R.id.learn_alg_name);
        learn_alg_alg = view.findViewById(R.id.learn_alg_alg);
        learn_alg_inputspace.setOnClickListener(this);
        setupHashmap();
        setupImageViews();
        Button button = view.findViewById(R.id.check_alg);
        checkAlg = button;
        button.setOnClickListener(this);
        setupKeyboard();
        setupDialogs();
        setupAlgorithm(nextAlgorithm());
        return view;
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

    private Algorithm nextAlgorithm() {
        return algorithmArrayList.get(new Random().nextInt(this.algorithmArrayList.size()));
    }

    private void setupAlgorithm(Algorithm nextAlgorithm) {
        currentAlgorithm = nextAlgorithm;
        ((Activity_StudyAlgorithm) getActivity()).getSupportActionBar().setSubtitle(this.currentAlgorithm.getAlg_name());
        learn_alg_name.setText(this.currentAlgorithm.getAlg_name());
        learn_alg_alg.setText(this.currentAlgorithm.getAlg());
        setupAlgImages();
    }

    private void checkCorrect() {
        if (this.learn_alg_inputspace.getText().toString().matches(this.currentAlgorithm.getAlg())) {
            correctDialog.show();
        } else {
            incorrectDialog.show();
        }
        setupAlgorithm(nextAlgorithm());
        learn_alg_inputspace.setText("");
    }

    private void setupDialogs() {
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
            stepIDs.add(this.algImageMap.get(step));
        }
        for (i = 0; i < stepIDs.size(); i++) {
            ImageView newImage = algImages.get(i);
            newImage.setImageResource(stepIDs.get(i));
            newImage.setVisibility(View.VISIBLE);
        }
    }
}
