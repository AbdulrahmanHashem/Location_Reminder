package com.example.location_reminder.base

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.location_reminder.utils.SingleLiveEvent

/**
 * Base class for View Models to declare the common LiveData objects in one place
 */
abstract class BaseViewModel(app: Application) : AndroidViewModel(app) {

    val navigationCommand: SingleLiveEvent<NavigationCommand> = SingleLiveEvent()
    val showErrorMessage: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()
    val showToast: SingleLiveEvent<String> = SingleLiveEvent()
    val showLoading: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val showNoData: MutableLiveData<Boolean> = MutableLiveData()

//    val navigationCommand: MutableLiveData<NavigationCommand> = MutableLiveData()
//    val showErrorMessage: MutableLiveData<String> = MutableLiveData()
//    val showSnackBar: MutableLiveData<String> = MutableLiveData()
//    val showSnackBarInt: MutableLiveData<Int> = MutableLiveData()
//    val showToast: MutableLiveData<String> = MutableLiveData()
//    val showLoading: MutableLiveData<Boolean> = MutableLiveData()
//    val showNoData: MutableLiveData<Boolean> = MutableLiveData()
}