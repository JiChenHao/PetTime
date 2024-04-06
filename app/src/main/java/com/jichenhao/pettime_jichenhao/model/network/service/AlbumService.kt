package com.jichenhao.pettime_jichenhao.model.network.service

import com.jichenhao.pettime_jichenhao.model.entity.PictureInfo
import com.jichenhao.pettime_jichenhao.model.response.AlbumResponse
import com.jichenhao.pettime_jichenhao.model.response.OperationResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

//这个类负责向API发起调用请求并获得返回的JSON数据
interface AlbumService {
    //使用@GET注解，当调用这个方法的时候，Retrofit就会发起一条GET请求，去访问@GET注解中配置的地址
    @GET("getAlbumByEmail")
    fun getAlbumByEmail(@Query("userEmail") userEmail: String): Call<AlbumResponse>

    // 添加一条图片信息,返回一个操作结果
    @POST("addPicture")
    fun addPicture(@Body album: PictureInfo): Call<OperationResponse>

    // 删除一个图片信息，返回一个操作结果
    @POST("deletePicture")
    fun deletePicture(@Query("picId") picId: Int): Call<OperationResponse>
}