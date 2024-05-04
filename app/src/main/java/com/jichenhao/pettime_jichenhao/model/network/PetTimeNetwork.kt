package com.jichenhao.pettime_jichenhao.model.network

import android.util.Log
import com.jichenhao.pettime_jichenhao.model.entity.Pet
import com.jichenhao.pettime_jichenhao.model.entity.PictureInfo
import com.jichenhao.pettime_jichenhao.model.entity.UserInfo
import com.jichenhao.pettime_jichenhao.model.entity.UserLoggedIn
import com.jichenhao.pettime_jichenhao.model.network.service.AlbumService
import com.jichenhao.pettime_jichenhao.model.network.service.PetCuisineService
import com.jichenhao.pettime_jichenhao.model.network.service.PetService
import com.jichenhao.pettime_jichenhao.model.network.service.StsApi
import com.jichenhao.pettime_jichenhao.model.network.service.UserService
import com.jichenhao.pettime_jichenhao.model.response.LoginResponse
import com.jichenhao.pettime_jichenhao.model.response.OperationResponse
import com.jichenhao.pettime_jichenhao.model.response.StsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.RuntimeException
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
// 统一的网络数据源访问入口，对所有网络请求的API进行封装
object PetTimeNetwork {
    private val userService = ServiceCreator.create(UserService::class.java)
    private val petService = ServiceCreator.create(PetService::class.java)
    private val petCuisineService = ServiceCreator.create(PetCuisineService::class.java)
    private val AlbumService = ServiceCreator.create(AlbumService::class.java)
    private val StsApi = ServiceCreator.create(StsApi::class.java)

    // ===stsApi===
    suspend fun fetchStsToken(): StsResponse = StsApi.getStsToken().await()

    //===User相关===
    /**
     * 登录
     * @param userInfo用户登录信息
     * @return loginResponse用户登录结果，成功则包含用户头像和用户凭证
     * */
    suspend fun login(userInfo: UserInfo): LoginResponse = userService.login(userInfo).await()

    // 注册之前先看看有没有重合的用户
    suspend fun getUserByEmail(email: String) = userService.getUserByEmail(email).await()

    suspend fun addUser(userInfo: UserInfo) = userService.addUser(userInfo).await()

    // 根据用户email拉取头像信息
    suspend fun getUserProfile(email: String):
            OperationResponse {
        Log.d("NetWork中的getUserProfile启动", "开始向userService请求调用")
        val response = userService.getUserProfile(email).await()
        Log.d("NetWork中的getUserProfile结果：", response.message)
        return response
    }

    // 更新用户头像
    suspend fun updateUserProfile(userLoggedIn: UserLoggedIn) =
        userService.updateUserProfile(UserInfo(userLoggedIn.email, "", userLoggedIn.profile))
            .await()

    //===Pet相关===
    // 根据已登录用户的email去拉取用户的宠物数据
    suspend fun getPetListByUserEmail(email: String) =
        petService.getPetListByUserEmail(email).await()

    // 添加宠物到数据库
    suspend fun addPetInfoToDB(petInfo: Pet) = petService.addPetInfoToDB(petInfo).await()

    // 更新宠物到数据库
    suspend fun updatePet(petInfo: Pet) = petService.updatePet(petInfo).await()

    // 删除宠物
    suspend fun deletePet(petId: Int) = petService.deletePet(petId).await()

    //===PetCuisine相关===
    // 从数据库中拉取宠物食谱
    suspend fun getAllPetCuisineList() = petCuisineService.getAllPetCuisineList().await()

    //===Album相关===
    // 根据email从数据库中拉取宠物图片列表
    suspend fun getAlbumByEmail(email: String) = AlbumService.getAlbumByEmail(email).await()

    // 添加一条图片信息,返回一个操作结果
    suspend fun addPicture(pictureInfo: PictureInfo) = AlbumService.addPicture(pictureInfo).await()

    // 删除一个图片信息，返回一个操作结果
    suspend fun deletePicture(picId: Int) = AlbumService.deletePicture(picId).await()

    //使用协程技术实现的Retrofit回调的简化写法，
    //这样，当外部调用这个函数的时候，Retrofit就会立即发起网络请求，同时当前的协程也会被阻塞
    //协程函数，
    private suspend fun <T> Call<T>.await(): T {
        Log.d("PetTime", "NetWork层suspend .await()函数执行")
        return suspendCoroutine { continuation ->
            Log.d("NetWork", "NetWork层suspend .await()函数执行，准备进入enqueue")
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    Log.d("PetTime", "NetWork层suspend .await()函数执行，enqueue的Response")
                    if (response.isSuccessful) {
                        Log.d("PetTime", "NetWork层suspend .await()函数执行，且网络连接正常")
                        //这里得到了返回的数据内容
                        val body = response.body()
                        if (body != null) continuation.resume(body)
                        else {
                            Log.d(
                                "PetTime",
                                "NetWork层suspend .await()函数执行，且response.body为null"
                            )
                            continuation.resumeWithException(RuntimeException("Received successful response but response body is null"))
                        }
                    } else {
                        Log.d(
                            "PetTime",
                            "NetWork层suspend .await()函数执行，且Server responded with error${response.code()}."
                        )
                        continuation.resumeWithException(IOException("Unexpected code ${response.code()}. Server responded with error."))
                    }
                }
                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}