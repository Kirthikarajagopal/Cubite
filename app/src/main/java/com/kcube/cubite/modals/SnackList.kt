package com.kcube.cubite.modals

import com.google.firebase.firestore.FirebaseFirestore

class SnackList {
    private val db = FirebaseFirestore.getInstance()
    private val snackCollection = db.collection("snacks_list")


    fun checksnackItem(snacks: String, callback: (Boolean) -> Unit) {
        snackCollection.document(snacks)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result
                    val exists = documents?.exists() ?: false
                    callback(exists)
                } else {
                    // Handle error
                    callback(false)
                }
            }
    }
}
