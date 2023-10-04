package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.util.Log

object SharedPreferencesUtil {
    private const val PREF_NAME = "BG_db"
    private const val KEY_USERS = "BGdata"
    private val gson = Gson()
    fun saveBGDatas(context: Context, BGdatas: List<BG>) { //여러개의 BGData를 저장
        val json = gson.toJson(BGdatas)
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_USERS, json).apply()
    }

    fun addBGData(context: Context, BGData: BG) { //하나의 BGData를 저장
        val existingUsers = getBGDatas(context).toMutableList()
        existingUsers.add(BGData)
        saveBGDatas(context, existingUsers)
    }

    fun getBGDatas(context: Context): List<BG> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(KEY_USERS, null)
        val type = object : TypeToken<List<BG>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
    fun getLatestBGData(context: Context): BG? {
        val datas = getBGDatas(context)
        Log.d("prefUtil", datas.toString())
        return try {
            datas.last()
        } catch (e: NoSuchElementException) {
            // 리스트가 비어있을 경우
            null
        }
    }
}