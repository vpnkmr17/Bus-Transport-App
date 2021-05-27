package com.example.bususerapp.Activities;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.bususerapp.Adapters.ViewPagerAdapter;
import com.example.bususerapp.Fragments.FoundFragment;
import com.example.bususerapp.Fragments.LostFragment;
import com.example.bususerapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

public class LostFoundMainActivity extends AppCompatActivity implements View.OnClickListener{

    private SearchView searchView;
    private FloatingActionButton buttonCreate;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private Context context = this;

    private LostFragment lostFrag = new LostFragment();
    private FoundFragment foundFrag = new FoundFragment();

    public static final String POST_ROUTE = "com.example.bususerapp.postpage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_found_main);

        searchView = (SearchView) findViewById(R.id.searchView);
        buttonCreate = (FloatingActionButton) findViewById(R.id.buttonCreate);

        //toolbar = (Toolbar)findViewById(R.id.toolbar);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        // Set drawer
        /*setSupportActionBar(toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.Open,R.string.Close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();*/

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (viewPager.getCurrentItem() == 0){
                    lostFrag.refreshList(newText);
                }
                else if (viewPager.getCurrentItem() == 1) {
                    foundFrag.refreshList(newText);
                }
                return false;
            }
        });

        buttonCreate.setOnClickListener(this);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add fragments
        adapter.addFragment(lostFrag,"Lost");
        adapter.addFragment(foundFrag,"Found");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void ClickMenu(View view)
    {   //open drawer
        openDrawer(drawerLayout);
    }

    public void openDrawer(DrawerLayout drawerLayout) {
        //open drawer layout
        drawerLayout.openDrawer(GravityCompat.START);

    }

    public void ClickLogo(View view)
    {
        closeDrawer(drawerLayout);
    }

    public void closeDrawer(DrawerLayout drawerLayout) {
        //close drawer layout
        //check condition
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            //drawer is open
            //close drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        closeDrawer(drawerLayout);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == buttonCreate){
            // Create post
            Intent intent = new Intent(this, PostActivity.class);
            if (viewPager.getCurrentItem() == 0){
                intent.putExtra(POST_ROUTE, "LOST");
            }
            else if (viewPager.getCurrentItem() == 1){
                intent.putExtra(POST_ROUTE, "FOUND");
            }
            startActivity(intent);
        }
    }
}