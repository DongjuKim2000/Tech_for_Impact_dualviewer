package com.nightscout.nightviewer
import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.nightscout.nightviewer.databinding.ActivityFullscreen1Binding
import java.text.SimpleDateFormat

class FullscreenActivity1 : AppCompatActivity() {

    lateinit var binding: ActivityFullscreen1Binding
    val showinfobr = ShowinfoBR()  //ShowinfoBR() 클래스
    var activityName = "액티비티1"

    private lateinit var fullscreenContent1: TextView
    private lateinit var fullscreenContent2: TextView
    private lateinit var fullscreenContent3: LineChart
    private lateinit var fullscreenContent4: TextView

    private val hideHandler = Handler(Looper.myLooper()!!)

    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent1.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            fullscreenContent2.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            fullscreenContent3.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            fullscreenContent4.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            fullscreenContent1.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            fullscreenContent2.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            fullscreenContent3.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            fullscreenContent4.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
        //fullscreenContentControls.visibility = View.VISIBLE
    }
    private var isFullscreen: Boolean = false
    private val hideRunnable = Runnable { hide() }

    inner class ShowinfoBR : BroadcastReceiver()
    {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent?.action == "showinfo")
                showinfo()
        }
    }
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val delayHideTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS)
            }
            MotionEvent.ACTION_UP -> view.performClick()
            else -> {
            }
        }
        false
    }

    @SuppressLint("ClickableViewAccessibility")

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_preference -> {
                val i = Intent(this, PreferencesActivity::class.java)
                startActivity(i)

                return true
            }
            R.id.menu_about -> {

                val image = ImageView(this)
                image.setImageResource(R.drawable.kst1d)

                var msg = "Made by 김해서연아빠<br>"
                msg += "Icon directed by 광명셀리나맘 Icon made by 광명셩키<br>"
                msg += "Thanks to 박상미 강서연 강지유 시조새팬클럽<br>"
                msg += "and 한국1형당뇨병환우회<br><br>"
                msg += "환우회 링크   :&nbsp;"
                msg += "<a href=\"http://kst1d.org\">홈페이지</a>&nbsp;&nbsp;&nbsp;"
                msg += "<a href=\"https://cafe.naver.com/t1d\">공식카페(슈거트리)</a><br>"
                msg += "<a href=\"https://blog.naver.com/kst1diabetes\">블로그</a>&nbsp;&nbsp;&nbsp;"
                msg += "<a href=\"https://www.youtube.com/channel/UCyO4LR8XD-UzCdsjAWRGlNQ?view_as=subscriber\">유튜브</a>&nbsp;&nbsp;&nbsp;"
                msg += "<a href=\"https://www.instagram.com/kst1diabetes\">인스타그램</a>&nbsp;&nbsp;&nbsp;"
                msg += "<a href=\"https://www.facebook.com/%ED%95%9C%EA%B5%AD1%ED%98%95%EB%8B%B9%EB%87%A8%EB%B3%91%ED%99%98%EC%9A%B0%ED%9A%8C-509826469456836\">페이스북</a>"

                var newmsg = Html.fromHtml(msg)

                val d: AlertDialog = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogTheme))

                    .setPositiveButton("OK", null)
                    //.setNegativeButton(android.R.string.ok, null)
                    //.setNeutralButton(android.R.string.ok, null)
                    .setIcon(R.mipmap.ic_main_round)
                    .setTitle("Nightviewer v1.11")
                    .setMessage(newmsg)
                    .setView(image)
                    .create()

                d.show()

                val positiveButton: Button = d.getButton(AlertDialog.BUTTON_POSITIVE)
                positiveButton.setTextColor(Color.parseColor("#515151"))

                (d.findViewById(android.R.id.message) as TextView).movementMethod = LinkMovementMethod.getInstance()
                //(d.findViewById(android.R.id.message) as TextView).gravity = Gravity.CENTER

                return true
                //Toast.makeText(this, "테스트", Toast.LENGTH_SHORT).show()
            }
            R.id.menu_exit -> {
                finish()
                return true
            }
            else -> return false
        }
    }

    override fun onBackPressed() {
        /*val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)*/
        //log.d(activityName,"onBackPressed")
        finish()
    }

    override fun onOptionsMenuClosed(menu: Menu?) {
        super.onOptionsMenuClosed(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView((R.layout.activity_fullscreen))

        prefs = getSharedPreferences("root_preferences", MODE_PRIVATE)
        bgprefs = getSharedPreferences("prefs_bghistory", MODE_PRIVATE)

        //log.d(activityName,"onCreate")

        binding = ActivityFullscreen1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= 26) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.icon)
        }
        isFullscreen = true
        // Set up the user interaction to manually show or hide the system UI.
        //fullscreenContent = binding.screen1
        fullscreenContent1 = binding.screenBg
        fullscreenContent2 = binding.screenDirection
        fullscreenContent3 = binding.screenLinechart
        fullscreenContent4 = binding.screenInfo
        fullscreenContent1.setOnClickListener { toggle() }
        fullscreenContent2.setOnClickListener { toggle() }
        fullscreenContent3.setOnClickListener { toggle() }
        fullscreenContent4.setOnClickListener { toggle() }

        val filter = IntentFilter()
        filter.addAction("showinfo") //수신할 action 종류 넣기
        registerReceiver(showinfobr, filter) //브로드캐스트리시버 등록

        if (ChartValue.count() == 0)
        {
            for(i: Int in 1..60)
                ChartValue.add(0)

            for(i: Int in 1..60)
                ChartValueDateTime.add(0)
        }

        binding.screenBg.text = ""
        binding.screenDirection.text = ""
        binding.screenInfo.text = ""

        setLineChartInitialization()

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        //log.d(activityName,"onPostCreate")
        delayedHide(100)
    }

    override fun onResume() {
        //log.d(activityName,"onResume")
        super.onResume()
        //Rest(this).getAllBG()
        Rest(this).getBG()
    }

    override fun onDestroy() {
        super.onDestroy()
        try{unregisterReceiver(showinfobr)} catch (e: Exception){}
        //log.d(activityName,"onDestroy")
    }

    private fun toggle() {
        if (isFullscreen) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {

        // Hide UI first
        supportActionBar?.hide()
        //fullscreenContentControls.visibility = View.GONE
        isFullscreen = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())

    }

    private fun show() {

        // Show the system bar
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent1.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            fullscreenContent2.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            fullscreenContent3.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            fullscreenContent4.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            fullscreenContent1.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            fullscreenContent2.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            fullscreenContent3.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            fullscreenContent4.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
        isFullscreen = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())

    }

    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    private fun showinfo() {

        //설정
        val pref_timeformat = prefs.getString("preftimeformat", "timeformat24")
        val pref_urgenthighvalue = prefs.getString ("urgent_high_value", "260")?.toFloat() ?: 260f
        val pref_highvalue = prefs.getString ("high_value", "180")?.toFloat() ?: 180f
        val pref_lowvalue = prefs.getString ("low_value", "80")?.toFloat() ?: 80f
        val pref_urgentlowvalue = prefs.getString ("urgent_low_value", "55")?.toFloat() ?: 55f
        val pref_bgfont = prefs.getString ("bg_font", "200")?.toFloat() ?: 200f
        val pref_directionfont = prefs.getString ("direction_font", "100")?.toFloat() ?: 100f
        val pref_timeinfofont = prefs.getString ("timeinfo_font", "30")?.toFloat() ?: 30f
        val pref_fontcolornormal = prefs.getString ("fontcolornormal", "#FCFFFFFF").toString()
        val pref_fontcolorhighlow = prefs.getString ("fontcolorhighlow", "#FCFFFFFF").toString()
        val pref_fontcolorurgenthighlow = prefs.getString ("fontcolorurgenthighlow", "#FCFFFFFF").toString()

        //log.d(activityName, "showinfo 시작")

        //텍스트뷰
        val currentbg = BgClass()
        currentbg.LoadCurrentBG()

        val currentTime : Long = System.currentTimeMillis() // ms로 반환
        val bgTime: Long = currentbg.bgtime.toLong()
        var mins: Long = 0
        var displayMins: String = ""
        var info : String = ""

        if (bgTime != 0L)
        {
            val minago_long = currentTime - bgTime
            mins = minago_long / (1000 * 60)
            displayMins = mins.toString()+"min"
        }

        var sdf = SimpleDateFormat("HH:mm")
        if (pref_timeformat == "timeformat12"){ sdf = SimpleDateFormat("a hh:mm") }
        val displayTime: String = sdf.format(currentTime)
        info = "$displayTime   $displayMins"

        var displayIOB = ""
        if (currentbg.iob != ""){
            displayIOB = "   \uD83C\uDD58${currentbg.iob}U"
            info += displayIOB
        }

        var displayCOB = ""
        if (currentbg.cob != ""){
            displayCOB = "   \uD83C\uDD52${currentbg.cob}g"
            info += displayCOB
        }

        var displayBASAL = ""
        if (currentbg.basal != ""){
            displayBASAL = "   \uD83C\uDD51${currentbg.basal}/h"
            info += displayBASAL
        }

        binding.screenBg.text = currentbg.bg
        val direction = "${currentbg.arrow} ${currentbg.delta}"
        binding.screenDirection.text = direction
        binding.screenInfo.text = info

        binding.screenBg.textSize = pref_bgfont.toFloat()
        binding.screenDirection.textSize = pref_directionfont.toFloat()
        binding.screenInfo.textSize = pref_timeinfofont.toFloat()

        //글자색깔변경
        var fontcolor : Int
        try {
            val bgInt : Int = currentbg.bg.toInt()

            //일반혈당
            if (pref_lowvalue <= bgInt && bgInt <= pref_highvalue) {
                fontcolor = Color.parseColor(pref_fontcolornormal)
            }
            else if (pref_urgentlowvalue <= bgInt && bgInt <= pref_urgenthighvalue) {
                fontcolor = Color.parseColor(pref_fontcolorhighlow)
            }
            else {
                fontcolor = Color.parseColor(pref_fontcolorurgenthighlow)
            }
        }
        catch (e: Exception ) {
            fontcolor = Color.WHITE
        }

        if (mins >= 16){
            binding.screenBg.setPaintFlags(binding.screenBg.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
            binding.screenDirection.setPaintFlags(binding.screenDirection.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
            fontcolor = Color.GRAY
            binding.screenDirection.setTextColor(Color.GRAY)
        }
        else {
            binding.screenBg.setPaintFlags(binding.screenBg.getPaintFlags() and Paint.STRIKE_THRU_TEXT_FLAG.inv())
            binding.screenDirection.setPaintFlags(binding.screenDirection.getPaintFlags() and Paint.STRIKE_THRU_TEXT_FLAG.inv())
            binding.screenDirection.setTextColor(Color.WHITE)
        }

        binding.screenBg.setTextColor(fontcolor)

        //차트
        val bgh = BgHistoryClass()
        bgh.LoadBGHistory()

        var maxbg : Int = pref_highvalue.toInt()
        var currenttime : Long = System.currentTimeMillis()
        currenttime -= (currenttime % 60000)
        for(i: Int in 0 until bgh.bghistorydatetime.count()){
            if (bgh.bghistorydatetime[i] > currenttime) currenttime = bgh.bghistorydatetime[i]
        }

        for(i: Int in 0 until ChartValueDateTime.count()){
            ChartValueDateTime[i] = currenttime - (60000 * i)
            ChartValue[i] = 0

            for(j: Int in 0 until bgh.bghistorydatetime.count()){
                if (ChartValueDateTime[i] == bgh.bghistorydatetime[j]) {
                    ChartValue[i] = bgh.bghistory[j]

                    if (ChartValue[i] >= maxbg) {
                        maxbg = ChartValue[i]
                    }
                }
            }
        }

        val lineChart: LineChart = binding.screenLinechart

        var i : Int = 0
        for(v in ChartValue.reversed()){
            lineChart.data.removeEntry(i.toFloat(),0)
            lineChart.data.removeEntry(i.toFloat(),1)
            lineChart.data.removeEntry(i.toFloat(),2)
            i++
        }

        i = 0
        for(v in ChartValue.reversed()){

            if (pref_lowvalue <= v && v <= pref_highvalue) {
                lineChart.data.addEntry( Entry(i.toFloat(), v.toFloat()),0 )
            }
            else if (pref_urgentlowvalue <= v && v <= pref_urgenthighvalue) {
                lineChart.data.addEntry( Entry(i.toFloat(), v.toFloat()),1 )
            }
            else {
                lineChart.data.addEntry( Entry(i.toFloat(), v.toFloat()),2 )
            }
            i++
        }

        lineChart.axisLeft.axisMaximum = (maxbg + 1).toFloat()
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()

        if (isFullscreen) { hide() }

    }

    private fun setLineChartInitialization() {

        //설정
        val pref_urgenthighvalue = prefs.getString ("urgent_high_value", "260")?.toFloat() ?: 260f
        val pref_highvalue = prefs.getString ("high_value", "180")?.toFloat() ?: 180f
        val pref_lowvalue = prefs.getString ("low_value", "80")?.toFloat() ?: 80f
        val pref_urgentlowvalue = prefs.getString ("urgent_low_value", "55")?.toFloat() ?: 55f
        val pref_chartbgcolornormal = prefs.getString ("chartbgcolornormal", "#FC00FF00").toString()
        val pref_chartbgcolorhighlow = prefs.getString ("chartbgcolorhighlow", "#FCFFFF00").toString()
        val pref_chartbgcolorurgenthighlow = prefs.getString ("chartbgcolorurgenthighlow", "#FCFF0000").toString()
        val pref_chartbgpointsize = prefs.getString ("chartbgpointsize", "5")?.toFloat() ?: 5f
        val pref_chartlinecolorhighlow = prefs.getString ("chartlinecolorhighlow", "#FCFFFF00").toString()
        val pref_chartlinecolorurgenthighlow = prefs.getString ("chartlinecolorurgenthighlow", "#FCFF0000").toString()
        val pref_chartlinewidth = prefs.getString ("chartlinewidth", "1")?.toFloat() ?: 1f

        //log.d(activityName, "초기화 시작")

        val lineChart: LineChart = binding.screenLinechart
        lineChart.clear()

        //차트초기화
        val entrybg = ArrayList<Entry>();
        val entrybghighlow = ArrayList<Entry>();
        val entrybgurgenthighlow = ArrayList<Entry>();
        val entrylineurgenthigh = ArrayList<Entry>();
        val entrylinehigh = ArrayList<Entry>();
        val entrylinelow = ArrayList<Entry>();
        val entrylineurgentlow = ArrayList<Entry>();

        for((i, v) in ChartValue.reversed().withIndex()){
            if (pref_lowvalue <= v && v <= pref_highvalue) {
                entrybg.add(Entry(i.toFloat(),v.toFloat()))
            }
            else if (pref_urgentlowvalue <= v && v <= pref_urgenthighvalue) {
                entrybghighlow.add(Entry(i.toFloat(),v.toFloat()))
            }
            else {
                entrybgurgenthighlow.add(Entry(i.toFloat(),v.toFloat()))
            }

            if (i == 0 || i == 59)
            {
                entrylineurgenthigh.add(Entry(i.toFloat(), pref_urgenthighvalue))
                entrylinehigh.add(Entry(i.toFloat(), pref_highvalue))
                entrylinelow.add(Entry(i.toFloat(), pref_lowvalue))
                entrylineurgentlow.add(Entry(i.toFloat(), pref_urgentlowvalue))
            }
        }

        val linedata_bg = LineDataSet(entrybg,"")
        linedata_bg.color = Color.BLACK
        linedata_bg.circleRadius = pref_chartbgpointsize
        linedata_bg.setCircleColor(Color.parseColor(pref_chartbgcolornormal))
        linedata_bg.circleHoleColor = Color.parseColor(pref_chartbgcolornormal)
        linedata_bg.setDrawHighlightIndicators(false)
        linedata_bg.setDrawValues(false)

        val linedata_bghighlow = LineDataSet(entrybghighlow,"")
        linedata_bghighlow.color = Color.BLACK
        linedata_bghighlow.circleRadius = pref_chartbgpointsize
        linedata_bghighlow.setCircleColor(Color.parseColor(pref_chartbgcolorhighlow))
        linedata_bghighlow.circleHoleColor = Color.parseColor(pref_chartbgcolorhighlow)
        linedata_bghighlow.setDrawHighlightIndicators(false)
        linedata_bghighlow.setDrawValues(false)

        val linedata_bgurgenthighlow = LineDataSet(entrybgurgenthighlow,"")
        linedata_bgurgenthighlow.color = Color.BLACK
        linedata_bgurgenthighlow.circleRadius = pref_chartbgpointsize
        linedata_bgurgenthighlow.setCircleColor(Color.parseColor(pref_chartbgcolorurgenthighlow))
        linedata_bgurgenthighlow.circleHoleColor = Color.parseColor(pref_chartbgcolorurgenthighlow)
        linedata_bgurgenthighlow.setDrawHighlightIndicators(false)
        linedata_bgurgenthighlow.setDrawValues(false)

        val linedata_lineurgenthigh = LineDataSet(entrylineurgenthigh,"")
        linedata_lineurgenthigh.color = Color.parseColor(pref_chartlinecolorurgenthighlow)
        linedata_lineurgenthigh.setDrawCircles(false)
        linedata_lineurgenthigh.setDrawCircleHole(false)
        linedata_lineurgenthigh.lineWidth = pref_chartlinewidth
        linedata_lineurgenthigh.setDrawHighlightIndicators(false)
        linedata_lineurgenthigh.setDrawValues(false)

        val linedata_linehigh = LineDataSet(entrylinehigh,"")
        linedata_linehigh.color = Color.parseColor(pref_chartlinecolorhighlow)
        linedata_linehigh.setDrawCircles(false)
        linedata_linehigh.setDrawCircleHole(false)
        linedata_linehigh.lineWidth = pref_chartlinewidth
        linedata_linehigh.setDrawHighlightIndicators(false)
        linedata_linehigh.setDrawValues(false)

        val linedata_linelow = LineDataSet(entrylinelow,"")
        linedata_linelow.color = Color.parseColor(pref_chartlinecolorhighlow)
        linedata_linelow.setDrawCircles(false)
        linedata_linelow.setDrawCircleHole(false)
        linedata_linelow.lineWidth = pref_chartlinewidth
        linedata_linelow.setDrawHighlightIndicators(false)
        linedata_linelow.setDrawValues(false)

        val linedata_lineurgentlow = LineDataSet(entrylineurgentlow,"")
        linedata_lineurgentlow.color = Color.parseColor(pref_chartlinecolorurgenthighlow)
        linedata_lineurgentlow.setDrawCircles(false)
        linedata_lineurgentlow.setDrawCircleHole(false)
        linedata_lineurgentlow.lineWidth = pref_chartlinewidth
        linedata_lineurgentlow.setDrawHighlightIndicators(false)
        linedata_lineurgentlow.setDrawValues(false)

        val data = LineData(linedata_bg,linedata_bghighlow,linedata_bgurgenthighlow,linedata_lineurgenthigh,linedata_linehigh,linedata_linelow,linedata_lineurgentlow)

        lineChart.data = data
        lineChart.setBackgroundColor(Color.BLACK)
        lineChart.legend.isEnabled = false
        lineChart.description.isEnabled = false
        lineChart.xAxis.isEnabled = false
        lineChart.axisLeft.isEnabled = false
        lineChart.axisRight.isEnabled = false
        lineChart.xAxis.setDrawGridLines(false)
        lineChart.axisRight.setDrawGridLines(false)
        lineChart.axisLeft.setDrawGridLines(false)
        lineChart.axisLeft.axisMaximum = pref_highvalue + 1
        lineChart.axisLeft.axisMinimum = 40f
        lineChart.setScaleEnabled(false)

    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }

}
