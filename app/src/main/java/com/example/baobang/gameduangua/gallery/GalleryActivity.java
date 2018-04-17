package com.example.baobang.gameduangua.gallery;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baobang.gameduangua.Constant;
import com.example.baobang.gameduangua.R;
import com.example.baobang.gameduangua.adapter.GalleryAdapter;
import com.example.baobang.gameduangua.all_course.MainActivity;
import com.example.baobang.gameduangua.data.ApiUtils;
import com.example.baobang.gameduangua.data.SOService;
import com.example.baobang.gameduangua.login.view.LoginActivity;
import com.example.baobang.gameduangua.model.DelGalleryResponse;
import com.example.baobang.gameduangua.model.Gallery;
import com.example.baobang.gameduangua.model.GalleryResponse;
import com.example.baobang.gameduangua.model.NewGallery;
import com.example.baobang.gameduangua.model.stt_result;
import com.example.baobang.gameduangua.profile.ProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryActivity extends AppCompatActivity{

    private ProgressBar progressBar;
    private GalleryAdapter mGalleryAdapter;
    private ArrayList<Gallery> mGalleries;
    private SOService mSoService;
    private FloatingActionButton btnAddGallery;

    private Uri filePath = null;
    public static final int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    public static final String TAG = "TAG_LOG";

    private StorageReference storageRef;
    private FirebaseStorage mFirebaseStor;

    public String imageUrlFromCloudStorage = "";

    private Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mSoService = ApiUtils.getSOService();
        addControls();
    }

    private void addControls() {
        mFirebaseStor = FirebaseStorage.getInstance();
        storageRef = mFirebaseStor.getReference();
        btnAddGallery = findViewById(R.id.fabAdd);
        progressBar = findViewById(R.id.progressBar);
        RecyclerView mRvGallery = findViewById(R.id.rvGallery);
        mGalleries = new ArrayList<>();
        mGalleryAdapter = new GalleryAdapter(this, mGalleries);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRvGallery.setLayoutManager(gridLayoutManager);
        mRvGallery.setAdapter(mGalleryAdapter);
        mGalleryAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onClick(int pos, boolean isLongClick) {
                if (!isLongClick) {
                    Intent intent = new Intent(GalleryActivity.this, FullScreenImageSliderActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("POS", pos);
                    bundle.putSerializable("LIST", mGalleries);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    selectOpstionEditDelete(mGalleries.get(pos));
                }
            }
        });
        LoadData loadData = new LoadData();
        loadData.execute();

        // ADD GALLERY
        btnAddGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(GalleryActivity.this);
                final View dialogView = layoutInflater.inflate(R.layout.dialog_new_gallery, null);
                final EditText edGalleryName = dialogView.findViewById(R.id.edGalleryName);
                final EditText edGalleryDes = dialogView.findViewById(R.id.edGalleryDes);
                final TextView tvGetImg = dialogView.findViewById(R.id.tvGetImg);
                final Button btnCancel = dialogView.findViewById(R.id.btnCancel);
                btnCreate = dialogView.findViewById(R.id.btnCreate);
                btnCreate.setEnabled(false);
                tvGetImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectImage();
                    }
                });
                final  AlertDialog.Builder builder = new AlertDialog.Builder(GalleryActivity.this);
                builder.setView(dialogView);

                final AlertDialog dialog = builder.create();
                dialog.show();

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                btnCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String galleryName = edGalleryName.getText().toString().trim();
                        String galleryDes = edGalleryDes.getText().toString().trim();

                        if (!TextUtils.isEmpty(galleryDes) && !TextUtils.isEmpty(galleryName)
                                && !TextUtils.isEmpty(imageUrlFromCloudStorage)){
                            NewGallery newGallery = new NewGallery(galleryName, imageUrlFromCloudStorage, galleryDes);
                            mSoService.addGallery(newGallery).enqueue(new Callback<DelGalleryResponse>() {
                                @Override
                                public void onResponse(Call<DelGalleryResponse> call, Response<DelGalleryResponse> response) {
                                    if (response.body().getStatuscode() == 200){
                                        mGalleries.add(response.body().getGallery());
                                        mGalleryAdapter.setmGalleries(mGalleries);
                                        mGalleryAdapter.notifyDataSetChanged();
                                        Toast.makeText(GalleryActivity.this, "Add Successfully", Toast.LENGTH_LONG).show();
                                    }
                                    dialog.dismiss();
                                }

                                @Override
                                public void onFailure(Call<DelGalleryResponse> call, Throwable t) {

                                }
                            });
                        }
                    }
                });



            }
        });

    }


    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(GalleryActivity.this);
        builder.setTitle("Add Photo!");
        final Boolean result = ContextCompat.checkSelfPermission( this, android.Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED;

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (items[i].equals("Take Photo")){
                    if (checkCamPermission()){
                        btnCreate.setEnabled(false);
                        cameraIntent();
                    }
                }else if (items[i].equals("Choose from Library")) {
                    if (checkGaleryPermission()){
                        btnCreate.setEnabled(false);
                        galleryIntent();
                    }
                }else if (items[i].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private Boolean checkCamPermission () {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA )
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},REQUEST_CAMERA);
            return false;
        }else{
            return true;
        }
    }

    private Boolean checkGaleryPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, SELECT_FILE);
            return false;
        }else {
            return true;
        }
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED){
                        cameraIntent();
                    }
                }
                else {
                    Toast.makeText(this, "Can't get camera because of permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            case SELECT_FILE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    galleryIntent();
                }
                else {
                    Toast.makeText(this, "Can't get location because of permission denied", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        filePath = Uri.fromFile(destination);

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        uploadImageCaptureAndUpdateDB(filePath);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            filePath = data.getData();
            Log.d("FILE_PATH", filePath.toString());
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        uploadImageInGaleryAndUpdateUser(filePath);
    }

    private void uploadImageInGaleryAndUpdateUser(Uri uri) {
        if (uri != null) {
            StorageReference ref = storageRef.child("Photos/" + UUID.randomUUID().toString());
            ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Uri downloadUri = taskSnapshot.getDownloadUrl();
                            imageUrlFromCloudStorage = downloadUri.toString();
                            btnCreate.setEnabled(true);
                            Log.d("imageUrlCloud_Galery", imageUrlFromCloudStorage);
                            Toast.makeText(GalleryActivity.this, "Upload Image in Galery Successfully", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(GalleryActivity.this, "Upload Galery Failed", Toast.LENGTH_LONG).show();
                        }
                    });
        }else{
            Log.d("URI NULL", "URI NULL");
        }

    }

    private String uploadImageCaptureAndUpdateDB(Uri uri){
        if (uri != null) {
            StorageReference ref = storageRef.child("Photos").child(uri.getLastPathSegment());
            ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUri = taskSnapshot.getDownloadUrl();
                            imageUrlFromCloudStorage = downloadUri.toString();
                            btnCreate.setEnabled(true);
                            Log.d("imageUrlCloud_Capture", imageUrlFromCloudStorage);
                            Toast.makeText(GalleryActivity.this, "Upload Image Capture Successfully", Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(GalleryActivity.this, "Upload Capture Failed", Toast.LENGTH_LONG).show();
                        }
                    });
        }
        return imageUrlFromCloudStorage;
    }

    private void selectOpstionEditDelete(Gallery gallery) {
        final CharSequence[] items = {"Edit Gallery", "Delete Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(GalleryActivity.this);
        builder.setTitle("Options");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (items[i].equals("Edit Gallery")) {
                    editGallery(gallery);
                } else if (items[i].equals("Delete Gallery")) {
                    deleteGallery(gallery);
                }
            }
        });
        builder.show();
    }

    private void deleteGallery(Gallery gallery) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notification");
        builder.setMessage("Do you want to delete this gallery?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSoService.deleteGallery(gallery.getId()).enqueue(new Callback<stt_result>() {
                    @Override
                    public void onResponse(Call<stt_result> call, Response<stt_result> response) {
                        if (response.body().getStatusCode() == 200){
                            mGalleries.remove(gallery);
                            mGalleryAdapter.setmGalleries(mGalleries);
                            mGalleryAdapter.notifyDataSetChanged();
                            Toast.makeText(GalleryActivity.this, "Delete Successfully", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<stt_result> call, Throwable t) {
                        Toast.makeText(GalleryActivity.this, "Delete Failed", Toast.LENGTH_LONG).show();

                    }
                });
                dialogInterface.dismiss();

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

    private void editGallery(Gallery gallery) {
        LayoutInflater layoutInflater = LayoutInflater.from(GalleryActivity.this);
        final View dialogView = layoutInflater.inflate(R.layout.dialog_new_gallery, null);
        final EditText edGalleryName = dialogView.findViewById(R.id.edGalleryName);
        final EditText edGalleryDes = dialogView.findViewById(R.id.edGalleryDes);
        final TextView tvGetImg = dialogView.findViewById(R.id.tvGetImg);
        final Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        btnCreate = dialogView.findViewById(R.id.btnCreate);

        final  AlertDialog.Builder builder = new AlertDialog.Builder(GalleryActivity.this);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.show();

        edGalleryName.setText(gallery.getName());
        edGalleryDes.setText(gallery.getDescription());
        btnCreate.setText("Update");

        tvGetImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectImage();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String galleryName = edGalleryName.getText().toString().trim();
                String galleryDes = edGalleryDes.getText().toString().trim();
                NewGallery newGallery;
                if (!TextUtils.isEmpty(galleryDes) && !TextUtils.isEmpty(galleryName)){
                    if (TextUtils.isEmpty(imageUrlFromCloudStorage)){
                        String url = gallery.getPhotUrl();
                        newGallery = new NewGallery(galleryName, url, galleryDes);
                    }else{
                       newGallery = new NewGallery(galleryName, imageUrlFromCloudStorage, galleryDes);
                    }
                    mSoService.updateGallery(newGallery, gallery.getId()).enqueue(new Callback<DelGalleryResponse>() {
                        @Override
                        public void onResponse(Call<DelGalleryResponse> call, Response<DelGalleryResponse> response) {
                            if (response.body().getStatuscode() == 200){
                                mGalleries.set(mGalleries.indexOf(gallery), response.body().getGallery());
                                mGalleryAdapter.setmGalleries(mGalleries);
                                mGalleryAdapter.notifyDataSetChanged();
                                Toast.makeText(GalleryActivity.this, "Update Successfully", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }else{
                                Toast.makeText(GalleryActivity.this, "Deo co statuscode", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<DelGalleryResponse> call, Throwable t) {

                        }
                    });
                    dialog.dismiss();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_home:
                goToHomeActivity();
                break;
            case R.id.profile:
                goToProfileActivity();
                break;
            case R.id.gallery:
//                goToGalleryActivity();
                break;
            case R.id.logout:
                logOut();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToGalleryActivity() {
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);
    }

    private void goToProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void logOut() {
        SharedPreferences preferences = getSharedPreferences(
                Constant.KEY_PREFERENCES,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constant.USER, "");
        editor.apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    class LoadData extends AsyncTask<Void, List<Gallery>, Void> {

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
                    if (!response.isSuccessful()) {
                        Toast.makeText(GalleryActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (response.body().getStatusCode() == 200) {

                        publishProgress(response.body().getGalleries());
                    } else {
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
