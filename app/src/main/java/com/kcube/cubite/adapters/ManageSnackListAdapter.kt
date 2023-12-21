package com.kcube.cubite.adapters


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.kcube.cubite.activities.FloatingDialogActivity
import com.kcube.cubite.R
import com.kcube.cubite.activities.editableList
import java.util.Locale

class ManageSnackListAdapter(private val items: ArrayList<editableList>,
                             private val context: Context
                             ):RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private val db = FirebaseFirestore.getInstance()
    private val SnacksMasterBox = db.collection("SnacksMasterBox")
    private var isButtonClicked = false
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var snackItem : TextView
        var editButton : ImageView = itemView.findViewById(R.id.editButton)
        var deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)

        init {
            snackItem  = itemView.findViewById(R.id.snackItem)
            editButton = itemView.findViewById(R.id.editButton)
            deleteButton= itemView.findViewById(R.id.deleteButton)

        }
    }

    private fun showPublishConfirmationDialog(position: Int, snack: String) {
        val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(context)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setMessage("Are you sure you want to Delete snacks?")
        alertDialogBuilder.setPositiveButton("Delete") { _, _ ->
            // Handle If click "Delete" delete Button
            deleteSnack(position ,snack)
        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            // Handle If click "Cancel"
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun deleteSnack(position: Int, snack: String) {
        // Delete snack
        var snackToDelete = items[position]
//        items.remove(snackToDelete)
//        notifyDataSetChanged()
        SnacksMasterBox
            .whereEqualTo("name",snack).get()
            .addOnSuccessListener {dataResults->
                if(dataResults.size() > 0){
                    val documentSnapshot = dataResults.documents[0]
                    documentSnapshot.reference.delete()
                        .addOnSuccessListener {
                            // Successfully deleted from Firestore
                            items.remove(snackToDelete) // Remove from the local snacksList
                            notifyDataSetChanged() // Notify the adapter
                            Toast.makeText(context, "$snack deleted", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            // Handle deletion failure
                            Toast.makeText(context, "Failed to delete $exception", Toast.LENGTH_SHORT).show()
                        }
//                for(snacks in dataResults){
//                val snack = snacks.get("snack") as String;
//                val price = snacks.get("price") as String
//                snacksList.add(SnackSpinner(snack,price))
//                }
                }
            }.addOnFailureListener {exception->
                Toast.makeText(context, "Failed to delete $exception", Toast.LENGTH_SHORT).show()
            }

        SnacksMasterBox.document()
            .update("snack", FieldValue.arrayRemove(snackToDelete))
            .addOnSuccessListener {
                // Successfully deleted from Firestore
                items.remove(snackToDelete) // Remove from the local snacksList
                notifyDataSetChanged() // Notify the adapter
                Toast.makeText(context, "$snack deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                // Handle deletion failure

            }
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val View = LayoutInflater.from(parent.context).inflate(R.layout.manage_snacklist,parent, false)
        return ViewHolder(View)
    }

    override fun getItemCount(): Int {
        Log.d("Adapter", "Item count: ${items.size}")
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val editableList = items[position]
        (holder as ViewHolder).snackItem.text = " ${editableList.snack} ₹ ${editableList.price}"
        Log.d("ItemOrder", "Item at position $position: ${editableList.snack}")
        //Add on OnclickListener for adding new item
        holder.editButton.setOnClickListener {
            showEditConfirmationDialog(position,editableList.snack ,editableList.price)
        }


        // Add an OnClickListener to the deleteButton
        holder.deleteButton.setOnClickListener {
            showPublishConfirmationDialog(position,editableList.snack)
//            val position = adapterPosition
//            if (position != RecyclerView.NO_POSITION) {
//            }
        }
    }

    private fun showEditConfirmationDialog(position: Int, snack: String, price: String) {
        val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(context)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setMessage("Are you sure you want to Edit snacks?")
        alertDialogBuilder.setPositiveButton("Edit") { _, _ ->
            // Handle If click "Edit" Edit Icon
            val intent = Intent(context, FloatingDialogActivity::class.java)
            isButtonClicked = true
            intent.putExtra("isButtonClicked", isButtonClicked)
            intent.putExtra("snack", snack)
            context.startActivity(intent)
            (context as Activity).finish()
            editSnack(position ,snack,price)

        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            // Handle If click "Cancel"
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun editSnack(position: Int, snack: String, price: String) {
        var snackToEdit = items[position]
    }
    fun isButtonClicked(): Boolean {
        return isButtonClicked
    }
}