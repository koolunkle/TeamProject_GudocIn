package com.neppplus.gudocin_android.ui.activity

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.neppplus.gudocin_android.R
import com.neppplus.gudocin_android.databinding.ActivityMainBinding
import com.neppplus.gudocin_android.model.BasicResponse
import com.neppplus.gudocin_android.ui.fragment.HomeFragment
import com.neppplus.gudocin_android.ui.fragment.ProductFragment
import com.neppplus.gudocin_android.ui.fragment.ProfileFragment
import com.neppplus.gudocin_android.utils.ContextUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.system.exitProcess

class MainActivity : BaseActivity() {

  lateinit var binding: ActivityMainBinding

  var backKeyPressedTime: Long = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    setupEvents()
    setValues()
  }

  override fun setupEvents() {
    binding.bottomNav.setOnItemSelectedListener {
      when (it.itemId) {
        R.id.navHome -> binding.viewPager.currentItem = 0
        R.id.navReview -> binding.viewPager.currentItem = 1
        else -> binding.viewPager.currentItem = 2
      }
      true
    }
  }

  override fun setValues() {
    getDeviceToken()
    getKeyHash()

    binding.viewPager.apply {
      adapter = ViewPagerAdapter(this@MainActivity)
      registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
          super.onPageSelected(position)
          binding.bottomNav.selectedItemId = when (position) {
            0 -> R.id.navHome
            1 -> R.id.navReview
            else -> R.id.navProfile
          }
        }
      })
    }
//        3장의 화면을 계속 유지하도록
    binding.viewPager.offscreenPageLimit = 3
    btnBack.visibility = View.GONE
  }

  override fun onBackPressed() {
    if (System.currentTimeMillis() - backKeyPressedTime >= 1500) {
      backKeyPressedTime = System.currentTimeMillis()
      Toast.makeText(this, "백버튼을 한번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show()
    } else {
      ActivityCompat.finishAffinity(this)
      System.runFinalization()
      exitProcess(0)
    }
  }

  inner class ViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount() = 3

    private val fragmentList = arrayListOf(HomeFragment(), ProductFragment(), ProfileFragment())

    override fun createFragment(position: Int): Fragment {
      return fragmentList[position]
    }

    fun getFragment(position: Int) = fragmentList[position]
  }

  fun viewpager(view: View) {
    view.parent.requestDisallowInterceptTouchEvent(true)
    false // setOnTouchListener
  }

  fun navigation(item: MenuItem) {
    when (item.itemId) {
      R.id.navHome -> binding.viewPager.currentItem = 0
      R.id.navReview -> binding.viewPager.currentItem = 1
      else -> binding.viewPager.currentItem = 2
    }
    true // setOnItemSelectedListener
  }

  private fun getDeviceToken() {
    if (ContextUtil.getDeviceToken(mContext) != "") {
      apiService.patchRequestUpdateUserInfo(
        "android_device_token",
        ContextUtil.getDeviceToken(mContext)
      ).enqueue(object : Callback<BasicResponse> {
        override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {

        }

        override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

        }
      })
    }
  }

  @RequiresApi(Build.VERSION_CODES.P)
  private fun getKeyHash() {
    try {
      val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
      val signature = info.signingInfo.apkContentsSigners

      for (signature in signature) {
        val md = MessageDigest.getInstance("SHA")
        md.update(signature.toByteArray())
        Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
      }
    } catch (e: PackageManager.NameNotFoundException) {
      e.printStackTrace()
    } catch (e: NoSuchAlgorithmException) {
      e.printStackTrace()
    }
  }

}