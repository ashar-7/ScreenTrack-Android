package com.se7en.screentrack.ui

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.View
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.se7en.screentrack.R
import kotlinx.android.synthetic.main.fragment_auth.*

class AuthFragment : Fragment(R.layout.fragment_auth) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (getPassword() == null) {
            enterPasswordEditText.hint = "Create Password"
        } else {
            enterPasswordEditText.hint = "Enter Password"
        }

        enterButton.setOnClickListener {
            when {
                getPassword() == null -> {
                    setPassword(enterPasswordEditText.text?.toString()?.takeIf { it.isNotBlank() }
                        ?: return@setOnClickListener)

                    val action = AuthFragmentDirections.actionAuthFragmentToPermissionFragment()
                    findNavController().navigate(action)
                }
                enterPasswordEditText.text?.toString() == getPassword() -> {
                    val action = AuthFragmentDirections.actionAuthFragmentToPermissionFragment()
                    findNavController().navigate(action)
                }
                else -> {
                    enterPasswordEditText.error = "Incorrect password: ${enterPasswordEditText.text}"
                }
            }
        }
    }

    private fun getPassword(): String? =
        context?.getSharedPreferences("auth", MODE_PRIVATE)?.getString("password", null)

    private fun setPassword(password: String) {
        context?.getSharedPreferences("auth", MODE_PRIVATE)?.edit {
            putString("password", password)
        }
    }
}
