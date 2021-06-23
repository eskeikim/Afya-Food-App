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
    brandList: List<BrandedList>
) :
    ArrayAdapter<BrandedList>(mContext, mLayoutResourceId, brandList) {
    private val brandedList: MutableList<BrandedList>
    private val brandedListAll: List<BrandedList>
    override fun getCount(): Int {
        return brandedList.size
    }

    override fun getItem(position: Int): BrandedList {
        return brandedList[position]
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
            val brand: BrandedList = getItem(position)
            val name = convertView!!.findViewById<View>(R.id.name) as TextView
            val calories = convertView!!.findViewById<View>(R.id.calories) as TextView
            val brandName = convertView!!.findViewById<View>(R.id.desc) as TextView
            brandName.text = brand.brandNameItemName
            calories.text = brand.nfCalories.toString()
            name.text = brand.brandName
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

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val brandSuggestion: MutableList<BrandedList> = ArrayList()
                if (constraint != null) {
                    for (brand in brandedListAll) {
                        if (brand.brandNameItemName.toLowerCase()
                                .startsWith(constraint.toString().toLowerCase())
                        ) {
                            brandSuggestion.add(brand)
                        }
                    }
                    filterResults.values = brandSuggestion
                    filterResults.count = brandSuggestion.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                brandedList.clear()
                if (results != null && results.count > 0) {
                    // avoids unchecked cast warning when using mDepartments.addAll((ArrayList<Department>) results.values);
                    for (`object` in results.values as List<*>) {
                        if (`object` is BrandedList) {
                            brandedList.add(`object` as BrandedList)
                        }
                    }
                    notifyDataSetChanged()
                } else if (constraint == null) {
                    // no filter, add entire original list back in
                    brandedList.addAll(brandedListAll)
                    notifyDataSetInvalidated()
                }
            }
        }
    }

    init {
        brandedList = ArrayList(brandList)
        brandedListAll = ArrayList(brandList)
    }
}