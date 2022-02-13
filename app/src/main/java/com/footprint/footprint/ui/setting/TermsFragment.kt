package com.footprint.footprint.ui.setting

import androidx.navigation.fragment.navArgs
import com.footprint.footprint.databinding.FragmentTermsBinding
import com.footprint.footprint.ui.BaseFragment

class TermsFragment : BaseFragment<FragmentTermsBinding>(FragmentTermsBinding::inflate) {
    private val args: TermsFragmentArgs by navArgs()

    override fun initAfterBinding() {
        binding.termsTermsTitleTv.text = args.title
        binding.termsTermsContentTv.text = args.content
    }

}