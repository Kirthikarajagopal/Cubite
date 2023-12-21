package com.kcube.cubite.com.kcube.cubite

import com.google.firebase.firestore.FirebaseFirestore


class PublishFireStore {
    private val db = FirebaseFirestore.getInstance()
    private val Snacks = db.collection("snacks_list")

    fun DisplaySnack (snacks: String, callback: (Boolean) -> Unit){
        Snacks.document(snacks)
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