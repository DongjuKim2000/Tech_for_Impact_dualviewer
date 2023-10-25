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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.nightscout.nightviewer.databinding.ActivityFullscreen1Binding
import java.text.SimpleDateFormat
import androidx.core.text.HtmlCompat

// 멀티스크린을 위한 액티비티입니다.
class FullscreenActivity1 : AppCompatActivity() {

    lateinit var binding: ActivityFullscreen1Binding
    val showinfobr = ShowinfoBR()  //ShowinfoBR() 클래스. 이 프로그램의 유일한 리시버

    private lateinit var fullscreenContent5:LinearLayout

    private val hideHandler = Handler(Looper.myLooper()!!)

    private var isFullscreen: Boolean = false
    private val hideRunnable = Runnable { hide() }

    inner class ShowinfoBR : BroadcastReceiver()
    {
        override fun onReceive(context: Context?, intent: Intent?) {
            showinfo()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // 메뉴 만들고 수행
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 메뉴에서 고른 항목별 액티비티 실행
        when (item.itemId) {
            R.id.menu_preference -> {
                val i = Intent(this, PreferencesActivity::class.java)
                startActivity(i) // preference 설정 페이지로 넘어감
                return true
            }
            R.id.menu_about -> {
                val dialogView = LayoutInflater.from(this).inflate(R.layout.menu_about_layout, null)
                val messageTextView = dialogView.findViewById<TextView>(R.id.about_message)
                messageTextView.movementMethod = LinkMovementMethod.getInstance()

                val about_message: AlertDialog = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogTheme))
                    .setPositiveButton("Thank you", null)
                    .setIcon(R.mipmap.ic_main_round)
                    .setTitle(R.string.menu_about_message)
                    .setView(dialogView)
                    .create()
                about_message.show()

                val positiveButton: Button = about_message.getButton(AlertDialog.BUTTON_POSITIVE)
                positiveButton.setTextColor(Color.parseColor("#00ff00"))

                (about_message.findViewById(android.R.id.message) as TextView).movementMethod = LinkMovementMethod.getInstance()

                val htmlMessage = HtmlCompat.fromHtml(getString(R.string.menu_about_message), HtmlCompat.FROM_HTML_MODE_LEGACY)
                messageTextView.text = htmlMessage
                messageTextView.movementMethod = LinkMovementMethod.getInstance()

                return true
            }

            R.id.menu_exit -> { // 설정 버튼 중 exit(나가기) 누름
                finish()  // 앱 (완전히) 종료
                return true
            }
            else -> return false // 지정된 버튼이 아닌 다른 곳 누름
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onOptionsMenuClosed(menu: Menu?) {
        super.onOptionsMenuClosed(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreen1Binding.inflate(layoutInflater) //??

        Log.d("Activity1","onCreate")
        setContentView(binding.root) // xml 파일 지정

        if (Build.VERSION.SDK_INT >= 26) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.icon)
        }
        isFullscreen = true
        // Set up the user interaction to manually show or hide the system UI.

        fullscreenContent5 = binding.fullscreenall
        fullscreenContent5.setOnClickListener { toggle() }

        val filter = IntentFilter()  // 인텐트 지정
        filter.addAction("showinfo") //수신할 action 종류 넣기
        registerReceiver(showinfobr, filter) //브로드캐스트리시버 등록
        Log.d("Activity1","브로드캐스트리시버 등록 완료")

        if (ChartValue.count() == 0) // 일단 필요 없는 부분임
        {
            for(i: Int in 1..60)
                ChartValue.add(0)

            for(i: Int in 1..60)
                ChartValueDateTime.add(0)
        }

        binding.screenBg.text = ""
        binding.screenDirection.text = ""
        binding.screenInfo.text = ""

        Log.d("Activity1","onCreate 끝")

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        Log.d("Activity1","onPostCreate")
    }

    override fun onResume() {
        Log.d("Activity1","onResume")
        super.onResume()
        if(Build.VERSION.SDK_INT >= 24) {
            if (isInMultiWindowMode) {
                Log.d("Activity1", "multi window mode")
                Log.d("FullscreenActivity","스타트 activity1")

            } else {
                val i = Intent(this, FullscreenActivity2::class.java)
                startActivity(i) // 멀티 윈도우 모드로 진행
                Log.d("Activity2", "not multi window")
            }
        }

        Rest(this).getBG()
    }

    override fun onDestroy() {
        Log.d("Activity1","onDetroy  시작")
        super.onDestroy()
        try{unregisterReceiver(showinfobr)} catch (e: Exception){}
    }


    private fun toggle() { //터치시 상단바
        Log.d("toggle", "toggle 작동")
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
        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.post(hidePart2Runnable)
        isFullscreen = false
    }

    private fun show() {

        // Show the system bar
        supportActionBar?.show()
        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.post(showPart2Runnable)
        isFullscreen = true

    }

    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent5.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            fullscreenContent5.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }

    private val showPart2Runnable = Runnable {
        supportActionBar?.show()
    }


    private fun showinfo() {//설정
        // class.kt가 바뀔 시 수정되어야 하는 부분
        //일단 글씨 크기 임의로 고정함. 추후 바뀔 예정
        val pref_bgfont = "90".toFloat()
        val pref_directionfont = "45".toFloat()
        val pref_timeinfofont = "25".toFloat()

        val pref_timeformat = prefs.getString("preftimeformat", "timeformat24")
        val pref_urgenthighvalue = prefs.getString ("urgent_high_value", "260")?.toFloat() ?: 260f
        val pref_highvalue = prefs.getString ("high_value", "180")?.toFloat() ?: 180f
        val pref_lowvalue = prefs.getString ("low_value", "80")?.toFloat() ?: 80f
        val pref_urgentlowvalue = prefs.getString ("urgent_low_value", "55")?.toFloat() ?: 55f
        val pref_fontcolornormal = prefs.getString ("fontcolornormal", "#FCFFFFFF").toString()
        val pref_fontcolorhighlow = prefs.getString ("fontcolorhighlow", "#FCFFFFFF").toString()
        val pref_fontcolorurgenthighlow = prefs.getString ("fontcolorurgenthighlow", "#FCFFFFFF").toString()

        Log.d("showinfo", "showinfo 시작")

        //텍스트뷰
        val currentbg = BgClass()
        currentbg.LoadCurrentBG()
        //시간관련
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
        // 현재 iob cob는 못 가져오는중..
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
        // xml 구성 관련 부분
        var  bg_value : String = currentbg.bg
        var float_bg = bg_value.toFloat()
        var int_bg = float_bg.toInt()
        binding.screenBg.text = int_bg.toString()

        val direction = "${currentbg.arrow} ${currentbg.delta}"
        binding.screenDirection.text = direction
        binding.screenInfo.text = info
        // 사이즈 고정. xml 파일에서 직접 textsize 지정 불가능하게함.
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

        if (isFullscreen) { hide() }

    }

    private fun setLineChartInitialization() {


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
