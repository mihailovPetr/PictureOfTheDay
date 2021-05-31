package com.example.pictureoftheday.view.earth

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class EarthViewPagerAdapter(fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var data:List<Pair<String, String>> = listOf()

    fun setData(data: List<Pair<String, String>>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        return ChildEarthFragment.newInstance(data[position].second)
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return data[position].first
    }
}
