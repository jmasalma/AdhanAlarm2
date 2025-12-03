package islam.adhanalarm.handler;

import net.sourceforge.jitl.astro.Location;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ScheduleHandlerTest {

    @Test
    public void testCalculate() {
        // Given
        Location location = new Location(34.0522, -118.2437, -8, 0); // Los Angeles
        String calculationMethodIndex = "0"; // ISNA
        String roundingTypeIndex = "0"; // No rounding
        int offsetMinutes = 0;

        // When
        ScheduleData scheduleData = ScheduleHandler.calculate(location, calculationMethodIndex, roundingTypeIndex, offsetMinutes);

        // Then
        assertNotNull(scheduleData);
        GregorianCalendar[] schedule = scheduleData.schedule;
        assertNotNull(schedule);
        assertEquals(7, schedule.length);

        // Verify that the prayer times are in the correct order
        for (int i = 0; i < schedule.length - 1; i++) {
            assertTrue(schedule[i].before(schedule[i+1]));
        }

        // Verify that the next prayer index is calculated correctly
        short nextPrayerIndex = scheduleData.nextTimeIndex;
        assertTrue(nextPrayerIndex >= 0 && nextPrayerIndex < schedule.length);
    }
}
