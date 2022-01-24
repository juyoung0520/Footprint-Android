package com.footprint.footprint.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.footprint.footprint.classes.NonNullMutableLiveData
import com.google.android.gms.location.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationSource

typealias Path = MutableList<LatLng>
typealias PathGroup = MutableList<Path>

class WalkService : LifecycleService() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var lastLocation: Location

    companion object {
        val isWalking = NonNullMutableLiveData<Boolean>(false)
        val currentTime = NonNullMutableLiveData<Int>(0)
        val currentLocation = MutableLiveData<Location>(null)
        val paths = NonNullMutableLiveData<PathGroup>(mutableListOf())
        val totalDistance = NonNullMutableLiveData<Float>(0.0f)

        const val NOTIFICATION_ID = 10
        const val NOTIFICATION_CHANNEL_ID = "primary_notification_channel"
        const val NOTIFICATION_NAME = "Footprint Notification"

        const val TRACKING_START_OR_RESUME = "start_or_resume"
        const val TRACKING_PAUSE = "pause"
        const val TRACKING_STOP = "stop"
    }

    override fun onCreate() {
        super.onCreate()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_NAME,
                NotificationManager.IMPORTANCE_LOW
            )

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).build()
            startForeground(NOTIFICATION_ID, notification)
        }

        isWalking.observe(this, Observer { state ->
            if (state) {
                addEmptyPath()
                activate()
            } else {
                deactivate()
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            when (intent.action) {
                "start_or_resume" -> {
                    isWalking.postValue(true)
                }
                "pause" -> {
                    isWalking.postValue(false)
                }
                "destroy" -> {
                    isWalking.postValue(false)
                    stopSelf()
                }
                else -> null
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)

            if (isWalking.value) {
                result.lastLocation.let {
                    currentLocation.postValue(it)
                    addLocation(it)
                    updateDistance(it)
                }
            }
        }
    }

    private fun activate() {
        // Permission Check 여기 안넣으면 에러
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val locationRequest = LocationRequest.create().apply {
            interval = 1000L // 위치 업데이트 주기
            fastestInterval = 1000L // 가장 빠른 위치 업데이트 주기
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // 배터리 소모를 고려하지 않으며 정확도를 최우선으로 고려
            // maxWaitTime = 5000 // 최대 대기 시간
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun deactivate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun addEmptyPath() {
        paths.value.apply {
            add(mutableListOf())
            paths.postValue(this)
        }
    }

    private fun addLocation(location: Location) {
        val pos = LatLng(location.latitude, location.longitude)
        paths.value.apply {
            last().add(pos)
            paths.postValue(this)
        }
    }

    private fun updateDistance(location: Location) {
        if (!::lastLocation.isInitialized) {
            lastLocation = location
            return
        }

        totalDistance.postValue(totalDistance.value + location.distanceTo(lastLocation))

        lastLocation = location
    }

    private fun hasLocationPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        } else {
            (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        }
    }
}