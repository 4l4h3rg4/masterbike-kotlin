package com.example.masterbike_kotlin.data.models

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("price")
    val price: Double,

    @SerializedName("stock")
    val stock: Int,

    @SerializedName("category")
    val category: ProductCategory,

    @SerializedName("image_url")
    val imageUrl: String?,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?
)

enum class ProductCategory {
    @SerializedName("bicycle")
    BICYCLE,

    @SerializedName("part")
    PART
}