package com.example.testet300enroll;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.util.Log;
import android.view.View;

public abstract class FPAnimationDrawableCallback implements Callback {

    private static final String TAG = "AnimationCallback";

	/**
     * The last frame of {@link Drawable} in the {@link AnimationDrawable}.
     */
    private Drawable mLastFrame;

    /**
     * The client's {@link Callback} implementation. All calls are proxied to this wrapped {@link Callback}
     * implementation after intercepting the events we need.
     */
    private Callback mWrappedCallback;

    /**
     * Flag to ensure that {@link #onAnimationComplete()} is called only once, since
     * {@link #invalidateDrawable(Drawable)} may be called multiple times.
     */
    private boolean mIsCallbackTriggered = false;

    /**
     * 
     * @param animationDrawable
     *            the {@link AnimationDrawable}.
     * @param callback
     *            the client's {@link Callback} implementation. This is usually the {@link View} the has the
     *            {@link AnimationDrawable} as background.
     */
    public FPAnimationDrawableCallback(AnimationDrawable animationDrawable, Callback callback) {
        mLastFrame = animationDrawable.getFrame(animationDrawable.getNumberOfFrames() - 1);
        mWrappedCallback = callback;
    }

    @Override
    public void invalidateDrawable(Drawable who) {
    	Log.d(TAG, "invalidateDrawable");
    	//Log.d(TAG, "Thread Name: " + Thread.currentThread().getName());
        if (mWrappedCallback != null) {
            mWrappedCallback.invalidateDrawable(who);
        }	        
        if (!mIsCallbackTriggered && mLastFrame != null && mLastFrame.equals(who.getCurrent())) {
            mIsCallbackTriggered = true;
            onAnimationComplete();
        }
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        if (mWrappedCallback != null) {
            mWrappedCallback.scheduleDrawable(who, what, when);
        }
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        if (mWrappedCallback != null) {
            mWrappedCallback.unscheduleDrawable(who, what);
        }
    }

    //
    // Public methods.
    //

    /**
     * Callback triggered when {@link View#invalidateDrawable(Drawable)} has been called on the last frame, which marks
     * the end of a non-looping animation sequence.
     */
    public abstract void onAnimationComplete();
}
