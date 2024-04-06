package com.jichenhao.pettime_jichenhao.model.entity


// 用户相册中的图片对象，包括图像id（自动生成），图像链接，用户email
data class PictureInfo(val picId: Int, val picUrl: String, val userEmail: String)