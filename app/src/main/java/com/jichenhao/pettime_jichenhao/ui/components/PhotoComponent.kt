package com.jichenhao.pettime_jichenhao.ui.components

import android.Manifest
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PhotoComponent {


    //这两行声明了用于启动相册选取图片和拍照操作的结果启动器，它们是在Android Compose中用来处理ActivityResult请求的，
    // 类型分别为ManagedActivityResultLauncher<Unit?, PictureResult>。
    private var openGalleryLauncher: ManagedActivityResultLauncher<Unit?, PictureResult>? = null
    private var takePhotoLauncher: ManagedActivityResultLauncher<Unit?, PictureResult>? = null

    //创建了一个CoroutineScope，用于执行异步任务，如发射MutableSharedFlow中的值。
    // 此处使用的SupervisorJob可以确保协程在遇到异常时全部取消，并结合Dispatchers.IO以在IO线程上执行任务。
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    // 这里采用对象单例模式，使得可以通过PhotoComponent.instance获取到全局唯一的PhotoComponent实例。
    companion object {
        val instance get() = Helper.obj
    }

    private object Helper {
        val obj = PhotoComponent()
    }


    /*
    * 分别定义了两个MutableSharedFlow，用于传递相机和相册权限的状态变化，
    * 当有新的权限检查结果时，通过setCheck...PermissionState方法将结果放入流中。
    * */
    //监听拍照权限flow
    private val checkCameraPermission =
        MutableSharedFlow<Boolean?>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private fun setCheckCameraPermissionState(value: Boolean?) {
        scope.launch {
            checkCameraPermission.emit(value)
        }
    }


    /*
    * 这两个方法分别用于将相机和相册权限的检查结果发送到对应的SharedFlow中。
    * */
    //相册权限flow
    private val checkGalleryImagePermission =
        MutableSharedFlow<Boolean?>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private fun setCheckGalleryPermissionState(value: Boolean?) {
        scope.launch {
            checkGalleryImagePermission.emit(value)
        }
    }


    // 这个方法被标记为@Composable，意味着它可以被用于Compose的UI构建函数中。
    // 在这个方法内部，它配置了结果启动器并设置了各种权限状态的观察者。
    /**
     * @param galleryCallback 相册结果回调
     * @param graphCallback 拍照结果回调
     * @param permissionRationale 权限拒绝状态回调
     **/
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun Register(
        galleryCallback: (selectResult: PictureResult) -> Unit,
        graphCallback: (graphResult: PictureResult) -> Unit,
        permissionRationale: ((gallery: Boolean) -> Unit)? = null,
    ) {
        val rememberGraphCallback = rememberUpdatedState(newValue = graphCallback)
        val rememberGalleryCallback = rememberUpdatedState(newValue = galleryCallback)

        //注册了打开相册和拍照的操作，并在权限满足时自动触发相应的操作。
        openGalleryLauncher = rememberLauncherForActivityResult(contract = SelectPicture()) {
            rememberGalleryCallback.value.invoke(it)
        }
        takePhotoLauncher = rememberLauncherForActivityResult(contract = TakePhoto.instance) {
            rememberGraphCallback.value.invoke(it)
        }

        // 根据Android版本动态选择所需的相册权限。
        val readGalleryPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            //13以上的权限申请
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        // 定义了权限状态变量并通过rememberSaveable使其在配置变更时保持状态。
        // 下面两行：代表相机和相册权限是否已被用户授予
        var permissionCameraState by rememberSaveable { mutableStateOf(false) }
        var permissionGalleryState by rememberSaveable { mutableStateOf(false) }

        // 创建了一个ArrayList，存储了所需权限的字符串标识。这里包含两个权限：
        // 相机权限(Manifest.permission.CAMERA)和相册读取权限（根据Android版本动态确定的readGalleryPermission）
        val permissionList = arrayListOf(
            Manifest.permission.CAMERA,
            readGalleryPermission,// 根据系统版本动态选择
        )

        // 创建了一个galleryPermissionState变量，
        // 它是通过rememberPermissionState()函数获得的，用于追踪readGalleryPermission权限的状态
        val galleryPermissionState = rememberPermissionState(readGalleryPermission)

        // 创建了一个cameraPermissionState变量，用于跟踪permissionList中列出的所有权限的状态，
        // 通过rememberMultiplePermissionsState()函数实现。
        val cameraPermissionState = rememberMultiplePermissionsState(permissionList)


        /*
        * 一个LaunchedEffect块（针对相机权限），它会在每次Composition发生变动时重新启动一个协程来收集checkCameraPermission流中的最新值。
        * 当流中有新值时，会更新permissionCameraState的状态，并根据权限状态做出不同的响应，
        * 包括显示权限说明、发起权限请求或直接执行拍照操作等。*/
        LaunchedEffect(Unit) {
            checkCameraPermission.collectLatest {
                permissionCameraState = it == true
                if (it == true) {
                    if (cameraPermissionState.allPermissionsGranted) {
                        setCheckCameraPermissionState(null)
                        takePhotoLauncher?.launch(null)
                    } else if (cameraPermissionState.shouldShowRationale) {
                        setCheckCameraPermissionState(null)
                        permissionRationale?.invoke(false)
                    } else {
                        cameraPermissionState.launchMultiplePermissionRequest()
                    }
                }
            }
        }

        /*
        * LaunchedEffect监听权限流更新（针对相册权限）： 类似于上述相机权限的处理，
        * 这里有一个针对checkGalleryImagePermission流的LaunchedEffect块，
        * 其作用是收集最新的相册权限状态并据此执行相应操作，比如打开相册或者显示权限说明。
        * */
        LaunchedEffect(Unit) {
            checkGalleryImagePermission.collectLatest {
                permissionGalleryState = it == true
                if (it == true) {
                    if (galleryPermissionState.status.isGranted) {
                        setCheckGalleryPermissionState(null)
                        openGalleryLauncher?.launch(null)
                    } else if (galleryPermissionState.status.shouldShowRationale) {
                        setCheckGalleryPermissionState(null)
                        permissionRationale?.invoke(true)
                    } else {
                        galleryPermissionState.launchPermissionRequest()
                    }
                }
            }
        }

        /*
        * 当所有相机权限都被授予且permissionCameraState为真时，该LaunchedEffect块会被触发，
        * 清空checkCameraPermission的状态，并立即尝试调用takePhotoLauncher执行拍照操作。
        * */
        LaunchedEffect(cameraPermissionState.allPermissionsGranted) {
            if (cameraPermissionState.allPermissionsGranted && permissionCameraState) {
                setCheckCameraPermissionState(null)
                takePhotoLauncher?.launch(null)
            }
        }

        /*
        * 同样，当相册权限已经被授予并且permissionGalleryState为真时，该LaunchedEffect块会被触发，
        * 清空checkGalleryPermission的状态，并尝试调用openGalleryLauncher执行打开相册操作。
        * */
        LaunchedEffect(galleryPermissionState.status.isGranted) {
            if (galleryPermissionState.status.isGranted && permissionGalleryState) {
                setCheckGalleryPermissionState(null)
                openGalleryLauncher?.launch(null)
            }
        }
    }

    //调用选择图片功能
    fun selectImage() {
        setCheckGalleryPermissionState(true)
    }

    //调用拍照
    fun takePhoto() {
        setCheckCameraPermissionState(true)
    }

}
