package com.example.pictureoftheday.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.pictureoftheday.R
import com.example.pictureoftheday.databinding.FragmentSettingsBinding
import com.example.pictureoftheday.utils.Settings
import com.example.pictureoftheday.utils.Theme

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        val themes = Theme.values()
        val themesNames = ArrayList<String>()
        for (theme in themes) {
            themesNames.add(theme.displayingName)
        }
        val adapter = ArrayAdapter(requireContext(), R.layout.settings_theme_dropdown_item, themesNames)
        binding.changeThemeAutoCompleteTextView.setText(Settings.theme.displayingName)
        binding.changeThemeAutoCompleteTextView.setAdapter(adapter)
        
        binding.changeThemeAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            Settings.theme = themes[id.toInt()]
            activity?.recreate()
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}