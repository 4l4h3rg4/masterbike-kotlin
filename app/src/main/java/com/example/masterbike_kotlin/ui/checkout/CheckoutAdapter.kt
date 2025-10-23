package com.example.masterbike_kotlin.ui.checkout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.masterbike_kotlin.R
import com.example.masterbike_kotlin.data.models.CartItem
import com.example.masterbike_kotlin.databinding.ItemCheckoutBinding
import java.text.NumberFormat
import java.util.*

class CheckoutAdapter : ListAdapter<CartItem, CheckoutAdapter.CheckoutViewHolder>(CheckoutDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
        val binding = ItemCheckoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CheckoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CheckoutViewHolder(private val binding: ItemCheckoutBinding) :
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

                    // Quantity
                    tvQuantity.text = "Cantidad: ${cartItem.quantity}"

                    // Subtotal
                    val subtotal = product.price * cartItem.quantity
                    tvSubtotal.text = formatPrice(subtotal)
                }
            }
        }

        private fun formatPrice(price: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
            return format.format(price)
        }
    }

    class CheckoutDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}