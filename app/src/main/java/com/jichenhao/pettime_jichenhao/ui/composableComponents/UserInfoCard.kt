package com.jichenhao.pettime_jichenhao.ui.composableComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.jichenhao.pettime_jichenhao.R
import com.jichenhao.pettime_jichenhao.model.entity.UserLoggedIn

@Composable
fun UserInfoCard(userLoggedIn: UserLoggedIn) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp) // 添加合适的内边距
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(all = 16.dp)// Card内部内容的内边距
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center, // 内容垂直居中
            horizontalAlignment = Alignment.Start, // 内容水平开头
        ) {
            // 头像行
            Row(
                modifier = Modifier
                    .size(80.dp) // 设置固定的直径大小，您可以根据需求调整这个数值
                    .clip(CircleShape), // 设置形状为圆形,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    //动态加载图片
                    painter = rememberAsyncImagePainter(
                        if (userLoggedIn.profile != null) {
                            ImageRequest.Builder(LocalContext.current)
                                .data(data = userLoggedIn.profile)
                                .apply(block = fun ImageRequest.Builder.() {
                                    crossfade(true)
                                    placeholder(R.drawable.image_loading)
                                    error(R.drawable.load_image_failed)
                                    transformations(CircleCropTransformation())
                                }).build()
                        } else {
                            R.drawable.load_image_failed
                        }
                    ),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,// 这可以确保图片适应指定大小时不拉伸变形
                )
            }
            // 姓名行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "亲爱的\n    ${userLoggedIn.email}，\n    你好。",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}