package com.apps.skimani.afyafood.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.apps.skimani.afyafood.R
import com.apps.skimani.afyafood.adapters.FoodItemAdpater
import com.apps.skimani.afyafood.database.FoodItem
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


@BindingAdapter("listFoodtems")
fun bindFoodRecyclerView(recyclerView: RecyclerView, data: List<FoodItem>?) {
    val adapter = recyclerView.adapter as FoodItemAdpater
    adapter.submitList(data)
}

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


@BindingAdapter("generalName")
fun bindName(nameTv: TextView, nameString: String?) {
    nameString?.let {
        nameTv.text = nameString
    }
}
