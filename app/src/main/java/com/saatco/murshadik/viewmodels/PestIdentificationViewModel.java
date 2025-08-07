package com.saatco.murshadik.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.saatco.murshadik.ui.PestIdentificationState;

public class PestIdentificationViewModel extends ViewModel {

    private final MutableLiveData<PestIdentificationState> _uiState = new MutableLiveData<>();
    public LiveData<PestIdentificationState> uiState = _uiState;

    public PestIdentificationViewModel() {
        // Set the initial state to Input
        _uiState.setValue(new PestIdentificationState.Input());
    }

}