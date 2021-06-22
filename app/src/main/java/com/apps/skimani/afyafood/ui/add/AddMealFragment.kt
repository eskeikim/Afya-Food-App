package com.apps.skimani.afyafood.ui.add

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.apps.skimani.afyafood.R
import com.apps.skimani.afyafood.adapters.FoodItemAdpater
import com.apps.skimani.afyafood.adapters.InstantFoodSearchAdapter
import com.apps.skimani.afyafood.database.FoodItem
import com.apps.skimani.afyafood.database.Meal
import com.apps.skimani.afyafood.databinding.FragmentAddMealBinding
import com.apps.skimani.afyafood.models.BrandedList
import com.apps.skimani.foodie.utils.NetworkResult
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class AddMealFragment : Fragment() {
    private lateinit var namelist:ArrayList<String>
    private  val brandedList=ArrayList<BrandedList>()
    private  val tempBrandedList=ArrayList<BrandedList>()
    private  val tempFoodItemList=ArrayList<FoodItem>()
    private lateinit var autoCompleteAdapter: ArrayAdapter<String>
    private lateinit var foodItem:List<FoodItem>
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
        binding.viewModel=addMealViewModel
        binding.lifecycleOwner=this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        namelist=ArrayList<String>()
        getData("car")
        setupObservers()
    }

    private fun initViews() {

        binding.btnSave.setOnClickListener {
            val mealName=binding.serving.text.toString()
            val serving=binding.serving.text.toString()
            val calories=binding.calories.text.toString()
            val time=binding.time.text.toString()
            val day=binding.day.text.toString()

            val meal=Meal(mealName,time,day,calories,serving)
            addMealViewModel.saveAMealRoomDB(meal)
            Toast.makeText(requireContext(),"Meal inserted successfully",Toast.LENGTH_SHORT).show()
        }
    }

    private fun getData(query:String) {
//        addMealViewModel.getInstantItems(query)
    }

    private fun setupObservers() {
        addMealViewModel.mealsSearch.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    Timber.e("Success ${it.data}")
                    val brandedList = it.data.branded
                    namelist.clear()
                    for (name in brandedList) {
                        namelist.add(name.brandName)
                        Timber.d("Name ${name.brandName}")
                    }

//                    initViews(it)
                }
                is NetworkResult.Error -> {
                    Timber.e( "Failed>>>>>>> ${it.exception.message}")
                }
            }
        })
        addMealViewModel.mealsSearchTest.observe(viewLifecycleOwner, Observer {
                    Timber.e( "Success ${it.size}")
                    namelist.clear()
//                    for (name in brandedList) {
//                        namelist.add(name.brandName)
//                        Timber.d("Name ${name.brandName}")
//                    }
                    initAutoComplete(it)
        })
        binding.footItemRv.adapter=FoodItemAdpater(FoodItemAdpater.OnClickListener{

        })
        addMealViewModel.tempFoodItems.observe(viewLifecycleOwner, Observer {
            if (it!=null) {
                Timber.e("Item Success from ROOM ${it.size} ")
                foodItem=it
                for (item in it){
                    Timber.d("Item Name ${item.foodName} >> ${item.calories}")

                }
            }
        })

    }

    private fun initAutoComplete(list: List<BrandedList>) {
        val mDepartmentList = Arrays.asList(list);
        val instantAdapter=InstantFoodSearchAdapter(requireContext(),R.layout.instant_search_item_list,
            list
        )
//        autoCompleteAdapter = ArrayAdapter<String>(
//            requireContext(), R.layout.instant_search_item_list, R.id.name, namelist
//        )
//        binding.searchAutoComplete.setAdapter(autoCompleteAdapter)
        binding.searchAutoComplete.setAdapter(instantAdapter)
        binding.searchAutoComplete.threshold = 1
//        instantAdapter.notifyDataSetChanged()
//        autoCompleteAdapter.notifyDataSetChanged()
        binding.searchAutoComplete.showDropDown()
        binding.searchAutoComplete.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Timber.d("Text Search $s")
//                    getData(s.toString())

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        binding.searchAutoComplete.setOnItemClickListener{_, _, position, _ ->
            val selectedItem=instantAdapter.getItem(position)
            Timber.d("Selected Item $selectedItem")
//            searchFoodItem(selectedItem.foodName)
            tempBrandedList.add(selectedItem)
//            tempFoodItemList.add(
                val foodItem=FoodItem(selectedItem.foodName,selectedItem.brandName,
                    selectedItem.nfCalories.toString(),selectedItem.servingQty.toString(),selectedItem.photo.thumb
            )
//            )
             addMealViewModel.saveFoodItemRoomDB(foodItem)
            Timber.d("Selected Item $tempBrandedList")


        }
    }

    private fun searchFoodItem(selectedItem: String?) {
        addMealViewModel.getFoodItems(selectedItem!!)
    }
}