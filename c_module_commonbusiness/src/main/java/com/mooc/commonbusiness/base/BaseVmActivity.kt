package com.mooc.commonbusiness.base

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer

/**
 * mvvm模式页面基类
 * 统一实现请求数据逻辑，和加载动画显示
 * @param M 数据模型
 * @param B binding类
 * @param V ViewModel
 */
abstract class BaseVmActivity<M:Any,B :ViewDataBinding> : BaseActivity() {

    protected var mViewModel: BaseVmViewModel<M>? = null
    protected var bindingView:B? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setContentView(getLayoutId())
        mViewModel = genericViewModel()
        bindingView = DataBindingUtil.setContentView<B>(this, getLayoutId())

        onCreateLayout(bindingView)
        mViewModel?.mLiveData?.observe(this, Observer {
//            bindingView?.setVariable(BR._all,it)
            onDataChange(it)
        })
        mViewModel?.initData()

    }

    abstract fun onCreateLayout(bindingView: B?)

    abstract fun genericViewModel(): BaseVmViewModel<M>


//    /**
//     * 初始化子类的viewModel
//     */
//    open fun genericViewModel() {
//        //利用 子类传递的 泛型参数实例化出absViewModel 对象。
//        val type = javaClass.genericSuperclass as ParameterizedType?
//        val arguments = type!!.actualTypeArguments
//        if (arguments.size > 1) {
//            val argument = arguments[1]
//            val modelClaz = (argument as Class<V>).asSubclass(BaseListViewModel::class.java)
//            mViewModel = ViewModelProviders.of(this)[modelClaz] as V
//        }
//    }

    abstract fun getLayoutId(): Int

    abstract fun onDataChange(it: M)


}