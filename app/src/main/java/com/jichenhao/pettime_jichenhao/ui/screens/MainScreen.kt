package com.jichenhao.pettime_jichenhao.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jichenhao.pettime_jichenhao.R
import com.jichenhao.pettime_jichenhao.ui.composableComponents.PetTimeBottomNavigationBar
import com.jichenhao.pettime_jichenhao.ui.composableComponents.UserInfoCard
import com.jichenhao.pettime_jichenhao.viewModel.AlbumViewModel
import com.jichenhao.pettime_jichenhao.viewModel.PetViewModel
import com.jichenhao.pettime_jichenhao.viewModel.UserViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    userViewModel: UserViewModel,
    albumViewModel: AlbumViewModel,
    onNavigateToAlbumScreen: () -> Unit,
    onNavigateToMainScreen: () -> Unit,
    onNavigateToKnowledgeScreen: () -> Unit,
    onNavigateToPetScreen: () -> Unit,
    onNavigateToUserScreen: () -> Unit,
) {
    val userLoggedIn by userViewModel.loggedInUser.collectAsState()
    albumViewModel.getAlbumByEmail(userLoggedIn.email)// 刷新相册
    val albumList by albumViewModel.picList.collectAsState()
    // Pager状态管理
    val pagerState = rememberPagerState(pageCount = { albumList.size })

    Scaffold(
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            // 背景图片
            Image(
                painter = painterResource(id = R.drawable.background_main),
                contentScale = ContentScale.Crop,// 这可以确保图片适应指定大小时不拉伸变形
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
            )
            // 保证控件从上到下摆放的Column
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 最上方的滑动相册展示页面
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    if (albumList.isNotEmpty()) {
                        // 一个可以横向滑动的图片列表，可以点击选中用来删除
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth(1f),
                            contentPadding = PaddingValues(vertical = 16.dp),
                        ) { currentPage ->
                            // 当前照片的信息
                            val currentAlbum = albumList[currentPage]
                            Card(
                                shape = RoundedCornerShape(16.dp), // 设置圆角大小
                                colors = CardColors(
                                    contentColor = Color.Black,
                                    containerColor = Color.White,
                                    disabledContainerColor = Color.White,
                                    disabledContentColor = Color.Black
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                ), // 设置边框宽度和颜色
                                modifier = Modifier
                                    .padding(horizontal = 10.dp, vertical = 5.dp) // 可选：添加内边距
                                    .align(Alignment.CenterHorizontally)
                                    .fillMaxWidth()
                                    .height(190.dp)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(currentAlbum.picUrl)
                                        //.transformations(CircleCropTransformation())
                                        .build(),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally) // 使Image在Column内部水平居中
                                        .fillMaxWidth()
                                        .height(180.dp) // 设置图片高度为固定值
                                        .aspectRatio(
                                            0.5f,
                                            false
                                        ), // 指定图片宽高比为1:1（正方形），若想改为其他比例可调整第一个参数
                                )
                            }
                        }

                    } else {
                        // 如果图库为空，就显示一个特定的图片
                        Card(
                            shape = RoundedCornerShape(16.dp), // 设置圆角大小
                            colors = CardColors(
                                contentColor = Color.Black,
                                containerColor = Color.White,
                                disabledContainerColor = Color.White,
                                disabledContentColor = Color.Black
                            ),
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                            ), // 设置边框宽度和颜色
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 5.dp) // 可选：添加内边距
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth()
                                .height(190.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.pet_cuisine_background),
                                contentDescription = "",
                                contentScale = ContentScale.Crop,// 这可以确保图片适应指定大小时不拉伸变形
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally) // 使Image在Column内部水平居中
                                    .fillMaxWidth()
                                    .height(180.dp) // 设置图片高度为固定值400.dp
                                    .aspectRatio(0.5f, false), // 指定图片宽高比为1:1（正方形），若想改为其他比例可调整第一个参数
                            )
                        }

                    }
                }
                Spacer(modifier = Modifier.size(50.dp))
                UserInfoCard(userLoggedIn)
                Spacer(modifier = Modifier.size(50.dp))
                // 放置主页其他组件
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        onClick = {
                            onNavigateToAlbumScreen()
                        },
                        modifier = Modifier
                            .size(120.dp)
                            .padding(5.dp),
                        colors = CardColors(
                            containerColor = Color.White,
                            contentColor = Color.Black,
                            disabledContentColor = Color.Black,
                            disabledContainerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {

                            Image(
                                painter = painterResource(id = R.drawable.ic_album_color),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.size(5.dp))
                            Text(text = "爱宠相册")
                        }
                    }
                }
            }
        }
    }
}