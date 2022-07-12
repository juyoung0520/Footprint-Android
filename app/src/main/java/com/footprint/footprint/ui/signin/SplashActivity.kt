package com.footprint.footprint.ui.signin

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.Observer
import com.footprint.footprint.BuildConfig
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivitySplashBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.main.MainActivity
import com.footprint.footprint.ui.onboarding.OnBoardingActivity
import com.footprint.footprint.utils.*
import com.footprint.footprint.viewmodel.SplashViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate){

    private val splashVm: SplashViewModel by viewModel()
    private val MY_REQUEST_CODE = 1524
    private lateinit var networkErrSb: Snackbar

    override fun initAfterBinding() {
        checkUpdate() // 업데이트 확인
        observe()
    }

    // 업데이트 확인
    private fun checkUpdate(){
        val version = BuildConfig.VERSION_NAME
        splashVm.getVersion(version)
    }

    // 자동 로그인
    private fun autoLogin() {
        if (getJwt() != null) { // O -> 자동로그인 API 호출
            splashVm.autoLogin()
        } else {  // X -> 로그인 액티비티
            startSignInActivity()
        }
    }

    // 업데이트 다이얼로그 띄우기
    private fun showUpdateDialog(version: String){
        val msg = "발자국 ${version}이 업데이트 되었습니다. \n원활한 서비스 이용을 위해 업데이트를 진행해 주세요."
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.title_update))
            .setMessage(msg)
            .setPositiveButton(getString(R.string.action_store)) { _, _ -> goAppStore() }
            .setOnDismissListener {
                Snackbar.make(binding.root, getString(R.string.msg_denied_update), Snackbar.LENGTH_INDEFINITE).setAction(
                    R.string.action_retry) {
                    goAppStore()
                }.show()
            }
            .create()
            .show()
    }

    private fun goAppStore(){
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(
                "https://play.google.com/store/apps/details?id=com.footprint.footprint")
            setPackage("com.android.vending")
        }
        startActivityForResult(intent, MY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {  // update failed or cancelled

                Snackbar.make(binding.root, getString(R.string.msg_denied_update), Snackbar.LENGTH_INDEFINITE).setAction(
                    R.string.action_retry) {
                    goAppStore()
                }.show()

            }
        }
    }

    /*액티비티 이동*/
    //Main Activity
    private fun startMainActivity(badgeCheck: Boolean) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("badgeCheck", badgeCheck)
        startActivity(intent)
        finish()
    }

    //SignIn Activity
    private fun startSignInActivity() {
        startNextActivity(SigninActivity::class.java)
        finish()
    }

    //OnBoarding Activity
    private fun startOnBoardingActivity() {
        startNextActivity(OnBoardingActivity::class.java)
        finish()
    }

    private fun observe(){
        splashVm.mutableErrorType.observe(this, androidx.lifecycle.Observer {
            when (it) {
                ErrorType.JWT -> { //JWT 관련 에러 발생 시, jwt 지우고 로그인 액티비티로 이동
                    removeJwt()
                    startSignInActivity()
                }
                ErrorType.NETWORK -> {
                    networkErrSb = Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.action_retry)) { checkUpdate() }
                    networkErrSb.show()
                }
                ErrorType.UNKNOWN, ErrorType.DB_SERVER -> {
                    showToast(getString(R.string.error_sorry))
                    onBackPressed()
                }
            }
        })

        splashVm.thisLogin.observe(this, Observer {
            when(it.status){
                "ACTIVE" -> {   // 가입된 회원
                    startMainActivity(it.checkMonthChanged)
                }
                "ONGOING" -> { // 가입이 완료되지 않은 회원 -> 로그인 액티비티
                    startSignInActivity()
                }
            }
        })

        splashVm.thisVersion.observe(this, Observer {
            if(it.whetherUpdate) { // true -> 강제 업데이트
                showUpdateDialog(it.serverVersion)
            }
            else { // false -> 온보딩 or 로그인
                if (!getOnboarding()) { // false -> 온보딩 액티비티
                    startOnBoardingActivity()
                } else { // true -> 자동 로그인
                    autoLogin()
                }
            }

        })
    }

    override fun onStop() {
        super.onStop()

        if (::networkErrSb.isInitialized && networkErrSb.isShown)
            networkErrSb.dismiss()
    }
}
