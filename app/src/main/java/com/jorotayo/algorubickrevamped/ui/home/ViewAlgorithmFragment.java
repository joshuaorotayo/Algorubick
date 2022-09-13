package com.jorotayo.algorubickrevamped.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.jorotayo.algorubickrevamped.ObjectBox;
import com.jorotayo.algorubickrevamped.OnBackPressed;
import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Algorithm;
import java.util.ArrayList;
import java.util.Objects;
import io.objectbox.Box;

public class ViewAlgorithmFragment extends Fragment implements OnClickListener, OnBackPressed {
    private static final String ALG_ID = "param1";
    private final ArrayList selectedList = new ArrayList();
    private Box algorithmBox;
    private Algorithm currentAlg = new Algorithm();
    private Drawable customDrawableImage;
    private Builder deleteDialog;
    private Drawable favDrawableImage;

    public static ViewAlgorithmFragment newInstance(long param1) {
        ViewAlgorithmFragment fragment = new ViewAlgorithmFragment();
        Bundle args = new Bundle();
        args.putLong(ALG_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Box algorithmBox = ObjectBox.getBoxStore().boxFor(Algorithm.class);
        this.algorithmBox = algorithmBox;
        this.currentAlg = (Algorithm) algorithmBox.get(getArguments().getLong(ALG_ID));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_algorithm_view, container, false);
        long id = Objects.requireNonNull(Objects.requireNonNull((AlgorithmActivity) getActivity()).getIntent().getExtras()).getLong("algorithm_id");
        this.currentAlg = (Algorithm) this.algorithmBox.get(id);
        setHasOptionsMenu(true);
        TextView view_alg_name = view.findViewById(R.id.view_alg_name);
        TextView view_alg_text = view.findViewById(R.id.view_alg_text);
        TextView view_alg_category = view.findViewById(R.id.view_alg_category);
        TextView view_alg_correct_amount = view.findViewById(R.id.view_alg_correct_amount);
        TextView view_alg_practiced_amount = view.findViewById(R.id.view_alg_practiced_amount);
        TextView view_alg_description = view.findViewById(R.id.view_alg_description);
        TextView view_alg_favourite_textview = view.findViewById(R.id.view_alg_favourite_textview);
        TextView view_alg_custom_textview = view.findViewById(R.id.view_alg_custom_textview);
        Button learn_alg_btn = view.findViewById(R.id.view_alg_learn_algorithm_btn);
        Button practice_alg_btn = view.findViewById(R.id.view_alg_practice_algorithm_btn);
        LinearLayout view_alg_image = view.findViewById(R.id.view_alg_image);
        view_alg_image.setBackgroundResource(R.drawable.cfop);
        view_alg_name.setText(this.currentAlg.alg_name);
        Objects.requireNonNull(Objects.requireNonNull((AlgorithmActivity) getActivity()).getActionBar()).setTitle("View Algorithm");
        String str = "";
        String stringBuilder = str +
                this.currentAlg.alg_name;
        Objects.requireNonNull(Objects.requireNonNull((AlgorithmActivity) getActivity()).getActionBar()).setSubtitle(stringBuilder);
        view_alg_text.setText(this.currentAlg.alg);
        view_alg_category.setText(this.currentAlg.category);
        view_alg_description.setText(this.currentAlg.alg_description);
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(str);
        stringBuilder2.append(this.currentAlg.practiced_correctly_int);
        view_alg_correct_amount.setText(stringBuilder2.toString());
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append("/ ");
        stringBuilder2.append(this.currentAlg.practiced_number_int);
        view_alg_practiced_amount.setText(stringBuilder2.toString());
        view_alg_image.setBackground(getIconDrawable(this.currentAlg));
        Drawable favouriteDrawable = getFavouriteDrawable(this.currentAlg);
        this.favDrawableImage = favouriteDrawable;
        view_alg_favourite_textview.setCompoundDrawablesWithIntrinsicBounds(null, null, favouriteDrawable, null);
        favouriteDrawable = getCustomDrawable(this.currentAlg);
        this.customDrawableImage = favouriteDrawable;
        view_alg_custom_textview.setCompoundDrawablesWithIntrinsicBounds(null, null, favouriteDrawable, null);
        learn_alg_btn.setOnClickListener(this);
        practice_alg_btn.setOnClickListener(this);
        setupDeleteDialog();
        return view;
    }

