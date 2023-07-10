package com.mooc.common.utils.permission;

import android.content.Context;
import android.content.Intent;

public interface ISetting {
    Intent getStartSettingsIntent(Context context);
}
