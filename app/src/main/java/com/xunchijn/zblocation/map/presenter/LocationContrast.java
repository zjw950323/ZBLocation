package com.xunchijn.zblocation.map.presenter;

import com.xunchijn.zblocation.util.BaseView;

public interface LocationContrast {
    interface Presenter {
        void reportLocation(String number, String lon, String lat, String address);
    }

    interface View extends BaseView<Presenter> {
        void reportLocationSuccess();

        void report();
    }
}
