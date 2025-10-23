package com.example.masterbike_kotlin.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.masterbike_kotlin.R
import com.example.masterbike_kotlin.data.models.Order
import com.example.masterbike_kotlin.databinding.ItemOrderBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter : ListAdapter<Order, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.apply {
                // Order ID
                tvOrderId.text = "Pedido #${order.id.takeLast(8)}"

                // Order date
                tvOrderDate.text = formatDate(order.createdAt)

                // Order status
                tvOrderStatus.text = when (order.status) {
                    com.example.masterbike_kotlin.data.models.OrderStatus.PENDING -> "Pendiente"
                    com.example.masterbike_kotlin.data.models.OrderStatus.CONFIRMED -> "Confirmado"
                    com.example.masterbike_kotlin.data.models.OrderStatus.SHIPPED -> "Enviado"
                    com.example.masterbike_kotlin.data.models.OrderStatus.DELIVERED -> "Entregado"
                    com.example.masterbike_kotlin.data.models.OrderStatus.CANCELLED -> "Cancelado"
                }

                // Status color
                val statusColor = when (order.status) {
                    com.example.masterbike_kotlin.data.models.OrderStatus.PENDING -> R.color.orange
                    com.example.masterbike_kotlin.data.models.OrderStatus.CONFIRMED -> R.color.blue
                    com.example.masterbike_kotlin.data.models.OrderStatus.SHIPPED -> R.color.purple
                    com.example.masterbike_kotlin.data.models.OrderStatus.DELIVERED -> R.color.green
                    com.example.masterbike_kotlin.data.models.OrderStatus.CANCELLED -> R.color.red
                }
                tvOrderStatus.setTextColor(itemView.context.getColor(statusColor))

                // Order total
                tvOrderTotal.text = formatPrice(order.totalAmount)

                // Items count
                val itemsCount = order.items?.size ?: 0
                tvItemsCount.text = "$itemsCount ${if (itemsCount == 1) "producto" else "productos"}"
            }
        }

        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                dateString
            }
        }

        private fun formatPrice(price: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
            return format.format(price)
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}