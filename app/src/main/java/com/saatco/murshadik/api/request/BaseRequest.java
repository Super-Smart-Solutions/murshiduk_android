package com.saatco.murshadik.api.request;


import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;

public class BaseRequest {

    APIInterface apiInterface;

    public BaseRequest() {
        apiInterface = APIClient.getClient().create(APIInterface.class);
    }
}
