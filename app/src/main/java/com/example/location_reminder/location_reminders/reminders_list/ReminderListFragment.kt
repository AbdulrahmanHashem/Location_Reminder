package com.example.location_reminder.location_reminders.reminders_list

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.example.location_reminder.BuildConfig
import com.example.location_reminder.R
import com.example.location_reminder.base.BaseFragment
import com.example.location_reminder.base.NavigationCommand
import com.example.location_reminder.databinding.FragmentReminderListBinding
import com.example.location_reminder.utils.setup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReminderListFragment : BaseFragment() {
    //use Koin to retrieve the ViewModel instance
    override val _viewModel: RemindersListViewModel by viewModel()
    private lateinit var binding: FragmentReminderListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReminderListBinding.inflate(layoutInflater, container, false)
        binding.viewModel = _viewModel

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    // TODO: add the logout implementation
                    R.id.logout -> {
                        Firebase.auth.signOut()
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.refreshLayout.setOnRefreshListener {
            _viewModel.loadReminders()
        }

        locationPermsRequest =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()) {
                    perms ->
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                    && perms[Manifest.permission.ACCESS_FINE_LOCATION] == false) {
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
                }
                navigateToSaveReminderFragment()
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this
        setupRecyclerView()

        binding.addReminderFAB.setOnClickListener {
            checkPerms()
        }
    }

    private fun checkPerms() {
        if (foregroundLocationApproved) {
            checkDeviceLocationSettings {
                navigateToSaveReminderFragment()
            }
        } else {
            requestForegroundLocationPermissions()
        }
    }

    private fun navigateToSaveReminderFragment() {
        _viewModel.navigationCommand.value = NavigationCommand
            .To(
                ReminderListFragmentDirections
                .actionReminderListFragmentToSaveReminderFragment())
    }

    override fun onResume() {
        super.onResume()
        // load the reminders list on the ui
        _viewModel.loadReminders()
    }

    private fun setupRecyclerView() {
        val adapter = RemindersListAdapter {

        }
        // setup the recycler view using the extension function
        binding.reminderssRecyclerView.setup(adapter)
    }
}