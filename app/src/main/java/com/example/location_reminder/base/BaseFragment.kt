package com.example.location_reminder.base

import android.Manifest
import android.annotation.TargetApi
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ContentInfoCompat.Flags
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.location_reminder.R
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar

const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
const val LOCATION_PERMISSION_INDEX = 0
const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1

val runningQOrLater =
    android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

abstract class BaseFragment : Fragment() {
    abstract val _viewModel: BaseViewModel
    lateinit var locationPermsRequest : ActivityResultLauncher<Array<String>>
    lateinit var snackbar: Snackbar

    val backgroundPermissionApproved : Boolean
        get() = if (runningQOrLater) {
            PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            true
        }

    val foregroundLocationApproved : Boolean
        get() = (
                PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                )

    val foregroundAndBackgroundPermissionApproved : Boolean
        get() = foregroundLocationApproved && backgroundPermissionApproved

    override fun onStart() {
        super.onStart()
        _viewModel.showErrorMessage.observe(this.viewLifecycleOwner, Observer {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        })
        _viewModel.showToast.observe(this.viewLifecycleOwner, Observer {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        })
        _viewModel.showSnackBar.observe(this.viewLifecycleOwner, Observer {
            snackbar = Snackbar.make(this.requireView(), it.toString(), Snackbar.LENGTH_LONG)
            snackbar.show()
        })
        _viewModel.showSnackBarInt.observe(this.viewLifecycleOwner, Observer {
            snackbar = Snackbar.make(this.requireView(), getString(it), Snackbar.LENGTH_LONG)
            snackbar.show()
        })

        _viewModel.navigationCommand.observe(this.viewLifecycleOwner, Observer { command ->
            when (command) {
                is NavigationCommand.To -> findNavController().navigate(command.directions)
                is NavigationCommand.Back -> findNavController().popBackStack()
                is NavigationCommand.BackTo -> findNavController().popBackStack(
                    command.destinationId,
                    false
                )
            }
        })
    }

    fun checkDeviceLocationSettings(onSuccess: (() -> Unit)? = null) {
        val locationSettingsRequest =
            // Location Settings Client
            LocationServices.getSettingsClient(requireActivity())
                // Location Settings Check
                .checkLocationSettings(
                    // Location Request Object
                    LocationSettingsRequest.Builder()
                        .addLocationRequest(
                            LocationRequest
                                .Builder(Priority.PRIORITY_HIGH_ACCURACY, 1)
                                .build()
                        ).build()
                )

        locationSettingsRequest.addOnFailureListener {exp ->
            if (exp is ResolvableApiException) {
                try {
                    exp.startResolutionForResult(requireActivity(), REQUEST_TURN_DEVICE_LOCATION_ON)
                } catch (e: Exception) {
                    _viewModel.showSnackBar.value = e.message
                }
            } else {
                Snackbar.make(requireView(),
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettings(onSuccess)
                }.show()
            }
        }

        onSuccess?.let {
            locationSettingsRequest.addOnSuccessListener { onSuccess() }
        }
    }

    fun requestForegroundLocationPermissions(
        andBackground: Boolean = false,
        launch: Boolean = true) {

        var permission =
            arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (foregroundLocationApproved && runningQOrLater && andBackground){
            permission =
                arrayOf(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }

        if (launch) {
            locationPermsRequest.launch(permission)
        }
    }

}