package com.footprint.footprint.ui.lock

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivityLockBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.utils.convertDpToPx
import com.footprint.footprint.utils.getDeviceWidth
import com.footprint.footprint.utils.getPWD
import com.footprint.footprint.utils.savePWD

class LockActivity() : BaseActivity<ActivityLockBinding>(ActivityLockBinding::inflate) {
    private lateinit var lockRVAdapter: LockRVAdapter

    private val numbers: ArrayList<Int> = arrayListOf()
    private var password: String? = null
    private var tmpPassword: String? = null

    private val mode = "CHANGE"
    private var type: String? = null
    override fun initAfterBinding() {
        /*화면 종류별*/
        when(mode){
            "SETTING" -> { //1. 암호 설정
                pwdSettingUI()
                type = "SETTING"
            }

            "CHANGE", "UNLOCK" -> { //2. 암호 변경 //3. 잠금 해제
                pwdUnlockUI("DEFAULT")
                type = "UNLOCK"
            }
        }

        /*화면 init*/
        initRV()
        initBackBtn()
    }

    /*Back 버튼*/
    private fun initBackBtn() {
        binding.lockBackBtnIv.setOnClickListener {
            finish()
        }
    }

    /*Number 리사이클러뷰*/
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

        Log.d(
            "LOCK/NUM",
            "number: $number numbers: $numbers numbers index: ${numbers.indexOf(number)}"
        )


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
                binding.lockFootprint1Tv.text = num.toString()
            }

            2 -> {
                setFootprint(2, true)
                setFootprint(3, false)
                setFootprint(4, false)
                binding.lockFootprint2Tv.text = num.toString()
            }

            3 -> {
                setFootprint(3, true)
                setFootprint(4, false)
                binding.lockFootprint3Tv.text = num.toString()
            }

            4 -> {
                setFootprint(4, true)
                binding.lockFootprint4Tv.text = num.toString()

                password = numbers.joinToString("")
                Toast.makeText(this, password, Toast.LENGTH_SHORT).show()

                numbers.clear()

                function(type!!)

            }
        }
    }
    private fun setFootprint(index: Int, status: Boolean) {
        when (index) {
            //Footprint 1
            1 -> {
                if (status) {
                    binding.lockFootprint1Iv.setImageResource(R.drawable.ic_lock_footprint_on)
                    binding.lockFootprint1Tv.visibility = View.VISIBLE
                } else {
                    binding.lockFootprint1Iv.setImageResource(R.drawable.ic_lock_footprint_off)
                    binding.lockFootprint1Tv.visibility = View.GONE
                }
            }

            //Footprint 2
            2 -> {
                if (status) {
                    binding.lockFootprint2Iv.setImageResource(R.drawable.ic_lock_footprint_on)
                    binding.lockFootprint2Tv.visibility = View.VISIBLE
                } else {
                    binding.lockFootprint2Iv.setImageResource(R.drawable.ic_lock_footprint_off)
                    binding.lockFootprint2Tv.visibility = View.GONE
                }
            }

            //Footprint 3
            3 -> {
                if (status) {
                    binding.lockFootprint3Iv.setImageResource(R.drawable.ic_lock_footprint_on)
                    binding.lockFootprint3Tv.visibility = View.VISIBLE
                } else {
                    binding.lockFootprint3Iv.setImageResource(R.drawable.ic_lock_footprint_off)
                    binding.lockFootprint3Tv.visibility = View.GONE
                }
            }

            //Footprint 4
            4 -> {
                if (status) {
                    binding.lockFootprint4Iv.setImageResource(R.drawable.ic_lock_footprint_on)
                    binding.lockFootprint4Tv.visibility = View.VISIBLE
                } else {
                    binding.lockFootprint4Iv.setImageResource(R.drawable.ic_lock_footprint_off)
                    binding.lockFootprint4Tv.visibility = View.GONE
                }
            }
        }
    }


    /*기능별 함수(type): SETTING(암호 설정), CHECKING(암호 확인), UNLOCK(잠금 해제)
    * SETTING  - 암호 설정/변경 위해 password -> tmpPassword 저장
    * CHECKING - 암호 확인 위해 password == tmpPassword 비교
    * UNLOCK   - 잠금 해제 위해 password == spf 비교
    *  */
    private fun function(inputType: String) {
        when(inputType){
            "SETTING", "CHANGE" -> {
                //확인 UI로 변경
                tmpPassword = pwdSettingFunction()
                pwdCheckingUI()
                type = "CHECKING"

                Log.d("LOCK/SET", "Type: $type Password: $password TmpPwd: $tmpPassword")
            }

            "CHECKING" -> {
                //tmp_pwd와 비교
                Log.d("LOCK/SET", "Type: $type Password: $password TmpPwd: $tmpPassword")
                if(pwdCheckingFunction(tmpPassword!!)){
                    //true -> spf에 저장
                    savePWD(this, tmpPassword!!)
                    //액티비티 종료
                    finish()

                    Log.d("LOCK/SET", "암호 등록에 성공하셨습니다.")
                }else{
                    //재설정
                    pwdResettingUI(mode)
                    type = "SETTING"

                    Log.d("LOCK/SET", "암호 등록에 실패하셨습니다.")
                }
            }

            "UNLOCK" -> {
                if(pwdUnlockFunction()){
                    //잠금 해제 성공
                    Log.d("LOCK/SET", "잠금 해제에 성공하셨습니다.")
                    if(mode == "CHANGE"){
                        //암호 변경을 위한 잠금 해제
                        pwdChangeUI()
                        type = "CHANGE"
                    }else{
                        //그냥 잠금 해제
                        finish()
                    }
                }else{
                    //잠금 해제 실패
                    Log.d("LOCK/SET", "잠금 해제에 실패하셨습니다.")
                    pwdUnlockUI("WRONG")
                }
            }
        }

        for (i in 1..4) setFootprint(i, false)
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
    private fun pwdUnlockFunction(): Boolean{
        //password, spf password 일치하는지 확인
        return password == getPWD(this)
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
        when(type){
            "DEFAULT" -> binding.lockDescriptionTv.setText(R.string.msg_lock_unlock)
            "WRONG" -> binding.lockDescriptionTv.setText(R.string.msg_lock_unlock_wrong)
        }
        binding.lockTitleTv.setText(R.string.title_lock_unlock)
    }

}