package com.jichenhao.pettime_jichenhao.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.jichenhao.pettime_jichenhao.model.PetTimeRepository
import com.jichenhao.pettime_jichenhao.model.entity.PictureInfo
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
class AlbumViewModel @Inject constructor(private val petTimeRepository: PetTimeRepository) :
    ViewModel() {
    private var _picList = MutableStateFlow<List<PictureInfo>>(emptyList())
    val picList: StateFlow<List<PictureInfo>> get() = _picList

    //用来控制显示Dialog
    private var _showDialog = MutableStateFlow<Boolean>(false)
    val showDialog: StateFlow<Boolean> get() = _showDialog

    var message = MutableStateFlow<String>("加载中....")

    // 图片上传到OSS后返回的URL
    private var _urlToOss = MutableStateFlow<String>("")
    val urlToOss: StateFlow<String> get() = _urlToOss

    private fun showDialog() {
        _showDialog.value = true
    }

    fun unShowDialog() {
        _showDialog.value = false
    }


    // 根据email获取用户宠物相册列表
    fun getAlbumByEmail(email: String) {
        viewModelScope.launch {
            val response = petTimeRepository.getAlbumByEmail(email)
            response.asFlow().collect { result ->
                if (result.isSuccess) {
                    _picList.value = result.getOrNull()!!
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
                    } else {
                        message.value = "上传图片到OSS失败，请检查网络连接。"
                        showDialog()
                    }
                }
            }
        } else {
            // 没有选择图片时给出提示
            Toast.makeText(context, "请先选择或拍摄一张图片", Toast.LENGTH_SHORT).show()
        }
    }

    fun addPicture(pictureInfo: PictureInfo) {
        viewModelScope.launch {
            val response = petTimeRepository.addPicture(pictureInfo)
            response.asFlow().collect { result ->
                if (result.isSuccess) {
                    message.value = "添加成功!"
                    showDialog()
                } else {
                    message.value = "添加失败，请检查网络，稍后再试。"
                    showDialog()
                }
            }
        }
    }

    fun deletePicture(picId: Int) {
        viewModelScope.launch {
            val response = petTimeRepository.deletePicture(picId)
            response.asFlow().collect { result ->
                if (result.isSuccess) {
                    message.value = "删除成功!"
                    showDialog()
                } else {
                    message.value = "删除失败，请检查网络，稍后再试。"
                    showDialog()
                }
            }
        }
    }
}