package com.example.fatchcurrentlocation.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.example.fatchcurrentlocation.DataClasses.ResponseDataClass
import com.example.fatchcurrentlocation.R

class YourAccount(val responseDataObject:ResponseDataClass) : Fragment() {
lateinit var messageCount:TextView
lateinit var reactionScore:TextView
lateinit var trophyPoints:TextView
lateinit var userName:TextView
var responseDataObject1=responseDataObject
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        var view =inflater.inflate(R.layout.fragment_your_account, container, false)
        var postBtn:TextView=view.findViewById(R.id.update_status_btn)
        var update_post_tv:EditText=view.findViewById(R.id.update_status_tv)
        initialize(view)
        update_post_tv.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                postBtn.visibility=View.VISIBLE
            }
        })
        Log.d("TAG","HELLO$responseDataObject1")
messageCount.setText(responseDataObject1.user.message_count.toString())
        reactionScore.setText(responseDataObject1.user.reaction_score.toString())
        trophyPoints.setText(responseDataObject1.user.trophy_points.toString())
        userName.setText(responseDataObject1.user.username)
        return view
    }

    private fun initialize(view: View) {
        messageCount=view.findViewById(R.id.user_message_Count)
        reactionScore=view.findViewById(R.id.user_reaction_score_Count)
        trophyPoints=view.findViewById(R.id.user_trophy_points_Count)
        userName=view.findViewById(R.id.userName)
    }

}