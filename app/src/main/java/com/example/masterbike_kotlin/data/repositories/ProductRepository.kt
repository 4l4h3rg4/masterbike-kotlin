package com.example.masterbike_kotlin.data.repositories

import com.example.masterbike_kotlin.data.api.SupabaseClient
import com.example.masterbike_kotlin.data.models.Product
import com.example.masterbike_kotlin.data.models.ProductCategory
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductRepository {

    suspend fun getAllProducts(): List<Product> {
        return SupabaseClient.client.postgrest["products"]
            .select()
            .decodeList<Product>()
    }

    suspend fun getProductsByCategory(category: ProductCategory): List<Product> {
        return SupabaseClient.client.postgrest["products"]
            .select {
                filter {
                    eq("category", category.name.lowercase())
                }
            }
            .decodeList<Product>()
    }

    suspend fun searchProducts(query: String): List<Product> {
        return SupabaseClient.client.postgrest["products"]
            .select {
                filter {
                    or {
                        ilike("name", "%$query%")
                        ilike("description", "%$query%")
                    }
                }
            }
            .decodeList<Product>()
    }

    suspend fun getProductById(id: String): Product? {
        return SupabaseClient.client.postgrest["products"]
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingleOrNull<Product>()
    }
}