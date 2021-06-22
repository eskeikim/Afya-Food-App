package com.apps.skimani.afyafood.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.apps.skimani.afyafood.database.FoodItem
import com.apps.skimani.afyafood.databinding.FoodItemListBinding


class FoodItemAdpater(private val onClickListener: OnClickListener): ListAdapter<FoodItem, FoodItemAdpater.FoodItemAdpaterViewHolder>(DiffCallBack){
    object DiffCallBack: DiffUtil.ItemCallback<FoodItem>(){
        override fun areItemsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
            return oldItem.id==newItem.id
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemAdpaterViewHolder {
        return FoodItemAdpaterViewHolder(FoodItemListBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: FoodItemAdpaterViewHolder, position: Int) {
        val food=getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.click(food)
        }
        holder.bind(food)
    }

    class FoodItemAdpaterViewHolder(private val binding: FoodItemListBinding) : RecyclerView.ViewHolder(binding.root){
        fun  bind( food:FoodItem){
            binding.foodItem=food
            binding.executePendingBindings()
        }
    }
    class OnClickListener(val clickListener:(project:FoodItem)->Unit){
        fun click(project: FoodItem)=clickListener(project)
    }
}
