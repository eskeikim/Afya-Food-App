package com.apps.skimani.afyafood.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.apps.skimani.afyafood.R
import com.apps.skimani.afyafood.adapters.FoodItemAdpater
import com.apps.skimani.afyafood.adapters.MealsAdapter
import com.apps.skimani.afyafood.database.FoodItem
import com.apps.skimani.afyafood.database.Meal
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.chip.Chip
import org.w3c.dom.Text

/**
 * Bind Food List with a recyclerview
 *
 * @param recyclerView
 * @param data
 */
@BindingAdapter("listFoodtems")
fun bindFoodRecyclerView(recyclerView: RecyclerView, data: List<FoodItem>?) {
    val adapter = recyclerView.adapter as FoodItemAdpater
    adapter.submitList(data)
}

/**
 *Bind Meals List with recyclerview
 *
 * @param recyclerView
 * @param data
 */
@BindingAdapter("listMeals")
fun bindMealsRecyclerView(recyclerView: RecyclerView, data: List<Meal>?) {
    val adapter = recyclerView.adapter as MealsAdapter
    adapter.submitList(data)
}

/**
 * Bind image to image view
 *
 * @param imageView
 * @param imgUrl
 */
@BindingAdapter("foodImage")
fun bindfoodImage(imageView: ImageView, imgUrl: String?) {
    Glide.with(imageView.context)
        .load(imgUrl)
        .circleCrop()
        .apply(
            RequestOptions().placeholder(R.drawable.loading_24)
                .error(R.drawable.ic_baseline_food_bank_24)
        )
        .centerInside()
        .into(imageView)
}

/**
 * Bind test to Textview
 *
 * @param nameTv
 * @param nameString
 */
@BindingAdapter("generalName")
fun bindName(nameTv: TextView, nameString: String?) {
    nameString?.let {
        nameTv.text = nameString
    }
}

/**
 * Bind text to a Material Chip
 *
 * @param nameTv
 * @param nameString
 */
@BindingAdapter("chipName")
fun bindChipName(nameTv: Chip, nameString: String?) {
    nameString?.let {
        nameTv.text = nameString
    }
}

/**
 * Check if the result is empty and switch view
 *
 * @param layout
 * @param status
 */
@BindingAdapter("showEmpty")
fun bindShowEmpty(layout: TextView, status: Boolean?) {
    when (status) {
        true -> {
            layout.visibility = View.VISIBLE
        }
        false -> {
            layout.visibility = View.GONE
        }
    }
}

/**
 * Check if the result is empty and switch view
 *
 * @param layout
 * @param status
 */
@BindingAdapter("showEmptyRecycleview")
fun bindShowEmptyRecyclerview(layout: RecyclerView, status: Boolean?) {
    when (status) {
        true -> {
            layout.visibility = View.GONE
        }
        false -> {
            layout.visibility = View.VISIBLE
        }
    }
}


