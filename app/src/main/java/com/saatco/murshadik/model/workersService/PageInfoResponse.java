package com.saatco.murshadik.model.workersService;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

public class PageInfoResponse implements Serializable {

    @SerializedName("totalCount")
    private int totalCount;

    @SerializedName("currentPage")
    private int currentPage;

    @SerializedName("totalPage")
    private int totalPage;

    @SerializedName("nextPage")
    private boolean nextPage;

    @SerializedName("previousPage")
    private boolean previousPage;

    /**
     * @param object Map<k,v> object
     */
    public PageInfoResponse(Object object) {
        if (object instanceof Map) {
            try {
                Map m = (Map) object;
                totalPage = (int) m.get("totalPage");
                currentPage = (int) m.get("currentPage");
                totalCount = (int) m.get("totalCount");
                nextPage = (boolean) m.get("nextPage");
                previousPage = (boolean) m.get("previousPage");
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    public static PageInfoResponse getInstance(Object obj){
        if (obj instanceof Map) {
            Gson g = new Gson();
            String s = g.toJson(obj);
            return g.fromJson(s, PageInfoResponse.class);
        }
        return null;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public boolean getNextPage() {
        return nextPage;
    }

    public boolean getPreviousPage() {
        return previousPage;
    }
}
