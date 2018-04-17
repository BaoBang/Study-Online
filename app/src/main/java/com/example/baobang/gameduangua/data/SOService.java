package com.example.baobang.gameduangua.data;

import com.example.baobang.gameduangua.model.AddCourseObj;
import com.example.baobang.gameduangua.model.AddCourseResponse;
import com.example.baobang.gameduangua.model.CategoryResponse;
import com.example.baobang.gameduangua.model.DelGalleryResponse;
import com.example.baobang.gameduangua.model.GalleryResponse;
import com.example.baobang.gameduangua.model.LessonObjResponse;
import com.example.baobang.gameduangua.model.ListCourseResponse;
import com.example.baobang.gameduangua.model.ListUserResponse;
import com.example.baobang.gameduangua.model.NewGallery;
import com.example.baobang.gameduangua.model.UserRequest;
import com.example.baobang.gameduangua.model.UserResponse;
import com.example.baobang.gameduangua.model.UserUpdateReQuest;
import com.example.baobang.gameduangua.model.stt_result;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SOService {
    @POST("user/login")
    Call<UserResponse> login(@Body UserRequest body);

    @POST("user/register")
    Call<UserResponse> register(@Body UserRequest body);

    @GET("user")
    Call<ListUserResponse> listAllUser();

    @GET("user/{userID}")
    Call<UserResponse> getUser(@Path("userID") String userID);


    @PUT("user/{userID}")
    Call<UserResponse> updateUser(@Path("userID") String userID,
                                  @Body UserUpdateReQuest body);

    @GET("course")
    Call<ListCourseResponse> listAllCourse();

    @GET("course/{courseID}")
    Call<LessonObjResponse> getLessonById(@Path("courseID") String courseID);

    @GET("category")
    Call<CategoryResponse> getAllCategories();

    @GET("gallery")
    Call<GalleryResponse> getAllGallery();

    @DELETE("gallery/{galleryId}")
    Call<stt_result> deleteGallery(@Path("galleryId") String galleryId);

    @POST("gallery")
    Call<DelGalleryResponse> addGallery(@Body NewGallery gallery);

    @PUT("gallery/{galleryId}")
    Call<DelGalleryResponse> updateGallery(@Body NewGallery gallery, @Path("galleryId") String galleryId);

    @POST("course/{courseId}")
    Call<AddCourseResponse> registerCourseForUser(@Body AddCourseObj userEmail, @Path("courseId") String courseId);
}
