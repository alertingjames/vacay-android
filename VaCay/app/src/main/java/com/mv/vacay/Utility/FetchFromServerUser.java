package com.mv.vacay.Utility;

public interface FetchFromServerUser {
    void onPreFetch();
    void onFetchCompletion(String string, int id);
}