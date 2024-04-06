package com.jichenhao.pettime_jichenhao.model

import androidx.lifecycle.LiveData
import com.jichenhao.pettime_jichenhao.model.entity.Credentials
import com.jichenhao.pettime_jichenhao.model.entity.Pet
import com.jichenhao.pettime_jichenhao.model.entity.PetCuisine
import com.jichenhao.pettime_jichenhao.model.entity.PictureInfo
import com.jichenhao.pettime_jichenhao.model.entity.UserInfo
import com.jichenhao.pettime_jichenhao.model.entity.UserLoggedIn

interface PetTimeRepository {
    // 返回一个LiveData对象，其中包含登录操作的结果
    // stsApi
    fun fetchStsToken(): LiveData<Result<Credentials>>

    // user
    fun login(userInfo: UserInfo): LiveData<Result<UserInfo>>
    fun addUser(userInfo: UserInfo): LiveData<Result<String>>
    fun getUserByEmail(email: String): LiveData<Result<UserInfo>>
    fun getUserProfile(email: String): LiveData<Result<String?>>
    fun updateUserProfile(userLoggedIn: UserLoggedIn): LiveData<Result<String>>

    // pet
    fun getPetListByUserEmail(email: String): LiveData<Result<List<Pet>>>
    fun updatePet(petInfo: Pet): LiveData<Result<String>>
    fun deletePet(petId: Int): LiveData<Result<String>>
    fun addPetInfoToDB(petInfo: Pet): LiveData<Result<String>>

    // petCuisine
    fun getAllPetCuisineList(): LiveData<Result<List<PetCuisine>>>

    // album
    fun getAlbumByEmail(email: String): LiveData<Result<List<PictureInfo>>>
    fun addPicture(pictureInfo: PictureInfo): LiveData<Result<String>>
    fun deletePicture(picId: Int): LiveData<Result<String>>
}