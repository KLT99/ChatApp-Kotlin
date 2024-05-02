package com.uintecs.klt.chatapp_kotlin.Notificaciones;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA8upxz0M:APA91bFW2FSjQ0nbLMBdTqwYwD3FW1r0yCRdJFzsCdFZzmfq-lBxIp2lM0WnFRhEQ09tLiUnpMARLlmwf2eMErekylebo79aHrv_tFX012DrpBuC_Z9oAggUujvu5flj6L8hCU15pyto"

    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
