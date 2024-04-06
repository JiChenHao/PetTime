package com.jichenhao.pettime_jichenhao.ui.screens
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.jichenhao.pettime_jichenhao.R
import com.jichenhao.pettime_jichenhao.ui.composableComponents.PetCuisineWithBar
import com.jichenhao.pettime_jichenhao.ui.composableComponents.PetTimeBottomNavigationBar
import com.jichenhao.pettime_jichenhao.viewModel.PetCuisineViewModel

@Composable
fun KnowledgeScreen(
    onNavigateToMainScreen: () -> Unit,
    onNavigateToKnowledgeScreen: () -> Unit,
    onNavigateToPetScreen: () -> Unit,
    onNavigateToUserScreen: () -> Unit,
    petCuisineViewModel: PetCuisineViewModel,
    onNavigateToPetCuisineDetail: () -> Unit, // 用于点击卡片跳转到食物详情
) {

    // 刷新食谱列表
    petCuisineViewModel.getAllPetCuisineList()
    // 取出在ViewModel中的食谱信息
    val petCuisineList by petCuisineViewModel.petCuisineList.collectAsState()

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
            PetCuisineWithBar(
                petCuisineViewModel,
                petCuisineList,
                onNavigateToPetCuisineDetail = onNavigateToPetCuisineDetail,
            )
        }
    }
}