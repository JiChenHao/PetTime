package com.jichenhao.pettime_jichenhao.ui.screens

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jichenhao.pettime_jichenhao.R
import com.jichenhao.pettime_jichenhao.ui.composableComponents.PetInfoCard
import com.jichenhao.pettime_jichenhao.ui.composableComponents.PetTimeBottomNavigationBar
import com.jichenhao.pettime_jichenhao.ui.composableComponents.UserInfoCard
import com.jichenhao.pettime_jichenhao.viewModel.PetViewModel
import com.jichenhao.pettime_jichenhao.viewModel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.Text

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PetScreen(
    userViewModel: UserViewModel,
    petViewModel: PetViewModel,
    onNavigateToMainScreen: () -> Unit,
    onNavigateToKnowledgeScreen: () -> Unit,
    onNavigateToPetScreen: () -> Unit,
    onNavigateToUserScreen: () -> Unit,
    onNavigateToPetAddScreen: () -> Unit,// 添加宠物页面
    onNavigateToPetUpdateScreen: () -> Unit// 用于点击卡片跳转到修改宠物信息页面
) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    onNavigateToPetAddScreen()
                },// 跳转到添加宠物页
                icon = { Icon(Icons.Filled.Add, "Extended floating action button.") },
                text = { Text(text = "添加新的宠物") },
            )
        }

    ) { innerPadding ->
        Log.d("PetScreen", "PetScreen被调用了")
        val context = LocalContext.current as ComponentActivity
        // 进入这个页面先刷新一下petList
        val loggedInUser by userViewModel.loggedInUser.collectAsState()
        petViewModel.getPetListByUserEmail(loggedInUser.email)
        val petList by petViewModel.petList.collectAsState()

        //记录下刷新状态
        var isRefreshing by remember {
            mutableStateOf(false)
        }
        // 刷新操作
        val scope = rememberCoroutineScope()   //协程模拟异步操作
        val state = rememberPullRefreshState(isRefreshing, onRefresh = {
            scope.launch {
                isRefreshing = true
                delay(1000)
                petViewModel.getPetListByUserEmail(loggedInUser.email)
                isRefreshing = false
            }
        })
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .pullRefresh(state)
        ) {
            // 背景图片
            Image(
                painter = painterResource(id = R.drawable.background_main),
                contentScale = ContentScale.Crop,// 这可以确保图片适应指定大小时不拉伸变形
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
            )

            if (petList.isNotEmpty()) {
                // 卡片列表
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(petList.size) { item ->
                        PetInfoCard(
                            petViewModel,
                            pet = petList[item],
                            onNavigateToPetUpdateScreen
                        )
                    }
                }
            } else {
                Text(
                    text = "您尚未添加任何宠物",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White
                )
            }
            //配置默认Indicator，当然也可以自定义,用于刷新
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = state,
                Modifier.align(Alignment.TopCenter)
            )
        }
    }
}


@Preview
@Composable
fun PreviewPetScreen() {
    //PetScreen()
}