package com.example.baobang.gameduangua.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.baobang.gameduangua.R;
import com.example.baobang.gameduangua.adapter.CategoryListAdapter;
import com.example.baobang.gameduangua.data.ApiUtils;
import com.example.baobang.gameduangua.data.SOService;
import com.example.baobang.gameduangua.model.Category;
import com.example.baobang.gameduangua.model.CategoryResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListCourseFragment extends Fragment {

    private  CategoryListAdapter mListAdapter;
    private  List<Category> mCategories;
    private  ProgressBar progressBar;
    private  SOService mService;


    public  ListCourseFragment() {
    }

    public static ListCourseFragment newInstance() {

        return new ListCourseFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_course, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        RecyclerView mRvCategories = view.findViewById(R.id.rvListCourse);
        mRvCategories.setHasFixedSize(true);
        mService = ApiUtils.getSOService();
        mCategories = new ArrayList<>();
        mListAdapter = new CategoryListAdapter(getContext(), mCategories);
        mRvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvCategories.setNestedScrollingEnabled(false);
        mRvCategories.setAdapter(mListAdapter);
       LoadData loadData = new LoadData();
       loadData.execute();

        return view;
    }

    class LoadData extends AsyncTask<Void, List<Category>, Void>{
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
        protected void onProgressUpdate(List<Category>[] values) {
            mCategories.addAll(values[0]);
            mListAdapter.notifyDataSetChanged();
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mService.getAllCategories().enqueue(new Callback<CategoryResponse>() {
                @Override
                public void onResponse(@NonNull Call<CategoryResponse> call,
                                       @NonNull Response<CategoryResponse> response) {

                    if (response.isSuccessful()){
                        int statusCode = response.body().getStatusCode();
                        if (statusCode == 200){
//                            mCategories.addAll(response.body().getResults());
//                            mListAdapter.notifyDataSetChanged();
                            List<Category> categories = response.body().getResults();
                            publishProgress(categories);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CategoryResponse> call, @NonNull Throwable t) {

                }
            });

            return null;
        }
    }
}
