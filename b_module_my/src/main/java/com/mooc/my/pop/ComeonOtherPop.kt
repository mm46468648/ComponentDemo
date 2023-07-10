package com.mooc.my.pop

import android.content.Context
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.model.studyproject.MusicBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.my.R
import com.mooc.my.databinding.MyPopComeOnBinding
import com.mooc.my.model.ComeonOtherResponse
import com.mooc.my.widget.ViewComeOnUser
//import kotlinx.android.synthetic.main.my_pop_come_on.view.*

/**
 * 为他人加油弹窗
 */
class ComeonOtherPop(context: Context, private var comeonResponse: ComeonOtherResponse) : CenterPopupView(context) {

    private lateinit var inflater : MyPopComeOnBinding
    override fun getImplLayoutId(): Int {
        return R.layout.my_pop_come_on
    }

    val ids = arrayOf(R.id.ivHead1,R.id.ivHead2,R.id.ivHead3,R.id.ivHead4,R.id.ivHead5,R.id.ivHead6,R.id.ivHead7,R.id.ivHead8)
    override fun onCreate() {
        super.onCreate()
        inflater = MyPopComeOnBinding.bind(popupImplView)
        inflater.ivMemberClose.setOnClickListener {
            dismiss()
        }
        //设置加油用户信息
        comeonResponse.data?.forEachIndexed { index, userInfo ->
            if(index >= ids.size) return
            val findViewById = findViewById<ViewComeOnUser>(ids[index])
            findViewById.setUserInfo(userInfo,index)
            findViewById.visibility = View.VISIBLE
        }

        //设置播放音频信息
        if(comeonResponse.audio?.isNotEmpty() == true){
            inflater.clMusic.visibility = View.VISIBLE
            setMusicInfo(comeonResponse.audio!!)
        }
    }

    /**
     * 设置音频信息
     */

    private fun setMusicInfo(audio: List<MusicBean>) {
        val music = audio.get(0)
        inflater.tvMusicName.setText(music.audio_name)
        inflater.ivMusicCover.setImageUrl(music.audio_bg_img,true)
        inflater.clMusic.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_AUDIO_OWN_BUILD_PLAY).withString(
                IntentParamsConstants.AUDIO_PARAMS_ID, music.id).navigation()
        }
    }

}