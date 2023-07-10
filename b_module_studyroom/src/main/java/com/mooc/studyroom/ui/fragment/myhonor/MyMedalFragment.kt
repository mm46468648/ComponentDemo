package com.mooc.studyroom.ui.fragment.myhonor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.NetUtils
import com.mooc.common.utils.SystemUtils
import com.mooc.common.utils.bitmap.BitmapUtils
import com.mooc.commonbusiness.constants.NormalConstants.Companion.SHARE_SOURCE_TYPE_MEDAL
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.commonbusiness.utils.IShare
import com.mooc.studyroom.R
import com.mooc.studyroom.databinding.StudyroomFragmentMyMedalBinding
import com.mooc.studyroom.model.MedalBean
import com.mooc.studyroom.model.MedalDataBean
import com.mooc.studyroom.model.MedalTypeBean
import com.mooc.studyroom.ui.adapter.MyMedalAdapter
import com.mooc.studyroom.ui.pop.ShowMedalBigPop
import com.mooc.studyroom.viewmodel.MedalViewModel
//import kotlinx.android.synthetic.main.studyroom_my_view_xunzhang_share.*
//import kotlinx.android.synthetic.main.studyroom_fragment_my_medal.*
import me.devilsen.czxing.code.BarcodeWriter
import me.devilsen.czxing.util.BarCodeUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject


/**
 * 我的勋章
 * @author limeng
 * @date 2020/9/23
 */
class MyMedalFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    var myMedalAdapter: MyMedalAdapter? = null
    var shareDataBean: MedalDataBean? = null

    companion object {
        const val TYPE_SHARE_MEDAL = 1 //分享勋章
        const val TYPE_SIGN_MEDAL = 2  //打卡勋章
        const val TYPE_SPECIAL_MEDAL = 3  //神秘勋章
        const val TYPE_STUDY_MEDAL = 4  //学习勋章
    }


    val shareService: ShareSrevice by lazy {
        ARouter.getInstance().build(Paths.SERVICE_SHARE).navigation() as ShareSrevice
    }

    //    val mViewModel: MedalViewModel by lazy {
