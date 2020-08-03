package com.hromovych.android.bookstats.ui.wantRead;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WantReadViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public WantReadViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}