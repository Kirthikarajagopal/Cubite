package com.kcube.cubite.activities


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.kcube.cubite.R
import com.kcube.cubite.com.kcube.cubite.SnackListAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SnacksPurchaser : AppCompatActivity() {

    lateinit var PurchaseList: ExpandableListView
    private lateinit var alertMessage : TextView
    private lateinit var userName : TextView
    lateinit var SnackListAdapter : ExpandableListAdapter
    private var itemMap: MutableMap<String, SnackCardModal> = mutableMapOf()
    var expandableListTitle:  MutableList<String> = mutableListOf()
    var expandableListDetail: HashMap<String,List<String>> = HashMap()
    var groupItem : String = ""
    var childList : String = ""
    private val  calendar = Calendar.getInstance()
    private val postData = FirebaseFirestore.getInstance()
    private val postList = postData.collection("posted_snacks_employees")
    private val postedSnacksMaster = postData.collection("postSnacksMaster")
    private val employee_list = postData.collection("employees_list")
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: SnackListAdapter
    private var snackCardModalList = mutableListOf<SnackCardModal>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snacks_purchaser)

//        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
//        val calendar = Calendar.getInstance()
//        calendar.add(Calendar.DATE, -1)
//        dateFormat.format(calendar.getTime()); //your formatted date here
//        val yesterday = calendar.time

        //hide appBar
        supportActionBar?.hide()

        val logoutImage = findViewById<ImageView>(R.id.uilogoutImage)
        PurchaseList = findViewById(R.id.PurchaseList)
        alertMessage = findViewById(R.id.alertMessage)
         userName = findViewById(R.id.userName)



        // If you want to logout currrent account click logoutImage icon
        logoutImage.setOnClickListener{
            // If click the logout icon logOutFunction function will call
            logOutFunction()
        }


         sharedPreferences = getSharedPreferences("Kcube_User", MODE_PRIVATE)
        val userType = sharedPreferences.getInt("user_type", 0)
        val empName = sharedPreferences.getString("emp_name","")
        if (empName != null) {
            // welcome greeting to the staff
            userName.text =  "Welcome $empName "
        }

        readPublishedSnack()

    }

    private fun logOutFunction() {
        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setTitle("Logout")
        alertDialogBuilder.setMessage("Are you sure you want to log out?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            // Handle logout logic here if "yes" clicked
            sharedPreferences = getSharedPreferences("Kcube_User", MODE_PRIVATE)
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

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()
        readPublishedSnack()

    }
    private fun readPublishedSnack() {

         alertMessage.visibility = View.GONE
        // willTakeLaterList function for how many take will take later option and their names
        willTakeLaterList()

        // postedSnacksCountAndName function for how many post snacks which snack posted how many times and their names
//        postedSnacksCountAndName()
        postedSnacksMasterSnacksCountAndName()
    }

    private fun postedSnacksMasterSnacksCountAndName() {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val yesterdayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        postedSnacksMaster.whereEqualTo("posted_date",currentDate)
            .get().addOnSuccessListener { documentSnapshot ->
             if (documentSnapshot!= null){
                 for (item in documentSnapshot){
                     val snack= item.get("snack") as String
                     val empName =item.get("emp_name") as String
                     if (snack != null && empName != null) {
                         if (itemMap.containsKey(snack)) {
                             // Handle if postedSnacks present in itemMap
                             val existingItem = itemMap[snack]

                             //  count how many times snacks posted
                             var existingSnackCount = existingItem?.countOrSnack?.toInt()?.plus(1)
                             existingItem?.countOrSnack = existingSnackCount.toString()
                             if (existingSnackCount!! > 0){
                                 // Handle if snack count more then 0

                                 if (existingItem?.employeeList == null){
                                     // Handle store empNam employeeList
                                     existingItem?.employeeList = mutableListOf(empName)
                                 }
                                 else{
                                     // add employeeName in employeeList
                                     existingItem?.employeeList!!.add(empName)
                                 }
                             }
                         } else {
                             // Map postedSnacks , snack Count , employeeList
                             itemMap[snack] = SnackCardModal(snack, "1", mutableListOf(empName))
                         }
                     }

                 }

                 adapter = SnackListAdapter(this , itemMap)
                 PurchaseList.setAdapter(adapter)

             }else{
                 // documentSnapShot size 0 or less then 0
                 alertMessage.visibility = View.VISIBLE
                 alertMessage.text = "No Snack Found"
             }

            }
            .addOnFailureListener { exception ->
                // handle posted_date not EqualTo yesterdayDate
                Toast.makeText(baseContext, "Failed to fetch published snacks: $exception", Toast.LENGTH_SHORT).show()
            }
}


    private fun postedSnacksCountAndName() {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
//
//        // check posted_date EqualTo yesterdayDate
//        val postedSnacksDocument = postList.whereEqualTo("posted_date", yesterdayDate)
//        postedSnacksDocument.get()
//            .addOnSuccessListener { documentSnapshot ->
//                // handle posted_date EqualTo yesterdayDate
//                if(documentSnapshot.size() > 0){
//                    for (item in documentSnapshot){
//                        // Fetch emp_name and snack
//                        val postedSnacks = item.get("snack") as String
//                        val employeeName = item.get("emp_name") as String
//                        if (itemMap.containsKey(postedSnacks)) {
//                            // Handle if postedSnacks present in itemMap
//                            val existingItem = itemMap[postedSnacks]
//
//                            //  count how many times snacks posted
//                            var existingSnackCount = existingItem?.countOrSnack?.toInt()?.plus(1)
//                            existingItem?.countOrSnack = existingSnackCount.toString()
//                            if (existingSnackCount!! > 0){
//                                // Handle if snack count more then 0
//
//                                if (existingItem?.employeeList == null){
//                                    // Handle store empNam employeeList
//                                    existingItem?.employeeList = mutableListOf(employeeName)
//                                }
//                                else{
//                                    // add employeeName in employeeList
//                                    existingItem?.employeeList!!.add(employeeName)
//                                }
//                            }
//                        } else {
//                            // Map postedSnacks , snack Count , employeeList
//                            itemMap[postedSnacks] = SnackCardModal(postedSnacks, "1", mutableListOf(employeeName))
//                        }
//                    }
//
//                    adapter = SnackListAdapter(this , itemMap)
//                    PurchaseList.setAdapter(adapter)
//
//                }else{
//                    // documentSnapShot size 0 or less then 0
//                    alertMessage.visibility = View.VISIBLE
//                    alertMessage.text = "No Snack Found"
//                }
//
//            }
//            .addOnFailureListener { exception ->
//                // handle posted_date not EqualTo yesterdayDate
//                Toast.makeText(baseContext, "Failed to fetch published snacks: $exception", Toast.LENGTH_SHORT).show()
//            }
    }

    private fun willTakeLaterList() {
        // Filter which emp takeLater value 1 from the firestore collection
        val  employeeListDocument = employee_list.whereEqualTo("takeLater" ,1)
        employeeListDocument.get()
            .addOnSuccessListener { querySnapshot ->
                // Handle how many emp take takeLater count
                var empCount = querySnapshot.documents.size

                var takeLaterEmployees = mutableListOf<String>()
                for (document in querySnapshot.documents) {
                    val employeeName = document.get("emp_name") as String
                    // Store took takeLater empNAme
                    takeLaterEmployees.add(employeeName)
                }
                // Store will Take Later , empCount , who took takeLater that empName
                itemMap["will Take Later"] = SnackCardModal("will Take Later", empCount.toString(), takeLaterEmployees)

            }.addOnFailureListener { exception ->
                // Handle Failure exception
                Toast.makeText(baseContext, "Failed to fetch published snacks: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
  }
}
data class SnackCardModal(
    var snackOrEmpName: String,
    var countOrSnack: String,
    var employeeList : MutableList<String>
)