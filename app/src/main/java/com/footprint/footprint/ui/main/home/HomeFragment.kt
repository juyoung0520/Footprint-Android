package com.footprint.footprint.ui.main.home

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.databinding.FragmentHomeBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.main.MainActivity

class HomeFragment(): BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
//    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            val resultIntent = result.data
//
//            if (resultIntent != null) {
//                Toast.makeText(requireContext(), "result", Toast.LENGTH_SHORT).show()
//                binding.homeResIv.setImageBitmap(resultIntent.getParcelableExtra("bitmap"))
//            }
//        }
//    }

    override fun initAfterBinding() {
        binding.homeContentTv.setOnClickListener {
//            val intent = Intent(requireContext(), WalkActivity::class.java)
//            activityResultLauncher.launch(intent)

            val mainActivity = activity as MainActivity
            mainActivity.startNextActivity(WalkActivity::class.java)
        }

    }


}