package com.example.smith.fabreveal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import static android.graphics.Color.TRANSPARENT;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    DisplayMetrics dp;
    View reveal, topPanel, animationLayout, topPanelContent, container;
    ImageView addButton, cancelButton;
    LinearLayout bottomSheet;
    boolean isCancel;
    float finalX, finalY, startX, startY;
    FragmentTransaction fragmentTransaction, fragmentTransaction1;
    FragmentManager fragmentManager;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        reveal = findViewById(R.id.reveal);
        container = findViewById(R.id.container);
        topPanel = findViewById(R.id.topPanel);
        topPanelContent = findViewById(R.id.topPanelContent);
        bottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        addButton = (ImageView) findViewById(R.id.addButton);
        cancelButton = (ImageView) findViewById(R.id.cancelButton);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        animationLayout = findViewById(R.id.animation_layout);
        dp = getResources().getDisplayMetrics();
        finalX = dp.widthPixels / 2 - (dp.density * 56) / 2;
        finalY = dp.heightPixels - dp.density * 200 - (dp.density * 56) / 2;
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, new FragmentDashboard());
        fragmentTransaction.commit();
    }

    public void onDisplayContentInBottomSheet() {
        fragment = new FragmentBarcode();
        fragmentTransaction1 = fragmentManager.beginTransaction();
        fragmentTransaction1.replace(R.id.container, new FragmentBarcode());
        fragmentTransaction1.commit();
    }

    //-----------fab and animations--------------
    public void animate(final View view) {
        container.setVisibility(View.VISIBLE);
        fab.animate().setInterpolator(null);
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
                            cancelButton.setX(-500);
                            // cancelButton.animate();
                            cancelButton.animate()
                                    .x(0)
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
                    animator1.start();
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

                    //-----------------All animations have been completed
                    //-----------------Display content--------------------
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            topPanelContent.setVisibility(View.VISIBLE);
                            onDisplayContentInBottomSheet();
                        }
                    }, 500);


                }
            });
            animator.start();
        }
    }

    public void hideViews(View view) {
        returnFabToInitialPosition(fab);
    }

    public void returnFabToInitialPosition(View view) {
        fragmentTransaction.remove(fragment);
        container.setVisibility(View.INVISIBLE);
        isCancel = false;
        hideBottomSheet();
        addButton.setImageResource(0);
        fab.setVisibility(VISIBLE);

        // reveal.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        fab.animate()
                .x(dp.widthPixels / 2 - dp.density * 56 / 2)
                .setDuration(100)
                .start();
        fab.animate()
                .y(finalY)
                .setDuration(200)
                .start();
        topPanel.animate()
                .scaleY(0f)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideTopPanel();
                Animator reset = ViewAnimationUtils.createCircularReveal(reveal, (int) (finalX + dp.density * 56 / 2), (int) (finalY - reveal.getY() + dp.density * 56 / 2), reveal.getHeight() * .7f, dp.density * 56 / 2);
                reset.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        hideRevealAndResetFAB();
                        fab.animate()
                                .rotationBy(450)
                                .setDuration(800)
                                .start();
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
                        }, 800);
                    }
                });
                reset.start();
            }
        }, 200);
    }

    public void cancelView(View view) {
        container.setVisibility(View.INVISIBLE);
        hideBottomSheet();
        Animator animatorCancel = ViewAnimationUtils.createCircularReveal(animationLayout, (int) (startX + dp.density * 28), (int) (startY + dp.density * 28), reveal.getWidth(), 0);
        animatorCancel.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorCancel.start();
        animatorCancel.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                hideRevealAndResetFAB();
                revealFAB();
                hideTopPanel();
            }
        });
    }

    private void hideTopPanel() {
        topPanel.setVisibility(View.INVISIBLE);
        topPanelContent.setVisibility(View.INVISIBLE);
    }

    private void hideBottomSheet() {
        bottomSheet.setVisibility(View.INVISIBLE);
        addButton.setImageResource(0);
    }

    private void hideRevealAndResetFAB() {
        reveal.setVisibility(View.INVISIBLE);
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary)));
        fab.setElevation(dp.density * 4);
    }

    private void revealFAB() {

        isCancel = false;
        fab.setX(startX);
        fab.setY(startY);
        fab.setVisibility(VISIBLE);
        fab.setScaleX(0);
        fab.setScaleY(0);
        fab.animate()
                .scaleXBy(1f)
                .setDuration(500)
                .setInterpolator(new BounceInterpolator());
        fab.animate()
                .scaleYBy(1f)
                .setDuration(500)
                .start();
    }
}
