package com.jichenhao.pettime_jichenhao.ui.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import com.jichenhao.pettime_jichenhao.model.network.oss.AliOssUtils
import com.jichenhao.pettime_jichenhao.ui.components.PhotoComponent
import com.jichenhao.pettime_jichenhao.viewModel.UserViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

@Composable
fun UploadPictureScreen(
    viewModel: UserViewModel,
    onNavigateBack: () -> Unit  // 返回上一页
) {
    val mediaAction by lazy { PhotoComponent.instance }
    var localImgPath by remember {
        mutableStateOf(Uri.EMPTY)
    }

    val context = LocalContext.current as ComponentActivity
    val loggedInUser = viewModel.loggedInUser.collectAsState()
    val showDialog by viewModel.showDialog.collectAsState()
    val message by viewModel.message.collectAsState()
    when {
        showDialog -> {
            AlertDialog(
                onDismissRequest = {
                    // 关闭Dialog
                    viewModel.unShowDialog()
                },
                title = { Text("同步数据库头像信息") },
                text = { Text(message) },
                confirmButton = {
                    TextButton(onClick = {
                        // 关闭Dialog
                        viewModel.unShowDialog()
                    }) {
                        Text("确定")
                    }
                }
            )
        }
    }

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
                        // 返回之前先刷新一下头像
                        viewModel.getUserProfile(loggedInUser.value.email)
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            AsyncImage(
                model = localImgPath,
                contentDescription = null,
                modifier = Modifier
                    .width(48.dp)
                    .height(48.dp)
                    .clip(CircleShape)
                    .placeholder(
                        visible = localImgPath == Uri.EMPTY,
                        color = Color(231, 234, 239, 255),
                        highlight = PlaceholderHighlight.shimmer(),
                    ),
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(
                onClick = {
                    mediaAction.takePhoto()
                },
            ) {
                Text(text = "拍照")
            }
            Spacer(modifier = Modifier.height(24.dp))
            TextButton(
                onClick = {
                    mediaAction.selectImage()
                },
            ) {
                Text(text = "相册")
            }
            // 将图片上传到阿里云
            TextButton(
                onClick = {
                    viewModel.uploadImageToOSS(localImgPath, context)
                },
            ) {
                Text(text = "上传")
            }
        }
    }

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
}
