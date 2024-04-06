package com.jichenhao.pettime_jichenhao.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.jichenhao.pettime_jichenhao.R
import com.jichenhao.pettime_jichenhao.ui.composableComponents.MyCardButton
import com.jichenhao.pettime_jichenhao.viewModel.PetCuisineViewModel

// 显示宠物食谱某种食物的详情页面
@Composable
fun PetCuisineDetailScreen(
    petCuisineViewModel: PetCuisineViewModel,
    onNavigateBack: () -> Unit// 返回上一个界面
) {
// 取出在ViewModel中的食物信息
    val petCuisineInfo by petCuisineViewModel.foodDetail.collectAsState()
    // 根据宠物种类判断是那种显示
    var chosePetTypeNum by remember {
        mutableIntStateOf(1)// 狗狗 或者 猫咪
    }
    // 能不能吃？
    var canEat by remember {
        mutableStateOf(petCuisineInfo.dog_eat)
    }
    // 建议？
    var suggestion by remember {
        mutableStateOf(petCuisineInfo.dog_suggestion)
    }
    when (chosePetTypeNum) {
        1 -> {
            canEat = petCuisineInfo.dog_eat
            suggestion = petCuisineInfo.dog_suggestion
        }

        2 -> {
            canEat = petCuisineInfo.cat_eat
            suggestion = petCuisineInfo.cat_suggestion
        }
    }
    // 最外层的Column用于竖直放置组件
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        // 分割屏幕的上板块Box，设置背景图
        // 利用Box的堆叠效果，直接设置背景图
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 黑猫背景
            Image(
                painter = painterResource(id = R.drawable.pet_cuisine_background),
                contentDescription = null
            )

            // 返回键、标题键所在行
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .background(color = Color.Transparent), // 保持背景透明以显示背景图
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onNavigateBack() },
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                // 空隙，占位使Text居中
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "能不能吃？",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                // 空隙，占位使Text居中
                Spacer(modifier = Modifier.weight(1f))
                // 空隙，占位使Text居中
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        // 显示主要内容的Box
        Box(modifier = Modifier.fillMaxSize()) {
            // 保证内容从上到下显示
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,// 垂直顺序分布
                horizontalAlignment = Alignment.CenterHorizontally// 水平居中
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Card(onClick = {
                        chosePetTypeNum = 1
                    }) {
                        MyCardButton(selectNum = 1, inputNum = chosePetTypeNum, textToShow = "狗狗")
                    }
                    Card(
                        onClick = {
                            chosePetTypeNum = 2
                        }
                    ) {
                        MyCardButton(selectNum = 2, inputNum = chosePetTypeNum, textToShow = "猫咪")
                    }
                }
                Image(
                    //动态加载图片
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(data = petCuisineInfo.image_url)
                            .apply(block = fun ImageRequest.Builder.() {
                                crossfade(true)
                                placeholder(R.drawable.image_loading)
                                error(R.drawable.load_image_failed)
                                //transformations(CircleCropTransformation())
                            }).build()
                    ),
                    contentScale = ContentScale.Crop,// 这可以确保图片适应指定大小时不拉伸变形
                    contentDescription = null,
                    modifier = Modifier
                        .size(260.dp, 260.dp)
                        .clip(RoundedCornerShape(8.dp)),// 带圆角的正方形
                )
                Spacer(modifier = Modifier.size(10.dp))
                androidx.compose.material3.Text(
                    text = petCuisineInfo.food_name,
                    color = Color.Black,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.size(10.dp))
                Card(
                    modifier = Modifier
                        .size(120.dp, 50.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = canEat,
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    }

                }
                Spacer(modifier = Modifier.size(10.dp))
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = suggestion,
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewPetCuisineDetailScreen() {
    //PetCuisineDetailScreen()
}