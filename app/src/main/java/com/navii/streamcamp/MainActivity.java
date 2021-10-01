package com.navii.streamcamp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private Fragment selectedFragment = new HomeFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);


        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentContainerView3);
        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        final FragmentManager fragmentManager = getSupportFragmentManager();

        final  Fragment homeFragment = new HomeFragment();
        final  Fragment exploreFragment = new ExploreFragment();
        final  Fragment messageFragment= new MessageFragment();
        final  Fragment profileFragment = new ProfileFragment();



//        fragmentManager.beginTransaction().add(R.id.profileFragment, new ProfileFragment(), "3").hide(new ProfileFragment()).commit();
//        fragmentManager.beginTransaction().add(R.id.messageFragment, new MessageFragment(), "3").hide(new MessageFragment()).commit();
//        fragmentManager.beginTransaction().add(R.id.exploreFragment, new ExploreFragment(), "3").hide(new ExploreFragment()).commit();
//        fragmentManager.beginTransaction().add(R.id.homeFragment, new HomeFragment(), "3").hide(homeFragment).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.homeFragment:
                        fragmentManager.beginTransaction().hide(selectedFragment).show(homeFragment).commit();
                        selectedFragment = homeFragment;

                        break;
                    case R.id.exploreFragment:

                        fragmentManager.beginTransaction().hide(selectedFragment).show(exploreFragment).commit();
                        selectedFragment =exploreFragment;
                        break;
                    case R.id.campFragment:

                        SharedPreferences sharedPreferences =  getSharedPreferences("STREAMCAMP",   MODE_PRIVATE);


                        String currentCampFire = sharedPreferences.getString("CURRENT_CAMPFIRE" ,"");
                        String currentCampFireCampMaster = sharedPreferences.getString("CURRENT_CAMPFIRE_CAMPMASTER" ,"");
                        String savedUser= sharedPreferences.getString("USERID","");
                        if(currentCampFire ==""){
                            return false ;
                        }
                        else{
                            if(currentCampFireCampMaster.equals(savedUser)){
                                Intent goLive = new Intent(MainActivity.this, GoLiveActivity.class);
                                 startActivity(goLive);
                            }else{
                                Intent comminityStandard = new Intent(MainActivity.this, CommunityStandardActivity.class);
                                startActivity(comminityStandard);
                            }
                        }



                        return false;
                    case R.id.messageFragment:
                        fragmentManager.beginTransaction().hide(selectedFragment).show(messageFragment).commit();
                        selectedFragment =messageFragment;
                        break;
                    case R.id.profileFragment:
                        Intent profileIntent = new Intent(MainActivity.this,ProfileActivity.class);
                        profileIntent.putExtra("isCampMaster",false);
                        startActivity(profileIntent);
                        return false;
                }


                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainerView3, selectedFragment);
                transaction.commit();
                return true;


            }
        });
    }
}