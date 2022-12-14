package de.felixnuesse.timedsilence


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

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.text.format.DateFormat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME
import de.felixnuesse.timedsilence.Constants.Companion.MAIN_ACTIVITY_LOAD_CALENDAR_FORCE
import de.felixnuesse.timedsilence.activities.SettingsMainActivity
import de.felixnuesse.timedsilence.fragments.CalendarEventFragment
import de.felixnuesse.timedsilence.fragments.KeywordFragment
import de.felixnuesse.timedsilence.fragments.TimeFragment
import de.felixnuesse.timedsilence.fragments.WifiConnectedFragment
import de.felixnuesse.timedsilence.fragments.graph.GraphFragment
import de.felixnuesse.timedsilence.handler.*
import de.felixnuesse.timedsilence.handler.calculator.CalendarHandler
import de.felixnuesse.timedsilence.handler.trigger.Trigger
import de.felixnuesse.timedsilence.handler.volume.VolumeHandler
import de.felixnuesse.timedsilence.receiver.AlarmBroadcastReceiver
import de.felixnuesse.timedsilence.services.PauseTimerService
import de.felixnuesse.timedsilence.services.WidgetService
import de.felixnuesse.timedsilence.services.`interface`.TimerInterface
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


class MainActivity : AppCompatActivity(), TimerInterface {


    private var mDontCheckGraph = false
    private var button_check : String = ""
    private var lastTabPosition = 0
    private lateinit var mPager : ViewPager
    private lateinit var mTrigger: Trigger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ThemeHandler.setTheme(this, window)
        ThemeHandler.setTabLayoutTheme(this, tabLayout)

        setContentView(R.layout.activity_main)
        setSupportActionBar(bottom_app_bar)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        VolumeHandler.getVolumePermission(this)
        CalendarHandler.getCalendarReadPermission(this)
        mTrigger = Trigger(this)

        button_check = getString(R.string.timecheck_stopped)

        //This hidden button is needed because the buttonsound of the main button is supressed because the device is still muted. A click is performed on this button, and when the onClick handler is set,
        //it plays a sound after the volume has changed to loud. Therefore it seems to be the main button who makes the sound
        button_buttonsound_fix.isSoundEffectsEnabled=true
        button_buttonsound_fix.setOnClickListener {
            Log.e(APP_NAME, "MainAcitivity: HiddenButton: PerformClick to make sound")
        }

        fab.setOnClickListener {
            //Log.e(APP_NAME, "Main: fab: Clicked")
            setHandlerState()
        }

        frameLayout.setOnClickListener {
            //Log.e(APP_NAME, "Main: FabTester: Clicked")
            buttonState()
        }


        val seekBarSupportText = findViewById<TextView>(R.id.textview_waittime_content)
        val seekBar = findViewById<SeekBar>(R.id.seekBar_waittime)

        val interval= SharedPreferencesHandler.getPref(
            this,
            PrefConstants.PREF_INTERVAL_CHECK,
            PrefConstants.PREF_INTERVAL_CHECK_DEFAULT
        )

        seekBarSupportText.text=interval.toString()
        seekBar.progress=interval

