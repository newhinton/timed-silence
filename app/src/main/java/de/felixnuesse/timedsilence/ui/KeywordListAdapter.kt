package de.felixnuesse.timedsilence.ui;

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_LOUD
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_VIBRATE
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.model.data.KeywordObject
import de.felixnuesse.timedsilence.model.data.CalendarObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import kotlinx.android.synthetic.main.adapter_keyword_list.view.*
import kotlin.collections.ArrayList


/**
 * Copyright (C) 2021  Felix Nüsse
 * Created on 15.07.21 - 21:10
 * <p>
 * Edited by: Felix Nüsse felix.nuesse(at)t-online.de
 * <p>
 * <p>
 * This program is released under the GPLv3 license
 * <p>
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

class KeywordListAdapter(private var myDataset: ArrayList<KeywordObject>) : RecyclerView.Adapter<KeywordListAdapter.CalendarKeywordViewHolder>() {

        fun removeAt(position: Int) {
                myDataset.removeAt(position)
                notifyDataSetChanged()
        }

        fun update(context: Context, co: CalendarObject){
                DatabaseHandler(context).updateCalendarEntry(co)
                myDataset.clear()
                myDataset = DatabaseHandler(context).getKeywords()
                notifyDataSetChanged()
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeywordListAdapter.CalendarKeywordViewHolder {
                // create a new view
                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_keyword_list, parent, false)
                // set the view's size, margins, paddings and layout parameters
                return CalendarKeywordViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: CalendarKeywordViewHolder, position: Int) {
                // - get element from your dataset at this position
                // - replace the contents of the view with that element
                val keywordObject=myDataset.get(position)

                holder.calendarKeywordView.keyword_keyword.text = keywordObject.keyword

                holder.calendarKeywordView.delete_keyword_element.setOnClickListener {
                        DatabaseHandler(holder.calendarKeywordView.context).deleteKeyword(keywordObject.id)
                        removeAt(position)
                }

                var imageID=R.drawable.ic_volume_up_black_24dp
                when (keywordObject.volume) {
                        TIME_SETTING_LOUD -> imageID=R.drawable.ic_volume_up_black_24dp
                        TIME_SETTING_VIBRATE -> imageID=R.drawable.ic_vibration_black_24dp
                        TIME_SETTING_SILENT -> imageID=R.drawable.ic_volume_off_black_24dp
                }
                holder.calendarKeywordView.keyword_state.setImageDrawable(holder.calendarKeywordView.context.getDrawable(imageID))
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
        class CalendarKeywordViewHolder(val calendarKeywordView: View) : RecyclerView.ViewHolder(calendarKeywordView)

}


