package com.example.gameescalator.Home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.gameescalator.Adapter.CommentAdapter;
import com.example.gameescalator.Adapter.RecyclerAdapter;
import com.example.gameescalator.Classes.Comment;
import com.example.gameescalator.Classes.Messages;
import com.example.gameescalator.Fragments.HomeFragment;
import com.example.gameescalator.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GameDetails extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "name";
    private static final String ARG_PARAM2 = "imageUrl";
    private static final String ARG_PARAM3 = "postKey";
    private static final String ARG_PARAM4 = "gamecreator";
    private static final String ARG_PARAM5 = "ratescale";

    // TODO: Rename and change types of parameters
    private String mParam1, mParam2, mParam3, mParam4, mParam5;
    private String gameName, imageUrl, gamePostKey, gameCreator, rateScale;

    public static final String TAG = "GameDetails";
    ImageView gameimg, starImage;
    TextView gamename, gameRateScale, gameWordRate, reviewsHeader, noReviews, reviewsCount, gameNameWhenCollapse;

    FloatingActionsMenu floatingActionsMenu;
    FloatingActionButton floatingActionButtonEdit;
    com.google.android.material.floatingactionbutton.FloatingActionButton floatingActionButtonWrite;

    FrameLayout.LayoutParams layoutParams;
    LinearLayout linearLayout;

    AppBarLayout appBarLayout;

    RecyclerView RvComment;
    DatabaseReference ref;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private ArrayList<Comment> listComment;
    CommentAdapter commentAdapter;
    FirebaseDatabase firebaseDatabase;
    Button btnAddComment;

    boolean hasSubmitted;

    private Comment currentUserReview;

    private SwipeRefreshLayout swipeRefreshLayout;

    public GameDetails() {

    }

    public static GameDetails newInstance(String param1, String param2) {
        GameDetails fragment = new GameDetails();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getString(ARG_PARAM4);
            mParam5 = getArguments().getString(ARG_PARAM5);

            gameName = mParam1;
            imageUrl = mParam2;
            gamePostKey = mParam3;
            gameCreator = mParam4;
            rateScale = mParam5;
        }

    }


        //Add Comment Button Click Listener
//        btnAddComment.setOnClickListener(new View.OnClickListener() {
//            @Override
//
//            public void onClick(View v) {
//
//                Intent intent = new Intent(GameDetails.this,ReviewPage.class);
//                intent.putExtra("name",getIntent().getStringExtra("name"));
//                intent.putExtra("imageUrl",getIntent().getStringExtra("imageUrl"));
//                intent.putExtra("postKey",getIntent().getStringExtra("postKey"));
//                intent.putExtra("gamecreator",getIntent().getStringExtra("gamecreator"));
//                startActivity(intent);
//
//            }
//        });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.game_details_fragment, container, false);

        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        gameimg = view.findViewById(R.id.game_detail);
        gamename = view.findViewById(R.id.game_name);
        //btnAddComment = findViewById(R.id.btnMakeAReview);
        gameRateScale = view.findViewById(R.id.game_detail_rate_scale);
        gameWordRate = view.findViewById(R.id.game_detail_rate_word);
        starImage = view.findViewById(R.id.game_detail_star);

        reviewsHeader = view.findViewById(R.id.reviews_header);
        noReviews = view.findViewById(R.id.no_reviews_game_details);
        reviewsCount = view.findViewById(R.id.game_detail_reviews_count);
        gameNameWhenCollapse = view.findViewById(R.id.game_name_when_collapsed);
        gameNameWhenCollapse.setText(gameName);

        RvComment = view.findViewById(R.id.game_reviews_recycler_view);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        floatingActionsMenu = view.findViewById(R.id.edit_review_menu);
        floatingActionButtonEdit = view.findViewById(R.id.edit_review);
        floatingActionButtonWrite = view.findViewById(R.id.write_a_review);

        gamename.setText(gameName);
        Glide.with(view.getContext()).load(imageUrl).into(gameimg);

        layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        linearLayout = view.findViewById(R.id.game_reviews_layout);

        //swipeRefreshLayout = view.findViewById(R.id.game_detail_swipe_refresh_layout);

        appBarLayout = view.findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0) {
                    reviewsHeader.setTextSize(25);
                    gameNameWhenCollapse.setVisibility(View.VISIBLE);
                } else {
                    reviewsHeader.setTextSize(35);
                    gameNameWhenCollapse.setVisibility(View.GONE);
                }
            }
        });

        prepareGameRateLayout();

        listComment = new ArrayList<>();

        iniRvComment();
        updateGameDetailsFragment();

//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                updateGameDetailsFragment();
//                iniRvComment();
//
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });

