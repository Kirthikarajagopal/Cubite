package com.kcube.cubite.com.kcube.snack_management.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.kcube.cubite.R
import com.kcube.cubite.activities.SnackSpinner

class FilterableDropDownAdapter(private val context: Context,
                                private val items: MutableList<SnackSpinner>):BaseAdapter() , Filterable {

    var originalList:MutableList<SnackSpinner> = items;
    override fun getCount(): Int {
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
        snackTitle.text = item.item
        priceTitle.text = "₹ ${item.price}"
        return view
    }

    override fun getFilter(): Filter {
       return object :Filter(){
           override fun performFiltering(p0: CharSequence?): FilterResults {
               val filteredResults = FilterResults()
               val filteredList = mutableListOf<SnackSpinner>()
               p0?.let { query ->
                   for (item in items) {
                       if (item.item.contains(query, true)) {
                           filteredList.add(item)
                       }
                   }
               }
               filteredResults.values = filteredList
               filteredResults.count = filteredList.size
               return filteredResults
           }

           override fun publishResults(p0: CharSequence?, results: FilterResults?) {
               // val filteredResults = mutableListOf<SnackSpinner>()
               if (results != null && results.count > 0) {
                   items.clear()
                   items.addAll(results.values as List<SnackSpinner>)
                   notifyDataSetChanged()
               } else {
                   items.clear()
                   items.addAll(originalList) // Assuming originalData is the source list
                   notifyDataSetChanged()
               }
           }

       }
    }
}