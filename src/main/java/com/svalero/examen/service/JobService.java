package com.svalero.examen.service;

import com.svalero.examen.Constants;
import com.svalero.examen.domain.Job;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

import java.util.List;

public class JobService {

    private JobServiceApi jobServiceApi;

    public JobService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        jobServiceApi = retrofit.create(JobServiceApi.class);
    }

    /* ------------------- ENDPOINTS ------------------- */
    public Observable<List<Job>> getAllJobs() {
        return jobServiceApi.getAllJobs();
    }

    public Observable<List<Job>> getJobsByDescription(String description) {
        return jobServiceApi.getJobsByDescription(description);
    }

    public Observable<List<Job>> getJobsByLocation(String location, String type) {
        return jobServiceApi.getJobsByLocation(location, type);
    }



}
