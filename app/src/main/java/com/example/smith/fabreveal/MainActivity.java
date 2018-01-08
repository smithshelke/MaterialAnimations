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
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import static android.graphics.Color.TRANSPARENT;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    DisplayMetrics dp;
    View reveal, topPanel;
    ImageView addButton;
    LinearLayout bottomSheet;
    boolean isCancel;
    float finalX, finalY, startX, startY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reveal = findViewById(R.id.reveal);
        topPanel = findViewById(R.id.topPanel);
        bottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        addButton = (ImageView) findViewById(R.id.addButton);
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
                    // fab.setVisibility(View.INVISIBLE);
                    fab.setBackgroundTintList(ColorStateList.valueOf(TRANSPARENT));
                    fab.setElevation(0);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fab.animate()
                                    .y(bottomSheet.getY() + dp.density * 4)
                                    .setDuration(200)
                                    .start();
                        }
                    }, 50);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fab.animate()
                                    .x(dp.widthPixels / 2 + dp.widthPixels / 4 - dp.density * 56 / 2)
                                    .setDuration(300)
                                    .start();

                        }
                    }, 200);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fab.setVisibility(View.INVISIBLE);
                            addButton.setImageResource(R.drawable.ic_add_black_24dp);
                        }
                    }, 500);
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
                    }, 250);
                    animator1.start();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bottomSheet.setVisibility(VISIBLE);
                            bottomSheet.animate()
                                    .alpha(1f)
                                    .setDuration(200)
                                    .start();
                        }
                    }, 200);
                }
            });
            animator.start();
        }
    }

    public void hideViews(View view) {
        returnFabToInitialPosition(fab);
    }

    public void returnFabToInitialPosition(View view) {
        isCancel = false;
        bottomSheet.setVisibility(View.INVISIBLE);
        addButton.setImageResource(0);
        // reveal.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        fab.animate()
                .x(dp.widthPixels / 2 - dp.density * 56 / 2)
                .setDuration(100)
                .start();
        fab.animate()
                .y(finalY)
                .setDuration(200)
                .start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animator reset = ViewAnimationUtils.createCircularReveal(reveal, (int) (finalX + dp.density * 56 / 2), (int) (finalY - reveal.getY() + dp.density * 56 / 2), reveal.getHeight() * .5f, dp.density * 56 / 2);
                reset.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        reveal.setVisibility(View.INVISIBLE);
                        //  fab.setX(finalX);
                        // fab.setY(finalY);
                        fab.setVisibility(VISIBLE);
                        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary)));
                        fab.setElevation(dp.density * 4);
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
        }, 200);
    }
}
