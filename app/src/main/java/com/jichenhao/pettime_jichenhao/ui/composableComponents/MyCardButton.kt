package com.jichenhao.pettime_jichenhao.ui.composableComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// 略微好看一点的卡片形式的按钮，被选中会改变样式和文字颜色
@Composable
fun MyCardButton(
    selectNum: Int,// 如果传入的数字与他相同就是被选中了，否则就是没有被选中
    inputNum: Int, textToShow: String// 按钮上的文字
) {
    if (selectNum == inputNum) {
        // 选中状态：彩色文字+卡片按钮
        Card(
            modifier = Modifier
                .width(90.dp)
                .height(40.dp),
        ) {
            Text(
                text = textToShow,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black,
                modifier = Modifier
                    .align(
                        Alignment.CenterHorizontally
                    )
            )
        }
    } else {
        Box(
            modifier = Modifier
                .width(90.dp)
                .height(40.dp),

            ) {
            Text(
                text = textToShow,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                modifier = Modifier
                    .align(
                        Alignment.Center
                    )
            )
        }


    }
}