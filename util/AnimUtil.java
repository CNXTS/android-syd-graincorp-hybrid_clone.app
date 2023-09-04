package com.webling.graincorp.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public class AnimUtil {

    private static final int SHORT_ANIM_TIME = 200;

    public static void showProgress(final boolean show, View view, View progressView) {
        showProgress(show, view, progressView, View.GONE);
    }

    public static void showProgress(final boolean show, View view, View progressView, int viewVisibilityHidden) {
        if (view != null) {
            view.setVisibility(show ? viewVisibilityHidden : View.VISIBLE);
            view.animate().setDuration(SHORT_ANIM_TIME).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(show ? viewVisibilityHidden : View.VISIBLE);
                }
            });
        }

        if (progressView != null) {
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(SHORT_ANIM_TIME).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
    }
}
