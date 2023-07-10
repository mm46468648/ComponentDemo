package com.mooc.column.ui.columnall.delegate

import com.mooc.discover.R
import com.mooc.discover.adapter.delegate.BaseColumnTagDelegeta
import com.mooc.discover.model.RecommendColumn
import com.mooc.commonbusiness.constants.ResourceTypeConstans

class ColumnTagThreeDelegate : BaseColumnTagDelegeta<RecommendColumn>() {

    init {

        addItemType(normalResType, R.layout.item_home_tag_three_micro_course)


    }

}