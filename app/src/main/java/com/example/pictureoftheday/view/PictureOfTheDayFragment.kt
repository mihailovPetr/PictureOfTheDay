package com.example.pictureoftheday.view

import android.os.Bundle
import android.view.*
import android.view.animation.AnticipateOvershootInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import coil.api.load
import com.example.pictureoftheday.R
import com.example.pictureoftheday.databinding.FragmentPictureOfTheDayBinding
import com.example.pictureoftheday.model.NetData
import com.example.pictureoftheday.model.PODServerResponseData
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

    private val contentAnimationDefaultSet = ConstraintSet()
    private val contentAnimationHideSet = ConstraintSet()
    private var contentAnimationIsShown = true

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getImage()
        viewModel.liveData.observe(viewLifecycleOwner, { renderData(it) })

//        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
//        binding.toolbarLayout.title = getString(R.string.title)
        binding.fab.setOnClickListener {
            toast("FAB pressed")
            changeDayAnimation()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initBottomSheet(binding.bottomLayout.bottomSheetContainer)
        initWiki()
        initChips()
        initContentAnimation()

    }

    private fun initChips() {
        binding.chipHd.setOnCheckedChangeListener { buttonView, isChecked ->
            isHD = isChecked
            changeDayAnimation()
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
                R.id.chipTwoDaysBefore -> {
                    calendar.add(Calendar.DATE, -2);
                }
                else -> return@setOnCheckedChangeListener
            }
            checkedDate = calendar.time

            changeDayAnimation()
            viewModel.getImage(checkedDate)
        }
    }

    private fun initWiki() {
//        binding.inputLayout.setEndIconOnClickListener {
//            startActivity(Intent(Intent.ACTION_VIEW).apply {
//                data =
//                    Uri.parse("https://en.wikipedia.org/wiki/${binding.inputEditText.text.toString()}")
//            })
//        }
    }

    private fun renderData(data: NetData) {
        when (data) {
            is NetData.Success<*> -> {
                binding.loadingBar.visibility = View.GONE
                val serverResponseData = data.data as PODServerResponseData

                val url: String?
                if (serverResponseData.mediaType == "image") {
                    url = if (isHD) serverResponseData.hdurl
                    else serverResponseData.url
                    binding.webView.visibility = View.GONE
                    binding.imageView.visibility = View.VISIBLE
                } else {
                    url = serverResponseData.url
                    binding.webView.visibility = View.VISIBLE
                    binding.imageView.visibility = View.INVISIBLE
                    binding.webView.settings.javaScriptEnabled = true
                }

                if (url.isNullOrEmpty()) {
                    showError("Ссылка пустая")
                    return
                }

                binding.descriptionTitle.text = serverResponseData.title
                binding.descriptionExplanation.text = serverResponseData.explanation

                if (serverResponseData.mediaType == "image") {
                    binding.imageView.load(url) {
                        lifecycle(this@PictureOfTheDayFragment)
                        error(R.drawable.ic_load_error_vector)
                        placeholder(R.drawable.ic_no_photo_vector)
                    }
                } else {
                    binding.webView.loadUrl(url)
                }
                changeDayAnimation()
            }
            is NetData.Loading -> {
                binding.loadingBar.visibility = View.VISIBLE
            }
            is NetData.Error -> {
                binding.loadingBar.visibility = View.GONE
                data.error.message?.let { showError(it) }
                changeDayAnimation()
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

    private fun initContentAnimation() {
        contentAnimationDefaultSet.clone(binding.main)
        val set = ConstraintSet()
        set.clone(contentAnimationDefaultSet)

        set.constrainWidth(R.id.frameLayout, ConstraintSet.WRAP_CONTENT)
        set.clear(R.id.frameLayout, ConstraintSet.START)
        set.clear(R.id.frameLayout, ConstraintSet.END)
        set.connect(R.id.frameLayout, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END)

        set.clear(R.id.description_title, ConstraintSet.START)
        set.clear(R.id.description_title, ConstraintSet.END)
        set.connect(R.id.description_title, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START)

        set.clear(R.id.description_explanation, ConstraintSet.START)
        set.clear(R.id.description_explanation, ConstraintSet.END)
        set.connect(R.id.description_explanation, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START)

        contentAnimationHideSet.clone(set)
        contentAnimationIsShown = false
        set.applyTo(binding.main)
    }

    private fun changeDayAnimation() {
        val transition = ChangeBounds()
        transition.interpolator = AnticipateOvershootInterpolator(0.8f)
        transition.duration = 800

        TransitionManager.beginDelayedTransition(binding.main, transition)
        when(contentAnimationIsShown){
            true -> contentAnimationHideSet.applyTo(binding.main)
            false -> contentAnimationDefaultSet.applyTo(binding.main)
        }
        contentAnimationIsShown = !contentAnimationIsShown
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.webView.onPause()
    }

    companion object {
        fun newInstance() = PictureOfTheDayFragment()
    }
}