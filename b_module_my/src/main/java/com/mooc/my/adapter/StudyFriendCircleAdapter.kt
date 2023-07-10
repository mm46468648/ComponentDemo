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
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.Gson
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
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.commonbusiness.widget.HeadView
import com.mooc.my.R
import com.mooc.my.databinding.ItemStudyFirendCircleDynamicBinding
import com.mooc.my.databinding.MyIncludeItemStudyFirendCircleDynamicFollowupBinding
import com.mooc.my.databinding.MyItemStudyFirendCircleDynamicFollowupBinding
import com.mooc.my.model.SchoolSourceBean
import org.jetbrains.annotations.NotNull

/**
 *我的学友圈适配器
 * @author limeng
 * @date 2021/4/12
 */
class StudyFriendCircleAdapter(data: ArrayList<SchoolSourceBean>) :
    BaseDelegateMultiAdapter<SchoolSourceBean, BaseViewHolder>(data), LoadMoreModule {
    var displayTypes = arrayOf(
        ResourceTypeConstans.TYPE_COURSE, ResourceTypeConstans.TYPE_TRACK,
        ResourceTypeConstans.TYPE_ALBUM, ResourceTypeConstans.TYPE_ARTICLE,
        ResourceTypeConstans.TYPE_COLUMN_ARTICLE, ResourceTypeConstans.TYPE_PERIODICAL,
        ResourceTypeConstans.TYPE_E_BOOK, ResourceTypeConstans.TYPE_MICRO_LESSON,
        ResourceTypeConstans.TYPE_BAIKE, ResourceTypeConstans.TYPE_NOTE,
        ResourceTypeConstans.TYPE_STUDY_PLAN_DYNAMIC, ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE,
        ResourceTypeConstans.TYPE_SPECIAL, ResourceTypeConstans.TYPE_QUESTIONNAIRE
    )

    init {
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<SchoolSourceBean>() {
            override fun getItemType(@NotNull data: List<SchoolSourceBean>, position: Int): Int {
                // 根据数据，自己判断应该返回的类型
                val resultBean = data.get(position)
                val type: Int = resultBean.resource_type ?: ResourceTypeConstans.TYPE_UNDEFINE
                //无图类型
                return if (type == ResourceTypeConstans.TYPE_STUDY_PLAN_DYNAMIC) {
//                        val dataBean: StudyDynamic = resultBean.detail_info as StudyDynamic
                    val gson = Gson()
                    val toJson = gson.toJson(resultBean.detail_info)
                    val dataBean = gson.fromJson(toJson, StudyDynamic::class.java)
                    //当activity_type == 1,并且source_type_id 28 代表跟读资源，展示跟读语音列表
                    if (dataBean.activity_type == 1 && dataBean.source_type_id == ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE) {
                        ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE
                    } else {
                        ResourceTypeConstans.TYPE_STUDY_PLAN_DYNAMIC
                    }


                } else if (resultBean.resource_type in displayTypes) {
                    resultBean.resource_type
                } else { //未定义类型
                    ResourceTypeConstans.TYPE_UNDEFINE
                }
            }
        })
        // 第二部，绑定 item 类型
        getMultiTypeDelegate()
            ?.addItemType(
                ResourceTypeConstans.TYPE_COURSE,
                R.layout.my_item_study_firend_circle_course
            )
            ?.addItemType(ResourceTypeConstans.TYPE_TRACK, R.layout.item_study_firend_circle_track)
            ?.addItemType(ResourceTypeConstans.TYPE_ALBUM, R.layout.item_study_firend_circle_album)
            ?.addItemType(
                ResourceTypeConstans.TYPE_ARTICLE,
                R.layout.item_study_firend_circle_article
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_COLUMN_ARTICLE,
                R.layout.item_study_firend_circle_article
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_PERIODICAL,
                R.layout.item_study_firend_circle_periodical
            )
            ?.addItemType(ResourceTypeConstans.TYPE_E_BOOK, R.layout.item_study_firend_circle_ebook)
            ?.addItemType(
                ResourceTypeConstans.TYPE_MICRO_LESSON,
                R.layout.item_study_firend_circle_slight_course
            )
            ?.addItemType(ResourceTypeConstans.TYPE_BAIKE, R.layout.item_study_firend_circle_baike)
            ?.addItemType(ResourceTypeConstans.TYPE_NOTE, R.layout.item_study_firend_circle_note)
            ?.addItemType(
                ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE,
                R.layout.my_item_study_firend_circle_dynamic_followup
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_STUDY_PLAN_DYNAMIC,
                R.layout.item_study_firend_circle_dynamic
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_SPECIAL,
                R.layout.my_item_study_firend_circle_special
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_QUESTIONNAIRE,
                R.layout.my_item_study_firend_circle_questionnaire
            )
            ?.addItemType(ResourceTypeConstans.TYPE_UNDEFINE, R.layout.common_item_empty)
    }


    override fun convert(holder: BaseViewHolder, item: SchoolSourceBean) {
        var heaview = holder.getViewOrNull<HeadView>(R.id.ivHead)
        heaview?.setHeadImage(item.avatar, item.avatar_identity)
        val type: Int = getItemViewType(holder.layoutPosition)
        when (type) {
            ResourceTypeConstans.TYPE_UNDEFINE -> {

            }
            ResourceTypeConstans.TYPE_QUESTIONNAIRE -> {
                holder.setGone(R.id.tvPriseNum, false)
                val binding =
                    DataBindingUtil.bind<com.mooc.my.databinding.MyItemStudyFirendCircleQuestionnaireBinding>(
                        holder.itemView
                    )
                binding?.schoolSourceBean = item
                binding?.uid = GlobalsUserManager.uid
                setQuestionData(holder, item)
                setPriseData(holder, item)

            }
            ResourceTypeConstans.TYPE_SPECIAL -> {
                holder.setGone(R.id.tvPriseNum, false)
                val binding =
                    DataBindingUtil.bind<com.mooc.my.databinding.MyItemStudyFirendCircleSpecialBinding>(
                        holder.itemView
                    )
                binding?.schoolSourceBean = item
                binding?.uid = GlobalsUserManager.uid

                setSpecialData(holder, item)
                setPriseData(holder, item)

            }
            ResourceTypeConstans.TYPE_COURSE -> {
                holder.setGone(R.id.tvPriseNum, false)
                val binding =
                    DataBindingUtil.bind<com.mooc.my.databinding.MyItemStudyFirendCircleCourseBinding>(
                        holder.itemView
                    )
                binding?.schoolSourceBean = item
                binding?.uid = GlobalsUserManager.uid

                setCourseData(holder, item)
                setPriseData(holder, item)
            }
            ResourceTypeConstans.TYPE_TRACK -> {
                holder.setGone(R.id.tvPriseNum, false)

                val binding =
                    DataBindingUtil.bind<com.mooc.my.databinding.ItemStudyFirendCircleTrackBinding>(
                        holder.itemView
                    )
                binding?.schoolSourceBean = item
                binding?.uid = GlobalsUserManager.uid
                setTrackData(holder, item)
                setPriseData(holder, item)
            }
            ResourceTypeConstans.TYPE_ALBUM -> {
                holder.setGone(R.id.tvPriseNum, false)

                val binding =
                    DataBindingUtil.bind<com.mooc.my.databinding.ItemStudyFirendCircleAlbumBinding>(
                        holder.itemView
                    )
                binding?.schoolSourceBean = item
                binding?.uid = GlobalsUserManager.uid
                setAlumData(holder, item)
                setPriseData(holder, item)
            }
            ResourceTypeConstans.TYPE_COLUMN_ARTICLE,
            ResourceTypeConstans.TYPE_ARTICLE -> {
                val binding =
                    DataBindingUtil.bind<com.mooc.my.databinding.ItemStudyFirendCircleArticleBinding>(
                        holder.itemView
                    )
                binding?.schoolSourceBean = item
                binding?.uid = GlobalsUserManager.uid
                setArticalData(holder, item)
                setPriseData(holder, item)

            }
            ResourceTypeConstans.TYPE_PERIODICAL -> {
                holder.setGone(R.id.tvPriseNum, false)

                val binding =
                    DataBindingUtil.bind<com.mooc.my.databinding.ItemStudyFirendCirclePeriodicalBinding>(
                        holder.itemView
                    )
                binding?.schoolSourceBean = item
                binding?.uid = GlobalsUserManager.uid
                setPeriodicalData(holder, item)
                setPriseData(holder, item)

            }
            ResourceTypeConstans.TYPE_E_BOOK -> {
                val binding =
                    DataBindingUtil.bind<com.mooc.my.databinding.ItemStudyFirendCircleEbookBinding>(
                        holder.itemView
                    )
                binding?.schoolSourceBean = item
                binding?.uid = GlobalsUserManager.uid
                setBookData(holder, item)
                setPriseData(holder, item)
            }
            ResourceTypeConstans.TYPE_MICRO_LESSON -> {
                val binding =
                    DataBindingUtil.bind<com.mooc.my.databinding.ItemStudyFirendCircleSlightCourseBinding>(
                        holder.itemView
                    )
                binding?.schoolSourceBean = item
                binding?.uid = GlobalsUserManager.uid
                setMicroLessonData(holder, item)
                setPriseData(holder, item)

            }
            ResourceTypeConstans.TYPE_BAIKE -> {
                val binding =
                    DataBindingUtil.bind<com.mooc.my.databinding.ItemStudyFirendCircleBaikeBinding>(
                        holder.itemView
                    )
                binding?.schoolSourceBean = item
                binding?.uid = GlobalsUserManager.uid
                setBaikeData(holder, item)
                setPriseData(holder, item)

            }

            ResourceTypeConstans.TYPE_NOTE -> {
                val binding =
                    DataBindingUtil.bind<com.mooc.my.databinding.ItemStudyFirendCircleNoteBinding>(
                        holder.itemView
                    )
                binding?.schoolSourceBean = item
                binding?.uid = GlobalsUserManager.uid
                setNoteData(holder, item)
                setPriseData(holder, item)

            }
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {//跟读资源动态
//                setStudyPlanData(holder, item)
                setPriseData(holder, item)

                val binding =
                    DataBindingUtil.bind<MyItemStudyFirendCircleDynamicFollowupBinding>(holder.itemView)
                binding?.schoolSourceBean = item
                binding?.uid = GlobalsUserManager.uid
                val gson = Gson()
                val toJson = gson.toJson(item.detail_info)
                val bean = gson.fromJson(toJson, StudyDynamic::class.java)
                binding?.incloudItem?.tvDynamicTitle?.let { setTvSpan(it, bean) }
                binding?.let { setFolloupItem(binding.incloudItem, bean) }
            }
            ResourceTypeConstans.TYPE_STUDY_PLAN_DYNAMIC -> {
                setPriseData(holder, item)

                val binding =
                    DataBindingUtil.bind<ItemStudyFirendCircleDynamicBinding>(holder.itemView)
                binding?.schoolSourceBean = item
                binding?.uid = GlobalsUserManager.uid
                val gson = Gson()
                val toJson = gson.toJson(item.detail_info)
                val bean = gson.fromJson(toJson, StudyDynamic::class.java)
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


            }
            else -> {

            }
        }
    }


    /**
     * 设置底部数据展示
     */
    private fun setPriseData(holder: BaseViewHolder, item: SchoolSourceBean) {
        holder.setGone(R.id.tvPriseNum, false)
        holder.setText(R.id.tvPriseNum, item.like_count.toString())
        var drawable_n: Drawable? = null
        if (item.is_like) {
            drawable_n = ContextCompat.getDrawable(context, R.mipmap.common_icon_red_like)!!;

        } else {
            drawable_n =
                ContextCompat.getDrawable(context, R.mipmap.common_icon_circle_item_unlike)!!;

        }
        drawable_n.setBounds(
            0,
            0,
            drawable_n.getMinimumWidth(),
            drawable_n.getMinimumHeight()
        ) //此为必须写的
        val textview = holder.getView<TextView>(R.id.tvPriseNum)
        textview.setCompoundDrawables(drawable_n, null, null, null)


    }

    private fun setNoteData(holder: BaseViewHolder, bean: SchoolSourceBean) {
        val gson = Gson()
        val toJson = gson.toJson(bean.detail_info)
        val item = gson.fromJson(toJson, NodeBean::class.java)
        holder.setText(R.id.tvNoteTitle, item.other_resource_title)
        holder.setText(R.id.tvNoteContent, item.content)
        if (item.recommend_title.isNullOrEmpty()) {
            holder.setText(R.id.tvNoteBelong, "")
        } else {
            holder.setText(
                R.id.tvNoteBelong,
                context.resources.getString(R.string.string_vertical_bar) + item.recommend_title
            )
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
        holder.setGone(R.id.tv_content, TextUtils.isEmpty(item.content))

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
        holder.setText(R.id.tv_duration, TimeFormatUtil.timeParse(time))


        if (!item.picture.isNullOrBlank()) {
            val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
            Glide.with(context)
                .load(item.picture)
                .transform(GlideTransform.centerCropAndRounder2)
                .placeholder(R.mipmap.common_bg_cover_default)
                .error(R.mipmap.common_bg_cover_default)
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
                .transform(GlideTransform.centerCropAndRounder2)
                .placeholder(R.mipmap.common_bg_cover_default)
                .error(R.mipmap.common_bg_cover_default)
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

        if (!item.basic_cover_url.isBlank()) {
            val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
            Glide.with(context)
                .load(item.basic_cover_url)
                .transform(GlideTransform.centerCropAndRounder2)
                .placeholder(R.mipmap.common_bg_cover_default)
                .error(R.mipmap.common_bg_cover_default)
                .into(imageView!!)
        }
//        if (TextUtils.isEmpty(item.basic_creator)) {
        holder.setVisible(R.id.tv_author, false)
//        } else {
//            holder.setVisible(R.id.tv_author, true)
//            holder.setText(R.id.tv_author, java.lang.String.format("作者：%s", item.basic_creator))
//
//        }
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
                .transform(GlideTransform.centerCropAndRounder2)
                .placeholder(R.mipmap.common_bg_cover_default)
                .error(R.mipmap.common_bg_cover_default)
                .into(imageView!!)
        }

    }

    /**
     * 设置alum数据 音频课
     */
    private fun setAlumData(holder: BaseViewHolder, bean: SchoolSourceBean) {
        val gson = Gson()
        val toJson = gson.toJson(bean.detail_info)
        val item = gson.fromJson(toJson, AlbumBean::class.java)
        holder.setText(R.id.tv_title, item.album_title)
        holder.setText(R.id.tv_play_num, item.play_count.toString())
        holder.setText(R.id.tv_collection, item.include_track_count.toString() + "集")

        if (item.cover_url_middle.isNotBlank()) {
            val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
            Glide.with(context)
                .load(item.cover_url_middle)
                .transform(GlideTransform.centerCropAndRounder2)
                .placeholder(R.mipmap.common_bg_cover_default)
                .error(R.mipmap.common_bg_cover_default)
                .into(imageView!!)
        }
    }
//    /**
//     * 设置alum数据 音频课
//     */
//    private fun setAlumData(holder: BaseViewHolder, bean: SchoolSourceBean) {
//        var gson = Gson()
//        val toJson = gson.toJson(bean.detail_info)
//        val item = gson.fromJson(toJson, AlbumBean::class.java)
//        holder.setText(R.id.tv_title, item.album_title)
//        holder.setText(R.id.tvPlayCount, item.play_count.toString())
//        holder.setText(R.id.tvAlbumTrackNum,item.include_track_count.toString()+"集")
//
//        if (item.cover_url_middle.isNotBlank()) {
//            val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
//            Glide.with(context)
//                    .load(item.cover_url_small)
//                    .apply(RequestOptions.bitmapTransform(RoundedCorners(5)))
//                    .into(imageView!!)
//        }
//    }


    /**
     * 设置音频数据
     */
    private fun setTrackData(holder: BaseViewHolder, bean: SchoolSourceBean) {
        val gson = Gson()
        val toJson = gson.toJson(bean.detail_info)
        val item = gson.fromJson(toJson, TrackBean::class.java)
        holder.setText(R.id.tv_title, item.trackTitle)
        holder.setText(R.id.tv_source, item.albumTitle)
        holder.setText(R.id.tv_play_num, item.play_count.toString())
        holder.setText(R.id.tv_duration, TimeUtils.timeParse(item.duration))



        if (item.cover_url_middle.isNotBlank()) {
            val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
            Glide.with(context)
                .load(item.cover_url_middle)
                .transform(GlideTransform.centerCropAndRounder2)
                .placeholder(R.mipmap.common_bg_cover_default)
                .error(R.mipmap.common_bg_cover_default)
                .into(imageView!!)
        }
    }

    /**
     * 设置问卷的数据
     */
    private fun setQuestionData(holder: BaseViewHolder, item: SchoolSourceBean) {
        val gson = Gson()
        val toJson = gson.toJson(item.detail_info)
        val bean = gson.fromJson(toJson, QuestionnaireDetailBean::class.java)

        if (bean.des.isNullOrEmpty()) {
            holder.setGone(R.id.tvContent, true)
        } else {
            holder.setGone(R.id.tvContent, false)
            holder.setText(R.id.tvContent, HtmlFromatUtil.html2Text(bean.des))
        }
        holder.setText(R.id.tvTitle, bean.title)
        holder.setText(R.id.type, ResourceTypeConstans.Companion.typeStringMap[item.resource_type])
    }

    /**
     * 设置关于专题的数据
     */
    private fun setSpecialData(holder: BaseViewHolder, item: SchoolSourceBean) {


        val gson = Gson()
        val toJson = gson.toJson(item.detail_info)
        val bean = gson.fromJson(toJson, SpecialDetailBean::class.java)

        if (bean.about.isNullOrEmpty()) {
            holder.setGone(R.id.tvContent, true)
        } else {
            holder.setGone(R.id.tvContent, false)
            holder.setText(R.id.tvContent, bean.about)
        }
        holder.setText(R.id.tvTitle, bean.title)
        //设置type
        var typeStr = "合集"
        if (!TextUtils.isEmpty(bean.identity_name)) {
            typeStr = bean.identity_name.toString()
        }
        holder.setText(R.id.type, typeStr)

    }

    /**
     * 设置关于课程的数据
     */
    private fun setCourseData(holder: BaseViewHolder, item: SchoolSourceBean) {


        val gson = Gson()
        val toJson = gson.toJson(item.detail_info)
        val bean = gson.fromJson(toJson, CourseBean::class.java)


        holder.setText(R.id.tv_title, bean.title)
        holder.setText(R.id.tv_source, bean.platform_zh)
        holder.setText(R.id.tv_author, bean.org)
        holder.setText(R.id.tv_opening, bean.course_start_time)

        val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)

        Glide.with(context)
            .load(bean.picture)
            .transform(GlideTransform.centerCropAndRounder2)
            .placeholder(R.mipmap.common_bg_cover_default)
            .error(R.mipmap.common_bg_cover_default)
            .into(imageView!!)
        setResourceType(holder, bean)
        //设置type
        holder.setText(R.id.tv_course_Lable, R.string.course)
        holder.setText(R.id.tvTypeSchoolCourse, R.string.course)
        if (!TextUtils.isEmpty(bean.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
            holder.setText(R.id.tvTypeSchoolCourse, bean.identity_name)
            holder.setGone(R.id.tvTypeSchoolCourse, false)
            holder.setGone(R.id.tv_course_Lable, true)
        } else {
            holder.setGone(R.id.tvTypeSchoolCourse, true)
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
        followBinding: MyIncludeItemStudyFirendCircleDynamicFollowupBinding,
        dataBean: StudyDynamic
    ) {
        val layout = LinearLayoutManager(context)
        followBinding.recyclerView.setLayoutManager(layout)
        val repeat_record = dataBean.extra_info?.repeat_record
        if (repeat_record != null) {
            followBinding.recyclerView.setVisibility(View.VISIBLE)
            val followUpVoiceDynamicAdapter = FollowUpVoiceDynamicAdapter(repeat_record)

//            boolean expandTvVisibale = itemViewHolder.tvFollowUpAudioExpand.getVisibility() == View.GONE;
            if (repeat_record.size > 4) {   //如果超过6条，显示4条，并且显示展开view
                followBinding.tvFollowUpAudioExpand.setText("展开")
                followBinding.tvFollowUpAudioExpand.setVisibility(View.VISIBLE)
                followBinding.tvFollowUpAudioExpand.setCompoundDrawables(
                    null,
                    null,
                    getTvFollowupDrawableRight(followBinding),
                    null
                )
                //                followUpVoiceDynamicAdapter.setExpand(false);
                val subList: java.util.ArrayList<StudyDynamic.RepeatRecordBean> =
                    java.util.ArrayList<StudyDynamic.RepeatRecordBean>()
                for (i in 0..3) {   //截取前4个音频
                    subList.add(repeat_record[i])
                }
                followUpVoiceDynamicAdapter.setNewData(subList)
                followBinding.tvFollowUpAudioExpand.setOnClickListener(View.OnClickListener {
                    val equals = "展开" == followBinding.tvFollowUpAudioExpand.getText().toString()
                    val expandStr = if (equals) "收起" else "展开"
                    //                        int drawRight = equals? R.mipmap.ic_arrow_up_blue : R.mipmap.ic_arrow_down_blue;
                    //                        followUpVoiceDynamicAdapter.setExpand(equals);
                    if (equals) {
                        followUpVoiceDynamicAdapter.setNewData(repeat_record)
                    } else {
                        followUpVoiceDynamicAdapter.setNewData(subList)
                    }
                    followBinding.tvFollowUpAudioExpand.setText(expandStr)
                    val tvFollowupDrawableRight = getTvFollowupDrawableRight(followBinding)
                    followBinding.tvFollowUpAudioExpand.setCompoundDrawables(
                        null,
                        null,
                        tvFollowupDrawableRight,
                        null
                    )
                })
            } else {
                followUpVoiceDynamicAdapter.setNewData(repeat_record)
                followBinding.tvFollowUpAudioExpand.setVisibility(View.GONE)
            }
            followBinding.recyclerView.setAdapter(followUpVoiceDynamicAdapter)
            followBinding.recyclerView.setVisibility(View.VISIBLE)
        }
    }

    private fun getTvFollowupDrawableRight(followBinding: MyIncludeItemStudyFirendCircleDynamicFollowupBinding): Drawable? {
        val equals = "展开" == followBinding.tvFollowUpAudioExpand.getText().toString()
        val drawRight: Int =
            if (equals) R.mipmap.common_ic_arrow_down_blue else R.mipmap.common_ic_arrow_up_blue
        val drawable: Drawable = context.getResources().getDrawable(drawRight)
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        return drawable
    }

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