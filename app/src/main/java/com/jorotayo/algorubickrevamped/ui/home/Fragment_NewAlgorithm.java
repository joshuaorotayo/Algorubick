package com.jorotayo.algorubickrevamped.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.jorotayo.algorubickrevamped.KeyboardDialog;
import com.jorotayo.algorubickrevamped.ObjectBox;
import com.jorotayo.algorubickrevamped.OnBackPressed;
import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Algorithm;
import com.jorotayo.algorubickrevamped.data.Algorithm_;
import com.jorotayo.algorubickrevamped.data.Category;
import com.jorotayo.algorubickrevamped.utils.UtilMethods;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.objectbox.Box;
import io.objectbox.query.QueryBuilder;

public class Fragment_NewAlgorithm extends Fragment implements CategoryAdapter.OnCategoryListener, View.OnClickListener, AdapterView.OnItemSelectedListener, OnBackPressed {

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
    private ImageView new_add_new_alg_image, new_alg_image_preview;
    private Uri alg_Uri;
    Box<Category> categoryBox;
    ArrayList<Category> categories;
    AlertDialog categoryConfirmDeleteDialog;
    Category deleteCategory = new Category();
    CategoryAdapter categoryAdapter;
    private String selectedCategory = "Default";

    static Fragment_NewAlgorithm newInstance() {
        return new Fragment_NewAlgorithm();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        algorithmBox = ObjectBox.getBoxStore().boxFor(Algorithm.class);
        categoryBox = ObjectBox.getBoxStore().boxFor(Category.class);

        categories = (ArrayList<Category>) categoryBox.getAll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_algorithm_new, container, false);

        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Create New Algorithm");
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        categoryAdapter = new CategoryAdapter(requireContext(), R.id.category_spinner_label, categories, this);

        new_alg_name_edit = view.findViewById(R.id.new_alg_name_edit);
        new_alg_edit = view.findViewById(R.id.new_alg_edit);
        new_alg_description_edit = view.findViewById(R.id.new_alg_description_edit);
        new_add_new_alg_image = view.findViewById(R.id.new_add_new_alg_image);
        new_alg_image_preview = view.findViewById(R.id.new_alg_image_preview);
        til_alg_name = view.findViewById(R.id.til_alg_name);
        til_alg = view.findViewById(R.id.til_alg);
        til_alg_description = view.findViewById(R.id.til_alg_description);
        new_alg_favourite_switch = view.findViewById(R.id.new_alg_favourite_switch);
        new_alg_custom_switch = view.findViewById(R.id.new_alg_custom_switch);
        new_alg_save_btn = view.findViewById(R.id.new_alg_save_btn);
        new_alg_category_spinner = view.findViewById(R.id.new_alg_category_spinner);

        new_alg_edit.setOnClickListener(v -> openKeyboard());
        new_alg_save_btn.setOnClickListener(this);
        new_add_new_alg_image.setOnClickListener(v -> UtilMethods.ImageSelection(this));
        new_alg_category_spinner.setAdapter(categoryAdapter);

