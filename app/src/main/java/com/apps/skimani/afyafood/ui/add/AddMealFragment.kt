package com.apps.skimani.afyafood.ui.add

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.apps.skimani.afyafood.R
import com.apps.skimani.afyafood.databinding.FragmentAddMealBinding
import com.apps.skimani.afyafood.databinding.FragmentHomeBinding
import com.apps.skimani.afyafood.ui.home.HomeViewModel
import com.apps.skimani.foodie.utils.NetworkResult
import timber.log.Timber

class AddMealFragment : Fragment() {
    private lateinit var namelist:ArrayList<String>
    private lateinit var autoCompleteAdapter: ArrayAdapter<String>
    private val addMealViewModel: AddMealViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(
            this,
            AddMealViewModel.Factory(activity.application)
        ).get(AddMealViewModel::class.java)
    }
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
//        initViews()
        namelist=ArrayList<String>()
        getData("car")
        setupObservers()
    }

    private fun getData(query:String) {
        addMealViewModel.getInstantItems(query)
    }

    private fun setupObservers() {
        addMealViewModel.mealsSearch.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    Log.e("Fetch", "Success ${it.data}")
                    val brandedList = it.data.branded
                    namelist.clear()
                    for (name in brandedList) {
                        namelist.add(name.brandName)
                        Timber.d("Name ${name.brandName}")
                    }

                    initViews()
                }
                is NetworkResult.Error -> {
                    Log.e("Fetch", "Failed>>>>>>> ${it.exception.message}")
                }
            }
        })
    }

    private fun initViews() {
        val sampleList =
            listOf<String>("Cabbage", "Carrot", "Cow Milk", "Onions", "Banana", "Oranges", "Apples")
        autoCompleteAdapter = ArrayAdapter<String>(
            requireContext(), R.layout.instant_search_item_list, R.id.name, namelist
        )
        binding.searchAutoComplete.setAdapter(autoCompleteAdapter)
        binding.searchAutoComplete.threshold = 3
        autoCompleteAdapter.notifyDataSetChanged()
        binding.searchAutoComplete.showDropDown()
        binding.searchAutoComplete.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Timber.d("Text Search $s")
                    getData(s.toString())

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        binding.searchAutoComplete.setOnItemClickListener{_, _, position, _ ->
            val selectedItem=autoCompleteAdapter.getItem(position)
            Timber.d("Selected Item $selectedItem")
            searchFoodItem(selectedItem)
        }
    }

    private fun searchFoodItem(selectedItem: String?) {
        addMealViewModel.getFoodItems(selectedItem!!)
    }
}