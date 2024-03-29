package com.apps.skimani.afyafood.ui.add

import android.Manifest
import android.R.attr
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.apps.skimani.afyafood.utils.Constants
import com.apps.skimani.afyafood.utils.ScannerActivity
import com.apps.skimani.afyafood.utils.Utils
import com.apps.skimani.foodie.utils.NetworkResult
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


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
    private val SCAN_SERIAL_REQUEST=920
    private val MY_PERMISSIONS_REQUEST_CAMERA=101
    private val CAMERA_CODE=777

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

        tempFoodItemIdList= ArrayList<Int>()
        foodItemAdapter=FoodItemAdpater(FoodItemAdpater.OnClickListener {

        })
        binding.footItemRv.adapter=foodItemAdapter
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

        foodItemAdapter.notifyDataSetChanged()

        binding.scan.setOnClickListener {
            if(checknGrantPermmision()) {
                Constants.SCANNED_CODE=""
                val intent = Intent(requireContext(), ScannerActivity::class.java)
                startActivityForResult(intent, SCAN_SERIAL_REQUEST)
            }
            val scannerActivity=ScannerActivity.ScannerConstants.SCAN_RESULT
            Toast.makeText(
                requireContext(),
                "ADD Food item $scannerActivity scanned ",
                Toast.LENGTH_SHORT
            ).show()

        }
        binding.dateChip.setOnClickListener {
            Utils.pickDate(requireContext(), binding.dateChip)
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
                    Toast.makeText(
                        requireContext(),
                        "Add food items before logging this meal",
                        Toast.LENGTH_SHORT
                    ).show()
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
                    val foodArrayL=HashMap<String, List<FoodItem>>()
                    foodArrayL.put("", foodItemList)
                    val meal = Meal(
                        mealName,
                        selectedTimeChipTag,
                        selectedChipTag,
                        calories,
                        serving
                    )
                    /**
                     * Save a User custom meal to Room database
                     */
                    addMealViewModel.saveAMealRoomDB(meal)
                    clearfields()

                    /**
                     * delete a User temp food item for this meal from Room database
                     */
                    addMealViewModel.deleteFoodItem(tempFoodItemIdList)
                    Toast.makeText(
                        requireContext(),
                        "Meal size ${meal.name} Logged successfully",
                        Toast.LENGTH_SHORT
                    )
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
            if (it != null && it > 0) {
                Toast.makeText(
                    requireContext(),
                    "Successfully $it cleared food items",
                    Toast.LENGTH_SHORT
                ).show()
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
        /**
         * Get Food items stored in roomdb
         */
        addMealViewModel.foodItemTempValue.observe(viewLifecycleOwner, Observer {
            Timber.e("Success ${it?.size}")
            if (it!=null){
            foodItemList = it!!
            getById()
            foodItemAdapter.notifyDataSetChanged()
        }
        })
        /**
         * Get Food items stored in roomdb
         */
        addMealViewModel.instantError.observe(viewLifecycleOwner, Observer {
          Timber.e("Error fetching the food  $it")
            Snackbar.make(binding.searchAutoComplete,"Error fetching the food  :Usage limits exceeded",Snackbar.LENGTH_LONG).show()
        })

    }

    private fun getById() {

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
                if (s?.length!! > 2) {
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
            binding.searchAutoComplete.setText("")
            /**
             * Insert the selected food item to room database
             */
             addMealViewModel.saveFoodItemRoomDB(foodItem)
            brandedList.clear()
            tempBrandedList.clear()

            Timber.d("Selected Item $tempBrandedList")
            addMealViewModel.getFoodItemRoomDB()
            setupObservers()
            foodItemAdapter.submitList(tempFoodItemList)

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
    fun checknGrantPermmision():Boolean{

        val permission = ContextCompat.checkSelfPermission(
            requireActivity().applicationContext,
            Manifest.permission.CAMERA
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Timber.v("Permission to record denied")
            makePermisionRequest()
            return false
        }
        return  true
    }
    private fun makePermisionRequest() {
        requestPermissions(arrayOf(Manifest.permission.CAMERA), MY_PERMISSIONS_REQUEST_CAMERA)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    val intent = Intent(requireContext(), ScannerActivity::class.java)
                    startActivityForResult(intent, SCAN_SERIAL_REQUEST)
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
//                    Toast.makeText(requireContext(),"Permission denied! ",Toast.LENGTH_SHORT).show()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data!=null){
            val intent=data?.extras
            val scan=ScannerActivity.ScannerConstants.SCAN_RESULT
            val dat=intent?.getString(scan)
            Toast.makeText(requireContext(),"Food item $dat scanned ",Toast.LENGTH_SHORT).show()
//
        }

    }
}