package com.example.fatchcurrentlocation.Fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fatchcurrentlocation.R
import com.example.fatchcurrentlocation.databinding.FragmentShowUserImageBinding
import com.example.fatchcurrentlocation.databinding.FragmentUserProfileBinding
import com.squareup.picasso.Picasso

class ShowUserImage(val image: String) : Fragment() {

    lateinit var binding: FragmentShowUserImageBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentShowUserImageBinding.inflate(layoutInflater, container, false)
        Log.d("TAG","$image")
        if (!image.isEmpty()){
        Picasso.get().load(Uri.parse(image)).placeholder(R.drawable.person).into(binding.fragmentShowUserImageImageview)
        }else{
            binding.fragmentShowUserImageImageview.setImageResource(R.drawable.person)
        }

        return binding.root
    }

}