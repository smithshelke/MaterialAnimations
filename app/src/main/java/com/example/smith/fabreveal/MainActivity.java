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

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    DisplayMetrics dp;
    View reveal;
    boolean isCancel;
    float finalX, finalY, startX, startY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reveal = findViewById(R.id.reveal);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        dp = getResources().getDisplayMetrics();
        finalX = dp.widthPixels / 2 - (dp.density * 56) / 2;
        Log.i("finalX", String.valueOf(finalX));
        finalY = dp.heightPixels - dp.density * 250 - (dp.density * 56) / 2;
        Toast.makeText(this, "" + finalY, Toast.LENGTH_SHORT).show();
        if (reveal.getY() != dp.density * 300) {
            float diff = dp.heightPixels - dp.density * 300;
            Toast.makeText(this, "revealY: " + reveal.getY() + " || " + diff, Toast.LENGTH_SHORT).show();
        }
        Log.i("finalY", String.valueOf(finalY));
    }

    public void animate(final View view) {
        Resources resources = this.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        float nav_size = resources.getDimensionPixelSize(resourceId);
        float tot = nav_size+dp.density*(80);
        float diff = dp.heightPixels - (dp.density * 300)-nav_size;

        Toast.makeText(this, "revealY: " + reveal.getY() + " || " + diff, Toast.LENGTH_SHORT).show();

        if (!isCancel) {
            isCancel = true;
            startX = fab.getX();
            Log.i("startX", String.valueOf(startX));
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
                   /* fab.setBackgroundTintList(ColorStateList.valueOf(TRANSPARENT));
                    fab.setElevation(0f);
                    fab.setVisibility(View.INVISIBLE);*/
                    reveal.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    Animator animator1 = ViewAnimationUtils.createCircularReveal(reveal, reveal.getWidth() / 2, (int) (finalY - reveal.getY() + dp.density * 56 / 2), dp.density * 56 / 2, reveal.getWidth());
                    animator1.start();
                    //fab.setBackgroundTintList(ColorStateList.valueOf(TRANSPARENT));
                }
            });
            animator.start();
        } else {
            returnFabToInitialPosition(fab);
        }
    }

    public void returnFabToInitialPosition(View view) {
        isCancel = false;
        Animator reset = ViewAnimationUtils.createCircularReveal(reveal,(int)(finalX+dp.density*56/2),(int) (finalY-reveal.getY()+dp.density*56/2),reveal.getWidth()*.7f,dp.density*56/2);
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
                        //animator.setInterpolator(new AnticipateInterpolator(.8f));
                        animator.start();
                    }
                },1000);
            }
        });
        reset.start();
    }
}