    public void onClick(View v) {
        Intent intent = new Intent(getContext(), StudyAlgorithmActivity.class);
        this.selectedList.add((int) this.currentAlg.id - 1);
        int id = v.getId();
        if (id == R.id.view_alg_learn_algorithm_btn) {
            intent.putExtra("learn", this.selectedList);
            startActivity(intent);
        } else if (id == R.id.view_alg_practice_algorithm_btn) {
            intent.putExtra("practice", this.selectedList);
            startActivity(intent);
        }
    }

    private void setupDeleteDialog() {
        Builder title = new Builder(requireContext()).setTitle("Delete Algorithm");
        String stringBuilder = "Are you sure you want to delete the current Algorithm: " +
                this.currentAlg.getAlg_name();
        String str = "No";
        this.deleteDialog = title.setMessage(stringBuilder).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ViewAlgorithmFragment.this.deleteAlgorithm();
            }
        }).setNegativeButton(str, (dialog, which) -> dialog.dismiss()).setIcon(R.drawable.incorrect_48_r).setCancelable(true);
    }

    public Drawable getIconDrawable(Algorithm alg) {
        int iconDrawable;
        Drawable drawable;
        Drawable iconDrawableImage;
        if (alg.getAlgorithm_icon() != null) {
            String str = "";
            if (!alg.getAlgorithm_icon().equals(str)) {
                iconDrawable = getResources().getIdentifier(alg.getAlgorithm_icon().replace("R.drawable.", str), "drawable", requireActivity().getPackageName());
                drawable = requireActivity().getDrawable(iconDrawable);
                iconDrawableImage = drawable;
                return drawable;
            }
        }
        iconDrawable = R.drawable.cfop;
        drawable = requireActivity().getDrawable(iconDrawable);
        iconDrawableImage = drawable;
        return drawable;
    }

    public Drawable getFavouriteDrawable(Algorithm alg) {
        int favDrawable;
        if (alg.isFavourite_alg()) {
            favDrawable = R.drawable.heart_filled_24;
        } else {
            favDrawable = R.drawable.heart_outlined_24;
        }
        Drawable drawable = requireActivity().getDrawable(favDrawable);
        this.favDrawableImage = drawable;
        return drawable;
    }

    public Drawable getCustomDrawable(Algorithm alg) {
        int customDrawable;
        if (alg.isCustom_alg()) {
            customDrawable = R.drawable.thumb_up_filled_24;
        } else {
            customDrawable = R.drawable.thumb_up_outlined_24;
        }
        Drawable drawable = requireActivity().getDrawable(customDrawable);
        this.customDrawableImage = drawable;
        return drawable;
    }

    private void deleteAlgorithm() {
        this.algorithmBox.remove(this.currentAlg);
        requireActivity().onBackPressed();
    }

    public void customBackPressed() {
        requireActivity().finish();
    }

    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_algorithm_menu, menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.view_algorithm_menu_delete /*2131362447*/:
                this.deleteDialog.show();
                break;
            case R.id.view_algorithm_menu_edit /*2131362448*/:
                editAlgorithm();
                break;
            default:
                requireActivity().onBackPressed();
                break;
        }
        return true;
    }

    private void editAlgorithm() {
        NewAlgorithmFragment newFragment = NewAlgorithmFragment.newInstance();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.algorithm_activity_container, newFragment);
        transaction.addToBackStack("editSolution");
        requireActivity().getIntent().putExtra("edit", this.currentAlg.id);
        transaction.commit();
    }
}
