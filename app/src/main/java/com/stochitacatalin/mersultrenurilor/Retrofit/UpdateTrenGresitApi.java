package com.stochitacatalin.mersultrenurilor.Retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UpdateTrenGresitApi {
    @POST("api/updateTrenGresit.php")
    @FormUrlEncoded
    Call<ResponseData> update(
            @Field("numar") String numar,
            @Field("origine") String origine,
            @Field("destinatie") String destinatie);
}
