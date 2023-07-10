package com.mooc.studyroom.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.manager.BaseObserver
import com.mooc.commonbusiness.model.search.EBookBean
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.dp2px
import com.mooc.common.utils.bitmap.BitmapUtils
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.adapter.SearchShareBookAdapter
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.NormalConstants
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.utils.IShare
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.studyroom.R
import com.mooc.studyroom.StudyRoomApi
import com.mooc.studyroom.databinding.StudyroomActivityInviteFriendsReadbookBinding
import com.mooc.studyroom.model.ShareBookCodeModel
import com.mooc.studyroom.ui.adapter.ShareBookInviteAdapter
import me.devilsen.czxing.code.BarcodeWriter

/**
 * 邀请朋友页面
 */
@Route(path = Paths.PAGE_INVITE_READ_BOOK)
class InviteFriendReadBookActivity : BaseActivity() {

    var binding: StudyroomActivityInviteFriendsReadbookBinding? = null
    var bookList = ArrayList<EBookBean>()   //书籍集合
    var searchShareBookAdapter = SearchShareBookAdapter(bookList)


    companion object {
        const val INTENT_REQUESTCODE_TOSEARCHBOOK = 0   //跳转到搜索页面请求码
        const val SHARE_BOOK_NUM = 5   //分享书籍数量
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<StudyroomActivityInviteFriendsReadbookBinding>(this, R.layout.studyroom_activity_invite_friends_readbook)
        initView()
        initData()
        initListener()

    }


