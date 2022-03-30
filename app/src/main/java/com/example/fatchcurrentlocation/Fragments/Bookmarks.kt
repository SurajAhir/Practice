package com.example.fatchcurrentlocation.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fatchcurrentlocation.R

class Bookmarks : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
       var view= inflater.inflate(R.layout.fragment_bookmarks, container, false)

        return view
    }



}