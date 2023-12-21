package com.kcube.cubite.modals

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

 class User{
     private val db = FirebaseFirestore.getInstance()
     private val usersCollection = db.collection("employees_list")

     fun checkUsernameAndPassword(Employee_id: String, callback: (DocumentSnapshot) -> Unit) {
         usersCollection.document(Employee_id)
             .get()
             .addOnCompleteListener { task ->
                 if (task.isSuccessful) {
                     val documents = task.result
                     callback(documents)
                 }
                 else {
                     // Handle error
                     //callback(false)
             }
         }
     }
 }
