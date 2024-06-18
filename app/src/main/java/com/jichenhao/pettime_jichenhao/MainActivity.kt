package com.jichenhao.pettime_jichenhao

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jichenhao.pettime_jichenhao.ui.composableComponents.PetTimeBottomNavigationBar
import com.jichenhao.pettime_jichenhao.ui.nav.NavGraph
import com.jichenhao.pettime_jichenhao.ui.nav.ScreenPage
import com.jichenhao.pettime_jichenhao.ui.screens.LoginScreen
import com.jichenhao.pettime_jichenhao.ui.theme.PetTime_jichenhaoTheme
import com.jichenhao.pettime_jichenhao.viewModel.AlbumViewModel
import com.jichenhao.pettime_jichenhao.viewModel.PetCuisineViewModel
import com.jichenhao.pettime_jichenhao.viewModel.PetViewModel
import com.jichenhao.pettime_jichenhao.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // 创建ViewModel实例并且保证它全局只有一个实例
    @Singleton
    private val userViewModel: UserViewModel by viewModels()

    @Singleton
    private val petViewModel: PetViewModel by viewModels()

    @Singleton
    private val petCuisineViewModel: PetCuisineViewModel by viewModels()

    @Singleton
    private val albumViewModel: AlbumViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel.fetchStsToken()
        setContent {
            PetTime_jichenhaoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 全局唯一的navController
                    val navController = rememberNavController()
                    var selectNumber by remember {
                        mutableIntStateOf(1)
                    }
                    //navController.currentBackStackEntryAsState()得到一个状态对象，
                    // 它会随着当前导航堆栈顶部变更而更新。这个状态对象包含了一个NavBackStackEntry的实时值
                    // NavBackStackEntry包含了关于当前页面的各种信息，如路由、标签、参数等
                    val currentBackStackEntry by navController.currentBackStackEntryAsState()
                    // 根据当前路由来判断是否显示底部导航栏
                    val showBottomBar = when (currentBackStackEntry?.destination?.route) {
                        ScreenPage.Main.route -> true
                        ScreenPage.Pet.route -> true
                        ScreenPage.Knowledge.route -> true
                        ScreenPage.User.route -> true
                        else -> false// 其他页面默认不显示
                    }
                    Scaffold(
                        bottomBar = {
                            if (showBottomBar) {
                                PetTimeBottomNavigationBar(
                                    selectedNum = selectNumber,
                                    onNavigateToMainScreen = {
                                        selectNumber = 1
                                        navController.navigate(ScreenPage.Main.route)
                                    },
                                    onNavigateToKnowledgeScreen = {
                                        selectNumber = 2
                                        navController.navigate(ScreenPage.Knowledge.route)
                                    },
                                    onNavigateToPetScreen = {
                                        selectNumber = 4
                                        navController.navigate(ScreenPage.Pet.route)
                                    },
                                    onNavigateToUserScreen = {
                                        selectNumber = 5
                                        navController.navigate(ScreenPage.User.route)
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            NavGraph(
                                navController,
                                albumViewModel,
                                userViewModel,
                                petViewModel,
                                petCuisineViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PetTime_jichenhaoTheme {
        Greeting("Android")
    }
}