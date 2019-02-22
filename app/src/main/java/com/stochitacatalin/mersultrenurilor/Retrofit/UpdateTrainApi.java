package com.stochitacatalin.mersultrenurilor.Retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UpdateTrainApi {
    @POST("api/updateLocTrain.php")
    @FormUrlEncoded
    Call<ResponseData> update(
            @Field("idtren") String idtren,
            @Field("lat") String lat,
            @Field("lon") String lon,
            @Field("time") String time,
            @Field("acuracy") int acuracy);
}
