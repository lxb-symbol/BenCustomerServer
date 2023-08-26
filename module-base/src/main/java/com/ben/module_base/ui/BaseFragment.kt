package com.ben.module_base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import kotlin.reflect.KClass


abstract class BaseFragment<T : ViewModel, M : ViewDataBinding> : Fragment() {

    open lateinit var mViewModel: T
    open lateinit var mViewBinding: M

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mViewBinding = DataBindingUtil.inflate(inflater, getLayoutResId(), container, false)

        return mViewBinding.root
    }

    abstract fun getLayoutResId(): Int

    abstract fun initData()

    abstract fun initView()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = initViewModel()
        initData()
        initView()
    }

    abstract  fun initViewModel(): T


}