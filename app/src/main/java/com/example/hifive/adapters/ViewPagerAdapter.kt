package com.example.hifive.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

// Adapter class for managing view paging functionality.
class ViewPagerAdapter (fm:FragmentManager): FragmentPagerAdapter(fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
    val fragmentList= mutableListOf<Fragment>()
    val titleList= mutableListOf<String>()
    // Returns the total number of pages (fragments).
    override fun getCount(): Int {
        return fragmentList.size
    }
    // Returns the fragment to display for a particular page.
    override fun getItem(position: Int): Fragment {
       return fragmentList.get(position)
    }
    // Returns the page title for the tabs.
    override fun getPageTitle(position: Int): CharSequence? {
        return titleList.get(position)
    }
    // Adds fragments and their titles to the lists.
    fun addFragments(fragment: Fragment,title:String){
        fragmentList.add(fragment)
        titleList.add(title)
    }
}