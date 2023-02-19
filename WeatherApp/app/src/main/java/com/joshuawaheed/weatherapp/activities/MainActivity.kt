package com.joshuawaheed.weatherapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.joshuawaheed.weatherapp.models.WeatherResponse
import com.joshuawaheed.weatherapp.network.WeatherService
import com.joshuawaheed.weatherapp.utils.Constants
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import retrofit.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class MainActivity : AppCompatActivity() {

    // A fused location client variable which is further user to get the user's current location
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mProgressDialog: Dialog? = null
    private var mCalendar: Calendar? = null
    private lateinit var mSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.joshuawaheed.weatherapp.R.layout.activity_main)

        // Initialize the Fused location variable
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mSharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)

        setupUI()

        if (!isLocationEnabled()) {
            Toast.makeText(
                this,
                "Your location provider is turned off. Please turn it on.",
                Toast.LENGTH_SHORT
            ).show()

            // This will redirect you to settings from where you need to turn on the location provider.
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } else {
            Dexter.withActivity(this)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            requestLocationData(false)
                        }

                        if (report.isAnyPermissionPermanentlyDenied) {
                            Toast.makeText(
                                this@MainActivity,
                                "You have denied location permission. Please allow it is mandatory.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread()
                .check()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(com.joshuawaheed.weatherapp.R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            com.joshuawaheed.weatherapp.R.id.action_refresh -> {
                requestLocationData(true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE)
            as LocationManager

        return (
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        )
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") {
                dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData(isRefreshButton: Boolean) {
        var forceRefresh = isRefreshButton

        val mLocationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                1000
            )
            .setWaitForAccurateLocation(false)
            .build()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val oneMinuteInMillis = 1 * 60 * 1000
                    var oneMinuteHasPassed = false
                    val currentTime = Calendar.getInstance()

                    if (
                        mCalendar == null ||
                        !forceRefresh &&
                        currentTime[Calendar.HOUR_OF_DAY] >= mCalendar!![Calendar.HOUR_OF_DAY] &&
                        currentTime.timeInMillis - mCalendar!!.timeInMillis >= oneMinuteInMillis
                    ) {
                        oneMinuteHasPassed = true
                        mCalendar = Calendar.getInstance()
                    }

                    if (forceRefresh || oneMinuteHasPassed) {
                        val mLastLocation: Location = locationResult.lastLocation as Location
                        val latitude = mLastLocation.latitude
                        val longitude = mLastLocation.longitude
                        getLocationWeatherDetails(latitude, longitude)
                        forceRefresh = false
                        mCalendar = Calendar.getInstance()
                    }
                }
            },
            Looper.myLooper()
        )
    }

    private fun getLocationWeatherDetails(latitude: Double, longitude: Double){
        if (Constants.isNetworkAvailable(this@MainActivity)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: WeatherService = retrofit
                .create(WeatherService::class.java)

            val listCall: Call<WeatherResponse> = service.getWeather(
                latitude,
                longitude,
                Constants.METRIC_UNIT,
                Constants.APP_ID
            )

            showCustomProgressDialog()

            listCall.enqueue(object: Callback<WeatherResponse> {
                override fun onResponse(response: Response<WeatherResponse>?, retrofit: Retrofit?) {
                    if (response?.isSuccess == true) {
                        hideProgressDialog()
                        val weatherList: WeatherResponse = response.body()
                        val weatherResponseJsonString = Gson().toJson(weatherList)
                        val editor = mSharedPreferences.edit()
                        editor.putString(Constants.WEATHER_RESPONSE_DATA, weatherResponseJsonString)
                        editor.apply()
                        Log.i("Response Result,", weatherList.toString())
                        setupUI()
                    } else {
                        when(response?.code() ?: 0) {
                            400 -> {
                                Log.e("Error 400", "Bad Connection")
                            }
                            404 -> {
                                Log.e("Error 404", "Not Found")
                            }
                            else -> {
                                Log.e("Error", "Generic Error")
                            }
                        }
                    }
                }

                override fun onFailure(t: Throwable?) {
                    Log.e("Error", t?.message.toString())
                    hideProgressDialog()
                }
            })
        } else {
            Toast.makeText(
                this@MainActivity,
                "No internet connection available.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showCustomProgressDialog() {
        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog!!.setContentView(com.joshuawaheed.weatherapp.R.layout.dialog_custom_progress)

        //Start the dialog and display it on screen.
        mProgressDialog!!.show()
    }

    private fun hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
    }

    private fun setupUI() {
        val weatherResponseJsonString = mSharedPreferences.getString(Constants.WEATHER_RESPONSE_DATA, "")

        if (!weatherResponseJsonString.isNullOrEmpty()) {
            val weatherList = Gson().fromJson(weatherResponseJsonString, WeatherResponse::class.java)

            // For loop to get the required data. And all are populated in the UI.
            for (z in weatherList.weather.indices) {
                Log.i("NAMEEEEEEEE", weatherList.weather[z].main)

                val tvMain: TextView = findViewById(com.joshuawaheed.weatherapp.R.id.tv_main)
                tvMain.text = weatherList.weather[z].main

                val tvMainDescription: TextView = findViewById(com.joshuawaheed.weatherapp.R.id.tv_main_description)
                tvMainDescription.text = weatherList.weather[z].description

                val tvTemp: TextView = findViewById(com.joshuawaheed.weatherapp.R.id.tv_temp)

                val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    resources.configuration.locales.getFirstMatch(resources.assets.locales)
                } else {
                    resources.configuration.locale;
                }

                tvTemp.text = weatherList.main.temp.toString() + getUnit(locale.toString())

                val tvHumidity: TextView = findViewById(com.joshuawaheed.weatherapp.R.id.tv_humidity)
                tvHumidity.text = weatherList.main.humidity.toString() + " per cent"

                val tvMin: TextView = findViewById(com.joshuawaheed.weatherapp.R.id.tv_min)
                tvMin.text = weatherList.main.temp_min.toString() + " min"

                val tvMax: TextView = findViewById(com.joshuawaheed.weatherapp.R.id.tv_max)
                tvMax.text = weatherList.main.temp_max.toString() + " max"

                val tvSpeed: TextView = findViewById(com.joshuawaheed.weatherapp.R.id.tv_speed)
                tvSpeed.text = weatherList.wind.speed.toString()

                val tvName: TextView = findViewById(com.joshuawaheed.weatherapp.R.id.tv_name)
                tvName.text = weatherList.name

                val tvCountry: TextView = findViewById(com.joshuawaheed.weatherapp.R.id.tv_country)
                tvCountry.text = weatherList.sys.country

                val tvSunriseTime: TextView = findViewById(com.joshuawaheed.weatherapp.R.id.tv_sunrise_time)
                tvSunriseTime.text = unixTime(weatherList.sys.sunrise.toLong())

                val tvSunsetTime: TextView = findViewById(com.joshuawaheed.weatherapp.R.id.tv_sunset_time)
                tvSunsetTime.text = unixTime(weatherList.sys.sunset.toLong())

                val ivMain: ImageView = findViewById(com.joshuawaheed.weatherapp.R.id.iv_main)

                // Here we update the main icon
                when (weatherList.weather[z].icon) {
                    "01d" -> ivMain.setImageResource(com.joshuawaheed.weatherapp.R.drawable.sunny)
                    "02d" -> ivMain.setImageResource(com.joshuawaheed.weatherapp.R.drawable.cloud)
                    "03d" -> ivMain.setImageResource(com.joshuawaheed.weatherapp.R.drawable.cloud)
                    "04d" -> ivMain.setImageResource(com.joshuawaheed.weatherapp.R.drawable.cloud)
                    "04n" -> ivMain.setImageResource(com.joshuawaheed.weatherapp.R.drawable.cloud)
                    "10d" -> ivMain.setImageResource(com.joshuawaheed.weatherapp.R.drawable.rain)
                    "11d" -> ivMain.setImageResource(com.joshuawaheed.weatherapp.R.drawable.storm)
                    "13d" -> ivMain.setImageResource(com.joshuawaheed.weatherapp.R.drawable.snowflake)
                    "01n" -> ivMain.setImageResource(com.joshuawaheed.weatherapp.R.drawable.cloud)
                    "02n" -> ivMain.setImageResource(com.joshuawaheed.weatherapp.R.drawable.cloud)
                    "03n" -> ivMain.setImageResource(com.joshuawaheed.weatherapp.R.drawable.cloud)
                    "10n" -> ivMain.setImageResource(com.joshuawaheed.weatherapp.R.drawable.cloud)
                    "11n" -> ivMain.setImageResource(com.joshuawaheed.weatherapp.R.drawable.rain)
                    "13n" -> ivMain.setImageResource(com.joshuawaheed.weatherapp.R.drawable.snowflake)
                }
            }
        }
    }

    private fun getUnit(value: String): String? {
        Log.i("unitttttt", value)
        var value = "°C"
        if ("US" == value || "LR" == value || "MM" == value) {
            value = "°F"
        }
        return value
    }

    private fun unixTime(timex: Long): String? {
        val date = Date(timex * 1000L)
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("HH:mm:ss")
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }
}