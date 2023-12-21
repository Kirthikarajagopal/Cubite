package com.kcube.cubite.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.kcube.cubite.activities.ListItem
import com.kcube.cubite.R



class RecyclerAdapter(private val items: ArrayList<ListItem>,
                      private val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

     inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

         var mTitle : TextView = itemView.findViewById(R.id.mTitle)
         var deleteButton : ImageView = itemView.findViewById(R.id.deleteButton)

         init {
             // Add an OnClickListener to the deleteButton
             deleteButton.setOnClickListener {
                 val position = adapterPosition
                 if (position != RecyclerView.NO_POSITION) {
                     showPublishConfirmationDialog(position)
                 }
             }
         }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Handle  sets the views to display the items
        val View = LayoutInflater.from(parent.context).inflate(R.layout.add_snack_dialog,parent, false)
        return ViewHolder(View)
    }

    override fun getItemCount(): Int {
        //returns the count of items present in the list
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Handle bind the list items to our widgets TextView
        val ListItem = items[position]
        (holder as ViewHolder).mTitle.text = ListItem.snacks
    }

    private fun deleteSnack(position: Int) {
        // Delete snack
        var snackToDelete = items[position]
        items.remove(snackToDelete)
        notifyDataSetChanged()
    }

    private fun showPublishConfirmationDialog(position:Int) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setMessage("Are you sure you want to Delete snacks?")
        alertDialogBuilder.setPositiveButton("Delete") { _, _ ->
            // Handle If click "Delete" delete Button
            deleteSnack(position)
        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            // Handle If click "Cancel"
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}