package com.example.androidpracticumcustomview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

class CustomViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    fun animateView(view: View, isSecondChild: Boolean, delay: Int) {
        // Opacity animation from 0 to 1
        val alphaAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f)

        // Translation animation: first child moves up, second child moves down
        val translationYAnimator = if (isSecondChild) {
            ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 1000f, 0f) // Move down for second child
        } else {
            ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, -1000f, 0f) // Move up for first child
        }


        alphaAnimator.startDelay = delay.toLong()
        translationYAnimator.startDelay = delay.toLong()

        val duration = 5000L
        alphaAnimator.duration = duration
        translationYAnimator.duration = duration


        alphaAnimator.start()
        translationYAnimator.start()


        translationYAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                view.visibility = View.VISIBLE // Ensure view is visible after animation
            }
        })
    }
}
