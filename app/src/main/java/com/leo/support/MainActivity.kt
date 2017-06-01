package com.leo.support

import android.media.AsyncPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.async
import org.jetbrains.anko.find
import org.jetbrains.anko.longToast
import org.jetbrains.anko.uiThread
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity() {

    private var items = listOf<String>(
            "mon", "tue", "wed", "thurs", "fri", "sat", "sun"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val forecastList = findViewById(R.id.forecast_list) as RecyclerView
        //使用Anko
        val forecastList: RecyclerView = find(R.id.forecast_list)
        forecastList.layoutManager = LinearLayoutManager(this)
        var adapter = ForecastListAdapter(items)
        forecastList.adapter = adapter
        kotlin_hello.text = "nihao"
        kotlin_hello.setOnClickListener {
            toast("ssss")
            items = reversList(items)
            adapter.items = items
            adapter.notifyDataSetChanged()
        }

        val f1 = Forecast(Date(), 23.5f, "shiny day")
        val (date, temperature, details) = f1 //多声明
    }

    fun toast(message: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, length).show()
    }

    fun reversList(items: List<String>): List<String>  = items.reversed()


    class ForecastListAdapter(var items: List<String>) : RecyclerView.Adapter<ForecastListAdapter.ViewHolder>() {

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            holder?.textView?.text = items[position]
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            return ViewHolder(TextView(parent?.context))
        }


        class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
    }

    open class Person {
        var name: String = ""
        get() = field.toUpperCase()
        set(value) {field = "Name: $value"}
    }

    data class Forecast(val date: Date, val temperature: Float, val details: String)

    open class Request(val url: String) {
        open fun run() {
            val forecastJsonStr = URL(url).readText()
            Log.d(javaClass.simpleName, forecastJsonStr)
        }
    }

}
