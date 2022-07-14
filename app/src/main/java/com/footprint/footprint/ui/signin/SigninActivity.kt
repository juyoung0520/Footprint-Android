package com.footprint.footprint.ui.signin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.footprint.footprint.databinding.ActivitySigninBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.main.MainActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import com.footprint.footprint.BuildConfig
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.footprint.footprint.R
import com.footprint.footprint.domain.model.SocialUserModel
import com.footprint.footprint.ui.agree.AgreeActivity
import com.footprint.footprint.ui.error.ErrorActivity
import com.footprint.footprint.utils.*
import com.footprint.footprint.viewmodel.SignInViewModel
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes.SIGN_IN_CANCELLED
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class SigninActivity : BaseActivity<ActivitySigninBinding>(ActivitySigninBinding::inflate){

    private val signInVm: SignInViewModel by viewModel()
    private lateinit var networkErrSb: Snackbar
    private lateinit var getResult: ActivityResultLauncher<Intent>

    lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var getGoogleResult: ActivityResultLauncher<Intent>
    private lateinit var socialUserModel: SocialUserModel

    private var doubleBackToExit = false //뒤로가기 두 번 눌러야 종료 확인하는 변수

    override fun initAfterBinding() {
        setClickListener()
        observe()
    }

    private fun setClickListener() {
        //카카오 로그인
        binding.signinKakaologinBtnLayout.setOnClickListener {
            setKakaoLogin()
        }

        //구글 로그인
        binding.signinGoogleloginBtnLayout.setOnClickListener {
            getGoogleResult.launch(mGoogleSignInClient.signInIntent)
        }
    }

    /*Funtion-Kakao*/
    private fun setKakaoLogin() {
        //카카오 계정으로 로그인
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                LogUtils.e("KAKAO/API-FAILURE", "카카오계정으로 로그인 실패", error)
                
                //사용자가 취소를 눌렀을 경우를 제외하고 에러 스낵바 띄움
                if (error is ClientError && error.reason != ClientErrorCause.Cancelled)
                    signinErrorCheck("KAKAO")

            } else if (token != null) {
                LogUtils.i("KAKAO/API-SUCCESS", "카카오계정으로 로그인 성공)")
                getKakaoUser()
            }
        }

        //카카오톡으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@SigninActivity)) {
            UserApiClient.instance.loginWithKakaoTalk(this@SigninActivity) { token, error ->
                if (error != null) {
                    LogUtils.e("KAKAO/API-FAILURE", "카카오톡으로 로그인 실패", error)

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
                    LogUtils.i("KAKAO/API-SUCCESS", "카카오톡으로 로그인 성공 ${token.accessToken}")

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
                LogUtils.e("KAKAO/USER-FAILURE", "사용자 정보 요청 실패", error)
            } else if (user != null) {
                LogUtils.i("KAKAO/USER-SUCCESS", "사용자 정보 요청 성공")

                val userId: String = user.id.toString()
                val nickname: String = user.kakaoAccount?.profile?.nickname!!
                val email: String? = user.kakaoAccount?.email

                //1. User 정보 등록
                socialUserModel = SocialUserModel(userId, nickname, email!!, "kakao")

                //2. 로그인 API
                callSignInAPI()
            }

        }
    }

    /*Function - Google*/
    private fun initGoogleResult(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.google_login_server_id)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        getGoogleResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->

            try{
                GoogleSignIn.getSignedInAccountFromIntent(result.data).getResult(ApiException::class.java)
            } catch (apiException: ApiException) {
                //사용자가 취소를 눌렀을 경우를 제외하고 에러 스낵바 띄움
                if(apiException.statusCode != SIGN_IN_CANCELLED) {
                    signinErrorCheck("GOOGLE")
                }
            }finally {
                if (result.resultCode == RESULT_OK) {
                    LogUtils.d("GOOGLE/API-SUCCESS", "구글 로그인 성공")
                    
                    //구글 로그인 성공
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    getGoogleUser(task)
                }
            }
        }
    }
    private fun getGoogleUser(completedTask: Task<GoogleSignInAccount>) {
        try {

            //1. user 정보 등록
            val account = completedTask.getResult(ApiException::class.java)
            val username = account?.displayName.toString()
            val useremail = account?.email.toString()
            val userid = account.id.toString()

            socialUserModel = SocialUserModel(userid, username, useremail, "google")

            //2. 로그인 API
            callSignInAPI()

        } catch (e: ApiException) {
            LogUtils.d("GOOGLE/SIGNUP-FAILURE", "signInResult:failed code=" + e.statusCode)
        }
    }

    /*로그인 API*/
    private fun callSignInAPI(){
        signInVm.login(socialUserModel)
    }

    /*액티비티 이동*/
    //Main Activity
    private fun startMainActivity(badgeCheck: Boolean) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("badgeCheck", badgeCheck)
        startActivity(intent)
        finish()
    }

    //Agree Activity
    private fun startAgreeActivity() {
        startNextActivity(AgreeActivity::class.java)
        finish()
    }

    /*에러 체크*/
    private fun signinErrorCheck(type: String){

        if(!isNetworkAvailable(this)){ //네트워크 에러
            networkErrSb = Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE)
            when(type){
                "KAKAO" -> {
                    networkErrSb.setAction(getString(R.string.action_retry)) { setKakaoLogin() }
                }
                "GOOGLE" -> {
                    networkErrSb.setAction(getString(R.string.action_retry)) { getGoogleResult.launch(mGoogleSignInClient.signInIntent) }
                }
                "LOGIN" -> {
                    networkErrSb.setAction(getString(R.string.action_retry)) { signInVm.login(socialUserModel) }
                }
            }

            networkErrSb.show()
        }else{ /* UNKNOWN, DB_SERVER */
            startErrorActivity(getResult, "SignInActivity")
        }

    }

    /*Observe*/
    private fun observe(){
        signInVm.mutableErrorType.observe(this, androidx.lifecycle.Observer {
            signinErrorCheck("LOGIN")
        })

        signInVm.thisLogin.observe(this, Observer{
            //1. spf에 jwtId 저장, 로그인 상태 저장
            val jwt = signInVm.thisLogin.value!!.jwtId
            saveJwt(jwt)
            saveLoginStatus(socialUserModel.providerType)

            //2. STATUS에 따른 처리
            // ACTIVE: 가입된 회원 -> 뱃지 API 호출
            // ONGOING: 가입 안된 회원/정보등록 안된 회원, Register Activity로
            when(it.status){
                "ACTIVE" -> {   // 가입된 회원
                    startMainActivity(it.checkMonthChanged)
                }
                "ONGOING" -> { // 가입이 안된 회원 -> 회원가입 액티비티
                    startAgreeActivity()
                }
            }
        })
    }

    /*에러 처리*/
    private fun initActivityResult() {
        getResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if(result.resultCode == ErrorActivity.RETRY){
                callSignInAPI()
            }
        }
    }

    /*백버튼 처리: 두 번 누르면 앱 종료*/
    override fun onBackPressed() {
        if (doubleBackToExit) {
            finishAffinity()
        } else {
            Toast.makeText(this, "종료하려면 뒤로가기를 한번 더 눌러주세요.", Toast.LENGTH_SHORT).show()
            doubleBackToExit = true
            runDelayed(1500L) {
                doubleBackToExit = false
            }
        }
    }

    private fun runDelayed(millis: Long, function: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed(function, millis)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initActivityResult()
        initGoogleResult()
    }

    override fun onStop() {
        super.onStop()

        if (::networkErrSb.isInitialized && networkErrSb.isShown)
            networkErrSb.dismiss()
    }


}