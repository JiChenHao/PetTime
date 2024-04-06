package com.jichenhao.pettime_jichenhao.ui.composableComponents

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.jichenhao.pettime_jichenhao.R
import com.jichenhao.pettime_jichenhao.model.entity.Pet
import com.jichenhao.pettime_jichenhao.viewModel.PetViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL


// 宠物信息卡组件
@Composable
fun PetInfoCard(
    petViewModel: PetViewModel,
    pet: Pet,
    onNavigateToUpdatePet: () -> Unit// 用于长按卡片跳转到修改宠物信息页面
) {

    //需要传入一个宠物的信息才能继续展示
    val petSex = if (pet.petSex) "弟弟" else "妹妹"
    Card(
        onClick = {
            // 将本卡片的宠物信息传递给viewmodel
            petViewModel.setPetToUpdate(pet)
            onNavigateToUpdatePet()// 跳转到修改信息页面
        },
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
            horizontalAlignment = Alignment.CenterHorizontally, // 内容水平居中
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
                        ImageRequest.Builder(LocalContext.current).data(data = pet.profile)
                            .apply(block = fun ImageRequest.Builder.() {
                                crossfade(true)
                                placeholder(R.drawable.image_loading)
                                error(R.drawable.load_image_failed)
                                transformations(CircleCropTransformation())
                            }).build()
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
                    text = pet.petName,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            // 性别行
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "性别:",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = petSex,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // 品种行
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "品种:",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = pet.petBreeds,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // 性别行
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "年龄:",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${pet.petAge}岁",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

        }
    }
}


@Preview
@Composable
fun PreviewPetInfoCard() {

}