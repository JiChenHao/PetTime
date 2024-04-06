package com.jichenhao.pettime_jichenhao.model.entity

// 从数据库中获得到的用户数据，也可用于在本地获取缓存，头像可以是null
data class UserInfo(val email: String, val password: String, val profile: String?)