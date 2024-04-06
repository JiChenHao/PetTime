package com.jichenhao.pettime_jichenhao

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jichenhao.pettime_jichenhao.model.entity.UserInfo
import com.jichenhao.pettime_jichenhao.model.network.PetTimeNetwork
import com.jichenhao.pettime_jichenhao.model.network.ServiceCreator
import com.jichenhao.pettime_jichenhao.model.network.service.StsApi
import com.jichenhao.pettime_jichenhao.model.network.service.UserService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import retrofit2.await

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.jichenhao.pettime_jichenhao", appContext.packageName)


        val stsService = ServiceCreator.create(StsApi::class.java)
        GlobalScope.launch {
            val stsResponse = PetTimeNetwork.fetchStsToken()
            println("test的结果是：${stsResponse.success}")
        }
    }
}