package com.kcube.cubite.activities


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.kcube.cubite.R
import com.kcube.cubite.adapters.ManageSnackListAdapter
import com.kcube.cubite.modals.SnackList
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Suppress("DEPRECATION")
class ManageSnacks : AppCompatActivity() {

    private val context: Context = this
    private lateinit var additem: FloatingActionButton
    private val snacksList = mutableListOf<SnackSpinner>()
    private lateinit var adapter: ManageSnackListAdapter
    private lateinit var snackListHelper: SnackList
    private lateinit var snackMenu: EditText
    private val db = FirebaseFirestore.getInstance()
    private val snackCollection = db.collection("snacks_list")
    private val snacksMasterBox = db.collection("SnacksMasterBox")
    private lateinit var logoutImage : ImageView
    private lateinit var recyclerView :RecyclerView
    private val calendar = Calendar.getInstance()
    private var snackListItem : MutableList<editableList> = mutableListOf()
    private var checkEditBtnClicked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_alert)

        supportActionBar?.hide()

        additem = findViewById(R.id.addingBtn)
        recyclerView = findViewById(R.id.myRecycler)
        logoutImage = findViewById(R.id.uilogoutImage)

        logoutImage.setOnClickListener {
            logoutFunction()
        }

        snackListHelper = SnackList()


//        recyclerView.layoutManager = LinearLayoutManager(this)



        readSnackMasterBoxList()

//        adapter = SnacksAdapter(snacksList,deleteItem = false,deleteButton = false,context)
//        recyclerView.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ManageSnackListAdapter(snackListItem as ArrayList<editableList> , context)
        recyclerView.adapter = adapter

        // Attach the ZeroSpacingItemDecoration to the RecyclerView
//        val zeroSpacingItemDecoration = ZeroSpacingItemDecoration()
//        recyclerView.addItemDecoration(zeroSpacingItemDecoration)
//        readData()

        additem.setOnClickListener {
//            showCustomDialog()
            val intent = Intent(this, FloatingDialogActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun readSnackMasterBoxList() {
        calendar.add(Calendar.DATE, -1)
        val yesterdayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        snacksMasterBox.get()
            .addOnSuccessListener { querySnapShot ->
                for (item in querySnapShot){
                    val snack= item.get("name") as String
                    val price = item.get("price") as String
                    snackListItem.add(editableList(snack,price))
                    adapter.notifyDataSetChanged()
                }
//                adapter.notifyDataSetChanged()
//                findViewById<RecyclerView>(R.id.myRecycler).adapter = adapter
//                Toast.makeText(baseContext, "snack Added", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(baseContext, "Snacks not Added", Toast.LENGTH_SHORT).show()
            }
    }

    private fun logoutFunction() {
        // Create an AlertDialog.Builder
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Logout")
        alertDialogBuilder.setMessage("Are you sure you want to log out?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            // Handle logout logic here
            val sharePreferences = getSharedPreferences("Kcube_User", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharePreferences.edit()
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

    private fun readData() {
        val snacksDocumentId = "snacks"
        snackCollection.document(snacksDocumentId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Handle documentSnapshot exists from collection
                    val snacksData = documentSnapshot.data // Retrieve the data as a Map
                    val itemsList = snacksData?.get("Items") as? List<String>
                    if (itemsList != null) {
                        // Here you can do something with the itemsList, like displaying it or using it in some way
                        // For example, you can update your adapter's data with this list
                        snacksList.clear()
//                        adapter.notifyDataSetChanged()
                    }
                } else {
                    // Handel the e
                    Toast.makeText(baseContext, "Snacks document does not exist", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Toast.makeText(baseContext, "Failed to read snacks data: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showCustomDialog() {

        val dialog = Dialog(context)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)
        snackMenu = dialog.findViewById(R.id.snackMenu)


        // Set an InputFilter to restrict special characters, numbers, and smileys
        val filter = InputFilter { source, _, _, _, _, _ ->
            val regex = "[^A-Za-z\\s]+".toRegex() // Allow only letters and spaces
            if (source.toString().matches(regex)) {
                ""
            } else {
                null
            }
        }

        snackMenu.filters = arrayOf(filter)

        val addSnackButton = dialog.findViewById<Button>(R.id.addSnackButton)
        val clearButton = dialog.findViewById<Button>(R.id.clearButton)
        additem.setOnClickListener {

            dialog.show()
            snackMenu.error = null
        }


        addSnackButton.setOnClickListener {

            val newSnack = snackMenu.text.toString().trim()

            if (newSnack.isNotEmpty()) {
                addData(newSnack)

                val added = Dialog(context)
                added.setContentView(R.layout.snack_itemlist)
                //  adapter.addNewSnackItem(newSnack)

                snackListHelper.checksnackItem(newSnack) { exists ->
                    snackMenu.text.clear()
                    dialog.dismiss()
                }
            }
            else{
                snackMenu.error = "Please enter a snack"
            }
        }
        clearButton.setOnClickListener {
            snackMenu.text.clear()
            dialog.dismiss()
        }
    }
    private fun addData(newSnack: String) {
//        val snacksDocumentId = "snacks"
//        snackCollection.document(snacksDocumentId)
//            .get()
//            .addOnSuccessListener { documentSnapshot ->
//                if (documentSnapshot.exists()) {
//                    val snacksData = documentSnapshot.data // Retrieve the data as a Map
//                    val itemsList = snacksData?.get("Items") as? MutableList<String>
//                    if (itemsList != null) {
//                        val isSnacksExits = itemsList.any { it.equals(newSnack, ignoreCase = true) }
//                        if (!isSnacksExits) {
//                            itemsList.add(newSnack) // Add the user-entered snack to the list
//                            // Update Firestore with the modified list
//                            snackCollection.document(snacksDocumentId)
//                                .update("Items", itemsList)
//                                .addOnSuccessListener {
//                                    // Update the local snacksList
//                                    snacksList.clear()
////                                    adapter.notifyDataSetChanged()
//                                    Toast.makeText(baseContext, "$newSnack Added", Toast.LENGTH_SHORT).show()
//                                }
//                                .addOnFailureListener { exception ->
//                                    // Handle any errors during document update
//                                    Toast.makeText(baseContext, "Failed to add $newSnack: $exception", Toast.LENGTH_SHORT).show()
//                                }
//                        } else {
////                            adapter.notifyDataSetChanged()
//                            Toast.makeText(baseContext, "$newSnack already exists", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                } else {
//                    Toast.makeText(baseContext, "Error fetching snack data", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .addOnFailureListener { exception ->
//                // Handle any errors
//                Toast.makeText(baseContext, "Invalid $exception", Toast.LENGTH_SHORT).show()
//            }

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

}
data class editableList(val snack:String,val price:String)