package com.example.gameescalator.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.gameescalator.Classes.Comment;
import com.example.gameescalator.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.LinkedList;

public class ReviewPage extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<ImageButton> stars = new ArrayList<ImageButton>();
    private ImageButton closeButton;
    private Button submitButton;

    private ImageView gameImage;
    private TextView gameName,
        gameCreator, gameRate;
    private EditText commentText;

    private int rate;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;

    private Comment comment;
    private String game_name, imageUrl, game_creator, postKey, rate_scale;

    private int submitted, userRate;
    private String content;
    private LinkedList<Comment> listComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_review_page);

        init();

        gameName.setText(game_name);
        Glide.with(getApplicationContext()).load(imageUrl).into(gameImage);
        gameCreator.setText("By " + game_creator);

        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /*NOTHING*/ }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeSubmitButtonFunctionality();
            }
            @Override
            public void afterTextChanged(Editable s) { /*NOTHING*/ }
        });

    }

    public void init() {

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        listComments = new LinkedList<>();

        stars.add(findViewById(R.id.star1));
        stars.add(findViewById(R.id.star2));
        stars.add(findViewById(R.id.star3));
        stars.add(findViewById(R.id.star4));
        stars.add(findViewById(R.id.star5));
        stars.add(findViewById(R.id.star6));
        stars.add(findViewById(R.id.star7));
        stars.add(findViewById(R.id.star8));
        stars.add(findViewById(R.id.star9));
        stars.add(findViewById(R.id.star10));

        closeButton = findViewById(R.id.closeButton);
        submitButton = findViewById(R.id.submitReviewButton);
        submitButton.setEnabled(false);

        gameImage = findViewById(R.id.imageReview);
        gameName = findViewById(R.id.nameReview);
        gameCreator = findViewById(R.id.gameCreator);
        gameRate = findViewById(R.id.rateWord);
        commentText = findViewById(R.id.reviewText);

        submitted = Integer.parseInt(getIntent().getStringExtra("submitted"));

        game_name = getIntent().getStringExtra("name");
        imageUrl = getIntent().getStringExtra("imageUrl");
        game_creator = getIntent().getStringExtra("gamecreator");
        postKey = getIntent().getStringExtra("postKey");
        rate_scale = getIntent().getStringExtra("ratescale");

        if (submitted == 1) {
            content = getIntent().getStringExtra("content");
            userRate = Integer.parseInt(getIntent().getStringExtra("user_rate"));
            rate = userRate;

            fillStars(rate-1);

            commentText.setText(content);
            submitButton.setEnabled(true);
        }

        for (int i = 0; i < stars.size(); i++)
            stars.get(i).setOnClickListener(this);

        closeButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);

    }

    public void whenBack() {

        resetStars();

        gameRate.setText("Rate");
        commentText.setText("");

    }

    @Override
    public void onClick(View v) {

        for (int i = 0; i < stars.size(); i++)
            if (v.getId() == stars.get(i).getId())
                fillStars(i);

            switch (v.getId()) {
                case R.id.closeButton:
                    whenBack();
                    finish();
                    break;
                case R.id.submitReviewButton:
                    comment = new Comment(commentText.getText().toString()
                            ,firebaseUser.getUid()
                            ,firebaseUser.getDisplayName()
                            ,postKey
                            ,game_name
                            ,rate);
                    addReviewToDatabase();
                    break;
            }

    }

    private void getAllComments() {

        Query q = firebaseDatabase.getReference("Comments");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                    listComments.add(new Comment(dataSnapshot.child("content").getValue().toString(),
                            dataSnapshot.child("uid").getValue().toString(), dataSnapshot.child("uname").getValue().toString(),postKey
                            , dataSnapshot.child("gameName").getValue().toString()
                            ,Integer.parseInt(dataSnapshot.child("rateScale").getValue().toString())));

                }

                Log.v("listCommentSize", listComments.size()+"");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private double getAVGRate() {

        double sum = 0;
        for (int i = 0; i < listComments.size(); i++)
            sum += listComments.get(i).getRateScale();

        int size = listComments.size();

        listComments.clear();

        return sum/size;

    }

    private void updateDatabase() {

        Query q = firebaseDatabase.getReference("message");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                    if (dataSnapshot.child("gamekey").getValue().toString().equals(postKey)) {
                        if (submitted == 0)
                            firebaseDatabase.getReference("message/"+dataSnapshot.getKey()).child("reviewcount").setValue(
                                    (Integer.parseInt(dataSnapshot.child("reviewcount").getValue().toString()) + 1)
                            );

                        firebaseDatabase.getReference("message/"+dataSnapshot.getKey()).child("ratescale").setValue(
                                Double.parseDouble(String.format("%.1f",getAVGRate()))
                        );

                        break;
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void fillStars(int position) {

        resetStars();

        for (int i = 0; i <= position; i++)
            stars.get(i).setImageResource(R.drawable.rated_star);

        if (!commentText.getText().toString().isEmpty() && rate == 0) {
            submitButton.setBackgroundResource(R.drawable.reviewbutton);
            submitButton.setEnabled(true);
        }

        rate = position+1;

        gameRate.setText(Comment.rateSystem[position]);

    }

    public void resetStars() {
        for (int i = 0; i < stars.size(); i++)
            stars.get(i).setImageResource(R.drawable.ic_baseline_star_outline_24);
        rate = 0;
    }

    public void addReviewToDatabase() {

        if (submitted == 0) {
            firebaseDatabase.getReference("Comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        getAllComments();
                        updateDatabase();
                        Toast.makeText(ReviewPage.this, "Your review has been submitted", Toast.LENGTH_SHORT).show();
                        finish();
                    } else
                        Toast.makeText(ReviewPage.this, "There was an error while submitting your review, try again later.", Toast.LENGTH_LONG).show();
                }
            });
        }else if (submitted == 1) {
            Query query = firebaseDatabase.getReference("Comments");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    boolean hasUpdated = false;
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        if (snapshot.child("uid").getValue().toString().equals(Home.firebaseAuth.getUid()) &&
                        snapshot.child("gameKey").getValue().toString().equals(postKey)) {
                            if (!snapshot.child("rateScale").getValue().toString().equals(userRate)) {
                                firebaseDatabase.getReference("Comments").child(snapshot.getKey()).child("rateScale").setValue(rate);
                                hasUpdated = true;
                                getAllComments();
                                updateDatabase();
                            }
                            if (!snapshot.child("content").getValue().toString().equals(commentText.getText().toString())) {
                                firebaseDatabase.getReference("Comments").child(snapshot.getKey()).child("content").setValue(commentText.getText().toString());
                                hasUpdated = true;
                            }

                            if (hasUpdated) {
                                Toast.makeText(ReviewPage.this, "Your review has been updated", Toast.LENGTH_LONG).show();
                                finish();
                            }

                            return;
                        }
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
    }

    private void changeSubmitButtonFunctionality(){
        if (!commentText.getText().toString().isEmpty() && rate != 0) {
            submitButton.setBackgroundResource(R.drawable.reviewbutton);
            submitButton.setEnabled(true);
        }else {
            submitButton.setBackgroundResource(R.drawable.submit_button_disabled);
            submitButton.setEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}