package com.xunchijn.zblocation.map.presenter;

import android.content.Context;
import android.util.Log;

import com.xunchijn.zblocation.map.model.LocationResult;
import com.xunchijn.zblocation.map.model.LocationService;
import com.xunchijn.zblocation.util.Result;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import retrofit2.Response;

public class LocationPresenter implements LocationContrast.Presenter {
    private String TAG = "LocationPresenter";
    private LocationContrast.View mView;
    private LocationService mLocationService;
    private Observer<Response<Result<LocationResult>>> mObserver;

    public LocationPresenter(LocationContrast.View view, Context context) {
        mView = view;
        mView.setPresenter(this);
        mLocationService = new LocationService();
    }

    @Override
    public void reportLocation(String number, String lon, String lat, String lonLat, String address, String timeStamp) {
        Map<String, String> map = new HashMap<>();
        map.put("account", number);
        map.put("longitude", lon);
        map.put("latitude", lat);
        map.put("longitude_latitude", lonLat);
        map.put("address", address);
        map.put("timestamp", timeStamp);
        mLocationService.reportLocation(map).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<Result<LocationResult>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(Response<Result<LocationResult>> resultResponse) {
                        if (resultResponse.isSuccessful()) {
                            parseResult(resultResponse.body());
                        } else {
                            mView.showError(resultResponse.message());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });
    }

    private void parseResult(Result<LocationResult> result) {
        if (result.getCode() == 200) {
            if (result.getData() == null) {
                return;
            }
            if (result.getData().getResultStatus() != null) {
                Log.d(TAG, "parseResult: " + result.getData().getResultStatus());
                mView.reportLocationSuccess();
            } else {
                mView.showError("上传定位数据失败");
            }
        } else {
            mView.showError(result.getMessage());
        }
    }
}
