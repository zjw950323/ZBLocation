package com.xunchijn.zblocation.util;

public interface BaseView<T> {
    void showError(String error);

    void setPresenter(T presenter);
}
