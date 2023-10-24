package com.example.myapplication

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
lateinit var prefs: SharedPreferences
lateinit var BG_db: SharedPreferences
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bgData = BGData(this)
        bgData.initializeBG_db()
        bgData.get_EntireBGInfo()

        //BGData에서 가져온 데이터를 사용하여 TextView에 표시하고자 함
        val displayTextView: TextView = findViewById(R.id.display_textview)

        //BGInfo에서 가져온 BG 데이터를 화면에 표시
        val current_bgInfo = listToString(bgData.get_Recent10BGValues(), ",")
        Log.d("main", current_bgInfo)
        displayTextView.text = current_bgInfo
    }
    override fun onResume() {
        super.onResume()
        val displayTextView: TextView = findViewById(R.id.display_textview)

        //BGInfo에서 가져온 BG 데이터를 화면에 표시
        val bgData = BGData(this)
        val bgInfo = bgData.BGInfo()
        val current_bgInfo = listToString(bgData.get_Recent10BGValues(), ",")
        Log.d("main", current_bgInfo)
        displayTextView.text = current_bgInfo
    }
}
