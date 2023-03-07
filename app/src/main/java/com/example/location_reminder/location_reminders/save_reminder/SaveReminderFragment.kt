package com.example.location_reminder.location_reminders.save_reminder

import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.example.location_reminder.BuildConfig
import com.example.location_reminder.R
import com.example.location_reminder.base.*
import com.example.location_reminder.databinding.FragmentSaveReminderBinding
import com.example.location_reminder.utils.setDisplayHomeAsUpEnabled
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject

class SaveReminderFragment() : BaseFragment() {
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSaveReminderBinding.inflate(layoutInflater, container, false)

        binding.viewModel = _viewModel

        locationPermsRequest =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()) { perms ->
                if (perms[Manifest.permission.ACCESS_FINE_LOCATION] == false) {
                    Snackbar.make(
                        binding.root,
                        R.string.permission_denied_explanation, Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.settings) {
                            startActivity(Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts(
                                    "package",
                                    BuildConfig.APPLICATION_ID,
                                    null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        }.show()
                } else {
                    checkPerms()
                }
            }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this

        binding.selectLocation.setOnClickListener {
            checkPerms()
        }

        binding.saveReminder.setOnClickListener {
            val title = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude
            val longitude = _viewModel.longitude.value

        // TODO: use the user entered reminder details to:
        //  1) add a geofencing request
        //  2) save the reminder to the local db

        }
    }

    private fun checkPerms() {
        if (foregroundLocationApproved) {
            checkDeviceLocationSettings {
                navigateToSelectLocationFragment()
            }
        } else {
            requestForegroundLocationPermissions()
        }
    }

    private fun navigateToSelectLocationFragment(){
        _viewModel.navigationCommand.value = NavigationCommand.To(
            SaveReminderFragmentDirections
                .actionSaveReminderFragmentToSelectLocationFragment())
    }

    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }
}