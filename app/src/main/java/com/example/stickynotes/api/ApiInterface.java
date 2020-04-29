package com.example.stickynotes.api;

import com.example.stickynotes.model.Note;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("sticky_save.php")
    Call<Note> saveNote(
        @Field("title") String title,
        @Field("note") String note,
        @Field("color") int color
    );
    @GET("sticky_notes.php")
    Call<List<Note>> getNotes();

    @FormUrlEncoded
    @POST("sticky_update.php")
    Call<Note> updateNote(
            @Field("id")int id,
            @Field("title") String title,
            @Field("note") String note,
            @Field("color") int color
    );
    @FormUrlEncoded
    @POST("sticky_delete.php")
    Call<Note> deleteNote(
            @Field("id")int id
    );



}
