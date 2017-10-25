package com.it.pulltozoomlistview

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView

class MainActivity : AppCompatActivity() {

    private var lv: PullToZoomListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()
        //以下代码用于去除阴影
        if (Build.VERSION.SDK_INT >= 21) {
            supportActionBar!!.elevation = 0f
        }
        lv = findViewById(R.id.main_lv) as PullToZoomListView?
        val header = View.inflate(this, R.layout.layout_lv_header, null)
        lv!!.addHeaderView(header)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayOf("腾讯", "阿里巴巴", "百度", "新浪", "c语言", "java", "php", "FaceBook", "Twiter", "xml", "html"))
        lv!!.adapter = adapter
        val iv = header.findViewById<ImageView>(R.id.header_img)
        lv!!.setPullToZoomListView(iv)
    }

    // 当界面显示出来的时候回调
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            lv!!.setViewBounds()
        }
    }
}
