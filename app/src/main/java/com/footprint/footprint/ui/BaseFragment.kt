package com.footprint.footprint.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.footprint.footprint.R
import com.footprint.footprint.ui.error.ErrorActivity
import com.footprint.footprint.utils.Inflate
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<VB : ViewBinding>(private val inflate: Inflate<VB>) : Fragment() {
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initAfterBinding()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected abstract fun initAfterBinding()

    fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun hideKeyboard() {
        val mInputMethodManager =
            requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        mInputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    fun showSnackBar(text: String, callback: () -> Unit) {
        Snackbar.make(requireView(), text, Snackbar.LENGTH_INDEFINITE).setAction(R.string.action_retry) {
            callback
        }.show()
    }

    fun startErrorActivity(getResult: ActivityResultLauncher<Intent>, screen: String){

        val intent = Intent(requireContext(), ErrorActivity::class.java)
        intent.putExtra(ErrorActivity.SCREEN, screen)

        getResult.launch(intent)
    }
}