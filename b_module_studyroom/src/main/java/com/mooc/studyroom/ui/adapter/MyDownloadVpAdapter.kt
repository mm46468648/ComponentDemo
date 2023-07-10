package com.mooc.studyroom.ui.adapter

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mooc.studyroom.ui.fragment.mydownload.AlbumDownloadFragment
import com.mooc.studyroom.ui.fragment.mydownload.CourseDownloadFragment
import com.mooc.studyroom.ui.fragment.mydownload.EbookDownloadFragment



class MyDownloadVpAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val fragments: SparseArray<Fragment> = SparseArray()

    companion object {
        const val PAGE_COURSE_DOWNLOAD = 0   //课程下载
        const val PAGE_ALBUM_DOWNLOAD = 1    //音频课下载
        const val PAGE_EBOOK_DOWNLOAD = 2     //电子书下载

    }

    init {
        fragments.put(PAGE_COURSE_DOWNLOAD, CourseDownloadFragment())
        fragments.put(PAGE_ALBUM_DOWNLOAD, AlbumDownloadFragment())
        fragments.put(PAGE_EBOOK_DOWNLOAD, EbookDownloadFragment())
    }

    override fun getItemCount(): Int {
        return fragments.size()
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}