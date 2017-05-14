package com.bahaso.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Hendry on 11/17/2015.
 */

public class AnimationHelper {
    public static final int SHORT_DURATION = 150;
    public static final int MEDIUM_DURATION = 300;
    public static final int LONG_DURATION = 500;

    private static TimeInterpolator mInterpolator;
//    static Rect startBounds = new Rect();    // start view left, top
//    static Rect endBounds = new Rect();      // end view start, top
//    static Rect finalBounds = new Rect();    // container start top
//    static Point globalOffset = new Point(); // to store global offset of container

    public static TimeInterpolator getDecelerateInterpolator() {
        if (null == mInterpolator){
            mInterpolator = new DecelerateInterpolator();
        }
        return mInterpolator;
    }

    /**
     * Animate the source view from source container to target container.
     *
     * @param rootContainer container that contain the source and the target object, must be object of DragContainer
     * @param animatedView View that want to be animated, must be direct child of root container
     * @param sourceView View that what to be moved, will be hidden then show after animation
     * @param sourceContainer direct parent of sourceview
     * @param targetContainer container for target object
     * @param indexToAdd indextoAdd for target container for moved object, -1 to add to the last
     * @param layoutParams layout params for target container & moved view
     */
    @TargetApi(14)
    public static void move (final ViewGroup rootContainer, final View animatedView, final View sourceView,
                             final ViewGroup sourceContainer, final ViewGroup targetContainer,
                             int indexToAdd, final ViewGroup.LayoutParams layoutParams){
        final LayoutTransition layoutTransition; // to store layout transition while remove/add

        final Rect startBounds = new Rect();    // start view left, top
        // final Rect endBounds = new Rect();      // end view start, top
        final Rect finalBounds = new Rect();    // container start top
        final Point globalOffset = new Point(); // to store global offset of container

        sourceView.setVisibility(View.GONE);
        sourceView.getGlobalVisibleRect(startBounds);
        layoutTransition = sourceContainer.getLayoutTransition();
        sourceContainer.setLayoutTransition(null);
        sourceContainer.removeView(sourceView);

        final LayoutTransition targetLayoutTransition = targetContainer.getLayoutTransition();
        targetContainer.setLayoutTransition(null);
        if (null == layoutParams) {
            if (indexToAdd == -1) {
                targetContainer.addView(sourceView);
            }else{
                targetContainer.addView(sourceView, indexToAdd);
            }
        }else{
            if (indexToAdd == -1) {
                targetContainer.addView(sourceView, layoutParams);
            }else{
                targetContainer.addView(sourceView, indexToAdd, layoutParams);
            }
        }

        // below is needed to recalculate global Rectangle
        sourceView.setVisibility(View.INVISIBLE);

        // after the target has been added on targetcontainer, animate it
        targetContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                // this logic is to cater the limitation because
                // getglobalVisibleRect only correct when the view is visible
                // hit bound to get the local rect, target will calculate global parent
                // sum the left and right to get the correct global rect
                Rect hitBound = new Rect();
                Rect targetContainerBound = new Rect();
                sourceView.getHitRect(hitBound);
                targetContainer.getGlobalVisibleRect(targetContainerBound);
                // right and bottom no matter
                Point endBounds = new Point(hitBound.left+targetContainerBound.left,
                        targetContainerBound.top + hitBound.top);

                rootContainer.getGlobalVisibleRect(finalBounds, globalOffset);
                startBounds.offset(-globalOffset.x, -globalOffset.y);
                endBounds.offset(-globalOffset.x, -globalOffset.y);

//                sourceView.getGlobalVisibleRect(endBounds);
//                rootContainer.getGlobalVisibleRect(finalBounds, globalOffset);
//                startBounds.offset(-globalOffset.x, -globalOffset.y);
//                endBounds.offset(-globalOffset.x, -globalOffset.y);

                // if out of bound, no need animation
//                if (endBounds.left > rootContainer.getRight() ||
//                        endBounds.right < rootContainer.getLeft() ||
//                        endBounds.top > rootContainer.getBottom() ||
//                        endBounds.bottom < rootContainer.getTop()) {
//                    targetContainer.setLayoutTransition(targetLayoutTransition);
//                    sourceContainer.setLayoutTransition(layoutTransition);
//                    sourceView.setVisibility(View.VISIBLE);
//                    return;
//                }

                animatedView.setVisibility(View.VISIBLE);
                // ANIMATE
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator.ofFloat(animatedView, View.X,
                        startBounds.left, endBounds.x))
                        .with(ObjectAnimator.ofFloat(animatedView, View.Y,
                                startBounds.top, endBounds.y));
                set.setDuration(SHORT_DURATION);
                set.setInterpolator(getDecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        animatedView.setVisibility(View.GONE);
                        sourceView.setVisibility(View.VISIBLE);
                        targetContainer.setLayoutTransition(targetLayoutTransition);
                        sourceContainer.setLayoutTransition(layoutTransition);
                    }
                });
                set.start();

                if (Build.VERSION.SDK_INT < 16) {
                    targetContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    targetContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

    }

    /**
     * Animate the source view from source container to target container.
     * Animate the target view from target container to source container.
     *
     * @param rootContainer container that contain the source and the target object, must be object of DragContainer
     * @param sourceAnimatedView sourceView that want to be animated, must be direct child of root container
     * @param targetAnimatedView targetView that want to be animated, must be direct child of root container
     * @param sourceView View that what to be moved to target container, will be hidden, then show after animation
     * @param targetView View that what to be moved to source container, will be hidden, then show after animation
     * @param sourceContainer direct parent of sourceview
     * @param targetContainer direct parent of targetView
     * @param sourceParams Layoutparams for source container & targetView
     * @param targetParams Layoutparams for target container & sourceView
     */
    @TargetApi(14)
    public static void swap (final ViewGroup rootContainer, final View sourceAnimatedView,
                             final View targetAnimatedView,
                             final View sourceView, final View targetView,
                             final ViewGroup sourceContainer, final ViewGroup targetContainer,
                             @Nullable final ViewGroup.LayoutParams sourceParams,
                             @Nullable final ViewGroup.LayoutParams targetParams){

        final LayoutTransition sourceLayoutTransition, targetLayoutTransition; // to store layout transition while remove/add

        final Rect startBounds = new Rect();    // start view left, top
        final Rect endBounds = new Rect();      // end view start, top
        final Rect finalBounds = new Rect();    // container start top
        final Point globalOffset = new Point(); // to store global offset of container

        final int indexSource, indexTarget;
        sourceView.setVisibility(View.GONE);
        sourceView.getGlobalVisibleRect(startBounds);
        sourceLayoutTransition = sourceContainer.getLayoutTransition();
        sourceContainer.setLayoutTransition(null);
        indexSource = sourceContainer.indexOfChild(sourceView);
        sourceContainer.removeView(sourceView);

        targetView.setVisibility(View.GONE);
        targetView.getGlobalVisibleRect(endBounds);
        targetLayoutTransition = targetContainer.getLayoutTransition();
        targetContainer.setLayoutTransition(null);
        indexTarget = targetContainer.indexOfChild(targetView);
        targetContainer.removeView(targetView);

        rootContainer.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        endBounds.offset(-globalOffset.x, -globalOffset.y);

        sourceAnimatedView.setVisibility(View.VISIBLE);
        targetAnimatedView.setVisibility(View.VISIBLE);

        if (null == sourceParams) {
            sourceContainer.addView(targetView, indexSource);
        } else {
            sourceContainer.addView(targetView, indexSource, sourceParams);
        }

        if (null == targetParams) {
            targetContainer.addView(sourceView, indexTarget);
        }else{
            targetContainer.addView(sourceView, indexTarget, targetParams);
        }

        // ANIMATE
        targetContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                AnimatorSet setSource = new AnimatorSet();
                setSource.play(ObjectAnimator.ofFloat(sourceAnimatedView, View.X,
                        startBounds.left, endBounds.left))
                        .with(ObjectAnimator.ofFloat(sourceAnimatedView, View.Y,
                                startBounds.top, endBounds.top));
                setSource.setDuration(SHORT_DURATION);
                setSource.setInterpolator(getDecelerateInterpolator());
                setSource.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        targetContainer.setLayoutTransition(targetLayoutTransition);
                        sourceAnimatedView.setVisibility(View.GONE);
                        sourceView.setVisibility(View.VISIBLE);
                    }
                });
                setSource.start();
                if (Build.VERSION.SDK_INT < 16) {
                    targetContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    targetContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        sourceContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // ANIMATE TARGET
                AnimatorSet setTarget = new AnimatorSet();
                setTarget.play(ObjectAnimator.ofFloat(targetAnimatedView, View.X,
                        endBounds.left, startBounds.left))
                        .with(ObjectAnimator.ofFloat(targetAnimatedView, View.Y,
                                endBounds.top, startBounds.top));
                setTarget.setDuration(SHORT_DURATION);
                setTarget.setInterpolator(getDecelerateInterpolator());
                setTarget.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        sourceContainer.setLayoutTransition(sourceLayoutTransition);
                        targetAnimatedView.setVisibility(View.GONE);
                        targetView.setVisibility(View.VISIBLE);
                    }
                });
                setTarget.start();

                if (Build.VERSION.SDK_INT < 16) {
                    sourceContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    sourceContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

    }

    /**
     * Animate the source view from source container to target container.
     *
     * @param rootContainer container that contain the source and the target object, must be object of DragContainer
     * @param sourceView View that what to be moved, will be hidden then show after animation
     * @param sourceContainer direct parent of sourceview
     * @param targetContainer container for target object
     * @param indexToAdd indextoAdd for target container for moved object, -1 to add to the last
     * @param layoutParams layout params for target container & moved view
     */
    @TargetApi(14)
    public static void move (final ViewGroup rootContainer, final View sourceView,
                             final ViewGroup sourceContainer, final ViewGroup targetContainer,
                             final int indexToAdd, final ViewGroup.LayoutParams layoutParams){
        final LayoutTransition layoutTransition; // to store layout transition while remove/add

        final Rect startBounds = new Rect();    // start view left, top
        // final Rect endBounds = new Rect();      // end view start, top
        final Rect finalBounds = new Rect();    // container start top
        final Point globalOffset = new Point(); // to store global offset of container

        // replicate the sourceview by caching to bitmap
        sourceView.setDrawingCacheEnabled(true);
        // Create a copy of the drawing cache so that it does not get recycled
        // by the framework when the list tries to clean up memory
        Bitmap bitmap = Bitmap.createBitmap(sourceView.getDrawingCache(true));
        sourceView.setDrawingCacheEnabled(false);
        final ImageView ivAnimation = new ImageView(sourceView.getContext());
        ivAnimation.setImageBitmap(bitmap);
        ivAnimation.setLayoutParams(
                new ViewGroup.LayoutParams(sourceView.getWidth(), sourceView.getHeight()));

        sourceView.setVisibility(View.GONE);
        sourceView.getGlobalVisibleRect(startBounds);
        layoutTransition = sourceContainer.getLayoutTransition();
        sourceContainer.setLayoutTransition(null);
        sourceContainer.removeView(sourceView);

        final LayoutTransition targetLayoutTransition = targetContainer.getLayoutTransition();
        targetContainer.setLayoutTransition(null);
        if (null == layoutParams) {
            if (indexToAdd == -1) {
                targetContainer.addView(sourceView);
            }else{
                targetContainer.addView(sourceView, indexToAdd);
            }
        }else{
            if (indexToAdd == -1) {
                targetContainer.addView(sourceView, layoutParams);
            }else{
                targetContainer.addView(sourceView, indexToAdd, layoutParams);
            }
        }

        // below is needed to recalculate global Rectangle
        sourceView.setVisibility(View.INVISIBLE);

        // after the target has been added on targetcontainer, animate it
        targetContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // this logic is to cater the limitation because
                // getglobalVisibleRect only correct when the view is visible
                // hit bound to get the local rect, target will calculate global parent
                // sum the left and right to get the correct global rect

                Rect hitBound = new Rect();
                Rect targetContainerBound = new Rect();
                sourceView.getHitRect(hitBound);
                targetContainer.getGlobalVisibleRect(targetContainerBound);
                // right and bottom no matter
                Point endBounds = new Point(hitBound.left+targetContainerBound.left,
                        targetContainerBound.top + hitBound.top);
                rootContainer.getGlobalVisibleRect(finalBounds, globalOffset);
                startBounds.offset(-globalOffset.x, -globalOffset.y);
                endBounds.offset(-globalOffset.x, -globalOffset.y);

//                sourceView.getGlobalVisibleRect(endBounds);
//                rootContainer.getGlobalVisibleRect(finalBounds, globalOffset);
//                startBounds.offset(-globalOffset.x, -globalOffset.y);
//                endBounds.offset(-globalOffset.x, -globalOffset.y);

                if (Build.VERSION.SDK_INT < 16) {
                    targetContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    targetContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                // if out of bound, no need animation
//                if (endBounds.left > rootContainer.getRight() ||
//                        endBounds.right < rootContainer.getLeft() ||
//                        endBounds.top > rootContainer.getBottom() ||
//                        endBounds.bottom < rootContainer.getTop()) {
//                    rootContainer.removeView(ivAnimation);
//                    targetContainer.setLayoutTransition(targetLayoutTransition);
//                    sourceContainer.setLayoutTransition(layoutTransition);
//                    sourceView.setVisibility(View.VISIBLE);
//                    return;
//                }

                rootContainer.addView(ivAnimation);

                // ANIMATE
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator.ofFloat(ivAnimation, View.X,
                        startBounds.left, endBounds.x))
                        .with(ObjectAnimator.ofFloat(ivAnimation, View.Y,
                                startBounds.top, endBounds.y));
                set.setDuration(SHORT_DURATION);
                set.setInterpolator(getDecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        rootContainer.removeView(ivAnimation);
                        targetContainer.setLayoutTransition(targetLayoutTransition);
                        sourceContainer.setLayoutTransition(layoutTransition);
                        sourceView.setVisibility(View.VISIBLE);
                    }
                });
                set.start();
            }
        });

    }


