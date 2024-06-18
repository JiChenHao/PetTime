package com.jichenhao.pettime_jichenhao.ui.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

/**
 * 用来选择图片，创建并获取图片的句柄
 * @author: 吉晨豪
 */
class SelectPicture : ActivityResultContract<Unit?, PictureResult>() {

    private var context: Context? = null

    override fun createIntent(context: Context, input: Unit?): Intent {
        this.context = context
        return Intent(Intent.ACTION_PICK).setType("image/*")
    }

    override fun parseResult(resultCode: Int, intent: Intent?): PictureResult {
        return PictureResult(intent?.data, resultCode == Activity.RESULT_OK)
    }
}

//图片结果
class PictureResult(val uri: Uri?, val isSuccess: Boolean)