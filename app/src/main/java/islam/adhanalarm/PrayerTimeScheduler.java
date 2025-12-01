package islam.adhanalarm;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import net.sourceforge.jitl.astro.Location;

import islam.adhanalarm.handler.ScheduleData;
import islam.adhanalarm.handler.ScheduleHandler;

public class PrayerTimeScheduler {

    public static ScheduleData scheduleAlarms(Context context) {
        SharedPreferences settings;
        try {
            MasterKey masterKey = new MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            settings = EncryptedSharedPreferences.create(
                    context,
                    "secret_shared_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        String latitude = settings.getString("latitude", null);
        String longitude = settings.getString("longitude", null);

        if (latitude != null && longitude != null) {
            String altitude = settings.getString("altitude", "0");
            String pressure = settings.getString("pressure", "1010");
            String temperature = settings.getString("temperature", "10");
            Location locationAstro = ScheduleHandler.getLocation(latitude, longitude, altitude, pressure, temperature);
            String calculationMethodIndex = settings.getString("calculationMethodsIndex", String.valueOf(CONSTANT.DEFAULT_CALCULATION_METHOD));
            String roundingTypeIndex = settings.getString("roundingTypesIndex", String.valueOf(CONSTANT.DEFAULT_ROUNDING_TYPE));
            int offsetMinutes = 0;
            try {
                offsetMinutes = Integer.parseInt(settings.getString("offsetMinutes", "0"));
            } catch (NumberFormatException e) {
                // Ignore and use 0
            }
            ScheduleData newScheduleData = ScheduleHandler.calculate(locationAstro, calculationMethodIndex, roundingTypeIndex, offsetMinutes);
            ScheduleHandler.scheduleAlarms(context, newScheduleData);
            return newScheduleData;
        }
        return null;
    }
}
