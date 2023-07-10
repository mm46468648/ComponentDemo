package com.mooc.my.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.Gson
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.visiable
import com.mooc.common.utils.TimeUtils
import com.mooc.commonbusiness.adapter.DynamicImagesAdapter
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.glide.GlideTransform
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.model.my.NodeBean
import com.mooc.commonbusiness.model.search.*
import com.mooc.commonbusiness.model.studyproject.StudyDynamic
import com.mooc.commonbusiness.utils.format.HtmlFromatUtil
import com.mooc.commonbusiness.utils.format.StringFormatUtil
import com.mooc.commonbusiness.utils.format.StringFormatUtil.Companion.infoTimeToString
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.my.R
import com.mooc.my.databinding.MyIncludeItemMyshareBottomBinding
import com.mooc.my.databinding.MyItemUserzoneMyshareDynamicBinding
import com.mooc.my.databinding.MyItemUserzoneMyshareDynamicFollowupBinding
import com.mooc.my.model.SchoolSourceBean
import org.jetbrains.annotations.NotNull

/**
个人信息页面的适配器
 * @Author limeng
 * @Date 2020/11/4-10:36 AM
 */
class UserShareInfoAdapter(data: ArrayList<SchoolSourceBean>?) :
    BaseDelegateMultiAdapter<SchoolSourceBean, BaseViewHolder>(data) {
    var displayTypes = arrayOf(
        ResourceTypeConstans.TYPE_COURSE, ResourceTypeConstans.TYPE_TRACK,
        ResourceTypeConstans.TYPE_ALBUM, ResourceTypeConstans.TYPE_ARTICLE,
        ResourceTypeConstans.TYPE_PERIODICAL, ResourceTypeConstans.TYPE_E_BOOK,
        ResourceTypeConstans.TYPE_MICRO_LESSON, ResourceTypeConstans.TYPE_BAIKE,
        ResourceTypeConstans.TYPE_NOTE, ResourceTypeConstans.TYPE_STUDY_PLAN_DYNAMIC,
        ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE, ResourceTypeConstans.TYPE_SPECIAL,
        ResourceTypeConstans.TYPE_QUESTIONNAIRE, ResourceTypeConstans.TYPE_COLUMN_ARTICLE
    )

    init {
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<SchoolSourceBean>() {
            override fun getItemType(@NotNull data: List<SchoolSourceBean>, position: Int): Int {
                // 根据数据，自己判断应该返回的类型
                val resultBean = data?.get(position)
                val type: Int = resultBean?.resource_type ?: ResourceTypeConstans.TYPE_UNDEFINE
                //无图类型x
                return if (type == ResourceTypeConstans.TYPE_STUDY_PLAN_DYNAMIC) {
//                    val dataBean: StudyDynamic = resultBean.detail_info as StudyDynamic
                    val toJson = Gson().toJson(resultBean.detail_info)
                    val dataBean = Gson().fromJson(toJson, StudyDynamic::class.java)
                    //当activity_type == 1,并且source_type_id 28 代表跟读资源，展示跟读语音列表
                    if (dataBean.activity_type == 1 && dataBean.source_type_id === ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE) {
                        ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE
                    } else {
                        ResourceTypeConstans.TYPE_STUDY_PLAN_DYNAMIC
                    }
                } else if (resultBean?.resource_type in displayTypes) {
                    resultBean?.resource_type
                } else { //未定义类型
                    ResourceTypeConstans.TYPE_UNDEFINE
                }
            }
        })
        // 第二部，绑定 item 类型
        getMultiTypeDelegate()
            ?.addItemType(ResourceTypeConstans.TYPE_COURSE, R.layout.my_userinfo_course)
            ?.addItemType(ResourceTypeConstans.TYPE_TRACK, R.layout.my_userinfo_track)
            ?.addItemType(ResourceTypeConstans.TYPE_ALBUM, R.layout.my_userinfo_album)
            ?.addItemType(ResourceTypeConstans.TYPE_ARTICLE, R.layout.my_userinfo_article)
            ?.addItemType(ResourceTypeConstans.TYPE_COLUMN_ARTICLE, R.layout.my_userinfo_article)
            ?.addItemType(ResourceTypeConstans.TYPE_PERIODICAL, R.layout.my_userinfo_periodical)
            ?.addItemType(ResourceTypeConstans.TYPE_E_BOOK, R.layout.my_userinfo_ebook)
            ?.addItemType(ResourceTypeConstans.TYPE_MICRO_LESSON, R.layout.my_userinfo_slightcourse)
            ?.addItemType(ResourceTypeConstans.TYPE_BAIKE, R.layout.my_userinfo_baike)
            ?.addItemType(ResourceTypeConstans.TYPE_NOTE, R.layout.my_userinfo_note)
            ?.addItemType(
                ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE,
                R.layout.my_item_userzone_myshare_dynamic_followup
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_STUDY_PLAN_DYNAMIC,
                R.layout.my_item_userzone_myshare_dynamic
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_SPECIAL,
                R.layout.my_item_userinfo_myshare_special
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_QUESTIONNAIRE,
                R.layout.my_item_userinfo_myshare_special
            )

            ?.addItemType(ResourceTypeConstans.TYPE_UNDEFINE, R.layout.common_item_empty)
    }


    override fun convert(holder: BaseViewHolder, item: SchoolSourceBean) {
        val type: Int = getItemViewType(holder.layoutPosition)

        when (type) {
            ResourceTypeConstans.TYPE_UNDEFINE -> {
            }
            ResourceTypeConstans.TYPE_QUESTIONNAIRE -> {//问卷
                setQuestionnaireData(holder, item)
                setBottomData(holder.getView(R.id.incloudBottom), item, "问卷")

            }
            ResourceTypeConstans.TYPE_SPECIAL -> {

                setSpecialData(holder, item)
            }
            ResourceTypeConstans.TYPE_COURSE -> {
                holder.setGone(R.id.line, true)

                setCourseData(holder, item)
                setBottomData(holder.getView(R.id.incloudBottom), item, "")

            }
            ResourceTypeConstans.TYPE_TRACK -> {
                holder.setGone(R.id.tarck_line, true)
                setTrackData(holder, item)
                setBottomData(holder.getView(R.id.incloudBottom), item, "")

            }
            ResourceTypeConstans.TYPE_ALBUM -> {
                holder.setGone(R.id.album_line, true)
                setAlumData(holder, item)
                setBottomData(holder.getView(R.id.incloudBottom), item, "")

            }
            ResourceTypeConstans.TYPE_COLUMN_ARTICLE,
            ResourceTypeConstans.TYPE_ARTICLE -> {
                holder.setGone(R.id.artical_line, true)
                holder.setGone(R.id.line, true)
                holder.setGone(R.id.tv_author, true)
                setArticalData(holder, item)
                setBottomData(holder.getView(R.id.incloudBottom), item, "")

            }
            ResourceTypeConstans.TYPE_PERIODICAL -> {
                setPeriodicalData(holder, item)
                setBottomData(holder.getView(R.id.incloudBottom), item, "")

            }
            ResourceTypeConstans.TYPE_E_BOOK -> {
                setBookData(holder, item)
                setBottomData(holder.getView(R.id.incloudBottom), item, "")

            }
            ResourceTypeConstans.TYPE_MICRO_LESSON -> {
                holder.setGone(R.id.line, true)
                setMicroLessonData(holder, item)
                setBottomData(holder.getView(R.id.incloudBottom), item, "")

            }
            ResourceTypeConstans.TYPE_BAIKE -> {
                holder.setGone(R.id.baike, true)
//                holder.setGone(R.id.line, true)

                setBaikeData(holder, item)
                setBottomData(holder.getView<ConstraintLayout>(R.id.incloudBottom), item, "百科")

            }

            ResourceTypeConstans.TYPE_NOTE -> {
                setNoteData(holder, item)
                setBottomData(holder.getView(R.id.incloudBottom), item, "笔记")

            }
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {//跟读资源动态
                holder.setGone(R.id.cl_gd_bottom, true)
                holder.setGone(R.id.line, true)
//                setStudyPlanData(holder, item)
                val binding =
                    DataBindingUtil.bind<MyItemUserzoneMyshareDynamicFollowupBinding>(holder.itemView)
//                val bean = item.detail_info as StudyDynamic
                val toJson = Gson().toJson(item.detail_info)
                val bean = Gson().fromJson(toJson, StudyDynamic::class.java)
                binding?.let { setFolloupItem(it, bean) }
                binding?.incloudItem?.tvDynamicTitle?.let { setTvSpan(it, bean) }
                setBottomData(holder.getView<ConstraintLayout>(R.id.incloudBottom), item, "动态")

            }
            ResourceTypeConstans.TYPE_STUDY_PLAN_DYNAMIC -> {
                holder.setGone(R.id.line, true)

                var binding =
                    DataBindingUtil.bind<MyItemUserzoneMyshareDynamicBinding>(holder.itemView)
                val toJson = Gson().toJson(item.detail_info)
                val bean = Gson().fromJson(toJson, StudyDynamic::class.java)
                if (bean.activity_type == 0 && !bean.publish_content.isNullOrEmpty()) {
                    binding?.incloudItem?.tvDynamicTitle?.visiable(true)
                    binding?.incloudItem?.tvDynamicTitle?.let { setTvSpan(it, bean) }

                } else {
                    binding?.incloudItem?.tvDynamicTitle?.visiable(false)

                }
                if (bean.activity_type == 0 && bean.publish_img_list?.size ?: 0 > 0) {
                    binding?.incloudItem?.recyclerView?.visiable(true)

//
                    val adapter = DynamicImagesAdapter(bean.publish_img_list)
                    val manager = GridLayoutManager(context, 3)
                    binding?.incloudItem?.recyclerView?.setLayoutManager(manager)
                    binding?.incloudItem?.recyclerView?.setAdapter(adapter)
                    binding?.incloudItem?.recyclerView?.setNestedScrollingEnabled(false)
                } else {
                    binding?.incloudItem?.recyclerView?.visiable(false)

                }
                if (bean.activity_type == 0 || (bean.activity_type == 1 && bean.publish_content.isNullOrEmpty())) {
                    binding?.incloudItem?.voicePlayerController?.visibility = View.GONE
                } else {
                    if (bean.activity_type == 1) {
                        binding?.incloudItem?.voicePlayerController?.setPlayPath(bean.publish_content)
                        binding?.incloudItem?.voicePlayerController?.setTotleTimeLength(bean.activity_content_long)
                        binding?.incloudItem?.voicePlayerController?.visiable(true)
                    } else {
                        binding?.incloudItem?.voicePlayerController?.visiable(false)

                    }


                }
                if (bean.activity_type == 0 || (bean.activity_type == 1 && !bean.publish_content.isNullOrEmpty())) {
                    binding?.incloudItem?.tvVoiceDel?.visiable(false)
                } else {
                    binding?.incloudItem?.tvVoiceDel?.visiable(true)
                }

                setBottomData(holder.getView<ConstraintLayout>(R.id.incloudBottom), item, "动态")

            }
            else -> {

            }
        }
    }

    private fun setQuestionnaireData(holder: BaseViewHolder, bean: SchoolSourceBean) {
        val gson = Gson()
        val toJson = gson.toJson(bean.detail_info)
        val item = gson.fromJson(toJson, QuestionnaireDetailBean::class.java)
        holder.setText(R.id.tvTitle, item.title)
        holder.setText(R.id.tvContent, HtmlFromatUtil.html2Text(item.des))
    }

    private fun setSpecialData(holder: BaseViewHolder, item: SchoolSourceBean) {
        val gson = Gson()
        val toJson = gson.toJson(item.detail_info)
        val bean = gson.fromJson(toJson, SpecialDetailBean::class.java)
        holder.setText(R.id.tvTitle, bean.title)
        holder.setText(R.id.tvContent, bean.about)
        //设置type
        var typeStr = "合集"
        if (!TextUtils.isEmpty(bean.identity_name)) {
            typeStr = bean.identity_name.toString()
        }
        setBottomData(holder.getView(R.id.incloudBottom), item, typeStr)

    }

    private fun setNoteData(holder: BaseViewHolder, bean: SchoolSourceBean) {
        holder.setGone(R.id.tvLable, true)
        holder.setGone(R.id.tvPriseNum, true)
        val gson = Gson()
        val toJson = gson.toJson(bean.detail_info)
        val item = gson.fromJson(toJson, NodeBean::class.java)
        holder.setText(R.id.tvNoteTitle, item.other_resource_title)
        holder.setText(R.id.tvNoteContent, item.content)
        holder.setText(
            R.id.tvNoteBelong,
            context.resources.getString(R.string.string_vertical_bar) + item.recommend_title
        )

        if (item.recommend_title?.isNotEmpty() == true) {
            holder.setGone(R.id.tvNoteBelong, false)
        } else {
            holder.setGone(R.id.tvNoteBelong, true)
        }

    }


    /**
     * 设置百科数据
     */
    private fun setBaikeData(holder: BaseViewHolder, bean: SchoolSourceBean) {
        val gson = Gson()
        val toJson = gson.toJson(bean.detail_info)
        val item = gson.fromJson(toJson, BaiKeBean::class.java)
        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_content, item.content)
    }

    /**
     * 微课数据
     */
    private fun setMicroLessonData(holder: BaseViewHolder, bean: SchoolSourceBean) {

        val gson = Gson()
        val toJson = gson.toJson(bean.detail_info)
        val item = gson.fromJson(toJson, MicroBean::class.java)
        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_source, item.platform_zh)
        val time = item.video_duration.toLong()
        holder.setText(R.id.tv_duration, TimeFormatUtil.formatAudioPlayTime(time))


        if (!item.picture.isNullOrBlank()) {
            val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
            Glide.with(context)
                .load(item.picture)
                .transform(GlideTransform.centerCropAndRounder2)
                .into(imageView!!)
        }
    }

    /**
     * 设置书籍数据
     */
    private fun setBookData(holder: BaseViewHolder, bean: SchoolSourceBean) {

        val gson = Gson()
        val toJson = gson.toJson(bean.detail_info)
        val item = gson.fromJson(toJson, EBookBean::class.java)
        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_author, item.writer)
        holder.setText(R.id.tv_source, item.press)
        holder.setText(
            R.id.tv_word_num,
            StringFormatUtil.formatPlayCount(item.word_count.toLong()) + "字"
        )



        if (!item.picture.isBlank()) {
            val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
            Glide.with(context)
                .load(item.picture)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(imageView!!)
        }
    }

    /**
     * 期刊数据
     */
    private fun setPeriodicalData(holder: BaseViewHolder, bean: SchoolSourceBean) {

        val gson = Gson()
        val toJson = gson.toJson(bean.detail_info)
        val item = gson.fromJson(toJson, PeriodicalBean::class.java)

        holder.setText(R.id.tv_title, item.title)

        if (!item.basic_cover_url.isNullOrBlank()) {
            val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
            Glide.with(context)
                .load(item.basic_cover_url)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(imageView!!)
        }
        if (TextUtils.isEmpty(item.basic_creator)) {
            holder.setVisible(R.id.tv_author, false)
        } else {
            holder.setVisible(R.id.tv_author, true)
            holder.setText(R.id.tv_author, java.lang.String.format("作者：%s", item.basic_creator))

        }
        if (TextUtils.isEmpty(item.basic_date_time)) {
            holder.setVisible(R.id.tv_publish_time, false)
        } else {
            holder.setVisible(R.id.tv_publish_time, true)
            holder.setText(R.id.tv_publish_time, item.basic_date_time)
        }
    }

    /**
     * 设置文章数据
     */
    private fun setArticalData(holder: BaseViewHolder, bean: SchoolSourceBean) {
        val gson = Gson()
        val toJson = gson.toJson(bean.detail_info)
        val item = gson.fromJson(toJson, ArticleBean::class.java)
        holder.setText(R.id.tv_title, item.title)

        if (item.source.isNotBlank()) {
            holder.setText(R.id.tv_source, item.source)
            holder.setVisible(R.id.tv_source, true)
        } else {
            holder.setVisible(R.id.tv_source, false)
        }
        if (!item.picture.isNullOrBlank()) {
            val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
            Glide.with(context)
                .load(item.picture)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(imageView!!)
        }

    }

    /**
     * 设置alum数据 音频课
     */
    private fun setAlumData(holder: BaseViewHolder, bean: SchoolSourceBean) {
        var gson = Gson()
        val toJson = gson.toJson(bean.detail_info)
        val item = gson.fromJson(toJson, AlbumBean::class.java)
        holder.setText(R.id.tv_title, item.album_title)
        holder.setText(R.id.tv_play_num, item.play_count.toString())
        holder.setText(R.id.tv_collection, item.include_track_count.toString() + "集")

        if (item.cover_url_middle.isNotBlank()) {
            val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
            Glide.with(context)
                .load(item.cover_url_middle)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(imageView!!)
        }
    }


    /**
     * 设置音频数据
     */
    private fun setTrackData(holder: BaseViewHolder, bean: SchoolSourceBean) {
        var gson = Gson()
        val toJson = gson.toJson(bean.detail_info)
        val item = gson.fromJson(toJson, TrackBean::class.java)
        holder.setText(R.id.tv_title, item.trackTitle)
        holder.setText(R.id.tv_source, item.albumTitle)
        holder.setText(R.id.tv_play_num, item.play_count.toString())
        holder.setText(R.id.tv_duration, TimeUtils.timeParse(item.duration))



        if (item.cover_url_small.isNotBlank()) {
            val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
            Glide.with(context)
                .load(item.cover_url_small)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(imageView!!)
        }
    }

    /**
     * 设置底部数据展示
     */
    private fun setBottomData(
        layout: ConstraintLayout,
        item: SchoolSourceBean,
        lableString: String
    ) {
        val binding = DataBindingUtil.bind<MyIncludeItemMyshareBottomBinding>(layout)
        binding?.showLabel = lableString.isNotEmpty()
        binding?.bottomLabelStr = lableString
        binding?.uid = GlobalsUserManager.uid
        binding?.schoolSourceBean = item
        binding?.time = infoTimeToString(item.created_time)
        if (item.is_like) {
            binding?.ivPrise?.setImageResource(R.mipmap.common_icon_red_like)
        } else {
            binding?.ivPrise?.setImageResource(R.mipmap.common_icon_circle_item_unlike)

        }

//        holder.setText(R.id.tvTime, )
//        var drawable_n: Drawable? = null
//        if (item.is_like) {
//            drawable_n = ContextCompat.getDrawable(context, R.mipmap.common_icon_red_like)!!;
//
//        } else {
//            drawable_n = ContextCompat.getDrawable(context, R.mipmap.common_icon_circle_item_unlike)!!;
//
//        }
//        drawable_n.setBounds(0, 0, drawable_n.getMinimumWidth(), drawable_n.getMinimumHeight()) //此为必须写的
//        val textview = holder.getView<TextView>(R.id.tvPriseNum)
//        textview.setCompoundDrawables(drawable_n, null, null, null)
//        holder.setText(R.id.tvPriseNum, item.like_count.toString())
//        if (!lableString.isNullOrEmpty()) {// lableString只有 动态 笔记 百科 有这个值
//            holder.setGone(R.id.tvLabel, false)
//            holder.setText(R.id.tvLabel, lableString)
//        } else {
//            holder.setGone(R.id.tvLabel, true)
//
//        }
//        if (GlobalsUserManager.uid.equals(item.user)) {
//            holder.setGone(R.id.ibDelete, false)
//        }else{
//            holder.setGone(R.id.ibDelete, true             )
//
//        }


    }

    /**
     * 设置关于课程的数据
     */
    private fun setCourseData(holder: BaseViewHolder, item: SchoolSourceBean) {


        val gson = Gson()
        val toJson = gson.toJson(item.detail_info)
        val bean = gson.fromJson(toJson, CourseBean::class.java)


        holder.setText(R.id.tv_title, bean.title)
        holder.setText(R.id.tv_source, bean.org)
        holder.setText(R.id.tv_opening, bean.course_start_time)
        var tvStaffStr =
            if (bean.staffsBeenList != null && bean.staffsBeenList?.size!! > 0) bean.staffsBeenList?.get(
                0
            )?.name else ""
        if (bean.staffsBeenList != null && bean.staffsBeenList?.size!! > 1) {
            tvStaffStr += "等"
        }
        holder.setText(R.id.tv_author, tvStaffStr)

        val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
        Glide.with(context)
            .load(bean.picture)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .centerCrop()
            .into(imageView!!)
        setResourceType(holder, bean)

        //设置type
        holder.setText(R.id.tv_course_Lable, R.string.course)
        holder.setText(R.id.tvTypeCommonCourse, R.string.course)
        if (!TextUtils.isEmpty(bean.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
            holder.setText(R.id.tvTypeCommonCourse, bean.identity_name)
            holder.setGone(R.id.tvTypeCommonCourse, false)
            holder.setGone(R.id.tv_course_Lable, true)
        } else {
            holder.setGone(R.id.tvTypeCommonCourse, true)
            holder.setGone(R.id.tv_course_Lable, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setResourceType(holder: BaseViewHolder, detailBean: CourseBean) {
        val isHaveExam: String? = detailBean.is_have_exam
        val isVerified: String? = detailBean.verified_active
        val isFree: String? = detailBean.is_free
        val platform: Int = detailBean.platform
        var noExamAndVerified: String? = null

        val tvExamType: TextView = holder.getView(R.id.tvType)

        val tvCertificateType: TextView = holder.getView(R.id.tvCertificateType)
        val tvPriceType: TextView = holder.getView(R.id.tvPriceType)
        if (isHaveExam == "1") {
            tvExamType.text = context.resources.getString(R.string.course_str_have_exam)
        } else {
            tvExamType.text = context.resources.getString(R.string.course_str_no_exam)
        }
        tvExamType.setTextColor(context.resources.getColor(R.color.colorPrimary))

        if (isVerified == "1" || isVerified == "1.0") {
//            if (platform == CoursePlatFormConstants.COURSE_PLATFORM_MOOC) {
//                tvCertificateType.text = context.getString(R.string.point) + context.resources.getString(R.string.course_str_charge_certificate)
//            } else {
//                tvCertificateType.text = context.getString(R.string.point) + context.resources.getString(R.string.course_str_free_certificate)
//            }
            tvCertificateType.text =
                context.getString(R.string.point) + context.resources.getString(R.string.course_str_have_certificate)

        } else {
            tvCertificateType.text =
                context.getString(R.string.point) + context.resources.getString(R.string.course_str_no_certificate)
        }
        tvCertificateType.setTextColor(context.resources.getColor(R.color.colorPrimary))

        if (isFree == "0") {
            tvPriceType.text =
                context.getString(R.string.point) + context.resources.getString(R.string.course_str_pay)
        } else {
            tvPriceType.text =
                context.getString(R.string.point) + context.resources.getString(R.string.course_str_free)
        }
        tvPriceType.setTextColor(context.resources.getColor(R.color.colorPrimary))

    }

    /**
     * 设置跟读
     *
     * @param itemStudyFirendCircleAlbumBinding
     * @param dataBean
     */
    private fun setFolloupItem(
        itemStudyFirendCircleAlbumBinding: MyItemUserzoneMyshareDynamicFollowupBinding,
        dataBean: StudyDynamic
    ) {
        val layout = LinearLayoutManager(context)
        itemStudyFirendCircleAlbumBinding.incloudItem.recyclerView.setLayoutManager(layout)
        val repeat_record = dataBean?.extra_info?.repeat_record
        if (repeat_record != null) {
            itemStudyFirendCircleAlbumBinding.incloudItem.recyclerView.setVisibility(View.VISIBLE)
            val followUpVoiceDynamicAdapter = FollowUpVoiceDynamicAdapter(repeat_record)

//            boolean expandTvVisibale = itemViewHolder.tvFollowUpAudioExpand.getVisibility() == View.GONE;
            if (repeat_record.size > 4) {   //如果超过6条，显示4条，并且显示展开view
                itemStudyFirendCircleAlbumBinding.incloudItem.tvFollowUpAudioExpand.setText("展开")
                itemStudyFirendCircleAlbumBinding.incloudItem.tvFollowUpAudioExpand.setVisibility(
                    View.VISIBLE
                )
                itemStudyFirendCircleAlbumBinding.incloudItem.tvFollowUpAudioExpand.setCompoundDrawables(
                    null,
                    null,
                    getTvFollowupDrawableRight(itemStudyFirendCircleAlbumBinding),
                    null
                )
                //                followUpVoiceDynamicAdapter.setExpand(false);
                val subList: java.util.ArrayList<StudyDynamic.RepeatRecordBean> =
                    java.util.ArrayList<StudyDynamic.RepeatRecordBean>()
                for (i in 0..3) {   //截取前4个音频
                    subList.add(repeat_record[i])
                }
                followUpVoiceDynamicAdapter.setNewData(subList)
                itemStudyFirendCircleAlbumBinding.incloudItem.tvFollowUpAudioExpand.setOnClickListener(
                    View.OnClickListener {
                        val equals =
                            "展开" == itemStudyFirendCircleAlbumBinding.incloudItem.tvFollowUpAudioExpand.getText()
                                .toString()
                        val expandStr = if (equals) "收起" else "展开"
                        //                        int drawRight = equals? R.mipmap.ic_arrow_up_blue : R.mipmap.ic_arrow_down_blue;
                        //                        followUpVoiceDynamicAdapter.setExpand(equals);
                        if (equals) {
                            followUpVoiceDynamicAdapter.setNewData(repeat_record)
                        } else {
                            followUpVoiceDynamicAdapter.setNewData(subList)
                        }
                        itemStudyFirendCircleAlbumBinding.incloudItem.tvFollowUpAudioExpand.setText(
                            expandStr
                        )
                        val tvFollowupDrawableRight =
                            getTvFollowupDrawableRight(itemStudyFirendCircleAlbumBinding)
                        itemStudyFirendCircleAlbumBinding.incloudItem.tvFollowUpAudioExpand.setCompoundDrawables(
                            null,
                            null,
                            tvFollowupDrawableRight,
                            null
                        )
                    })
            } else {
                followUpVoiceDynamicAdapter.setNewData(repeat_record)
                itemStudyFirendCircleAlbumBinding.incloudItem.tvFollowUpAudioExpand.setVisibility(
                    View.GONE
                )
            }
            itemStudyFirendCircleAlbumBinding.incloudItem.recyclerView.setAdapter(
                followUpVoiceDynamicAdapter
            )
            itemStudyFirendCircleAlbumBinding.incloudItem.recyclerView.setVisibility(View.VISIBLE)
        }
    }

    private fun getTvFollowupDrawableRight(itemViewHolder: MyItemUserzoneMyshareDynamicFollowupBinding): Drawable? {
        val equals = "展开" == itemViewHolder.incloudItem.tvFollowUpAudioExpand.getText().toString()
        val drawRight: Int =
            if (equals) R.mipmap.common_ic_arrow_down_blue else R.mipmap.common_ic_arrow_up_blue
        val drawable: Drawable = context.getResources().getDrawable(drawRight)
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        return drawable
    }

    /**
     * 动态特殊标签字体
     *
     * @param tv
     * @param dataBean
     */
    private fun setTvSpan(tv: TextView, dataBean: StudyDynamic) {
        val spannableString: SpannableString
        var title = ""
        var detail = ""
        if (!TextUtils.isEmpty(dataBean.source_title)) {
            title = dataBean?.source_title!!
        }
        if (!TextUtils.isEmpty(dataBean.publish_content)) {
            if (dataBean.activity_type == 0) {
                detail = dataBean.publish_content!!
            }
        }
        val str: String
        if (!TextUtils.isEmpty(title)) {
            tv.highlightColor = context.getResources().getColor(R.color.white)
            if (!TextUtils.isEmpty(detail)) {
                str = "#$title#$detail"
                spannableString = SpannableString(str)
                spannableString.setSpan(
                    ForegroundColorSpan(
                        context.getResources().getColor(R.color.color_4A90E2)
                    ), 0, title.length + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableString.setSpan(
                    ForegroundColorSpan(
                        context.getResources().getColor(R.color.color_6)
                    ), title.length + 2, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tv.text = spannableString
                tv.movementMethod = LinkMovementMethod.getInstance()
            } else {
                str = "#$title#"
                spannableString = SpannableString(str)
                spannableString.setSpan(
                    ForegroundColorSpan(
                        context.getResources().getColor(R.color.color_4A90E2)
                    ), 0, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tv.text = spannableString
                tv.movementMethod = LinkMovementMethod.getInstance()
            }
        } else {
            if (!TextUtils.isEmpty(detail)) {
                str = detail
                spannableString = SpannableString(str)
                spannableString.setSpan(
                    ForegroundColorSpan(
                        context.getResources().getColor(R.color.color_6)
                    ), 0, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tv.text = spannableString
            } else {
                tv.text = ""
            }
        }
    }
}