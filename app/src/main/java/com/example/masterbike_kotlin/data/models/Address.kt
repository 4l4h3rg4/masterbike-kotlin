package com.example.masterbike_kotlin.data.models

import com.google.gson.annotations.SerializedName

data class Address(
    @SerializedName("id")
    val id: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("street")
    val street: String,

    @SerializedName("city")
    val city: String,

    @SerializedName("state")
    val state: String,

    @SerializedName("zip_code")
    val zipCode: String,

    @SerializedName("country")
    val country: String,

    @SerializedName("is_default")
    val isDefault: Boolean = false,

    @SerializedName("created_at")
    val createdAt: String?
)