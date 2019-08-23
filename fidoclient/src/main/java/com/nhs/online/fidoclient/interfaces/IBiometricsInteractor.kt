package com.nhs.online.fidoclient.interfaces

import android.support.v4.app.FragmentActivity

interface IBiometricsInteractor {
    fun showProgressDialog()

    fun dismissProgressDialog()

    fun toggleBiometricSwitch(isChecked: Boolean)

    fun showBiometricsOnRegistrationSuccessMessage()

    fun showBiometricsOnDeRegistrationSuccessMessage()

    fun showBiometricRegistrationError()

    fun showBiometricDeviceError()

    fun getActivity(): FragmentActivity

    fun loadBiometricLoginPage(url: String)
}