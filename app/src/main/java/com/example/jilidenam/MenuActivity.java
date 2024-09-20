package com.example.jilidenam;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "MenuActivity";

    private RecyclerView recyclerView;
    private MenuAdapter adapter;
    private List<MenuItem> menuList;
    private List<MenuItem> allMenuList;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        menuList = new ArrayList<>();
        allMenuList = new ArrayList<>();
        adapter = new MenuAdapter(menuList);
        recyclerView.setAdapter(adapter);

        storage = FirebaseStorage.getInstance();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("menu");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                menuList.clear();
                allMenuList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    MenuItem menuItem = postSnapshot.getValue(MenuItem.class);
                    if (menuItem != null) {
                        Log.d(TAG, "Menu Item: " + menuItem.getName() + ", " + menuItem.getPrice() + ", " + menuItem.getImageUrl() + ", " + menuItem.getCategory());
                        allMenuList.add(menuItem);
                    } else {
                        Log.d(TAG, "MenuItem is null");
                    }
                }
                filterMenu("All"); // Initially show all items sorted by category
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });

        Button btnAll = findViewById(R.id.btnAll);
        Button btnFood = findViewById(R.id.btnFood);
        Button btnDrink = findViewById(R.id.btnDrink);
        Button btnDessert = findViewById(R.id.btnDessert);

        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterMenu("All");
            }
        });

        btnFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterMenu("Food");
            }
        });

        btnDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterMenu("Drink");
            }
        });

        btnDessert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterMenu("Dessert");
            }
        });
    }

    private void filterMenu(String category) {
        menuList.clear();
        if (category.equals("All")) {
            menuList.addAll(allMenuList);
        } else {
            for (MenuItem menuItem : allMenuList) {
                if (menuItem.getCategory().equals(category)) {
                    menuList.add(menuItem);
                }
            }
        }
        // Sort menuList based on category order
        Collections.sort(menuList, new Comparator<MenuItem>() {
            @Override
            public int compare(MenuItem o1, MenuItem o2) {
                return getCategoryOrder(o1.getCategory()) - getCategoryOrder(o2.getCategory());
            }
        });
        adapter.notifyDataSetChanged();
    }

    private int getCategoryOrder(String category) {
        switch (category) {
            case "Food":
                return 1;
            case "Dessert":
                return 2;
            case "Drink":
                return 3;
            default:
                return 4;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
