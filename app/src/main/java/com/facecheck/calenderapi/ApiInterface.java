package com.facecheck.calenderapi;
  import java.util.List;
  import retrofit2.Call;
  import retrofit2.http.GET;
  import retrofit2.http.Query;


public interface ApiInterface {
    @GET("calendar/events")
    Call<List<Event>>getEvents(@Query("data")String date);
}

