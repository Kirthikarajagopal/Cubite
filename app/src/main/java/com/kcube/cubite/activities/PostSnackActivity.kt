package com.kcube.cubite.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kcube.cubite.R
import com.kcube.cubite.adapters.ManageSnackListAdapter
import com.kcube.cubite.adapters.SpinnerAdapter
import com.kcube.cubite.com.kcube.cubite.SnackIncrementAdapter
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale




@Suppress("DEPRECATION", "NAME_SHADOWING")
class PostSnackActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val db = FirebaseFirestore.getInstance()
    private val PublishSnacksList = db.collection("publish_snacks")
    private val posted_snacks_by_employees = db.collection("posted_snacks_employees")
    private val biscuitSnackList = db.collection("biscuitsbox")
    private val juiceSnackList = db.collection("juicebox")
    private val postList = db.collection("posted_snacks")
    private val snacksList = db.collection("snacks_list")
    private val employees_list = db.collection("employees_list")
    private val published_snacks_collections = db.collection("published_snacks")
    private val data = FirebaseFirestore.getInstance()
    private val postSnacksMaster = data.collection("postSnacksMaster")
    private val SnacksMasterBox = data.collection("SnacksMasterBox")
    private val publishSnacksMasterBox = data.collection("publishSnacksMasterBox")
    var selectedSnacks: String = ""
    var selectedSnackPrice = 0
    private var empId: Int = 0
    private var userType: Int = 0
    private lateinit var empName: String
    private lateinit var snacksSpinner: Spinner
    private lateinit var biscuitsSpinner: Spinner
    private lateinit var juiceSpinner: Spinner
    private lateinit var postSnacksBtn: Button
    private lateinit var willTakeLater: Button
    private lateinit var getSnacksToday: Button
    private lateinit var uiFormLayout: LinearLayout
    private lateinit var logoutImage: ImageView
    private lateinit var uiOutRangeImage: ImageView
    private lateinit var uiSessionTextView: TextView
    private lateinit var uiEmployeeTextView: TextView
    private var postedSnackType: String = ""
    private var categoryExist :Boolean = false
    private lateinit var previousPostedSnack: TextView
    private var postedSnackDocId: String = ""
    private lateinit var progressDialog: AlertDialog
    private var isInsideOffice = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var ListView: RecyclerView
    private lateinit var comments:EditText
    private lateinit var combinationList :TextView
    private var itemList: ArrayList<increaseordecrease> = ArrayList()
    private var snacksItemsList = mutableListOf<SnackSpinner>()
    private var userComments :String = ""
