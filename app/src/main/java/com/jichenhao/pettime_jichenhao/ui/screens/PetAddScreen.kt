package com.jichenhao.pettime_jichenhao.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
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
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import com.jichenhao.pettime_jichenhao.model.entity.Pet
import com.jichenhao.pettime_jichenhao.ui.components.PhotoComponent
import com.jichenhao.pettime_jichenhao.viewModel.PetViewModel
import com.jichenhao.pettime_jichenhao.viewModel.UserViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// 表格页，用来添加新的宠物
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetAddScreen(
    userViewModel: UserViewModel,// 用来获取目前操作用户的email
    petViewModel: PetViewModel,
    onNavigateBack: () -> Unit
) {
    val loggedInUser by userViewModel.loggedInUser.collectAsState()
    val userEmail = loggedInUser.email
    //输入框、选择框的变量
    var petName by remember { mutableStateOf("请输入您爱宠的名字") }
    var petSex by remember { mutableStateOf(true) }
    var petBreeds by remember { mutableStateOf("") }
    var petAge by remember { mutableIntStateOf(0) }
    var profile: String? by remember { mutableStateOf(null) }

    // 是否显示底部动作条（用来添加宠物头像）
    var showBottomSheet by remember {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()// 获取当前协程内容，用来执行协程函数

    // 是否显示对话框以及对话框中的内容
    val showDialog by petViewModel.showDialog.collectAsState()
    // Dialog要显示的信息
    val message by petViewModel.message.collectAsState()

    // 上传相片相关变量
    var localImgPath by remember {
        mutableStateOf(Uri.EMPTY)
    }

    val mediaAction by lazy { PhotoComponent.instance }
    val context = LocalContext.current as ComponentActivity

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


    // 对话提示框
    when {
        showDialog ->
            AlertDialog(
                onDismissRequest = {},
                title = { Text("操作信息") },
                text = { Text(message) },
                confirmButton = {
                    TextButton(onClick = {
                        // 关闭Dialog
                        petViewModel.unShowDialog()
                        // 返回之前刷新宠物列表
                        petViewModel.getPetListByUserEmail(userEmail)
                        onNavigateBack()
                    }) {
                        Text("确定")
                    }
                }
            )
    }
// 每当图片被选择到相框里面，都会自动上传文件并且返回URI到profile
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
                        AsyncImage(
                            model = localImgPath, contentDescription = null,
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
                                Icon(imageVector = Icons.Filled.Remove, contentDescription = "")
                            }
                            Text(
                                text = "${petAge}岁",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            TextButton(onClick = { petAge++ }) {
                                Icon(imageVector = Icons.Filled.Add, contentDescription = "")
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
                val petToAdd = Pet(
                    0,
                    petName,
                    petSex,
                    petBreeds,
                    petAge,
                    userEmail,
                    profile
                )
                Log.d("添加宠物到DB", "要添加的宠物信息为：$petToAdd")
                // 开始上传宠物对象到数据库中
                petViewModel.addPet(petToAdd)
                // 只有出了结果，才能够显示按钮,使用showDialogButton控制
            }) {
                Text(text = "确认添加")
            }
        }
    }
}

@Composable
@Preview
fun PreviewFormAddPet() {
    //FormAddPet()
}