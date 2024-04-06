package com.jichenhao.pettime_jichenhao

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.jichenhao.pettime_jichenhao.ui.nav.NavGraph
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
                    NavGraph(
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