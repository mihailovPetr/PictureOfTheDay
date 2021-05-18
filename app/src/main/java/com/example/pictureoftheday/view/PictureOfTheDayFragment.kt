package com.example.pictureoftheday.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.api.load
import com.example.pictureoftheday.MainActivity
import com.example.pictureoftheday.R
import com.example.pictureoftheday.databinding.FragmentPictureOfTheDayBinding
import com.example.pictureoftheday.model.PictureOfTheDayData
import com.example.pictureoftheday.viewmodel.PictureOfTheDayViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import java.util.*

class PictureOfTheDayFragment : Fragment() {

    private var _binding: FragmentPictureOfTheDayBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private var isHD = false
    private var checkedDate = Date()
    private val viewModel: PictureOfTheDayViewModel by lazy {
        ViewModelProvider(this).get(PictureOfTheDayViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getImage()
        viewModel.liveData.observe(viewLifecycleOwner, { renderData(it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomBar(view)
        initBottomSheet(binding.bottomLayout.bottomSheetContainer)
        binding.inputLayout.setEndIconOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data =
                    Uri.parse("https://en.wikipedia.org/wiki/${binding.inputEditText.text.toString()}")
            })
        }

        binding.chipHd.setOnCheckedChangeListener { buttonView, isChecked ->
            isHD = isChecked
            viewModel.getImage(checkedDate)
        }

        binding.chipGroup.setOnCheckedChangeListener { chipGroup, position ->
            val calendar = Calendar.getInstance()
            when (position) {
                R.id.chipToday -> {
                }
                R.id.chipYesterday -> {
                    calendar.add(Calendar.DATE, -1);
                }
                R.id.chipDayBeforeYesterday -> {
                    calendar.add(Calendar.DATE, -2);
                }
                else -> return@setOnCheckedChangeListener
            }
            checkedDate = calendar.time
            viewModel.getImage(checkedDate)
        }
    }

    private fun renderData(data: PictureOfTheDayData) {
        when (data) {
            is PictureOfTheDayData.Success -> {
                binding.loadingBar.visibility = View.GONE
                val serverResponseData = data.serverResponseData
                val url = if (isHD && !serverResponseData.hdurl.isNullOrEmpty()) serverResponseData.hdurl
                else serverResponseData.url
                if (url.isNullOrEmpty()) {
                    showError("Ссылка пустая")
                } else {
                    binding.imageView.load(url) {
                        lifecycle(this@PictureOfTheDayFragment)
                        error(R.drawable.ic_load_error_vector)
                        placeholder(R.drawable.ic_no_photo_vector)
                    }
                    binding.bottomLayout.bottomSheetHeader.text = serverResponseData.title
                    binding.bottomLayout.bottomSheetDescription.text = serverResponseData.explanation
                }
            }
            is PictureOfTheDayData.Loading -> {
                binding.loadingBar.visibility = View.VISIBLE
            }
            is PictureOfTheDayData.Error -> {
                binding.loadingBar.visibility = View.GONE
                data.error.message?.let { showError(it) }
            }
        }
    }

    private fun showError(text: String) {
        binding.main.showSnackBar(text, "Перезагрузить", Snackbar.LENGTH_LONG) {
            viewModel.getImage()
        }
    }

    private fun initBottomSheet(bottomSheet: ConstraintLayout) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPictureOfTheDayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_bottom_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_settings -> {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.container, SettingsFragment.newInstance())?.addToBackStack(null)
                    ?.commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setBottomBar(view: View) {
        val context = activity as MainActivity
        context.setSupportActionBar(binding.bottomAppBar)
        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PictureOfTheDayFragment()
    }
}