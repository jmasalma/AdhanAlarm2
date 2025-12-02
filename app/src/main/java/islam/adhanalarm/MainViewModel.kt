package islam.adhanalarm

import android.app.Application
import android.content.Context
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import islam.adhanalarm.handler.CompassHandler
import islam.adhanalarm.handler.LocationHandler
import islam.adhanalarm.handler.ScheduleData
import islam.adhanalarm.handler.ScheduleHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.sourceforge.jitl.Jitl
import net.sourceforge.jitl.astro.Direction
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }

    private val compassHandler: CompassHandler
    private val locationHandler: LocationHandler
    private val masterKey = MasterKey.Builder(application, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    private val settings = EncryptedSharedPreferences.create(
        application,
        "secret_shared_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _scheduleData = MediatorLiveData<ScheduleData>()
    val scheduleData: LiveData<ScheduleData> = _scheduleData

    private val _qiblaDirection = MediatorLiveData<Double>()
    val qiblaDirection: LiveData<Double> = _qiblaDirection

    val northDirection: LiveData<Float>
    private val _location = MediatorLiveData<Location>()
    val location: LiveData<Location> = _location

    init {
        compassHandler = CompassHandler(application.getSystemService(Context.SENSOR_SERVICE) as SensorManager)
        locationHandler = LocationHandler(application.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
        northDirection = compassHandler.northDirection
        _location.addSource(locationHandler.location) {
            saveLocation(it)
            _location.postValue(it)
        }

        _scheduleData.addSource(_location) { it?.let { loc -> updateData(loc) } }
        _qiblaDirection.addSource(_location) { it?.let { loc -> updateData(loc) } }

        loadLocationFromSettings()
    }

    private fun saveLocation(location: Location) {
        settings.edit()
            .putString(KEY_LATITUDE, location.latitude.toString())
            .putString(KEY_LONGITUDE, location.longitude.toString())
            .apply()
    }

    fun startCompass() {
        compassHandler.startTracking()
    }

    fun stopCompass() {
        compassHandler.stopTracking()
    }

    fun updateLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            locationHandler.update()
        }
    }

    fun loadLocationFromSettings() {
        val latitude = settings.getString(KEY_LATITUDE, null)
        val longitude = settings.getString(KEY_LONGITUDE, null)
        if (latitude != null && longitude != null) {
            val location = Location("settings")
            location.latitude = latitude.toDouble()
            location.longitude = longitude.toDouble()
            _location.postValue(location)
        } else {
            val location = Location("default")
            location.latitude = 43.467
            location.longitude = -80.517
            saveLocation(location)
            _location.postValue(location)
        }
    }

    fun updateData(loc: Location) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val latitude = loc.latitude.toString()
                val longitude = loc.longitude.toString()
                val altitude = settings.getString("altitude", "0")
                    val pressure = settings.getString("pressure", "1010")
                    val temperature = settings.getString("temperature", "10")

                    val locationAstro = ScheduleHandler.getLocation(latitude, longitude, altitude, pressure, temperature)

                    // Calculate and post schedule
                    var calculationMethodIndex = settings.getString("calculationMethodsIndex", null)
                    if (calculationMethodIndex == null) {
                        val geocoder = android.location.Geocoder(getApplication(), java.util.Locale.getDefault())
                        val addresses = awaitGetFromLocation(geocoder, loc.latitude, loc.longitude)
                        val countryCode = addresses?.firstOrNull()?.countryCode
                        if (countryCode != null) {
                            val locale = java.util.Locale("", countryCode)
                            val countryCodeAlpha3 = locale.isO3Country.uppercase(java.util.Locale.ROOT)
                            for ((index, codes) in CONSTANT.CALCULATION_METHOD_COUNTRY_CODES.withIndex()) {
                                if (codes.contains(countryCodeAlpha3)) {
                                    calculationMethodIndex = index.toString()
                                    break
                                }
                            }
                        }
                        if (calculationMethodIndex == null) {
                            calculationMethodIndex = CONSTANT.DEFAULT_CALCULATION_METHOD
                        }
                        settings.edit().putString("calculationMethodsIndex", calculationMethodIndex).apply()
                    }
                    val newScheduleData = PrayerTimeScheduler.scheduleAlarms(getApplication())
                    if (newScheduleData != null) {
                        _scheduleData.postValue(newScheduleData)
                    }
                    // Calculate and post qibla direction
                    val qibla = Jitl.getNorthQibla(locationAstro)
                    _qiblaDirection.postValue(qibla.getDecimalValue(Direction.NORTH))
                }
            }
    }

    private suspend fun awaitGetFromLocation(geocoder: android.location.Geocoder, latitude: Double, longitude: Double): List<android.location.Address>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCoroutine { continuation ->
                geocoder.getFromLocation(latitude, longitude, 1, object : android.location.Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<android.location.Address>) {
                        continuation.resume(addresses)
                    }
                    override fun onError(errorMessage: String?) {
                        continuation.resume(null)
                    }
                })
            }
        } else {
            try {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(latitude, longitude, 1)
            } catch (e: java.io.IOException) {
                null
            }
        }
    }
}