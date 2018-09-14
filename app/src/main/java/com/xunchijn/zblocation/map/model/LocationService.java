package com.xunchijn.zblocation.map.model;

import com.xunchijn.zblocation.util.Result;
import com.xunchijn.zblocation.util.RetrofitProvider;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class LocationService {
    private LocationApi mLocationApi;

    public LocationService() {
        mLocationApi = RetrofitProvider.get().create(LocationApi.class);
    }

    //上传定位数据
    public Observable<Response<Result<LocationResult>>> reportLocation(Map<String, String> map) {
        return mLocationApi.ReportLocation(map).subscribeOn(Schedulers.io());
    }
}
