package com.jichenhao.pettime_jichenhao.model.network.service

import com.jichenhao.pettime_jichenhao.model.entity.UserInfo
import com.jichenhao.pettime_jichenhao.model.response.LoginResponse
import com.jichenhao.pettime_jichenhao.model.response.OperationResponse
import com.jichenhao.pettime_jichenhao.model.response.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

//这个类负责向API发起调用请求并获得返回的JSON数据
interface UserService {
    //使用@GET注解，当调用这个方法的时候，Retrofit就会发起一条GET请求，去访问@GET注解中配置的地址
    @POST("login")
    //方法的返回值被声明成了Call<UserInfo>，这样Retrofit就会将服务器返回的JSON数据自动解析成LoginResponse对象
    fun login(@Body user: UserInfo): Call<LoginResponse>

    @GET("getUserByEmail")
    fun getUserByEmail(@Query("email") email: String): Call<UserResponse>

    //
    // 根据已登录的用户的email拉取头像信息，返回一个结果类，其中的data属性就是我们要的结果
    @GET("getUserProfile")
    fun getUserProfile(@Query("email") email: String): Call<OperationResponse>

    // 更新用户头像，传入的内容应该没有密码
    @POST("updateUserProfile")
    // 后台接受一个UserInfo对象，但是仅仅对email和profile进行识别和操作，因此传入的对象password字段为null即可
    fun updateUserProfile(@Body userLoggedIn: UserInfo): Call<OperationResponse>

    @POST("addUser")
    fun addUser(@Body user: UserInfo): Call<OperationResponse>
}