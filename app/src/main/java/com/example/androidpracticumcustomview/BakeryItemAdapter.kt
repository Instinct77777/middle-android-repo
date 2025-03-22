package com.example.androidpracticumcustomview

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

data class BakeryItemAdapter(
    private val bakeryItems: List<BakeryItem>,
    private val onAddToCart: (BakeryItem) -> Unit
) : RecyclerView.Adapter<BakeryItemAdapter.BakeryViewHolder>() {

    inner class BakeryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.bakery_item_name)
        val itemPrice: TextView = itemView.findViewById(R.id.bakery_item_price)
        val itemImage: ImageView = itemView.findViewById(R.id.bakery_item_image)
        val addToCartButton: MaterialButton = itemView.findViewById(R.id.add_to_cart_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BakeryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bakery_item_layout, parent, false)
        return BakeryViewHolder(view)
    }


    override fun onBindViewHolder(holder: BakeryViewHolder, position: Int) {
        val bakeryItem = bakeryItems[position]
        holder.itemName.text = bakeryItem.name
        holder.itemPrice.text = "Price: %.2f rub".format(bakeryItem.price)
        holder.itemImage.setImageResource(bakeryItem.imageRes)

        val fadeIn = ObjectAnimator.ofFloat(holder.itemView, "alpha", 0f, 1f)
        fadeIn.duration = 500
        fadeIn.start()

        holder.addToCartButton.setOnClickListener {
            onAddToCart(bakeryItem)
        }
    }

    override fun getItemCount(): Int {
        return bakeryItems.size
    }
}
