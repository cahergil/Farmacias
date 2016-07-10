package com.chernandezgil.farmacias.iretrofit;

import com.chernandezgil.farmacias.model.ResponseNearbyPlaces;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;


/**
 * Created by Carlos on 06/07/2016.
 */
public class PlacesApiManager {

    public static IPlaces myService;
    public interface IPlaces {
        //in retrofit 2 should start without /
        @GET("maps/api/place/nearbysearch/json?")
        Observable<Response<ResponseNearbyPlaces>> getPharmacy(@Query("location") String location, @Query("radius") String radius,
                                                               @Query("types") String types, @Query("sensor")String sensor, @Query("key") String server_key);

    }
    public static IPlaces getMyApiService(){
        if(myService==null) {
            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return myService=retrofit.create(IPlaces.class);
        } else {
            return myService;
        }
    }

}
