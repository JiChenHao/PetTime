package com.jichenhao.pettime_jichenhao.ui.screens

import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DisabledVisible
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.jichenhao.pettime_jichenhao.viewModel.UserViewModel

@Composable
fun RegisterScreen(
    viewModel: UserViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current as ComponentActivity
    //输入框的变量
    var email by rememberSaveable { mutableStateOf("") }
    var password1 by rememberSaveable { mutableStateOf("") }
    var password2 by rememberSaveable { mutableStateOf("") }
    //是否显示密码
    var showPassword by remember { mutableStateOf(false) }

    // ====START监听
    // 监听ViewModel中showDialog的变化，一旦变为true，说明密码错误，提醒Dialog
    val showDialog by viewModel.showDialog.collectAsState(initial = false)
    // 监听后台异步操作结果，当后台操作有了结果之后，对话框才能关闭，控制对话框关闭按钮的显示
    val message by viewModel.message.collectAsState()
    // 监听ViewModel中showDialog的变化，一旦变为true，说明密码错误，提醒Dialog
    val showRegisterSuccessDialog by viewModel.showRegisterSuccessDialog.collectAsState(initial = false)
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

    if (showRegisterSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
            },
            title = { Text("操作结果") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.unShowRegisterDialog()
                    onNavigateBack()
                }) {
                    Text("点击返回登录界面")
                }
            }
        )
    }


    // ====END监听
    Scaffold(
        topBar = {
            // 返回键 、 删除键
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp), // 添加内边距为10dp,
                horizontalArrangement = Arrangement.SpaceBetween,// 下面的两个组件位于一行的两侧
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        onNavigateBack()
                    },
                    colors = IconButtonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Black,
                        disabledContentColor = Color.White
                    )
                ) {
                    Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "")
                }
            }
        }

    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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
                    },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusable(true),
                    maxLines = 1,
                )

                // 密码输入框
                OutlinedTextField(
                    value = password1,
                    onValueChange = {
                        password1 = it
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

                // 密码确认框
                OutlinedTextField(
                    value = password2,
                    onValueChange = {
                        password2 = it
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

                // 注册按钮
                Button(
                    onClick = {
                        Log.d("注册", "LoginButtonDown")
                        viewModel.register(email, password1, password2)
                        //订阅ViewModel中的loginResult，一旦登陆成功这边就可以处理UI界面
                    }
                ) {
                    Text("注册")
                }
            }
        }

    }

}