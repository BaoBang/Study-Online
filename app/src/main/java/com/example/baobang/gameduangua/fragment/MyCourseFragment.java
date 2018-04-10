package com.example.baobang.gameduangua.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.baobang.gameduangua.Constant;
import com.example.baobang.gameduangua.R;
import com.example.baobang.gameduangua.adapter.UserCourseListAdapter;
import com.example.baobang.gameduangua.all_course.detail.CourseDetailActivity;
import com.example.baobang.gameduangua.data.ApiUtils;
import com.example.baobang.gameduangua.data.SOService;
import com.example.baobang.gameduangua.model.User;
import com.example.baobang.gameduangua.model.UserCourse;
import com.example.baobang.gameduangua.model.UserResponse;
import com.example.baobang.gameduangua.utils.AppUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class MyCourseFragment extends Fragment {
    ProgressBar progressBar;
    RecyclerView mRvCourse;
    UserCourseListAdapter mCourseListAdapter;
    List<UserCourse> mCourses;

    private SOService mSoService;

    public MyCourseFragment() {
        // Required empty public constructor
    }

    public static MyCourseFragment newInstance() {
        return new MyCourseFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_course, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        mRvCourse = view.findViewById(R.id.rcCourses);
        mCourses = new ArrayList<>();
        mCourseListAdapter = new UserCourseListAdapter(getContext(), mCourses);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        mRvCourse.setLayoutManager(layoutManager);
        mRvCourse.setAdapter(mCourseListAdapter);
        mCourseListAdapter.setOnItemClickListener(new UserCourseListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int pos) {

                UserCourse course = mCourses.get(pos);

                AppUtils.setValueToSharedPreferences(
                        getContext(),
                        Constant.KEY_PREFERENCES,
                        MODE_PRIVATE,
                        Constant.COURSE_ID,
                        course.getCourse().getCourseID()
                );

                Intent detailIntent = new Intent(getContext(), CourseDetailActivity.class);
                detailIntent.putExtra(Constant.COURSE_ID, course.getCourse().getCourseID());
                getContext().startActivity(detailIntent);
            }
        });

        mSoService = ApiUtils.getSOService();
        String userId = getUserId();
        LoadData loadData = new LoadData();
        loadData.execute(userId);

        return view;
    }
    private String getUserId(){
        SharedPreferences preferences = getContext().getSharedPreferences(Constant.KEY_PREFERENCES, Context.MODE_PRIVATE);
        String userString = preferences.getString(Constant.USER, "");
        Gson gson = new Gson();
        User user = gson.fromJson(userString, User.class);
        return user.getId();
    }

    class LoadData extends AsyncTask<String, List<UserCourse>, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(List<UserCourse>[] values) {
            super.onProgressUpdate(values);
            mCourses.clear();
            mCourses.addAll(values[0]);
            mCourseListAdapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(String... strings) {
            mSoService.getUser(strings[0]).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                    if(response.isSuccessful()){
                        publishProgress(response.body().getUser().getCourse());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {

                }
            });
            return null;
        }
    }
}
