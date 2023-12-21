package com.kcube.cubite.com.kcube.cubite



import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SpinnerAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.kcube.cubite.R
import com.kcube.cubite.activities.SnackSpinner


class SnacksAdapter(
    private val snacksList: MutableList<SnackSpinner>,
    private val deleteItem: Boolean,
    private val deleteButton: Boolean,
    private val context: Context
    ) : RecyclerView.Adapter<SnacksAdapter.SnackViewHolder>(), SpinnerAdapter {

    private val db = FirebaseFirestore.getInstance()
    private val publishSnacksMasterBox = db.collection("publishSnacksMasterBox")
    private lateinit var sharedPreferences: SharedPreferences

    inner class SnackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var UserSnack:TextView
        var DeleteButton :Button
        init {
            UserSnack = itemView.findViewById(R.id.mTitle)
            DeleteButton =  itemView.findViewById(R.id.deleteButton)
            sharedPreferences = context.getSharedPreferences("Cubite" ,Context.MODE_PRIVATE)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnackViewHolder {
        // Handle  sets the views to display the items
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.snack_itemlist, parent, false)
        return SnackViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SnackViewHolder, position: Int) {
        // Handle bind the list items to our widgets TextView
        val snack = snacksList[position]
        holder.UserSnack.text = " ${snack.item} ₹ ${snack.price}"
        if (deleteButton ) {
            holder.DeleteButton.visibility = View.GONE
        } else {
            holder.DeleteButton.visibility = View.VISIBLE
            holder.DeleteButton.setOnClickListener {
                // OnClickListener to the deleteButton
                showPublishConfirmationDialog(position,snack.item)
            }
        }
    }

    private fun showPublishConfirmationDialog(position: Int, item: String) {
        val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(context)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setMessage("Are you sure you want to Delete snacks?")
        alertDialogBuilder.setPositiveButton("Delete") { _, _ ->
            // Handle If click "Delete" delete Button
            deletePublishSnack(position,item)
        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            // Handle If click "Cancel"
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    override fun getItemCount(): Int {
        //returns the count of items present in the list
        return snacksList.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun addNewSnackItem(snack : SnackSpinner){
        snacksList.add(snack)
        notifyDataSetChanged()
    }


    private fun deleteSnack(position:Int) {
//        snackCollection.whereEqualTo("snack",snackToDelete).get().addOnSuccessListener {dataResults->
//            if(dataResults.size() > 0){
//                for(snacks in dataResults){
//                val snack:String = snacks.get("snack") as String;
//                val price:Long = snacks.get("price") as Long
//                snacksList.add(SnackSpinner(snack,price.toString()))
//                }
//            }
//
//
//        }.addOnFailureListener {exeception->
//        Log.d("Failed to Read", "Failed to fetch published snacks: $exeception")
//        }
//        snackCollection.document("snacks")
//            .update("Items", FieldValue.arrayRemove(snackToDelete))
//            .addOnSuccessListener {
//                // Successfully deleted from Firestore
//               // snacksList.remove(snackToDelete) // Remove from the local snacksList
//                notifyDataSetChanged() // Notify the adapter
//                Toast.makeText(context, "$snackToDelete deleted", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener { exception ->
//                // Handle deletion failure
//                Toast.makeText(context, "Failed to delete $snackToDelete: $exception", Toast.LENGTH_SHORT).show()
//            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deletePublishSnack(position: Int, item: String) {
        // Handle delete the Snack
        var snackToDelete = snacksList[position]
//        snacksList.remove(snackToDelete)
//        notifyDataSetChanged()
        publishSnacksMasterBox
                .whereEqualTo("snack",item).get()
            .addOnSuccessListener {dataResults->
            if(dataResults.size() > 0){
                val documentSnapshot = dataResults.documents[0]
                documentSnapshot.reference.delete()
                    .addOnSuccessListener {
                        // Successfully deleted from Firestore
                        snacksList.remove(snackToDelete) // Remove from the local snacksList
                        notifyDataSetChanged() // Notify the adapter
                        Toast.makeText(context, "$item deleted", Toast.LENGTH_SHORT).show()
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


        publishSnacksMasterBox.document()
            .update("snack", FieldValue.arrayRemove(snackToDelete))
            .addOnSuccessListener {
                // Successfully deleted from Firestore
                snacksList.remove(snackToDelete) // Remove from the local snacksList
                notifyDataSetChanged() // Notify the adapter
                Toast.makeText(context, "$item deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                // Handle deletion failure

            }
        }

     fun getUpdatedSnacksList(): MutableList<SnackSpinner> {
        return snacksList
    }

    override fun registerDataSetObserver(observer: DataSetObserver?) {
        TODO("Not yet implemented")
    }
    override fun unregisterDataSetObserver(observer: DataSetObserver?) {
        TODO("Not yet implemented")
    }
    override fun getCount(): Int {
        TODO("Not yet implemented")
    }
    override fun getItem(position: Int): Any {
        TODO("Not yet implemented")
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        TODO("Not yet implemented")
    }
    override fun getViewTypeCount(): Int {
        TODO("Not yet implemented")
    }
    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        TODO("Not yet implemented")
    }
}