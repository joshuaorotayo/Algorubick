package com.jorotayo.algorubickrevamped;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends Activity {
    private static final int SPLASH_SCREEN = 2500;
    ImageView image;
    TextView logo, slogan;
    Animation spinAnim, bottomAnim, topAnim, scaleInAnim;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ObjectBox.init(this);
        setContentView(R.layout.splash_screen);
        getWindow().setFlags(1024, 1024);
        this.topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        this.spinAnim = AnimationUtils.loadAnimation(this, R.anim.spin_animation);
        this.bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        this.scaleInAnim = AnimationUtils.loadAnimation(this, R.anim.scale_in_animation);
        this.image = findViewById(R.id.splash_image_view);
        this.logo = findViewById(R.id.splash_logo);
        this.slogan = findViewById(R.id.splash_slogan);
        this.logo.setAnimation(this.bottomAnim);
        this.image.setAnimation(this.scaleInAnim);
        this.slogan.setAnimation(this.bottomAnim);
        new Handler().postDelayed(() -> {
            SplashScreen.this.startActivity(new Intent(SplashScreen.this, MainActivity.class));
            SplashScreen.this.finish();
        }, SPLASH_SCREEN);
    }
}
