package com.jichenhao.pettime_jichenhao.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jichenhao.pettime_jichenhao.R
import com.jichenhao.pettime_jichenhao.viewModel.AlbumViewModel
import com.jichenhao.pettime_jichenhao.viewModel.UserViewModel

// 相册页面
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumScreen(
    albumViewModel: AlbumViewModel,
    userViewModel: UserViewModel,
    onNavigateToAlbumUploadOneScreen: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val userLoggedIn by userViewModel.loggedInUser.collectAsState()
    // 刷新相册列表
    albumViewModel.getAlbumByEmail(userLoggedIn.email)
    // 取出在ViewModel中的相册列表
    val albumList by albumViewModel.picList.collectAsState()
    // Pager状态管理
    val pagerState = rememberPagerState(pageCount = { albumList.size })

    val showDialog by albumViewModel.showDialog.collectAsState()

    // Dialog要显示的信息
    val message by albumViewModel.message.collectAsState()

    // 是否显示对话框以及对话框中的内容
    var showConfirmDialog by remember {
        mutableStateOf(false)
    }

    // 是否显示（相册已满，无法上传）的Dialog
    var showAlbumIsFullDialog by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(text = "操作结果")
            },
            confirmButton = {
                TextButton(onClick = { }) {
                    albumViewModel.getAlbumByEmail(userLoggedIn.email)// 操作完成，刷新相册列表
                    albumViewModel.unShowDialog()
                }
            },
            text = {
                Text(text = message)
            }
        )
    }

    // 询问是否删除。对话框
    when {
        showConfirmDialog ->
            AlertDialog(
                onDismissRequest = {
                    showConfirmDialog = false
                },
                title = { Text("操作信息") },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Dangerous,
                        contentDescription = "",
                        tint = Color.Red
                    )
                },
                text = { Text("确认删除吗？？？！！！", color = Color.Red) },
                confirmButton = {
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = {
                            showConfirmDialog = false
                            // 使用currentPage获取当前图片信息并处理返回事件
                            val currentPictureInfo = albumList[pagerState.currentPage]
                            albumViewModel.deletePicture(currentPictureInfo.picId)
                        }) {
                            Text("确定删除")
                        }
                        TextButton(onClick = {
                            showConfirmDialog = false
                        }) {
                            Text("我再想想")
                        }
                    }
                }
            )
    }

    // 提示用户相册已满，无法上传的对话框
    when {
        showAlbumIsFullDialog ->
            AlertDialog(
                onDismissRequest = {

                },
                title = { Text("注意!", style = MaterialTheme.typography.titleMedium) },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Dangerous,
                        contentDescription = "",
                        tint = Color.Red
                    )
                },
                text = { Text("您的相册已满20张，VIP用户可继续上传。", color = Color.Black) },
                confirmButton = {
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(onClick = {
                            showAlbumIsFullDialog = false
                        }) {
                            Text("好的")
                        }
                    }
                }
            )
    }



    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 大背景
        Image(
            painter = painterResource(id = R.drawable.background_allblack),
            contentScale = ContentScale.Crop,// 这可以确保图片适应指定大小时不拉伸变形
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
        )
        // 用于给图片定位和加边距的Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp), // 添加水平内边距为30dp，使内容距离屏幕两边30dp
        ) {
            //Spacer(modifier = Modifier.size(230.dp))
            Spacer(modifier = Modifier.weight(1f))
            // 填充上方空白区域，将图片顶下去
            /*
            * 真正要展示的图片
            * =====================================================================
            * */
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

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(currentAlbum.picUrl)
                            //.transformations(CircleCropTransformation())
                            .build(),
                        contentDescription = "",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally) // 使Image在Column内部水平居中
                            .fillMaxWidth(1f)
                    )
                }

            } else {
                // 如果图库为空，就显示一个特定的图片
                Image(
                    painter = painterResource(id = R.drawable.splash_screen_background),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,// 这可以确保图片适应指定大小时不拉伸变形
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally) // 使Image在Column内部水平居中
                        .fillMaxWidth(1f)
                )
            }
            // =====================================================================
            Spacer(modifier = Modifier.weight(1f))// 填充下方空白区域，让图片的下方与相框的下方基本一至
        }

        // 相框背景
        Image(
            //动态加载图片
            painter = painterResource(id = R.drawable.background_album_pailide),
            contentScale = ContentScale.Crop,// 这可以确保图片适应指定大小时不拉伸变形
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
        )
        // 用于垂直放置内容的列
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
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
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Black
                    )
                ) {
                    Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "")
                }

                IconButton(
                    onClick = {
                        showConfirmDialog = true
                    },
                    colors = IconButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Black
                    )
                ) {
                    Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "")
                }
            }


            Spacer(modifier = Modifier.weight(1f)) // 填充中间空白区域，使按钮始终位于底部


            // 上传照片
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp), // 添加内边距为10dp,
                horizontalArrangement = Arrangement.Center,// 下面的两个组件位于一行的两侧
                verticalAlignment = Alignment.CenterVertically
            ) {

                Button(
                    onClick = {
                        if (albumList.size <= 20) {
                            onNavigateToAlbumUploadOneScreen()
                        } else {
                            showAlbumIsFullDialog = true
                        }
                    },
                    colors = ButtonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "上传爱宠美照",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.size(30.dp)) // 让按钮与底部有一定的间隔
        }


    } // 移动到背景图上对应相框的位置

}

@Composable
@Preview
fun PreviewAlbumScreen() {
    // AlbumScreen()
}