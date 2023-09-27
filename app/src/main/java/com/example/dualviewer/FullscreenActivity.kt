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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //log.d("FullscreenActivity","ornCreate 시작")
        prefs = getSharedPreferences("root_preferences", Context.MODE_PRIVATE)
        bgprefs = getSharedPreferences("prefs_bghistory", MODE_PRIVATE)

        //타이머
        WorkManager.getInstance(this).cancelAllWork()
        WorkManager.getInstance(this).enqueue(OneTimeWorkRequest.Builder(TimeWorker::class.java).build())

        //노티
        val pref_enablenoti = prefs.getBoolean ("enablenoti", false)  //원래는 false
        if (pref_enablenoti) {
            NotificationService.startService(applicationContext,"NightViewer Notification")
        }
        else {
            NotificationService.stopService(applicationContext)
        }

        //화면선택
        val pref_layout = prefs.getString ("pref_layout", "1").toString()
        when (pref_layout)
        {
            "1" -> {val i = Intent(this, FullscreenActivity1::class.java)
                startActivity(i)
                //log.d("FullscreenActivity","스타트 activity1")
            }
            "2" -> {val i = Intent(this, FullscreenActivity2::class.java)
                startActivity(i)
                //log.d("FullscreenActivity","스타트 activity2")
            }
            "3" -> {val i = Intent(this, FullscreenActivity3::class.java)
                startActivity(i)
                //log.d("FullscreenActivity","스타트 activity3")
            }
            "4" -> {val i = Intent(this, FullscreenActivity4::class.java)
                startActivity(i)
                //log.d("FullscreenActivity","스타트 activity4")
            }
        }

        //배터리최적화비활성
        val intent = Intent(this, NotificationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(intent)
        } else {
            this.startService(intent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            val packageName = packageName
            // 메모리 최적화가 되어 있다면, 풀기 위해 설정 화면 띄움.
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent()
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, 0)
            }
        }
        //메인액티비티종료
        finish()
    }
}
