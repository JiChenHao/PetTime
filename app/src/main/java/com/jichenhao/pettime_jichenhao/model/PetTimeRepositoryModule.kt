package com.jichenhao.pettime_jichenhao.model

import com.jichenhao.pettime_jichenhao.model.network.PetTimeNetwork
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// 用来告诉Hilt如何提供Repository实例
@Module
@InstallIn(SingletonComponent::class)
//这里将@InstallIn(SingletonComponent::class)添加到RepositoryModule上，
// 表示这个模块中的提供方法应注册到SingletonComponent中，确保MyRepository在整个应用程序中只有一个实例。
object PetTimeRepositoryModule {
    @Provides
    fun providePetTimeRepository(petTimeNetwork: PetTimeNetwork): PetTimeRepository {
        return PetTimeRepositoryImpl(petTimeNetwork)
    }
}