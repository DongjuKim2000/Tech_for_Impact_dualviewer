package com.example.myapplication


import android.util.Log
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
class BGData(private val context: Context){

    //User_Prefs : 기본 Preferences (iob, cob, basal enable 등)
    //BG_db: 혈당 데이터베이스 (Preferences로 구현 sql로도 가능할듯)
    fun getAllBG() {
        Log.d("시작!!", "시작!!!!!!")
        val pref_urlText = "https://pkd7320591.my.nightscoutpro.com"
        Thread{
            val currentbg = get_BGInfoFromURL(pref_urlText)
            currentbg.LogCurrentData()
            currentbg.saveCurrentBG()
        }.start()

    }
    fun get_BGInfoFromURL(urlText:String): BGInfo{ //URL을 받아 BGInfo class를 리턴
        val currenttime: Long = System.currentTimeMillis()
        val url = URL("${urlText}/api/v2/properties/bgnow,delta,direction,buckets,iob,cob,basal")
        val result = URL(url.toString()).readText()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currenttimedisplay : String = sdf.format(currenttime)
        //추후 try except 기능 추가 필요
        val bg = JSONObject(result).getJSONObject("bgnow").getString("last")
        val time = currenttimedisplay
        val delta = JSONObject(result).getJSONObject("delta").getString("display")
        val arrow = JSONObject(result).getJSONObject("direction").getString("label")
        val iob = JSONObject(result).getJSONObject("iob").getString("display")
        val cob = JSONObject(result).getJSONObject("cob").getString("display")
        val basal = JSONObject(result).getJSONObject("basal").getString("display")
        return BGInfo(bg, time, arrow, delta, iob, cob, basal)
    }

    inner class BGInfo {
        var bginfo: BG?
        constructor(){ //현재 저장된 BGINFO 불러오기
            bginfo = SharedPreferencesUtil.getLatestBGData(context)
            if (bginfo == null) {
                //처리 필요
            }
        }
        constructor(bg: String, time: String, arrow: String, delta: String, iob: String, cob: String, basal: String){
            this.bginfo = BG(bg,time,arrow,delta,iob,cob,basal)
        }
        fun saveCurrentBG(){ //현재 BG 저장하기
            bginfo?.let { SharedPreferencesUtil.addBGData(context, it) }
        }
        fun LogCurrentData(){ //받아온 데이터 Log
            Log.d("BGData.kt", this.bginfo.toString())
        }
    }


}
