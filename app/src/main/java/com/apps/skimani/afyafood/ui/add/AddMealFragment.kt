package com.apps.skimani.afyafood.ui.add

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
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
import com.apps.skimani.afyafood.utils.Utils
import com.apps.skimani.foodie.utils.NetworkResult
import com.google.android.material.chip.Chip
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddMealFragment : Fragment() {
    private lateinit var namelist:ArrayList<String>
    private lateinit var brandedList:ArrayList<BrandedList>
    private  val tempBrandedList=ArrayList<BrandedList>()
    private  val tempFoodItemList=ArrayList<FoodItem>()
    private  var tempFoodItemIdList=ArrayList<Int>()
    private lateinit var autoCompleteAdapter: ArrayAdapter<String>
    private lateinit var foodItemList:List<FoodItem>
    private lateinit var instantAdapter:InstantFoodSearchAdapter
private lateinit var foodItemAdapter: FoodItemAdpater
    private lateinit var selectedChipTag: String
    private lateinit var selectedTimeChipTag: String
    private var isCustomDate: Boolean=false

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
        foodItemList= emptyList()
//        getData("car")
        initAutoComplete()
        setupObservers()
    }

    /**
     * Initialize views
     *
     */
    private fun initViews() {
        initChipsView()
          foodItemAdapter=FoodItemAdpater(FoodItemAdpater.OnClickListener {

          })
        binding.footItemRv.adapter=foodItemAdapter

        binding.dateChip.setOnClickListener {
            Utils.pickDate(requireContext(),binding.dateChip)
        }
        binding.btnSave.setOnClickListener {
            val mealName=binding.mealName.text.toString()
            val serving=binding.serving.text.toString()
            val calories=binding.calories.text.toString()

            selectedTimeChipTag="Breakfast"

            selectedTimeChipTag=binding.timeChipGroup.findViewById<Chip>(binding.timeChipGroup.checkedChipId).tag.toString()

            if (!isCustomDate){
                selectedChipTag =
                    binding.dayChipGroup.findViewById<Chip>(binding.dayChipGroup.checkedChipId).tag.toString()
                Timber.d("Selected query $selectedChipTag")
            }

            /**
             * Fields validation before saving
             */
            when {
                foodItemList.isEmpty() -> {
                    Toast.makeText(requireContext(), "Add food items before logging this meal", Toast.LENGTH_SHORT).show()
                }
                mealName.isEmpty() -> {
                    binding.tiMealName.error="Enter valid name"
                }
                serving.isEmpty() -> {
                    binding.tiServings.error="Enter valid serving"
                }
                calories.isEmpty() -> {
                    binding.tiCalories.error="Enter valid calories number"
                }
                else -> {
                    val meal = Meal(mealName, selectedTimeChipTag, selectedChipTag, calories, serving)
                    /**
                     * Save a User custom meal to Room database
                     */
                    addMealViewModel.saveAMealRoomDB(meal)
                    clearfields()

                    /**
                     * delete a User temp food item for this meal from Room database
                     */
                    addMealViewModel.deleteFoodItem(tempFoodItemIdList)
                    Toast.makeText(requireContext(), "Meal size ${meal.name} Inserted successfully", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().navigate(R.id.navigation_home)
                }
            }
        }
    }

    private fun initChipsView() {
        selectedChipTag = Utils.getAnyDay(0, true)
        binding.todayChip.tag = (Utils.getAnyDay(0, true))
        binding.yesterdayChip.text = "Yesterday"
        binding.yesterdayChip.tag = (Utils.getAnyDay(-1, true))
        binding.juzi2Chip.tag = (Utils.getAnyDay(-3, true))
        binding.juzi2Chip.text = (Utils.getAnyDay(-3, false))
        binding.juziChip.tag = (Utils.getAnyDay(-2, true))
        binding.juziChip.text = (Utils.getAnyDay(-2, false))

        binding.dayChipGroup.setOnCheckedChangeListener { group, checkedId ->
            isCustomDate = !(checkedId==R.id.todayChip || checkedId==R.id.yesterdayChip ||
                    checkedId==R.id.juzi2Chip || checkedId==R.id.juziChip)

        binding.dateChip.setOnClickListener {
            chooseDate()
        }
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
        /**
         * show result after Clearing the food item after inserting meal observer
         */
        addMealViewModel.deleteFoodStatus.observe(viewLifecycleOwner, Observer {
            if (it!=null && it>0){
                Toast.makeText(requireContext(),"Successfully cleared food items",Toast.LENGTH_SHORT).show()
            }
        })

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
        addMealViewModel.foodItemTempValue.observe(viewLifecycleOwner, Observer {
                    Timber.e( "Success ${it?.size}")
            foodItemList=it!!
            getById()
        })
    }

    private fun getById() {

         tempFoodItemIdList= ArrayList<Int>()
        var totalCalories=0
        var totalCaloriesString=""
        for (food in foodItemList){
            tempFoodItemIdList.add(food.id)
            totalCaloriesString=food.calories
            totalCalories+=totalCaloriesString.toDouble().toInt()
            Timber.e("Meal ids ${food.id}")
        }
        Timber.d("Total Cal for this food $totalCalories")
        binding.calories.setText("$totalCalories")
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
            setupObservers()
        }
    }

    private fun searchFoodItem(selectedItem: String?) {
        addMealViewModel.getFoodItems(selectedItem!!)
    }
private fun chooseDate() {
    val dateListener: DatePickerDialog.OnDateSetListener
    val myCalendar = Calendar.getInstance()
    val currYear = myCalendar[Calendar.YEAR]
    val currMonth = myCalendar[Calendar.MONTH]
    val currDay = myCalendar[Calendar.DAY_OF_MONTH]
    dateListener =
        DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            myCalendar[Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = monthOfYear
            myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            var isDateOk = true

            val preferredFormat = "dd/MM/yyyy"
            val date =
                SimpleDateFormat(preferredFormat, Locale.ENGLISH).format(myCalendar.time)
            binding.dateChip.tag = Utils.formatRequestTag(date)
            val textString = Utils.formatRequestDate(date)
            binding.dateChip.text = textString
            selectedChipTag = Utils.formatRequestTag(date)
            Timber.e("DATEE $date ...${Utils.formatRequestTag(date)} ..SEL TAG $selectedChipTag")

        }
    DatePickerDialog(
        requireContext(), dateListener, myCalendar[Calendar.YEAR],
        myCalendar[Calendar.MONTH],
        myCalendar[Calendar.DAY_OF_MONTH]
    ).show()
}

}