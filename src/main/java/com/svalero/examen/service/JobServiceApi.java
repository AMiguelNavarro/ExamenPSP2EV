package com.svalero.examen.service;

import com.svalero.examen.domain.Job;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

import java.util.List;

public interface JobServiceApi {

    @GET("positions.json")
    Observable<List<Job>> getAllJobs();

    @GET("positions.json")
    Observable<List<Job>> getJobsByDescription(@Query("description") String description);

    @GET("positions.json")
    Observable<List<Job>> getJobsByLocation(@Query("location") String location, @Query("full_time") String type);


}
