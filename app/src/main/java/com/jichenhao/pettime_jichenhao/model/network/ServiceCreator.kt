package com.jichenhao.pettime_jichenhao.model.network

import android.util.Log
import com.jichenhao.pettime_jichenhao.model.entity.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//单例类，Retrofit构建器
object ServiceCreator {
    // 这里注意，android会将自己的地址默认为localhost（127.0.0.1）
    // 如果要访问电脑本地服务器，需要将ip地址设置为10.0.2.2
    // 我的云服务器47.96.189.254
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    // 每次登录/从本地读取用户信息登录都要获取一次，token失效时间是7天，
    // 如果没有过期且用户不重新登录的话，token就会存放到本地
    //private var jwtToken:String ?=null
    private var jwtToken = "";

    /**
     * 为jwtToken专门创建的存取方法
     * */
    fun setJwtToken(token:String){
        jwtToken = token
        Log.d("jwtTOKEN","jwtToken被初始化为了$jwtToken")
    }

    private val okHttpClientBuilder = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(jwtToken)) // 添加JWT Token拦截器，可以为空
        .build()

    // 初始化Retrofit2构建器
    private val retrofit =
        Retrofit.Builder()
            .client(okHttpClientBuilder)// 添加带有header的拦截器
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())// 使用GSON，将响应的JSON格式数据转换
            .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)
    inline fun <reified T> create(): T = create(T::class.java)

}