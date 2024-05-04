package com.jichenhao.pettime_jichenhao.model.entity

data class UserWithJwtToken(var email: String, var password:String,var profile: String?, var jwtToken:String)
