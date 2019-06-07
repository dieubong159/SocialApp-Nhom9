package com.nhom9.socialapp.Fragments;

import com.nhom9.socialapp.Notifications.MyResponse;
import com.nhom9.socialapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAeAesmfE:APA91bEtoDUy0URtoberVl7vHvZzG7BxXhpuqlfTWscrpEGfHkkYRy0Gi_ptaEquWgIm49RPecQcLtRtNzngUiIZ-26tkTIEA7A14ZnD-6PsJ9aCPtz2JLnIzLJebKao-1-eqTZnkqit"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
