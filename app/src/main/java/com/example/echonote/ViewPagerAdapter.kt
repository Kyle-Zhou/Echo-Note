package com.example.echonote

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = Page.entries.size
    override fun createFragment(position: Int): Fragment {
        return when (Page.entries[position]) {
            Page.HOME -> PageFragmentHome()
            Page.ADD -> PageFragmentAdd()
            Page.TEST -> PageFragmentTest()
        }
    }
}
