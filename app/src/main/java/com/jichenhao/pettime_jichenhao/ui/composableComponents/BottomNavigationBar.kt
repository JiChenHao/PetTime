package com.jichenhao.pettime_jichenhao.ui.composableComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import com.jichenhao.pettime_jichenhao.ui.nav.ScreenPage

/**
 *底部导航栏，可以在任意界面引入，使用MyNavGraph所构建的导航图进行导航
 * @param selectedNum 默认选中的导航栏
 * @param onNavigateToMainScreen 跳转到主页
 * @param onNavigateToKnowledgeScreen 跳转到知识
 * @param onNavigateToPetScreen 跳转到宠物
 * @param onNavigateToUserScreen 跳转到用户
 */
@Composable
fun PetTimeBottomNavigationBar(
    selectedNum: Int,//用来更换图标
    onNavigateToMainScreen: () -> Unit,
    onNavigateToKnowledgeScreen: () -> Unit,
    onNavigateToPetScreen: () -> Unit,
    onNavigateToUserScreen: () -> Unit,
) {
    var hereSelectedNum by rememberSaveable {
        mutableIntStateOf(selectedNum)
    }
    //===END_定义跳转函数===

    //底部导航栏UI
    BottomAppBar(
        containerColor = Color.Black,
        actions = {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = {
                        //更改图标并且跳转内容页
                        hereSelectedNum = 1
                        onNavigateToMainScreen()
                    },
                    modifier = Modifier
                        .size(60.dp)
                ) {
                    Icon(
                        painterResource(id = if (hereSelectedNum == 1) ScreenPage.Main.iconSelect else ScreenPage.Main.iconUnselect),
                        contentDescription = "Localized description",
                        tint = Color.White,
                        modifier = Modifier.size(50.dp, 50.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp)) // 添加间隔以均匀分布
                IconButton(
                    onClick = { //更改图标并且跳转内容页
                        hereSelectedNum = 2
                        onNavigateToKnowledgeScreen()
                    },
                    modifier = Modifier
                        .size(60.dp)
                ) {
                    Icon(
                        painterResource(id = if (hereSelectedNum == 2) ScreenPage.Knowledge.iconSelect else ScreenPage.Knowledge.iconUnselect),
                        contentDescription = "宠物知识",
                        tint = Color.White,
                        modifier = Modifier.size(50.dp, 50.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp)) // 添加间隔以均匀分布

                IconButton(
                    onClick = { //更改图标并且跳转内容页
                        hereSelectedNum = 4
                        onNavigateToPetScreen()
                    },
                    modifier = Modifier
                        .size(60.dp)
                ) {
                    Icon(
                        painterResource(id = if (hereSelectedNum == 4) ScreenPage.Pet.iconSelect else ScreenPage.Pet.iconUnselect),
                        contentDescription = "Localized description",
                        tint = Color.White,
                        modifier = Modifier.size(50.dp, 50.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp)) // 添加间隔以均匀分布
                IconButton(
                    onClick = { //更改图标并且跳转内容页
                        hereSelectedNum = 5
                        onNavigateToUserScreen()
                    },
                    modifier = Modifier
                        .size(60.dp)
                ) {
                    Icon(
                        painterResource(id = if (hereSelectedNum == 5) ScreenPage.User.iconSelect else ScreenPage.User.iconUnselect),
                        contentDescription = "Localized description",
                        tint = Color.White,
                        modifier = Modifier.size(50.dp, 50.dp)
                    )
                }
            }
        },
        contentPadding = PaddingValues(horizontal = 16.dp),
    )
}

/**
* 另外，需要注意的一点是，如果跳转的目标路由地址不存在时，
* NavController会直接抛出IllegalArgumentException异常，导致应用崩溃，
* 因此在执行navigate方法时我们应该进行异常捕获，并给出用户提示：
* */
//封装定义一个跳转+报错的函数，方便重复调用
fun NavHostController.navigateWithCall(
    route: String,
    onNavigateFailed: ((IllegalArgumentException) -> Unit)?,
    builder: NavOptionsBuilder.() -> Unit
) {
    try {
        this.navigate(route, builder)
    } catch (e: IllegalArgumentException) {
        onNavigateFailed?.invoke(e)
    }
}