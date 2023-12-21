package com.kcube.cubite.com.kcube.cubite

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.kcube.cubite.R
import com.kcube.cubite.activities.SnackCardModal

class SnackListAdapter internal constructor(
    private val context: Context,
    private val expandableListDetail: Map< String, SnackCardModal>,
//    private val snackList: List<SnackCardModal>
    ):
    BaseExpandableListAdapter() {
    override fun getGroupCount(): Int {
        return expandableListDetail.size
    }
    override fun getChildrenCount(groupPosition: Int): Int {
        return expandableListDetail[expandableListDetail.keys.elementAt(groupPosition)]?.employeeList?.size
            ?: 0
    }
    override fun getGroup(listposition: Int): Any {
        return expandableListDetail
    }

    override fun getChild(listposition: Int, expandableListPosition: Int): MutableList<String>? {
        return expandableListDetail[expandableListDetail.keys.elementAt(listposition)]?.employeeList
    }

    override fun getGroupId(listposition: Int): Long {
        return listposition.toLong()
    }
    override fun getChildId(listposition: Int, expandableListPosition: Int): Long {
        return expandableListPosition.toLong()
    }
    override fun hasStableIds(): Boolean {
        return false
    }
    @SuppressLint("MissingInflatedId")
    override fun getGroupView(
        listposition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?,
    ): View {

        val convertViews = LayoutInflater.from(context).inflate(R.layout.listitem,null)
        val listTitle = getGroup(listposition)  as MutableMap<String , SnackCardModal>
        var listTitleTextView = convertViews.findViewById<View>(R.id.snackItem) as TextView
        val listTitleTextView2 = convertViews.findViewById<View>(R.id.optionMenu) as ImageView
        listTitleTextView.setTypeface(null,Typeface.BOLD)
        listTitleTextView.text = listTitle[expandableListDetail.keys.elementAt(listposition)]?.snackOrEmpName

        // Adjust this part as needed for your data
        listTitleTextView2.setOnClickListener { v ->

            // Handle the click on the options icon for the parent item
            showPopupMenu(v, listTitle)
            val countText = listTitle[expandableListDetail.keys.elementAt(listposition)]?.countOrSnack
//        listTitleTextView2.setTypeface(null, Typeface.BOLD)
//        listTitleTextView2.text = countText
        }
        return convertViews
    }

    private fun showPopupMenu(v: View?, listTitle: MutableMap<String, SnackCardModal>) {

    }

    override fun getChildView(
        listposition: Int,
        expandableListPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?,
    ): View {
        var convertViews = LayoutInflater.from(context).inflate(R.layout.expandable_listview,null)
        val expandedListText = getChild(listposition,expandableListPosition)
        val expandableListTextView = convertViews.findViewById<TextView>(R.id.expandedListItem)
        expandableListTextView.text = expandedListText?.get(expandableListPosition)
        return convertViews
    }
    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
      return true
    }
}