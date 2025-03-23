package com.example.androidpracticumcustomview

import android.animation.ObjectAnimator
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.view.animation.AccelerateDecelerateInterpolator


class XmlActivity : AppCompatActivity() {

    private lateinit var totalPriceText: TextView
    private lateinit var customViewGroup: CustomViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bakery_order)

        // Initialize views
        val backButton: ImageButton = findViewById(R.id.back_button)
        totalPriceText = findViewById(R.id.total_price_text)
        customViewGroup = findViewById(R.id.custom_view_group)

        backButton.setOnClickListener {
            onBackPressed()
        }

        setupCustomViewGroup(customViewGroup)
    }


    private fun setupCustomViewGroup(customViewGroup: CustomViewGroup) {
        val images = listOf(R.drawable.baget, R.drawable.kruassan)


        for (i in images.indices) {
            val imageView = ImageView(this).apply {
                setImageResource(images[i])
                scaleType = ImageView.ScaleType.CENTER_CROP
                layoutParams = FrameLayout.LayoutParams(200, 200).apply {
                    gravity = android.view.Gravity.CENTER_HORIZONTAL // Center horizontally
                    topMargin = if (i == 0) 0 else customViewGroup.height // First element at the top, second at the bottom
                }
            }


            customViewGroup.addView(imageView)


            customViewGroup.animateView(imageView, isSecondChild = (i == 1), delay = i * 500)
        }


        val priceAnimator = ObjectAnimator.ofFloat(totalPriceText, "translationY", 0f, -300f)
        priceAnimator.duration = 1000
        priceAnimator.interpolator = AccelerateDecelerateInterpolator()
        priceAnimator.start()
    }
}
