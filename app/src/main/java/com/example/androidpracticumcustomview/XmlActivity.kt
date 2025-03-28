package com.example.androidpracticumcustomview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.Animation.AnimationListener
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.isEmpty
import androidx.core.view.isInvisible

class CustomViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val animationDuration: Long = 2000L,
    private val offsetDuration: Long = 5000L
) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "CustomViewGroup"
        private const val MAX_CHILDREN = 2
        private const val INITIAL_OFFSET_FACTOR = 0.5f
    }

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val childWidthSpec = MeasureSpec.makeMeasureSpec(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.AT_MOST
        )
        val childHeightSpec = MeasureSpec.makeMeasureSpec(
            MeasureSpec.getSize(heightMeasureSpec),
            MeasureSpec.AT_MOST
        )

        for (i in 0 until childCount) {
            try {
                getChildAt(i)?.measure(childWidthSpec, childHeightSpec)
            } catch (e: Exception) {
                handleError(e, "Error measuring child $i")
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (isEmpty()) return

        for (i in 0 until childCount) {
            val child = getChildAt(i) ?: continue
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            val centerX = (width - childWidth) / 2
            val centerY = (height - childHeight) / 2
            val offset = (childHeight * INITIAL_OFFSET_FACTOR).toInt()

            val childTop = if (i == 0) centerY - offset else centerY + offset

            try {
                child.layout(centerX, childTop, centerX + childWidth, childTop + childHeight)
            } catch (e: Exception) {
                handleError(e, "Error laying out child $i")
            }
        }

        if (childCount == MAX_CHILDREN) {
            post {
                for (i in 0 until childCount) {
                    getChildAt(i)?.takeIf { it.isInvisible }?.let {
                        animateChild(it, i == 1)
                    }
                }
            }
        }
    }

    override fun addView(child: View?) {
        if (childCount >= MAX_CHILDREN) {
            throw IllegalStateException("$TAG can contain max $MAX_CHILDREN children")
        }
        try {
            child?.visibility = View.INVISIBLE
            super.addView(child)
        } catch (e: Exception) {
            handleError(e, "Error adding child")
            throw e
        }
    }

    private fun animateChild(view: View, isSecond: Boolean) {
        try {
            if (view.width == 0 || view.height == 0 || width == 0 || height == 0) return

            val finalTop = if (isSecond) height - view.height else 0
            val currentTop = view.top
            val toY = (finalTop - currentTop).toFloat()

            val animationSet = AnimationSet(true).apply {
                addAnimation(AlphaAnimation(0f, 1f).apply {
                    duration = animationDuration
                })

                addAnimation(TranslateAnimation(0f, 0f, 0f, toY).apply {
                    duration = offsetDuration
                    interpolator = AccelerateDecelerateInterpolator()
                    fillAfter = true

                })

                setAnimationListener(object : AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        view.visibility = View.VISIBLE
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        view.post {
                            view.layout(
                                view.left,
                                finalTop,
                                view.right,
                                finalTop + view.height
                            )
                            view.clearAnimation()
                        }
                    }

                    override fun onAnimationRepeat(animation: Animation?) = Unit
                })
            }

            view.startAnimation(animationSet)
        } catch (e: Exception) {
            handleError(e, "Error animating child")
        }
    }

    private fun handleError(e: Exception, message: String) {
        Log.e(TAG, message, e)
    }
}

class XmlActivity : AppCompatActivity() {
    private lateinit var customViewGroup: CustomViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bakery_order)
        customViewGroup = findViewById(R.id.custom_view_group)
        setupViews()
    }

    private fun setupViews() {
        val images = listOf(R.drawable.kruassan, R.drawable.kruassan)
        try {
            images.forEach { resId ->
                ImageView(this).apply {
                    setImageResource(resId)
                    scaleType = ImageView.ScaleType.CENTER_CROP

                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    customViewGroup.addView(this)
                }
            }
        } catch (e: IllegalStateException) {
            Log.e("XmlActivity", "View setup failed", e)
        }
    }
}