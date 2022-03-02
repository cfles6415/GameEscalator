package com.example.gameescalator.Adapter;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gameescalator.Classes.Comment;
import com.example.gameescalator.Home.GameDetails;
import com.example.gameescalator.Classes.Messages;
import com.example.gameescalator.Home.Home;
import com.example.gameescalator.R;
import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
//class "Hey adapter we gonna use yo" to put the data inside the RecyclerView, and that's going to organize it!!!!!
//class "Hey adapter we gonna use yo" to put the data inside the RecyclerView, and that's going to organize it!!!!!
//class "Hey adapter we gonna use yo" to put the data inside the RecyclerView, and that's going to organize it!!!!!

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements Filterable {

    private static final String Taq = "RecyclerView";
    private Context mContext;
    private ArrayList<Messages> messagesList;
    public static ArrayList<Messages> allMessagesList;
    FragmentManager fragmentManager;

    public RecyclerAdapter(Context mContext, ArrayList<Messages> messagesList, FragmentManager fragmentManager) {
        this.mContext = (Context) mContext;
        this.messagesList = messagesList;
        allMessagesList = new ArrayList <>(messagesList);
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //TextView
        Messages Messges = messagesList.get(position);
        holder.gameName.setText(messagesList.get(position).getName());
        //ImageView : Glide Library we have to use it
        //Glide.with(load).load("http://goo.gl/gEgYUD").into(imageView);

        Glide.with(mContext).load(messagesList.get(position).getImageUrl()).into(holder.gameImage);
        holder.gameCreator.setText("By " + messagesList.get(position).getCreatorName());

        int reviewCount = messagesList.get(position).getReviewersCount();

        if (reviewCount == 0){
            holder.gameRate.setText("Unrated");
            holder.rateScaleWord.setText("");
            holder.ratedUnrated.setBackgroundResource(R.drawable.ic_baseline_star_outline_24);
            holder.reviewersCount.setText(0+"");
        }else {

            if (messagesList.get(position).getRateScale() == (int) messagesList.get(position).getRateScale())
                holder.gameRate.setText((int) messagesList.get(position).getRateScale() + "");
            else holder.gameRate.setText(messagesList.get(position).getRateScale() + "");

            holder.reviewersCount.setText(reviewCount + "");
            holder.ratedUnrated.setBackgroundResource(R.drawable.rated_star);
            holder.rateScaleWord.setText(Comment.rateSystem[((int) Double.parseDouble(messagesList.get(position).getRateScale() + "")) - 1]);

        }

    }

    @Override
    public int getItemCount() { return messagesList.size(); }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<Messages> filteredGames = new ArrayList<>();

            if (constraint.toString().isEmpty())
                filteredGames.addAll(allMessagesList);
            else {
                for (Messages game: allMessagesList) {
                    String gameName = game.getName();
                    if (gameName.toLowerCase().contains(constraint.toString().toLowerCase()))
                        filteredGames.add(game);
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredGames;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            messagesList.clear();
            messagesList.addAll((Collection<? extends Messages>) results.values);
            notifyDataSetChanged();

        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {

        //widget
        ImageView gameImage, ratedUnrated;
        TextView gameName, gameRate, reviewersCount, gameCreator, rateScaleWord;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            gameImage = itemView.findViewById(R.id.imageView);
            gameName = itemView.findViewById(R.id.gameName);
            gameRate = itemView.findViewById(R.id.gameRates);
            reviewersCount = itemView.findViewById(R.id.reviewersCount);
            gameCreator = itemView.findViewById(R.id.gameCreator);
            rateScaleWord = itemView.findViewById(R.id.rateScaleWord);
            ratedUnrated = itemView.findViewById(R.id.ratedUnrated);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int postion = getAdapterPosition();

            Bundle bundle = new Bundle();
            GameDetails gameDetails = new GameDetails();

//            Intent intent = new Intent(mContext, GameDetails.class);
            bundle.putString("name",messagesList.get(postion).getName());
            bundle.putString("imageUrl",messagesList.get(postion).getImageUrl());
            bundle.putString("postKey",messagesList.get(postion).getPostKey());
            bundle.putString("gamecreator",messagesList.get(postion).getCreatorName());
            bundle.putString("ratescale",messagesList.get(postion).getRateScale()+"");

            gameDetails.setArguments(bundle);

            fragmentManager.beginTransaction().replace(R.id.fragment_container, gameDetails, "GAME_DETAILS_FRAGMENT").commit();
            Home.hideToolbar();
//            mContext.startActivity(intent);
        }
    }

}
