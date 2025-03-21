package com.example.androidpracticumcustomview

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class XmlActivity : AppCompatActivity() {

    private lateinit var bakeryItems: List<BakeryItem>
    private lateinit var bakeryAdapter: BakeryItemAdapter
    private lateinit var totalPriceText: TextView
    private var cartItems: MutableList<BakeryItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bakery_order)


        val backButton: ImageButton = findViewById(R.id.back_button)
        totalPriceText = findViewById(R.id.total_price_text)


        backButton.setOnClickListener {
            onBackPressed()
        }


        bakeryItems = listOf(
            BakeryItem("Baguette", 40.0, R.drawable.baget),
            BakeryItem("Croissant", 50.0, R.drawable.kruassan)
        )

        bakeryAdapter = BakeryItemAdapter(bakeryItems) { bakeryItem ->
            addToCart(bakeryItem)
        }

        val recyclerView: RecyclerView = findViewById(R.id.bakery_items_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = bakeryAdapter

        updateTotalPrice()

        animateView(backButton, 300)
        animateView(findViewById(R.id.title), 500)
        animateView(recyclerView, 700)
        animateView(totalPriceText, 900)
    }

    private fun addToCart(bakeryItem: BakeryItem) {
        cartItems.add(bakeryItem)
        updateTotalPrice()
    }

    private fun updateTotalPrice() {
        val totalPrice = cartItems.sumByDouble { it.price }

        val totalPriceFloat = totalPrice.toFloat()


        val animator = ValueAnimator.ofFloat(totalPriceFloat - (totalPriceFloat / 2), totalPriceFloat)
        animator.duration = 1000  //
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            totalPriceText.text = "Total: %.2f rub".format(animatedValue)
        }
        animator.start()
    }

    private fun animateView(view: View, delay: Long) {

        val scaleXAnimator = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f)
        val scaleYAnimator = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f)

        val translationXAnimator = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0f)
        val translationYAnimator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f)

        scaleXAnimator.startDelay = delay
        scaleYAnimator.startDelay = delay
        translationXAnimator.startDelay = delay
        translationYAnimator.startDelay = delay

        scaleXAnimator.duration = 5000
        scaleYAnimator.duration = 5000
        translationXAnimator.duration = 5000
        translationYAnimator.duration = 5000

        scaleXAnimator.start()
        scaleYAnimator.start()
        translationXAnimator.start()
        translationYAnimator.start()
    }
}