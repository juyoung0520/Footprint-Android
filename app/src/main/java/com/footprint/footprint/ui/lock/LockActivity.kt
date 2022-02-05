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

class LockActivity : BaseActivity<ActivityLockBinding>(ActivityLockBinding::inflate) {
    private val numbers: ArrayList<Int> = arrayListOf()

    override fun initAfterBinding() {
        initRV()

        binding.lockBackBtnIv.setOnClickListener {
            finish()
        }

    }

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

        Log.d("LOCK/NUM", "number: $number numbers: $numbers numbers index: ${numbers.indexOf(number)}")


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

                Toast.makeText(this, "Full", Toast.LENGTH_SHORT).show()
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

    private fun initRV() {
        //item Width
        val widthPx = getDeviceWidth() - convertDpToPx(this, 24 * 2) //디바이스 넓이 - 양 옆 마진(24*2)
        val itemWidthPx = widthPx / 3

        val lockRVAdapter = LockRVAdapter(itemWidthPx)
        binding.lockNumberRv.adapter = lockRVAdapter
        binding.lockNumberRv.layoutManager =
            GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false)

        //Number 클릭 이벤트 리스너
        lockRVAdapter.setItemClickListener(object : LockRVAdapter.OnItemClickListener {
            override fun onClick(data: Int) {
                changeFootprintUI(data)
            }
        })

    }

}