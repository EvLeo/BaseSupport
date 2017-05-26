package com.leo.support

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var items = listOf<String>(
            "mon", "tue", "wed", "thurs", "fri", "sat", "sun"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val forecastList = findViewById(R.id.forecast_list) as RecyclerView
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

}
