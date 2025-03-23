package com.example.androidpracticumcustomview

import android.animation.ValueAnimator
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ImageView
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

        // Initialize views
        val backButton: ImageButton = findViewById(R.id.back_button)
        totalPriceText = findViewById(R.id.total_price_text)
        val customViewGroup: CustomViewGroup = findViewById(R.id.custom_view_group)


        backButton.setOnClickListener {
            onBackPressed()
        }


        setupCustomViewGroup(customViewGroup)


        setupBakeryItems()
    }

    private fun setupCustomViewGroup(customViewGroup: CustomViewGroup) {
        // Add up to 2 child views
        val images = listOf(R.drawable.baget, R.drawable.kruassan)
       // val names = listOf("Baguette", "Croissant")

        for (i in images.indices) {
            val imageView = ImageView(this).apply {
                setImageResource(images[i])
                scaleType = ImageView.ScaleType.CENTER_CROP
                layoutParams = FrameLayout.LayoutParams(200, 200) // Set size
            }


        }
    }

    private fun setupBakeryItems() {
        // Define bakery items
        bakeryItems = listOf(
           BakeryItem("Baguette", 40.0, R.drawable.baget),
           BakeryItem("Croissant", 50.0, R.drawable.kruassan),
          //  BakeryItem("Bread", 30.0, R.drawable.bread),
            //BakeryItem("Cake", 100.0, R.drawable.cake)
        )

        // Set up RecyclerView
        bakeryAdapter = BakeryItemAdapter(bakeryItems) { bakeryItem ->
            addToCart(bakeryItem)
        }

        val recyclerView: RecyclerView = findViewById(R.id.bakery_items_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = bakeryAdapter
    }

    private fun addToCart(bakeryItem: BakeryItem) {
        cartItems.add(bakeryItem)
        updateTotalPrice()
    }

    private fun updateTotalPrice() {
        val totalPrice = cartItems.sumByDouble { it.price }
        val totalPriceFloat = totalPrice.toFloat()

        val animator = ValueAnimator.ofFloat(totalPriceFloat - (totalPriceFloat / 2), totalPriceFloat)
        animator.duration = 1000
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            totalPriceText.text = "Total: %.2f rub".format(animatedValue)
        }
        animator.start()
    }
}