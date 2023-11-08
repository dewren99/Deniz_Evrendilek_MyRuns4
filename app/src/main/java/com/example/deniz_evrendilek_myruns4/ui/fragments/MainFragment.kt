package com.example.deniz_evrendilek_myruns4.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.deniz_evrendilek_myruns4.R
import com.example.deniz_evrendilek_myruns4.ui.fragments.tabs.HistoryFragment
import com.example.deniz_evrendilek_myruns4.ui.fragments.tabs.SettingsFragment
import com.example.deniz_evrendilek_myruns4.ui.fragments.tabs.StartFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainFragment : Fragment() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2

    private var currentTabIndex = 0

    @Suppress("RedundantOverride")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        viewPager2 = view.findViewById(R.id.view_pager_2)
        tabLayout = view.findViewById(R.id.tab_layout)


        setupViewPager2()

        return view
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val sp = requireActivity().getPreferences(Context.MODE_PRIVATE)
        currentTabIndex = sp.getInt("position", 0)
        viewPager2.setCurrentItem(currentTabIndex, false) // viewPager2 current item setter
        // doesn't work unless smoothScroll is set to false... What a mess!
    }

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            val editor = requireActivity().getPreferences(Context.MODE_PRIVATE).edit()
            editor.putInt("position", position).apply()

            currentTabIndex = position
        }
    }

    private fun setupViewPager2() {
        viewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 3

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> StartFragment()
                    1 -> HistoryFragment()
                    2 -> SettingsFragment()
                    else -> throw IllegalStateException("Unexpected tab position: $position")
                }
            }
        }

        viewPager2.registerOnPageChangeCallback(pageChangeCallback)

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> "Start"
                1 -> "History"
                2 -> "Settings"
                else -> null
            }
        }.attach()
    }

    override fun onDestroyView() {
        viewPager2.unregisterOnPageChangeCallback(pageChangeCallback)
        super.onDestroyView()
    }
}