        //minimum is zero, so we need to offset by one
        seekBar.max=179
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Write code to perform some action when progress is changed.
                seekBarSupportText.text = (seekBar.progress + 1).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is started.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                //Toast.makeText(this@MainActivity, "Progress is " + seekBar.progress+1 + "%", Toast.LENGTH_SHORT).show()
                seekBarSupportText.text = (seekBar.progress + 1).toString()
                setInterval(seekBar.progress + 1)

            }
        })


        val tabs = tabLayout
        mPager = viewPager

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                lastTabPosition = tab.position
                mPager.currentItem = lastTabPosition
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        mPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                val tab = tabs.getTabAt(position)
                tab?.select()
            }
        })


        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        mPager.adapter = pagerAdapter

        tabs.getTabAt(0)?.setText("")
        tabs.getTabAt(1)?.setText("")
        tabs.getTabAt(2)?.setText("")
        tabs.getTabAt(3)?.setText("")
        tabs.getTabAt(4)?.setText("")


        SharedPreferencesHandler.getPreferences(this)?.registerOnSharedPreferenceChangeListener(
            getSharedPreferencesListener()
        )

        PauseTimerService.registerListener(this)

        // use this to start and trigger a service
        val i = Intent(this, WidgetService::class.java)
        // potentially add data to the intent
        i.putExtra("KEY1", "Value to be used by the service");
        startService(i)
        buttonState()

        loadCalendarFragment()
        if(mDontCheckGraph){
            AlarmBroadcastReceiver().switchVolumeMode(this)
        }
    }

    fun loadCalendarFragment(){
        val intentFragment = intent?.extras?.getInt(Constants.MAIN_ACTIVITY_LOAD_CALENDAR)
        if(intentFragment == MAIN_ACTIVITY_LOAD_CALENDAR_FORCE){
            mPager.currentItem = 2
            mDontCheckGraph=true
        }
    }
    override fun onResume() {
        super.onResume()

        loadCalendarFragment()
        buttonState()
        SharedPreferencesHandler.getPreferences(this)?.registerOnSharedPreferenceChangeListener(
            getSharedPreferencesListener()
        )


        //This handler is needed. Otherwise the state is not beeing restored
        Handler().postDelayed({
            if (viewPager.adapter != null) {
                viewPager.adapter = ScreenSlidePagerAdapter(supportFragmentManager)
                mPager.currentItem = lastTabPosition
            }
        }, 0)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        val voLHandler = VolumeHandler(baseContext)

        when (item.itemId) {
            R.id.action_settings -> openSettings()
            R.id.action_set_manual_loud -> {
                val makeSound = !voLHandler.isButtonClickAudible(this)
                voLHandler.setLoud()
                if (makeSound) {
                    button_buttonsound_fix.performClick()
                }
                Toast.makeText(this, getString(R.string.loud), Toast.LENGTH_LONG).show()
            }
            R.id.action_set_manual_vibrate -> {
                voLHandler.setVibrate(); Toast.makeText(
                    this, getString(
                        R.string.vibrate
                    ), Toast.LENGTH_LONG
                ).show()
            }
            R.id.action_set_manual_silent -> {
                voLHandler.setSilent(); Toast.makeText(
                    this, getString(
                        R.string.silent
                    ), Toast.LENGTH_LONG
                ).show()
            }
        }
        voLHandler.applyVolume(applicationContext)
        return true
    }

    override fun timerStarted(context: Context, timeAsLong: Long, timeAsString: String) {}

    override fun timerReduced(context: Context, timeAsLong: Long, timeAsString: String) {
        buttonState()
    }

    override fun timerFinished(context: Context) {
        buttonState()
    }


    fun openSettings(): Boolean {
        val intent = Intent(this, SettingsMainActivity::class.java).apply {}
        startActivity(intent)
        return true
    }



    fun setInterval(interval: Int){
        SharedPreferencesHandler.setPref(this, PrefConstants.PREF_INTERVAL_CHECK, interval)
        mTrigger.removeTimecheck()
        mTrigger.createTimecheck()

    }


    fun updateTimeCheckDisplay(){
        val nextCheckDisplayTextView= findViewById<TextView>(R.id.nextCheckDisplay)
        nextCheckDisplayTextView.text= mTrigger.getNextAlarmTimestamp()

        val sharedPref = this?.getSharedPreferences("test", Context.MODE_PRIVATE) ?: return
        val lasttime = sharedPref.getLong("last_ExecTime", 0)

        val current =System.currentTimeMillis()
        val date = Date(current)


        //val df = DateFormat.
        val date1 = Date(current)
        val date2 = Date(lasttime)

        var difference = date1.time - date2.time

        val days = (difference / (1000 * 60 * 60 * 24));
        val hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60))
        val min = (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours) / (1000 * 60)

        var highScore =  DateFormat.getTimeFormat(this)
        if(hours>24){
            highScore =  DateFormat.getDateFormat(this)
        }

        nextCheckDisplayTextView.text=highScore.format(date)
    }



    private fun buttonState() {

        Log.e(Constants.APP_NAME, "Main: ButtonStartCheck: State: " + button_check)

        //Todo remove dummy textview
        if(mTrigger.checkIfNextAlarmExists()){
            setFabStarted(fab, TextView(this))
        }else if(PauseTimerService.isTimerRunning()){
            setFabPaused(fab, TextView(this))
        }else{
            setFabStopped(fab, TextView(this))
        }
        updateTimeCheckDisplay()
        WidgetService.updateStateWidget(this)


        val seekBarSupportText = findViewById<TextView>(R.id.textview_waittime_content)
        val seekBar = findViewById<SeekBar>(R.id.seekBar_waittime)
        val tv = findViewById<TextView>(R.id.interval_text_view)
        val tv1 = findViewById<TextView>(R.id.textView4)


        when (mTrigger.getTriggertype()) {
            PrefConstants.PREF_TRIGGERTYPE_REPEATING -> {
                seekBarSupportText.visibility = View.VISIBLE
                seekBar.visibility = View.VISIBLE
                tv.visibility = View.VISIBLE
                tv1.visibility = View.VISIBLE
            }
            PrefConstants.PREF_TRIGGERTYPE_TARGETED -> {
                seekBarSupportText.visibility = View.GONE
                seekBar.visibility = View.GONE
                tv.visibility = View.GONE
                tv1.visibility = View.GONE
            }
            else ->{
                seekBarSupportText.visibility = View.GONE
                seekBar.visibility = View.GONE
                tv.visibility = View.GONE
                tv1.visibility = View.GONE
            }
        }

    }

    private fun setHandlerState() {

        Log.e(APP_NAME, "Main: setHandlerState: State: " + button_check)

        if(button_check == getString(R.string.timecheck_start)){
            mTrigger.createTimecheck()
            SharedPreferencesHandler.setPref(this, PrefConstants.PREF_BOOT_RESTART, true)
            AlarmBroadcastReceiver().switchVolumeMode(this)
        }else if(button_check == getString(R.string.timecheck_paused)){
            mTrigger.createTimecheck()
            SharedPreferencesHandler.setPref(this, PrefConstants.PREF_BOOT_RESTART, true)
            PauseTimerService.cancelTimer(this)
            AlarmBroadcastReceiver().switchVolumeMode(this)
        }else{
            mTrigger.removeTimecheck()
            SharedPreferencesHandler.setPref(this, PrefConstants.PREF_BOOT_RESTART, false)
        }
        buttonState()
    }


    fun setFabStarted(fab: FloatingActionButton, text: TextView){
        text.text = getString(R.string.timecheck_running)
        fab.backgroundTintList = ColorStateList.valueOf(getColor(R.color.colorFab_running))
       // fab.setImageResource(R.drawable.ic_play_arrow_white_24dp)

        val d = getDrawable(R.drawable.ic_pause_black_24dp)
        d?.mutate()?.setColorFilter(
            resources.getColor(R.color.colorStateButtonIcon),
            PorterDuff.Mode.SRC_IN
        )

        fab.setImageDrawable(d)

        button_check=getString(R.string.timecheck_stop)

    }

    fun setFabStopped(fab: FloatingActionButton, text: TextView){
        text.text = getString(R.string.timecheck_stopped)
        fab.backgroundTintList = ColorStateList.valueOf(getColor(R.color.colorFab_stopped))


        val d = getDrawable(R.drawable.ic_play_arrow_white_24dp)
        d?.mutate()?.setColorFilter(
            resources.getColor(R.color.colorStateButtonIcon),
            PorterDuff.Mode.SRC_IN
        )

        fab.setImageDrawable(d)
        mTrigger.removeTimecheck()
        button_check=getString(R.string.timecheck_start)
    }

    fun setFabPaused(fab: FloatingActionButton, text: TextView){
        text.text = getString(R.string.timecheck_paused)
        fab.backgroundTintList = ColorStateList.valueOf(getColor(R.color.colorFab_paused))
        fab.setImageResource(R.drawable.ic_fast_forward_white_24dp)
        button_check=getString(R.string.timecheck_paused)
    }

    @Deprecated("replace by callback")
    fun getSharedPreferencesListener(): SharedPreferences.OnSharedPreferenceChangeListener {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if(key==PrefConstants.PREFS_LAST_KEY_EXEC){
                updateTimeCheckDisplay()
            }
        }
        return listener
    }


    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     * FragmentStatePagerAdapter
     */
    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getCount(): Int = 5

        override fun getItem(position: Int): Fragment {

            when (position) {
                0 -> return GraphFragment()
                1 -> return TimeFragment()
                2 -> return CalendarEventFragment()
                3 -> return KeywordFragment()
                4 -> return WifiConnectedFragment()
                else -> return TimeFragment()
            }
        }
    }

}
