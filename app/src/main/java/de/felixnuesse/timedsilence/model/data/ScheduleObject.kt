package de.felixnuesse.timedsilence.model.data

/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 13.04.19 - 19:41
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
class ScheduleObject(var name: String, var time_start: Long, var time_end: Long, var time_setting: Int, var id: Long) {


    constructor(name: String, time_start: Long, time_end: Long, time_setting: Int, id: Long,
                pmon: Boolean, ptue: Boolean, pwed: Boolean, pthu: Boolean, pfri: Boolean, psat: Boolean, psun: Boolean
    ) : this(name, time_start, time_end, time_setting, id){

        mon=pmon
        tue=ptue
        wed=pwed
        thu=pthu
        fri=pfri
        sat=psat
        sun=psun

    }

    var mon: Boolean = false
    var tue: Boolean = false
    var wed: Boolean = false
    var thu: Boolean = false
    var fri: Boolean = false
    var sat: Boolean = false
    var sun: Boolean = false


}