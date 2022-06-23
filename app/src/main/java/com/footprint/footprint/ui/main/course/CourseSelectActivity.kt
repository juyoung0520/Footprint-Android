package com.footprint.footprint.ui.main.course

import android.content.Intent
import com.footprint.footprint.databinding.ActivityCourseSelectBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.adapter.WalkVerCSRVAdapter
import com.footprint.footprint.ui.adapter.viewtype.WalkVerCSVT

class CourseSelectActivity : BaseActivity<ActivityCourseSelectBinding>(ActivityCourseSelectBinding::inflate) {
    private lateinit var adapter: WalkVerCSRVAdapter

    override fun initAfterBinding() {
        initAdapter()
        setMyEventListener()
    }

    private fun initAdapter() {
        adapter = WalkVerCSRVAdapter(arrayListOf<WalkVerCSVT>(WalkVerCSVT(WalkVerCSVT.TYPE_A), WalkVerCSVT(WalkVerCSVT.TYPE_B), WalkVerCSVT(WalkVerCSVT.TYPE_B), WalkVerCSVT(WalkVerCSVT.TYPE_B), WalkVerCSVT(WalkVerCSVT.TYPE_B), WalkVerCSVT(WalkVerCSVT.TYPE_B), WalkVerCSVT(WalkVerCSVT.TYPE_B)))
        adapter.setMyClickListener(object : WalkVerCSRVAdapter.MyClickListener {
            override fun click() {
                val intent: Intent = Intent(this@CourseSelectActivity, CourseSetActivity::class.java)
                startActivity(intent)
            }
        })
        binding.courseSelectRv.adapter = adapter
    }

    private fun setMyEventListener() {
        binding.courseSelectCancelTv.setOnClickListener {
            finish()
        }
    }
}