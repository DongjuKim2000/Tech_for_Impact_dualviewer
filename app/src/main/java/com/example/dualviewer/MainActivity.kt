package com.example.dualviewer

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.github.mikephil.charting.charts.LineChart

lateinit var prefs: SharedPreferences
lateinit var BG_db: SharedPreferences
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val bgData = BGData(this)
//        bgData.initializeBG_db()
//        bgData.get_EntireBGInfo()
//
//        //BGData에서 가져온 데이터를 사용하여 TextView에 표시하고자 함
//        val displayTextView: TextView = findViewById(R.id.display_textview)
//
//        //BGInfo에서 가져온 BG 데이터를 화면에 표시
//        val bgInfo = bgData.BGInfo()
//        displayTextView.text = bgInfo.bginfo?.bg ?: "No BG data available"


        //그래프 표시
        val lineChart: LineChart = findViewById(R.id.lineChart)
        val thread = GraphThread(lineChart, baseContext)
        thread.start()
    }
    override fun onResume() {
        super.onResume()
        val displayTextView: TextView = findViewById(R.id.display_textview)

        //BGInfo에서 가져온 BG 데이터를 화면에 표시
        val bgData = BGData(this)
        val bgInfo = bgData.BGInfo()
        displayTextView.text = bgInfo.bginfo?.bg ?: "No BG data available"
    }
}
