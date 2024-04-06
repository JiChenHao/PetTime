package com.jichenhao.pettime_jichenhao.model.network.service

import com.jichenhao.pettime_jichenhao.model.response.PetCuisineResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PetCuisineService {
    //使用@GET注解，当调用这个方法的时候，Retrofit就会发起一条GET请求，去访问@GET注解中配置的地址
    @GET("getAllPetCuisineList")
    fun getAllPetCuisineList(): Call<PetCuisineResponse>
}