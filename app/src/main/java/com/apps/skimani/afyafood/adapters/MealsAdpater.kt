package com.apps.skimani.afyafood.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.apps.skimani.afyafood.database.FoodItem
import com.apps.skimani.afyafood.database.Meal
import com.apps.skimani.afyafood.databinding.FoodItemListBinding
import com.apps.skimani.afyafood.databinding.MealsItemListBinding


class MealsAdapter(private val onClickListener: OnClickListener): ListAdapter<Meal, MealsAdapter.MealsAdapterViewHolder>(DiffCallBack){
    object DiffCallBack: DiffUtil.ItemCallback<Meal>(){
        override fun areItemsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem.mealId==newItem.mealId
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealsAdapterViewHolder {
        return MealsAdapterViewHolder(MealsItemListBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: MealsAdapterViewHolder, position: Int) {
        val meal=getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.click(meal)
        }
        holder.bind(meal)
    }

    class MealsAdapterViewHolder(private val binding: MealsItemListBinding) : RecyclerView.ViewHolder(binding.root){
        fun  bind(meals:Meal){
            binding.meal=meals
            binding.executePendingBindings()
        }
    }
    class OnClickListener(val clickListener:(meal:Meal)->Unit){
        fun click(meal: Meal)=clickListener(meal)
    }
}
