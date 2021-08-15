package de.felixnuesse.timedsilence.handler.volume

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.text.DateFormat
import java.util.*
import de.felixnuesse.timedsilence.receiver.AlarmBroadcastReceiver
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.PrefConstants
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.Utils
import de.felixnuesse.timedsilence.handler.SharedPreferencesHandler
import de.felixnuesse.timedsilence.ui.PausedNotification
import java.text.SimpleDateFormat


/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 10.04.19 - 12:00
 *
 * Edited by: Felix Nüsse felix.nuesse(at)t-online.de
 *
 *
 * This program is released under the GPLv3 license
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 *
 *
 *
 */


class AlarmHandler {

    companion object {

        fun createRepeatingTimecheck(context: Context){
            createAlarmIntime(context)
        }

        fun createAlarmIntime(context: Context){
           System.err.println("start create")
           val now = System.currentTimeMillis()
           var calculatedChecktime = 0L
           var list = VolumeHandler().getChangeList(context)
           for (it in list) {
               //Log.e(Constants.APP_NAME, "Calculated time $it")
               //Log.e(Constants.APP_NAME, "Calculated time ${Utils.getDate(calculatedChecktime)}")
               if(it > now && calculatedChecktime == 0L){
                   calculatedChecktime = it
               }
           }
           Log.e(Constants.APP_NAME, "Calculated time $calculatedChecktime")
           Log.e(Constants.APP_NAME, "Calculated time ${Utils.getDate(calculatedChecktime)}")

           val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
           val pi: PendingIntent? = createRestartBroadcast(
               context
           )
           am.cancel(pi)
           am.setExact(
               AlarmManager.RTC_WAKEUP,
               calculatedChecktime,
               pi
           )
        }

       fun removeRepeatingTimecheck(context: Context){
           val alarms = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
           alarms.cancel(createRestartBroadcast(context))
           createRestartBroadcast(context)?.cancel()

           if(!checkIfNextAlarmExists(context)){
               Log.d(Constants.APP_NAME, "AlarmHandler: Recurring alarm canceled")
               return
           }
           Log.e(Constants.APP_NAME, "AlarmHandler: Error canceling recurring alarm!")

       }

       private fun createRestartBroadcast(context: Context): PendingIntent? {
           return createRestartBroadcast(context, 0)
       }

       private fun createRestartBroadcast(context: Context, flag: Int): PendingIntent? {

           val broadcastIntent = Intent(context, AlarmBroadcastReceiver::class.java)
           broadcastIntent.putExtra(
               Constants.BROADCAST_INTENT_ACTION,
               Constants.BROADCAST_INTENT_ACTION_DELAY
           )
           broadcastIntent.putExtra(
               Constants.BROADCAST_INTENT_ACTION_DELAY_EXTRA,
               Constants.BROADCAST_INTENT_ACTION_DELAY_RESTART_NOW
           )
           broadcastIntent.putExtra(
               Constants.BROADCAST_INTENT_ACTION,
               Constants.BROADCAST_INTENT_ACTION_UPDATE_VOLUME
           )

           // The Pending Intent to pass in AlarmManager
           return PendingIntent.getBroadcast(context,0, broadcastIntent,flag)

       }

       fun checkIfNextAlarmExists(context: Context): Boolean{
           val pIntent =
               createRestartBroadcast(
                   context,
                   PendingIntent.FLAG_NO_CREATE
               )

           if(pIntent == null){
               Log.d(Constants.APP_NAME, "AlarmHandler: There is no next Alarm set!")
               PausedNotification.show(context)
               return false
           }else {
               Log.d(Constants.APP_NAME, "AlarmHandler: There is an upcoming Alarm!")
               PausedNotification.cancelNotification(context)
               return true
           }
       }

        fun getNextAlarmTimestamp(context: Context): String{
            val alarms = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val clockInfo =  alarms.nextAlarmClock

            if(clockInfo==null){
                return context.getString(R.string.no_next_time_set)
            }

            Log.d(Constants.APP_NAME, "AlarmHandler: Next Runtime: "+clockInfo.triggerTime)
            return  DateFormat.getDateInstance().format(Date(clockInfo.triggerTime))

        }

   }

}
