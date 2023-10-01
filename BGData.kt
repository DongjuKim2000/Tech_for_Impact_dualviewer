package com.example.myapplication


import android.util.Log
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
class Testt(){
    //User_Prefs : 기본 Preferences (iob, cob, basal enable 등)
    //BG_db: 혈당 데이터베이스 (Preferences로 구현 sql로도 가능할듯)
    fun testMain() {
        Log.d("시작!!", "시작!!!!!!")
        Thread{
            val pref_urlText = "https://pkd7320591.my.nightscoutpro.com"
            val currentbg = get_BGInfoFromURL(pref_urlText)
            currentbg.LogCurrentData()
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

    class BGInfo {
        var bg: String // 혈당값
        var time: String //측정된 시간
        var arrow: String //혈당 화살표
        var delta: String //??
        var iob: String //Insulin on Board
        var cob: String //??
        var basal: String //Basal
        constructor(){ //현재 저장된 BGINFO 불러오기
            bg = BG_db.getString("bg", "").toString();
            time = BG_db.getString("bgtime", "0").toString();
            arrow = BG_db.getString("arrow", "").toString();
            delta = BG_db.getString("delta", "").toString();
            iob = BG_db.getString("iob", "").toString();
            cob = BG_db.getString("cob", "").toString();
            basal = BG_db.getString("basal", "").toString();
        }
        constructor(bg: String, time: String, arrow: String, delta: String, iob: String, cob: String, basal: String){
            this.bg = bg
            this.time = time
            this.arrow = arrow
            this.delta = delta
            this.iob = iob
            this.cob = cob
            this.basal = basal
        }
        fun saveCurrentBG(){ //이 BGInfo 객채를 현재 BG로 저장핮기
            val editor = BG_db.edit()
            editor.putString("bg", bg)
            editor.putString("bgtime", time)
            editor.putString("arrow", arrow)
            editor.putString("delta", delta)
            editor.putString("iob", iob)
            editor.putString("cob", cob)
            editor.putString("basal", basal)
            editor.apply()
        }
        fun LogCurrentData(){
            Log.d("Log", "bg: $bg time: $time arrow: $arrow delta: $delta iob: $iob cob: $cob basal: $basal")
        }
    }
}
