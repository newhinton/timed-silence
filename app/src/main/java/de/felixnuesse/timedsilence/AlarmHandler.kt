package de.felixnuesse.timedsilence

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import de.felixnuesse.timedsilence.Constants.Companion.RECURRING_INTENT_ID

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


        fun createAlarmIntime(context: Context, delayInMs: Long){

          /*  val alarms = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarms.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + delayInMs,
                createRestartBroadcast(context)
            )
            */
        }

        fun createRepeatingTimecheck(context: Context){

            val interval= SharedPreferencesHandler.getPref(context, Constants.PREF_INTERVAL_CHECK, Constants.PREF_INTERVAL_CHECK_DEFAULT)

            getNextAlarm(context)
            createRepeatingTimecheck(context, interval)

            getNextAlarm(context)
        }

        fun createRepeatingTimecheck(context: Context, intervalInMinutes: Int){

            //todo create inexact version
            val alarms = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intentToSend=createIntentBroadcast(context)

            if(intentToSend==null){
                Log.e(Constants.APP_NAME, "Alarm already set!")
                return
            }

            Log.e(Constants.APP_NAME, "Alarm not set. yet")


            alarms.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 100,
                (1000 * 60 * intervalInMinutes).toLong(),
                intentToSend
            )
        }

        fun removeRepeatingTimecheck(context: Context){


            getNextAlarm(context)
            val alarms = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarms.cancel(createIntentBroadcast(context))
            createIntentBroadcast(context)?.cancel()

            getNextAlarm(context)

        }

        private fun createIntentBroadcast(context: Context): PendingIntent? {
            return createIntentBroadcast(context, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        private fun createIntentBroadcast(context: Context, flag: Int): PendingIntent? {

            val broadcastIntent = Intent(context, AlarmBroadcastReceiver::class.java)
            broadcastIntent.putExtra(Constants.BROADCAST_INTENT_ACTION, Constants.BROADCAST_INTENT_ACTION_UPDATE_VOLUME)

            // The Pending Intent to pass in AlarmManager
            val pIntent = PendingIntent.getBroadcast(context, Constants.RECURRING_INTENT_ID, broadcastIntent,  flag)
            return pIntent

        }

        private fun createRestartBroadcast(context: Context): PendingIntent? {

            val broadcastIntent = Intent(context, AlarmBroadcastReceiver::class.java)
            broadcastIntent.putExtra(Constants.BROADCAST_INTENT_ACTION,Constants.BROADCAST_INTENT_ACTION_DELAY)
            broadcastIntent.putExtra(Constants.BROADCAST_INTENT_ACTION_DELAY_EXTRA,Constants.BROADCAST_INTENT_ACTION_DELAY_RESTART_NOW)

            // The Pending Intent to pass in AlarmManager
            val pIntent = PendingIntent.getBroadcast(context,0,broadcastIntent,0)

            return pIntent

        }

        fun getNextAlarm(context: Context): Boolean{

            val pIntent = createIntentBroadcast(context,PendingIntent.FLAG_NO_CREATE)
            if (pIntent != null) {
                Log.e(Constants.APP_NAME, "intent exists")
                return true
            }else{
                Log.e(Constants.APP_NAME, "intent does not exists")
                return false
            }
        }

    }

}