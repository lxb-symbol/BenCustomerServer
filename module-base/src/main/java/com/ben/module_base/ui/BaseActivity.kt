package com.ben.module_base.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import kotlin.reflect.KClass


abstract class BaseActivity<T : ViewModel, M : ViewDataBinding> : AppCompatActivity() {

    open lateinit var mViewModel: T
    open lateinit var mViewBinding: M

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        supportActionBar?.hide()
        mViewBinding = DataBindingUtil.setContentView(this, getLayoutResId())
        mViewModel = initViewModel()
        initView()
        initData()

    }

    abstract fun getLayoutResId(): Int


    abstract fun initData()

    abstract fun initView()


    abstract fun initViewModel(): T


}