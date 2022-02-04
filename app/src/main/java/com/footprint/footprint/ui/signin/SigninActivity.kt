package com.footprint.footprint.ui.signin

import android.content.Intent
import android.util.Log
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
import com.footprint.footprint.data.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.footprint.footprint.R
import com.footprint.footprint.data.model.SocialUserModel
import com.footprint.footprint.data.remote.auth.AuthService
import com.footprint.footprint.data.remote.auth.Login
import com.footprint.footprint.ui.register.RegisterActivity
import com.footprint.footprint.utils.*


class SigninActivity : BaseActivity<ActivitySigninBinding>(ActivitySigninBinding::inflate),
    SignInView {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = -1
    private lateinit var newUser: SocialUserModel

    override fun initAfterBinding() {
        //카카오 로그인
        binding.signinKakaologinBtnLayout.setOnClickListener {
            setKakaoLogin()
        }

        //구글 로그인
        val getResult = googleClient()
        binding.signinGoogleloginBtnLayout.setOnClickListener {
            getResult.launch(mGoogleSignInClient.signInIntent)
        }

        //다음에 로그인 할래요 -> RegisterActivity 로 이동
        binding.signinNologinTv.setOnClickListener {
            this.startNextActivity(RegisterActivity::class.java)
            finish()
        }
    }


    /*Funtion-Kakao*/
    private fun setKakaoLogin() {
        //카카오 계정으로 로그인
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
                Log.e("KAKAO/USER-FAILURE", "사용자 정보 요청 실패", error)
            } else if (user != null) {
                Log.i(
                    "KAKAO/USER-SUCCESS", "사용자 정보 요청 성공" +
                            "\n회원번호: ${user.id}" +
                            "\n이메일: ${user.kakaoAccount?.email}" +
                            "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                            "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}"
                )

                val userId: String = user.id.toString()
                val nickname: String? = user.kakaoAccount?.profile?.nickname
                val email: String? = user.kakaoAccount?.email

                //1. User 정보 등록
                newUser = SocialUserModel(userId, nickname, email)
                Log.d("KAKAO/USER", newUser.toString())

                //2. 로그인 API
                callSignInAPI("kakao")
            }

        }
    }

    /*Function - Google*/
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
                getGoogleUser(task)
            }
        }
        return getResult
    }

    private fun getGoogleUser(completedTask: Task<GoogleSignInAccount>) {
        try {

            //1. user 정보 등록
            val account = completedTask.getResult(ApiException::class.java)
            val username = account?.displayName.toString()
            val useremail = account?.email.toString()
            val idToken = account.idToken.toString()

            newUser = SocialUserModel(idToken, username, useremail)
            Log.d("GOOGLE/USER", newUser.toString())

            //2. 로그인 API
            callSignInAPI("google")

        } catch (e: ApiException) {
            Log.w("GOOGLE/SIGNUP-FAILURE", "signInResult:failed code=" + e.statusCode)
        }
    }

    /*로그인 API*/
    private fun callSignInAPI(provideType: String){
        AuthService.login(this, newUser.userId!!, newUser.username!!, newUser.email!!, provideType)
    }

    /*-> Response */
    override fun onSignInLoading() {}

    override fun onSignInSuccess(result: Login) {
        Log.d("SIGNIN/API-SUCCESS", "status: ${result.status}")

        val jwtId = result.jwtId
        val status = result.status

        //1. spf에 jwtId 저장
        saveJwt(jwtId)

        //3. STATUS에 따른 처리 (0) 존재하는 회원 -> MainActivity (1) 존재하지 않는 회원 -> RegisterActivity (2) 에러 -> RegisterActivity
        when (status) {
            "exist" -> startMainActivity()
            "non-exist" -> startRegisterActivity()
        }
    }


    override fun onSignInFailure(code: Int, message: String) {
        Log.d("SIGNIN/API-FAILURE", "code: $code message: $message")
    }

    /*액티비티 이동*/
    //Main Activity
    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    //Register Activity
    private fun startRegisterActivity() {
        startActivity(Intent(this, RegisterActivity::class.java))
        finish()
    }
}