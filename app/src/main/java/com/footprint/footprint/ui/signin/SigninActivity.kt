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
import com.footprint.footprint.data.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


class SigninActivity : BaseActivity<ActivitySigninBinding>(ActivitySigninBinding::inflate) {

    val RC_SIGN_IN = -1
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var newUser: User
    override fun initAfterBinding() {
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


    /*Funtion-Kakao*/
    //로그인
    private fun setKakaoLogin() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("KAKAO/API-FAILURE", "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i("KAKAO/API-SUCCESS", "카카오계정으로 로그인 성공 ${token.accessToken}")
                signupKakao()
            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
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
                    Log.i("KAKAO/API-SUCCESS", "카카오톡으로 로그인 성공 ${token.accessToken}")
                    signupKakao()
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
                val nickname = user.kakaoAccount?.profile?.nickname
                val email = user.kakaoAccount?.email
                val gender = user.kakaoAccount?.gender
                val birthday = user.kakaoAccount?.birthday

                newUser = User(
                    userIdx.toString(),
                    nickname.toString(),
                    email.toString(),
                    gender.toString(),
                    birthday.toString()
                )
                Log.d("KAKAO/USER", newUser.toString())
            }
        }
    }

    private fun signupKakao() {
        //1. User 정보 받아오기
        getKakaoUser()

        //2. 회원가입 api 호출

        //3. 액티비티 이동
        this.startNextActivity(MainActivity::class.java)
        finish()
    }

    /*Funtion - Google*/
    private fun googleClient(): ActivityResultLauncher<Intent> {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
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
                Toast.makeText(this, "구글 로그인 완료", Toast.LENGTH_SHORT).show()
                signupGoogle(task)
            }
        }
        return getResult
    }

    private fun signupGoogle(completedTask: Task<GoogleSignInAccount>) {
        try {
            //1. user 정보 저장
            val account = completedTask.getResult(ApiException::class.java)
            val email = account?.email.toString()
            val displayName = account?.displayName.toString()
            val token = account?.idToken.toString()
            val scope = account?.grantedScopes.toString()
            val id = account?.id.toString()

            newUser = User(id, displayName, email)

            Log.d("GOOGLE/USER-EMAIL", email)
            Log.d("GOOGLE/USER-NAME", displayName)
            Log.d("GOOGLE/USER-ID", id)
            Log.d("GOOGLE/USER-TOKEN", token)
            Log.d("GOOGLE/USER-SCOPE", scope)

            //2. 회원가입 api 호출


            //3. 메인 액티비티로 이동
            startNextActivity(MainActivity::class.java)
            finish()
        } catch (e: ApiException) {
            Log.w("failed", "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun getUser() {
        val acct = GoogleSignIn.getLastSignedInAccount(this@SigninActivity)
        if (acct != null) {
            val personName = acct.displayName
            val personEmail = acct.email
            val personId = acct.id
            newUser = User(personId, personName, personEmail)
            Log.d(
                "GOOGLE/GETUSER",
                "personName: ${personName} personEmail: ${personEmail} personId: ${personId}"
            )
            Log.d("GOOGLE/USER", newUser.toString())
        }
    }
}