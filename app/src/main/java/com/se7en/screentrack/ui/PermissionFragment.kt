package com.se7en.screentrack.ui

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.se7en.screentrack.R
import kotlinx.android.synthetic.main.fragment_permission.*

class PermissionFragment: Fragment(R.layout.fragment_permission) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showDialogOrProceed()

        settingsButton.setOnClickListener {
            openSettings()
        }

        proceedButton.setOnClickListener {
            showDialogOrProceed()
        }
    }

    private fun showDialogOrProceed() {
        if(!hasUsageAccessPermission())
            showUsageAccessPermissionDialog()
        else {
            findNavController().navigate(R.id.action_permissionFragment_to_homeFragment)
        }
    }

    private fun showUsageAccessPermissionDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission")
            .setMessage("In order to use the app, " +
                    "please grant the App Usage Access permission in settings")
            .setNegativeButton("No") { _, _ ->
                activity?.finish()
            }
            .setPositiveButton("Okay") { _, _ ->
                openSettings()
            }
            .create().show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        if(intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun hasUsageAccessPermission(): Boolean {
        val appOpsManager = context?.getSystemService(
            Context.APP_OPS_SERVICE
        ) as AppOpsManager? ?: return false

        val mode = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                requireContext().applicationInfo.uid,
                requireContext().packageName
            )
            else -> appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                requireContext().applicationInfo.uid,
                requireContext().applicationInfo.packageName
            )
        }

        return mode == AppOpsManager.MODE_ALLOWED
    }

}