package com.mooc.my.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.appbar.AppBarLayout
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.put
import com.mooc.common.ktextends.setDrawLeft
import com.mooc.common.ktextends.toast
import com.mooc.common.ktextends.visiable
import com.mooc.common.utils.NetUtils
import com.mooc.common.utils.bitmap.BitmapUtils
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.dialog.PublicDialog
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.commonbusiness.model.UserInfo
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.commonbusiness.utils.IShare
import com.mooc.commonbusiness.utils.format.StringFormatUtil.Companion.getStrUserName
import com.mooc.my.R
import com.mooc.my.adapter.NewUserShareAdapter
import com.mooc.my.adapter.UserShareInfoAdapter
import com.mooc.my.databinding.ActivityUserFollowBinding
import com.mooc.my.databinding.ActivityUserInfoBinding
import com.mooc.my.databinding.MyViewUserShareBinding
import com.mooc.my.fragment.UserInfoFragmentAdapter
import com.mooc.my.fragment.UserShareListFragment
import com.mooc.my.model.SchoolCircleBean
import com.mooc.my.model.SchoolSourceBean
import com.mooc.my.model.SchoolUserBean
import com.mooc.my.viewmodel.MyInfoViewModelL
import com.mooc.resource.utils.AppBarStateChangeListener
import com.mooc.resource.utils.AppBarStateChangeListener.State.COLLAPSED
import com.mooc.resource.utils.AppBarStateChangeListener.State.EXPANDED
//import kotlinx.android.synthetic.main.activity_user_info.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

/**
 *个人信息页面
 * @author limeng
 * @date 2020/11/3
 */
@Route(path = Paths.PAGE_USER_INFO)
class UserInfoActivity : BaseActivity() {
    private var schoolUserBean: SchoolUserBean? = null
    private var schoolCircleBean: SchoolCircleBean? = null
    var mUserShareInfoAdapter: UserShareInfoAdapter? = null
    var fragmentAdapter: UserInfoFragmentAdapter? = null
    var userBean: UserInfo? = null
    var userId: String = ""
    private var mAppBarStatus = 0 //判断现在是折叠还是展开，0 展开，1 折叠，2中间
    var curentSourceBean: SchoolSourceBean? = null
//    val mViewModel: MyInfoViewModelL by lazy {
//        ViewModelProviders.of(this)[MyInfoViewModelL::class.java]
//    }
      val mViewModel: MyInfoViewModelL by viewModels()
    private lateinit var inflate : ActivityUserInfoBinding

    val shareService: ShareSrevice by lazy {
        ARouter.getInstance().build(Paths.SERVICE_SHARE).navigation() as ShareSrevice
    }

