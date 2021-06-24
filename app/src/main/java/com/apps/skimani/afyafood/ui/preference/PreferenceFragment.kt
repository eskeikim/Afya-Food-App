package com.apps.skimani.afyafood.ui.preference

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.apps.skimani.afyafood.R
import com.apps.skimani.afyafood.databinding.FragmentPreferencesBinding
import com.apps.skimani.afyafood.ui.add.AddMealViewModel
import com.apps.skimani.afyafood.utils.Utils

class PreferenceFragment : Fragment() {

    /**
     * Initialize the viewmodel by lazy
     */
    private val preferenceViewModel: PreferenceViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(
            this,
            PreferenceViewModel.Factory(activity.application)
        ).get(PreferenceViewModel::class.java)
    }
    private lateinit var binding: FragmentPreferencesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPreferencesBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        preferenceViewModel.dailyCaloriesLimit.observe(viewLifecycleOwner, Observer {
            binding.numberOfCalories.setText(it.toString())
        })
    }

    private fun initView() {
        binding.btnSave.setOnClickListener {
            val calLimit=binding.numberOfCalories.text.toString()
            if (calLimit.isNotEmpty()  && calLimit.isDigitsOnly()){
                Utils.setPreference(requireContext(),Utils.PREFS_CALORIES_LIMIT,calLimit)
                Toast.makeText(requireContext(),"Daily calories updated",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(),"Enter valid Daily calories",Toast.LENGTH_SHORT).show()
            }

        }
    }
}