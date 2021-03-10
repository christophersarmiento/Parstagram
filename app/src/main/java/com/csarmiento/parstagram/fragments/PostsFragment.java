package com.csarmiento.parstagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csarmiento.parstagram.Post;
import com.csarmiento.parstagram.PostsAdapter;
import com.csarmiento.parstagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class PostsFragment extends Fragment {

  public static final String TAG = "PostsFragement";
  private RecyclerView rvPosts;
  protected PostsAdapter adapter;
  protected List<Post> allPosts;
  private SwipeRefreshLayout swipeContainer;


  public PostsFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_posts, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    rvPosts = view.findViewById(R.id.rvPosts);

    allPosts = new ArrayList<>();
    adapter = new PostsAdapter(getContext(), allPosts);

    swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
    swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        queryPosts();
      }
    });

    rvPosts.setAdapter(adapter);
    rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
    queryPosts();
  }

  protected void queryPosts() {
    ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
    query.include(Post.KEY_AUTHOR);
    query.setLimit(20);
    query.addDescendingOrder(Post.KEY_CREATED);
    query.findInBackground(new FindCallback<Post>() {
      @Override
      public void done(List<Post> posts, ParseException e) {
        if (e != null) {
          Log.e(TAG, "Issue with getting posts", e);
          return;
        }
        for (Post post : posts) {
          Log.i(TAG, "Post: " + post.getDescription() + ", Author: " + post.getUser().getUsername());
        }
        allPosts.addAll(posts);
        adapter.clear();
        adapter.addAll(posts);
        adapter.notifyDataSetChanged();
        swipeContainer.setRefreshing(false);
      }
    });
  }
}