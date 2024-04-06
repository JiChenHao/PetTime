package com.jichenhao.pettime_jichenhao.model.network.service

import com.jichenhao.pettime_jichenhao.model.entity.Pet
import com.jichenhao.pettime_jichenhao.model.response.OperationResponse
import com.jichenhao.pettime_jichenhao.model.response.PetResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

//这个类负责向API发起调用请求并获得返回的JSON数据
interface PetService {
    //使用@GET注解，当调用这个方法的时候，Retrofit就会发起一条GET请求，去访问@GET注解中配置的地址
    @GET("getPetListByUserEmail")
    fun getPetListByUserEmail(@Query("email") email: String): Call<PetResponse>

    // 添加一条宠物信息,返回一个操作结果
    @POST("addPet")
    fun addPetInfoToDB(@Body pet: Pet): Call<OperationResponse>

    // 更新一条宠物信息,返回一个操作结果
    @POST("updatePet")
    fun updatePet(@Body pet: Pet): Call<OperationResponse>

    // 删除一个宠物信息，返回一个操作结果
    @POST("deletePet")
    fun deletePet(@Query("petId") petId: Int): Call<OperationResponse>
}