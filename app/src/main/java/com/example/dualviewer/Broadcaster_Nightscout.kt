package com.example.dualviewer
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import kotlin.math.log


class Broadcaster_Nightscout : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        if(intent.getAction().equals(Intents.ACTION_NEW_BG_ESTIMATE)){

            prefs = context.getSharedPreferences("root_preferences", Context.MODE_PRIVATE)
            bgprefs = context.getSharedPreferences("prefs_bghistory", AppCompatActivity.MODE_PRIVATE)

            val timestamp : Long = intent.getLongExtra(Intents.EXTRA_TIMESTAMP, 0)
            val timestampround : Long = timestamp - (timestamp % 60000)
            val bg : Double = intent.getDoubleExtra(Intents.EXTRA_BG_ESTIMATE, 0.0)
            val bgslope : Double = intent.getDoubleExtra(Intents.EXTRA_BG_SLOPE, 0.0) * 1000 * 60 * 5
            val slopename : String? = intent.getStringExtra(Intents.EXTRA_BG_SLOPE_NAME)
            var arrow = ""
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val currenttime : Long = System.currentTimeMillis()
            val currenttimedisplay : String = sdf.format(currenttime)
            val timestampdisplay: String = sdf.format(timestamp)

            val logstr = "B : ${currenttimedisplay} ${timestampdisplay} ${bg.toInt()} ${bgslope.toInt()} ${slopename}"
            //log.d("브로드캐스트" , logstr)
            //writeTextFile(logstr)

            when (slopename) {
                "DoubleUp" -> arrow = "⇈"
                "SingleUp" -> arrow = "↑︎"
                "FortyFiveUp" -> arrow = "↗︎"
                "Flat" -> arrow = "→︎"
                "FortyFiveDown" -> arrow = "↘︎"
                "SingleDown" -> arrow = "↓︎"
                "DoubleDown" -> arrow = "⇊︎"
            }

            val currentbg = BgClass()
            currentbg.LoadCurrentBG()

            if (timestampround > currentbg.bgtime.toLong())
            {
                if (bg.toInt() <= 39) {
                    //currentBG
                    BgClass().SaveCurrentBG(timestampround.toString(), "ERR", "", "")
                }
                else {
                    //currentBG
                    BgClass().SaveCurrentBG(timestampround.toString(), bg.toInt().toString(), arrow, bgslope.toInt().toString())

                    //BgHistory
                    val bgh = BgHistoryClass()
                    bgh.LoadBGHistory()
                    var min_j : Int = 0;
                    var min_value : Long = bgh.bghistorydatetime[0]
                    var bool_exist : Boolean = false
                    for (j: Int in 0 until bgh.bghistorydatetime.count()) {
                        if (bgh.bghistorydatetime[j] == timestampround) {
                            bool_exist = true
                            break
                        }
                        if (bgh.bghistorydatetime[j] < min_value) {
                            min_value = bgh.bghistorydatetime[j]
                            min_j = j
                        }
                    }
                    if (!bool_exist)
                    {
                        bgh.bghistorydatetime[min_j] = timestampround
                        bgh.bghistory[min_j] = bg.toInt()
                    }

                    BgHistoryClass().SaveBGHistory(bgh.bghistorydatetime, bgh.bghistory)
                }

                //showinfo
                try {
                    val intent = Intent()
                    intent.setAction("showinfo")
                    //intent.putExtra("value", 0)
                    context.sendBroadcast(intent)
                }
                catch (e: Exception){
                    //log.d("브로드캐스트","showinfo 에러")
                }

                //noti
                UpdateNotification(context)

            }

        }

    }

    private fun UpdateNotification(context: Context) {

        val pref_enablenoti = prefs.getBoolean ("enablenoti", false)
        if (!pref_enablenoti) { return }

        //텍스트뷰
        val currentbg = BgClass()
        currentbg.LoadCurrentBG()

        val currentTime : Long = System.currentTimeMillis() // ms로 반환
        val bgTime: Long = currentbg.bgtime.toLong()
        val minago_long = currentTime - bgTime
        val mins: Long = minago_long / (1000 * 60)
        var bglevel : Int
        try { bglevel = currentbg.bg.toInt() }
        catch (e: Exception){ bglevel = -1 }

        val notification = NotificationCompat.Builder(context, channelId)

        if (mins >= 16) {
            notification
                .setContentTitle("BG Readings Missed")
                .setContentText("${mins}분 전   |   ${currentbg.bg}  ${currentbg.delta}  ${currentbg.arrow}")
                .setSmallIcon(R.drawable.bg_old)
                .setSilent(true)
                .build()
        }
        else
        {
            notification
                .setContentTitle("${currentbg.bg}  ${currentbg.delta}  ${currentbg.arrow}")
                .setContentText("${mins}분 전")
                .setSmallIcon(getBGLevelIcon(bglevel, mins.toInt()))
                .setSilent(true)
                .build()
        }

        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification.build())

    }

    private fun getBGLevelIcon(level: Int, timeago: Int): Int {

        return if (timeago >= 16) {
            R.drawable.bg_old
        } else if (level < 0) {
            R.drawable.bg_old
        } else if (level in 40..399) {
            R.drawable.bg_040 + level - 40
        } else if (level <= 39) {
            R.drawable.bg_low
        } else if (level >= 400) {
            R.drawable.bg_high
        } else {
            R.drawable.bg_old
        }
    }
}