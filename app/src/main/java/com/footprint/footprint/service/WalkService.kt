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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.footprint.footprint.classes.NonNullMutableLiveData
import com.google.android.gms.location.*
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.*

typealias Path = MutableList<LatLng>
typealias PathGroup = MutableList<Path>

class WalkService : LifecycleService() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var lastLocation: Location
    private var totalTimeMillis = 0L
    private var stopCount = 0

    companion object {
        val isWalking = NonNullMutableLiveData(false)
        val currentTime = NonNullMutableLiveData(0)
        val currentLocation = MutableLiveData<Location>(null)
        val paths = NonNullMutableLiveData<PathGroup>(mutableListOf())
        val totalDistance = NonNullMutableLiveData(0.0f)

        const val NOTIFICATION_ID = 10
        const val NOTIFICATION_CHANNEL_ID = "primary_notification_channel"
        const val NOTIFICATION_NAME = "Footprint Notification"

        const val TRACKING_START_OR_RESUME = "start_or_resume"
        const val TRACKING_PAUSE = "pause"
        const val TRACKING_STOP = "stop"

        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate() {
        super.onCreate()

        Log.d("Walk/WalkService", "onCreate")

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
            Log.d("Walk/WalkService", "onCreate/isWalking - ${state.toString()}")
            if (state) {
                addEmptyPath()
                locationActivate()
                startTimer()
            } else {
                locationDeactivate()
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            when (intent.action) {
                TRACKING_START_OR_RESUME -> {
                    isWalking.postValue(true)
                }
                TRACKING_PAUSE -> {
                    isWalking.postValue(false)
                }
                TRACKING_STOP -> {
                    isWalking.postValue(false)
                    Log.d("Walk/WalkService", "서비스 종료")
                    stopSelf()
                    isWalking.postValue(false)
                    currentTime.postValue(0)
                    currentLocation.postValue(null)
                    paths.postValue(mutableListOf())
                    totalDistance.postValue(0.0f)
                }
                else -> null
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (!isWalking.value) {
                return
            }

            result.lastLocation.let {
                if (it.speed <= 0.2f) {
                    stopCount++

                    if (stopCount == 20) {
                        isWalking.postValue(false)
                    }

                    return
                }

                if (stopCount != 0) {
                    stopCount = 0
                }

                currentLocation.postValue(it)
                addLocation(it)
                updateDistance(it)
            }
        }
    }

    private fun locationActivate() {
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

    private fun locationDeactivate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun startTimer() {
        val startTime= System.currentTimeMillis()
        var lapTime = 0L
        var lastSecondTimeMillis = currentTime.value * 1000L

        CoroutineScope(Dispatchers.Main).launch {
            while (isWalking.value) {
                lapTime = System.currentTimeMillis() - startTime

                if (totalTimeMillis + lapTime >= lastSecondTimeMillis + 1000L) {
                    currentTime.postValue(currentTime.value + 1)
                    lastSecondTimeMillis += 1000L
                }

                delay(500L)
            }

            totalTimeMillis += lapTime
        }
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
}