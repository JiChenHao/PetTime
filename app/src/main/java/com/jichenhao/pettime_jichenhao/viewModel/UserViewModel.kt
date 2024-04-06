package com.jichenhao.pettime_jichenhao.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.jichenhao.pettime_jichenhao.model.PetTimeRepository
import com.jichenhao.pettime_jichenhao.model.entity.UserInfo
import com.jichenhao.pettime_jichenhao.model.entity.UserLoggedIn
import com.jichenhao.pettime_jichenhao.model.network.oss.AliOssUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class UserViewModel @Inject constructor(private val petTimeRepository: PetTimeRepository) :
    ViewModel() {
    //登陆结果，UI界面通过观察这个值更新UI界面，处理登陆结果
    private var _loginResult = MutableStateFlow<Boolean>(false)
    val loginResult: StateFlow<Boolean> get() = _loginResult

    // 全局可观察的已登录用户的信息（email以及profile）
    private var _loggedInUser = MutableStateFlow<UserLoggedIn>(UserLoggedIn("", null))
    val loggedInUser: StateFlow<UserLoggedIn> get() = _loggedInUser

    //用来控制显示Dialog
    private var _showDialog = MutableStateFlow<Boolean>(false)
    val showDialog: StateFlow<Boolean> get() = _showDialog

    // 提示注册成功的对话框
    private var _showRegisterSuccessDialog = MutableStateFlow<Boolean>(false)
    val showRegisterSuccessDialog: StateFlow<Boolean> get() = _showRegisterSuccessDialog

    var message = MutableStateFlow<String>("加载中....")

    private fun showDialog() {
        _showDialog.value = true
    }

    fun unShowDialog() {
        _showDialog.value = false
    }

    private fun showRegisterDialog() {
        _showRegisterSuccessDialog.value = true
    }

    fun unShowRegisterDialog() {
        _showRegisterSuccessDialog.value = false
    }

    // 提供一个方法来fetchToken，每次进入Splash页面就执行一次
    fun fetchStsToken() {
        viewModelScope.launch {
            Log.d("STSToken——UserViewModel", "启动fetch")
            val credentials = petTimeRepository.fetchStsToken()
            credentials.asFlow().collect { result ->
                if (result.isSuccess && result.getOrNull() != null) {
                    AliOssUtils.setStsToken(result.getOrNull()!!)
                } else {
                    // TODO 没拿到stsToken的情况
                }
            }
        }
    }

    fun login(email: String, password: String) {
        Log.d("我的登录", "ViewModel调用")
        val userInfo = UserInfo(email, password, null)
        viewModelScope.launch {
            Log.d("我的登录", "viewModelScope协程执行")
            val loginLiveData = petTimeRepository.login(userInfo)
            // 观察LiveData并处理结果
            loginLiveData.asFlow().collect { result ->
                if (result.isSuccess) {
                    Log.d("我的登录", "viewModelScope协程中的Success")
                    _loginResult.value = true
                    val userLoggedIn =
                        UserLoggedIn(result.getOrNull()!!.email, result.getOrNull()!!.profile)
                    _loggedInUser.value = userLoggedIn
                } else if (result.isFailure) {
                    Log.d("我的登录", "viewModelScope协程中的Failure")
                    _loginResult.value = false
                    message.value = "登录失败，请检查网络和密码，稍后再试。"
                    showDialog()
                }
            }
        }
    }

    fun register(email: String, password1: String, password2: String) {
        if (password1 == password2) {
            viewModelScope.launch {
                val ifUserHaveRegistered = petTimeRepository.getUserByEmail(email)
                ifUserHaveRegistered.asFlow().collect { result ->
                    if (result.isSuccess) {
                        if (result.getOrNull() != null) {// 邮箱已经被注册
                            message.value = "注册失败，此邮箱已被注册。"
                            showDialog()
                        } else {
                            Log.d("ViewModel", "进入else准备addUser")
                            addUser(email, password1)
                        }
                    }
                }
            }
        } else {
            message.value = "两次密码不一样，请重新确认。"
            showDialog()
        }
    }

    private fun addUser(email: String, password: String) {
        val userInfo = UserInfo(email, password, null)
        viewModelScope.launch {
            val operationResponse = petTimeRepository.addUser(userInfo)
            operationResponse.asFlow().collect { result ->
                if (result.isSuccess) {
                    message.value = "注册成功，点击确定返回登录界面。"
                    showRegisterDialog()
                } else if (result.isFailure) {
                    message.value = "注册失败，请检查网络连接，稍后重试。"
                    showDialog()
                }
            }
        }
    }

    // 获取/更新头像数据
    fun getUserProfile(email: String) {
        viewModelScope.launch {
            val response = petTimeRepository.getUserProfile(email)
            response.asFlow().collect { result ->
                if (result.isSuccess) {
                    _loggedInUser.value = UserLoggedIn(email, result.getOrNull())
                    Log.d("读取已经登录的用户profile：", "${_loggedInUser.value.profile}")
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
                        updateUserProfile(url)
                        // 清理临时文件（此处假设AliOssUtils.uploadFile是异步操作，所以应该在回调内清理）
                        tempFile.delete()
                        message.value = "上传成功！"
                        showDialog()
                    } else {
                        // 上传失败
                        message.value = "上传失败！"
                        showDialog()
                    }
                }
            }
        } else {
            // 没有选择图片时给出提示
            Toast.makeText(context, "请先选择或拍摄一张图片", Toast.LENGTH_SHORT).show()
        }
    }

    // 更新头像数据
    fun updateUserProfile(url: String) {
        val userLoggedIn = UserLoggedIn(loggedInUser.value.email, url)
        viewModelScope.launch {
            val response = petTimeRepository.updateUserProfile(userLoggedIn)
            response.asFlow().collect { result ->
                if (result.isSuccess) {
                    message.value = "头像更新成功！"
                    showDialog()
                } else {
                    message.value = "更新头像失败，请检查网络，稍后再试。"
                    showDialog()
                }
            }
        }
    }

    // 登出
    fun logout(context: Context) {
        _loggedInUser.value = UserLoggedIn("", null)
        _loginResult.value = false
        // 如果选择logout，就要清除内存和全局的登录状态
        val preferences =
            context.getSharedPreferences("login_state_prefs", Context.MODE_PRIVATE)
        with(preferences.edit()) {
            remove("email")
            putBoolean("isUserLoggedIn", false)//更改登陆状态
            apply()
        }
        // 跳转操作在UI层完成
    }

    // ===关于记住密码、记住登录状态的内容===
    data class LoadedPrefsInfo(
        val rememberMeLoaded: Boolean,
        val emailLoaded: String?,
        val passwordLoaded: String?
    )

    // 加载保存的凭证
    fun loadSavedLoginInfo(context: Context): LoadedPrefsInfo {
        val preferences = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val rememberMeLoaded = preferences.getBoolean("rememberMe", false)
        val emailLoad = preferences.getString("email", "NOT FOUND")
        val passwordLoaded = preferences.getString("password", "NOT FOUND")

        return LoadedPrefsInfo(rememberMeLoaded, emailLoad, passwordLoaded)
    }

    // 根据用户的选择，选择是否进行保存密码
    fun saveCredentialsIfNeeded(
        context: Context,
        email: String,
        password: String,
        rememberMe: Boolean
    ) {
        val preferences = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        if (rememberMe) {
            with(preferences.edit()) {
                putString("email", email)
                putString("password", password)
                putBoolean("rememberMe", true)//将是否记住我也写进缓存
                apply()
            }
        } else {
            //清空缓存内容
            // 如果不勾选“记住密码”，则清除之前存储的信息
            with(preferences.edit()) {
                remove("email")
                remove("password")
                putBoolean("rememberMe", false)//将是否记住我也写进缓存
                apply()
            }
        }
        //只要登陆成功就记住登录状态
        val preferences_state =
            context.getSharedPreferences("login_state_prefs", Context.MODE_PRIVATE)
        with(preferences_state.edit()) {
            putString("email", email)
            putBoolean("isUserLoggedIn", true)//记住登陆状态
            apply()
        }
    }

    //默认一次登录之后，如果不主动退出就不会退出
    // Splash页面读取本地登录信息
    //读取用户登录状态用于保持APP登录状态
    fun loadSavedIfUserLoggedIn(context: Context): Boolean {
        val preferences = context.getSharedPreferences("login_state_prefs", Context.MODE_PRIVATE)
        return preferences.getBoolean("isUserLoggedIn", false)
    }

    //读取已经登录用户的用户名
    fun loadSavedLoggedInUserEmail(context: Context) {
        val preferences = context.getSharedPreferences("login_state_prefs", Context.MODE_PRIVATE)
        val email = preferences.getString("email", "NOT FOUND")
        // 更新用户email以及头像数据
        _loggedInUser.value = UserLoggedIn(email!!, null)
        // 去数据库中查看是否有头像数据，如果有，更新头像数据
        getUserProfile(email)
    }
}


