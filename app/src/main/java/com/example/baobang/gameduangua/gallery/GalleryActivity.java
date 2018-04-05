package com.example.baobang.gameduangua.gallery;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.baobang.gameduangua.R;
import com.example.baobang.gameduangua.adapter.GalleryAdapter;
import com.example.baobang.gameduangua.data.ApiUtils;
import com.example.baobang.gameduangua.data.SOService;
import com.example.baobang.gameduangua.model.Gallery;
import com.example.baobang.gameduangua.model.GalleryResponse;

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
