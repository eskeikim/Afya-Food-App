package com.apps.skimani.afyafood.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.apps.skimani.afyafood.R
import com.apps.skimani.afyafood.databinding.FragmentHomeBinding
import com.apps.skimani.afyafood.models.FoodResponse
import timber.log.Timber

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(this, HomeViewModel.Factory(activity.application)).get(HomeViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
     setupObservers()
    }

    private fun setupObservers() {
        homeViewModel.allMeals.observe(viewLifecycleOwner, Observer {
            if (it!=null) {
                Timber.e("Meals Success from ROOM ${it.size}")
                for (item in it){
                    Timber.d("Meal Name ${item.name} >> ${item.totalCalories}")

                }
            }
        })
    }

}