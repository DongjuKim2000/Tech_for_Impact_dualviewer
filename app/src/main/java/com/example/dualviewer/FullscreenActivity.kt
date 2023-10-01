package com.nightscout.nightviewer
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager


var ChartValueDateTime = ArrayList<Long>()
var ChartValue = ArrayList<Int>()
var lastrequestdatatime : Long = 0
var lastrequestalldatatime : Long = 0
lateinit var prefs: SharedPreferences
lateinit var bgprefs: SharedPreferences

class FullscreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) { // 초기화. 화면 초기 상태
        super.onCreate(savedInstanceState)
        Log.d("FullscreenActivity","onCreate 시작")
        prefs = getSharedPreferences("root_preferences", Context.MODE_PRIVATE)
        bgprefs = getSharedPreferences("prefs_bghistory", MODE_PRIVATE)

        //타이머
        WorkManager.getInstance(this).cancelAllWork() // this: 현재클래스
        WorkManager.getInstance(this).enqueue(OneTimeWorkRequest.Builder(TimeWorker::class.java).build())
        Log.d("FullscreenActivity","timerset 완료")
        //노티
        val pref_enablenoti = prefs.getBoolean ("enablenoti", true)  //원래는 false
        if (pref_enablenoti) {
            Log.d("FullscreenActivity","pref true")
            NotificationService.startService(applicationContext,"NightViewer Notification")

        } // applicationContext는 앱 실행 중 모든 컴포넌트 데이터 공유 가능
        else {
            NotificationService.stopService(applicationContext)
        }
        Log.d("FullscreenActivity","화면선택이전 ")
        //화면선택
        val pref_layout = prefs.getString ("pref_layout", "1").toString()
        when (pref_layout)
        {
            "1" -> {val i = Intent(this, FullscreenActivity1::class.java)
                startActivity(i)
                Log.d("FullscreenActivity","스타트 activity1")
            }

        }


        //메인액티비티종료
        finish()
    }
}
