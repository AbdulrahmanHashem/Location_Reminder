package com.example.location_reminder

import android.app.Application
import com.example.location_reminder.location_reminders.data.ReminderDataSource
import com.example.location_reminder.location_reminders.data.local.LocalDB
import com.example.location_reminder.location_reminders.data.local.RemindersLocalRepository
import com.example.location_reminder.location_reminders.reminders_list.RemindersListViewModel
import com.example.location_reminder.location_reminders.save_reminder.SaveReminderViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApp)
            modules(listOf(myModule))
        }
    }

    val myModule = module {
        viewModel {
            RemindersListViewModel(get(), get() as ReminderDataSource)
        }

            single {
                //This view model is declared singleton to be used across multiple fragments
                SaveReminderViewModel(
                    get(),
                    get() as ReminderDataSource
                )
            }

        single { LocalDB.createRemindersDao(this@MyApp) }
        single<ReminderDataSource> { RemindersLocalRepository(get()) }
    }
}