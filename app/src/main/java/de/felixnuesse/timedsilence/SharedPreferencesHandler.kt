package de.felixnuesse.timedsilence

import android.content.Context

/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 10.04.19 - 18:07
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


class SharedPreferencesHandler {
    companion object {
        fun setPref(context: Context, name: String, value: String){
            val prefs = context.getSharedPreferences(Constants.PREFS_NAME, 0)
            val editor = prefs!!.edit()
            editor.putString(name, value)
            editor.apply()
        }

        fun setPref(context: Context, name: String, value: Int){
            val prefs = context.getSharedPreferences(Constants.PREFS_NAME, 0)
            val editor = prefs!!.edit()
            editor.putInt(name, value)
            editor.apply()
        }

        fun setPref(context: Context, name: String, value: Long){
            val prefs = context.getSharedPreferences(Constants.PREFS_NAME, 0)
            val editor = prefs!!.edit()
            editor.putLong(name, value)
            editor.apply()
        }

        fun setPref(context: Context, name: String, value: Boolean){
            val prefs = context.getSharedPreferences(Constants.PREFS_NAME, 0)
            val editor = prefs!!.edit()
            editor.putBoolean(name, value)
            editor.apply()
        }

        fun getPref(context: Context, name: String, value: String): String?{
            val prefs = context.getSharedPreferences(Constants.PREFS_NAME, 0)
            return prefs.getString(name, value)
        }

        fun getPref(context: Context, name: String, value: Long): Long{
            val prefs = context.getSharedPreferences(Constants.PREFS_NAME, 0)
            return prefs.getLong(name, value)
        }

        fun getPref(context: Context, name: String, value: Int): Int{
            val prefs = context.getSharedPreferences(Constants.PREFS_NAME, 0)
            return prefs.getInt(name, value)
        }
    }
}
