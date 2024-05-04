package com.jichenhao.pettime_jichenhao.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.jichenhao.pettime_jichenhao.model.entity.Credentials
import com.jichenhao.pettime_jichenhao.model.entity.Pet
import com.jichenhao.pettime_jichenhao.model.entity.PictureInfo
import com.jichenhao.pettime_jichenhao.model.entity.UserInfo
import com.jichenhao.pettime_jichenhao.model.entity.UserLoggedIn
import com.jichenhao.pettime_jichenhao.model.network.PetTimeNetwork
import com.jichenhao.pettime_jichenhao.model.response.StsResponse
import kotlinx.coroutines.Dispatchers
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class PetTimeRepositoryImpl @Inject constructor(
    private val petTimeNetwork: PetTimeNetwork
) : PetTimeRepository {

    //fire是一个按照liveData()函数参数接收标准定义的一个高阶函数，在fire()内部线调用一下liveData()函数，
    //然后在liveData函数代码块中统一进行了try catch处理，并将结果使用emit方法发射出去，从而避免了每一个调用都要
    //用一次try catch的繁琐
    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }

    // sts
    override fun fetchStsToken(): LiveData<Result<Credentials>> = liveData(Dispatchers.IO) {
        val result = try {
            //获取网络请求的结果
            Log.d("STSToken——Repository", "启动fetch")
            val stsResponse: StsResponse = petTimeNetwork.fetchStsToken()
            Log.d("STSToken——Repository", "stsResponse.success是：${stsResponse.success}")
            if (stsResponse.success) {//如果网络没有问题
                val stsResult = stsResponse.data
                Log.d("STSToken——UserViewModel", "网络正常，结果是：${stsResult.credentials}")
                Result.success(stsResult.credentials)// token就在其中
            } else {
                Result.failure(IOException(stsResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(IOException("网络错误。"))
        }
        //emit实际上是类似于调用LiveData的setValue方法通知数据变化
        emit(result)
    }

    /**
     * @return UserWithJwtToken带有用户凭证
     * */
    override fun login(userInfo: UserInfo) = fire(Dispatchers.IO) {
        val loginResponse = petTimeNetwork.login(userInfo)
        if (loginResponse.success && loginResponse.data != null) {//如果网络没有问题
            val loginResultData = loginResponse.data// 返回的信息，如果不是null，就说明登录成功了
            Result.success(loginResultData)
        } else {
            Result.failure(IOException("Error WrongPassword "))
        }
    }

    override fun addUser(userInfo: UserInfo) = fire(Dispatchers.IO) {
        Log.d("Repository层", "registerResponse开始")
        val registerResponse = petTimeNetwork.addUser(userInfo)
        if (registerResponse.success) {
            Log.d("Repository层", "registerResponse.success且message为${registerResponse.message}")
            Result.success(registerResponse.message)
        } else {
            Result.failure(IOException(registerResponse.message))
        }
    }

    override fun getUserByEmail(email: String) = fire(Dispatchers.IO) {
        Log.d("Repository层", "getUserByEmail")
        val userResponse = petTimeNetwork.getUserByEmail(email)
        if (userResponse.success) {
            Log.d("Repository层", "getUserByEmail_data${userResponse.data}")
            Result.success(userResponse.data)
        } else {
            Result.failure(IOException(userResponse.message))
        }
    }

    override fun getUserProfile(email: String) = fire(Dispatchers.IO) {
        val operationResponse = petTimeNetwork.getUserProfile(email)
        if (operationResponse.success) {
            Result.success(operationResponse.data)
        } else {
            Result.failure(IOException(operationResponse.message))
        }
    }

    override fun updateUserProfile(userLoggedIn: UserLoggedIn) = fire(Dispatchers.IO) {
        val operationResponse = petTimeNetwork.updateUserProfile(userLoggedIn)
        if (operationResponse.success) {
            Result.success(operationResponse.message)
        } else {
            Result.failure(IOException(operationResponse.message))
        }
    }

    // pet
    override fun getPetListByUserEmail(email: String) = fire(Dispatchers.IO) {
        val petResponse = petTimeNetwork.getPetListByUserEmail(email)
        if (petResponse.success) {
            Result.success(petResponse.data)
        } else {
            Result.failure(IOException(petResponse.message))
        }
    }

    override fun updatePet(petInfo: Pet) = fire(Dispatchers.IO) {
        val operationResponse = petTimeNetwork.updatePet(petInfo)
        if (operationResponse.success) {
            Result.success(operationResponse.message)
        } else {
            Result.failure(IOException(operationResponse.message))
        }
    }

    override fun deletePet(petId: Int) = fire(Dispatchers.IO) {
        val operationResponse = petTimeNetwork.deletePet(petId)
        if (operationResponse.success) {
            Result.success(operationResponse.message)
        } else {
            Result.failure(IOException(operationResponse.message))
        }
    }

    override fun addPetInfoToDB(petInfo: Pet) = fire(Dispatchers.IO) {
        val operationResponse = petTimeNetwork.addPetInfoToDB(petInfo)
        if (operationResponse.success) {
            Result.success(operationResponse.message)
        } else {
            Result.failure(IOException(operationResponse.message))
        }
    }

    // PetCuisine
    override fun getAllPetCuisineList() = fire(Dispatchers.IO) {
        val petCuisineResponse = petTimeNetwork.getAllPetCuisineList()
        if (petCuisineResponse.success) {
            Result.success(petCuisineResponse.data)
        } else {
            Result.failure(IOException(petCuisineResponse.message))
        }
    }

    // Album
    override fun getAlbumByEmail(email: String) = fire(Dispatchers.IO) {
        val albumResponse = petTimeNetwork.getAlbumByEmail(email)
        if (albumResponse.success) {
            Result.success(albumResponse.data)
        } else {
            Result.failure(IOException(albumResponse.message))
        }
    }

    override fun addPicture(pictureInfo: PictureInfo) = fire(Dispatchers.IO) {
        val operationResponse = petTimeNetwork.addPicture(pictureInfo)
        if (operationResponse.success) {
            Result.success(operationResponse.message)
        } else {
            Result.failure(IOException(operationResponse.message))
        }
    }

    override fun deletePicture(picId: Int) = fire(Dispatchers.IO) {
        val operationResponse = petTimeNetwork.deletePicture(picId)
        if (operationResponse.success) {
            Result.success(operationResponse.message)
        } else {
            Result.failure(IOException(operationResponse.message))
        }
    }

}