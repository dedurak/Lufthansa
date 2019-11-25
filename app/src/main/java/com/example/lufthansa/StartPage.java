package com.example.lufthansa;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.lufthansa.APIObjects.AccessTokenObject;
import com.example.lufthansa.APIObjects.ApiDepartures;
import com.example.lufthansa.APIObjects.Flight;
import com.example.lufthansa.APIObjects.ApiFlightResults;
import com.example.lufthansa.MainFragments.FragmentsAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import io.reactivex.*;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import org.json.JSONObject;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.*;

public class StartPage extends FragmentActivity {

    private static final String TAG = AppCompatActivity.class.getSimpleName();

    public static AccessTokenObject access_token;
    public static String nextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        try {
            retrofitAccessToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AccessTokenObject getToken() {
        return access_token;
    }

    // Retrofit call for access token
    private void retrofitAccessToken() throws IOException {

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.lufthansa.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        ApiService service = retrofit.create(ApiService.class);

        Single<retrofit2.Response<AccessTokenObject>> retroftiApiToken = service.getAccessToken(
                BuildConfig.ApiKey,
                BuildConfig.ApiSec,
                "client_credentials");

        retroftiApiToken.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<AccessTokenObject>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: " + d.toString());
                    }

                    @Override
                    public void onSuccess(retrofit2.Response<AccessTokenObject> object) {
                        Log.d(TAG, "onSuccess - AccessToken: " + object.body().getAccess_token());
                        access_token = object.body();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError");
                    }
                });
    }

    public void backToLastFrag(View view) {
        this.onBackPressed();
    }

}
