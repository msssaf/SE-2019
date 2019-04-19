package com.Lieyang.Chef.Network;

import okhttp3.HttpUrl;
import okhttp3.Request;

public class RequestProvider {

    public static final String BASE_URL = "http://ec2-52-39-140-122.us-west-2.compute.amazonaws.com/api/";



    public static Request getCurrentOrdersRequest(){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "orders").newBuilder();

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("Content-Type", "application/json")
                .get()
                .build();
        return request;
    }

    public static Request getCompletedOrdersRequest(){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "orders").newBuilder();

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("Content-Type", "application/json")
                .get()
                .build();
        return request;
    }

    public static Request getCompleteOrderRequest(String id){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "orders/" + id + "/complete").newBuilder();

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("Content-Type", "application/json")
                .get()
                .build();
        return request;
    }

    public static Request getUpdateFirebaseTokenRequest(String userId, String firebaseToken){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "users/" + userId + "/firebase/" + firebaseToken).newBuilder();

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("Content-Type", "application/json")
                .get()
                .build();
        return request;
    }
}
