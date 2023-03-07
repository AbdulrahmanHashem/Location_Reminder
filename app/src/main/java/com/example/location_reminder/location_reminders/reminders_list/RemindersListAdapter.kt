package com.example.location_reminder.location_reminders.reminders_list

import com.example.location_reminder.R
import com.example.location_reminder.base.BaseRecyclerViewAdapter

class RemindersListAdapter(callBack: (selectedReminder: ReminderDataItem) -> Unit) :
    BaseRecyclerViewAdapter<ReminderDataItem>(callBack) {
    override fun getLayoutRes(viewType: Int) = R.layout.it_reminder
}