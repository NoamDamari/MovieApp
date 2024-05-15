package com.example.movieapp

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

class Animator {

    fun expandViewAnim(view : View) {

        view.visibility = View.VISIBLE

        view.measure(
            View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val targetHeight = view.measuredHeight

        val anim = ValueAnimator.ofInt(1, targetHeight)
        anim.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            view.layoutParams.height = value
            view.requestLayout()
        }
        anim.interpolator = AccelerateDecelerateInterpolator()
        anim.duration = 750
        anim.start()
    }

    fun collapseViewAnim(view : View) {
        val initialHeight = view.height

        val anim = ValueAnimator.ofInt(initialHeight, 0)
        anim.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            view.layoutParams.height = value
            view.requestLayout()
        }
        anim.interpolator = AccelerateDecelerateInterpolator()
        anim.duration = 750
        anim.start()
    }
}