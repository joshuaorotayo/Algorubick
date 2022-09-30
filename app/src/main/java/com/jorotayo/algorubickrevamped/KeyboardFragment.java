package com.jorotayo.algorubickrevamped;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.fragment.app.Fragment;

public class KeyboardFragment extends Fragment implements OnClickListener, View.OnLongClickListener {
    private final EditText inputSpace;
    private String backSpaceAlg = "";
    private TextView btn_2;
    private TextView btn_prime;
    private TextView current_alg_text;
    private TableLayout double_face_moves;
    private ViewSwitcher keyboardSwitcher;
    private Boolean modified = Boolean.valueOf(false);
    private Dialog rootView;
    private TableLayout single_face_moves;
    private String wholeAlg;

    public KeyboardFragment(EditText inputSpace) {
        this.inputSpace = inputSpace;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.keyboard, container, false);
        TextView btn_r = rootView.findViewById(R.id.btn_r);
        TextView btn_l = rootView.findViewById(R.id.btn_l);
        TextView btn_f = rootView.findViewById(R.id.btn_f);
        TextView btn_b = rootView.findViewById(R.id.btn_b);
        TextView btn_u = rootView.findViewById(R.id.btn_u);
        TextView btn_d = rootView.findViewById(R.id.btn_d);
        TextView btn_s_r = rootView.findViewById(R.id.btn_s_r);
        TextView btn_s_l = rootView.findViewById(R.id.btn_s_l);
        TextView btn_s_f = rootView.findViewById(R.id.btn_s_f);
        TextView btn_s_b = rootView.findViewById(R.id.btn_s_b);
        TextView btn_s_u = rootView.findViewById(R.id.btn_s_u);
        TextView btn_s_d = rootView.findViewById(R.id.btn_s_d);
        TextView btn_x = rootView.findViewById(R.id.btn_x);
        TextView btn_y = rootView.findViewById(R.id.btn_y);
        TextView btn_z = rootView.findViewById(R.id.btn_z);
        TextView btn_e = rootView.findViewById(R.id.btn_e);
        TextView btn_s = rootView.findViewById(R.id.btn_s);
        TextView btn_m = rootView.findViewById(R.id.btn_m);
        this.btn_2 = rootView.findViewById(R.id.btn_2);
        TextView btn_backspace = rootView.findViewById(R.id.btn_backspace);
        this.btn_prime = rootView.findViewById(R.id.btn_prime);
        TextView keyboard_close_btn = rootView.findViewById(R.id.keyboard_close_btn);
        //TextView btn_shift = btn_x;
        keyboard_close_btn.setVisibility(View.GONE);
        TextView btn_shift = rootView.findViewById(R.id.shift_btn);
        this.keyboardSwitcher = rootView.findViewById(R.id.keyboard_switcher);
        this.single_face_moves = rootView.findViewById(R.id.single_face_moves);
        this.double_face_moves = rootView.findViewById(R.id.double_face_moves);
        this.current_alg_text = rootView.findViewById(R.id.current_alg_text);
        btn_r.setOnClickListener(this);
        btn_l.setOnClickListener(this);
        btn_f.setOnClickListener(this);
        btn_b.setOnClickListener(this);
        btn_u.setOnClickListener(this);
        btn_d.setOnClickListener(this);
        btn_s_r.setOnClickListener(this);
        btn_s_l.setOnClickListener(this);
        btn_s_f.setOnClickListener(this);
        btn_s_b.setOnClickListener(this);
        btn_s_u.setOnClickListener(this);
        btn_s_d.setOnClickListener(this);
        btn_x.setOnClickListener(this);
        btn_y.setOnClickListener(this);
        btn_z.setOnClickListener(this);
        btn_e.setOnClickListener(this);
        btn_s.setOnClickListener(this);
        btn_m.setOnClickListener(this);
        keyboard_close_btn.setOnClickListener(this);
        this.btn_2.setOnClickListener(this);
        this.btn_prime.setOnClickListener(this);
        btn_backspace.setOnClickListener(this);
        btn_backspace.setOnLongClickListener(this);
        this.btn_prime.setClickable(false);
        this.btn_2.setClickable(false);
        this.btn_2.setTextColor(getContext().getResources().getColor(R.color.colorPrimary, null));
        this.btn_prime.setTextColor(getContext().getResources().getColor(R.color.colorPrimary, null));
        btn_shift.setOnClickListener(v -> {
            if (KeyboardFragment.this.single_face_moves.getVisibility() == View.VISIBLE) {
                KeyboardFragment.this.single_face_moves.setVisibility(View.GONE);
                KeyboardFragment.this.double_face_moves.setVisibility(View.VISIBLE);
                btn_shift.setTextColor(KeyboardFragment.this.getContext().getResources().getColor(R.color.colorRed, null));
                return;
            }
            KeyboardFragment.this.single_face_moves.setVisibility(View.VISIBLE);
            KeyboardFragment.this.double_face_moves.setVisibility(View.GONE);
            btn_shift.setTextColor(KeyboardFragment.this.getContext().getResources().getColor(R.color.colorAccent, null));
        });
        return rootView;
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.keyboard_close_btn) {
            this.rootView.dismiss();
        } else if (id != R.id.shift_btn) {
            switch (id) {
                case R.id.btn_2 /*2131361951*/:
                    this.modified = Boolean.valueOf(true);
                    enter("2");
                    return;
                case R.id.btn_b /*2131361952*/:
                    this.modified = Boolean.valueOf(false);
                    enter("B");
                    return;
                case R.id.btn_backspace /*2131361953*/:
                    this.modified = Boolean.valueOf(true);
                    backspace();
                    return;
                case R.id.btn_d /*2131361954*/:
                    this.modified = Boolean.valueOf(false);
                    enter("D");
                    return;
                case R.id.btn_e /*2131361955*/:
                    this.modified = Boolean.valueOf(false);
                    enter("E");
                    return;
                case R.id.btn_f /*2131361956*/:
                    this.modified = Boolean.valueOf(false);
                    enter("F");
                    return;
                case R.id.btn_l /*2131361957*/:
                    this.modified = Boolean.valueOf(false);
                    enter("L");
                    return;
                case R.id.btn_m /*2131361958*/:
                    this.modified = Boolean.valueOf(false);
                    enter("M");
                    return;
                case R.id.btn_prime /*2131361959*/:
                    this.modified = Boolean.valueOf(true);
                    enter("'");
                    return;
                case R.id.btn_r /*2131361960*/:
                    this.modified = Boolean.valueOf(false);
                    enter("R");
                    return;
                case R.id.btn_s /*2131361961*/:
                    this.modified = Boolean.valueOf(false);
                    enter("S");
                    return;
                case R.id.btn_s_b /*2131361962*/:
                    this.modified = Boolean.valueOf(false);
                    enter("b");
                    return;
                case R.id.btn_s_d /*2131361963*/:
                    this.modified = Boolean.valueOf(false);
                    enter("d");
                    return;
                case R.id.btn_s_f /*2131361964*/:
                    this.modified = Boolean.valueOf(false);
                    enter("f");
                    return;
                case R.id.btn_s_l /*2131361965*/:
                    this.modified = Boolean.valueOf(false);
                    enter("l");
                    return;
                case R.id.btn_s_r /*2131361966*/:
                    this.modified = Boolean.valueOf(false);
                    enter("r");
                    return;
                case R.id.btn_s_u /*2131361967*/:
                    this.modified = Boolean.valueOf(false);
                    enter("u");
                    return;
                case R.id.btn_u /*2131361968*/:
                    this.modified = Boolean.valueOf(false);
                    enter("U");
                    return;
                case R.id.btn_x /*2131361969*/:
                    this.modified = Boolean.valueOf(false);
                    enter("X");
                    return;
                case R.id.btn_y /*2131361970*/:
                    this.modified = Boolean.valueOf(false);
                    enter("Y");
                    return;
                case R.id.btn_z /*2131361971*/:
                    this.modified = Boolean.valueOf(false);
                    enter("Z");
                    return;
                default:
            }
        } else {
            enter("^");
        }
    }

    private void enter(String keypressed) {
        int i = 0;
        if (this.modified) {
            this.btn_prime.setClickable(false);
            this.btn_2.setClickable(false);
            this.btn_2.setTextColor(getContext().getResources().getColor(R.color.colorPrimary, null));
            this.btn_prime.setTextColor(getContext().getResources().getColor(R.color.colorPrimary, null));
        } else {
            this.btn_prime.setClickable(true);
            this.btn_2.setClickable(true);
            this.btn_2.setTextColor(getContext().getResources().getColor(R.color.white, null));
            this.btn_prime.setTextColor(getContext().getResources().getColor(R.color.white, null));
        }
        if (this.inputSpace.getText().toString().isEmpty()) {
            this.inputSpace.append(keypressed);
            return;
        }
        String inputSpaceText = this.inputSpace.getText().toString();
        String wholeAlgorithm = "";
        String str = ",";
        String str2 = "";
//        wholeAlgorithm = wholeAlgorithm + inputSpaceText.replace(str, str2);
//        wholeAlgorithm = wholeAlgorithm + keypressed;

        wholeAlgorithm += inputSpaceText.replace(str, str2);
        wholeAlgorithm += keypressed;
        StringBuilder wholeFormattedAlg = new StringBuilder();
        String[] alg = wholeAlgorithm.split("(?=([A-z][2]?[{'}]?)|([A-z][2]?)|([A-z][{'}]?)|([A-z]))");
        int length = alg.length;
        while (i < length) {
            String letter = alg[i];
            wholeFormattedAlg.append(str).append(letter);
            i++;
        }
        String wholeFormattedAlg2 = wholeFormattedAlg.toString().replace(",,", str2);
        wholeFormattedAlg2 = wholeFormattedAlg2.startsWith(str) ? wholeFormattedAlg2.substring(1) : wholeFormattedAlg2;
        this.inputSpace.setText(wholeFormattedAlg2);
        this.current_alg_text.setText(wholeFormattedAlg2);
    }

    private void backspace() {
        String obj = this.inputSpace.getText().toString();
        this.backSpaceAlg = obj;
        String str = "";
        if (obj.isEmpty()) {
            this.backSpaceAlg = str;
        } else {
            String str2 = ",";
            if (this.backSpaceAlg.contains(str2)) {
                obj = this.backSpaceAlg;
                this.backSpaceAlg = obj.substring(0, obj.lastIndexOf(str2));
            } else {
                this.backSpaceAlg = str;
            }
        }
        this.inputSpace.setText(this.backSpaceAlg);
        obj = this.backSpaceAlg;
        this.wholeAlg = obj;
        this.current_alg_text.setText(obj);
    }

    @Override
    public boolean onLongClick(View v) {
        this.inputSpace.setText("");
        return true;
    }
}
