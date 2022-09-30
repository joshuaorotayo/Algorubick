package com.jorotayo.algorubickrevamped.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Algorithm;

public class UtilMethods {

    public static void LoadAlgorithmIcon(Context ctx, ImageView imageView, Algorithm algorithmItem) {
        if (ctx != null) {
            if (algorithmItem.getAlgorithm_icon() != null) {
                String str = "";
                if (algorithmItem.getAlgorithm_icon().isEmpty()) {
                    imageView.setImageResource(R.drawable.cfop);
                } else if (algorithmItem.getAlgorithm_icon().contains("file:///")) {

                    imageView.setImageURI(Uri.parse(algorithmItem.getAlgorithm_icon().substring(6)));
                } else {
                    imageView.setImageResource(ctx.getResources().getIdentifier(algorithmItem.getAlgorithm_icon().replace("R.drawable.", str), "drawable", ctx.getPackageName()));
                }
            }
        }
    }

    public static void LoadStepIcon(Context ctx, ImageView imageView, String stepIcon) {
        if (ctx != null) {
            if (stepIcon != null) {
                String str = "";
                if (stepIcon.isEmpty()) {
                    imageView.setImageResource(R.drawable.cfop);
                } else if (stepIcon.contains("file:///")) {
                    imageView.setImageURI(Uri.parse(stepIcon.substring(6)));
                } else {
                    imageView.setImageResource(ctx.getResources().getIdentifier(stepIcon.replace("R.drawable.", str), "drawable", ctx.getPackageName()));
                }
            }
        }
    }

    public static void ImageSelection(Fragment fragment) {
        String[] mimeTypes = {"image/png", "image/jpg", "image/jpeg"};
        ImagePicker.Companion.with(fragment)
                .crop()
                .compress(1024)
                .galleryMimeTypes(  //Exclude gif images
                        mimeTypes
                )
                .start();
    }
}