//    static public void fadeIn(View v, AnimatorListenerAdapter adapter){
//        v.setAlpha(0.0F);
//        v.setVisibility(View.VISIBLE);
//        AnimatorSet fadeSet = new AnimatorSet();
//        fadeSet.play(ObjectAnimator.ofFloat(v, View.ALPHA, new float[]{1.0F}));
//        fadeSet.setInterpolator(new AccelerateDecelerateInterpolator());
//        fadeSet.setDuration(MEDIUM_DURATION);
//        if (adapter != null) {
//            fadeSet.addListener(adapter);
//        }
//        fadeSet.start();
//    }

    public static void startAnimatedImageView(ImageView imageView,
                                              int resAnimVectDrawable,
                                              int plainDrawable,
                                              int tintColor,
                                              @NonNull PorterDuff.Mode porterMode,
                                              boolean useAnimation){
        if (useAnimation && Build.VERSION.SDK_INT >= 21) {
            imageView.setImageResource(resAnimVectDrawable);
            if (tintColor != 0) {
                imageView.getDrawable().setColorFilter(tintColor, porterMode);
            }
            ((Animatable) imageView.getDrawable()).start();
        }
        else {
            imageView.setImageResource(plainDrawable);
            if (tintColor != 0) {
                imageView.getDrawable().setColorFilter(tintColor, porterMode);
            }
        }
    }

    public static void expand(final View v, boolean animated, final int time) {
        // v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        final int targetHeight = v.getMeasuredHeight();

        if (!animated || Build.VERSION.SDK_INT < 21) {
            v.setVisibility(View.VISIBLE);
            return;
        }
        int tempHeight = v.getLayoutParams().height;// height;
        if (tempHeight <= 0) {
            // do inflating before animation
            v.setVisibility(View.INVISIBLE);
            final LinearLayout viewParent = (LinearLayout)v.getParent();
            viewParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // int height = v.getHeight();
                    v.getLayoutParams().height = v.getHeight();
                    // expand(v, height);
                    doExpandAfterHeightFixed(v, time);
                    if (Build.VERSION.SDK_INT < 16) {
                        viewParent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        viewParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
        else{
            doExpandAfterHeightFixed(v, time);
        }
    }

    private static void doExpandAfterHeightFixed(final View v, int time){
        final int targetHeight = v.getLayoutParams().height;// height;
        if (targetHeight <= 0) {
            v.setVisibility(View.VISIBLE);
            return;
        }
        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        if (time > 0) {
            a.setDuration(time);
        }
        else { // 1dp/ms
            int duration = (int)(targetHeight
                    / v.getContext().getResources().getDisplayMetrics().density * 5);
            a.setDuration(duration);
        }
        a.setInterpolator(new AccelerateDecelerateInterpolator());
        v.startAnimation(a);
    }

    public static void collapse(final View v, boolean animated,  int time) {
        if (!animated || Build.VERSION.SDK_INT < 21) {
            v.setVisibility(View.GONE);
            return;
        }

        //final int initialHeight = v.getMeasuredHeight();
        final int initialHeight = v.getHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.getLayoutParams().height = initialHeight;
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        if (time > 0) {
            a.setDuration(time);
        }
        else { // 1dp/ms
            a.setDuration((int) (
                    initialHeight /
                    v.getContext().getResources().getDisplayMetrics().density * 5));
        }
        a.setInterpolator(new AccelerateDecelerateInterpolator());
        v.startAnimation(a);
    }

}
