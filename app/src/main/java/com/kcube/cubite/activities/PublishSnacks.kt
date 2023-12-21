package com.kcube.cubite.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.kcube.cubite.R
import com.kcube.cubite.adapters.SpinnerAdapter
import com.kcube.cubite.com.kcube.cubite.SnacksAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Suppress("DEPRECATION")
class PublishSnacks : AppCompatActivity() {

    private val context: Context = this

    private var snackCount: Int = 0
    private lateinit var snackAdapter: SnacksAdapter
    private val snacksList = mutableListOf<SnackSpinner>()
    private val selectedSnacks = mutableSetOf<String>()
    private val db = FirebaseFirestore.getInstance()
    private val published_snacks_collection = db.collection("published_snacks")
    private val posted_snacks_collection = db.collection("posted_snacks_employees")
    private val SnacksMasterBox = db.collection("SnacksMasterBox")
    private val publishSnacksMasterBox = db.collection("publishSnacksMasterBox")
    private val Snacks = db.collection("snacks_list")
    private val snacksbox_Collection = db.collection("snacksbox")
    private lateinit var snacksDropdown: Spinner
    private lateinit var publishButton: Button
    private lateinit var logoutImage: ImageView
    private lateinit var snacksRecyclerView: RecyclerView
    private lateinit var noPublishedSnacks:TextView
    private var publishedSnacksList = mutableListOf<SnackSpinner>()
    private lateinit var progressDialog: AlertDialog
    private lateinit var sharedPreferences: SharedPreferences
    private val  calendar = Calendar.getInstance()
    var snackItem: String = ""
    var snacksNotInList = ""

    @SuppressLint("SuspiciousIndentation", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publish_snacks)

        supportActionBar?.hide()

        logoutImage = findViewById(R.id.uilogoutImage)
        snacksDropdown = findViewById(R.id.snacksDropdown)
        publishButton = findViewById(R.id.publishButton)
        noPublishedSnacks = findViewById(R.id.noPublishedSnacks)
        snacksRecyclerView = findViewById(R.id.ListView)


        sharedPreferences = getSharedPreferences("Kcube_User", Context.MODE_PRIVATE)
        val userType = sharedPreferences.getInt("user_type", 0)

