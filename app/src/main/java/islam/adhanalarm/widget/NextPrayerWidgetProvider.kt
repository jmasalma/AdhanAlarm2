package islam.adhanalarm.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.widget.RemoteViews
import islam.adhanalarm.R
import islam.adhanalarm.handler.ScheduleHandler
import islam.adhanalarm.repo.PrayerTimesRepository
import java.util.Calendar

class NextPrayerWidgetProvider : BaseWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.next_prayer_widget)
        val repository = PrayerTimesRepository(context)
        val schedule = repository.getTodaysSchedule()

        if (schedule != null) {
            val nextPrayer = schedule.nextTimeIndex
            val prayerName = getPrayerName(context, nextPrayer)
            val prayerTime = ScheduleHandler.getFormattedTime(schedule.schedule, schedule.extremes, nextPrayer, "0")

            views.setTextViewText(R.id.prayer_name, prayerName)
            views.setTextViewText(R.id.prayer_time, prayerTime)

            scheduleNextUpdate(context, appWidgetId, schedule.schedule[nextPrayer.toInt()].timeInMillis, NextPrayerWidgetProvider::class.java)
        } else {
            views.setTextViewText(R.id.prayer_name, "Error")
            views.setTextViewText(R.id.prayer_time, " ")
        }

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getPrayerName(context: Context, prayerIndex: Short): String {
        val prayerNames = context.resources.getStringArray(R.array.prayer_names)
        return prayerNames[prayerIndex.toInt()]
    }
}
