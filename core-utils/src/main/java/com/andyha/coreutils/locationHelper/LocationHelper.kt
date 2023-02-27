package com.andyha.coreutils.locationHelper

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.andyha.coreutils.permission.RxPermission
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener


class LocationHelper(
    context: Context,
    private var onLocationDetected: ((Location) -> Unit)?
) {

    companion object {
        private val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }

    private val rxPermission = RxPermission.getInstance(context)
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun detectLocation() {
        rxPermission
            .withPermissions(LOCATION_PERMISSIONS)
            .request {
                fusedLocationClient
                    .getCurrentLocation(
                        LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
                        object : CancellationToken() {
                            override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                                CancellationTokenSource().token

                            override fun isCancellationRequested() = false
                        })
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            onLocationDetected?.invoke(location)
                        }
                    }
            }
    }
}