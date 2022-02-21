package com.footprint.footprint.ui.lock

import androidx.navigation.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivityLockBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.adapter.LockRVAdapter
import com.footprint.footprint.utils.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LockActivity() : BaseActivity<ActivityLockBinding>(ActivityLockBinding::inflate) {
    private lateinit var lockRVAdapter: LockRVAdapter

    private val numbers: ArrayList<Int> = arrayListOf() //실시간으로 입력받는 숫자
    private var password: String? = null                //4개 입력되면 numbers -> string
    private var tmpPassword: String? = null             //암호 설정,변경,재설정에서 확인 위해 임시로 담아두는 password

    private var mode: String? = "SETTING"               //mode: SETTING, CHANGE, UNLOCK (기능별)
    private var type: String? = null                    //type: SETTING, CHANGE, CHECKING, UNLOCK (함수별)

    override fun initAfterBinding() {
        //SettingFragment 또는 CalendarBlankFragment 를 통해 현재 암호 모드를 전달 받는다.
        val navArgs: LockActivityArgs by navArgs()
        mode = navArgs.mode

        /*화면 종류별*/
        when (mode) {
            "SETTING" -> { //1. 암호 설정
                pwdSettingUI()
                type = "SETTING"
            }

            "CHANGE" -> { //2. 암호 변경
                pwdUnlockUI("CHECK")
                type = "UNLOCK"
            }

            "UNLOCK" -> { //3. 잠금 해제
                pwdUnlockUI("DEFAULT")
                type = "UNLOCK"
            }
        }

        initRV()
        initBackBtn()
    }

    /*화면 init*/
    //Back 버튼
    private fun initBackBtn() {
        binding.lockBackBtnIv.setOnClickListener {
            saveCrackStatus("CANCEL")
            finish()
        }
    }

    //Number 리사이클러뷰
    private fun initRV() {
        //item Width
        val widthPx = getDeviceWidth() - convertDpToPx(this, 24 * 2) //디바이스 넓이 - 양 옆 마진(24*2)
        val itemWidthPx = widthPx / 3

        lockRVAdapter = LockRVAdapter(itemWidthPx)
        binding.lockNumberRv.adapter = lockRVAdapter
        binding.lockNumberRv.layoutManager =
            GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false)

        lockRVAdapter.setItemClickListener(object : LockRVAdapter.OnItemClickListener {
            override fun onClick(data: Int) {
                changeFootprintUI(data)
            }
        })
    }

    /*Number -> Footprint UI 변경*/
    private fun changeFootprintUI(number: Int) {
        var num = number

        //200 = remove
        if (number == 200) {
            // 1, 2, 3, 4인 경우 -> 마지막 지우기
            if (numbers.size >= 1) {
                numbers.removeLast()
                if (numbers.isNotEmpty()) num = numbers.last() // 2, 3, 4
            }
        } else numbers.add(num)


        //Footprint UI 바꿔주기
        when (numbers.size) {
            0 -> {
                setFootprint(1, false)
                setFootprint(2, false)
                setFootprint(3, false)
                setFootprint(4, false)
            }

            1 -> {
                setFootprint(1, true)
                setFootprint(2, false)
                setFootprint(3, false)
                setFootprint(4, false)
            }

            2 -> {
                setFootprint(2, true)
                setFootprint(3, false)
                setFootprint(4, false)
            }

            3 -> {
                setFootprint(3, true)
                setFootprint(4, false)
            }

            4 -> {
                setFootprint(4, true)

                password = numbers.joinToString("")
                function(type!!)
            }
        }

    }

    private fun setFootprint(index: Int, status: Boolean) {
        when (index) {
            //Footprint 1
            1 -> {
                if (status)
                    binding.lockFootprint1Iv.setImageResource(R.drawable.ic_lock_footprint_on)
                else
                    binding.lockFootprint1Iv.setImageResource(R.drawable.ic_lock_footprint_off)
            }

            //Footprint 2
            2 -> {
                if (status)
                    binding.lockFootprint2Iv.setImageResource(R.drawable.ic_lock_footprint_on)
                else
                    binding.lockFootprint2Iv.setImageResource(R.drawable.ic_lock_footprint_off)
            }

            //Footprint 3
            3 -> {
                if (status)
                    binding.lockFootprint3Iv.setImageResource(R.drawable.ic_lock_footprint_on)
                else
                    binding.lockFootprint3Iv.setImageResource(R.drawable.ic_lock_footprint_off)
            }

            //Footprint 4
            4 -> {
                if (status)
                    binding.lockFootprint4Iv.setImageResource(R.drawable.ic_lock_footprint_on)
                else
                    binding.lockFootprint4Iv.setImageResource(R.drawable.ic_lock_footprint_off)
            }
        }
    }


    /*기능별 함수(type): SETTING(암호 설정), CHECKING(암호 확인), UNLOCK(잠금 해제)
    * SETTING  - 암호 설정/변경 위해 password -> tmpPassword 저장
    * CHECKING - 암호 확인 위해 password == tmpPassword 비교
    * UNLOCK   - 잠금 해제 위해 password == spf 비교
    *  */
    private fun function(inputType: String) {
        when (inputType) {
            "SETTING", "CHANGE" -> {
                //확인 UI로 변경
                tmpPassword = pwdSettingFunction()
                pwdCheckingUI()
                type = "CHECKING"

                LogUtils.d("LOCK/SET", "Type: $type Password: $password TmpPwd: $tmpPassword")
            }

            "CHECKING" -> {
                //tmp_pwd와 비교
                LogUtils.d("LOCK/CHECK", "Type: $type Password: $password TmpPwd: $tmpPassword")
                if (pwdCheckingFunction(tmpPassword!!)) {
                    //true -> spf에 저장
                    savePWD( tmpPassword!!)
                    savePWDstatus("ON")

                    //변경 or 설정 완료 -> 액티비티 종료
                    finish()

                    LogUtils.d("LOCK/CHECK-SUCCESS", "암호 등록에 성공하셨습니다.")
                } else {
                    //재설정
                    pwdResettingUI(mode!!)
                    type = "SETTING"

                    LogUtils.d("LOCK/CHECK-FAILURE", "암호 등록에 실패하셨습니다.")
                }
            }

            "UNLOCK" -> {
                if (pwdUnlockFunction()) {
                    //잠금 해제 성공
                    LogUtils.d("LOCK/UNLOCK-SUCCESS", "잠금 해제에 성공하셨습니다.")

                    if (mode == "CHANGE") { //암호 변경을 위한 잠금 해제 -> 암호 변경
                        pwdChangeUI()
                        type = "CHANGE"
                    } else {    //그냥 잠금 해제 -> SharedPreferences 에 암호가 풀렸음(SUCCESS)을 저장한다.
                        saveCrackStatus("SUCCESS")
                        finish()
                    }
                } else {
                    //잠금 해제 실패
                    LogUtils.d("LOCK/UNLOCK-FAILURE", "잠금 해제에 실패하셨습니다.")
                    pwdUnlockUI("WRONG")
                }
            }
        }
        resetPassword()
    }

    //password 초기화 함수
    private fun resetPassword() {
        GlobalScope.launch {
            delay(500)
            runOnUiThread {
                for (i in 1..4) setFootprint(i, false)
                numbers.clear()
            }
        }
    }

    /*기능별 Function*/
    //암호 설정 Function
    private fun pwdSettingFunction(): String {
        //tmp에 넣어주고 password 초기화
        val tmpPwd: String = password!!
        password = null

        return tmpPwd
    }

    //암호 확인 Function
    private fun pwdCheckingFunction(tmpPwd: String): Boolean {
        //tmpPwd, password 일치하는지 확인
        return tmpPwd == password
    }

    //잠금 해제 Function
    private fun pwdUnlockFunction(): Boolean {
        //password, spf password 일치하는지 확인
        return password == getPWD()
    }


    /*화면별 UI*/
    //암호 설정 UI
    private fun pwdSettingUI() {
        binding.lockTitleTv.setText(R.string.title_lock_setting)
        binding.lockDescriptionTv.setText(R.string.msg_lock_setting)
    }

    //암호 확인 UI
    private fun pwdCheckingUI() {
        binding.lockTitleTv.setText(R.string.title_lock_checking)
        binding.lockDescriptionTv.setText(R.string.msg_lock_checking)
    }

    //암호 확인 UI
    private fun pwdChangeUI() {
        binding.lockTitleTv.setText(R.string.title_lock_change)
        binding.lockDescriptionTv.setText(R.string.msg_lock_change)
    }

    //암호 재설정 UI
    private fun pwdResettingUI(type: String) {
        when (type) {
            "SETTING" -> binding.lockTitleTv.setText(R.string.title_lock_setting)
            "CHANGE" -> binding.lockTitleTv.setText(R.string.title_lock_change)
        }
        binding.lockDescriptionTv.setText(R.string.msg_lock_resetting)
    }

    //잠금 해제 UI
    private fun pwdUnlockUI(type: String) {
        when (type) {
            "DEFAULT" -> binding.lockDescriptionTv.setText(R.string.msg_lock_setting)
            "CHECK" -> binding.lockDescriptionTv.setText(R.string.msg_lock_unlock)
            "WRONG" -> binding.lockDescriptionTv.setText(R.string.msg_lock_unlock_wrong)
        }
        binding.lockTitleTv.setText(R.string.title_lock_unlock)
    }

    //뒤로가기 이벤트: SharedPreferences 에 LockActivity 에 들어왔다가 그냥 뒤로 나가 버린 것(CANCEL)을 저장한다.
    override fun onBackPressed() {
        saveCrackStatus("CANCEL")
        super.onBackPressed()
    }
}