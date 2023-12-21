package com.kcube.cubite.com.kcube.cubite

import android.content.Context
import android.media.audiofx.DynamicsProcessing.Limiter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.kcube.cubite.R
import com.kcube.cubite.activities.PostSnackActivity
import com.kcube.cubite.activities.SnackSpinner
import com.kcube.cubite.activities.increaseordecrease
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SnackIncrementAdapter(
    private val item: ArrayList<increaseordecrease>,
    private val context : Context,
//    private val totalLimit: Int
):
        RecyclerView.Adapter<SnackIncrementAdapter.ViewHolder>(){

    var isButtonClicked: Boolean = false
        private set

    private val db = FirebaseFirestore.getInstance()
    private val publishSnacksMasterBox = db.collection("publishSnacksMasterBox")
    private var currentCount: Int = 0

    private val  calendar = Calendar.getInstance()
    private val counts = HashMap<increaseordecrease, Int>()
    private val limitList = mutableListOf<String>()
    private val listOfSnacks = mutableListOf<List<String>>()
//    private  var currentItem : increaseordecrease ?= null


    @Suppress("DEPRECATION")
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
                var mTitle : TextView = itemView.findViewById(R.id.mTitle)
                var plushButton :ImageView = itemView.findViewById(R.id.plushButton)
                var quantityTextView :TextView = itemView.findViewById(R.id.quantityTextView)
                var minusButton :ImageView = itemView.findViewById(R.id.minusButton)
                init {
                    addingSnacksCategory()
                    itemView.setOnClickListener {
                        // Handle item click

                        (context as PostSnackActivity).onItemClicked()
                    }

                    plushButton.setOnClickListener {
                        isButtonClicked = true
                        notifyDataSetChanged()
                        val position = adapterPosition
                        val currentSnackItem = item[position]
                        currentCount = currentSnackItem.Snackcount
                        val snacksList = item
                        val sumOfCounts = snacksList.sumBy { it.Snackcount }
                        val firstSnack = listOfSnacks[0]
                        val secondSnack = listOfSnacks[1]
                        val firstElement = limitList[0]
                        val secondElement = limitList[1]
                        // In your adapter class
                        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                        val editor = prefs.edit()
                        // Store values in SharedPreferences
                        editor.putInt("sumOfCounts", sumOfCounts)
                        editor.putString("firstElement", firstElement)
                        editor.putString("secondElement", secondElement)
                        editor.apply()

                        if (limitList.isNotEmpty() && currentSnackItem.snacks in firstSnack){
                            if (sumOfCounts <=  limitList[0].toInt() - 1 ){
                                currentSnackItem.Snackcount++
                                quantityTextView.text = currentSnackItem.Snackcount.toString()
                            }
                            else{
                                Toast.makeText(context, "maximum you can add $firstElement ", Toast.LENGTH_SHORT).show()
                            }

                        }  else if (listOfSnacks.size > 1 && currentSnackItem.snacks in secondSnack) {
                            if(sumOfCounts <= limitList[1].toInt() - 1) {
                                currentSnackItem.Snackcount++
                                quantityTextView.text = currentSnackItem.Snackcount.toString()
                            }
                            else{
                                Toast.makeText(context, "maximum you can add $secondElement ", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                        minusButton.setOnClickListener {
                                val position = adapterPosition
                                if (position != RecyclerView.NO_POSITION) {
                                        val currentItem = item[position]
                                        if (currentItem.Snackcount > 0) {
                                                currentItem.Snackcount--
                                                quantityTextView.text = currentItem.Snackcount.toString()
                                        }
                                }
                        }
                }

        private fun addingSnacksCategory() {

            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            publishSnacksMasterBox.whereEqualTo("publishDate",currentDate)
                .get()
                .addOnSuccessListener { dataresult ->
                    if (dataresult.size() > 0) {
                        limitList.clear()
                        listOfSnacks.clear()
//                        val limitList = mutableListOf<String>()
//                        val listOfSnacks = mutableListOf<List<String>>()
                        for (item in dataresult) {
                            var limit = item.get("limit") as String
                            var categories = item.get("categories") as ArrayList<String>
                            if (!limit.isNullOrEmpty() && categories != null) {
                                limitList.add(limit)
                                listOfSnacks.add(categories)
                            }
                        }

//                        val firstSnack = listOfSnacks[0]
//                        val secondSnack = listOfSnacks[1]
//
//                        var indexString = limitList[0]
//                        var index = indexString.toInt()
//                        var modifiedIndex = index - 1
//                        val secondElement = limitList[1].toInt() - 1
//                        if ( currentSnackItem.snacks in firstSnack) {
//                            if (sumOfCounts <=  modifiedIndex ){
//                                currentSnackItem.Snackcount++
//                                quantityTextView.text = currentSnackItem.Snackcount.toString()
//                            }else{
//                                Toast.makeText(context, "maximum you can add $index ", Toast.LENGTH_SHORT).show()
//                            }
//                        }else if (listOfSnacks.size > 1 && currentSnackItem.snacks in secondSnack) {
//                            var categoryTwo = limitList[1].toInt()
//                            if (sumOfCounts <= secondElement) {
//                                currentSnackItem.Snackcount++
//                                quantityTextView.text = currentSnackItem.Snackcount.toString()
//                            }else{
//                                Toast.makeText(context, "maximum you can add $categoryTwo ", Toast.LENGTH_SHORT).show()
//                            }
//                            }
                    }
                }
        }
    }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val View = LayoutInflater.from(parent.context).inflate(R.layout.addsnack_count , parent,false)
                return ViewHolder(View)
        }

        override fun getItemCount(): Int {
                return item.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val ListItem = item[position]
                holder.mTitle.text = ListItem.snacks
                holder.quantityTextView.text = ListItem.Snackcount.toString()
        }
}