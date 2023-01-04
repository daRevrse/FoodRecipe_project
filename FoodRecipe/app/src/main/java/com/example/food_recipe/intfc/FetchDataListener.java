package com.example.pj_off.intfc;

import com.example.pj_off.model.ApiResponse;

public interface FetchDataListener {

    void didFetch(ApiResponse apiResponse, String message);
    void didError(String message);


}
