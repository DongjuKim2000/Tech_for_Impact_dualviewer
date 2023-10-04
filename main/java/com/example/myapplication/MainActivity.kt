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
        bgData.getAllBG()

        //BGData에서 가져온 데이터를 사용하여 TextView에 표시하고자 함
        val displayTextView: TextView = findViewById(R.id.display_textview)

        //BGInfo에서 가져온 BG 데이터를 화면에 표시
        val bgInfo = bgData.BGInfo()
        displayTextView.text = bgInfo.bginfo?.bg ?: "No BG data available"
    }
}
