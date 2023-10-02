package com.example.dualviewer

class DataStore {
    // bgrefs에 필요한 데이터를 저장하는 클래스
    fun SaveCurrentBG(bgtime: String, bg: String, arrow: String, delta: String) {
        // 일부 데이터 입력이 들어올 경우 bgrefs에 저장.
        val editor = bgprefs.edit()
        editor.putString("bg", bg)
        editor.putString("bgtime", bgtime)
        editor.putString("arrow", arrow)
        editor.putString(if(delta.toInt()>0) "+$delta" else delta)
        editor.apply()
    }

    fun SaveCurrentBG(bgtime: String, bg: String, arrow: String, delta: String, iob: String, cob: String, basal: String) {
        // 전체 데이터 입력이 들어올 경우
        val editor = bgprefs.edit()
        editor.putString("bg", bg)
        editor.putString("bgtime", bgtime)
        editor.putString("arrow", arrow)
        editor.putString("delta", delta)
        editor.putString("iob", iob)
        editor.putString("cob", cob)
        editor.putString("basal", basal)
        editor.apply()
    }
    fun SaveBGHistory(bghistorydatetime: ArrayList<Long>, bghistory: ArrayList<Int>) {
        // bgrefs에 시간대를 parsing하여 저장
        var savebghistorydatetime = ""
        var savebghistory = ""
        for (i: Int in 0 until bghistorydatetime.count()) {
            savebghistorydatetime += bghistorydatetime[i].toString()
            savebghistory += bghistory[i].toString()
            if(i + 1 < bghistorydatetime.count()){
                savebghistorydatetime += ","
                savebghistory += ","
            }
        }
        val editor = bgprefs.edit()
        editor.putString("bghistorydatetime", savebghistorydatetime)
        editor.putString("bghistory", savebghistory)
        editor.apply()
    }

}

class DataLoadBG {
    // bgrefs에서 데이터를 불러오는 클래스
    lateinit var bg: String
    lateinit var bgtime: String
    lateinit var arrow: String
    lateinit var delta: String
    lateinit var iob: String
    lateinit var cob: String
    lateinit var basal: String
    fun LoadCurrentBG() {
        //저장된 혈당데이터 불러오기
        bg = bgprefs.getString("bg", "").toString();
        bgtime = bgprefs.getString("bgtime", "0").toString();
        arrow = bgprefs.getString("arrow", "").toString();
        delta = bgprefs.getString("delta", "").toString();
        iob = bgprefs.getString("iob", "").toString();
        cob = bgprefs.getString("cob", "").toString();
        basal = bgprefs.getString("basal", "").toString();
    }

}

class DataLoadBGHistory {
    lateinit var bghistorydatetime: ArrayList<Long>
    lateinit var bghistory: ArrayList<Int>
    fun LoadBGHistory() {
        //저장된 bghistory 불러오기
        bghistorydatetime = ArrayList<Long>(20)
        bghistory = ArrayList<Int>(20)

        val loadbghistorydatetime = bgprefs.getString("bghistorydatetime", "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0").toString().split(",")
        val loadbghistory = bgprefs.getString("bghistory", "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0").toString().split(",")

        for (i: Int in 0 until loadbghistorydatetime.count()) {
            bghistorydatetime.add(loadbghistorydatetime.get(i).toLong())
            bghistory.add(loadbghistory.get(i).toInt())
        }

    }
}