package com.example.fatchcurrentlocation.ReactionDialogWork;

import com.example.fatchcurrentlocation.AdaptersClasses.ShowPostsOfThreadsAdapter;

public interface ReactionListener {
    void onReactionSelection(int reactionType, int postId, Object holder,int reactionScore);
}
