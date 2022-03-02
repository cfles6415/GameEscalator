package com.example.gameescalator.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.gameescalator.Adapter.MyReviewsAdapter;
import com.example.gameescalator.Classes.Messages;
import com.example.gameescalator.Classes.MyReview;
import com.example.gameescalator.Home.Home;
import com.example.gameescalator.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.LinkedList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private MyReviewsAdapter myReviewsAdapter;
    private LinkedList<MyReview> myReviewsList;
    private DatabaseReference myRef;
    private RecyclerView recyclerView;
    private ImageView arrow;
    TextView reviewsCount, profileName, noReviewsView;
    private ImageButton changeUsernameButton;

    private LinearLayout linearLayout;
    private FrameLayout.LayoutParams params;

    private LinearLayout changePasswordLayout;

    private String newUsernameText, newPasswordText;

    String username, uid;

    private LinkedList<Messages> myGamesList;

    private boolean isCorrectPassword = true;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // TODO: HERE
        myRef = FirebaseDatabase.getInstance().getReference();

        myGamesList = new LinkedList<>();

        username = Home.firebaseAuth.getCurrentUser().getDisplayName();
        uid = Home.firebaseAuth.getCurrentUser().getUid();

        recyclerView = view.findViewById(R.id.your_reviews_recyclerview);

        reviewsCount = view.findViewById(R.id.your_reviews_count);
        profileName = view.findViewById(R.id.profile_name);

        arrow = view.findViewById(R.id.arrow_close_open);
        changeUsernameButton = view.findViewById(R.id.edit_username);

        profileName.setText(username);

        changePasswordLayout = view.findViewById(R.id.change_password_layout);

        noReviewsView = view.findViewById(R.id.no_reviews_view);

        params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;

        linearLayout = view.findViewById(R.id.reviews_layout);

        changeUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChangeUsernameDialog();
            }
        });

        changePasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChangePasswordDialog();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        GetDataFromFirebase();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void GetDataFromFirebase() {

        Query query = myRef.child("Comments");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                myReviewsList = new LinkedList<>();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if (!snapshot.child("uid").getValue().equals(uid))
                        continue;

                        getGameDetails(snapshot.child("gameKey").getValue().toString());
                        MyReview myReview = new MyReview(snapshot.child("gameName").getValue().toString(),
                                snapshot.child("content").getValue().toString(), Integer.parseInt(snapshot.child("rateScale").getValue().toString()));
                        myReviewsList.add(myReview);
                }
                myReviewsAdapter = new MyReviewsAdapter(myReviewsList, myGamesList);
                recyclerView.setAdapter(myReviewsAdapter);
                myReviewsAdapter.notifyDataSetChanged();

                reviewsCount.setText("(" + myReviewsList.size() + ")");

                if (myReviewsList.isEmpty()) {
                    noReviewsView.setVisibility(View.VISIBLE);
                    linearLayout.setLayoutParams(params);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                NOTHING!!
            }

        });

    }

    private void getGameDetails(String gameKey) {

        Query query = myRef.child("message");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if (!snapshot.child("gamekey").getValue().equals(gameKey))
                        continue;

                    myGamesList.add(new Messages(snapshot.child("name").getValue().toString()
                            ,snapshot.child("image").getValue().toString()
                            ,snapshot.child("gamekey").getValue().toString()
                            ,snapshot.child("creator").getValue().toString()
                            ,Integer.parseInt(snapshot.child("reviewcount").getValue().toString())
                            ,Double.parseDouble(snapshot.child("ratescale").getValue().toString())));

                    return;
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                NOTHING!!
            }

        });

    }

    private void createChangeUsernameDialog() {

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.change_username_layout);

        EditText changeUsernameEditText = dialog.findViewById(R.id.change_username_edit_text);
        Button cancelChangeUsernameButton = dialog.findViewById(R.id.cancel_change_username_button),
                confirmChangeUsernameButton = dialog.findViewById(R.id.confirm_change_username_button);
        ImageView exitButton = dialog.findViewById(R.id.close_change_username_image_view);

        changeUsernameEditText.setHint(Home.firebaseAuth.getCurrentUser().getDisplayName());

        changeUsernameEditText.requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        cancelChangeUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        confirmChangeUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changeUsernameEditText.getText().toString().isEmpty() ||
                        changeUsernameEditText.getText().toString().equals(username))
                    dialog.cancel();
                else {
                    newUsernameText = changeUsernameEditText.getText().toString();
                    updateDatabaseReviews();
                    Toast.makeText(getContext(), "Username has been changed", Toast.LENGTH_LONG).show();
                    dialog.cancel();
                }
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                closeKeyboard();
            }
        });

        dialog.show();

    }

    private void createChangePasswordDialog(){

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.change_password_layout);

        TextInputLayout oldPasswordTextInputLayout = dialog.findViewById(R.id.old_password_edit_text_layout),
                newPasswordTextInputLayout = dialog.findViewById(R.id.new_password_edit_text_layout),
                confirmNewPasswordTextInputLayout = dialog.findViewById(R.id.confirm_new_password_edit_text_layout);

        EditText oldPasswordEditText = dialog.findViewById(R.id.old_password_edit_text),
                newPasswordEditText = dialog.findViewById(R.id.new_password_edit_text),
                confirmNewPasswordEditText = dialog.findViewById(R.id.confirm_new_password_edit_text);

        Button changePasswordCancel = dialog.findViewById(R.id.change_password_cancel),
                changePasswordConfirm = dialog.findViewById(R.id.change_password_confirm);
        ImageView exitButton = dialog.findViewById(R.id.close_change_username_image_view);

        oldPasswordEditText.requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        changePasswordCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        changePasswordConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean correctSyntax = true;
                if (oldPasswordEditText.getText().toString().isEmpty()) {
                    oldPasswordTextInputLayout.setError("Empty field");
                    oldPasswordEditText.requestFocus();
                    correctSyntax = false;
                }else oldPasswordTextInputLayout.setError(null);

                if (newPasswordEditText.getText().toString().isEmpty()){
                    newPasswordTextInputLayout.setError("Empty field");
                    newPasswordEditText.requestFocus();
                    correctSyntax = false;
                }else newPasswordTextInputLayout.setError(null);

                if (confirmNewPasswordEditText.getText().toString().isEmpty()){
                    confirmNewPasswordTextInputLayout.setError("Empty field");
                    confirmNewPasswordEditText.requestFocus();
                    correctSyntax = false;
                }else confirmNewPasswordTextInputLayout.setError(null);

                if (!correctSyntax)
                    return;

                if (!newPasswordEditText.getText().toString().equals(confirmNewPasswordEditText.getText().toString())) {
                    newPasswordTextInputLayout.setError("Passwords not matched");
                    confirmNewPasswordTextInputLayout.setError("Passwords not matched");
                    confirmNewPasswordEditText.requestFocus();
                    return;
                }

                AuthCredential credential = EmailAuthProvider
                        .getCredential(Home.firebaseAuth.getCurrentUser().getEmail(), oldPasswordEditText.getText().toString());
                Home.firebaseAuth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            newPasswordText = newPasswordEditText.getText().toString();
                            updateDatabasePassword(oldPasswordEditText.getText().toString());
                            Toast.makeText(getContext(), "Password has been changed", Toast.LENGTH_LONG).show();
                            dialog.cancel();
                        }else {
                            oldPasswordTextInputLayout.setError("Wrong password");
                            oldPasswordEditText.requestFocus();
                        }
                    }
                });
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                closeKeyboard();
            }
        });

        dialog.show();

    }

    private void changeUsername() {

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(newUsernameText).build();
        Home.firebaseAuth.getCurrentUser().updateProfile(request);

        profileName.setText(newUsernameText);
        Home.navigationUsername.setText(newUsernameText);
        username = newUsernameText;

    }

    private void updateDatabaseUsername() {
        myRef = FirebaseDatabase.getInstance().getReference().child("Users");
        myRef.child(uid).child("name").setValue(newUsernameText);
        changeUsername();
    }

    private void changePassword(String oldPassword) {

        // TODO: User change password by FirebaseAuth.
        AuthCredential credential = EmailAuthProvider
                .getCredential(Home.firebaseAuth.getCurrentUser().getEmail(), oldPassword);

// Prompt the user to re-provide their sign-in credentials
        Home.firebaseAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Home.firebaseAuth.getCurrentUser().updatePassword(newPasswordText).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                        Toast.makeText(getContext(), "Username has been changed", Toast.LENGTH_LONG).show();
                                    else
                                        Toast.makeText(getContext(), "There has been an error while changing password", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "There has been an error while changing password", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
    private void updateDatabasePassword(String oldPassword) {
        // TODO: Updating firebase [password]
        myRef = FirebaseDatabase.getInstance().getReference().child("Users");
        myRef.child(uid).child("password").setValue(newPasswordText);
        changePassword(oldPassword);
    }

    private void updateDatabaseReviews() {

        updateDatabaseUsername();

        Query q = Home.firebaseDatabase.getReference("Comments");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    if (dataSnapshot.child("uid").getValue().toString().equals(uid)) {
                        Home.firebaseDatabase.getReference("Comments/"+ dataSnapshot.getKey()).child("uname").setValue(newUsernameText);
                        break;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

}