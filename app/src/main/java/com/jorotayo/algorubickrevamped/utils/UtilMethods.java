package com.jorotayo.algorubickrevamped.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Algorithm;

public class UtilMethods {

    public static void LoadAlgorithmIcon(Context ctx, ImageView imageView, Algorithm algorithm) {
        if (ctx == null || imageView == null) return;

        String icon = algorithm.getAlgorithm_icon();

        if (icon == null || icon.isEmpty()) {
            Glide.with(ctx)
                    .load(R.drawable.cfop)
                    .into(imageView);
            return;
        }

        if (icon.startsWith("file:///")) {
            Glide.with(ctx)
                    .load(Uri.parse(icon))
                    .placeholder(R.drawable.cfop)
                    .error(R.drawable.cfop)
                    .into(imageView);
        } else {
            int resId = ctx.getResources().getIdentifier(
                    icon.replace("R.drawable.", ""),
                    "drawable",
                    ctx.getPackageName()
            );

            Glide.with(ctx)
                    .load(resId != 0 ? resId : R.drawable.cfop)
                    .into(imageView);
        }
    }


    public static void LoadStepIcon(Context ctx, ImageView imageView, String stepIcon) {
        if (ctx == null || imageView == null) return;

        if (stepIcon == null || stepIcon.isEmpty()) {
            Glide.with(ctx)
                    .load(R.drawable.cfop)
                    .into(imageView);
            return;
        }

        Glide.with(ctx)
                .load(stepIcon.startsWith("file:///") ? Uri.parse(stepIcon) : stepIcon)
                .placeholder(R.drawable.cfop)
                .error(R.drawable.cfop)
                .into(imageView);
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
