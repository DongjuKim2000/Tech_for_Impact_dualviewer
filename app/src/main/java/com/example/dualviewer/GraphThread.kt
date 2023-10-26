package com.example.dualviewer

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.example.dualviewer.ui.theme.Purple80
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.lang.ProcessBuilder.Redirect
import java.util.ArrayList

class GraphThread(private val lineChart: LineChart, private val context: Context): Thread() {
    override fun run() {
//        val input = Array<Double>(25,{400*Math.random()}) //랜덤 데이터
        val input = SharedPreferencesUtil.getBGDatas(context) //bg 데이터 리스트 가져오기
        // Entry 배열 생성
        var entries: ArrayList<Entry> = ArrayList()
        // Entry 배열 초기값 입력
        entries.add(Entry(0F , 0F))
        // 그래프 구현을 위한 LineDataSet 생성
        var dataset: LineDataSet = LineDataSet(entries, "input")

        chartSetting(dataset) //그래프 모양 설정하는 함수, 아래에 정의 있음

        // 그래프 data 생성 -> 최종 입력 데이터
        var data: LineData = LineData(dataset)
        // xml에 배치된 lineChart에 데이터 연결
        lineChart.data = data

        runOnUiThread {
            // 그래프 생성
            lineChart.animateXY(1, 1)
        }

        for (i in 0 until input.size){

//            SystemClock.sleep(1000)
//            data.addEntry(Entry(i.toFloat(), input[i].toFloat()), 0) //랜덤 데이터
            data.addEntry(Entry(i.toFloat() , input[i].bg.toFloat()), 0) //bg 데이터
            data.notifyDataChanged()
            lineChart.notifyDataSetChanged()
            lineChart.invalidate()
        }

    }

    private fun runOnUiThread(action: () -> Unit) {
        Handler(Looper.getMainLooper()).post(action)
    }

    private fun chartSetting(dataset: LineDataSet){
        val xAxis = lineChart.xAxis
        val yAxisLeft = lineChart.axisLeft
        val yAxisRight = lineChart.axisRight

        dataset.apply {
            color = R.color.black //그래프 선 색
            lineWidth = 2f //그래프 선 두께
            valueTextSize = 0F //값 출력 안되도록
            setDrawCircles(false) //그래프 점에 동그라미 없앰
        }

        lineChart.apply {
            //오른쪽 y축 안보이게
            axisRight.isEnabled = false
            xAxis.isEnabled = false
            setTouchEnabled(false)
            legend.isEnabled = false //라벨 없애기
            description.isEnabled = false //설명 없애기
        }

//        xAxis.apply {
//            //x축 그래프 아래에 표시
//            position = XAxis.XAxisPosition.BOTTOM
//            valueFormatter = IndexAxisValueFormatter(List_localDatestr)
//        }

        //주의 혈당 기준선
        val lowLimit = LimitLine(70.toFloat(), 70.toString()).apply {
            lineWidth = 1f
            lineColor = Color.YELLOW
        }
        val highLimit = LimitLine(180.toFloat(), 180.toString()).apply {
            lineWidth = 1f
            lineColor = Color.YELLOW
        }

        //위험 혈당 기준선
        val urgentLow = LimitLine(55.toFloat(), 55.toString()).apply {
            lineWidth = 1f
            lineColor = Color.RED
        }
        val urgentHigh = LimitLine(260.toFloat(), 260.toString()).apply {
            lineWidth = 1f
            lineColor = Color.RED
        }

        yAxisLeft.apply {
            axisMaximum = 400.toFloat() //y축 최댓값
            axisMinimum = 40.toFloat() //y축 최솟값
            setDrawLabels(false) //레이블 비활성화
            setDrawAxisLine(false) //축 비활성화
            setDrawGridLines(false) //그리드 비활성화
            //기준 선들 그리기
            removeAllLimitLines()
            addLimitLine(lowLimit)
            addLimitLine(highLimit)
            addLimitLine(urgentLow)
            addLimitLine(urgentHigh)
        }

    }
}