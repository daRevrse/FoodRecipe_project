package com.example.pj_off.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.pj_off.R;
import com.example.pj_off.intfc.FetchDataListener;
import com.example.pj_off.model.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


public class FoodReadApi {
    Context context;
    Retrofit retrofit = new Retrofit.Builder().
            baseUrl("https://api.spoonacular.com/").
            addConverterFactory(GsonConverterFactory.create()).
            build();

    public FoodReadApi(Context context) {
        this.context = context;
    }


    public interface CallFoodApi {

        @GET("/recipes/complexSearch?")
        Call<ApiResponse> callFood(

                @Query("number") String number,
                @Query("query") String query,
                @Query("apiKey") String api_key
        );

    }

    public void getFood(FetchDataListener listener,String query) {
        int requestRecipe=80;
        CallFoodApi callFoodApi = retrofit.create(CallFoodApi.class);
        Call<ApiResponse> call = callFoodApi.callFood(String.valueOf(requestRecipe),query, context.getString(R.string.api_key));

        try {

            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                    if (!response.isSuccessful()) {
                        Toast.makeText(context, "Fail ", Toast.LENGTH_LONG).show();

                    }
                    listener.didFetch(response.body(), response.message());
                    SharedPreferences sharedPreferences = context.getSharedPreferences("db", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("number", requestRecipe);
                    editor.apply();


                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    listener.didError("Requete failled");

                }
            });
        } catch (Exception e) {

            e.printStackTrace();
        }

    }
}
