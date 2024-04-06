package com.jichenhao.pettime_jichenhao.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import com.jichenhao.pettime_jichenhao.R
import com.jichenhao.pettime_jichenhao.model.entity.Pet
import com.jichenhao.pettime_jichenhao.ui.components.PhotoComponent
import com.jichenhao.pettime_jichenhao.viewModel.PetViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// 更新宠物信息的页面（删除按钮也在这里）
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetUpdateScreen(
    petViewModel: PetViewModel,
    onNavigateBack: () -> Unit
) {
    // 取出选中卡片放在ViewModel中的卡片信息
    val pet by petViewModel.petToUpdate.collectAsState()
    // 是否显示对话框以及对话框中的内容
    val showDialog by petViewModel.showDialog.collectAsState()
    // 是否显示对话框以及对话框中的内容
    var showConfirmDialog by remember {
        mutableStateOf(false)
    }
    // Dialog要显示的信息
    val message by petViewModel.message.collectAsState()
    // 在update或者delete操作成功后，此页面会被唤起，点击确定会刷新列表并且返回上一页
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("操作信息") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = {
                    // 关闭Dialog
                    petViewModel.unShowDialog()
                    // 返回之前刷新宠物列表
                    petViewModel.getPetListByUserEmail(pet.userEmail)
                    onNavigateBack()
                }) {
                    Text("确定")
                }
            }
        )
    }

    // 确认删除对话框
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
                            petViewModel.deletePet(pet)
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

    Box(modifier = Modifier.fillMaxSize()) {
        // 背景图片
        Image(
            painter = painterResource(id = R.drawable.background_main),
            contentScale = ContentScale.Crop,// 这可以确保图片适应指定大小时不拉伸变形
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
        )

        // 主体
        Scaffold(
            topBar = {
                // 单独的顶部导航，因为不只要放回退栏，还要放一个删除按钮
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
                            // 先显示一个Dialog，提醒用户是否真的要删除
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
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
            ) {


                //输入框、选择框的变量
                var petName by remember { mutableStateOf(pet.petName) }
                var petSex by remember { mutableStateOf(pet.petSex) }
                var petBreeds by remember { mutableStateOf(pet.petBreeds) }
                var petAge by remember { mutableIntStateOf(pet.petAge) }
                var profile: String? by remember { mutableStateOf(pet.profile) }

                // 是否显示底部动作条（用来添加宠物头像）
                var showBottomSheet by remember {
                    mutableStateOf(false)
                }
                val sheetState = rememberModalBottomSheetState()
                val scope = rememberCoroutineScope()// 获取当前协程内容，用来执行协程函数

                val context = LocalContext.current as ComponentActivity


                // =====相机、相册相关变量
                // 保存要上传的照片的本地URI
                var localImgPath by remember {
                    mutableStateOf(Uri.EMPTY)
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


                // 每当图片被选择到相框里面，都会自动上传文件并且返回URI到profile
                // 每当localImgPath刷新且不是空值的时候就会执行
                if (localImgPath != Uri.EMPTY) {
                    // 如果已经选了头像，则先尝试上传到OSS，将profile赋值为返回的url
                    petViewModel.uploadImageToOSS(
                        localImgPath,
                        context
                    )
                    LaunchedEffect(Unit) {
                        petViewModel.urlToOss.collectLatest { imageUrl ->
                            // 在这里处理新的imageUrl，如更新UI展示
                            // 注意，collectLatest会自动取消之前的收集并在新值可用时重新启动
                            profile = imageUrl
                        }
                    }
                }

                // 底部动作条
                if (showBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            showBottomSheet = false
                        },
                        sheetState = sheetState
                    ) {
                        // Sheet content
                        // 包裹Button在Box容器中
                        Box(
                            // 设置垂直居中
                            // 添加底部内边距，例如设置为24dp
                            modifier = Modifier
                                .padding(bottom = with(LocalDensity.current) { 40.dp })
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Button(
                                    onClick = {
                                        mediaAction.takePhoto()
                                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                                            if (!sheetState.isVisible) {
                                                showBottomSheet = false
                                            }
                                        }
                                    }
                                ) {
                                    Text("拍照")
                                }
                                Button(
                                    onClick = {
                                        mediaAction.selectImage()
                                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                                            if (!sheetState.isVisible) {
                                                showBottomSheet = false
                                            }
                                        }
                                    }
                                ) {
                                    Text("从相册中选择")
                                }
                            }
                        }
                    }
                }

                // 添加宠物表格。本身
                Column {
                    Card(
                        onClick = { /*TODO*/ },
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
                                    .size(100.dp) // 设置固定的直径大小，您可以根据需求调整这个数值
                                    .clip(CircleShape)
                                    .fillMaxWidth(), // 设置形状为圆形,
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                // 可点击的图片，点击就能选择图片上传
                                Box(
                                    modifier = Modifier
                                        .clickable {
                                            showBottomSheet = true// 点击显示底部栏
                                        }
                                        .clip(CircleShape),
                                ) {
                                    // 如果宠物目前已经有头像且尚未选择要更新的头像就先显示一个静态的图片
                                    if (profile != null && localImgPath == Uri.EMPTY) {
                                        Image(//动态加载图片
                                            painter = rememberAsyncImagePainter(
                                                ImageRequest.Builder(LocalContext.current)
                                                    .data(data = profile)
                                                    .apply(block = fun ImageRequest.Builder.() {
                                                        crossfade(true)
                                                        placeholder(R.drawable.image_loading)
                                                        error(R.drawable.load_image_failed)
                                                        transformations(CircleCropTransformation())
                                                    }).build()
                                            ), contentDescription = ""
                                        )
                                    } else {
                                        AsyncImage(
                                            model = localImgPath,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .placeholder(
                                                    visible = localImgPath == Uri.EMPTY,
                                                    color = Color(231, 234, 239, 255),
                                                    highlight = PlaceholderHighlight.shimmer(),
                                                )
                                                .fillMaxSize(),
                                            contentScale = ContentScale.Crop,
                                        )
                                    }
                                }
                            }
                            // 姓名行
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {

                                OutlinedTextField(
                                    value = petName,
                                    onValueChange = {
                                        petName = it
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .focusable(true),
                                    maxLines = 1,
                                )

                            }
                            // 性别行
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
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
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.SpaceBetween,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            RadioButton(
                                                selected = petSex,
                                                onClick = { petSex = true },
                                                modifier = Modifier.semantics {
                                                    contentDescription = "Localized Description"
                                                }
                                            )
                                            Text(text = "弟弟")
                                        }
                                        Column(
                                            verticalArrangement = Arrangement.SpaceBetween,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            RadioButton(
                                                selected = !petSex,
                                                onClick = { petSex = false },
                                                modifier = Modifier.semantics {
                                                    contentDescription = "Localized Description"
                                                }
                                            )
                                            Text(text = "妹妹")
                                        }

                                    }
                                }
                            }

                            // 品种行
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "品种:",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }

                                Column(
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        OutlinedTextField(
                                            value = petBreeds,
                                            onValueChange = {
                                                petBreeds = it
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .focusable(true),
                                            maxLines = 1,
                                        )
                                    }
                                }
                            }

                            // 年龄行
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
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
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextButton(onClick = { petAge-- }) {
                                            Icon(
                                                imageVector = Icons.Filled.Remove,
                                                contentDescription = ""
                                            )
                                        }
                                        Text(
                                            text = "${petAge}岁",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        TextButton(onClick = { petAge++ }) {
                                            Icon(
                                                imageVector = Icons.Filled.Add,
                                                contentDescription = ""
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = {
                            // 根据表格信息构建要上传的对象
                            val petToUpdate = Pet(
                                pet.petId,// 注意这里必须要id
                                petName,
                                petSex,
                                petBreeds,
                                petAge,
                                pet.userEmail,
                                profile
                            )
                            Log.d("更新宠物到DB", "要更新的宠物信息为：$petToUpdate")
                            // 开始上传宠物对象到数据库中
                            petViewModel.updatePet(petToUpdate)
                            // 只有出了结果，才能够显示按钮,使用showDialogButton控制
                        }) {
                            Text(text = "确认更新")
                        }
                    }
                }
            }

        }
    }

}