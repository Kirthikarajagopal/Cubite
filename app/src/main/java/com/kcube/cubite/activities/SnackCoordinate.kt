package com.kcube.cubite.activities


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.io.IOException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kcube.cubite.adapters.EmployeeAdapter
import com.kcube.cubite.modals.EmployeeModel
import com.kcube.cubite.R


@Suppress("DEPRECATION")
 class SnackCoordinate : AppCompatActivity() {

    private val employee = arrayOf(
        "Arun Krishnan K",
        "Shivaprakash T",
        "Christopher S",
        "Saravana Pradeep P",
        "Harikrishnan V",
        "Ramya Maheswari M A",
        "Arunachalam P",
        "Kiran M",
        "Rajesh V",
        "Vijay M",
        "Rithicka K",
        "Deepak R S",
        "Ajith Kumar B",
        "Saraswathi PV",
        "Yuvaraj M",
        "Harshavarthini M",
        "Bhuvana K",
        "Koteeswaran N",
        "John Philip Bosco",
        "Dinesh Dass S",
        "Achutharaman S",
        "Satish Murugesan",
        "Kebaroy Johnraj B",
        "Sitansu Pattnaik",
        "Deepa S",
        "Kumaran Narayanaswamy",
        "Thenkabilarasu T ",
        "Bravin S ",
        "Haritha M ",
        "Kamal V",
        "Kiruthika R" ,"Maharajan K" ,
        "Suresh G"
    )
     private lateinit var sharedPreferences: SharedPreferences
     private lateinit var CurrentCoordinate: TextView
     private lateinit var display : TextView
     private lateinit var Snack_Button :Button
     private lateinit var logoutImage : ImageView
     private lateinit var selectedValueToRemove:String
     private val enteredValues = mutableListOf<String>()
     private lateinit var autoCompleteTextView: AutoCompleteTextView
     private lateinit var selectedEmployeeName:String
     private lateinit var selectedEmpId:String
     private lateinit var firestore :FirebaseFirestore
    lateinit var emp_list: EmployeeModel;
     var oldCoordinator : Any = 0;
     private var itemSelected = false
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snack_coordinate)

        getSupportActionBar()?.hide()

        firestore = FirebaseFirestore.getInstance()

        CurrentCoordinate = findViewById(R.id.CurrentCoordinate)
        display = findViewById(R.id.display)
        logoutImage = findViewById(R.id.uilogoutImage)
        autoCompleteTextView = findViewById(R.id.searchBar)
         Snack_Button = findViewById(R.id.Snack_Button)


        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, employee)
        val searchableEditView = autoCompleteTextView.text.toString().trim()
        autoCompleteTextView.threshold = 1
        autoCompleteTextView.setAdapter(adapter)


        logoutImage.setOnClickListener{
            logoutFunction()
        }

        try {

            getOldSnackscordinator(CurrentCoordinate)

        var fileInString: String =
            applicationContext.assets.open("employee_list.json").bufferedReader()
                .use { it.readText() }
        emp_list = Gson().fromJson(fileInString, EmployeeModel::class.java)


        var employeeAdapter: EmployeeAdapter = EmployeeAdapter(this, emp_list.employess)

        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedValue = adapter.getItem(position)
            for (employee in emp_list.employess) {
                if (employee.empName == selectedValue) {
//                    id = employee.empId?.toString()
                    itemSelected = true
                    selectedEmpId = employee.empId.toString()
                    selectedEmployeeName = employee.empName.toString();
                    showToast("Employee ID for '$selectedEmpId': $selectedEmployeeName")
                    break
                }
            }
        }




            Snack_Button.setOnClickListener {
                // Set an OnDismissListener for the AutoCompleteTextView to detect when the dropdown is dismissed
//                autoCompleteTextView.setOnDismissListener {
                    // If no item was selected, show a Toast message
                    if (!itemSelected) {
                        Toast.makeText(this, "Please Select Employee Name", Toast.LENGTH_SHORT).show()
                    }else{
                        if (selectedEmpId != null && selectedEmployeeName != null) {
                            showPublishConfirmationDialog()
                            autoCompleteTextView.text = null
                        }
                        else{
                            Toast.makeText(this, "Please select an item first", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Reset the flag for the next interaction

//                }
                itemSelected = false
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        }

     private fun logoutFunction() {
         // Create an AlertDialog.Builder
         val alertDialogBuilder = AlertDialog.Builder(this)
         alertDialogBuilder.setTitle("Logout")
         alertDialogBuilder.setMessage("Are you sure you want to log out?")
         alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
             // Handle logout logic here
             val sharePrefer = getSharedPreferences("Kcube_User", Context.MODE_PRIVATE)
             val editor: SharedPreferences.Editor = sharePrefer.edit()
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

     private fun updateSnacksCoordinator(){
         // Here change new snack user type = 2 old snack coordinate user type = 3
         if (selectedEmpId != null && selectedEmployeeName != null) {
             // Display employee ID and name in CurrentCoordinate
             val docRef = firestore.collection("employees_list").document(selectedEmpId)
             docRef.get()
                 .addOnSuccessListener { documentSnapshot ->
                     if (documentSnapshot.exists()) {
                         val empName = documentSnapshot.getString("emp_name")
                         CurrentCoordinate.text = "Employee ID: $selectedEmpId\nEmployee Name: $empName"

                        val employeeDocument = firestore.collection("employees_list").document(selectedEmpId)

                         // Modify the user_type field in the document
                         val updateData = mapOf("user_type" to 2) // Update user_type to 3
                         employeeDocument.set(updateData, SetOptions.merge())
                             .addOnSuccessListener {
                                  showToast("Snacks Coordinator updated successfully")
                                 autoCompleteTextView.text = null

                             }
                             .addOnFailureListener { exception ->
                                 showToast("Failed to update user type")
                                 exception.printStackTrace()
                             }
                     } else {
                         showToast("Document not found")
                     }
                 }
                 .addOnFailureListener { exception ->
                     showToast("Failed to fetch employee data")
                     exception.printStackTrace()
                 }

             val employeeDocument = firestore.collection("employees_list").document(
                 oldCoordinator.toString()
             )

             // Modify the user_type field in the document
             val updateData = mapOf("user_type" to 3) // Update user_type to 2
             employeeDocument.set(updateData, SetOptions.merge())
                 .addOnSuccessListener { }
                 .addOnFailureListener { exception ->
                     showToast("Failed to update user type")
                     exception.printStackTrace()
             }
         }
     }

     private fun showPublishConfirmationDialog() {
         val alertDialogBuilder = AlertDialog.Builder(this)
         alertDialogBuilder.setMessage("Are you sure you want to Update Coordinate?")
         alertDialogBuilder.setPositiveButton("Select") { _, _ ->

             // Handle the "Select" button click or process the updateSnacksCoordinator function
             updateSnacksCoordinator()
         }
         alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
             // Handle the "Cancel" button click or dismiss the dialog
             dialog.dismiss()
         }

         val alertDialog = alertDialogBuilder.create()
         alertDialog.show()
     }

     @SuppressLint("SetTextI18n")
     private fun getOldSnackscordinator(CurrentCoordinate: TextView) {
         val firestore = FirebaseFirestore.getInstance()
         val employeeCollection = firestore.collection("employees_list")

         // Query Firestore for employees with user_type = 2
         employeeCollection
             .whereEqualTo("user_type", 2)
             .get()
             .addOnSuccessListener { querySnapshot ->

                 for (document in querySnapshot) {
                     val empName = document.getString("emp_name")
                      oldCoordinator = document.get("emp_id")!!
                     CurrentCoordinate.text = "Employee ID: $oldCoordinator\nEmployee Name: $empName"
                 }
             }
             .addOnFailureListener { exception ->
                 // Handle any errors here
                 showToast("Failed to fetch employee data")
                 exception.printStackTrace()
             }
     }

     private fun showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

     override fun onCreateOptionsMenu(menu: Menu): Boolean {
         menuInflater.inflate(R.menu.menu_main, menu)
         return true
     }
 }