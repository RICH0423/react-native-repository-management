package com.repository.couchbase;

/**
 * Created by rich on 2016/4/25.
 */
public class QueryApiResult {

    private String docId;
    private String apiResponse;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getApiResponse() {
        return apiResponse;
    }

    public void setApiResponse(String apiResponse) {
        this.apiResponse = apiResponse;
    }

}
