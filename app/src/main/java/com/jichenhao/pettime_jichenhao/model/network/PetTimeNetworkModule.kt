package com.jichenhao.pettime_jichenhao.model.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// 用来告诉Hilt如何提供PetTimeNetwork实例
@Module
@InstallIn(SingletonComponent::class)
object PetTimeNetworkModule {
    @Provides
    fun providePetTimeNetwork(): PetTimeNetwork {
        return PetTimeNetwork // 或其他实现方式
    }
}