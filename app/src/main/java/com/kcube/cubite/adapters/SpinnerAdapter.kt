package com.kcube.cubite.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.kcube.cubite.R
import com.kcube.cubite.activities.SnackSpinner

class SpinnerAdapter(private val context: Context,
                     private val items: MutableList<SnackSpinner>):BaseAdapter() {

    override fun getCount(): Int {
        //returns the count of items present in the list
        return items.size
    }

    override fun getItem(p0: Int): Any {
        return items[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view = p1 ?: LayoutInflater.from(context).inflate(R.layout.spinner_item_view, p2, false)
        val snackTitle = view.findViewById<TextView>(R.id.snackItem)
        val priceTitle = view.findViewById<TextView>(R.id.price)
        val item = items[p0]
        if(item.item == "Select Snack" || item.item == "Select Juice" || item.item == "Select Biscuit"){
            // Handle true statement
            snackTitle.text = item.item
            priceTitle.text = ""
        }else{
            // Handle if the condition is false
            snackTitle.text = item.item
            priceTitle.text = "₹ ${item.price}"
            if(item.price.toInt() > 50){
                priceTitle.setTextColor(Color.RED)
            }else{
                priceTitle.setTextColor(Color.GREEN)
            }
        }
        return view
    }

}