package com.saatco.murshadik;

import static org.junit.Assert.assertEquals;


import android.util.ArrayMap;

import com.google.gson.Gson;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.model.workersService.PageInfoResponse;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ApiCallTest {
    String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiIzOGJiNDM2Ny1mOTg5LTRhY2ItYTRjNi0yOGFlZTcxYjAxNzgiLCJ2YWxpZCI6IlRydWUiLCJ1c2VyaWQiOiIyMjEwMjMiLCJmdWxsbmFtZSI6IjkxMjI4NDQzNSAiLCJuYW1lIjoiOTEyMjg0NDM1IiwibGFzdF9uYW1lIjoiIiwicm9sZWlkIjoiNiIsInJvbGVuYW1lIjoiQ29uc3VsdGFudHMiLCJpc19wcm9maWxlX2NvbXBsZXRlZCI6IkZhbHNlIiwiZXhwIjoxNzExMzU5NTI2LCJpc3MiOiJodHRwOi8vbXVyc2hhZGlrLmNvbSIsImF1ZCI6Imh0dHA6Ly9tdXJzaGFkaWsuY29tIn0.Y6OXyF0OTCt2rKgiu-I0dUHvX27qCCc2p2RvJ9Pl3DE";

    @Test
    public void getAllWorkersById() throws IOException {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        assertEquals("Success", apiInterface.getAllWorkersByJobId("Bearer " + token, 1).execute().body().getMessage());
        assertEquals("Error", apiInterface.getAllWorkersByJobId("Bearer " + token, 0).execute().body().getMessage());
    }

    @Test
    public void getAllWorkerByPage() throws IOException {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        PageInfoResponse page = new PageInfoResponse(apiInterface.getAllWorkerByPage("Bearer " + token, 20, 1).execute().body().getInfo());

        assertEquals("Success", page.getCurrentPage());
    }


    @Test
    public void getAllNationalities() throws IOException {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        assertEquals("Success", apiInterface.getAllNationalities("Bearer " + token).execute().body().getMessage());
    }


    @Test
    public void getWorkerById() throws IOException {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        assertEquals("Success", apiInterface.getWorkerById("Bearer " + token, 1).execute().body().getMessage());
        assertEquals("Error", apiInterface.getWorkerById("Bearer " + token, 0).execute().body().getMessage());
    }


    @Test
    public void addWorkerExperience() throws IOException {

        String s = "{\r\n    \"WorkerId\":\"1\",\r\n    \"Description\":\"asdef\",\r\n    \"FromDate\":\"2021-12-10\",\r\n    \"ToDate\":\"2021-12-20\"\r\n}\r\n";

        RequestBody body = RequestBody.create(s, okhttp3.MediaType.parse("application/json; charset=utf-8"));

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        assertEquals("Success", apiInterface.addWorkerExperience("Bearer " + token, body).execute().body().getMessage());
        s = "{\r\n    \"WorkerId\":\"99\",\r\n    \"Description\":\"asdef\",\r\n    \"FromDate\":\"2021-12-10\",\r\n    \"ToDate\":\"2021-12-20\"\r\n}\r\n";
        body = RequestBody.create(s, okhttp3.MediaType.parse("application/json; charset=utf-8"));

        assertEquals("Error", apiInterface.addWorkerExperience("Bearer " + token, body).execute().body().getMessage());
    }


}
