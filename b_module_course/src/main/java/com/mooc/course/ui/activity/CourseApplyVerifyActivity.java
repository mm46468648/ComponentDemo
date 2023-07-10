package com.mooc.course.ui.activity;



import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.mooc.commonbusiness.api.HttpService;
import com.mooc.commonbusiness.base.BaseActivity;
import com.mooc.commonbusiness.constants.IntentParamsConstants;
import com.mooc.commonbusiness.manager.BaseObserver;
import com.mooc.commonbusiness.model.xuetang.ApplyVerifyBean;
import com.mooc.course.R;
import com.mooc.course.databinding.CourseActivityApplyVerifyBinding;
import com.mooc.common.ktextends.AnyExtentionKt;
import com.mooc.common.utils.rxjava.RxUtils;
import com.mooc.commonbusiness.route.Paths;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;


@Route(path = Paths.PAGE_COURSE_APPLY_VERIFY)
public class CourseApplyVerifyActivity extends BaseActivity {

    CourseActivityApplyVerifyBinding binding;

    String courseId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.course_activity_apply_verify);
        initView();
        initData();
        initListener();
    }

    public void initView() {

    }

    public void initData() {
        courseId = getIntent().getStringExtra(IntentParamsConstants.COURSE_XT_PARAMS_ID);
    }

    public void initListener() {
        //点击提交
        binding.commonTitle.setOnRightTextClickListener(view -> {
            postData();

        });

        //点击返回
        binding.commonTitle.setOnLeftClickListener(view -> {
            finish();
        });

        InputFilter enTypeFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern p = Pattern.compile("[a-zA-Z0-9\\s]+");
                Matcher m = p.matcher(source.toString());
                if (!m.matches()) return "";
                return null;
            }
        };

        InputFilter zhTypeFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern p = Pattern.compile("[\u4e00-\u9fa5\\s]+");
                Matcher m = p.matcher(source.toString());
                if (!m.matches()) return "";
                return null;
            }
        };

        binding.zhName.setFilters(new InputFilter[]{zhTypeFilter, new InputFilter.LengthFilter(25)});

        binding.engName.setFilters(new InputFilter[]{enTypeFilter, new InputFilter.LengthFilter(50)});

        binding.zhName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable.toString().trim())) {
                    binding.delZhName.setVisibility(View.GONE);
                } else {
                    binding.delZhName.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.engName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable.toString().trim())) {
                    binding.delEnName.setVisibility(View.GONE);
                } else {
                    binding.delEnName.setVisibility(View.VISIBLE);
                }
            }
        });


        binding.delZhName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.zhName.setText("");
            }
        });

        binding.delEnName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.engName.setText("");
            }
        });

    }


    private void postData() {
        String enName = binding.zhName.getText().toString().trim();
        String engName = binding.engName.getText().toString().trim();

        if (TextUtils.isEmpty(enName)) {
            binding.errorTipZh.setText("中文名不能为空");
            binding.errorTipZh.setVisibility(View.VISIBLE);
            return;
        } else {
            binding.errorTipZh.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(engName)) {
            binding.errorTipEng.setText("英文名不能为空");
            binding.errorTipEng.setVisibility(View.VISIBLE);
            return;
        } else {
            binding.errorTipEng.setVisibility(View.GONE);
        }

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("course_id", generateRequestBody(courseId));
        map.put("username", generateRequestBody(enName));
        map.put("englishname", generateRequestBody(engName));



        HttpService.Companion.getXtApi()
                .applyXtCourseVerify(map).compose(RxUtils.<ApplyVerifyBean>applySchedulers())
                .subscribe(new BaseObserver<ApplyVerifyBean>(this, true) {
            @Override
            public void onSuccess(ApplyVerifyBean applyVerifyBean) {
                AnyExtentionKt.toast(this,"申请成功");
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }


    RequestBody generateRequestBody(String str){
        return RequestBody.Companion.create(str,MediaType.parse("multipart/form-data"));
    }


}