//        Log.i("niceThing",hasSubmitted+"!");
//        if (hasSubmitted) {
//
//            Log.i("niceThing","true");
//            floatingActionsMenu.setVisibility(View.VISIBLE);
//
//            floatingActionButtonEdit.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
//
//        } else {
//            floatingActionButtonWrite.setVisibility(View.VISIBLE);
//
//            floatingActionButtonWrite.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getContext(),ReviewPage.class);
//                    intent.putExtra("name",gameName);
//                    intent.putExtra("imageUrl",imageUrl);
//                    intent.putExtra("postKey",gamePostKey);
//                    intent.putExtra("gamecreator",gameCreator);
//                    intent.putExtra("ratescale",rateScale);
//                    startActivity(intent);
//                }
//            });
//
//        }


        return view;
    }

    private void initOfFloatingActionButtonsFunction() {
        
        if (hasSubmitted) {
            floatingActionButtonWrite.setVisibility(View.GONE);
            floatingActionsMenu.setVisibility(View.VISIBLE);

            floatingActionButtonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(),ReviewPage.class);
                    intent.putExtra("name",gameName);
                    intent.putExtra("imageUrl",imageUrl);
                    intent.putExtra("postKey",gamePostKey);
                    intent.putExtra("gamecreator",gameCreator);
                    intent.putExtra("ratescale",rateScale);
                    intent.putExtra("user_rate",currentUserReview.getRateScale()+"");
                    intent.putExtra("content",currentUserReview.getContent());
                    intent.putExtra("submitted","1");

                    floatingActionsMenu.collapse();

                    startActivity(intent);
                }
            });

        } else {
            floatingActionsMenu.setVisibility(View.GONE);
            floatingActionButtonWrite.setVisibility(View.VISIBLE);

            floatingActionButtonWrite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(),ReviewPage.class);
                    intent.putExtra("name",gameName);
                    intent.putExtra("imageUrl",imageUrl);
                    intent.putExtra("postKey",gamePostKey);
                    intent.putExtra("gamecreator",gameCreator);
                    intent.putExtra("ratescale",rateScale);
                    intent.putExtra("submitted","0");
                    startActivity(intent);
                }
            });

        }
    }

    private boolean hasSubmittedReview() {

        if (listComment.isEmpty())
            return false;

        for (int i = 0; i < listComment.size(); i++) {
            Comment comment = listComment.get(i);
            if (comment.getUid().equals(Home.firebaseAuth.getCurrentUser().getUid())) {
                currentUserReview = new Comment(comment);
                return true;
            }
        }

        return false;
    }

    private void prepareGameRateLayout() {

        if (rateScale.equals("0.0")) {

            starImage.setImageResource(R.drawable.ic_baseline_star_outline_24);
            gameRateScale.setVisibility(View.GONE);
            gameWordRate.setText("UNRATED");

        }else {
            double rate = Double.parseDouble(rateScale);
            if (rate == ( (int) rate))
                gameRateScale.setText(((int)rate)+"");
            else gameRateScale.setText(rateScale);
            gameWordRate.setText(Comment.rateSystem[ ((int) rate) - 1 ].toUpperCase());
        }

    }

    private void updateGameDetailsFragment() {

        Query query = firebaseDatabase.getReference("message");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if (snapshot.child("gamekey").getValue().toString().equals(gamePostKey)) {
                        if (!snapshot.child("ratescale").getValue().toString().equals("0")) {

                            if (gameRateScale.getVisibility() == View.GONE) {
                                gameRateScale.setVisibility(View.VISIBLE);
                                starImage.setImageResource(R.drawable.rated_star);
                            }
                            gameRateScale.setText(snapshot.child("ratescale").getValue().toString());
                            gameWordRate.setText(Comment.rateSystem[ ((int) (Double.parseDouble(snapshot.child("ratescale").getValue().toString()) - 1)) ]);
                        }
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }
    
    private void iniRvComment() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        RvComment.setLayoutManager(layoutManager);
        RvComment.setHasFixedSize(true);
        DatabaseReference commentRef = firebaseDatabase.getReference("Comments");

        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (listComment != null)
                    listComment.clear();

                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    if (!snap.child("gameKey").getValue().toString().equals(gamePostKey))
                        continue;

                    listComment.add(new Comment(snap.child("content").getValue().toString(),
                            snap.child("uid").getValue().toString(), snap.child("uname").getValue().toString(),gamePostKey
                            , snap.child("gameName").getValue().toString()
                            ,Integer.parseInt(snap.child("rateScale").getValue().toString())));

                }

                commentAdapter = new CommentAdapter(getContext(),listComment);
                RvComment.setAdapter(commentAdapter);
                commentAdapter.notifyDataSetChanged();

                if (listComment.isEmpty()) {
                    noReviews.setVisibility(View.VISIBLE);
                    layoutParams.gravity = Gravity.CENTER;
                    linearLayout.setLayoutParams(layoutParams);
                }else {
                    noReviews.setVisibility(View.GONE);
                    layoutParams.gravity = Gravity.START;
                    linearLayout.setLayoutParams(layoutParams);
                }

                reviewsCount.setText("(" + listComment.size() + ")");

                hasSubmitted = hasSubmittedReview();
                initOfFloatingActionButtonsFunction();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                /*NOTHING*/
            }
        });
    }

}