    val defaultTabDatas = arrayOf("我的分享", "我公开的学习清单")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflate = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(inflate.root)
        iniData()
        iniTitData()
        initView()
        initListener()
        initDataListerner()
    }


    private fun initView() {
        fragmentAdapter = UserInfoFragmentAdapter(userId, this)
        inflate.viewPage2.adapter = fragmentAdapter
//        simpleTabLayout.setTabStrs(defaultTabDatas)
//        simpleTabLayout.setViewPager2(viewPage2)

        inflate.simpleTabLayout.setUpWithViewPage2(inflate.viewPage2, defaultTabDatas.toList() as ArrayList<String>)
    }

    /**
     * 判断是否是个人或者他人的分享页面
     */
    private fun isOwn(): Boolean {
        return TextUtils.isEmpty(userId) || userBean?.id.equals(userId)
    }

    private fun iniTitData() {
        if (isOwn()) {
            defaultTabDatas[0] = "我的分享"
            defaultTabDatas[1] = "我公开的学习清单"
        } else {
            defaultTabDatas[0] = "他的分享"
            defaultTabDatas[1] = "他公开的学习清单"
        }
    }

    private fun iniData() {
        userId = intent.getStringExtra(IntentParamsConstants.MY_USER_ID) ?: ""
        userBean = GlobalsUserManager.userInfo
        getData()
    }

    private fun initListener() {
        inflate.llLike.setOnClickListener(View.OnClickListener {
            if (schoolUserBean != null) {
                likeAndDisUserDialog(true)
            }
        })
        inflate.tvRightUser.setOnClickListener {
            // 举报他人
            val put = Bundle().put(IntentParamsConstants.PARAMS_RESOURCE_ID, schoolUserBean?.user_id.toString())
                    .put(IntentParamsConstants.PARAMS_RESOURCE_TYPE, ResourceTypeConstans.TYPE_REPORT_NUM_USER)
            ARouter.getInstance().build(Paths.PAGE_REPORT_DIALOG).with(put).navigation()
        }
        inflate.mTvLeft.setOnClickListener { finish() }
        inflate.mSwipeLayout.setOnRefreshListener {
            if (NetUtils.isNetworkConnected()) {
                getData()
            } else {
                inflate.mSwipeLayout.isRefreshing = false
                toast(getString(R.string.net_error))
            }
        }
        // 跳转到关注的人
        inflate.mViewFollow.setOnClickListener {
            if (schoolUserBean != null) {
                ARouter.getInstance()
                        .build(Paths.PAGE_USERFOLLOW)
                        .withInt(IntentParamsConstants.Follow_Fans, 1)//关注的人
                        .withString(IntentParamsConstants.MY_USER_ID, schoolUserBean?.user_id?.toString())
                        .navigation()
            }
        }
        //跳转到粉丝列表
        inflate.mViewFans.setOnClickListener {
            if (schoolUserBean != null) {
                ARouter.getInstance()
                        .build(Paths.PAGE_USERFOLLOW)
                        .withInt(IntentParamsConstants.Follow_Fans, 0)//粉丝
                        .withString(IntentParamsConstants.MY_USER_ID, schoolUserBean?.user_id?.toString())
                        .navigation()
            }
        }
        inflate.llFollow.setOnClickListener {
            if (schoolUserBean != null) {
                if (isOwn()) {//个人 分享图片出去
                    shareUser()
                } else {//是否关注
                    followUserDialog()
                }
            }
        }
        inflate.alUser.addOnOffsetChangedListener(object : com.mooc.resource.utils.AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: Int) {
                when (state) {
                    EXPANDED -> {//展开状态
                        //展开状态
                        inflate.mViewShadow.setVisibility(View.VISIBLE)
                        mAppBarStatus = 0
                        //展开状态栏颜色
//                        initStatusBar(resources.getColor(R.color.color_0))
                        inflate.mTvTitle.setText("")
                        inflate.mTvUserName.setVisibility(View.VISIBLE)
                        inflate.mIvUser.setVisibility(View.VISIBLE)
                        inflate.mTvLeft.setImageResource(R.mipmap.my_ic_back)
                        //展开状态的刷新按钮
//                        if (mSwipeLayout != null) {
//                            if (mFirstItemPosition == 0) {
//                                mSwipeLayout.setEnabled(true)
//                            } else {
//                                mSwipeLayout.setEnabled(false)
//                            }
//                        }
                        setTvFollow(schoolUserBean)
                    }
                    COLLAPSED -> {//折叠状态
                        //折叠状态
                        inflate.mViewShadow.setVisibility(View.GONE)
                        mAppBarStatus = 1
//                        initStatusBar(resources.getColor(R.color.color_white))
                        if (schoolUserBean != null && !(schoolUserBean?.user_name).isNullOrEmpty()) {
                            inflate.mTvTitle.setText(schoolUserBean?.user_name)
                        }
                        inflate.mTvUserName.visibility = View.INVISIBLE
                        inflate.mIvUser.setVisibility(View.INVISIBLE)
                        inflate.mTvLeft.setImageResource(R.mipmap.my_icon_back_gray)
                        inflate.mSwipeLayout.setEnabled(false)
                        setTvFollow(schoolUserBean)
                    }
                    else -> {//中间状态
                        //中间状态
                        mAppBarStatus = 2
                        inflate.mViewShadow.setVisibility(View.VISIBLE)
//                        initStatusBar(resources.getColor(R.color.color_64F))
                        inflate.mTvTitle.setText("")
//                        mTvUserName.setVisibility(View.INVISIBLE);
//                        mIvUser.setVisibility(View.INVISIBLE);
                        //                        mTvUserName.setVisibility(View.INVISIBLE);
//                        mIvUser.setVisibility(View.INVISIBLE);
                        inflate.mTvLeft.setImageResource(R.mipmap.my_ic_back)
                        inflate.mSwipeLayout.setEnabled(false)
                        setTvFollow(schoolUserBean)
                    }
                }
            }

        })
    }

    /**
     * 获取网络请求结果
     */
    private fun initDataListerner() {
        mViewModel.mFollowStatusBean.observe(this, Observer {
            if (it != null) {
                toast(it.message)
                if (!TextUtils.isEmpty(it.code)) {
                    val code = it.code?.toInt()
                    when (code) {
                        2 -> {
                            if (schoolUserBean != null) {
                                schoolUserBean?.user_is_follow = true
                                schoolUserBean?.state = 2
                                if (schoolUserBean?.user_be_follow_count != null) {
                                    schoolUserBean?.user_be_follow_count = schoolUserBean?.user_be_follow_count!! + 1
                                }
                            }
                            if (schoolCircleBean != null) {
                                schoolCircleBean!!.user_info = schoolUserBean
                            }
                            inflate.mTvUserFans.setText(java.lang.String.valueOf(schoolUserBean?.user_be_follow_count))
                            setTvFollow(schoolUserBean)
                            updateFollowNum(true)
                        }
                        3 -> {
                            if (schoolUserBean != null) {
                                schoolUserBean?.user_is_follow = false
                                schoolUserBean?.state = 0
                                if (schoolUserBean?.user_be_follow_count != null) {
                                    schoolUserBean?.user_be_follow_count = schoolUserBean?.user_be_follow_count!! - 1
                                }
                            }
                            if (schoolCircleBean != null) {
                                schoolCircleBean!!.user_info = schoolUserBean
                            }
                            inflate.mTvUserFans.setText(java.lang.String.valueOf(schoolUserBean?.user_be_follow_count))
                            setTvFollow(schoolUserBean)
                            updateFollowNum(false)

                        }
                        6 -> {
                            if (schoolUserBean != null) {
                                schoolUserBean?.user_is_follow = true
                                schoolUserBean?.state = 1
                                if (schoolUserBean?.user_be_follow_count != null) {
                                    schoolUserBean?.user_be_follow_count = schoolUserBean?.user_be_follow_count!! + 1
                                }
                            }
                            if (schoolCircleBean != null) {
                                schoolCircleBean!!.user_info = schoolUserBean
                            }
                            inflate.mTvUserFans.setText(java.lang.String.valueOf(schoolUserBean?.user_be_follow_count))
                            setTvFollow(schoolUserBean)
                            updateFollowNum(true)
                        }
                    }
                }
            }
        })
        mViewModel.mUserParserStatusBean.observe(this, Observer {
            toast(it.message)
            if (!TextUtils.isEmpty(it.code)) {
                val code = it.code
                when (code) {
                    "1" -> {//点赞成功
                        if (schoolUserBean != null) {
                            schoolUserBean?.is_like = true
                            if (schoolUserBean?.is_diss == true) {
                                schoolUserBean?.is_diss = false
                                schoolUserBean?.user_dislike_count = schoolUserBean?.user_dislike_count!! - 1
                            }
                            schoolUserBean?.user_like_count = schoolUserBean?.user_like_count!! + 1
                            setLike(schoolUserBean?.user_like_count!!, schoolUserBean?.is_like!!)
                            //                                            setDisLike(schoolUserBean?.getUser_dislike_count(), schoolUserBean?.isIs_diss());
                        }
                        if (schoolCircleBean != null) {
                            schoolCircleBean?.user_info = schoolUserBean
                        }
                    }
                    "2" -> {//取消点赞
                        if (schoolUserBean != null) {
                            schoolUserBean?.is_like = false
                            schoolUserBean?.user_like_count = schoolUserBean?.user_like_count!! - 1
                            setLike(schoolUserBean?.user_like_count!!, schoolUserBean?.is_like!!)
                        }
                        if (schoolCircleBean != null) {
                            schoolCircleBean?.user_info = schoolUserBean
                        }
                    }
                }
            }

        })
        mViewModel.mSchoolCircleBean.observe(this, Observer {
            inflate.mSwipeLayout.isRefreshing = false
            schoolCircleBean = it
            schoolUserBean = it.user_info
            updateData()
        })
    }

    /**
     * 获取个人信息
     */
    fun getData() {
        if (userId.isNullOrEmpty()) return
        mViewModel.getUserSchoolCircle(userId!!)

    }

    private fun likeAndDisUserDialog(isTvLike: Boolean) {
        val message: String
        val likeType: Int
        if (isOwn()) {
//            likeType = 0;
            message = resources.getString(R.string.text_user_like_and_dis)
            toast(message)
            return
        } else {
            if (isTvLike) { //点赞按按
                message = if (schoolUserBean?.is_like == true) {
                    resources.getString(R.string.text_cancel_like_user)
                } else {
                    resources.getString(R.string.text_like_user)
                }
                likeType = 1
            } else {
                message = if (schoolUserBean?.is_like == true) {
                    resources.getString(R.string.text_cancel_dis_like_user)
                } else {
                    resources.getString(R.string.text_dis_like_user)
                }
                likeType = 0
            }
        }

        val publicDialogBean = PublicDialogBean()
        publicDialogBean.strMsg = message
        publicDialogBean.strLeft = resources?.getString(R.string.text_cancel)
        publicDialogBean.strRight = resources?.getString(R.string.text_ok)
        publicDialogBean.isLeftGreen = 0
        XPopup.Builder(this)
                .asCustom(PublicDialog(this, publicDialogBean) {
                    if (it == 1) {
                        if (!isOwn()) {
                            mViewModel.postUserLikeAndDis(userId, likeType)
                        }
                    }
                })
                .show()
    }


    /**
     * 更新页面信息
     */
    private fun updateData() {
        if (schoolCircleBean != null) {
            schoolUserBean = schoolCircleBean?.user_info
            if (schoolUserBean != null) {
                setTvFollow(schoolUserBean)
                if (!isFinishing) {
                    inflate.mIvUser.setHeadImage(schoolUserBean?.user_avatar, schoolUserBean?.avatar_identity)
//                    Glide.with(this)
//                            .load(schoolUserBean?.user_avatar)
//                            .error(R.mipmap.common_ic_user_head_default)
//                            .placeholder(R.mipmap.common_ic_user_head_default)
//                            .centerCrop()
//                            .circleCrop()
//                            .into(mIvUser)
                }
                inflate.mTvUserDesc.setVisibility(View.GONE)
                inflate.mTvUserDesc.setText(schoolUserBean?.user_introduction)
                inflate.mTvUserFans.setText(schoolUserBean?.user_be_follow_count.toString())
                inflate.mTvUserFollow.setText(schoolUserBean?.user_follow_count.toString())
                inflate.mTvUserScore.setText(schoolUserBean?.user_score.toString())
                if (isOwn()) {
                    schoolUserBean?.is_like = true
                    schoolUserBean?.is_diss = true
                    inflate.mTvUserName.setText(schoolUserBean?.user_name)
                } else {
                    inflate.mTvUserName.setText(schoolUserBean?.user_name?.let { getStrUserName(it) })
                }
                schoolUserBean?.is_like?.let { setLike(schoolUserBean?.user_like_count!!, it) }
                //                setDisLike(schoolUserBean?.getUser_dislike_count(), schoolUserBean?.isIs_diss());
            }
        }
    }

    /**
     * 设置右上角是分享还是关注的状态
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setTvFollow(schoolUserBean: SchoolUserBean?) {
        if (schoolUserBean != null) {
            val drawable: Drawable
            if (isOwn()) {//是个人
                inflate.flUserShare.setVisibility(View.GONE)
                inflate.mTvRightFollowed.setVisibility(View.VISIBLE)
                inflate.llFollow.setBackgroundResource(R.drawable.my_shape_gray_corner_40)
                inflate.mTvRightFollowed.setTextColor(resources.getColor(R.color.color_3))
                inflate.mTvRightFollowed.setText(resources.getString(R.string.text_share))
                drawable = resources.getDrawable(R.mipmap.my_iv_small_share_gray)

                //这一步必须要做,否则不会显示.
                drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                inflate.mTvRightFollowed.setCompoundDrawables(drawable, null, null, null)
            } else {//是他人
                inflate.tvRightUser.setVisibility(View.VISIBLE)//可以举报他人

                if (schoolUserBean?.user_is_follow) {//已关注
                    inflate.flUserShare.setVisibility(View.GONE)
                    inflate.mTvRightFollowed.setVisibility(View.VISIBLE)
                    inflate.mTvRightFollowed.setTextColor(resources.getColor(R.color.color_9))
                    inflate.mTvRightFollowed.text = "取消关注"
                    inflate.llFollow.setBackgroundResource(R.drawable.my_shape_gray_corner_40)

                    drawable = resources.getDrawable(R.mipmap.my_iv_user_followed_gray)

                    //这一步必须要做,否则不会显示.
                    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                    inflate.mTvRightFollowed.setCompoundDrawables(drawable, null, null, null)
                } else {//未关注

                    inflate.flUserShare.setVisibility(View.VISIBLE)
                    inflate.mTvRightFollowed.setVisibility(View.VISIBLE)
                    inflate.mTvRightFollowed.setText(resources.getString(R.string.text_my_follow))
                    inflate.mTvRightFollowed.setTextColor(resources.getColor(R.color.color_white))
                    inflate.llFollow.setBackgroundResource(R.drawable.my_shape_green_corner_40)
                    drawable = resources.getDrawable(R.mipmap.my_iv_user_follow_add)

                    //这一步必须要做,否则不会显示.
                    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                    inflate.mTvRightFollowed.setCompoundDrawables(drawable, null, null, null)
                }
            }
        } else {
            inflate.mTvRightFollowed.setVisibility(View.GONE)
            inflate.tvRightUser.setVisibility(View.GONE)
        }
    }

    /**
     * 设置点赞ui
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setLike(count: Int, isLike: Boolean) {
        val drawable: Drawable
        drawable = if (isLike) {
            resources.getDrawable(R.mipmap.common_icon_red_like)
        } else {
            resources.getDrawable(R.mipmap.common_iv_plan_fill)
        }
        //这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        inflate.mTvUserLike.setCompoundDrawables(drawable, null, null, null)
        inflate.mTvUserLike.setText(java.lang.String.format(getString(R.string.text_str_fill), count))
    }


    /**
     * 弹出是否关注的提示
     */
    @Suppress("DEPRECATION")
    private fun followUserDialog() {
        val message = if (schoolUserBean?.user_is_follow == true) {
            resources.getString(R.string.text_cancel_follow_user)
        } else {
            resources.getString(R.string.text_follow_user)
        }
        val publicDialogBean = PublicDialogBean()
        publicDialogBean.strMsg = message
        publicDialogBean.strLeft = resources?.getString(R.string.text_cancel)
        publicDialogBean.strRight = resources?.getString(R.string.text_ok)
        publicDialogBean.isLeftGreen = 0
        XPopup.Builder(this)
                .asCustom(PublicDialog(this, publicDialogBean) {
                    if (it == 1) {
                        followOne()
                    }
                })
                .show()
    }

    /**
     * 关注的接口
     */
    fun followOne() {
        val status: Int
        status = if (schoolUserBean?.user_is_follow == true) {
            0
        } else {
            1
        }

        val requestData = JSONObject()
        try {
            requestData.put("follow_user_id", userId)
            requestData.put("follow_status", status)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val stringBody = requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())

        mViewModel.postFollowUser(stringBody)

    }

    /**
     * 更新关注数量
     * @param increase 是否增加
     */
    fun updateFollowNum(increase: Boolean) {
//        GlobalsUserManager.userInfo?.apply {
//            val userFollowCount = if (increase) user_follow_count + 1 else user_follow_count - 1
//            this.user_follow_count = if(userFollowCount>0) userFollowCount else 0
//
//            SPUserUtils.getInstance().saveUserInfo(this)
//            LiveDataBus.get().with(LiveDataBusEventConstants.EVENT_USERINFO_CHANGE).postValue(this)
//        }

    }

    /**
     * 分享个人
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun shareUser() {
        var shareListFragment = fragmentAdapter?.fragments?.get(0) as UserShareListFragment
        mUserShareInfoAdapter = shareListFragment.mUserShareInfoAdapter
        val binding: MyViewUserShareBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.my_view_user_share, null, false)
        val dataList = mUserShareInfoAdapter?.data
        val list = ArrayList<SchoolSourceBean>()
        dataList?.let {
            if (dataList.size > 0) {
                list.add(dataList.get(0))
//                if (dataList.size > 1) {
//                    list.add(dataList.get(1))
//                }
                binding.rcUser.layoutManager = LinearLayoutManager(this)
                var newAdapter = NewUserShareAdapter(list)
                binding.rcUser.adapter = newAdapter
                newAdapter.notifyDataSetChanged()


            } else {
                binding.rcUser.visiable(false)
            }

        }
        //
        if (schoolUserBean != null) {
            schoolUserBean = schoolCircleBean?.user_info
            if (schoolUserBean != null) {
//                setTvFollow(schoolUserBean)
                binding.tvUserName.setText(schoolUserBean?.user_name)
                binding.tvUserCraft.setText(schoolUserBean?.user_introduction)
                binding.tvUserFansCount.setText(schoolUserBean?.user_be_follow_count.toString())
                binding.tvUserFollowCount.setText((schoolUserBean?.user_follow_count.toString()))
                binding.tvUserScore.setText((schoolUserBean?.user_score.toString()))

                binding.tvLikeUserShare.setDrawLeft(R.mipmap.my_iv_colour_like)
                binding.tvLikeUserShare.text = java.lang.String.format(getString(R.string.text_str_fill), schoolUserBean?.user_like_count)
            }
            binding.tvUserShareTitle.setText(java.lang.String.format(getString(R.string.text_share_user_name), schoolUserBean?.user_name))
//            Glide.with(this)
//                    .load(schoolUserBean?.user_avatar)
//                    .into(object : CustomTarget<Drawable>() {
//                        override fun onResourceReady(
//                                resource: Drawable,
//                                transition: Transition<in Drawable>?
//                        ) {
            if (!schoolUserBean?.user_avatar.isNullOrEmpty()) {

                val builder = Glide.with(this).load(schoolUserBean?.user_avatar)
                builder.transform(CircleCrop())
                val layoutParams = binding.ivUserAvatar.layoutParams
                if (layoutParams != null && layoutParams.width > 0 && layoutParams.height > 0) {
                    builder.override(layoutParams.width, layoutParams.height)
                }
                builder.into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                    ) {
                        binding.ivUserAvatar.setImageDrawable(resource)
                        binding.ivUserAvatar.setBackgroundColor(Color.TRANSPARENT)


                        val shareBitmap = BitmapUtils.createUnShowBitmapFromLayout(binding.root)
                        val commonBottomSharePop = CommonBottomSharePop(this@UserInfoActivity, {
                            val build = IShare.Builder()
                                    .setSite(it)
                                    .setTitle("")
                                    .setMessage("")
                                    .setImageBitmap(shareBitmap)
                                    .build()
                            shareService.share(this@UserInfoActivity, build)
                        })
                        XPopup.Builder(this@UserInfoActivity).asCustom(commonBottomSharePop).show()
                    }


                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })


//                binding.ivUserAvatar.setImageUrl(schoolUserBean?.user_avatar!!, true)
            }


//                        }
//
//                        override fun onLoadCleared(placeholder: Drawable?) {
//
//                        }
//
//                        override fun onLoadFailed(errorDrawable: Drawable?) {
//                            super.onLoadFailed(errorDrawable)
//
//                        }
//                    })


        }
    }
}