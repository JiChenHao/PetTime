package com.jichenhao.pettime_jichenhao.model.response

import com.jichenhao.pettime_jichenhao.model.entity.Pet
import com.jichenhao.pettime_jichenhao.model.entity.PetCuisine
import com.jichenhao.pettime_jichenhao.model.entity.PictureInfo
import com.jichenhao.pettime_jichenhao.model.entity.StsResult
import com.jichenhao.pettime_jichenhao.model.entity.UserInfo
import com.jichenhao.pettime_jichenhao.model.entity.UserWithJwtToken

/*
* sealed class Result 定义了一个密封类。密封类是一种特殊的抽象类，其所有子类都必须在同一个文件中声明。
* 这种设计使得编译器可以知道 Result 类的所有可能子类，便于进行类型检查和模式匹配。
* */
sealed class GenericResponse<out T> {
    abstract val success: Boolean   // success（连接是否成功）
    abstract val data: T            // data（返回的数据
    abstract val message: String    // message（本次连接的运行结果）
}

// 使用泛型响应类创建具体的响应子类
data class StsResponse(
    override val success: Boolean,
    override val data: StsResult,
    override val message: String
) : GenericResponse<StsResult>()

data class LoginResponse(
    override val success: Boolean,
    override val data: UserWithJwtToken?,// 如果不是null就是登录成功，里面包含了用户头像数据，以及包含身份凭证的jwtToken
    override val message: String
) : GenericResponse<UserWithJwtToken?>()

data class PetResponse(
    override val success: Boolean,
    override val data: List<Pet>,
    override val message: String
) : GenericResponse<List<Pet>>()

data class PetCuisineResponse(
    override val success: Boolean,
    override val data: List<PetCuisine>,
    override val message: String
) : GenericResponse<List<PetCuisine>>()

data class UserResponse(
    override val success: Boolean,
    override val data: UserInfo,
    override val message: String
) : GenericResponse<UserInfo>()

data class AlbumResponse(
    // 查询数据库中album的结果，包含一个相片列表
    override val success: Boolean,
    override val data: List<PictureInfo>,
    override val message: String
) : GenericResponse<List<PictureInfo>>()

data class OperationResponse(
    // 常规对与数据库的操作，要使用这个类接收后端返回的操作结果
    override val success: Boolean,
    override val data: String?,// 操作结果通常不携带数据，所以可以是null，前端也不会读取这个结果
    override val message: String
) : GenericResponse<String?>()