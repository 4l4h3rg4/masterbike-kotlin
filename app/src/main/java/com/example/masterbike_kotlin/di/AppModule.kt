package com.example.masterbike_kotlin.di

import com.example.masterbike_kotlin.data.repositories.AuthRepository
import com.example.masterbike_kotlin.data.repositories.CartRepository
import com.example.masterbike_kotlin.data.repositories.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepository()
    }

    @Provides
    @Singleton
    fun provideProductRepository(): ProductRepository {
        return ProductRepository()
    }

    @Provides
    @Singleton
    fun provideCartRepository(): CartRepository {
        return CartRepository()
    }
}