package com.example.sleepscreen

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.RemoteViews
import android.R.style.Widget
import android.content.ComponentName

open class SleepScreenWidget : AppWidgetProvider() {
    private val myOnClick1 = "myOnClickTag1"
    private val myOnClick2 = "myOnClickTag2"
    lateinit var remoteViews: RemoteViews
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }


    private fun updateAppWidget(
        context: Context, appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {

        remoteViews = RemoteViews(context.packageName, R.layout.sleep_screen_widget)
        remoteViews.setOnClickPendingIntent(R.id.up, getPendingSelfIntent(context, myOnClick1,appWidgetId))
        remoteViews.setOnClickPendingIntent(R.id.down, getPendingSelfIntent(context, myOnClick2,appWidgetId))
        remoteViews.setTextViewText(R.id.textId, convertTime(Settings.System.getInt(context.contentResolver,
            Settings.System.SCREEN_OFF_TIMEOUT)))
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)


    }

    private fun getPendingSelfIntent(context: Context, action: String,appWidgetId: Int): PendingIntent {
        val intent = Intent(context, SleepScreenWidget::class.java)
        val idArray = intArrayOf(appWidgetId)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray)
        intent.action = action
        return PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (myOnClick1 == intent.action) {
            Settings.System.putInt(
                context.contentResolver,
                Settings.System.SCREEN_OFF_TIMEOUT,600_000 // 10 minutes
            )
            updateMy(context)
        } else if (myOnClick2 == intent.action) {
            Settings.System.putInt(
                context.contentResolver,
                Settings.System.SCREEN_OFF_TIMEOUT, 15_000 // 30 seconds
            )
            updateMy(context)
        }


    }

    private fun updateMy(context: Context){
       val remoteViews = RemoteViews(context.packageName, R.layout.sleep_screen_widget)
        remoteViews.setTextViewText(R.id.textId,convertTime(Settings.System.getInt(context.contentResolver,Settings.System.SCREEN_OFF_TIMEOUT)))
        AppWidgetManager.getInstance(context).updateAppWidget(
            ComponentName(context, SleepScreenWidget::class.java), remoteViews
        )
    }

    private fun convertTime(time: Int):String{
        return if(time/1000>60){
            "${time/1000/60} min"
        }else{
            "${time/1000} sec"
        }
    }




}


