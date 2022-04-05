package com.example.fatchcurrentlocation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.fatchcurrentlocation.AdaptersClasses.ShowPostsOfThreadsAdapter;

public class ReactionDialogClass extends DialogFragment implements View.OnClickListener {
     int postId;
    ShowPostsOfThreadsAdapter.ShowPostsOfThreadsViewHolder holder;
    public ReactionDialogClass(int postId, ShowPostsOfThreadsAdapter.ShowPostsOfThreadsViewHolder holder){
        this.postId=postId;
        this.holder=holder;
    }
    View view;
    ImageView like_btn, love_btn, haha_btn, wow_btn, angry_btn,sad_btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.reaction_dialog_layout, container, false);
        initialize();
        return view;
    }

    private void initialize() {
        if (getView() == null) return;
        like_btn = getView().findViewById(R.id.reaction_dialog_like_btn);
        love_btn = getView().findViewById(R.id.reaction_dialog_love_btn);
        haha_btn = getView().findViewById(R.id.reaction_dialog_haha_btn);
        wow_btn = getView().findViewById(R.id.reaction_dialog_wow_btn);
        angry_btn = getView().findViewById(R.id.reaction_dialog_angry_btn);
        sad_btn = getView().findViewById(R.id.reaction_dialog_sad_btn);
        like_btn.setOnClickListener(this);
        love_btn.setOnClickListener(this);
        haha_btn.setOnClickListener(this);
        wow_btn.setOnClickListener(this);
        angry_btn.setOnClickListener(this);
        sad_btn.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reaction_dialog_like_btn:
                reactionListener.onReactionSelection(1,postId,holder);
                getDialog().dismiss();
                break;
            case R.id.reaction_dialog_love_btn:
                reactionListener.onReactionSelection(2,postId,holder);
                getDialog().dismiss();
                break;
            case R.id.reaction_dialog_haha_btn:
                reactionListener.onReactionSelection(3,postId,holder);
                getDialog().dismiss();
                break;
            case R.id.reaction_dialog_wow_btn:
                reactionListener.onReactionSelection(4,postId,holder);
                getDialog().dismiss();
                break;
            case R.id.reaction_dialog_sad_btn:
                reactionListener.onReactionSelection(5,postId,holder);
                getDialog().dismiss();
                break;
            case R.id.reaction_dialog_angry_btn:
                reactionListener.onReactionSelection(6,postId,holder);
                getDialog().dismiss();
                break;
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        WindowManager.LayoutParams manager = new WindowManager.LayoutParams();
        manager.width = WindowManager.LayoutParams.MATCH_PARENT;
        manager.height = WindowManager.LayoutParams.WRAP_CONTENT;
        manager.dimAmount = 0.0f;
        dialog.getWindow().getDecorView().setBackground(getResources().getDrawable(android.R.color.transparent));
        dialog.setCanceledOnTouchOutside(true);
        dialog.onBackPressed();

    }

    ReactionListener reactionListener;

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            reactionListener = (ReactionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }
}
