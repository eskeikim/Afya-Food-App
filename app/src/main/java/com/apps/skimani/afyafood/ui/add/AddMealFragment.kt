package com.apps.skimani.afyafood.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.apps.skimani.afyafood.R
import com.apps.skimani.afyafood.databinding.FragmentAddMealBinding
import com.apps.skimani.afyafood.databinding.FragmentHomeBinding

class AddMealFragment : Fragment() {

    private lateinit var dashboardViewModel: AddMealViewModel
    private lateinit var binding: FragmentAddMealBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddMealBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dashboardViewModel =
                ViewModelProvider(this).get(AddMealViewModel::class.java)
        initViews()
        setupObservers()
    }

    private fun setupObservers() {

    }

    private fun initViews() {
        val autoCompleteAdapter=ArrayAdapter<String>(
            requireContext(),R.layout.instant_search_item_list,R.id.name,sampleList)
        binding.searchAutoComplete.setAdapter(autoCompleteAdapter)
        binding.searchAutoComplete.threshold=1
        autoCompleteAdapter.notifyDataSetChanged()

    }
}