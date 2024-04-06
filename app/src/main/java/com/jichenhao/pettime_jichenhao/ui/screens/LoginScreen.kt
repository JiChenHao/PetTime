package com.jichenhao.pettime_jichenhao.ui.screens

import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DisabledVisible
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jichenhao.pettime_jichenhao.viewModel.UserViewModel

@Composable
fun LoginScreen(
    viewModel: UserViewModel,
    onNavigateToMainScreen: () -> Unit,
    onNavigateToRegisterScreen: () -> Unit,
    onNavigatePopBackStack: () -> Unit
) {
    val context = LocalContext.current as ComponentActivity
    //输入框的变量
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    //是否显示密码
    var showPassword by remember { mutableStateOf(false) }
    //用来判断用户是否开始输入，防止无限重复的读取填充缓存中的email和密码
    var isManuallyEditing by rememberSaveable { mutableStateOf(false) }
    //从login_prefs中读取的上次是否选择了记住密码的选项
    val loadedPrefsInfo = viewModel.loadSavedLoginInfo(context = context)
    //是否记住密码的checkBox的变量
    var rememberMe by rememberSaveable { mutableStateOf(loadedPrefsInfo.rememberMeLoaded) }

    // 监听ViewModel中showDialog的变化，一旦变为true，说明有消息要显示，提醒Dialog
    val showDialog by viewModel.showDialog.collectAsState(initial = false)
    val message by viewModel.message.collectAsState()
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                viewModel.unShowDialog()
            }, // 关闭对话框
            title = { Text("注意") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.unShowDialog()
                }) {
                    Text("确定")
                }
            }
        )
    }

    //读取本地缓存，如果存在被记住的密码，就自动填充，用户一旦点击了输入框开始输入，就不再自动填充了
    if (!isManuallyEditing && loadedPrefsInfo.rememberMeLoaded) {
        email = loadedPrefsInfo.emailLoaded.toString()
        password = loadedPrefsInfo.passwordLoaded.toString()
        Log.d("我的登录", "密码被自动填充为：${email},${password}")
    }

    // ====START监听
    //监听ViewModel中loginResult的变化，一旦变为true，说明登录成功，就进行页面跳转动作
    val loginResult by viewModel.loginResult.collectAsState(initial = false)
    LaunchedEffect(key1 = loginResult) { // key1用于监听loginResult的变化
        if (loginResult) {
            //登陆成功就根据用户的选择去保存密码
            viewModel.saveCredentialsIfNeeded(context, email, password, rememberMe)
            // 跳转之前清空返回栈，使得用户无法使用返回键重新回到login页面，只能通过logout按钮
            onNavigatePopBackStack()
            onNavigateToMainScreen()
        }
    }
    // ====END监听

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Email 输入框
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                isManuallyEditing = true
            },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .focusable(true),
            maxLines = 1,
        )

        // 密码输入框
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                isManuallyEditing = true
            },
            label = { Text("Password") },
            maxLines = 1,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.RemoveRedEye else Icons.Filled.DisabledVisible,
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusable(true)
        )

        // 记住密码选项
        Row {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it },
                modifier = Modifier.align(Alignment.CenterVertically),
            )
            Spacer(modifier = Modifier.systemBarsPadding())
            Text("Remember Me")
        }

        // 登录按钮
        Button(
            onClick = {
                Log.d("我的登录", "LoginButtonDown")
                viewModel.login(email, password)
                //订阅ViewModel中的loginResult，一旦登陆成功这边就可以处理UI界面
            }
        ) {
            Text("登录")
        }
        // 注册按钮
        Button(
            onClick = {
                onNavigateToRegisterScreen()
            }
        ) {
            Text("注册")
        }
    }

}