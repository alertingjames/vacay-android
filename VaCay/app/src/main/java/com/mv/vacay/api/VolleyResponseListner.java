package com.mv.vacay.api;

/**
 * Created by a on 2017.01.03.
 */

public interface VolleyResponseListner {
    void onError(String message);

    void onResponse(Object response);
}
