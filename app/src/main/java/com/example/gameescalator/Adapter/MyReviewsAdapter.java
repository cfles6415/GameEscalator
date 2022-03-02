package com.example.gameescalator.Adapter;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gameescalator.Classes.MyReview;
import com.example.gameescalator.Home.GameDetails;
import com.example.gameescalator.Classes.Messages;
import com.example.gameescalator.Home.ReviewPage;
import com.example.gameescalator.R;
import com.example.gameescalator.User.MainActivity;

import java.util.ArrayList;
import java.util.LinkedList;

//class "Hey adapter we gonna use yo" to put the data inside the RecyclerView, and that's going to organize it!!!!!
//class "Hey adapter we gonna use yo" to put the data inside the RecyclerView, and that's going to organize it!!!!!
//class "Hey adapter we gonna use yo" to put the data inside the RecyclerView, and that's going to organize it!!!!!

public class MyReviewsAdapter extends RecyclerView.Adapter<MyReviewsAdapter.ViewHolder> {

    private static final String Taq = "RecyclerView";
    private LinkedList<MyReview> myReviews;
    private LinkedList<Messages> myGamesList;
    private RelativeLayout relativeLayout;

    public MyReviewsAdapter(LinkedList<MyReview> myReviewsList, LinkedList<Messages> myGamesList) {
        this.myReviews = myReviewsList;
        this.myGamesList = myGamesList;
    }


    @NonNull
    @Override
    public MyReviewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.your_reviews_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MyReview my_reviews = myReviews.get(position);
        holder.gameName.setText(my_reviews.getGameName());
        holder.rate.setText(my_reviews.getRate()+"");
        holder.reviewContent.setText(my_reviews.getContent());

    }

    @Override
    public int getItemCount() { return myReviews.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {

        //widget
        TextView gameName, reviewContent, rate;
        ImageView arrowdown;
        ImageButton reviewOptions;
        PopupMenu popupMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            gameName = itemView.findViewById(R.id.game_name_item);
            reviewContent = itemView.findViewById(R.id.review_content);
            rate = itemView.findViewById(R.id.review_rate_scale);
            arrowdown = itemView.findViewById(R.id.arrow_close_open);
            relativeLayout = itemView.findViewById(R.id.reveal_review_item);
            relativeLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

            reviewOptions = itemView.findViewById(R.id.review_options);

            popupMenu = new PopupMenu(reviewOptions.getContext(),reviewOptions);
            popupMenu.inflate(R.menu.popup_edit_menu);

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.edit_review_profile) {
                        Intent intent = new Intent(itemView.getContext(), ReviewPage.class);
                        intent.putExtra("name",myReviews.get(getAdapterPosition()).getGameName());
                        intent.putExtra("imageUrl", myGamesList.get(getAdapterPosition()).getImageUrl());
                        intent.putExtra("postKey", myGamesList.get(getAdapterPosition()).getPostKey());
                        intent.putExtra("gamecreator", myGamesList.get(getAdapterPosition()).getCreatorName());
                        intent.putExtra("user_rate", myReviews.get(getAdapterPosition()).getRate()+"");
                        intent.putExtra("content", myReviews.get(getAdapterPosition()).getContent());
                        intent.putExtra("submitted", "1");
                        itemView.getContext().startActivity(intent);
                    }
                    return false;
                }
            });

            reviewOptions.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.review_card) {
                int visible = (reviewContent.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
                int fromDegrees = (visible == View.VISIBLE) ? 0 : 90;
                int toDegrees = (visible == View.VISIBLE) ? 90 : 0;
                TransitionManager.beginDelayedTransition(relativeLayout, new AutoTransition());

                reviewContent.setVisibility(visible);
                RotateAnimation rotateAnimation = new RotateAnimation(fromDegrees, toDegrees, RotateAnimation.RELATIVE_TO_SELF,
                        .5f, RotateAnimation.RELATIVE_TO_SELF,
                        .5f);

                rotateAnimation.setDuration(300);
                rotateAnimation.setFillAfter(true);
                arrowdown.startAnimation(rotateAnimation);
                return;
            }
            if (v.getId() == R.id.review_options) {
                popupMenu.show();
                return;
            }

        }
    }

}
