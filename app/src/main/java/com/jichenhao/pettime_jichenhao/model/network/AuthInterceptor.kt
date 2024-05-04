package com.jichenhao.pettime_jichenhao.model.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Retrofit

/**
 * 拦截请求并在请求上面添加header信息（身份凭证）
 * @param jwtToken身份凭证
 */
// 定义一个名为AuthInterceptor的拦截器类，它继承自OkHttp3中的Interceptor接口
// 这个拦截器的作用是在每个HTTP请求发出之前动态地向请求头中添加JWT令牌，以便进行后端的身份验证
class AuthInterceptor(private val getToken: () -> String) : Interceptor {

    // 重写Interceptor接口中的intercept方法
    // 这个方法会在每个请求被发送之前调用，允许我们修改请求或者观察/控制响应
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = getToken() // 动态获取jwtToken
        // 获取原始的Request对象，这是未经修改的即将发出的请求
        val originalRequest = chain.request()

        // 创建一个新的Request.Builder对象，它可以用来构建一个新的请求，基于原始请求
        val requestBuilder = originalRequest.newBuilder()
        Log.d("jwtTOKEN输出","$token")
        // 检查是否有JWT Token存在（即token是否非空且非空字符串）
        if (!token.isNullOrEmpty()) {
            // 若有Token，则将其添加到请求头的"Authorization"字段中，JWT通常采用Bearer方案
            // Bearer后面跟着的是Token的具体值
            Log.d("jwtTOKEN添加到header","$token")
            requestBuilder.header("Authorization", "Bearer $token")
        }

        // 使用新的请求构建器来创建并发出一个已添加了Token的请求
        // proceed方法会继续执行请求，并返回响应
        return chain.proceed(requestBuilder.build())
    }
}