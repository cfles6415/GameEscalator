package com.example.gameescalator.Fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.gameescalator.Adapter.RecyclerAdapter;
import com.example.gameescalator.Classes.Messages;
import com.example.gameescalator.Home.Home;
import com.example.gameescalator.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class HomeFragment extends Fragment {

    //widgets:
    RecyclerView recyclerView;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    //Firebase:
    private DatabaseReference myRef;
    //Vars:
    private ArrayList<Messages> messagesList;
    private RecyclerAdapter recyclerAdapter;
    View view;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView gamesCount;

    private SwipeRefreshLayout swipeRefreshLayout;

    public HomeFragment(TextView gamesCount) {
        this.gamesCount = gamesCount;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2, TextView gamesCount) {
        HomeFragment fragment = new HomeFragment(gamesCount);
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
        view = inflater.inflate(R.layout.home_fragment, container, false);

        setHasOptionsMenu(true);
//
//        toolbar = view.findViewById(R.id.toolbar);
//        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        recyclerView = view.findViewById(R.id.recycleView);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_home);

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetDataFromFirebase();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //Firebase
        myRef = FirebaseDatabase.getInstance().getReference();


        //Arraylist:
        messagesList = new ArrayList<>();


        //Clear Arraylist:

        ClearAll();


        //Get Data Method

        GetDataFromFirebase();


        return view;
    }

    private void GetDataFromFirebase() {

        Query query = myRef.child("message");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ClearAll();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    messagesList.add(new Messages(snapshot.child("name").getValue().toString()
                            ,snapshot.child("image").getValue().toString()
                            ,snapshot.child("gamekey").getValue().toString()
                            ,snapshot.child("creator").getValue().toString()
                            ,Integer.parseInt(snapshot.child("reviewcount").getValue().toString())
                            ,Double.parseDouble(snapshot.child("ratescale").getValue().toString())));

                }
                recyclerAdapter = new RecyclerAdapter(view.getContext(),messagesList, getFragmentManager());
                recyclerView.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();

                gamesCount.setText("(" + RecyclerAdapter.allMessagesList.size() + ")");

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu,menu);
        MenuItem item = menu.findItem(R.id.searchGame);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerAdapter.getFilter().filter(newText);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void ClearAll(){
        if (messagesList != null){
            messagesList.clear();

            if(recyclerAdapter != null){
                recyclerAdapter.notifyDataSetChanged();
            }
        }

    }
}