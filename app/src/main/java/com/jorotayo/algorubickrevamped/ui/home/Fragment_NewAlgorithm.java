package com.jorotayo.algorubickrevamped.ui.home;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

public class Fragment_NewAlgorithm extends Fragment implements OnClickListener, OnItemSelectedListener, OnBackPressed {
    MaterialAlertDialogBuilder alertDialogBuilder;
    Button new_alg_save_btn;
    View view;
    private Box<Algorithm> algorithmBox;
    private Algorithm currentAlgorithm;
    private Intent intent;
    private Spinner new_alg_category_spinner;
    private Switch new_alg_custom_switch;
    private EditText new_alg_description_edit, new_alg_edit, new_alg_name_edit;
    private Switch new_alg_favourite_switch;
    private TextInputLayout til_alg, til_alg_description, til_alg_name;
    private ImageView add_new_alg_image, new_alg_image_preview;
    private Uri alg_Uri;

    static Fragment_NewAlgorithm newInstance() {
        return new Fragment_NewAlgorithm();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.algorithmBox = ObjectBox.getBoxStore().boxFor(Algorithm.class);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        this.view = inflater.inflate(R.layout.fragment_algorithm_new, container, false);
        Objects.requireNonNull(Objects.requireNonNull((Activity_Algorithm) getActivity()).getSupportActionBar()).setTitle("Create New Algorithm");
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
        this.add_new_alg_image = this.view.findViewById(R.id.add_new_alg_image);
        this.new_alg_image_preview = this.view.findViewById(R.id.new_alg_image_preview);
        this.til_alg_name = this.view.findViewById(R.id.til_alg_name);
        this.til_alg = this.view.findViewById(R.id.til_alg);
        this.til_alg_description = this.view.findViewById(R.id.til_alg_description);
        this.new_alg_favourite_switch = this.view.findViewById(R.id.new_alg_favourite_switch);
        this.new_alg_custom_switch = this.view.findViewById(R.id.new_alg_custom_switch);
        this.new_alg_save_btn = this.view.findViewById(R.id.new_alg_save_btn);
        Spinner spinner = this.view.findViewById(R.id.new_alg_category_spinner);
        this.new_alg_category_spinner = spinner;
        spinner.setOnItemSelectedListener(this);
        this.new_alg_edit.setOnClickListener(v -> Fragment_NewAlgorithm.this.openKeyboard());
        this.new_alg_save_btn.setOnClickListener(this);
        this.add_new_alg_image.setOnClickListener(click -> Fragment_NewAlgorithm.this.addNewImage());
        this.new_alg_category_spinner.setAdapter(categoryAdapter);
        createAlertDialog();
        checkEditAlgorithm();
        return this.view;
    }


    private void addNewImage() {
        launchPickerSingleMode();
    }

    private final ActivityResultLauncher<Intent> startForSingleModeResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                final Intent data = result.getData();
                //Log.d(TAG, " resultCode: " + resultCode + " data: " + data);
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (data == null) return;
                    // Get photo picker response for single select.
                    // Do stuff with the photo/video URI.
                    alg_Uri = data.getData();
                    this.add_new_alg_image.setVisibility(View.GONE);
                    this.new_alg_image_preview.setImageURI(alg_Uri);
                    Log.d(TAG, "" + alg_Uri );
                    this.new_alg_image_preview.setVisibility(View.VISIBLE);

                }
            });

    private void launchPickerSingleMode() {
        // Launches photo picker in single-select mode.
        // This means that the user can select one photo or video.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (!"all".equals("image/*")) intent.setType("image/*");
        try {
            startForSingleModeResult.launch(intent);
        } catch (ActivityNotFoundException ignored) {

        }
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
        Objects.requireNonNull(((Activity_Algorithm) requireActivity()).getSupportActionBar()).setTitle("Edit Algorithm");
        ActionBar actionBar = Objects.requireNonNull(((Activity_Algorithm) requireActivity()).getSupportActionBar());
        actionBar.setSubtitle("" + this.currentAlgorithm.getAlg_name());
        this.new_alg_name_edit.setText(this.currentAlgorithm.getAlg_name());
        this.new_alg_edit.setText(this.currentAlgorithm.getAlg());
        this.new_alg_description_edit.setText(this.currentAlgorithm.getAlg_description());
        this.new_alg_category_spinner.setSelected(this.currentAlgorithm.getSelected_alg());
        this.new_alg_custom_switch.setChecked(this.currentAlgorithm.isCustom_alg());
        this.new_alg_favourite_switch.setChecked(this.currentAlgorithm.isFavourite_alg());
        this.new_alg_save_btn.setOnClickListener(v -> Fragment_NewAlgorithm.this.saveEditAlgorithm());
    }

    private void createAlertDialog() {
        CharSequence charSequence = "Cancel";
        this.alertDialogBuilder = new MaterialAlertDialogBuilder(requireContext()).setTitle("Close without saving?").setMessage("If you carry on the current Algorithm will be closed without saving. Click ok if you are fine to do this.").setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Fragment_NewAlgorithm.this.requireActivity().finish();
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
            this.currentAlgorithm.setAlg_name(this.new_alg_name_edit.getText().toString());
            this.currentAlgorithm.setAlg(this.new_alg_edit.getText().toString());
            this.currentAlgorithm.setAlg_description(this.new_alg_description_edit.getText().toString());
            this.currentAlgorithm.setCategory(this.new_alg_category_spinner.getSelectedItem().toString());
            this.currentAlgorithm.custom_alg = this.new_alg_custom_switch.isChecked();
            this.currentAlgorithm.favourite_alg = this.new_alg_favourite_switch.isChecked();
           // this.currentAlgorithm.algorithm_icon = alg_Uri.toString();
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
            newAlg.setAlg_name(this.new_alg_name_edit.getText().toString());
            newAlg.setAlg(this.new_alg_edit.getText().toString());
            newAlg.setAlg_description(this.new_alg_description_edit.getText().toString());
            newAlg.setCategory(this.new_alg_category_spinner.getSelectedItem().toString());
            newAlg.custom_alg = this.new_alg_custom_switch.isChecked();
            newAlg.favourite_alg = this.new_alg_favourite_switch.isChecked();
            newAlg.setAlgorithm_icon(this.alg_Uri.toString());
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
