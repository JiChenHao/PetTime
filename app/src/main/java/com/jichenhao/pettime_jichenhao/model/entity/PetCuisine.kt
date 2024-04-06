package com.jichenhao.pettime_jichenhao.model.entity

data class PetCuisine(
    val id: Int,
    val food_name: String,
    val food_type: String,// ENUM('水果', '零食甜点', '饮料', '坚果', '主食', '蔬菜', '肉类')
    val image_url: String,
    val dog_eat: String,// ENUM('能吃', '慎吃', '不能吃')
    val dog_suggestion: String,
    val cat_eat: String,// ENUM('能吃', '慎吃', '不能吃')
    val cat_suggestion: String,
    )