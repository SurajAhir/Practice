package com.example.fatchcurrentlocation;

import com.example.fatchcurrentlocation.AdaptersClasses.ShowPostsOfThreadsAdapter;

public interface ReactionListener {
    void onReactionSelection(int reactionType, int postId, ShowPostsOfThreadsAdapter.ShowPostsOfThreadsViewHolder holder);
}