        buildCategoryDeleteDialog();
        createAlertDialog();
        checkEditAlgorithm();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            alg_Uri = data.getData();
            new_alg_image_preview.setImageURI(alg_Uri);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getActivity(), "No Image Selected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Image selection cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkEditAlgorithm() {
        intent = requireActivity().getIntent();
        if (intent.hasExtra("edit")) {
            editAlgorithm();
        }
    }

    public void editAlgorithm() {
        currentAlgorithm = algorithmBox.get(intent.getLongExtra("edit", 0));
        ((AppCompatActivity) requireActivity()).setTitle("Edit Algorithm");
        new_alg_name_edit.setText(currentAlgorithm.getAlg_name());
        new_alg_edit.setText(currentAlgorithm.getAlg());
        new_alg_description_edit.setText(currentAlgorithm.getAlg_description());
        new_alg_category_spinner.setSelection(findSelection(currentAlgorithm.getCategory()));
        new_alg_custom_switch.setChecked(currentAlgorithm.isCustom_alg());
        new_alg_favourite_switch.setChecked(currentAlgorithm.isFavourite_alg());
        new_alg_save_btn.setOnClickListener(v -> saveEditAlgorithm());
        UtilMethods.LoadAlgorithmIcon(getContext(), new_alg_image_preview, currentAlgorithm);
    }

    private int findSelection(String category) {
        for (int i = 0; i < categories.size(); i++) {
            if (Objects.equals(categories.get(i).category_name, category)) return i;
        }
        return 0;
    }

    private void createAlertDialog() {
        alertDialogBuilder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Close without saving?")
                .setMessage("If you carry on the current Algorithm will be closed without saving. Click ok if you are fine to do this.")
                .setPositiveButton("Close", (dialog, which) -> requireActivity().finish())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
    }

    private void openKeyboard() {
        new KeyboardDialog().newKeyboard(requireContext(), new_alg_edit);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.new_alg_save_btn) saveAlgorithm();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        new_alg_category_spinner.setSelection(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    private void saveEditAlgorithm() {
        clearErrors();
        if (validateErrors()) {
            currentAlgorithm.setAlg_name(new_alg_name_edit.getText().toString());
            currentAlgorithm.setAlg(new_alg_edit.getText().toString());
            currentAlgorithm.setAlg_description(new_alg_description_edit.getText().toString());
            currentAlgorithm.setCategory(selectedCategory);
            currentAlgorithm.custom_alg = new_alg_custom_switch.isChecked();
            currentAlgorithm.favourite_alg = new_alg_favourite_switch.isChecked();
            if (alg_Uri != null) currentAlgorithm.setAlgorithm_icon(alg_Uri.toString());
            currentAlgorithm.setCreatedTime();
            algorithmBox.put(currentAlgorithm);
            requireActivity().finish();
        }
    }

    private void saveAlgorithm() {
        clearErrors();
        if (validateErrors()) {
            Algorithm newAlg = new Algorithm();
            newAlg.setAlg_name(new_alg_name_edit.getText().toString());
            newAlg.setAlg(new_alg_edit.getText().toString());
            newAlg.setAlg_description(new_alg_description_edit.getText().toString());
            newAlg.setCategory(selectedCategory);
            newAlg.custom_alg = new_alg_custom_switch.isChecked();
            newAlg.favourite_alg = new_alg_favourite_switch.isChecked();
            if (alg_Uri != null) newAlg.setAlgorithm_icon(alg_Uri.toString());
            newAlg.setPracticed_correctly_int(0);
            newAlg.setPracticed_number_int(0);
            algorithmBox.put(newAlg);
            requireActivity().finish();
        }
    }

    private void clearErrors() {
        til_alg.setError(null);
        til_alg_name.setError(null);
        til_alg_description.setError(null);
    }

    private boolean validateErrors() {
        boolean valid = true;
        if (new_alg_name_edit.getText().toString().isEmpty()) {
            til_alg_name.setError("Algorithm Name cannot be blank");
            valid = false;
        }
        if (new_alg_edit.getText().toString().isEmpty()) {
            til_alg.setError("Algorithm cannot be blank");
            valid = false;
        }
        if (new_alg_description_edit.getText().toString().isEmpty()) {
            til_alg_description.setError("Algorithm Description cannot be blank");
            valid = false;
        }
        return valid;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            customBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void customBackPressed() {
        alertDialogBuilder.show();
    }

    public void categoryLabelClick(long position) {
        int selectedIndex = 0;
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).id == position) selectedIndex = i;
        }
        new_alg_category_spinner.setSelection(selectedIndex, true);
        selectedCategory = categories.get(selectedIndex).category_name;
        hideSpinnerDropDown(new_alg_category_spinner);
    }

    public static void hideSpinnerDropDown(Spinner spinner) {
        try {
            Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
            method.setAccessible(true);
            method.invoke(spinner);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void categoryDeleteClick(long position) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).id == position) deleteCategory = categories.get(i);
        }
        categoryConfirmDeleteDialog.show();
    }

    public void buildCategoryDeleteDialog() {
        categoryConfirmDeleteDialog = new MaterialAlertDialogBuilder(getContext())
                .setTitle(getString(R.string.spinner_category_delete_title))
                .setMessage(getString(R.string.spinner_delete_confirmation, categories.get(0).category_name))
                .setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    deleteCategory();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create();
    }

    public void deleteCategory() {
        if (categories.size() >= 2) {
            List<Algorithm> algorithmList = algorithmBox.query()
                    .equal(Algorithm_.category, deleteCategory.category_name, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                    .build().find();

            categoryBox.remove(deleteCategory);
            categories = (ArrayList<Category>) categoryBox.getAll();

            for (Algorithm algorithm : algorithmList) {
                algorithm.setCategory(categories.get(0).category_name);
            }

            algorithmBox.put(algorithmList);
            categoryAdapter = new CategoryAdapter(getContext(), R.id.category_spinner_label, categories, this);
            new_alg_category_spinner.setAdapter(categoryAdapter);
        } else {
            Toast.makeText(getContext(), "At least one category is needed", Toast.LENGTH_SHORT).show();
        }
        hideSpinnerDropDown(new_alg_category_spinner);
    }
}
