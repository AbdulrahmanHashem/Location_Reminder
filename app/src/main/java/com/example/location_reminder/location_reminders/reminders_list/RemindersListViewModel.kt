package com.example.location_reminder.location_reminders.reminders_list

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.location_reminder.base.BaseViewModel
import com.example.location_reminder.location_reminders.data.ReminderDataSource
import com.example.location_reminder.location_reminders.data.dto.ReminderDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RemindersListViewModel(
    app: Application,
    private val dataSource: ReminderDataSource
) : BaseViewModel(app) {
    // list that holds the reminder data to be displayed on the UI
    val remindersList = MutableLiveData<List<ReminderDataItem>>()

    /**
     * Get all the reminders from the DataSource and add them to the remindersList to be shown on the UI,
     * or show error if any
     */
    fun loadReminders() {
        viewModelScope.launch {
            showLoading.value = true
            showNoData.value = false
            val result = dataSource.getReminders()
            when (result.getOrThrow()) {
                is Throwable -> showSnackBar.value = result.exceptionOrNull()?.message.toString()
                else -> {
                    val dataList = ArrayList<ReminderDataItem>()
                    if ((result.getOrNull() as List<ReminderDTO>).isEmpty()){
                        showSnackBar.value = "No Saved Reminders"
                        showLoading.value = false
                        showNoData.value = true
                    } else {
                        dataList.addAll((result.getOrNull() as List<ReminderDTO>).map { reminder ->
                            ReminderDataItem(
                                reminder.title,
                                reminder.description,
                                reminder.location,
                                reminder.latitude,
                                reminder.longitude,
                                reminder.id
                            )
                        })
                    }
                }
            }
            showLoading.value = false
            invalidateShowNoData()
        }
    }

    /**
     * Inform the user that there's not any data if the remindersList is empty
     */
    private fun invalidateShowNoData() {
        showNoData.value = remindersList.value == null || remindersList.value!!.isEmpty()
    }
}