//    private var currentDate: String = ""
    private var ListOfSncks: MutableList<Any> = mutableListOf()
    private val  calendar = Calendar.getInstance()
    private lateinit var newAdapter: SnackIncrementAdapter
    private var isItemClicked = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    private val targetLatitude = 13.038129175920137
    private val targetLongitude = 80.25279839483336
    private val targetRadius = 200.0 // 200 meters

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId", "SetTextI18n", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_snack)

        supportActionBar?.hide()

        // For Accessing purposes get the emp Id, emp Name, and user Type frorm shared preferences
        sharedPreferences = getSharedPreferences("Kcube_User", MODE_PRIVATE)
        empId = sharedPreferences.getInt("emp_id", 0)
        empName = sharedPreferences.getString("emp_name", "").toString()
        userType = sharedPreferences.getInt("user_type", 0)


        val dialogView =
            LayoutInflater.from(applicationContext).inflate(R.layout.progress_dialog_view, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(false) // Make the dialog non-cancelable
        progressDialog = builder.create()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        logoutImage = findViewById(R.id.uilogoutImage)
        snacksSpinner = findViewById(R.id.uiSelectSnack)
        biscuitsSpinner = findViewById(R.id.uiSelectBiscut)
        juiceSpinner = findViewById(R.id.uiSelectJuice)
        postSnacksBtn = findViewById(R.id.PurchaserButton)
        willTakeLater = findViewById(R.id.takelater)
        previousPostedSnack = findViewById(R.id.previousPostedSnack)
        uiFormLayout = findViewById(R.id.UIlinearSpinner)
        uiOutRangeImage = findViewById(R.id.uiOutRange)
        uiSessionTextView = findViewById(R.id.uiSessionText)
        uiEmployeeTextView = findViewById(R.id.uiEmployeeName)
        getSnacksToday = findViewById(R.id.revertTakeLater)
        ListView = findViewById(R.id.ListView)
        comments = findViewById(R.id.comments)
        combinationList = findViewById(R.id.combinationList)

        progressDialog.dismiss()

//        if (isTodayIsFridayOrWeekEnd()) {
//            willTakeLater.visibility = View.GONE
////                                readSnacksBoxList(0)
//            readSnacksMasterBox(0)
//        } else {
////                                progressDialog.dismiss()
//            checkWillTakeLaterAvailable()
//        }
        logoutImage.setOnClickListener {
            logOutFunction()

        }


        newAdapter = SnackIncrementAdapter(itemList,this)
        ListView.layoutManager = LinearLayoutManager(this)
        ListView.adapter = newAdapter
        newAdapter.notifyDataSetChanged()



        // Hide UI properties
//        willTakeLater.visibility = View.VISIBLE
        comments.visibility = View.INVISIBLE
        combinationList.visibility = View.GONE
        ListView.visibility = View.GONE


        if (userType == 1 || userType == 2 ||userType == 3) {
            // Handle if user Type 3
            uiSessionTextView.text = "Good ${getTimeOfDay()}"
            uiEmployeeTextView.text = empName
            uiSessionTextView.visibility = View.VISIBLE
            uiEmployeeTextView.visibility = View.VISIBLE
        }

                val storedSelectedSnacks = sharedPreferences.getString("selectedSnacks", "")

                previousPostedSnack.text = "Posted Snack :  ${storedSelectedSnacks}"
                previousPostedSnack.visibility = View.VISIBLE
                selectedSnacks = ""

        getSnacksToday.setOnClickListener {
            // call gettingSnacksToday function
            gettingSnacksToday()

        }
        willTakeLater.setOnClickListener {
            // call
           willTakeLaterProcess()
        }

        postSnacksBtn.setOnClickListener {
            storeSnacksList()
        }


        snacksSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val selectedSnackData = parent?.getItemAtPosition(position) as? SnackSpinner
                val hint = "Select Snack"
                if (selectedSnackData != null && selectedSnackData.item != hint) {
                    selectedSnacks = selectedSnackData.item
                    selectedSnackPrice = selectedSnackData.price.toIntOrNull() ?:0
                    comments.visibility = View.VISIBLE
                    if (selectedSnackPrice != null) {
                        previousPostedSnack.text = "Selected Snack :  $selectedSnacks"
                        previousPostedSnack.visibility = View.VISIBLE
                        biscuitsSpinner.setSelection(0)
                        juiceSpinner.setSelection(0)
                        calendar.add(Calendar.DAY_OF_YEAR,-1)
                        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        val yesterdayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                        publishSnacksMasterBox.whereEqualTo("publishDate" ,currentDate).get()
                            .addOnSuccessListener { dataresult ->
                                for (item in dataresult) {
                                    var Category= item.get("category") as Boolean
                                    var snack  = item.get("snack") as String
                                    var categories = item.get("categories") as ArrayList<String>
                                    if (selectedSnacks == snack ){
                                        if (Category){
                                            // Clear existing items in itemList before adding new ones
                                            itemList.clear()
                                            for (index in categories.indices) {
                                                val element = categories[index]
                                                itemList.add(increaseordecrease(snacks = element , Snackcount = 0))
                                                newAdapter.notifyDataSetChanged()
                                            }
                                            combinationList.visibility = View.VISIBLE
                                            ListView.visibility = View.VISIBLE
                                        }else{
                                            combinationList.visibility = View.GONE
                                            ListView.visibility = View.GONE
                                    }
                                        break
                                }
                            }
                        }
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


        biscuitsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val selectedItemText = parent?.getItemAtPosition(position) as? SnackSpinner
                val hint = "Select Biscuit"
                if (selectedItemText != null && selectedItemText.item != hint) {
                    selectedSnacks = selectedItemText.item
                    selectedSnackPrice = selectedItemText.price.toInt()
                    comments.visibility = View.GONE
                    previousPostedSnack.text = "Selected Snack :  $selectedSnacks"
                    previousPostedSnack.visibility = View.VISIBLE
                    snacksSpinner.setSelection(0)
                    juiceSpinner.setSelection(0)
                    combinationList.visibility = View.GONE
                    ListView.visibility = View.GONE
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        juiceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val selectedItemText = parent?.getItemAtPosition(position) as? SnackSpinner
                val hint = "Select Juice"
                if (selectedItemText != null && selectedItemText.item != hint) {
                    snacksSpinner.setSelection(0)// Select the hint item
                    biscuitsSpinner.setSelection(0)
                    selectedSnacks = selectedItemText.item
                    selectedSnackPrice = selectedItemText.price.toInt()
                    previousPostedSnack.text = "Selected Snack :  $selectedSnacks"
                    previousPostedSnack.visibility = View.VISIBLE
                    combinationList.visibility = View.GONE
                    ListView.visibility = View.GONE
                    comments.visibility = View.GONE
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun storeSnacksList() {
        userComments = comments.text.toString()

        if (selectedSnacks != "") {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setCancelable(false)
            alertDialogBuilder.setTitle("Post Snack")
            alertDialogBuilder.setMessage("are you sure you want to post this snack?")
            alertDialogBuilder.setPositiveButton("Post") { _, _ ->

                if (newAdapter.isButtonClicked) {
                    comments.visibility = View.INVISIBLE
                    combinationList.visibility = View.INVISIBLE
                    ListView.visibility = View.INVISIBLE
                    snackspinner()
                    biscuitSpinnerList()
                    juiceSpinnerList()

                    progressDialog.show()
                    calendar.add(Calendar.DAY_OF_YEAR,-1)
                    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val yesterdayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                    var snackData = PostedEmployeeSnack(empId, empName, selectedSnacks, currentDate)
                    if (postedSnackDocId != "" ) {
                        // map the objects
                        val updates: Map<String, Any> = mapOf(
                            "emp_id" to empId,
                            "emp_name" to empName,
                            "comments" to userComments,
                            "posted_date" to currentDate,
                            "snacks" to selectedSnacks
                        )
                        // update mapped object  in firestore collection
                        posted_snacks_by_employees.document(postedSnackDocId).update(updates)
                            .addOnSuccessListener {
                                // handle Success event
                                Toast.makeText(applicationContext, "Posted Successfully", Toast.LENGTH_SHORT).show()
//                            previousPostedSnack.text = "Posted Snack : ${selectedSnacks}"
//                            previousPostedSnack.visibility = View.VISIBLE
//                            selectedSnacks = ""
                                willTakeLater.visibility = View.GONE
                                if (selectedSnackPrice >= 80) {
                                    updateWillTakeSnacks(0, "")
                                }
                                progressDialog.dismiss()
                            }.addOnFailureListener { e ->
                                // Handle failure Event
                                progressDialog.dismiss()
//                            Toast.makeText(applicationContext, "Post Failed", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // Add snackData in firestore collection
                        posted_snacks_by_employees.add(snackData)
                            .addOnSuccessListener { documentRef ->
                                postedSnackDocId = documentRef.id
                                Toast.makeText(
                                    applicationContext, "Posted Successfully", Toast.LENGTH_SHORT
                                ).show()
                                willTakeLater.visibility = View.GONE
                                if (selectedSnackPrice >= 80) {
                                    // Handle selectedSnackPrice equals to or more then 80 rupees
                                    updateWillTakeSnacks(0, "")
                                }
//                            previousPostedSnack.text = "Posted Snack :  ${selectedSnacks}"
//                            previousPostedSnack.visibility = View.VISIBLE
//                            selectedSnacks = ""
                                progressDialog.dismiss()
                            }.addOnFailureListener { e ->
                                // Handle Failure event
                                progressDialog.dismiss()
//                            Toast.makeText(applicationContext, "Post Failed", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else{
                    Toast.makeText(applicationContext, "No Snacks Select", Toast.LENGTH_SHORT).show()
                }
            }
            alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                // Handle If user click "Cancel"
                dialog.dismiss()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        } else {
            Toast.makeText(applicationContext, "No Snacks Select", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun willTakeLaterProcess() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setTitle("Take Today Snacks Later")
        alertDialogBuilder.setMessage("you can get today snack later within this week?")
        alertDialogBuilder.setPositiveButton("Take Later") { _, _ ->
            calendar.add(Calendar.DAY_OF_YEAR,-1)
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val yesterdayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            // call updateWillTakeSnacks function
            comments.visibility = View.GONE
            combinationList.visibility = View.GONE
            ListView.visibility = View.GONE
            updateWillTakeSnacks(1, currentDate);
        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun gettingSnacksToday() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setTitle("Take Snacks Today")
        alertDialogBuilder.setMessage("Revert to take today snacks?")
        alertDialogBuilder.setPositiveButton("Revert") { _, _ ->
            updateWillTakeSnacks(0, "");
        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun logOutFunction() {
        // Create an AlertDialog.Builder
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Logout")
        alertDialogBuilder.setMessage("Are you sure you want to log out?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            // Handle logout logic here
            sharedPreferences = getSharedPreferences("Kcube_User", MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.clear()
            editor.putBoolean("loginStatus", false)
            editor.apply()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Finish the MainActivity so the user cannot navigate back without logging in
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        // Create and show the AlertDialog
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun juiceSpinnerList() {
        // Store user select juiceSpinnerList store postSnacksMaster collection
        juiceSnackList.get()
            .addOnSuccessListener {dataresult ->
                for (item in dataresult) {
                    val snack: String = item.get("item") as String
                    if (snack != null ) {
                        calendar.add(Calendar.DAY_OF_YEAR,-1)
                        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        val yesterdayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

                        if (selectedSnacks == snack){
                            addSnacks(snack)
                            Toast.makeText(baseContext, "Successfully Added", Toast.LENGTH_SHORT).show()
                            break
                        }
                    }
                }
            }
        }

    private fun biscuitSpinnerList() {
        biscuitSnackList.get()
            // Store user select biscuitSpinnerList store postSnacksMaster collection
            .addOnSuccessListener {dataresult ->
                for (item in dataresult) {
                    val snack: String = item.get("item") as String;
                    var price: Long = item.get("price") as Long
                    calendar.add(Calendar.DAY_OF_YEAR,-1)
                    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val yesterdayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                    val updatedSnacksList  = hashMapOf(
                        "emp_id" to empId,
                        "emp_name" to empName,
                        "categories_exist" to categoryExist,
                        "comments" to userComments,
                        "posted_date" to currentDate,
                        "snack" to snack
                    )
                    if (selectedSnacks == snack){
                        addSnacks(snack)
                        Toast.makeText(baseContext, "Successfully Added", Toast.LENGTH_SHORT).show()
                        break
                    }
                }
            }
    }

    private fun snackspinner() {
        calendar.add(Calendar.DAY_OF_YEAR,-1)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val yesterdayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        publishSnacksMasterBox.whereEqualTo("publishDate",currentDate).get()
            .addOnSuccessListener {dataresult ->
                for (item in dataresult) {
                    var snack: String = item.getString("snack") ?: ""
                    var category: Boolean? = item.getBoolean("category")
                    var selectSnackItem = selectedSnacks
                    if (selectSnackItem == snack){
                        if (category == true){
                            addupdatedCategories(snack, category)

                        }else{
                            addSnacks(snack)

                        }
                        break
                    }

                }
            }.addOnFailureListener { e ->
                // Handle the error
                // TODO:
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun readSnacksMasterBox(priceFlag: Int) {
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        publishSnacksMasterBox.whereEqualTo("publishDate",currentDate)
            .get()
            .addOnSuccessListener { dataresult ->
                 val snacksItemsList = mutableListOf<SnackSpinner>()
                snacksItemsList.add(SnackSpinner("Select Snack", ""))
//                val snacksList: MutableList<Any> = mutableListOf()

                if (dataresult.size() > 0) {
                    for (item in dataresult) {

                        val snack = item.get("snack") as String
                        val price = item.get("price") as String
                        val Category= item.get("category") as Boolean
                        val categories = item.get("categories") as ArrayList<String>


                        snacksItemsList.add(SnackSpinner(snack, price))

                    }
                    // set to the adapter
                    val adapter = SpinnerAdapter(this, snacksItemsList)
                    snacksSpinner.adapter = adapter
                    snacksSpinner.visibility = View.VISIBLE
                    // call the readBiscuitsSnack function
                    readBiscuitsSnack(biscuitsSpinner, priceFlag)
                }
            }
    }

    private fun addupdatedCategories(snack: String, Category: Boolean) {

        calendar.add(Calendar.DAY_OF_YEAR,-1)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val yesterdayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        val updatedCategories  = hashMapOf(
            "emp_id" to empId,
            "emp_name" to empName,
            "snack" to snack,
            "categories_exist" to Category,
            "comments" to userComments,
            "posted_date" to currentDate,
            "categories" to itemList.map { it.snacks to it.Snackcount }.toMap()
        )
        // update mapped object  in firestore collection
        postSnacksMaster.whereEqualTo("posted_date",currentDate).get()
            .addOnSuccessListener { dataResult ->
                if (dataResult.size()>0){
                    for (document in dataResult) {
                        val documentId = document.id
                        postSnacksMaster.document(documentId).delete()
                            .addOnSuccessListener {
                                                    }
                            .addOnFailureListener { e ->
                                // Handle the error
                                // TODO:
                    }
               }
                    postSnacksMaster.add(updatedCategories)
                        .addOnSuccessListener { documentReference ->
                            // Data added successfully
                            sharedPreferences = getSharedPreferences("Kcube_User", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("selectedSnacks", snack)
                            editor.apply()
                            previousPostedSnack.text = "Posted Snack:  ${snack}"
                            previousPostedSnack.visibility = View.VISIBLE
                            selectedSnacks = ""
                            Toast.makeText(baseContext, "Successfully Added", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            // Handle the error
                            // TODO:
                        }
            }else{
                    postSnacksMaster.add(updatedCategories)
                        .addOnSuccessListener { documentReference ->
                            // Data added successfully
                            sharedPreferences = getSharedPreferences("Kcube_User", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("selectedSnacks", snack)
                            editor.apply()
                            previousPostedSnack.text = "Posted Snack:  ${snack}"
                            previousPostedSnack.visibility = View.VISIBLE
                            selectedSnacks = ""
                            Toast.makeText(baseContext, "Successfully Added", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            // Handle the error
                            // TODO:
                        }
            }
        }
    }

    private fun addSnacks(snack: String) {
        calendar.add(Calendar.DAY_OF_YEAR,-1)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val yesterdayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        val updatedSnacksList  = hashMapOf(
            "emp_id" to empId,
            "emp_name" to empName,
            "categories_exist" to categoryExist,
            "comments" to userComments,
            "posted_date" to currentDate,
            "snack" to snack
        )

        postSnacksMaster.whereEqualTo("emp_id",empId).whereEqualTo("posted_date",currentDate).get()
            .addOnSuccessListener { dataResult ->
                if (dataResult.size()>0){
                    for (document in dataResult) {
                        val documentId = document.id
                        postSnacksMaster.document(documentId).delete()
                            .addOnSuccessListener {
                                // Document successfully deleted
                                // Now, add the updatedCategories
                            }
                            .addOnFailureListener { e ->
                                // Handle the error
                                // TODO:
                            }
                    }
                    postSnacksMaster.add(updatedSnacksList)
                        .addOnSuccessListener { documentReference ->

                            // Data added successfully
                            sharedPreferences = getSharedPreferences("Kcube_User", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("selectedSnacks", snack)
                            editor.apply()
                            previousPostedSnack.text = "Posted Snack :  $snack"
                            previousPostedSnack.visibility = View.VISIBLE
                            selectedSnacks = ""

                        }.addOnFailureListener { e ->
                            // Handle the error
                            // TODO:
                        }
                } else{
                    postSnacksMaster.add(updatedSnacksList)
                        .addOnSuccessListener { documentReference ->

                            // Data added successfully
                            sharedPreferences = getSharedPreferences("Kcube_User", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("selectedSnacks", snack)
                            editor.apply()
                            previousPostedSnack.text = "Posted Snack :  $snack"
                            previousPostedSnack.visibility = View.VISIBLE
                            selectedSnacks = ""

                        }.addOnFailureListener { e ->
                            // Handle the error
                            // TODO:
                        }
                }
            }
    }

    fun getTimeOfDay(): String {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

        return when {
            currentHour in 0..11 -> "Morning"
            currentHour in 12..16 -> "Afternoon"
            else -> "Evening"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkAndRequestLocationPermission() {
        // Check and request location permissions
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            requestLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }


    private fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Float {
        val result = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, result)
        return result[0]
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Handle if the condition is true
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        // Use the location
                        val distance = calculateDistance(
                            location.latitude,
                            location.longitude,
                            targetLatitude,
                            targetLongitude
                        )
                        if (distance <= targetRadius) {
                            // Handle if the condition is true
                            if (isTodayIsFridayOrWeekEnd()) {
                                willTakeLater.visibility = View.GONE
//                                readSnacksBoxList(0)
                                readSnacksMasterBox(0)
                                postSnacksBtn.visibility = View.VISIBLE
                            } else {
                                progressDialog.dismiss()
                                checkWillTakeLaterAvailable()

                            }
                        } else {

                            postSnacksBtn.visibility = View.VISIBLE
                            postSnacksBtn.text = "Your Not Inside The Office"
                            postSnacksBtn.isEnabled = false
                            uiFormLayout.visibility = View.VISIBLE
                            uiOutRangeImage.visibility = View.VISIBLE
                        }
                    } else {
                        // Handle the case where location is null
                    }
                }
                .addOnFailureListener { e ->
                    var error = e
                    // Handle location request failure
                }
            return
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // handle requestCode equals to LOCATION_PERMISSION_REQUEST_CODE
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Handle if the condition is true
                requestLocation()

            } else {
                checkAndRequestLocationPermission()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        if (isCurrentTimeWithin430PM()) {
            snacksSpinner.isEnabled = false
            biscuitsSpinner.isEnabled = false
            juiceSpinner.isEnabled = false
            previousPostedSnack.text = "Time Exceeded ,after 4.30 PM you can't post snacks Today"
            previousPostedSnack.visibility = View.VISIBLE
            postSnacksBtn.text = "Time Exceeded"
            postSnacksBtn.isEnabled = false
            willTakeLater.visibility = View.GONE
        } else {
            checkCurrentUserLocation()
        }
    }

    private fun checkCurrentUserLocation() {
        //progressDialog.show()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            val locationRequest = LocationRequest.create().apply {
                interval = 60000 * 5 // Update interval in milliseconds (e.g., every 10 seconds)
                fastestInterval = 60000 // Fastest update interval
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Use high accuracy
            }

            // Set up location callback
            val locationCallback = object : LocationCallback() {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onLocationResult(p0: LocationResult) {
                    //progressDialog.dismiss()
                    p0?.lastLocation?.let { location ->
                        // Handle the retrieved location here
                        val distance = calculateDistance(
                            location.latitude,
                            location.longitude,
                            targetLatitude,
                            targetLongitude
                        )
                        if (distance <= targetRadius) {
                            if (isTodayIsFridayOrWeekEnd()) {
                                willTakeLater.visibility = View.GONE
//                                readSnacksBoxList(0)
                                readSnacksMasterBox(0)
                            } else {
//                                progressDialog.dismiss()
                                checkWillTakeLaterAvailable()
                            }
                        } else {
                            postSnacksBtn.visibility = View.VISIBLE
                            postSnacksBtn.text = "Your Not Inside The Office"
                            postSnacksBtn.isEnabled = false
                            previousPostedSnack.text =
                                "You Can't Post Snacks, If you are not inside the office"
                            previousPostedSnack.visibility = View.VISIBLE
                            uiFormLayout.visibility = View.VISIBLE
                            uiOutRangeImage.visibility = View.VISIBLE
                        }
                        // Do something with latitude and longitude
                    }
                }
            }
            // Request location updates
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } else {
            // Location permission is not granted, request it from the user
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isCurrentTimeWithin430PM(): Boolean {
        val currentTime = LocalTime.now()
        val targetTime = LocalTime.of(17, 0) // 4:30 PM
        return !currentTime.isBefore(targetTime)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isDateWithinCurrentWeek(dateString: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return try {
            val date = LocalDate.parse(dateString, formatter)
            val currentDate = LocalDate.now()
            // is the Date Within Current Week
            date.dayOfWeek in setOf(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY
            ) || date.isEqual(currentDate) || date.isAfter(currentDate.minusDays(1))
        } catch (e: Exception) {
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isStringDateToday(dateString: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return try {
            val date = LocalDate.parse(dateString, formatter)
            val currentDate = LocalDate.now()
            date.isEqual(currentDate)
        } catch (e: Exception) {
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateWillTakeSnacks(willTakeFlag: Int, currentDate: String) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val updates: Map<String, Any> = mapOf(
            "takeLater" to willTakeFlag, "took_date" to currentDate
        )
        employees_list.document(empId.toString()).update(updates).addOnSuccessListener {
            if (willTakeFlag == 0) {
                // Handle willTakeFlag equals to 0
                willTakeLater.text = "Take Later"
                willTakeLater.setBackgroundColor(Color.parseColor("#3E98D0"))
                willTakeLater.isEnabled = true;
                willTakeLater.visibility = View.VISIBLE
                getSnacksToday.visibility = View.GONE
                progressDialog.show()
//                readSnacksBoxList(0)
                readSnacksMasterBox(0)
            } else {
                // Handle willTakeFlag not equals to 0
                snacksSpinner.visibility = View.GONE
                biscuitsSpinner.visibility = View.GONE
                juiceSpinner.visibility = View.GONE
                previousPostedSnack.text = "Revert take Later and Get Your Snacks Today"
                previousPostedSnack.visibility = View.VISIBLE
                postSnacksBtn.visibility = View.GONE
                willTakeLater.visibility = View.GONE
                getSnacksToday.visibility = View.VISIBLE
            }
            Toast.makeText(applicationContext, "Updated", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { exception ->
            // Handle Failure
            Toast.makeText(applicationContext, "Update failed", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkWillTakeLaterAvailable() {
        employees_list.document(empId.toString()).get().addOnSuccessListener { dataResuls ->
            if (dataResuls.exists()) {
                val willTakeLaterCount: Long? = dataResuls.get("takeLater") as? Long
                if (willTakeLaterCount != null && willTakeLaterCount > 0) {
                    willTakeLater.visibility = View.VISIBLE
                    willTakeLater.text = "One Take Later Available"
                    willTakeLater.setBackgroundColor(Color.RED)
                    willTakeLater.isEnabled = false;
                    val takeLaterDate = dataResuls.get("took_date") as String
                    if (isStringDateToday(takeLaterDate)) {
                        val alertDialogBuilder = AlertDialog.Builder(this)
                        alertDialogBuilder.setCancelable(false)
                        alertDialogBuilder.setTitle("Your Snack on Hold")
                        alertDialogBuilder.setMessage("You can not take snack today, you can take tomorrow!")
                        alertDialogBuilder.setPositiveButton("Done") { dialog, _ ->
                            dialog.dismiss()
//                            finish()
                        }
                        val alertDialog = alertDialogBuilder.create()
                        alertDialog.show()
                        snacksSpinner.isEnabled = false
                        biscuitsSpinner.isEnabled = false
                        juiceSpinner.isEnabled = false
                        previousPostedSnack.text = "Revert take Later and Get Your Snacks Today"
                        previousPostedSnack.visibility = View.VISIBLE
                        postSnacksBtn.visibility = View.GONE
                        willTakeLater.visibility = View.GONE
                        getSnacksToday.visibility = View.VISIBLE
                    } else {
                        if (!isDateWithinCurrentWeek(takeLaterDate)) {
                            updateWillTakeSnacks(0, "");
                        } else {
                            progressDialog.show()
//                            readSnacksBoxList(1)
                            readSnacksMasterBox(1)
                        }
                    }
                } else {
                    if (!isCurrentTimeWithin430PM()) {
                        willTakeLater.visibility = View.VISIBLE
                    }
//                    readSnacksBoxList(0)
                    readSnacksMasterBox(0)
                }

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun readSnacksBoxList(priceFlag: Int) {
        progressDialog.show()
        val currentDate =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        calendar.add(Calendar.DAY_OF_YEAR,-1)
        val yesterdayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        published_snacks_collections.whereEqualTo("publish_date", yesterdayDate).get()
            .addOnSuccessListener { dataResults ->
                val snacksList = mutableListOf<SnackSpinner>()
                if (dataResults.size() > 0) {
                    snacksList.add(SnackSpinner("Select Snack", ""))
                    for (snacks in dataResults) {
                        val snack: String = snacks.get("snack") as String;
                        val price: Long = snacks.get("price") as Long
                        if (priceFlag == 1) {
                            snacksList.add(SnackSpinner(snack, price.toString()))
                        } else {
                            if (price <= 60) {
                                snacksList.add(SnackSpinner(snack, price.toString()))
                            }
                        }
                    }
                    val adapter = SpinnerAdapter(this, snacksList)
                    snacksSpinner.adapter = adapter
                    snacksSpinner.visibility = View.VISIBLE
                    readBiscuitsSnack(biscuitsSpinner, priceFlag)
                } else {
                    previousPostedSnack.text = "No Snacks Posted Yet"
                    previousPostedSnack.visibility = View.VISIBLE
                    postSnacksBtn.visibility = View.VISIBLE
                    postSnacksBtn.isEnabled = false
                    snacksSpinner.isEnabled = false
                    biscuitsSpinner.isEnabled = false
                    juiceSpinner.isEnabled = false
                    postSnacksBtn.isEnabled = false
                    willTakeLater.visibility = View.GONE
                    progressDialog.dismiss()
                }

            }.addOnFailureListener { exceptions ->
                var exec = exceptions
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun readJuiceSnack(selectJuice: Spinner?, priceFlag: Int) {
        juiceSnackList.get().addOnSuccessListener { dataResults ->
            val juiceList = mutableListOf<SnackSpinner>()
            juiceList.add(SnackSpinner("Select Juice", ""))
            for (snacks in dataResults) {
                val snack: String = snacks.get("item") as String;
                val price: Long = snacks.get("price") as Long
                if (priceFlag == 1) {

                    juiceList.add(SnackSpinner(snack, price.toString()))
                } else {
                    if (price <= 50) {

                        juiceList.add(SnackSpinner(snack, price.toString()))
                    }
                }
            }
            val adapter = SpinnerAdapter(this, juiceList)
            if (selectJuice != null) {

                selectJuice.adapter = adapter
                selectJuice.visibility = View.VISIBLE
            }
            postSnacksBtn.visibility = View.VISIBLE
            updautePreviousSnack()
        }.addOnFailureListener {

        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun readBiscuitsSnack(SelectBiscut: Spinner, priceFlag: Int) {
        biscuitSnackList.get().addOnSuccessListener { dataResults ->
            val biscuitsList = mutableListOf<SnackSpinner>()
            biscuitsList.add(SnackSpinner("Select Biscuit", ""))
            for (snacks in dataResults) {
                val snack: String = snacks.get("item") as String;
                val price: Long = snacks.get("price") as Long
                biscuitsList.add(SnackSpinner(snack, price.toString()))
            }
            val adapter = SpinnerAdapter(this, biscuitsList)
            // set to adapter
            SelectBiscut.adapter = adapter
            SelectBiscut.visibility = View.VISIBLE
            readJuiceSnack(juiceSpinner, priceFlag)
        }.addOnFailureListener {
            // Handle Failure
            Toast.makeText(applicationContext, "something wrong fetch biscuit ", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isTodayIsFridayOrWeekEnd(): Boolean {
        // check Today friday or week end
        val today = LocalDate.now()
        val dayOfWeek = today.dayOfWeek
        return dayOfWeek == DayOfWeek.FRIDAY || dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY
    }

    private fun showPublishConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setMessage("Are you sure you want to Post snacks?")
        alertDialogBuilder.setPositiveButton("Post") { _, _ ->
            //handle  if "post" clicked
            Toast.makeText(applicationContext, "$selectedSnacks Selected", Toast.LENGTH_SHORT)
                .show()
        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            //handle  if "Cancel" clicked
            dialog.dismiss()
            finish()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun updateEmployeesListCollection() {
        val updates = hashMapOf(
            "back" to 1
        )
        employees_list.document(empId.toString()).set(updates, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Updated", Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener {
                Toast.makeText(applicationContext, "Update failed", Toast.LENGTH_SHORT).show()
            }
    }


    private fun updautePreviousSnack() {

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val postedSnackDacument = postList.document(currentDate)
        postSnacksMaster.whereEqualTo("posted_date", currentDate)
            .whereEqualTo("emp_id", empId).get().addOnSuccessListener { documentSnapShot ->
                if (documentSnapShot.size() > 0) {
                    for (snacksData in documentSnapShot) {
                        // Fetch snack from collection
                        val postedSnacks = snacksData.get("snack") as? String
                        if (postedSnacks != null){
                            previousPostedSnack.text = "Posted Snack :  ${postedSnacks}"
                            willTakeLater.visibility = View.GONE
                            previousPostedSnack.visibility = View.VISIBLE
                            postedSnackType = postedSnacks
                            postedSnackDocId = snacksData.id;
                        }

                    }
                } else {
                    // documentSnapShot size 0 or less then 0
                    previousPostedSnack.text = "Not Yet Posted Snack"
                    previousPostedSnack.visibility = View.VISIBLE
                    willTakeLater.visibility = View.VISIBLE
                }
                progressDialog.dismiss()
            }.addOnFailureListener {
                // Handle Failure
                progressDialog.dismiss()
            }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Handle if the user without clicking post snack button click the back button
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (postedSnackType != "" && postedSnackType != selectedSnacks && selectedSnacks != "") {
                showPublishConfirmationDialog()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)

    }

    private fun readPostedSnacks(callback: (JSONObject?) -> Unit) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val postedSnackDacument = postList.document(currentDate)

        postedSnackDacument.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val dataItem = documentSnapshot.data
                if (dataItem != null && dataItem.containsKey("snacks")) {
                    val snacksJsonObject = dataItem["snacks"] as? JSONObject
                    val sharedPreferences =
                        getSharedPreferences("Kcube_User", MODE_PRIVATE)
                    val empId = sharedPreferences.getInt("emp_id", 0)
                    val empName = sharedPreferences.getString("emp_name", "")

                    val snacksArray = dataItem["snacks"] as? MutableList<HashMap<String, Any>>

                    // Assuming you have already retrieved the necessary values like empId, empName, and selectedSnacks
                    // Initialize a flag to track whether emp_id exists
                    var empIdExists = false
                    var existingEmpId = empId
                    // Iterate through the snacksArray to find the emp_id
                    if (snacksArray != null && snacksArray.isNotEmpty()) {
                        for (snackItem in snacksArray) {

                            val snackEmpId= snackItem.getValue("emp_id") as Long
                            val snackEmpName = snackItem.getValue("emp_name") as String
                            //snackItem["snack"] = selectedSnacks


                            // Debugging output to check the values
                            Log.d(
                                "EmpIdCheck",
                                "existingEmpId: $existingEmpId, snackEmpId: $snackEmpId"
                            )

                            if (snackEmpId == existingEmpId.toLong()) {
                                // emp_id exists in the "snacks" array, update the snack field
                                snackItem["snack"] = selectedSnacks
                                empIdExists = true
                                break  // Exit the loop once emp_id is found and updated
                            }
                        }
                    }
                    if (!empIdExists) {

                        // emp_id does not exist in the "snacks" array
                        val sharedPreferences =
                            getSharedPreferences("Kcube_User", MODE_PRIVATE)
                        val empId = sharedPreferences.getInt("emp_id", 0)
                        val empName = sharedPreferences.getString("emp_name", "")

                        val newSnackItem = HashMap<String, Any>()
                        newSnackItem["emp_id"] = empId
                        newSnackItem["emp_name"] = empName ?: "DefaultName"
                        newSnackItem["snack"] = selectedSnacks

                        // Add the new snack item to the existing array
                        snacksArray?.add(newSnackItem)

                    }

                    // Update the Firestore document with the updated "snacks" array
                    val updateData = hashMapOf("snacks" to snacksArray)
                    postedSnackDacument.update(updateData as Map<String, Any>)
                        .addOnSuccessListener {
                            callback(snacksJsonObject)

                        }.addOnFailureListener { e ->
                            // Handle the error
                            callback(null)
                        }
                }
//                        callback(snacksJsonObject)
                else {
                    callback(null)  // No "snacks" field or it's not a JSONObject
                }
            } else {

                val sharedPreferences = getSharedPreferences("Kcube_User", MODE_PRIVATE)
                val empId = sharedPreferences.getInt("emp_id", 0)
                val empName = sharedPreferences.getString("emp_name", "")
                // Document doesn't exist, create it with the current date and an initial "snacks" field
                val jsonObject = JSONObject()

                val objectList = listOf(empName?.let { PostedSnack(empId, it, selectedSnacks) }
                    // Add more objects as needed
                )

                val dataClassList = objectList.map { obj ->
                    PostedSnack(
                        emp_id = obj!!.emp_id, emp_name = obj!!.emp_name, snack = obj!!.snack
                        // Map other properties as needed
                    )
                }

                try {
                    jsonObject.put("emp_id", empId)
                    jsonObject.put("emp_name", empName)
                    jsonObject.put("snack", selectedSnacks)
                } catch (e: JSONException) {
                    // Handle JSON exception, if any
                    e.printStackTrace()
                }
                postedSnackDacument.set(mapOf("snacks" to dataClassList)).addOnSuccessListener {
                    callback(jsonObject)
                }.addOnFailureListener { e ->
                    // Handle the error
                    callback(null)
                }
            }
        }.addOnFailureListener { e ->
            // Handle the error
//                callback(null)
            val sharedPreferences = getSharedPreferences("Kcube_User", MODE_PRIVATE)
            val empId = sharedPreferences.getInt("emp_id", 0)
            val empName = sharedPreferences.getString("emp_name", "")
            // Document doesn't exist, create it with the current date and an initial "snacks" field
            val jsonObject = JSONObject()

            val objectList = listOf(empName?.let { PostedSnack(empId, it, selectedSnacks) }
                // Add more objects as needed
            )


            val dataClassList = objectList.map { obj ->
                PostedSnack(
                    emp_id = obj!!.emp_id, emp_name = obj!!.emp_name, snack = obj!!.snack
                    // Map other properties as needed
                )
            }

            try {
                jsonObject.put("emp_id", empId)
                jsonObject.put("emp_name", empName)
                jsonObject.put("snack", selectedSnacks)
            } catch (e: JSONException) {
                // Handle JSON exception, if any
                e.printStackTrace()
            }
            postedSnackDacument.set(mapOf("snacks" to dataClassList)).addOnSuccessListener {
                callback(jsonObject)
            }.addOnFailureListener { e ->
                // Handle the error
                callback(null)
            }
        }
    }


    private fun readPublishSnacks(selectSnack: Spinner?) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val publishList =
            PublishSnacksList.document(currentDate) // Replace "currentDate" with the actual document ID

        publishList.get().addOnSuccessListener { documentSnapshot ->
            val itemsList = mutableListOf<String>()
            itemsList.add("Select Snack") // Add the "Select Snacks" option at index 0
            if (documentSnapshot.exists()) {
                val items = documentSnapshot["snacks"] as? List<String>
                if (items != null) {
                    itemsList.addAll(items)
                }
            }
            val adapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, itemsList)
            if (selectSnack != null) {
                selectSnack.adapter = adapter
            }
        }.addOnFailureListener { exception ->
            // Handle any errors
            val adapter = ArrayAdapter(
                this, android.R.layout.simple_spinner_dropdown_item, emptyList<String>()
            )
//                if (selectSnack != null) {
            selectSnack!!.adapter = adapter
//                }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    fun onItemClicked() {
        isItemClicked = true
    }
}

data class PostedSnack(
    val emp_id: Int, val emp_name: String, val snack: String
    // Add other fields as needed
)

data class PostedEmployeeSnack(val emp_id: Int, val emp_name: String, val snack: String, val posted_date: String)
data class SnackSpinner( val item: String, val price: String)
data class increaseordecrease(val snacks: String, var Snackcount: Int)