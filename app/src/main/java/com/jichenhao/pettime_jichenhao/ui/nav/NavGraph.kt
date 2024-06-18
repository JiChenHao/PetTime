package com.jichenhao.pettime_jichenhao.ui.nav

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jichenhao.pettime_jichenhao.ui.screens.AlbumScreen
import com.jichenhao.pettime_jichenhao.ui.screens.AlbumUploadOneScreen
import com.jichenhao.pettime_jichenhao.ui.screens.KnowledgeScreen
import com.jichenhao.pettime_jichenhao.ui.screens.LoginScreen
import com.jichenhao.pettime_jichenhao.ui.screens.MainScreen
import com.jichenhao.pettime_jichenhao.ui.screens.PetAddScreen
import com.jichenhao.pettime_jichenhao.ui.screens.PetCuisineDetailScreen
import com.jichenhao.pettime_jichenhao.ui.screens.PetScreen
import com.jichenhao.pettime_jichenhao.ui.screens.PetUpdateScreen
import com.jichenhao.pettime_jichenhao.ui.screens.RegisterScreen
import com.jichenhao.pettime_jichenhao.ui.screens.SplashScreen
import com.jichenhao.pettime_jichenhao.ui.screens.UploadPictureScreen
import com.jichenhao.pettime_jichenhao.ui.screens.UserScreen
import com.jichenhao.pettime_jichenhao.viewModel.AlbumViewModel
import com.jichenhao.pettime_jichenhao.viewModel.PetCuisineViewModel
import com.jichenhao.pettime_jichenhao.viewModel.PetViewModel
import com.jichenhao.pettime_jichenhao.viewModel.UserViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    // 从MainActivity获取到全局唯一的ViewModel实例（由hilt管理并且注入到Activity）
    albumViewModel: AlbumViewModel,
    userViewModel: UserViewModel,
    petViewModel: PetViewModel,
    petCuisineViewModel: PetCuisineViewModel
) {
    Log.d("MyNavGraph", "MyNavGraph被调用了")
    //导航控制中心
    /* NavHost
    NavHost是Compose Navigation的核心组件，它负责构建整个应用的导航图（navigation graph）。在这里，它接收两个主要参数：
    navController: 是一个NavHostController实例，它是导航图的控制器，用于发起屏幕间的跳转操作。
    startDestination: 指定导航图中的起始页面路由字符串，这里是ScreenPage.Home.route，即应用启动后首先显示的页面。
    * */
    NavHost(navController, startDestination = ScreenPage.Splash.route) { // 设置起始页为登录页
        /*
        * composable
        在NavHost内部使用composable函数来声明每一个可导航到的屏幕页面。
        * 每个composable块接受一个路由字符串参数和一个lambda表达式，在该表达式内编写实际的UI布局代码。
        参数route对应于页面的唯一标识符，当调用navController.navigate(route)时，
        * Compose会找到与之匹配的composable并显示其中的内容。
        * */
        composable(ScreenPage.Splash.route) {
            SplashScreen(
                userViewModel,
                onNavigateToMainScreen = { navController.navigate(ScreenPage.Main.route) },
                onNavigateToLoginScreen = { navController.navigate(ScreenPage.Login.route) },
                onNavigatePopBackStack = { navController.popBackStack() } // 清空返回栈
            )
        }
        composable(ScreenPage.Login.route) {
            LoginScreen(
                userViewModel,
                onNavigateToMainScreen = { navController.navigate(ScreenPage.Main.route) },
                onNavigateToRegisterScreen = { navController.navigate(ScreenPage.Register.route) },
                onNavigatePopBackStack = { navController.popBackStack() } // 清空返回栈
            )
        }
        composable(ScreenPage.Register.route) {
            RegisterScreen(
                userViewModel,
                onNavigateBack = { navController.navigateUp() }// 返回上一页
            )
        }
        composable(ScreenPage.Main.route) {
            MainScreen(
                userViewModel,
                albumViewModel,
                onNavigateToAlbumScreen = { navController.navigate(ScreenPage.Album.route) },
                onNavigateToMainScreen = { navController.navigate(ScreenPage.Main.route) },
                onNavigateToKnowledgeScreen = { navController.navigate(ScreenPage.Knowledge.route) },
                onNavigateToPetScreen = { navController.navigate(ScreenPage.Pet.route) },
                onNavigateToUserScreen = { navController.navigate(ScreenPage.User.route) }
            )
        }
        composable(ScreenPage.Album.route) {
            AlbumScreen(
                albumViewModel,
                userViewModel,
                onNavigateToAlbumUploadOneScreen = { navController.navigate(ScreenPage.AlbumUploadOne.route) },//TODO
                onNavigateBack = { navController.navigateUp() }// 返回上一页
            )
        }
        composable(ScreenPage.AlbumUploadOne.route) {
            AlbumUploadOneScreen(
                albumViewModel,
                userViewModel,
                onNavigateBack = { navController.navigateUp() }// 返回上一页
            )
        }
        composable(ScreenPage.Knowledge.route) {
            KnowledgeScreen(
                onNavigateToMainScreen = { navController.navigate(ScreenPage.Main.route) },
                onNavigateToKnowledgeScreen = { navController.navigate(ScreenPage.Knowledge.route) },
                onNavigateToPetScreen = { navController.navigate(ScreenPage.Pet.route) },
                onNavigateToUserScreen = { navController.navigate(ScreenPage.User.route) },
                petCuisineViewModel,
                onNavigateToPetCuisineDetail = { navController.navigate(ScreenPage.PetCuisineDetail.route) }
            )
        }
        composable(ScreenPage.PetCuisineDetail.route) {
            PetCuisineDetailScreen(
                petCuisineViewModel,
                onNavigateBack = { navController.navigateUp() }// 返回上一页
            )
        }
        composable(ScreenPage.Pet.route) {
            PetScreen(
                userViewModel,
                petViewModel,
                onNavigateToMainScreen = { navController.navigate(ScreenPage.Main.route) },
                onNavigateToKnowledgeScreen = { navController.navigate(ScreenPage.Knowledge.route) },
                onNavigateToPetScreen = { navController.navigate(ScreenPage.Pet.route) },
                onNavigateToUserScreen = { navController.navigate(ScreenPage.User.route) },
                onNavigateToPetUpdateScreen = { navController.navigate(ScreenPage.UpdatePet.route) },
                onNavigateToPetAddScreen = { navController.navigate(ScreenPage.AddPet.route) }
            )
        }
        composable(ScreenPage.AddPet.route) {
            PetAddScreen(
                userViewModel,
                petViewModel,
                onNavigateBack = { navController.navigateUp() }// 返回上一页
            )
        }
        composable(ScreenPage.UpdatePet.route) {
            PetUpdateScreen(
                petViewModel,
                onNavigateBack = { navController.navigateUp() }// 返回上一页
            )
        }
        composable(ScreenPage.User.route) {
            UserScreen(
                userViewModel,
                onNavigateToMainScreen = { navController.navigate(ScreenPage.Main.route) },
                onNavigateToKnowledgeScreen = { navController.navigate(ScreenPage.Knowledge.route) },
                onNavigateToPetScreen = { navController.navigate(ScreenPage.Pet.route) },
                onNavigateToUserScreen = { navController.navigate(ScreenPage.User.route) },
                onNavigatePopBackStack = { navController.popBackStack() }, // 清空返回栈
                onNavigateToLoginScreen = { navController.navigate(ScreenPage.Login.route) },
                onNavigateToPictureUpload = { navController.navigate(ScreenPage.UpLoadPicture.route) }
            )
        }
        composable(ScreenPage.UpLoadPicture.route) {
            UploadPictureScreen(
                userViewModel,
                onNavigateBack = { navController.navigateUp() }// 返回上一页
            )
        }
    }
}