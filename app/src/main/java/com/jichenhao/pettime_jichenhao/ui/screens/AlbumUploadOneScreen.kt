package com.jichenhao.pettime_jichenhao.ui.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import com.jichenhao.pettime_jichenhao.R
import com.jichenhao.pettime_jichenhao.model.entity.PictureInfo
import com.jichenhao.pettime_jichenhao.ui.components.PhotoComponent
import com.jichenhao.pettime_jichenhao.viewModel.AlbumViewModel
import com.jichenhao.pettime_jichenhao.viewModel.UserViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AlbumUploadOneScreen(
    albumViewModel: AlbumViewModel,
    userViewModel: UserViewModel,
    onNavigateBack: () -> Unit
) {
    // =====相机、相册相关变量
    // 保存要上传的照片的本地URI
    var localImgPath by remember {
        mutableStateOf(Uri.EMPTY)
    }
    // 用于上传到数据库的图片的OSS的URL
    var pictureUriToDB: String? by remember {
        mutableStateOf(null)
    }
    val context = LocalContext.current as ComponentActivity
    val userLoggedIn by userViewModel.loggedInUser.collectAsState()
    val showDialog by albumViewModel.showDialog.collectAsState()
    val message by albumViewModel.message.collectAsState()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("操作信息") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = {
                    albumViewModel.unShowDialog()// 关闭Dialog
                    albumViewModel.getAlbumByEmail(userLoggedIn.email)// 刷新图片列表
                    onNavigateBack()
                }) {
                    Text("确定")
                }
            }
        )
    }
    // 每当图片被选择到相框里面，都会自动上传文件并且返回URI到profile
    // 每当localImgPath刷新且不是空值的时候就会执行
    if (localImgPath != Uri.EMPTY) {
        // 如果已经选了头像，则先尝试上传到OSS，将profile赋值为返回的url
        albumViewModel.uploadImageToOSS(
            localImgPath,
            context
        )
        LaunchedEffect(Unit) {
            albumViewModel.urlToOss.collectLatest { imageUrl ->
                // 注意，collectLatest会自动取消之前的收集并在新值可用时重新启动
                pictureUriToDB = imageUrl
                Log.d("添加相册UI", "pictureUriToDB值更新为：${pictureUriToDB}")
            }
        }
    }


    // 延迟创建的媒体示例，用来打开相册、相机
    val mediaAction by lazy { PhotoComponent.instance }

    // 对相机和相册的动作反馈的注册
    mediaAction.Register(
        // 返回从相册中返回的图片的本地位置
        galleryCallback = {
            Log.d("我的log", "相册内容${it}")
            if (it.isSuccess) {
                localImgPath = it.uri
            }
        },
        // 返回拍摄照片后返回的图片的本地位置
        graphCallback = {
            Log.d("我的log", "拍照内容${it.uri}")
            if (it.isSuccess) {
                localImgPath = it.uri
            }
        },
        permissionRationale = {
            //权限拒绝的处理
        }
    )


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
            AsyncImage(
                model = localImgPath,
                contentDescription = null,
                modifier = Modifier
                    .placeholder(
                        visible = localImgPath == Uri.EMPTY,
                        color = Color(231, 234, 239, 255),
                        highlight = PlaceholderHighlight.shimmer(),
                    )
                    .align(Alignment.CenterHorizontally) // 使Image在Column内部水平居中
                    .fillMaxWidth(1f),
                contentScale = ContentScale.Crop,
            )
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
                horizontalArrangement = Arrangement.Start,// 返回键位于最左边
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
            }


            Spacer(modifier = Modifier.weight(1f)) // 填充中间空白区域，使按钮始终位于底部


            // 上传照片、拍照
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp), // 添加内边距为10dp,
                horizontalArrangement = Arrangement.SpaceBetween,// 下面的两个组件位于一行的两侧
                verticalAlignment = Alignment.CenterVertically
            ) {

                Button(
                    onClick = {
                        // 拍照
                        mediaAction.takePhoto()
                    },
                    colors = ButtonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "点击拍照",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }

                Button(
                    onClick = {
                        // 选择照片
                        mediaAction.selectImage()
                    },
                    colors = ButtonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "打开相册",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.size(10.dp)) // 让按钮与底部有一定的间隔
            // 确认上传
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp), // 添加内边距为10dp,
                horizontalArrangement = Arrangement.Center,// 下面的两个组件位于一行的两侧
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        // 将选中的图片的OSS链接上传到DB
                        Log.d("上传选中的图片到DB", "图片OSS链接为：$pictureUriToDB")
                        if (pictureUriToDB != null) {
                            val pictureInfo = PictureInfo(
                                0,
                                pictureUriToDB!!,
                                userLoggedIn.email
                            )
                            // 开始上传宠物对象到数据库中
                            albumViewModel.addPicture(pictureInfo)
                        } else {
                            Toast.makeText(
                                context,
                                "网络出了点小差错，请重新选择图片",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "确认上传",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.size(10.dp)) // 让按钮与底部有一定的间隔
        }
    } // 移动到背景图上对应相框的位置
}

@Composable
@Preview
fun PreviewAlbumUploadOneScreen() {
    //AlbumUploadOneScreen()
}