package com.example.masterbike_kotlin.data.models

import com.google.gson.annotations.SerializedName

data class CartItem(
    @SerializedName("id")
    val id: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("product_id")
    val productId: String,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("product")
    val product: Product? = null
)