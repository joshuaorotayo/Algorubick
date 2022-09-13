package com.jorotayo.algorubickrevamped.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.jorotayo.algorubickrevamped.KeyboardDialog;
import com.jorotayo.algorubickrevamped.ObjectBox;
import com.jorotayo.algorubickrevamped.OnBackPressed;
import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.objectbox.Box;

public class NewAlgorithmFragment extends Fragment implements OnClickListener, OnItemSelectedListener, OnBackPressed {
    MaterialAlertDialogBuilder alertDialogBuilder;
    Button new_alg_save_btn;
    View view;
    private Box<Algorithm> algorithmBox;
    private Algorithm currentAlgorithm;
    private Intent intent;
    private Spinner new_alg_category_spinner;
    private Switch new_alg_custom_switch;
    private EditText new_alg_description_edit;
    private EditText new_alg_edit;
    private Switch new_alg_favourite_switch;
    private EditText new_alg_name_edit;
    private TextInputLayout til_alg;
    private TextInputLayout til_alg_description;
    private TextInputLayout til_alg_name;

    static NewAlgorithmFragment newInstance() {
        return new NewAlgorithmFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.algorithmBox = ObjectBox.getBoxStore().boxFor(Algorithm.class);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        this.view = inflater.inflate(R.layout.fragment_algorithm_new, container, false);
        Objects.requireNonNull(Objects.requireNonNull((AlgorithmActivity) getActivity()).getSupportActionBar()).setTitle("Create New Algorithm");
        this.algorithmBox = ObjectBox.getBoxStore().boxFor(Algorithm.class);
        ArrayList<String> filter_category = new ArrayList();
        filter_category.add("Triggers");
        filter_category.add("OLL");
        filter_category.add("PLL");
        filter_category.add("F2L");
        filter_category.add("Cross");
        filter_category.add("EOLL");
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, filter_category);
        categoryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        this.new_alg_name_edit = this.view.findViewById(R.id.new_alg_name_edit);
        this.new_alg_edit = this.view.findViewById(R.id.new_alg_edit);
        this.new_alg_description_edit = this.view.findViewById(R.id.new_alg_description_edit);
        this.til_alg_name = this.view.findViewById(R.id.til_alg_name);
        this.til_alg = this.view.findViewById(R.id.til_alg);
        this.til_alg_description = this.view.findViewById(R.id.til_alg_description);
        this.new_alg_favourite_switch = this.view.findViewById(R.id.new_alg_favourite_switch);
        this.new_alg_custom_switch = this.view.findViewById(R.id.new_alg_custom_switch);
        this.new_alg_save_btn = this.view.findViewById(R.id.new_alg_save_btn);
        Spinner spinner = this.view.findViewById(R.id.new_alg_category_spinner);
        this.new_alg_category_spinner = spinner;
        spinner.setOnItemSelectedListener(this);
        this.new_alg_edit.setOnClickListener(v -> NewAlgorithmFragment.this.openKeyboard());
        this.new_alg_save_btn.setOnClickListener(this);
        this.new_alg_category_spinner.setAdapter(categoryAdapter);
        createAlertDialog();
        checkEditAlgorithm();
        return this.view;
    }

    private void checkEditAlgorithm() {
        Intent intent = requireActivity().getIntent();
        this.intent = intent;
        if (intent.hasExtra("edit")) {
            editAlgorithm();
        }
    }

    public void editAlgorithm() {
        this.currentAlgorithm = this.algorithmBox.get(this.intent.getLongExtra("edit", 0));
        Objects.requireNonNull(((AlgorithmActivity) requireActivity()).getSupportActionBar()).setTitle("Edit Algorithm");
        String stringBuilder = "" +
                this.currentAlgorithm.getAlg_name();
        ((AlgorithmActivity) requireActivity()).getActionBar().setSubtitle(stringBuilder);
        this.new_alg_name_edit.setText(this.currentAlgorithm.getAlg_name());
        this.new_alg_edit.setText(this.currentAlgorithm.getAlg());
        this.new_alg_description_edit.setText(this.currentAlgorithm.getAlg_description());
        this.new_alg_category_spinner.setSelected(this.currentAlgorithm.getSelected_alg());
        this.new_alg_custom_switch.setChecked(this.currentAlgorithm.isCustom_alg());
        this.new_alg_favourite_switch.setChecked(this.currentAlgorithm.isFavourite_alg());
        this.new_alg_save_btn.setOnClickListener(v -> NewAlgorithmFragment.this.saveEditAlgorithm());
    }

    private void createAlertDialog() {
        CharSequence charSequence = "Cancel";
        this.alertDialogBuilder = new MaterialAlertDialogBuilder(requireContext()).setTitle("Close without saving?").setMessage("If you carry on the current Algorithm will be closed without saving. Click ok if you are fine to do this.").setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                NewAlgorithmFragment.this.requireActivity().finish();
            }
        }).setNegativeButton(charSequence, (dialog, which) -> dialog.dismiss());
    }

    private void openKeyboard() {
        new KeyboardDialog().newKeyboard(requireContext(), this.new_alg_edit);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.new_alg_save_btn) {
            saveAlgorithm();
        }
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        this.new_alg_category_spinner.setSelection(position);
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void saveEditAlgorithm() {
        clearErrors();
        if (validateErrors()) {
            this.currentAlgorithm.alg_name = this.new_alg_name_edit.getText().toString();
            this.currentAlgorithm.alg = this.new_alg_edit.getText().toString();
            this.currentAlgorithm.alg_description = this.new_alg_description_edit.getText().toString();
            this.currentAlgorithm.category = this.new_alg_category_spinner.getSelectedItem().toString();
            this.currentAlgorithm.custom_alg = this.new_alg_custom_switch.isChecked();
            this.currentAlgorithm.favourite_alg = this.new_alg_favourite_switch.isChecked();
            this.algorithmBox.put(this.currentAlgorithm);
            List<Algorithm> algorithmList = this.algorithmBox.getAll();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SaveAlgorithm: ");
            stringBuilder.append(algorithmList.size());
            requireActivity().finish();
        }
    }

    private void saveAlgorithm() {
        clearErrors();
        if (validateErrors()) {
            Algorithm newAlg = new Algorithm();
            newAlg.alg_name = this.new_alg_name_edit.getText().toString();
            newAlg.alg = this.new_alg_edit.getText().toString();
            newAlg.alg_description = this.new_alg_description_edit.getText().toString();
            newAlg.category = this.new_alg_category_spinner.getSelectedItem().toString();
            newAlg.custom_alg = this.new_alg_custom_switch.isChecked();
            newAlg.favourite_alg = this.new_alg_favourite_switch.isChecked();
            newAlg.setPracticed_correctly_int(0);
            newAlg.setPracticed_number_int(0);
            this.algorithmBox.put(newAlg);
            List<Algorithm> algorithmList = this.algorithmBox.getAll();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SaveAlgorithm: ");
            stringBuilder.append(algorithmList.size());
            requireActivity().finish();
        }
    }

    private void clearErrors() {
        String str = "";
        this.til_alg.setError(str);
        this.til_alg_name.setError(str);
        this.til_alg_description.setError(str);
    }

    private boolean validateErrors() {
        boolean valid = true;
        String str = "";
        if (this.new_alg_name_edit.getText().toString().equals(str)) {
            this.til_alg_name.setError("Algorithm Name cannot be blank");
            valid = false;
        }
        if (this.new_alg_edit.getText().toString().equals(str)) {
            this.til_alg.setError("Algorithm cannot be blank");
            valid = false;
        }
        if (!this.new_alg_description_edit.getText().toString().equals(str)) {
            return valid;
        }
        this.til_alg_description.setError("Algorithm Description cannot be blank");
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return false;
        }
        customBackPressed();
        return true;
    }

    public void customBackPressed() {
        this.alertDialogBuilder.show();
    }

}
