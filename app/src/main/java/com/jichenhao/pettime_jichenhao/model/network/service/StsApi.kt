package com.jichenhao.pettime_jichenhao.model.network.service

import com.jichenhao.pettime_jichenhao.model.response.StsResponse
import retrofit2.Call
import retrofit2.http.GET

interface StsApi {
    @GET("getStsToken")
    fun getStsToken(): Call<StsResponse>
}