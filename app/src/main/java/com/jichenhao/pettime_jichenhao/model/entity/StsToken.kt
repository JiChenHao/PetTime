package com.jichenhao.pettime_jichenhao.model.entity

import java.util.Date

data class Credentials(
    val securityToken: String,
    val accessKeySecret: String,
    val accessKeyId: String,
    val expiration: String
)

data class StsAssumedRoleUser(
    val arn: String,
    val assumedRoleId: String
)

data class StsResult(// response中的data
    val requestId: String,
    val credentials: Credentials,
    val assumedRoleUser: StsAssumedRoleUser
)