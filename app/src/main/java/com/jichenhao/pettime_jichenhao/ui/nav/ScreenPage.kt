package com.jichenhao.pettime_jichenhao.ui.nav

import androidx.annotation.StringRes
import com.jichenhao.pettime_jichenhao.R

sealed class ScreenPage(
    val route: String,
    @StringRes val resId: Int = 0, // 如果没有文字标题，就不需要使用这个属性
    val iconSelect: Int,
    val iconUnselect: Int,
) {
    object Splash : ScreenPage(
        route = "Splash",
        resId = 0,
        iconSelect = 0,
        iconUnselect = 0,
    )
    object Login : ScreenPage(
        route = "Login",
        resId = 0,
        iconSelect = 0,
        iconUnselect = 0,
    )

    object Register : ScreenPage(
        route = "Register",
        resId = 0,
        iconSelect = 0,
        iconUnselect = 0,
    )

    object Main : ScreenPage(
        route = "Main",
        resId = 0,
        iconSelect = R.drawable.ic_home_filled,
        iconUnselect = R.drawable.ic_home_line,
    )

    object Album : ScreenPage(
        route = "album",
        iconSelect = R.drawable.ic_album_filled,
        iconUnselect = R.drawable.ic_album_line,
    )

    object AlbumUploadOne : ScreenPage(
        route = "album_upload_one",
        resId = 0,
        iconSelect = 0,
        iconUnselect = 0,
    )

    object Knowledge : ScreenPage(
        route = "knowledge",
        iconSelect = R.drawable.ic_knowledge_filled,
        iconUnselect = R.drawable.ic_knowledge_line,
    )

    object PetCuisineDetail : ScreenPage(
        route = "pet_cuisine_detail",
        resId = 0,
        iconSelect = 0,
        iconUnselect = 0,
    )

    object Pet : ScreenPage(
        route = "pet",
        iconSelect = R.drawable.ic_pet_filled,
        iconUnselect = R.drawable.ic_pet_line,
    )

    object AddPet : ScreenPage(
        route = "add_pet",
        resId = 0,
        iconSelect = 0,
        iconUnselect = 0,
    )

    object UpdatePet : ScreenPage(
        route = "update_pet",
        resId = 0,
        iconSelect = 0,
        iconUnselect = 0,
    )


    object User : ScreenPage(
        route = "user",
        iconSelect = R.drawable.ic_user_filled,
        iconUnselect = R.drawable.ic_user_line,
    )

    object UpLoadPicture : ScreenPage(
        route = "up_load_picture",
        resId = 0,
        iconSelect = 0,
        iconUnselect = 0,
    )
}
