package com.jichenhao.pettime_jichenhao.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.jichenhao.pettime_jichenhao.model.PetTimeRepository
import com.jichenhao.pettime_jichenhao.model.entity.Pet
import com.jichenhao.pettime_jichenhao.model.entity.PetCuisine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetCuisineViewModel @Inject constructor(private val petTimeRepository: PetTimeRepository) :
    ViewModel() {
    // 宠物食谱列表
    private var _petCuisineList = MutableStateFlow<List<PetCuisine>>(emptyList())

    val petCuisineList: StateFlow<List<PetCuisine>> get() = _petCuisineList

    // 点击卡片后暂存卡片内食物信息，在详情页取出
    private var _foodDetail =
        MutableStateFlow<PetCuisine>(
            PetCuisine(
                0, "", "",
                "", "", "", "", ""
            )
        )
    val foodDetail: StateFlow<PetCuisine> get() = _foodDetail

    fun setFoodDetail(foodDetail: PetCuisine) {
        _foodDetail.value = foodDetail
    }

    fun getAllPetCuisineList() {
        viewModelScope.launch {
            Log.d("petCuisine", "getList方法执行")
            val response = petTimeRepository.getAllPetCuisineList()
            response.asFlow().collect { result ->
                if (result.isSuccess) {
                    _petCuisineList.value = result.getOrNull()!!
                }
            }
        }
    }
}