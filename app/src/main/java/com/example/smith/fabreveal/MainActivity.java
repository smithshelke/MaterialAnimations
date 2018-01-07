package com.example.smith.fabreveal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;

import static android.graphics.Color.TRANSPARENT;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    DisplayMetrics dp;
    View reveal, topPanel;
    boolean isCancel;
    float finalX, finalY, startX, startY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reveal = findViewById(R.id.reveal);
        topPanel = findViewById(R.id.topPanel);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        dp = getResources().getDisplayMetrics();
        finalX = dp.widthPixels / 2 - (dp.density * 56) / 2;
        finalY = dp.heightPixels - dp.density * 250 - (dp.density * 56) / 2;
    }

    public void animate(final View view) {
        if (!isCancel) {
            isCancel = true;
            startX = fab.getX();
            startY = fab.getY();
            Log.i("startY", String.valueOf(startY));
            ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float currentValue = (Float) animation.getAnimatedValue();
                    fab.setX(
                            startX - (startX - finalX) * currentValue
                    );
                    fab.setY(
                            startY - (startY - finalY) * currentValue * currentValue
                    );
                }
            });
            animator.setInterpolator(new AnticipateInterpolator(.8f));
            animator.addListener(new AnimatorListenerAdapter() {
                @Override


                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    reveal.setVisibility(View.VISIBLE);
                    reveal.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    Animator animator1 = ViewAnimationUtils.createCircularReveal(reveal, reveal.getWidth() / 2, (int) (finalY - reveal.getY() + dp.density * 56 / 2), dp.density * 56 / 2, reveal.getHeight() * .7f);
                    topPanel.setScaleY(0f);
                    topPanel.setVisibility(VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            topPanel.animate()
                                    .scaleY(1f)
                                    .setDuration(200)
                                    .start();
                        }
                    }, 200);
                    animator1.start();
                }
            });
            animator.start();
        } else {
            returnFabToInitialPosition(fab);
        }
    }

    public void returnFabToInitialPosition(View view) {
        isCancel = false;
        Animator reset = ViewAnimationUtils.createCircularReveal(reveal, (int) (finalX + dp.density * 56 / 2), (int) (finalY - reveal.getY() + dp.density * 56 / 2), reveal.getHeight() * .5f, dp.density * 56 / 2);
        reset.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                reveal.setVisibility(View.INVISIBLE);
                fab.animate().rotationBy(360).setDuration(1000).start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float currentValue = (Float) animation.getAnimatedValue();
                                fab.setX(
                                        finalX + (startX - finalX) * currentValue
                                );
                                fab.setY(
                                        finalY + (startY - finalY) * (float) Math.pow(currentValue, 0.5)
                                );
                            }
                        });
                        animator.setInterpolator(new AnticipateInterpolator(.8f));
                        animator.start();
                    }
                }, 1000);
            }
        });
        topPanel.setScaleY(1f);
        topPanel.setVisibility(View.INVISIBLE);

        reset.start();
    }
}
