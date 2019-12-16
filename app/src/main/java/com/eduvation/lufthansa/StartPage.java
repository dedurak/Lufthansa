package com.eduvation.lufthansa;

import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;
import android.view.View;

import com.eduvation.lufthansa.APIObjects.AccessTokenObject;

import io.reactivex.*;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class StartPage extends FragmentActivity {

    private static final String TAG = AppCompatActivity.class.getSimpleName();

    public static AccessTokenObject access_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);

        try {
            retrofitAccessToken();
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
/*
        try {
            retrofitAccessToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
 **/
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
