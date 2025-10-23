package com.example.masterbike_kotlin.data.models

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("id")
    val id: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("total_amount")
    val totalAmount: Double,

    @SerializedName("status")
    val status: OrderStatus,

    @SerializedName("shipping_address_id")
    val shippingAddressId: String?,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String?,

    @SerializedName("items")
    val items: List<OrderItem>? = null,

    @SerializedName("shipping_address")
    val shippingAddress: Address? = null
)

data class OrderItem(
    @SerializedName("id")
    val id: String,

    @SerializedName("order_id")
    val orderId: String,

    @SerializedName("product_id")
    val productId: String,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("unit_price")
    val unitPrice: Double,

    @SerializedName("product")
    val product: Product? = null
)

enum class OrderStatus {
    @SerializedName("pending")
    PENDING,

    @SerializedName("confirmed")
    CONFIRMED,

    @SerializedName("shipped")
    SHIPPED,

    @SerializedName("delivered")
    DELIVERED,

    @SerializedName("cancelled")
    CANCELLED
}