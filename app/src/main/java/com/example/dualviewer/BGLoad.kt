package com.example.dualviewer
import android.content.Context
import android.os.Build
import com.google.android.gms.security.ProviderInstaller
import org.json.JSONArray
import java.net.URL
class BGLoad(val context: Context) {
    // NightScout에서 BG 가져오기
    var pref_urlText = prefs.getString("ns_url", "https://{yoursite}.herokuapp.com").toString().replace(" ","")
    fun getBG() {
        //혈당 데이터 가져오기
        if (prefs.getBoolean("readfromns", false) == false)
            return

        if (pref_urlText.char == '/') pref_urlText.dropLast(1)

        val currenttime : Long = System.currentTimeMillis()

        if (currenttime - lastrequestdatatime < 10000) {
            //lastrequestdatatime은 원래 프로젝트에서 전역변수. preference에서 가져오면 좋겠음.
            //10초 이내로 다시 리퀘스트하면 무시
            return
        }

        lastrequestdatatime = currenttime

        LoadFromURL.start()

    }

    fun getAllBG() {

        if (pref_readfromns == false) {
            return
        }

        if (urllaststring == "/") { pref_urlText = pref_urlText.substring(0, pref_urlText.length - 1) }
        val fuctionname = "getAllBG"
        val currenttime : Long = System.currentTimeMillis()

        if (currenttime - lastrequestalldatatime < 5000) {
            //log.d(fuctionname, "getAllBG 패스")
            return
        }

        lastrequestalldatatime = currenttime

        Thread {
            val bgh = DataLoadBGHistory()
            bgh.LoadBGHistory()

            try {
                if (Build.VERSION.SDK_INT <= 19)
                    ProviderInstaller.installIfNeeded(context)
                val url = URL("${pref_urlText}/api/v1/entries.json?count=12")
                val result = URL(url.toString()).readText()

                try {
                    try {
                        val entries = JSONArray(result)

                        for (i in 0 until entries.length()) {

                            if (entries.getJSONObject(i).has("sgv")) {

                                val bg = entries.getJSONObject(i).getString("sgv")
                                var bgtime : Long = entries.getJSONObject(i).getString("date").toLong()
                                bgtime = bgtime - (bgtime % 60000)

                                bgh.bghistorydatetime[i] = bgtime
                                bgh.bghistory[i] = bg.toInt()

                            }
                        }
                    }
                    catch (e: Exception){}

                    //bghistory저장
                    DataStore().SaveBGHistory(bgh.bghistorydatetime, bgh.bghistory)

                } catch (e: Exception) {}

            } catch (e: Exception) {}
            getBG()
        }.start()

    }
}