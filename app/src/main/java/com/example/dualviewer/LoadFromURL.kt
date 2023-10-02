package com.example.dualviewer

import android.os.Build
import org.json.JSONObject
import java.net.URL

class LoadFromURL: Thread() {
    public override fun run() {
        try {

            if (Build.VERSION.SDK_INT <= 19)
                ProviderInstaller.installIfNeeded(context)

            val url = URL("${pref_urlText}/api/v2/properties/bgnow,delta,direction,buckets,iob,cob,basal")
            val result = URL(url.toString()).readText()
            // url을 통해 데이터 가져오기

            try {
                // BG 가져오기
                val currentbg = DataLoadBG()
                val bgh = DataLoadBGHistory()
                currentbg.LoadCurrentBG()
                bgh.LoadBGHistory()

                // BG 구성
                val recenttimestamp: Long = currentbg.bgtime.toLong()
                val gettimestamp: String = try {
                    JSONObject(result).getJSONObject("bgnow").getString("mills")
                } catch (e: Exception) { "" }

                if (gettimestamp.toLong() > recenttimestamp) {
                    currentbg.bgtime = gettimestamp
                    currentbg.bg = try {
                         JSONObject(result).getJSONObject("bgnow").getString("last")
                    } catch (e: Exception) { "ERR" }

                    currentbg.delta = try {
                        JSONObject(result).getJSONObject("delta").getString("display")
                    } catch (e: Exception) { "" }

                    currentbg.arrow = try {
                        JSONObject(result).getJSONObject("direction").getString("label")
                    } catch (e: Exception) { "" }
                }
                // 선택적 정보 가져오기
                if (prefs.getBoolean ("iob_enable", false))
                    currentbg.iob = try {
                        JSONObject(result).getJSONObject("iob").getString("display")
                    } catch (e: Exception) { "" }
                if (prefs.getBoolean ("cob_enable", false))
                    currentbg.cob = try {
                        JSONObject(result).getJSONObject("cob").getString("display")
                    } catch (e: Exception) { "" }
                if (prefs.getBoolean ("basal_enable", false))
                    currentbg.basal = try {
                        JSONObject(result).getJSONObject("basal").getString("display")
                    } catch (e: Exception) { "" }

                //BG저장
                BGLoad().SaveCurrentBG(
                    currentbg.bgtime,
                    currentbg.bg,
                    currentbg.arrow,
                    currentbg.delta,
                    currentbg.iob,
                    currentbg.cob,
                    currentbg.basal
                )

                // BGHistory 구성
                try {
                    val buckets = JSONObject(result).getJSONArray("buckets")
                    for (i in 0 until buckets.length()) {

                        if (buckets.getJSONObject(i).has("last")) {

                            val bg = buckets.getJSONObject(i).getString("last")
                            var bgtime: Long = buckets.getJSONObject(i).getString("mills").toLong()
                            bgtime = bgtime - (bgtime % 60000)

                            val minbgh = bgh.maxByOrNull{ it.bghistorydatetime }
                            var min_value: Long = bgh.bghistorydatetime[0]
                            var bool_exist: Boolean = false
                            for (j: Int in 0 until bgh.bghistorydatetime.count()) {
                                if (bgh.bghistorydatetime[j] < min_value) {
                                    min_value = bgh.bghistorydatetime[j]
                                    min_j = j
                                }
                            }

                            if (!bgh.bghistorydatetime.contains(bgtime)) {
                                bgh.bghistorydatetime[min_j] = bgtime
                                bgh.bghistory[min_j] = bg.toInt()
                            }
                        }
                    }

                    //BGHistory저장
                    DataStore().SaveBGHistory(bgh.bghistorydatetime, bgh.bghistory)

                } catch (e: Exception) {}

            } catch (e: Exception) {}

        } catch (e: Exception) {}
    }
}