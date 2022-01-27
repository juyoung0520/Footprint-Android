package com.footprint.footprint.ui.signin

import android.R.id
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import com.footprint.footprint.databinding.ActivitySigninBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.main.MainActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.footprint.footprint.data.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import android.R.id.text1
import androidx.constraintlayout.widget.ConstraintLayout
import com.footprint.footprint.R
import com.footprint.footprint.ui.register.RegisterActivity
import com.footprint.footprint.utils.*
import gun0912.tedimagepicker.util.ToastUtil.context


class SigninActivity : BaseActivity<ActivitySigninBinding>(ActivitySigninBinding::inflate) {

    val RC_SIGN_IN = -1
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var newUser: User
    override fun initAfterBinding() {
        initXML()

        //카카오 로그인
        binding.signinKakaologinBtnLayout.setOnClickListener {
            setKakaoLogin()
        }

        //다음에 로그인 할래요
        binding.signinNologinTv.setOnClickListener {
            this.startNextActivity(MainActivity::class.java)
            finish()
        }

        //구글 로그인
        val getResult = googleClient()
        binding.signinGoogleloginBtnLayout.setOnClickListener {
            getResult.launch(mGoogleSignInClient.signInIntent)
        }
    }

    /*XML 동적 Layout*/
    private fun initXML() {
        val density = context.resources?.displayMetrics?.density
        val statusbarHeight = getStatusBarHeightDP(this) + dp2px(density!!, 10)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, statusbarHeight, 0, 0) // 왼쪽, 위, 오른쪽, 아래 순서입니다.
        binding.signinTopLayout.layoutParams = params
    }

    //dp to px 변환 함수 (params)
    private fun dp2px(density:Float, dp: Int): Int {
        return Math.round(dp.toFloat() * density)
    }

    fun getStatusBarHeightDP(context: Context): Int {
        var result = 0
        val resourceId: Int = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimension(resourceId).toInt()
        }
        return result
    }

    /*Funtion-Kakao*/
    //로그인
    private fun setKakaoLogin() {
        //카카오 계정으로 로그인(콜백)
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("KAKAO/API-FAILURE", "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i("KAKAO/API-SUCCESS", "카카오계정으로 로그인 성공 ${token.accessToken}")
                getKakaoUser()
            }
        }

        //카카오톡으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@SigninActivity)) {
            UserApiClient.instance.loginWithKakaoTalk(this@SigninActivity) { token, error ->
                if (error != null) {
                    Log.e("KAKAO/API-FAILURE", "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(
                        this@SigninActivity,
                        callback = callback
                    )
                } else if (token != null) {
                    Toast.makeText(this, "카카오톡 로그인 완료", Toast.LENGTH_SHORT).show()
                    Log.i("KAKAO/API-SUCCESS", "카카오톡으로 로그인 성공 ${token.accessToken}")
                    getKakaoUser()
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this@SigninActivity, callback = callback)
        }
    }

    private fun getKakaoUser() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("KAKAO/USER-FAIL", "사용자 정보 요청 실패", error)
            } else if (user != null) {
                Log.i("KAKAO/USER-SUCCESS", "사용자 정보 요청 성공" + user.toString())
                val userIdx = user.id
                val username = user.kakaoAccount!!.profile!!.nickname
                val useremail = user.kakaoAccount!!.email

                newUser = User(
                    userIdx.toString(),
                    username.toString(),
                    useremail.toString(),
                )

                saveSpf("kakao")
                Log.d("KAKAO/USER", newUser.toString())

                startRegisterActivity()
            }
        }
    }

    private fun startRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.putExtra("user", newUser)
        startActivity(intent)
        finish()
    }


    /*Funtion - Google*/
    private fun googleClient(): ActivityResultLauncher<Intent> {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_login_server_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        val getResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RC_SIGN_IN) {
                //구글 로그인 성공
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                Log.d("GOOGLE/API-SUCCESS", "구글 로그인 성공")
                signupGoogle(task)
            }
        }
        return getResult
    }

    private fun signupGoogle(completedTask: Task<GoogleSignInAccount>) {
        try {
            //1. user 정보 저장
            val account = completedTask.getResult(ApiException::class.java)
            val userid = account?.id.toString()
            val username = account?.displayName.toString()
            val useremail = account?.email.toString()
            val token = account.idToken.toString()


            newUser = User(userid, username, useremail)

            Log.d("GOOGLE/USER", newUser.toString())
            Log.d("GOOGLE/USER-TOKEN", token)

            //2. spf & 회원가입 api 호출
            saveSpf("google")

            //3. Register 액티비티로 이동
            startRegisterActivity()
            finish()
        } catch (e: ApiException) {
            Log.w("GOOGLE/SIGNUP-FAILURE", "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun getUser() {
//        val acct = GoogleSignIn.getLastSignedInAccount(this@SigninActivity)
//        if (acct != null) {
//            val personName = acct.displayName
//            val personEmail = acct.email
//            val personId = acct.id
//            newUser = User(personId, personName, personEmail)
//            Log.d(
//                "GOOGLE/GETUSER",
//                "personName: ${personName} personEmail: ${personEmail} personId: ${personId}"
//            )
//            Log.d("GOOGLE/USER", newUser.toString())
//        }
    }

    private fun saveSpf(status: String){
        saveUserIdx(this, newUser.userIdx)
        saveLoginStatus(this, status)
        val userId = getUserIdx(this)
        val loginStatus = getLoginStatus(this)
        Log.d("SIGNUP/SPF-SUCCESS", "User Idx: ${userId} LoginStatus: ${loginStatus}")
    }
}