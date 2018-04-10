package com.example.baobang.gameduangua.all_course.detail;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.baobang.gameduangua.Constant;
import com.example.baobang.gameduangua.R;
import com.example.baobang.gameduangua.adapter.MyPagerAdapter;
import com.example.baobang.gameduangua.all_course.MainActivity;
import com.example.baobang.gameduangua.base.BaseFragment;
import com.example.baobang.gameduangua.data.ApiUtils;
import com.example.baobang.gameduangua.data.SOService;
import com.example.baobang.gameduangua.fragment.CourseDescriptionFragment;
import com.example.baobang.gameduangua.fragment.LessonFragment;
import com.example.baobang.gameduangua.gallery.GalleryActivity;
import com.example.baobang.gameduangua.login.view.LoginActivity;
import com.example.baobang.gameduangua.model.AddCourseObj;
import com.example.baobang.gameduangua.model.AddCourseResponse;
import com.example.baobang.gameduangua.model.LessonObjResponse;
import com.example.baobang.gameduangua.model.LessonResponse;
import com.example.baobang.gameduangua.model.UserCourse;
import com.example.baobang.gameduangua.model.UserResponse;
import com.example.baobang.gameduangua.profile.ProfileActivity;
import com.example.baobang.gameduangua.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by huuduc on 08/03/2018.
 */

    public class CourseDetailActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private SOService mService;
    private String courseId;
    private ImageView imvCourse;
    private boolean isPurchased = false;
    private Button btnPurchase;
    private List<BaseFragment> listFragments;

    private List<UserCourse> listUserCourse;

    String userID;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_course_not_purchase);

        mService = ApiUtils.getSOService();
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        imvCourse = findViewById(R.id.imvCourse);
        btnPurchase = findViewById(R.id.btnPurchase);
        listUserCourse = new ArrayList<>();

        userID = AppUtils.getValueToSharedPreferences(
                this,
                Constant.KEY_PREFERENCES,
                MODE_PRIVATE,
                Constant.USER_ID
        );

        courseId = AppUtils.getValueToSharedPreferences(
                this,
                Constant.KEY_PREFERENCES,
                MODE_PRIVATE,
                Constant.COURSE_ID
        );

        userEmail = AppUtils.getValueToSharedPreferences(
                this,
                Constant.KEY_PREFERENCES,
                MODE_PRIVATE,
                Constant.EMAIL
        );

        Log.d("userID", userID);
        Log.d("CourseID", courseId);
        Log.d("userEmail", userEmail);
//        LoadData loadData = new LoadData();
//        loadData.execute(userID);

        mService.getUser(userID).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                List<UserCourse> userCourses = response.body().getUser().getCourse();
               if(checkWhetherCoursePurchaseOrNotToVisibleButton(userCourses, courseId) == 0){
                   // chua dang ky
                   btnPurchase.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                            showAlertDialog();
                       }
                   });
               }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

            }
        });

        if (!courseId.equals("")) {
            mService.getLessonById(courseId).enqueue(new Callback<LessonObjResponse>() {
                @Override
                public void onResponse(Call<LessonObjResponse> call, Response<LessonObjResponse> response) {
                    if (response.isSuccessful()) {
                        Log.e("SIZE", response.body().getLessonResponse().getLessons().size() + "");
                        onGetLessonSuccess(response.body().getLessonResponse(), isPurchased);
                        String url = response.body().getLessonResponse().getImage();
                        Glide.with(CourseDetailActivity.this).load(url).into(imvCourse);

                    }
                }

                @Override
                public void onFailure(Call<LessonObjResponse> call, Throwable t) {
                }
            });
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notification");
        builder.setMessage("Transfer the money to XXX to activate course");
        builder.setCancelable(true);
        builder.setPositiveButton("Agree it", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();


                addCourseToUser();
                //TODO : Request API
//                sendRequestToActiveCourse();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void addCourseToUser() {
        AddCourseObj obj = new AddCourseObj(userEmail);
        mService.registerCourseForUser(obj, courseId).enqueue(new Callback<AddCourseResponse>() {
            @Override
            public void onResponse(Call<AddCourseResponse> call, Response<AddCourseResponse> response) {
                int status = response.body().getStatusCode();
                if (status == 200){
                    btnPurchase.setText("Waiting for activate");
                    btnPurchase.setEnabled(false);
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplication());
                    builder.setTitle("Notification");
                    builder.setMessage("Error occured! Please try again later..!!");
                    builder.setCancelable(true);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<AddCourseResponse> call, Throwable t) {

            }
        });
    }

    private void sendRequestToActiveCourse() {

    }

    private int checkWhetherCoursePurchaseOrNotToVisibleButton(List<UserCourse> userCourses, String courseID) {

        for (int i = 0; i < userCourses.size(); i++){
            if (userCourses.get(i).getCourse().getCourseID().equalsIgnoreCase(courseID)){
                if (userCourses.get(i).getStatus()!= 0){
                    btnPurchase.setVisibility(View.INVISIBLE);
                }else{
                    btnPurchase.setText("Waiting for activate");
                    btnPurchase.setEnabled(false);
                }
                return 1;
            }
        }
        return 0;
    }

    private void onGetLessonSuccess(LessonResponse lessonResponse, Boolean isPurchased) {
        addControls(lessonResponse, isPurchased);
        addEvents();
    }


    private void addEvents() {
    }

    private void addControls(LessonResponse lessonResponse, Boolean isPurchased) {
        CourseDescriptionFragment courseDescriptionFragment = CourseDescriptionFragment.newInstance(lessonResponse.getDescription());
        LessonFragment lecturesFragment = LessonFragment.newInstance(lessonResponse.getLessons(), isPurchased);
        listFragments = new ArrayList<>();

        listFragments.add(courseDescriptionFragment);
        listFragments.add(lecturesFragment);

        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), listFragments));
        tabLayout.setupWithViewPager(viewPager);
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
            case R.id.navigation_home:
                goToHomeActivity();
                break;
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

    private void goToHomeActivity() {
        Intent  intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
