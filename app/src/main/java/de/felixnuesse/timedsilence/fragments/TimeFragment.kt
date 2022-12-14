package de.felixnuesse.timedsilence.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME
import de.felixnuesse.timedsilence.R
import kotlinx.android.synthetic.main.time_fragment.*
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import de.felixnuesse.timedsilence.dialogs.ScheduleDialog
import de.felixnuesse.timedsilence.model.data.ScheduleObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.ui.ScheduleListAdapter
import de.felixnuesse.timedsilence.ui.custom.NestedRecyclerManager


class TimeFragment : Fragment() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.time_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button_time_fragment.setOnClickListener {
            Log.e(APP_NAME, "TimeFragment: Add new!")
            ScheduleDialog(view.context, this).show()
        }



        val db = DatabaseHandler(view.context)

        Log.e(APP_NAME, "TimeFragment: DatabaseResuluts: Size: "+db.getAllSchedules().size)

        viewManager = NestedRecyclerManager(view.context)
        viewAdapter = ScheduleListAdapter(db.getAllSchedules())

        time_fragment_recylcer_list_view.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }




    }

    fun saveSchedule(context: Context, so: ScheduleObject){
        val db = DatabaseHandler(context)
        db.createScheduleEntry(so)
        viewAdapter = ScheduleListAdapter(db.getAllSchedules())

        time_fragment_recylcer_list_view.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }
    }
}
