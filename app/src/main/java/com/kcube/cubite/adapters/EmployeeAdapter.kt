package com.kcube.cubite.adapters





import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
//import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.TextView
import com.kcube.cubite.modals.Employees


class EmployeeAdapter(context: Context, private val employeeList: MutableList<Employees>) :

    ArrayAdapter<Employees>(context, android.R.layout.simple_dropdown_item_1line, employeeList) {
    private var suggestions: MutableList<Employees> = employeeList

    override fun getCount(): Int {
        //returns the count of items present in the list
        return suggestions.size
    }

    override fun getItem(p0: Int): Employees? {
        return suggestions.get(p0)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_dropdown_item_1line, parent, false)

        val employee = getItem(position)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = employee?.empName ?: ""

        if (hasFilter()) {
            // Show employee  when a filter is applied
            textView.text = "${employee?.empName}"
        }
        return view
    }
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val query = constraint.toString().trim()
                    if ( query.isEmpty()) {
                        //Handle Query is empty
                        suggestions = employeeList
                    } else {
                        // Handle Query is Not empty
                        suggestions = employeeList.filter { employee ->

                            employee.empName.toString().contains(query ,ignoreCase = true)

                        } as MutableList<Employees>
                    }
                    filterResults.values = suggestions
                    filterResults.count =  suggestions.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                @Suppress("UNCHECKED_CAST")
                suggestions = (results.values as List<Employees>).toMutableList()

                if (results.count > 0) {
                    // Handle count more then Zero
                    notifyDataSetChanged()
                } else {
                    // Handle count less then Zero
                    notifyDataSetInvalidated()
                }
            }
        }
    }
private fun hasFilter(): Boolean {
  return suggestions.size < employeeList.size
}
}