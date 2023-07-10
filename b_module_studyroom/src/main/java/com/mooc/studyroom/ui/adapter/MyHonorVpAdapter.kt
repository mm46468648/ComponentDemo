package com.mooc.studyroom.ui.adapter

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mooc.studyroom.ui.fragment.myhonor.CertificateFragment
import com.mooc.studyroom.ui.fragment.myhonor.MyMedalFragment

/**
 * 学习记录viewpager适配器
 * 包含（勋章，证书）
 */
class MyHonorVpAdapter(activity : FragmentActivity) : FragmentStateAdapter(activity){

    private val fragments: SparseArray<Fragment> = SparseArray()

    companion object {
        const val PAGE_CERTIFICATE = 0   //证书
        const val PAGE_MEDAL = 1     //勋章
    }

    init {
        fragments.put(PAGE_CERTIFICATE, CertificateFragment())
        fragments.put(PAGE_MEDAL, MyMedalFragment())

    }
    override fun getItemCount(): Int {
        return fragments.size()
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}