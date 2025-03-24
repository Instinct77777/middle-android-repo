package com.example.androidpracticumcustomview

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class CustomViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val MAX_CHILDREN = 2
        private const val ALPHA_ANIMATION_DURATION = 2000L // Fade duration 2000ms
        private const val TRANSLATION_ANIMATION_DURATION = 5000L // Movement duration 5000ms
    }

    private var viewHeight = 0

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewHeight = h
    }

    override fun addView(child: View?) {
        enforceMaxChildren()
        super.addView(child)
    }

    override fun addView(child: View?, index: Int) {
        enforceMaxChildren()
        super.addView(child, index)
    }

    private fun enforceMaxChildren() {
        if (childCount >= MAX_CHILDREN) {
            throw IllegalStateException("CustomViewGroup can contain no more than $MAX_CHILDREN children")
        }
    }

    private fun animateChild(view: View, delay: Long) {
        if (indexOfChild(view) < 0) {
            throw IllegalStateException("View must be added before animation")
        }

        view.alpha = 0f
        view.translationY = 0f
        view.visibility = View.VISIBLE

        val targetTranslation = when (indexOfChild(view)) {
            0 -> -viewHeight / 4f
            1 -> viewHeight / 4f
            else -> 0f
        }

        ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
            duration = ALPHA_ANIMATION_DURATION
            startDelay = delay
            start()
        }

        ObjectAnimator.ofFloat(view, "translationY", 0f, targetTranslation).apply {
            duration = TRANSLATION_ANIMATION_DURATION
            startDelay = delay
            start()
        }
    }

    fun animateAllChildren() {
        for (i in 0 until childCount) {
            getChildAt(i)?.let {
                animateChild(it, i * 500L)
            }
        }
    }
}

class XmlActivity : AppCompatActivity() {
    private lateinit var customViewGroup: CustomViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bakery_order)

        findViewById<ImageButton>(R.id.back_button).setOnClickListener { onBackPressed() }
        customViewGroup = findViewById(R.id.custom_view_group)

        setupCustomViewGroup()
    }

    private fun setupCustomViewGroup() {
        val images = listOf(R.drawable.baget, R.drawable.kruassan)

        try {
            images.forEachIndexed { index, resId ->
                ImageView(this).apply {
                    setImageResource(resId)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    layoutParams = LinearLayout.LayoutParams(500, 500).apply {
                        gravity = Gravity.CENTER
                        topMargin = if (index == 0) 0 else 20
                    }
                    customViewGroup.addView(this)
                }
            }
            customViewGroup.post {
                customViewGroup.animateAllChildren()
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }
}