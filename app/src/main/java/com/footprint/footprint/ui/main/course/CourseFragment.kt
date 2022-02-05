package com.footprint.footprint.ui.main.course

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.footprint.footprint.databinding.FragmentCourseBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.lock.LockActivity
import com.footprint.footprint.utils.getPWDstatus
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult


class CourseFragment() : BaseFragment<FragmentCourseBinding>(FragmentCourseBinding::inflate) {
    private var lock = "OFF" //ON, OFF, UNLOCK

    override fun initAfterBinding() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val resultLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result ->
            if (result.resultCode == -1)
                    lock = "UNLOCK"
        }

        val pwdStatus = getPWDstatus(requireContext())
        if (pwdStatus == "ON") {
            lock = "ON"
            //잠금 해제 액티비티(LockActivity)로 이동, UNLOCK: 잠금 해제 모드
            val intent = Intent(requireContext(), LockActivity::class.java)
            intent.putExtra("mode", "UNLOCK")
            resultLauncher.launch(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        if(lock == "UNLOCK" || lock == "OFF"){
            Toast.makeText(requireContext(), "코스 프래그먼트", Toast.LENGTH_SHORT).show()
        }
    }
}