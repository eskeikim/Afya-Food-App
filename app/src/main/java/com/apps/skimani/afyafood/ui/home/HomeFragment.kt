package com.apps.skimani.afyafood.ui.home

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.apps.skimani.afyafood.R
import com.apps.skimani.afyafood.adapters.FoodItemAdpater
import com.apps.skimani.afyafood.adapters.MealsAdapter
import com.apps.skimani.afyafood.databinding.FragmentHomeBinding
import com.apps.skimani.afyafood.models.FoodResponse
import com.apps.skimani.afyafood.utils.Utils
import com.google.android.material.chip.Chip
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(
            this,
            HomeViewModel.Factory(activity.application)
        ).get(HomeViewModel::class.java)

    }
    private lateinit var selectedChipTag: String
    var totalConsumedCal=0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        /**
         * Specify the viewmodel the layout will use to bind data from the viewmodel
         */
        binding.viewwModel = homeViewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupObservers()

    }

    /**
     * Initialize views in this Fragment
     *
     */
    private fun initViews() {
        val adapter = MealsAdapter(MealsAdapter.OnClickListener {
            findNavController().navigate(R.id.HometoEdi)
        })
        binding.dailyCalories.setOnClickListener {
            findNavController().navigate(R.id.navigation_dashboard)
        }
        binding.mealsRv.adapter = adapter
        selectedChipTag = Utils.getAnyDay(0, true)
        binding.todayChip.tag = (Utils.getAnyDay(0, true))
        binding.yesterdayChip.text = "Yesterday"
        binding.yesterdayChip.tag = (Utils.getAnyDay(-1, true))
        binding.juzi2Chip.tag = (Utils.getAnyDay(-3, true))
        binding.juzi2Chip.text = (Utils.getAnyDay(-3, false))
        binding.juziChip.tag = (Utils.getAnyDay(-2, true))
        binding.juziChip.text = (Utils.getAnyDay(-2, false))
       fetchMeals(false)
        binding.todayChip.setOnClickListener {

            fetchMeals(false)
        }
        binding.yesterdayChip.setOnClickListener {
            fetchMeals(false)
        }
        binding.juziChip.setOnClickListener {
            fetchMeals(false)
        }
        binding.juzi2Chip.setOnClickListener {
            fetchMeals(false)
        }
        binding.dateChip.setOnClickListener {
            chooseDate()
        }

    }

    /**
     * Get checked tag from Material chips and fetch meals
     *
     * @param isCustom
     */
    private fun fetchMeals(isCustom: Boolean) {
        if (isCustom) {
            Timber.d("Selected query $selectedChipTag")
            fetchMealsFromRoomDB(selectedChipTag)
        } else {
            selectedChipTag =
                binding.dayChipGroup.findViewById<Chip>(binding.dayChipGroup.checkedChipId).tag.toString()
            Timber.d("Selected query $selectedChipTag")
            fetchMealsFromRoomDB(selectedChipTag)
        }

    }

    /**
     * Actually fetch meals from room DB
     *
     * @param query
     */
    private fun fetchMealsFromRoomDB(query: String) {
       homeViewModel.getMealsByDay(query)
    }

    /**
     * Choose a custom date to view logged meals
     *
     */
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
                fetchMeals(true)
                Timber.e("DATEE $date ...${Utils.formatRequestTag(date)} ..SEL TAG $selectedChipTag")

            }
        DatePickerDialog(
            requireContext(), dateListener, myCalendar[Calendar.YEAR],
            myCalendar[Calendar.MONTH],
            myCalendar[Calendar.DAY_OF_MONTH]
        ).show()
    }

    /**
     * Start observing livedat from viewmodel
     *
     */
    private fun setupObservers() {
        homeViewModel.mealsByDay.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Timber.e("Meals Success from ROOM ${it.size}")
                var dailyCal=0
                for (item in it) {
                    dailyCal+=item.totalCalories.toDouble().toInt()
                    Timber.d("Meal Name ${item.name} >> ${item.totalCalories}")
                }
                totalConsumedCal=dailyCal
                Utils.setPreference(requireContext(),Utils.PREFS_DAILY_CALORIES,dailyCal.toString())
                Timber.e("Total cal ${totalConsumedCal}")
             refreshCaloriesLimit()
            }else{
                refreshCaloriesLimit()
            }
        })



    }

    /**
     * Refresh the calories textviews
     *
     */
    private fun refreshCaloriesLimit() {
        homeViewModel.dailyCaloriesLimit.observe(viewLifecycleOwner, Observer {
            if (it!="0"){
                val limit=it.toString().toInt()
                val totalConsumedCalo=Utils.getPreferences(requireContext(),Utils.PREFS_DAILY_CALORIES)!!.toDouble().toInt()
                if (totalConsumedCalo!=0) {
                    val remaining =  limit -totalConsumedCalo
                    if (remaining>0) {
                        binding.dailyCalories.text = "Daily Calories limit :$it Cal "
                        binding.calories.text = " Remaining today :$remaining Cal"
                    }else{
                        binding.dailyCalories.text = "Daily Calories limit :$it Cal"
                        binding.calories.text = "Exeeded your limit $totalConsumedCalo Cal"
                    }
                }else{
                    binding.calories.text = " Remaining today :$it Cal"

                }
            }else{
                binding.dailyCalories.text = "Kindly set your Daily Calories limit"
            }
        })    }
}