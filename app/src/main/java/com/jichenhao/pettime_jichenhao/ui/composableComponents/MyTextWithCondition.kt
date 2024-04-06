package com.jichenhao.pettime_jichenhao.ui.composableComponents

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 可以根据传入的数字判定自己是否处于被选中状态，从而表现出不同的样式
@Composable
fun MyTextWithCondition(
    selectNum: Int,// 如果传入的数字与他相同就是被选中了，否则就是没有被选中
    inputNum: Int,
    textToShow: String// 按钮上的文字
) {
    if (selectNum == inputNum) {
        Text(
            text = textToShow,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black
        )
    } else {
        Text(text = textToShow, style = MaterialTheme.typography.bodyLarge, color = Color.Black)
    }
}