package com.example.pictureoftheday.view.earth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pictureoftheday.databinding.FragmentEarthBinding
import com.example.pictureoftheday.model.NetData
import com.example.pictureoftheday.view.toast
import com.example.pictureoftheday.viewmodel.EarthViewModel

class EarthFragment : Fragment() {

    private var _binding: FragmentEarthBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EarthViewModel by lazy {
        ViewModelProvider(this).get(EarthViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEarthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPagerEarth.adapter = EarthViewPagerAdapter(childFragmentManager)
        binding.tabLayoutEarth.setupWithViewPager(binding.viewPagerEarth)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getPhotos()
        viewModel.liveData.observe(viewLifecycleOwner, { renderData(it) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun renderData(data: NetData) {
        when (data) {
            is NetData.Success<*> -> {
                val serverResponseData = data.data as List<Pair<String, String>>
                (binding.viewPagerEarth.adapter as EarthViewPagerAdapter).setData(serverResponseData)
            }
            is NetData.Loading -> {
            }
            is NetData.Error -> {
                toast(data.error.message)
            }
        }
    }

    companion object {
        fun newInstance() = EarthFragment()
    }
}