package com.jichenhao.pettime_jichenhao.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.jichenhao.pettime_jichenhao.model.PetTimeRepository
import com.jichenhao.pettime_jichenhao.model.entity.Pet
import com.jichenhao.pettime_jichenhao.model.entity.UserLoggedIn
import com.jichenhao.pettime_jichenhao.model.network.oss.AliOssUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

@HiltViewModel
class PetViewModel @Inject constructor(private val petTimeRepository: PetTimeRepository) :
    ViewModel() {
    // 宠物列表，按照已登录用户的email从数据库中拉取
    private var _petList = MutableStateFlow<List<Pet>>(emptyList())
    val petList: StateFlow<List<Pet>> get() = _petList

    // 宠物列表，按照已登录用户的email从数据库中拉取
    private var _petToUpdate = MutableStateFlow<Pet>(
        Pet(
            0,
            "", false,
            "", 0,
            "", null
        )
    )
    val petToUpdate: StateFlow<Pet> get() = _petToUpdate

    //用来控制显示Dialog
    private var _showDialog = MutableStateFlow<Boolean>(false)
    val showDialog: StateFlow<Boolean> get() = _showDialog

    // 图片上传到OSS后返回的URL
    private var _urlToOss = MutableStateFlow<String>("")
    val urlToOss: StateFlow<String> get() = _urlToOss

    var message = MutableStateFlow<String>("加载中....")

    private fun showDialog() {
        _showDialog.value = true
    }

    fun unShowDialog() {
        _showDialog.value = false
    }

    fun setPetToUpdate(pet: Pet) {
        _petToUpdate.value = pet
    }

    fun getPetListByUserEmail(email: String) {
        viewModelScope.launch {
            val petLiveData = petTimeRepository.getPetListByUserEmail(email)
            // 观察LiveData并处理结果
            petLiveData.asFlow().collect { result ->
                if (result.isSuccess) {
                    _petList.value = petLiveData.value!!.getOrNull()!!
                }
            }
        }
    }

    fun addPet(petInfo: Pet) {
        viewModelScope.launch {
            val response = petTimeRepository.addPetInfoToDB(petInfo)
            // 观察LiveData并处理结果
            response.asFlow().collect { result ->
                if (result.isSuccess) {
                    // 成功则刷新宠物列表
                    getPetListByUserEmail(petInfo.userEmail)
                    message.value = "添加成功"
                    showDialog()
                } else {
                    message.value = "添加失败，请检查网络，稍后再试。"
                    showDialog()
                }
            }
        }
    }

    fun updatePet(petInfo: Pet) {
        viewModelScope.launch {
            val response = petTimeRepository.updatePet(petInfo)
            // 观察LiveData并处理结果
            response.asFlow().collect { result ->
                if (result.isSuccess) {
                    // 成功则刷新宠物列表
                    getPetListByUserEmail(petInfo.userEmail)
                    message.value = "更新成功"
                    showDialog()
                } else {
                    message.value = "更新失败，请检查网络，稍后再试。"
                    showDialog()
                }
            }
        }
    }

    fun deletePet(petInfo: Pet) {
        viewModelScope.launch {
            val response = petTimeRepository.deletePet(petInfo.petId)
            // 观察LiveData并处理结果
            response.asFlow().collect { result ->
                if (result.isSuccess) {
                    // 成功则刷新宠物列表
                    getPetListByUserEmail(petInfo.userEmail)
                    message.value = "删除成功"
                    showDialog() // 删除成功后显示Dialog，点击确定返回
                } else {
                    message.value = "删除失败，请检查网络，稍后再试。"
                    showDialog()
                }
            }
        }
    }

    fun uploadImageToOSS(localImgPath: Uri, context: Context) {
        if (localImgPath != Uri.EMPTY) {
            val resolver = context.contentResolver
            val inputStream: InputStream? = resolver.openInputStream(localImgPath)
            // 创建临时文件并写入流
            val tempFile = File.createTempFile("temp_", ".jpg")
            val outputStream: OutputStream = FileOutputStream(tempFile).apply {
                inputStream?.use { input ->
                    input.copyTo(this)
                }
            }.also { it.close() }
            // 确保流关闭后，现在可以安全地上传文件
            if (tempFile.exists()) {
                // 利用回调函数得到url，然后继续将url传回数据库
                AliOssUtils.uploadFile(context, tempFile) { url ->
                    if (url != null) {
                        // 将url传给ViewModel，从而传回数据库
                        _urlToOss.value = url
                        // 清理临时文件（此处假设AliOssUtils.uploadFile是异步操作，所以应该在回调内清理）
                        tempFile.delete()
                        Log.d("宠物图片上传到Oss", "成功")
                    } else {
                        // 上传失败
                        Log.d("宠物图片上传到Oss", "失败")
                    }
                }
            }
        } else {
            // 没有选择图片时给出提示
            Toast.makeText(context, "请先选择或拍摄一张图片", Toast.LENGTH_SHORT).show()
        }
    }
}