package com.mooc.battle.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GameResuleViewModel extends ViewModel {

    //继续挑战是否可以点击
    public MutableLiveData<Boolean> continueAble = new MutableLiveData<>();
}
