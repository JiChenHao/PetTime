package com.jichenhao.pettime_jichenhao.model.entity

data class Pet(
    val petId: Int,
    val petName: String,
    val petSex: Boolean,
    val petBreeds: String,
    val petAge: Int,
    val userEmail: String,
    val profile: String?
)