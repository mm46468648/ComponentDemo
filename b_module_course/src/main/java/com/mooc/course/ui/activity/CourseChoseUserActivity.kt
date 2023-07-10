package com.mooc.course.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.course.R
import com.mooc.course.databinding.ActivityCourseChoseUserBinding
import com.mooc.course.ui.fragment.CourseChoseUserFragment
//import kotlinx.android.synthetic.main.activity_course_chose_user.*

/**
 *选课用户列表
 * @author limeng
 * @date 2022/5/5
 */
@Route(path = Paths.PAGE_COURSE_SUB_USER)
class CourseChoseUserActivity : BaseActivity() {
    var course_id: String? = null
    var sub_users: String? = null

    companion object {
        const val PARAMS_SUB_USERS = "params_sub_users"
    }
    private lateinit var inflater : ActivityCourseChoseUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = ActivityCourseChoseUserBinding.inflate(layoutInflater)
        setContentView(inflater.root)
        inflater.commonTitle.middle_text=getString(R.string.course_chose_user)
        inflater.commonTitle.setOnLeftClickListener { finish() }
        course_id = intent.getStringExtra(IntentParamsConstants.COURSE_PARAMS_ID)
        sub_users = intent.getStringExtra(PARAMS_SUB_USERS)

        val fragment = CourseChoseUserFragment()
        fragment.sub_users = sub_users
        fragment.course_id = course_id

        supportFragmentManager.beginTransaction().add(R.id.fragment, fragment).commit()
    }
}