//        ViewModelProviders.of(this)[MedalViewModel::class.java]
//    }
    val mViewModel: MedalViewModel by viewModels()
    private var _binding : StudyroomFragmentMyMedalBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = StudyroomFragmentMyMedalBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        intData()
        initListener()
        initDataListener()
    }

    private fun initDataListener() {
        mViewModel.medalBean.observe(viewLifecycleOwner, {
            setData(it)
        })
        mViewModel.shareData.observe(viewLifecycleOwner, Observer {
            val cmsShareBean = it
            val writer = BarcodeWriter()
            val bitmap = writer.write(cmsShareBean?.weixin_url, BarCodeUtil.dp2px(activity, 200f),
                    Color.BLACK, BitmapFactory.decodeResource(context?.resources, R.mipmap.common_ic_share_logo_transparent))
            toShare(bitmap, shareDataBean)
        })
    }

    /**
     * 生成图片并分享
     */
    private fun toShare(bitmap: Bitmap, bean: MedalDataBean?) {

        val shareView = LayoutInflater.from(context).inflate(R.layout.studyroom_my_view_xunzhang_share, null)
        val header = shareView.findViewById<View>(R.id.share_point_header) as ImageView
        val nameTv = shareView.findViewById<View>(R.id.name) as TextView
        val introTv = shareView.findViewById<View>(R.id.tip1) as TextView
        val qrImg = shareView.findViewById<View>(R.id.qr_img) as ImageView
        val shareImg = shareView.findViewById<View>(R.id.img_dedal) as ImageView
        nameTv.setText(GlobalsUserManager.userInfo?.name)

        Glide.with(this).asBitmap().circleCrop().error(R.mipmap.common_ic_user_head_default).load(GlobalsUserManager.userInfo?.avatar).into(object : CustomTarget<Bitmap>() {
            override fun onLoadCleared(placeholder: Drawable?) {

            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                header.setImageBitmap(resource)
                Glide.with(requireActivity())
                        .load(bean?.after_img)
                        .into(object : CustomTarget<Drawable>() {
                            override fun onResourceReady(
                                    resource: Drawable,
                                    transition: Transition<in Drawable>?
                            ) {
                                shareImg.setImageDrawable(resource)
                                qrImg.setImageBitmap(bitmap)
                                introTv.setText(bean?.title)

                                val shareBitmap = BitmapUtils.createUnShowBitmapFromLayout(shareView)
                                val shareDialog = CommonBottomSharePop(requireContext(), {
                                    val build = IShare.Builder()
                                            .setSite(it)
                                            .setTitle("")
                                            .setMessage("")
                                            .setImageBitmap(shareBitmap)
                                            .build()
                                    activity?.let { it1 -> shareService?.shareAddScore(ShareTypeConstants.TYPE_MEDAL, it1, build) }
                                })

                                XPopup.Builder(requireContext())
                                        .asCustom(shareDialog)
                                        .show()
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {

                            }

                            override fun onLoadFailed(errorDrawable: Drawable?) {
                                super.onLoadFailed(errorDrawable)
                                shareImg.setImageDrawable(errorDrawable)
                                qrImg.setImageBitmap(bitmap)
                                introTv.setText(bean?.title)

                                val shareBitmap = BitmapUtils.createUnShowBitmapFromLayout(shareView)
                                val shareDialog = CommonBottomSharePop(requireContext(), {
                                    val build = IShare.Builder()
                                            .setSite(it)
                                            .setTitle("")
                                            .setMessage("")
                                            .setImageBitmap(shareBitmap)
                                            .build()
                                    activity?.let { it1 -> shareService.shareAddScore(ShareTypeConstants.TYPE_MEDAL, it1, build) }
                                })

                                XPopup.Builder(requireContext())
                                        .asCustom(shareDialog)
                                        .show()
                            }
                        })
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
                header.setImageDrawable(errorDrawable)
            }

        })
    }

    private fun initListener() {
        //查看勋章大图
        myMedalAdapter?.onItemClick = {
                val pop =ShowMedalBigPop(requireContext(), binding.recyvlerView)
                pop.medalBean = it
                pop.show()
                //分享勋章
                pop.medalBigCallBack = {
                    getMediaShareView(it)
                }
        }
    }

    /**
     * 获取后台返回的有关个人信息的二维码地址
     */
    private fun getMediaShareView(bean: MedalDataBean?) {
        shareDataBean = bean
//        mViewModel.loadData()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("source_type", SHARE_SOURCE_TYPE_MEDAL.toString())
            jsonObject.put("source_id", "0")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val toRequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        mViewModel.getCMSShareData(toRequestBody)

    }

    /**
     * 初始化数据
     */
    private fun intData() {
        binding.swipeLayout.isRefreshing = true
        getMedal()
    }

    private fun initView() {
        myMedalAdapter = MyMedalAdapter(null)
        binding.swipeLayout.setOnRefreshListener(this)
        binding.recyvlerView.layoutManager = LinearLayoutManager(activity)
        binding.recyvlerView.adapter = myMedalAdapter
    }

    /**
     *
     *获取数据
     */
    private fun getMedal() {

        mViewModel.loadData {
            binding.swipeLayout.isRefreshing = false
        }
    }


    /**
     * 刷新
     */
    override fun onRefresh() {
        if (NetUtils.isNetworkConnected()) {
            getMedal()
        } else {
            binding.swipeLayout.setRefreshing(false)
            toast(getString(R.string.net_error))
        }
    }

    /**
     * 设置adapter数据
     */
    private fun setData(medalBean: MedalBean?) {
        if (medalBean == null) {
            return
        }
        val shareMedalList: ArrayList<MedalDataBean>? = medalBean.share_medal_finish_list
        medalBean.share_medal_not_finish_list?.let { shareMedalList?.addAll(it) }

        val checkMedalList: ArrayList<MedalDataBean>? = medalBean.check_medal_finish_list
        medalBean.check_medal_not_finish_list?.let { checkMedalList?.addAll(it) }

        val specialMedalList: ArrayList<MedalDataBean>? = medalBean.special_medal_finish_list

        val studyMedalList: ArrayList<MedalDataBean>? = medalBean.study_medal_finish_list
        medalBean.study_medal_not_finish_list?.let { studyMedalList?.addAll(it) }

//        //移除没有点亮的神秘勋章
//        if (specialMedalList != null && specialMedalList.size > 0) {
//            val iterator: MutableIterator<*> = specialMedalList.iterator()
//            while (iterator.hasNext()) {
//                val index = iterator.next() as MedalDataBean
//                if (index.is_obtain.equals("0")) {
//                    iterator.remove()
//                }
//            }
//        }

        val medalTypeList = ArrayList<MedalTypeBean>()
        //根据数据情况添加数据
        if (checkMedalList != null) {
            medalTypeList.add(MedalTypeBean(TYPE_SIGN_MEDAL, getString(R.string.medal_str_sign),
                    medalBean.check_medal_count, false, checkMedalList))
        }
        if (studyMedalList != null) {
            medalTypeList.add(MedalTypeBean(TYPE_STUDY_MEDAL, getString(R.string.medal_str_study),
                    medalBean.studyplan_medal_count, false, studyMedalList))
        }
        if (specialMedalList != null) {
            medalTypeList.add(MedalTypeBean(TYPE_SPECIAL_MEDAL, getString(R.string.medal_str_special),
                    medalBean.special_medal_count, false, specialMedalList))
        }
        if (shareMedalList != null) {
            medalTypeList.add(MedalTypeBean(TYPE_SHARE_MEDAL, getString(R.string.medal_str_share),
                    medalBean.share_medal_count, false, shareMedalList))
        }

        myMedalAdapter?.setNewInstance(medalTypeList)
        binding.swipeLayout.isRefreshing = false
    }

}