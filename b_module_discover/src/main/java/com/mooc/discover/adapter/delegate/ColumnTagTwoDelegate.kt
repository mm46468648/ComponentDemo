package com.mooc.column.ui.columnall.delegate

import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.mooc.discover.R
import com.mooc.discover.model.RecommendColumn
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.discover.adapter.delegate.BaseColumnTagDelegeta

class ColumnTagTwoDelegate : BaseColumnTagDelegeta<RecommendColumn>() {



    init {

        addItemType(normalResType, R.layout.item_home_tag_two_column)

    }

}