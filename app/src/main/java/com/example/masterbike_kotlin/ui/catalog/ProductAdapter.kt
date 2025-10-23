package com.example.masterbike_kotlin.ui.catalog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.masterbike_kotlin.R
import com.example.masterbike_kotlin.data.models.Product
import com.example.masterbike_kotlin.databinding.ItemProductBinding
import java.text.NumberFormat
import java.util.*

class ProductAdapter(
    private val onProductClick: (Product) -> Unit,
    private val onAddToCartClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                // Product image
                ivProductImage.load(product.imageUrl) {
                    placeholder(R.drawable.ic_launcher_background)
                    error(R.drawable.ic_launcher_background)
                    crossfade(true)
                }

                // Product name
                tvProductName.text = product.name

                // Product price
                tvProductPrice.text = formatPrice(product.price)

                // Product category
                tvProductCategory.text = when (product.category) {
                    com.example.masterbike_kotlin.data.models.ProductCategory.BICYCLE -> "🚴 Bicicleta"
                    com.example.masterbike_kotlin.data.models.ProductCategory.PART -> "⚙️ Repuesto"
                }

                // Stock status
                tvStockStatus.text = if (product.stock > 0) {
                    "En stock (${product.stock})"
                } else {
                    "Agotado"
                }
                tvStockStatus.setTextColor(
                    itemView.context.getColor(
                        if (product.stock > 0) R.color.green else R.color.red
                    )
                )

                // Click listeners
                root.setOnClickListener { onProductClick(product) }
                btnAddToCart.setOnClickListener { onAddToCartClick(product) }

                // Disable add to cart if out of stock
                btnAddToCart.isEnabled = product.stock > 0
                btnAddToCart.alpha = if (product.stock > 0) 1.0f else 0.5f
            }
        }

        private fun formatPrice(price: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
            return format.format(price)
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}