    fun initView() {
        binding?.btnToShare?.setOnClickListener {
            if (bookList.size != SHARE_BOOK_NUM) {
                Toast.makeText(this, NormalConstants.SHARE_NUM_TIP, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            createShareBitmap()
        }

    }

    /**
     * 将选择的内容生成一张图片，
     * 需要先获取分享的url，然后生成二维码
     * ，分享出去
     */
    private fun createShareBitmap() {
        //
        if (bookList.isEmpty()) return

        var bookids = ""
        bookList.forEachIndexed { index, eBookBean ->
            bookids += eBookBean.id
            if (index != bookList.lastIndex) {
                bookids += ","
            }
        }
        ApiService.getRetrofit().create(StudyRoomApi::class.java).getInviteFriendReadBookInfo(bookids)
                .compose(RxUtils.applySchedulers<HttpResponse<ShareBookCodeModel>>())
                .map {
                    val url = it.data.url
                    //根据字符串生成bitmap
//                    val qrImage = CodeUtils.createImage(url, Utils.dp2px(100f).toInt(), Utils.dp2px(100f).toInt(), null)
                    val barcodeWriter = BarcodeWriter()
                    val qrImage = barcodeWriter.write(url, 100.dp2px())
//                    val qrImage = EncodingHandler.createQRCode(url, Utils.dp2px(80f).toInt())
                    val inflate = View.inflate(this, R.layout.studyroom_layout_share_invation_read_book, null)
                    val rvShareBook = inflate.findViewById<RecyclerView>(R.id.rvShareBook)
                    val tvUserName = inflate.findViewById<TextView>(R.id.tvUserName)
                    val tvTitle = inflate.findViewById<TextView>(R.id.tvTitle)
                    val iVQrImg = inflate.findViewById<ImageView>(R.id.iVQrImg)
                    iVQrImg.setImageBitmap(qrImage)
                    tvTitle.text = it.data.words
                    tvUserName.text = "——${GlobalsUserManager.userInfo?.name}"
                    rvShareBook.layoutManager = LinearLayoutManager(this)
                    rvShareBook.adapter = ShareBookInviteAdapter(bookList, searchShareBookAdapter.coverMap)
                    val createUnShowBitmapFromLayout = BitmapUtils.createUnShowBitmapFromLayout(inflate)
                    createUnShowBitmapFromLayout
                }
                .subscribe(object : BaseObserver<Bitmap>(this) {
                    override fun onSuccess(createUnShowBitmapFromLayout: Bitmap) {

                        val commonBottomSharePop = CommonBottomSharePop(this@InviteFriendReadBookActivity)
                        commonBottomSharePop.onItemClick = {
                            //将图片url，分享
                            shareImage(it, createUnShowBitmapFromLayout, commonBottomSharePop)
                        }
                        XPopup.Builder(this@InviteFriendReadBookActivity)
                                .asCustom(commonBottomSharePop)
                                .show()
                    }

                })


    }


    /**
     * 分享图片
     * @param it 分享位置
     */
    private fun shareImage(shareSite: Int, bitmap: Bitmap, commonBottomSharePop: CommonBottomSharePop) {
//        if(alertMsgBean.share_url.isEmpty()) return
//        //正确的分享地址，防止图片被篡改
//        if(!alertMsgBean.share_url.contains("moocnd.ykt.io") && !alertMsgBean.share_url.contains("learning.mil.cn") ) return

        val shareService = ARouter.getInstance().build(Paths.SERVICE_SHARE).navigation() as ShareSrevice
        shareService.share(this, IShare.Builder()
                .setSite(shareSite)
                .setTitle("")
                .setMessage("")
                .setImageBitmap(bitmap)
                .build()) {
                    //分享回调
                    bitmap.recycle()
                    commonBottomSharePop.dismiss()
                }
    }

    fun initListener() {
        binding?.commonTitle?.setOnLeftClickListener {
            finish()
        }
    }

    fun initData() {
        binding?.rcyBookShare?.layoutManager = LinearLayoutManager(this)
        searchShareBookAdapter.addFooterView(getFootView())
        binding?.rcyBookShare?.adapter = searchShareBookAdapter
        searchShareBookAdapter.addChildClickViewIds(R.id.ivAdd)
        searchShareBookAdapter.setOnItemChildClickListener { _, view, position ->
            if (view.id == R.id.ivAdd) {
                //点击移除选中的书籍
                val get = bookList.get(position)
                bookList.remove(get)
                searchShareBookAdapter.notifyDataSetChanged()

                checkBookListSize()
            }
        }
    }

    /**
     * 检测书单列表大小改变ui
     */
    fun checkBookListSize() {
        if (bookList.size < SHARE_BOOK_NUM) {
            if (searchShareBookAdapter.footerLayoutCount <= 0) {
                searchShareBookAdapter.addFooterView(getFootView())
            }
            binding?.btnToShare?.setBackgroundResource(R.drawable.shape_radius20_gradient_gray)
        } else {
            //隐藏脚布局
            if (searchShareBookAdapter.footerLayoutCount > 0) {
                val childAt = searchShareBookAdapter.footerLayout?.getChildAt(0)
                childAt?.let { searchShareBookAdapter.removeFooterView(it) }
            }
            binding?.btnToShare?.setBackgroundResource(R.drawable.shape_radius20_gradient_green)
        }
    }

    /**
     * 适配器脚布局
     */
    private fun getFootView(): View {
        val inflate = View.inflate(this, R.layout.studyroom_item_invite_add_ebook_foot, null)
        inflate.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_SEARCH_ADD_BOOK).withParcelableArrayList(IntentParamsConstants.INTENT_BOOKS_PARAMS,bookList).navigation(this,
                INTENT_REQUESTCODE_TOSEARCHBOOK
            )
        }
        return inflate
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //获取搜索页面的书籍，添加到列表
        if (resultCode == Activity.RESULT_OK && requestCode == INTENT_REQUESTCODE_TOSEARCHBOOK) {
            val checkList: ArrayList<EBookBean>? = data?.getParcelableArrayListExtra<EBookBean>(IntentParamsConstants.INTENT_BOOKS_PARAMS)
            checkList?.let {
                bookList.clear()
                bookList.addAll(it)
            }
            searchShareBookAdapter.notifyDataSetChanged()

            checkBookListSize()

        }
    }


}