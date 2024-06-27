package com.jichenhao.pettime_jichenhao.ui.screens

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.jichenhao.pettime_jichenhao.R
import com.jichenhao.pettime_jichenhao.model.network.oss.AliOssUtils
import com.jichenhao.pettime_jichenhao.viewModel.PetCuisineViewModel
import com.jichenhao.pettime_jichenhao.viewModel.PetViewModel
import com.jichenhao.pettime_jichenhao.viewModel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun SplashScreen(
    viewModel: UserViewModel,
    petCuisineViewModel: PetCuisineViewModel,
    onNavigateToMainScreen: () -> Unit,
    onNavigatePopBackStack: () -> Unit,
    onNavigateToLoginScreen: () -> Unit
) {
    val context = LocalContext.current as ComponentActivity
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Splash Screen内容
        Image(
            painter = painterResource(id = R.drawable.splash_screen_background),
            contentDescription = "SplashBackground",
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.FillWidth
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "吉晨豪",
                style = TextStyle(fontSize = 30.sp, color = Color.Black)
            )
            Text(
                text = "宠物时光管理",
                style = TextStyle(fontSize = 30.sp, color = Color.Black)
            )
        }
    }
    // 在指定延迟时间后调用 onFinish 回调
    LaunchedEffect(Unit) {
        delay(3000)
        // 检查用户是否已登录
        val isLoggedIn = withContext(Dispatchers.IO) { viewModel.loadSavedIfUserLoggedIn(context) }

        if (isLoggedIn) {
            // 如果已经登录，初始化ViewModel中的已登录用户信息
            viewModel.loadSavedLoggedInUserEmail(context)
            Log.d("SplashScreen","已经登录,初始化的内容中email是${viewModel.loggedInUser.value.email},profile是${viewModel.loggedInUser.value.profile}")
            viewModel.getUserProfile(viewModel.loggedInUser.value.email)
            // 加载宠物食谱信息,需要在加载jwtToken之后
            petCuisineViewModel.getAllPetCuisineList()
            withContext(Dispatchers.Main) {
                // 在主线程执行UI操作
                onNavigatePopBackStack() // 清空返回栈
                onNavigateToMainScreen()
            }
        } else {
            withContext(Dispatchers.Main) {
                // 在主线程执行UI操作
                onNavigatePopBackStack() // 清空返回栈
                onNavigateToMainScreen()
            }
        }
    }
}