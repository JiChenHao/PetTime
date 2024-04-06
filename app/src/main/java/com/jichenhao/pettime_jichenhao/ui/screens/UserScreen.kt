package com.jichenhao.pettime_jichenhao.ui.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jichenhao.pettime_jichenhao.R
import com.jichenhao.pettime_jichenhao.ui.composableComponents.PetTimeBottomNavigationBar
import com.jichenhao.pettime_jichenhao.ui.composableComponents.UserInfoCard
import com.jichenhao.pettime_jichenhao.viewModel.UserViewModel

@Composable
fun UserScreen(
    viewModel: UserViewModel,
    onNavigateToMainScreen: () -> Unit,
    onNavigateToKnowledgeScreen: () -> Unit,
    onNavigateToPetScreen: () -> Unit,
    onNavigateToUserScreen: () -> Unit,
    onNavigateToPictureUpload: () -> Unit,
    onNavigatePopBackStack: () -> Unit,
    onNavigateToLoginScreen: () -> Unit
) {
    val context = LocalContext.current as ComponentActivity
    val userLoggedIn by viewModel.loggedInUser.collectAsState()

    Scaffold(
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 背景图片
            Image(
                painter = painterResource(id = R.drawable.background_main),
                contentScale = ContentScale.Crop,// 这可以确保图片适应指定大小时不拉伸变形
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
            )
            Column(
                //本列垂直居中
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UserInfoCard(userLoggedIn)
                Button(onClick = {
                    viewModel.logout(context)
                    //跳转到Login
                    onNavigatePopBackStack()
                    onNavigateToLoginScreen()
                }) {
                    Text(text = "点击退出登录")
                }

                Button(onClick = {
                    onNavigateToPictureUpload()
                }) {
                    Text(text = "点击更换头像")
                }
            }
        }
    }


}