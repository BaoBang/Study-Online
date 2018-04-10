package com.example.baobang.gameduangua.gallery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.baobang.gameduangua.Constant;
import com.example.baobang.gameduangua.R;
import com.example.baobang.gameduangua.adapter.GalleryAdapter;
import com.example.baobang.gameduangua.all_course.MainActivity;
import com.example.baobang.gameduangua.data.ApiUtils;
import com.example.baobang.gameduangua.data.SOService;
import com.example.baobang.gameduangua.login.view.LoginActivity;
import com.example.baobang.gameduangua.model.Gallery;
import com.example.baobang.gameduangua.model.GalleryResponse;
import com.example.baobang.gameduangua.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private GalleryAdapter mGalleryAdapter;
    private ArrayList<Gallery> mGalleries;
    private SOService mSoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mSoService = ApiUtils.getSOService();
        addControls();
    }

    private void addControls() {
        progressBar = findViewById(R.id.progressBar);
        RecyclerView mRvGallery = findViewById(R.id.rvGallery);
        mGalleries = new ArrayList<>();
        mGalleryAdapter = new GalleryAdapter(this, mGalleries);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRvGallery.setLayoutManager(gridLayoutManager);
        mRvGallery.setAdapter(mGalleryAdapter);
        mGalleryAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onClick(int pos) {
                Intent intent = new Intent(GalleryActivity.this, FullScreenImageSliderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("POS", pos);
                bundle.putSerializable("LIST", mGalleries);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        LoadData loadData = new LoadData();
        loadData.execute();

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

    class LoadData extends AsyncTask<Void, List<Gallery>, Void>{

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
        protected void onProgressUpdate(List<Gallery>[] values) {
            super.onProgressUpdate(values);
            mGalleries.addAll(values[0]);
            mGalleryAdapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mSoService.getAllGallery().enqueue(new Callback<GalleryResponse>() {
                @Override
                public void onResponse(@NonNull Call<GalleryResponse> call, @NonNull Response<GalleryResponse> response) {
                    if(!response.isSuccessful()){
                        Toast.makeText(GalleryActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(response.body().getStatusCode() == 200){

                        publishProgress(response.body().getGalleries());
                    }else{
                        Toast.makeText(GalleryActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GalleryResponse> call, @NonNull Throwable t) {

                }
            });
            return null;
        }
    }
}
