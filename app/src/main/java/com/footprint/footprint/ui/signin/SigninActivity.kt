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
import com.footprint.footprint.ui.register.RegisterActivity
import com.footprint.footprint.utils.*


class SigninActivity : BaseActivity<ActivitySigninBinding>(ActivitySigninBinding::inflate) {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = -1
    private lateinit var newUser: UserModel

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
    //로그인
    private fun setKakaoLogin() {
        //카카오 계정으로 로그인
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("KAKAO/API-FAILURE", "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i("KAKAO/API-SUCCESS", "카카오계정으로 로그인 성공 ${token.accessToken}")
                signupKakao(token.accessToken)
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

                    signupKakao(token.accessToken)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this@SigninActivity, callback = callback)
        }
    }

    private fun signupKakao(token: String) {
        //1. User 정보 저장: token
        newUser = UserModel(token)

        //2. SPF에 로그인 상태 저장
        saveSpf("kakao")
        Log.d("KAKAO/USER", newUser.toString())

        //3. 존재하는 User인지 확인
        isExistUser()
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
            val username = account?.displayName.toString()
            val useremail = account?.email.toString()
            val idToken = account.idToken.toString()

            newUser = UserModel(idToken)

            Log.d("GOOGLE/ACCOUNT", "username: ${username} useremail: ${useremail}")
            Log.d("GOOGLE/USER", newUser.toString())
            Log.d("GOOGLE/USER-TOKEN", idToken)

            //2. spf
            saveSpf("google")

            //3. 존재하는 회원인지 확인
            isExistUser()
        } catch (e: ApiException) {
            Log.w("GOOGLE/SIGNUP-FAILURE", "signInResult:failed code=" + e.statusCode)
        }
    }

    /*Tools*/
    //SPF에 token, loginstatus 저장
    private fun saveSpf(status: String) {
        saveToken(this, newUser.idToken)
        saveLoginStatus(this, status)
        val token = getToken(this)
        val loginStatus = getLoginStatus(this)
        Log.d("SIGNUP/SPF-SUCCESS", "Token: ${token} LoginStatus: ${loginStatus}")
    }

    //회원 확인 요청 API
    private fun isExistUser() {
        /*서버에 존재하는 회원인지?*/

        //O-> MainActivity
        //startMainActivity()

        //X-> RegisterActivity
        startRegisterActivity()
    }

    //Register Activity
    private fun startRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.putExtra("user", newUser)
        startActivity(intent)
        finish()
    }

    //Main Activity
    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}