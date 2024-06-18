package com.jichenhao.pettime_jichenhao.ui.composableComponents

import android.graphics.drawable.shapes.Shape
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.jichenhao.pettime_jichenhao.R
import com.jichenhao.pettime_jichenhao.model.entity.PetCuisine
import com.jichenhao.pettime_jichenhao.viewModel.PetCuisineViewModel

// 用于显示列表中单个食材的形象
@Composable
fun PetCuisineItemCard(
    petCuisineViewModel: PetCuisineViewModel,
    petCuisine: PetCuisine,
    onNavigateToPetCuisineDetail: () -> Unit, // 用于点击卡片跳转到食物详情
) {
    Card(
        onClick = {
            petCuisineViewModel.setFoodDetail(petCuisine)
            onNavigateToPetCuisineDetail()
        },// 点击进入详情页面
        modifier = Modifier.size(120.dp, 150.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize() // 设置固定的直径大小，您可以根据需求调整这个数值
                .clip(RoundedCornerShape(8.dp))// 带圆角的正方形
                .padding(5.dp),// 内部边缘
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(
                //动态加载图片
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(data = petCuisine.image_url)
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
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),// 带圆角的正方形
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(text = petCuisine.food_name)
        }
    }
}

@Composable
fun PetCuisineCardGrid(
    petCuisineViewModel: PetCuisineViewModel,
    petCuisineList: List<PetCuisine>,
    onNavigateToPetCuisineDetail: () -> Unit, // 用于点击卡片跳转到食物详情
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 128.dp)// 使用 GridCells.Adaptive 将每列设置为至少 128.dp 宽
        ) {
            items(petCuisineList.size) { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    PetCuisineItemCard(
                        petCuisineViewModel,
                        petCuisineList[item],
                        onNavigateToPetCuisineDetail,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ShowPetCuisineByCanEat(
    petCuisineViewModel: PetCuisineViewModel,
    petCuisineList: List<PetCuisine>, dogOrCat: String,
    onNavigateToPetCuisineDetail: () -> Unit, // 用于点击卡片跳转到食物详情
) {
    val groupedCuisines: Map<String, List<PetCuisine>>
    if (dogOrCat == "dog") {
        // 将传进来的List分类为 ENUM('能吃', '慎吃', '不能吃')
        groupedCuisines = petCuisineList.groupBy { it.dog_eat }
    } else {
        // 将传进来的List分类为 ENUM('能吃', '慎吃', '不能吃')
        groupedCuisines = petCuisineList.groupBy { it.cat_eat }
    }
    val canEatList = groupedCuisines["能吃"] ?: emptyList()
    val cautiousList = groupedCuisines["慎吃"] ?: emptyList()
    val cannotEatList = groupedCuisines["不能吃"] ?: emptyList()
    var choseCanEatType by remember {
        mutableIntStateOf(1)// 1是能吃，2是慎吃，3是不能吃
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            // 选择列表的按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { choseCanEatType = 1 }) {
                    MyTextWithCondition(1, choseCanEatType, "能吃")
                }
                TextButton(onClick = { choseCanEatType = 2 }) {
                    MyTextWithCondition(2, choseCanEatType, "慎吃")
                }
                TextButton(onClick = { choseCanEatType = 3 }) {
                    MyTextWithCondition(3, choseCanEatType, "不能吃")
                }
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
        // 根据选择显示不同的列表
        when (choseCanEatType) {
            1 -> {
                PetCuisineCardGrid(petCuisineViewModel, canEatList, onNavigateToPetCuisineDetail)
            }

            2 -> {
                PetCuisineCardGrid(petCuisineViewModel, cautiousList, onNavigateToPetCuisineDetail)
            }

            3 -> {
                PetCuisineCardGrid(petCuisineViewModel, cannotEatList, onNavigateToPetCuisineDetail)
            }
        }
    }
}

// 整体页面
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetCuisineWithBar(
    petCuisineViewModel: PetCuisineViewModel,
    petCuisineList: List<PetCuisine>,
    onNavigateToPetCuisineDetail: () -> Unit, // 用于点击卡片跳转到食物详情
) {
// ENUM('水果', '零食甜点', '饮料', '坚果', '主食', '蔬菜', '肉类')
    var choseFoodType by remember {
        mutableIntStateOf(1)
    }
    var chosePetType by remember {
        mutableStateOf("dog")// dog or cat
    }
    var chosePetTypeNum by remember {
        mutableIntStateOf(1)
    }

    val groupedCuisines = petCuisineList.groupBy { it.food_type }

    val fruitList = groupedCuisines["水果"] ?: emptyList()
    val desertList = groupedCuisines["零食甜点"] ?: emptyList()
    val drinkList = groupedCuisines["饮料"] ?: emptyList()
    val beanList = groupedCuisines["坚果"] ?: emptyList()
    val mineFoodList = groupedCuisines["主食"] ?: emptyList()
    val caiList = groupedCuisines["蔬菜"] ?: emptyList()
    val meatList = groupedCuisines["肉类"] ?: emptyList()

    var listToShow by remember {
        mutableStateOf(fruitList)
    }
    Scaffold(
// 本页定制顶部栏，同时也要显示外部的顶部栏
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Card(onClick = {
                    chosePetTypeNum = 1
                    chosePetType = "dog"
                }) {
                    MyCardButton(selectNum = 1, inputNum = chosePetTypeNum, textToShow = "狗狗")
                }
                Card(
                    onClick = {
                        chosePetTypeNum = 2
                        chosePetType = "cat"
                    }
                ) {
                    MyCardButton(selectNum = 2, inputNum = chosePetTypeNum, textToShow = "猫咪")
                }
            }
        }

    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            Row {
                // 卡片式侧边栏
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(100.dp)
                        .clip(RoundedCornerShape(10.dp)) // 设置圆角半径为 10.dp
                        .background(Color.White)

                ) {
                    // 侧边栏列
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(2.dp)
                            .width(100.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = {
                                choseFoodType = 1
                                listToShow = fruitList
                            }) {
                                MyTextWithCondition(1, choseFoodType, "水果")
                            }
                        }

                        Spacer(modifier = Modifier.size(2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = {
                                choseFoodType = 2
                                listToShow = desertList
                            }) {
                                MyTextWithCondition(2, choseFoodType, "零食甜点")
                            }
                        }

                        Spacer(modifier = Modifier.size(2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = {
                                choseFoodType = 3
                                listToShow = drinkList
                            }) {
                                MyTextWithCondition(3, choseFoodType, "饮料")
                            }
                        }

                        Spacer(modifier = Modifier.size(2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = {
                                choseFoodType = 4
                                listToShow = beanList
                            }) {
                                MyTextWithCondition(4, choseFoodType, "坚果")
                            }
                        }

                        Spacer(modifier = Modifier.size(2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = {
                                choseFoodType = 5
                                listToShow = mineFoodList
                            }) {
                                MyTextWithCondition(5, choseFoodType, "主食")
                            }
                        }

                        Spacer(modifier = Modifier.size(2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = {
                                choseFoodType = 6
                                listToShow = caiList
                            }) {
                                MyTextWithCondition(6, choseFoodType, "蔬菜")
                            }
                        }

                        Spacer(modifier = Modifier.size(2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = {
                                choseFoodType = 7
                                listToShow = meatList
                            }) {
                                MyTextWithCondition(7, choseFoodType, "肉类")
                            }
                        }
                    }
                }
                // 显示内容
                ShowPetCuisineByCanEat(
                    petCuisineViewModel,
                    listToShow,
                    chosePetType,
                    onNavigateToPetCuisineDetail
                )
            }
        }
    }
}

