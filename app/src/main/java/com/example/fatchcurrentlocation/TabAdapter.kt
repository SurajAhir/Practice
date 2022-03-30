package com.example.fatchcurrentlocation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.fatchcurrentlocation.DataClasses.ResponseDataClass
import com.example.fatchcurrentlocation.Fragments.YourAccount

class TabAdapter(fm: FragmentManager,val responseDataObject:ResponseDataClass) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 1
    }

    override fun getItem(position: Int): Fragment {
       when(position){
           0->return YourAccount(responseDataObject)

       }
        return YourAccount(responseDataObject)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title:String=""
        when(position){
            0->title="Your accounts"
        }
        return title
    }
}