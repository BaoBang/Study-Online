package com.example.baobang.gameduangua.all_course;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.baobang.gameduangua.Constant;
import com.example.baobang.gameduangua.R;
import com.example.baobang.gameduangua.fragment.ListCourseFragment;
import com.example.baobang.gameduangua.fragment.MyCourseFragment;
import com.example.baobang.gameduangua.gallery.GalleryActivity;
import com.example.baobang.gameduangua.helper.BottomNavigationViewBehavior;
import com.example.baobang.gameduangua.login.view.LoginActivity;
import com.example.baobang.gameduangua.profile.ProfileActivity;


/*
 * Created by huuduc on 08/03/2018.
 */

public class MainActivity extends AppCompatActivity {
    private static final String SELECTED_ITEM = "arg_selected_item";
    private BottomNavigationView navigation;
    private int mSelectedItem;
//    private RecyclerView mRvCategories;
//    private CategoryListAdapter mListAdapter;
//    private List<Category> mCategories;
//    private SOService mService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_course);
        addControls(savedInstanceState);
        addEvents();
    }
    private void addEvents() {
        navigation.setOnNavigationItemSelectedListener(item -> {
            selectedFragment(item);
            return true;
        });
    }
    private void addControls(Bundle savedInstanceState) {
        navigation = findViewById(R.id.navigation);
        // attaching bottom sheet behaviour - hide / show on scroll
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationViewBehavior());

        MenuItem selectedItem;
        if (savedInstanceState != null) {
            mSelectedItem = savedInstanceState.getInt(SELECTED_ITEM, 0);
            selectedItem = navigation.getMenu().findItem(mSelectedItem);
        } else {
            selectedItem = navigation.getMenu().getItem(0);
        }
        selectedFragment(selectedItem);
    }

    private void selectedFragment(MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()){
            case R.id.navigation_home:
                fragment =  ListCourseFragment.newInstance();
                break;
            case R.id.my_course:
                fragment = MyCourseFragment.newInstance();
                break;
        }
        // update selected item
        mSelectedItem = item.getItemId();
        if(fragment != null){
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.profile:
                goToProfileActivity();
                break;
            case R.id.gallery:
                goToGalleryActivity();
                break;
            case R.id.logout:
                logOut();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToGalleryActivity() {
        Intent  intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);
    }

    private void goToProfileActivity() {
        Intent  intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void logOut() {
        SharedPreferences preferences = getSharedPreferences(
                Constant.KEY_PREFERENCES,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constant.USER, "");
        editor.apply();
        Intent  intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
