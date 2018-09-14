package com.xunchijn.zblocation.map.model;

import com.xunchijn.zblocation.util.Result;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LocationApi {
    //上传定位数据
    @FormUrlEncoded
    @POST("API/Location/AddData")
    Observable<Response<Result<LocationResult>>> ReportLocation(@FieldMap Map<String, String> map);
}
