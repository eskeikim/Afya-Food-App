package com.apps.skimani.afyafood.ui.add

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.apps.skimani.afyafood.R
import com.apps.skimani.afyafood.adapters.FoodItemAdpater
import com.apps.skimani.afyafood.adapters.InstantFoodSearchAdapter
import com.apps.skimani.afyafood.database.FoodItem
import com.apps.skimani.afyafood.database.Meal
import com.apps.skimani.afyafood.databinding.FragmentAddMealBinding
import com.apps.skimani.afyafood.models.BrandedList
import com.apps.skimani.foodie.utils.NetworkResult
import com.google.android.material.chip.Chip
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList


class AddMealFragment : Fragment() {
    private lateinit var namelist:ArrayList<String>
    private lateinit var brandedList:ArrayList<BrandedList>
    private  val tempBrandedList=ArrayList<BrandedList>()
    private  val tempFoodItemList=ArrayList<FoodItem>()
    private lateinit var autoCompleteAdapter: ArrayAdapter<String>
    private lateinit var foodItem:List<FoodItem>
    private lateinit var instantAdapter:InstantFoodSearchAdapter
private lateinit var foodItemAdapter: FoodItemAdpater
    /**
     * Initialize the viewmodel by lazy
     */
    private val addMealViewModel: AddMealViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(
            this,
            AddMealViewModel.Factory(activity.application)
        ).get(AddMealViewModel::class.java)
    }

    /**
     * late initialization of the FragmentAddMeal Fragment
     */
    private lateinit var binding: FragmentAddMealBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddMealBinding.inflate(layoutInflater)
        /**
         * Specify the viewmodel the layout will use to bind data from the viewmodel
         */
        binding.viewModel=addMealViewModel
        binding.lifecycleOwner=this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        namelist=ArrayList<String>()
        brandedList=ArrayList<BrandedList>()
//        getData("car")
        initAutoComplete()
        setupObservers()
    }

    /**
     * Initialize views
     *
     */
    private fun initViews() {
          foodItemAdapter=FoodItemAdpater(FoodItemAdpater.OnClickListener {

        })
        binding.footItemRv.adapter=foodItemAdapter
        binding.btnSave.setOnClickListener {
            val mealName=binding.mealName.text.toString()
            val serving=binding.serving.text.toString()
            val calories=binding.calories.text.toString()
//            val time=binding.time.text.toString()
            val time=""
            var day=""
            binding.dayChipGroup.setOnCheckedChangeListener { group, checkedId ->
                val chip: Chip? = group.findViewById(checkedId)
                chip?.let {
                    Toast.makeText(chip.context, chip.text.toString(), Toast.LENGTH_SHORT).show()
                    Timber.d("${chip.text} ${chip.tag}")
                    day=chip.text.toString()
                }
                if (checkedId==R.id.todayChip){
                    Timber.e("${chip?.text} ${chip?.tag}")
                    Toast.makeText(chip?.context, chip?.text.toString(), Toast.LENGTH_SHORT).show()
                }
            }
            /**
             * Add validation before saving
             */
            val meal=Meal(mealName, time, day, calories, serving)
            /**
             * Save a User custom meal to Room database
             */
            addMealViewModel.saveAMealRoomDB(meal)
            clearfields()
            Toast.makeText(requireContext(), "Meal inserted successfully", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.navigation_home)
        }
    }

    /**
     * Clear the input fields
     *
     */
    private fun clearfields() {
        binding.mealName.setText("")
        binding.serving.setText("")
        binding.calories.setText("")
//        binding.day.setText("")
//        binding.time.setText("")
    }

    /**
     * Send a network request to Nutritionix API to fetch instant search food items
     *
     * @param query
     */
    private fun getData(query: String) {
        addMealViewModel.getInstantItems(query)
    }

    /**
     * Observe livedata from the viemodel
     *mealsearch-the livedata for autocomplete request
     */
    private fun setupObservers() {
        addMealViewModel.mealsSearch.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    Timber.e("Success search ${it.data}")
                    brandedList.addAll(it.data.branded)
                    instantAdapter.notifyDataSetChanged()
                    binding.searchAutoComplete.showDropDown()
                    namelist.clear()
                    for (name in brandedList) {
                        namelist.add(name.brandName)
                        Timber.d("Names Search ${name.brandName}")
                    }
                    initAutoComplete()
                    instantAdapter.notifyDataSetChanged()
                }
                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "Error Occurred, please try again!",
                        Toast.LENGTH_LONG
                    ).show()
                    Timber.e("Failed>>>>>>> ${it.exception.message}")
                }
            }
        })
//        addMealViewModel.mealsSearchTest.observe(viewLifecycleOwner, Observer {
//                    Timber.e( "Success ${it.size}")
//                    namelist.clear()
////                    for (name in brandedList) {
////                        namelist.add(name.brandName)
////                        Timber.d("Name ${name.brandName}")
////                    }
//                    initAutoComplete()
//        })
    }

    /**
     * Initialize autocomplete and get the searched query
     *parse the query to nutritionix instant api
     * @param list
     */
    private fun initAutoComplete() {
         instantAdapter=InstantFoodSearchAdapter(
             requireContext(), R.layout.instant_search_item_list,
             brandedList
         )

        binding.searchAutoComplete.setAdapter(instantAdapter)
        binding.searchAutoComplete.threshold = 1
        instantAdapter.notifyDataSetChanged()
        binding.searchAutoComplete.showDropDown()
        binding.searchAutoComplete.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Timber.d("Text Search $s")
                if (s?.length!! > 4) {
                    getData(s.toString())
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        binding.searchAutoComplete.setOnItemClickListener{ _, _, position, _ ->
            val selectedItem=instantAdapter.getItem(position)
            Timber.d("Selected Item $selectedItem")
            tempBrandedList.add(selectedItem)
                val foodItem=FoodItem(
                    selectedItem.foodName,
                    selectedItem.brandName,
                    selectedItem.nfCalories.toString(),
                    selectedItem.servingQty.toString(),
                    selectedItem.photo.thumb
                )
            /**
             * Insert the selected food item to room database
             */
             addMealViewModel.saveFoodItemRoomDB(foodItem)
            Timber.d("Selected Item $tempBrandedList")
            addMealViewModel.getFoodItemRoomDB()
        }
    }

    private fun searchFoodItem(selectedItem: String?) {
        addMealViewModel.getFoodItems(selectedItem!!)
    }

}