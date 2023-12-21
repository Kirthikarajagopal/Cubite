 package com.kcube.cubite.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.kcube.cubite.R
import com.kcube.cubite.adapters.RecyclerAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Suppress("DEPRECATION")
class FloatingDialogActivity : AppCompatActivity() {
     lateinit var snackName :EditText
     lateinit var snackPrice : EditText
     lateinit var combinationSwitch :Switch
     lateinit var SnackLimit :EditText
     lateinit var enterSnack:EditText
     lateinit var addButton: Button
     private var category_exist: Boolean = false
     private lateinit var snackList: MutableList<String>
     private var snackListItem : MutableList<ListItem> = mutableListOf()
     private val context: Context = this
     lateinit var addSnackList: RecyclerView
     lateinit var publishSnacks:Button
     private val calendar = Calendar.getInstance()
     private val objectData = FirebaseFirestore.getInstance()
     private val storeSnackList = objectData.collection("SnacksMasterBox")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_floating_dialog)

        getSupportActionBar()?.hide()

        snackName = findViewById(R.id.snackName)
        snackPrice= findViewById(R.id.snackPrice)
        combinationSwitch = findViewById(R.id.combinationSwitch)
        SnackLimit = findViewById(R.id.SnackLimit)
        enterSnack = findViewById(R.id.enterSnack)
        addButton = findViewById(R.id.addButton)
        addSnackList = findViewById(R.id.addSnackList)
        publishSnacks = findViewById(R.id.publishSnacks)

        SnackLimit.visibility = View.INVISIBLE
        enterSnack.visibility = View.INVISIBLE
        addButton.visibility = View.INVISIBLE
        addSnackList.visibility = View.INVISIBLE



        combinationSwitch.setOnCheckedChangeListener { _, isChecked ->
            buttonChecked(isChecked)
        }
        addButton.setOnClickListener {
            addSnacksList()
            enterSnack.text.clear()
        }

        publishSnacks.setOnClickListener {
            val checkEmptySpace = snackName.text.toString() + snackPrice.text.toString() + SnackLimit.text.toString()+ enterSnack.text.toString()
            if (checkEmptySpace.isNotEmpty()){
                showPublishConfirmationDialog()
            }else{
                Toast.makeText(applicationContext, "Please Fill the columns ", Toast.LENGTH_SHORT).show()
            }


        }
    }

    private fun showPublishConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setMessage("Are you sure you want to publish snacks?")
        alertDialogBuilder.setPositiveButton("PUBLISH") { _, _ ->
            // Handle the "Add" button click
            publishSnackList()

        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            // Handle the "Cancel" button click or dismiss the dialog
            dialog.dismiss()
            finish()

        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun addSnacksList() {
         category_exist = true
         var snack = enterSnack.text.toString()

         if (snack.isEmpty()){
             Toast.makeText(applicationContext, "Please enter Your snack", Toast.LENGTH_SHORT).show()
         }else{
             //            val snackListItem = mutableListOf<ListItem>()
             if (!snackListItem.any { it.snacks == snack }) {
                 snackListItem.add(ListItem(snack))
                 addSnackList.layoutManager = LinearLayoutManager(this)
                 val adapter = RecyclerAdapter(snackListItem as ArrayList<ListItem> , context)
                 addSnackList.adapter = adapter
                 adapter.notifyDataSetChanged()

             }
             else{
                 Toast.makeText(applicationContext, "$snack already exist", Toast.LENGTH_SHORT).show()
             }
         }

     }

    private fun buttonChecked(isChecked:Boolean) {
         if (isChecked){
             // Handle if the button clicked
             SnackLimit.visibility = View.VISIBLE
             enterSnack.visibility = View.VISIBLE
             addButton.visibility = View.VISIBLE
             addSnackList.visibility = View.VISIBLE
         }
         else{
             // Handle if the button not clicked
             SnackLimit.visibility = View.INVISIBLE
             enterSnack.visibility = View.INVISIBLE
             addButton.visibility = View.INVISIBLE
             addSnackList.visibility = View.INVISIBLE
         }
    }

     private fun publishSnackList() {
         // Create SnacksMasterBox store myObject credential
         calendar.add(Calendar.DATE, 0)
         val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

         var name = snackName.text.toString()
         var price = snackPrice.text.toString()

         val lowercaseName = name.lowercase(Locale.getDefault())

         val myObject = hashMapOf(
             "name" to name,
             "price" to price,
             "category" to category_exist,
             "limit" to SnackLimit.text.toString(),
             "created_date" to currentDate,
             "categories" to snackListItem.map { it.snacks }
         )
         storeSnackList
            .get()
             .addOnSuccessListener { querySnapshot  ->
                 val list = mutableListOf<String>()
                  var duplicateValue = false
                     for (document in querySnapshot ){
                         val snack = document.get("name") as String
                         val lowerCaseSnack = snack.lowercase(Locale.getDefault())
                         list.add(lowerCaseSnack)
                     }


                 if (list.contains(lowercaseName) ){
                     Toast.makeText(
                         applicationContext,
                         "Snacks Already Exist",
                         Toast.LENGTH_SHORT
                     ).show()

                 }else{
                     for (index in list.indices) {
                         // Replace white space with an empty string for each element in the list
                         list[index] = list[index].replace(" ", "")
                     }
                     var lowercaseNameWithoutSpace = lowercaseName.replace(" ", "")
                     if (list.contains(lowercaseNameWithoutSpace) ){
                         Toast.makeText(applicationContext, "Snacks Already Exist", Toast.LENGTH_SHORT).show()
                     } else{
                         val isButtonClicked = intent.getBooleanExtra("isButtonClicked", false)
                         val snack = intent.getStringExtra("snack") ?: "default_snack_value"
                         val sharedPref = getSharedPreferences("Kcube_User", Context.MODE_PRIVATE)
                         val editor = sharedPref.edit()
                         editor.putBoolean("isButtonClicked", isButtonClicked)
                         editor.putString("snack", snack)
                         editor.apply()

                        // Now checkEditBtnClicked contains the stored boolean value
                         if (isButtonClicked) {
                             val intent = Intent(this, ManageSnacks::class.java)
                             startActivity(intent)
                             finish()
                             editSnacksMasterBox(snack,myObject)
                             // when checkEditBtnClicked is true
                             Toast.makeText(applicationContext, "Snacks edited", Toast.LENGTH_SHORT).show()
                         }else{
                             storeSnackList.add(myObject)
                                 .addOnSuccessListener {
                                     // Handle Success event
                                     val intent = Intent(this, ManageSnacks::class.java)
                                     startActivity(intent)
                                     finish()
                                     Toast.makeText(applicationContext, "Snacks Added Successfully", Toast.LENGTH_SHORT).show()
                                 }.addOnFailureListener {
                                     Toast.makeText(applicationContext, "Snacks Not Added to the DataBase", Toast.LENGTH_SHORT).show()
                             }
                         }
                     }
                 }
             }
             .addOnFailureListener { e ->
                 Toast.makeText(applicationContext, "Snacks Not Stored Properly", Toast.LENGTH_SHORT).show()
             }
     }

    private fun editSnacksMasterBox(snack: String, myObject: HashMap<String, Any>) {
        storeSnackList
            .whereEqualTo("name",snack).get()
            .addOnSuccessListener {dataResults->
                if(dataResults.size() > 0){
//                    var documentIdToUpdate: String? = null
                    for (documentSnapshot in dataResults.documents) {
                        val item = documentSnapshot.get("name") as String

                        // Retrieve the document ID
                        var documentIdToUpdate = documentSnapshot.id
                        if (documentIdToUpdate != null) {
                            storeSnackList.document(documentIdToUpdate)
                                .set(myObject)
                                .addOnSuccessListener {
//                                    Toast.makeText(context, "$snack Updated", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Failed to update $snack: $e", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
            }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                super.onBackPressed() // This will exit the app
                val intent = Intent(this, ManageSnacks::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

}
 data class ListItem(val snacks: String)