package com.apps.skimani.afyafood.adapters

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.apps.skimani.afyafood.R
import com.apps.skimani.afyafood.models.BrandedList



class InstantFoodSearchAdapter(
    private val mContext: Context,
    private val mLayoutResourceId: Int,
    departments: List<BrandedList>
) :
    ArrayAdapter<BrandedList>(mContext, mLayoutResourceId, departments) {
    private val mDepartments: MutableList<BrandedList>
    private val mDepartmentsAll: List<BrandedList>
    override fun getCount(): Int {
        return mDepartments.size
    }

    override fun getItem(position: Int): BrandedList {
        return mDepartments[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        try {
            if (convertView == null) {
                val inflater = (mContext as Activity).layoutInflater
                convertView = inflater.inflate(mLayoutResourceId, parent, false)
            }
            val department: BrandedList = getItem(position)
            val name = convertView!!.findViewById<View>(R.id.name) as TextView
            name.setText(department.brandNameItemName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return convertView!!
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun convertResultToString(resultValue: Any): String {
                return (resultValue as BrandedList).brandNameItemName
            }

            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filterResults = FilterResults()
                val departmentsSuggestion: MutableList<BrandedList> = ArrayList()
                if (constraint != null) {
                    for (department in mDepartmentsAll) {
                        if (department.brandNameItemName.toLowerCase()
                                .startsWith(constraint.toString().toLowerCase())
                        ) {
                            departmentsSuggestion.add(department)
                        }
                    }
                    filterResults.values = departmentsSuggestion
                    filterResults.count = departmentsSuggestion.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                mDepartments.clear()
                if (results != null && results.count > 0) {
                    // avoids unchecked cast warning when using mDepartments.addAll((ArrayList<Department>) results.values);
                    for (`object` in results.values as List<*>) {
                        if (`object` is BrandedList) {
                            mDepartments.add(`object` as BrandedList)
                        }
                    }
                    notifyDataSetChanged()
                } else if (constraint == null) {
                    // no filter, add entire original list back in
                    mDepartments.addAll(mDepartmentsAll)
                    notifyDataSetInvalidated()
                }
            }
        }
    }

    init {
        mDepartments = ArrayList(departments)
        mDepartmentsAll = ArrayList(departments)
    }
}