        if (userType == 4){
            snacksDropdown.visibility = View.GONE
            publishButton.visibility = View.GONE

            calendar.add(Calendar.DAY_OF_YEAR,-1)
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
             val yesterdayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            // Here Filter published_snacks collection publish_date field equals to  yesterday
             val publishSnackDocument = publishSnacksMasterBox.whereEqualTo("publishDate", currentDate)
                publishSnackDocument.get()
                .addOnSuccessListener { documentSnapshot ->
                    // Handle SuccessListener process
                    if (documentSnapshot.size() > 0){
                       val snackCardList = mutableListOf<SnackCardModal>()
                        for (data in documentSnapshot) {

                            // fetch snack and price from published_snacks firestore collection
                            val snack = data.get("snack") as String
                            val price = data.get("price") as String
                            snackCardList.add(snack, price)
                            // set to the adapter
                            snacksRecyclerView.layoutManager = LinearLayoutManager(this)
                            snackAdapter = SnacksAdapter(publishedSnacksList, deleteItem = true, deleteButton = true, context)
                            snacksRecyclerView.adapter = snackAdapter
                            snackAdapter.notifyDataSetChanged()
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Handel Failure exception
                    Toast.makeText(baseContext, "Failed to fetch published snacks: $exception", Toast.LENGTH_SHORT).show()
                }
        }

        val dialogView =
            LayoutInflater.from(applicationContext).inflate(R.layout.progress_dialog_view, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(false) // Make the dialog non-cancelable
        progressDialog = builder.create()

        logoutImage.setOnClickListener {

            logoutFunction()
        }

//        val doc = publishSnacksMasterBox.document("o5hXyZdbUflenvr4UOr2").delete().addOnSuccessListener {  }
//        val document = publishSnacksMasterBox.document("mhnEJBUYX58un1MAcM22").delete().addOnSuccessListener {  }
//        val docref = publishSnacksMasterBox.document("r1tZYpNBSLYpyizyGFzT").delete().addOnSuccessListener {  }
        snacksDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // Handle  snacksDropdown select Item from dropdown
            override fun onItemSelected(
                parent: AdapterView<*>?,
                p1: View?,
                position: Int,
                p3: Long
            ) {
                val selectedSnack = parent?.getItemAtPosition(position) as? SnackSpinner
                if (selectedSnack != null && selectedSnack.item != "Select Snack") {
                     var fieldExistsInAnyItem = false
                    for (item in publishedSnacksList) {
                        snackItem = selectedSnack.item
                        if (item.item == selectedSnack.item) {
                            fieldExistsInAnyItem = true
                        }
                    }
                    if (fieldExistsInAnyItem) {
                        // Handle fieldExistsInAnyItem true
                        Toast.makeText(
                            applicationContext,
                            "${selectedSnack.item} already selected",
                            Toast.LENGTH_SHORT
                        ).show()
                        snacksDropdown.setSelection(0)
                    } else {
                        // Handle fieldExistsInAnyItem false
                        publishedSnacksList.add(selectedSnack)
                        snacksRecyclerView.adapter = snackAdapter
                        snackAdapter.notifyDataSetChanged()
                        snacksDropdown.setSelection(0)
                        noPublishedSnacks.visibility = View.GONE
                        snacksRecyclerView.visibility = View.VISIBLE
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        // set to the adapter
        snacksRecyclerView.layoutManager = LinearLayoutManager(this)
        snackAdapter = SnacksAdapter(publishedSnacksList, deleteItem = true,deleteButton = false, context)

//        readPublishSnacksMasterBox()
        readSnacksBoxList()
        readPublishSnacksMasterBox()
        publishButton.setOnClickListener {
            if (snacksList.isNotEmpty()) {
                // Call the function to add the selected snacks data to Firestore
                showPublishConfirmationDialog()
            } else {
                // Handle if it's not empty
                Toast.makeText(baseContext, "No snacks selected to publish", Toast.LENGTH_SHORT).show()
            }
        }
//        progressDialog.show()
    }

    private fun logoutFunction() {
        // Create an AlertDialog.Builder
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Logout")
        alertDialogBuilder.setMessage("Are you sure you want to log out?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->

            // Handle logout logic here
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.clear()
            editor.putBoolean("loginStatus", false)
            editor.apply()

            // Navigate back to the login screen
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Finish the MainActivity so the user cannot navigate back without logging in
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            // Dismiss the dialog if "No" is clicked
            dialog.dismiss()
        }

        // Create and show the AlertDialog
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showPublishConfirmationDialog() {

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setMessage("Are you sure you want to publish snacks?")
        alertDialogBuilder.setPositiveButton("PUBLISH") { _, _ ->
            // Handle the "Add" button click
            addDataList()

        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            // Handle the "Cancel" button click or dismiss the dialog
            dialog.dismiss()


        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun readPublishedSnacks() {
        calendar.add(Calendar.DAY_OF_YEAR,-1)
        val yesterdayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Fetch the previously published snacks from the Firestore document
        published_snacks_collection.whereEqualTo("publish_date", yesterdayDate).get()
            .addOnSuccessListener { dataResults ->
                if (dataResults.size() > 0) {
                    // Handle dataResults size more then 0
                    publishedSnacksList.clear()
                    for (snacks in dataResults) {
                        // Fetch snack and price firestore collection
                        val snack: String = snacks.get("snack") as String;
                        val price: Long = snacks.get("price") as Long
//                        publishedSnacksList.add(SnackSpinner(snack, price.toString()))

                    }
                    // count snack size
//                    snackCount = publishedSnacksList.size
//                    snacksRecyclerView.adapter = snackAdapter
//                    snackAdapter.notifyDataSetChanged()
                }else{
                    // Handle dataResults size not more then 0
                    snacksRecyclerView.visibility = View.INVISIBLE
                    noPublishedSnacks.visibility = View.VISIBLE
                }
                progressDialog.dismiss()
            }
            .addOnFailureListener { exception ->
                // Handle the error exception
                progressDialog.dismiss()
                Toast.makeText(
                    baseContext,
                    "Failed to fetch published snacks: $exception",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    private fun readSnacksBoxList() {
        progressDialog.show()
        SnacksMasterBox.get().addOnSuccessListener { dataResults ->
            snacksList.clear()
             var sortedList = mutableListOf<SnackSpinner>()
            for (snacks in dataResults) {
                // Fetch item and price firestore collection
                val snack = snacks.get("name") as String;
                val price = snacks.get("price") as String

                sortedList.add(SnackSpinner(snack, price))

            }
            sortedList = sortedList.sortedBy { it.item } as MutableList<SnackSpinner>
            snacksList.add(SnackSpinner("Select Snack", ""))
            // Add sortedList in snacksList
            snacksList.addAll(sortedList)

            // set up to the adapter
            var snacksAdapter = SpinnerAdapter(this, snacksList)
            snacksDropdown.adapter = snacksAdapter
            // call readPublishedSnacks function
//            readPublishedSnacks()
            progressDialog.dismiss()
        }.addOnFailureListener { exceptions ->
            // Handle Failure
            progressDialog.dismiss()

        }
    }

    private fun addDataList() {
        progressDialog.show()
        var finalisedSnacks = snackAdapter.getUpdatedSnacksList()
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        calendar.add(Calendar.DAY_OF_YEAR,-1)
        val yesterdayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        SnacksMasterBox.get()
            .addOnSuccessListener { dataResults ->
            for (document in finalisedSnacks) {
                var snackExists = false
                val itemList = mutableListOf<String>()
                for (doc in dataResults){
                    val snack = doc.get("name") as String
                    val price = doc.get("price") as String
                    val categories = doc.get("categories") as ArrayList<String>
                    val category = doc.get("category") as Boolean
                    val limit = doc.get("limit") as String
                    itemList.add(snack)
                    if (document.item == snack && document.price == price) {
                        val storedSnacksList = hashMapOf<String,Any>(
                            "snack" to snack,
                            "price" to price,
                            "categories" to categories,
                            "limit" to limit,
                            "category" to category,
                            "publishDate" to currentDate
                        )
                        // Add credential to publishSnacksMasterBox
                        val snacksDataList: MutableList<String> = mutableListOf()
                        publishSnacksMasterBox.whereEqualTo("publishDate", currentDate)
                            .get()
                            .addOnSuccessListener { QuerySnapshot ->
                                for (item in QuerySnapshot.documents) {
                                    val snacks = item.get("snack") as String
                                    snacksDataList.add(snacks)
                                }
                                if (snacksDataList.isEmpty()){
                                    publishSnacksMasterBox.add(storedSnacksList)
                                        .addOnSuccessListener {
                                            Toast.makeText(baseContext, "Snacks added", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener { e ->
                                            // Handle the error if document deletion fails
                                        }
                                }else{
                                    if (document.item in snacksDataList){

                                    } else{
                                        publishSnacksMasterBox.add(storedSnacksList)
                                            .addOnSuccessListener {
                                                Toast.makeText(baseContext, "Snacks added", Toast.LENGTH_SHORT).show()
                                            }
                                            .addOnFailureListener { e ->
                                                // Handle the error if document deletion fails
                                            }
                                    }
                                    val notInList = snacksDataList.filter { it !in itemList }
                                    if (notInList.isNotEmpty()){
                                        sharedPreferences
                                       var notInSacklist =notInList[0]
                                        editSnacksMasterBox(notInSacklist)
                                        val editor = sharedPreferences.edit()
                                        // Put the value in SharedPreferences
                                        editor.putString("notInSacklist", notInSacklist)
                                        // Apply the changes
                                        editor.apply()
                                    }
                                }
                        }.addOnFailureListener {
                            Toast.makeText(baseContext, "Snacks exist", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                    var snackData = PublishSnackSDate(currentDate, document.item, document.price.toInt() )

            }
//                snackCount = finalisedSnacks.size
                progressDialog.dismiss()
                snackAdapter.notifyDataSetChanged()
                Toast.makeText(baseContext, "Snacks Published", Toast.LENGTH_SHORT)
                    .show()
        } .addOnFailureListener {
                progressDialog.dismiss()
                finalisedSnacks.clear()
            }
    }

    private fun editSnacksMasterBox(snacksNotInList: String) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        calendar.add(Calendar.DAY_OF_YEAR,-1)
        publishSnacksMasterBox
            .whereEqualTo("snack",snacksNotInList).whereEqualTo("publishDate",currentDate).get()
            .addOnSuccessListener {dataResults->
                if(dataResults.size() > 0){
                    for (documentSnapshot in dataResults.documents) {
                       // Retrieve the document ID
                        val documentId = documentSnapshot.id
                        documentSnapshot.reference.delete()
                                .addOnSuccessListener {
                                    Toast.makeText(context,"Sorry ,$snacksNotInList Not Available", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Failed to update $snacksNotInList: $e", Toast.LENGTH_SHORT).show()
                                }
//                        }
                    }
                }
            }
    }

    private fun readDataTextView() {
        val itemsList = mutableListOf<String>()

        // Fetch all snack documents from the "snacks_list" collection
        Snacks.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val snackItems = document.get("Items") as? List<String>
                    if (snackItems != null) {
                        itemsList.addAll(snackItems)
                        snackAdapter.notifyDataSetChanged() // Add this line
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Toast.makeText(baseContext, "Failed to fetch items: $exception", Toast.LENGTH_SHORT)
                    .show()
            }

    }

        private fun readPublishSnacksMasterBox() {

            calendar.add(Calendar.DAY_OF_YEAR,1)
            val yesterdayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            // Here Filter published_snacks collection publish_date field equals to  yesterday

            publishSnacksMasterBox.whereEqualTo("publishDate", currentDate).get()
                .addOnSuccessListener { querySnapShot ->
                    publishedSnacksList.clear()
                    // Handle SuccessListener process
                        val snackCardList = mutableListOf<SnackCardModal>()
                        for (data in querySnapShot) {
                            // fetch snack and price from published_snacks firestore collection
                            val snack = data.get("snack") as String
                            val price = data.get("price") as String
//                            snackCardList.add(snack, price)

                            val retrievedValue = sharedPreferences.getString("notInSacklist", "") as String
                            if ( retrievedValue.isNotEmpty()&&snack == retrievedValue){
                                editSnacksMasterBox(retrievedValue)
                            }else{
                                publishedSnacksList.add(SnackSpinner(snack, price))
                            }
                            // set to the adapter
//                            snacksRecyclerView.layoutManager = LinearLayoutManager(this)
//                            snackAdapter = SnacksAdapter(publishedSnacksList, deleteItem = true,deleteButton = false, context)
//                            publishedSnacksList.add()

                        }
                    snackCount = publishedSnacksList.size
                    snacksRecyclerView.adapter = snackAdapter
                    snackAdapter.notifyDataSetChanged()

                    progressDialog.dismiss()
                }
                .addOnFailureListener { exception ->
                    // Handel Failure exception
                    Toast.makeText(baseContext, "Failed to fetch published snacks: $exception", Toast.LENGTH_SHORT).show()
                }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            var finalList= snackAdapter.getUpdatedSnacksList();
            if (snackCount != finalList.size) {
                // Handle snackCount not equals to finalList length
                val userType = sharedPreferences.getInt("user_type", 0)
                if (userType == 1 && userType == 2){
                    // call alert dialog box
                    showPublishConfirmationDialog()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

}
private fun <E> MutableList<E>.add(index: String, element: String) { }
data class PublishSnackSDate(val publish_date: String,val snack:String,val price:Int)