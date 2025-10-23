package com.example.masterbike_kotlin.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.masterbike_kotlin.R
import com.example.masterbike_kotlin.data.models.CartItem
import com.example.masterbike_kotlin.databinding.ItemCartBinding
import java.text.NumberFormat
import java.util.*

class CartAdapter(
    private val onQuantityChanged: (CartItem, Int) -> Unit,
    private val onRemoveItem: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.apply {
                // Product image
                cartItem.product?.let { product ->
                    ivProductImage.load(product.imageUrl) {
                        placeholder(R.drawable.ic_launcher_background)
                        error(R.drawable.ic_launcher_background)
                        crossfade(true)
                    }

                    // Product name
                    tvProductName.text = product.name

                    // Product price
                    tvProductPrice.text = formatPrice(product.price)

                    // Subtotal
                    val subtotal = product.price * cartItem.quantity
                    tvSubtotal.text = formatPrice(subtotal)
                }

                // Quantity controls
                tvQuantity.text = cartItem.quantity.toString()

                btnIncrease.setOnClickListener {
                    val newQuantity = cartItem.quantity + 1
                    onQuantityChanged(cartItem, newQuantity)
                }

                btnDecrease.setOnClickListener {
                    if (cartItem.quantity > 1) {
                        val newQuantity = cartItem.quantity - 1
                        onQuantityChanged(cartItem, newQuantity)
                    }
                }

                // Remove button
                btnRemove.setOnClickListener {
                    onRemoveItem(cartItem)
                }
            }
        }

        private fun formatPrice(price: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
            return format.format(price)
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}