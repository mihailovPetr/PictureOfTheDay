package com.example.pictureoftheday.view.earth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.transition.ChangeBounds
import androidx.transition.ChangeImageTransform
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import coil.api.load
import com.example.pictureoftheday.R
import com.example.pictureoftheday.databinding.FragmentChildEarthBinding

class ChildEarthFragment : Fragment() {

    private var _binding: FragmentChildEarthBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageUrl: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChildEarthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString(BUNDLE_EXTRA)?.let { imageUrl ->
            this.imageUrl = imageUrl
        }
        initViews()
    }

    private var isExpanded = false

    private fun initViews() {

        binding.imageViewEarth.load(imageUrl) {
            lifecycle(this@ChildEarthFragment)
            error(R.drawable.ic_load_error_vector)
            placeholder(R.drawable.ic_no_photo_vector)
        }

        with(binding) {
            imageViewEarth.setOnClickListener {
                isExpanded = !isExpanded
                TransitionManager.beginDelayedTransition(
                    frameLayoutEarth, TransitionSet()
                        .addTransition(ChangeBounds())
                        .addTransition(ChangeImageTransform())
                )

                val params: ViewGroup.LayoutParams = imageViewEarth.layoutParams
                params.height =
                    if (isExpanded) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
                imageViewEarth.layoutParams = params
                imageViewEarth.scaleType =
                    if (isExpanded) ImageView.ScaleType.CENTER else ImageView.ScaleType.FIT_CENTER
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private const val BUNDLE_EXTRA = "imageUrl"

        fun newInstance(imageUrl: String) = ChildEarthFragment().apply {
            arguments = Bundle().apply {
                putString(BUNDLE_EXTRA, imageUrl)
            }
        }
    }

}