package com.repository;

import android.util.Log;

import com.repository.Utils.HttpClientUtil;
import com.repository.Utils.PropertyValue;
import com.repository.couchbase.DBClientFactory;
import com.repository.couchbase.DBOperationManagement;
import com.repository.couchbase.QueryApiResult;
import com.couchbase.lite.CouchbaseLiteException;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rich on 2016/4/15.
 */
public class RepositoryModule extends ReactContextBaseJavaModule {

    private static final String PERSISTENT_RELATION = "RELATION";
    private static final String PERSISTENT_RAW = "RAW";
    private static final String PERSISTENT_DOCUMENT = "DOCUMENT";

    private DBClientFactory dbClient;

    public RepositoryModule(ReactApplicationContext reactContext){
        super(reactContext);
        dbClient = new DBClientFactory(reactContext);
    }

    @Override
    public String getName() {
        return "RepositoryMgmt";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(PERSISTENT_RELATION, PERSISTENT_RELATION);
        constants.put(PERSISTENT_RAW, PERSISTENT_RAW);
        constants.put(PERSISTENT_DOCUMENT, PERSISTENT_DOCUMENT);

        return constants;
    }

    /**
     * Call remote API and cache request and response data.
     *
     * @param map request data from front-end
     * @see Fields
     *
     * @param successCallback
     * @param errorCallback
     */
    @ReactMethod
    public void callAPI(ReadableMap map, Callback successCallback, Callback errorCallback) {
        queryAllData();

        String url =  map.hasKey(Fields.URL) ? map.getString(Fields.URL) : "";
        String method =  map.hasKey(Fields.METHOD) ? map.getString(Fields.METHOD) : "";
        String body =  map.hasKey(Fields.BODY) ? map.getString(Fields.BODY) : "";

        Map<String, Object> docMap = null;
        try {
            docMap = callRemoteServer(url, method, body);
        } catch (IOException e) {
            Log.w(PropertyValue.LOG_REPOSITORY_TAG, "Call remote server failed, URL: " + url);

            // Call remote failed, so get data from local DB
            try {
                QueryApiResult queryResult = method.equalsIgnoreCase(HttpClientUtil.GET) ?
                DBOperationManagement.queryResponseByUrl(dbClient.getDatabaseInstance(), url) :
                DBOperationManagement.queryResponseByUrlAndBody(
                        dbClient.getDatabaseInstance(), url, body);

                if(queryResult.getDocId() == null){
                    successCallback.invoke("Database not have data!");
                } else {
                    successCallback.invoke(queryResult.getApiResponse());
                }
            } catch (Exception ex) {
                Log.e(PropertyValue.LOG_REPOSITORY_TAG, "Query local DB by url failed!, URL: " + url, ex);
                errorCallback.invoke(e.getMessage());
            }

            return;
        }

        try {
            upsert(docMap);
            successCallback.invoke(docMap.get(Fields.RESPONSE));
        }catch(Exception e){
            Log.e(PropertyValue.LOG_REPOSITORY_TAG, "Save req and res to DB failed!", e);
            errorCallback.invoke(e.getMessage());
        }
    }

    private void upsert(Map<String, Object> docMap) throws CouchbaseLiteException, IOException {
        String url = (String)docMap.get(Fields.URL);
        String body = (String)docMap.get(Fields.BODY);
        String method = (String)docMap.get(Fields.METHOD);

        QueryApiResult queryResult = method.equalsIgnoreCase(HttpClientUtil.GET) ?
                DBOperationManagement.queryResponseByUrl(dbClient.getDatabaseInstance(), url) :
                DBOperationManagement.queryResponseByUrlAndBody(
                        dbClient.getDatabaseInstance(), url, body);

        // Check document already exist or not
        if(queryResult.getDocId() == null){
            Log.i(PropertyValue.LOG_REPOSITORY_TAG, "Create doc, URL: " + url + ", BODY: " + body);
            DBOperationManagement.createDoc(dbClient.getDatabaseInstance(), docMap);
        } else {
            Log.i(PropertyValue.LOG_REPOSITORY_TAG, "Update doc, DocID: " + queryResult.getDocId() +
                    ", URL: " + url + ", BODY: " + body);
            DBOperationManagement.updateDoc(dbClient.getDatabaseInstance(), queryResult.getDocId(), docMap);
        }

    }

    private Map<String, Object> callRemoteServer(String url, String method,
                                           String body) throws IOException {
        String response = null;
        if(method.equalsIgnoreCase(HttpClientUtil.GET)){
            response = HttpClientUtil.get(url);
        } else if(method.equalsIgnoreCase(HttpClientUtil.POST)){
            response = HttpClientUtil.post(url, body);
        }

        Log.d(PropertyValue.LOG_REPOSITORY_TAG, "Call remote server success, URL: " + url +
                ", response: " + response);

        Map<String, Object> docMap = new HashMap<>();
        docMap.put(Fields.URL, url);
        docMap.put(Fields.METHOD, method.toUpperCase());
        if(method.equalsIgnoreCase(HttpClientUtil.POST)){
            docMap.put(Fields.BODY, body);
        }
        docMap.put(Fields.RESPONSE, response);

        return docMap;
    }

    /**
     * Query Document by ID.
     *
     * @param docId
     * @param successCallback
     * @param errorCallback
     */
    @ReactMethod
    public void queryDoc(String docId, Callback successCallback, Callback errorCallback) {

        String queryResult = null;
        try {
            queryResult = DBOperationManagement.queryDoc(dbClient.getDatabaseInstance(), docId);
            successCallback.invoke(queryResult);
        }catch(Exception e){
            errorCallback.invoke(e.getMessage());
        }
    }

    /**
     * Clear Database and re-create.
     */
    @ReactMethod
    public void clearDatabase() {
        try {
            dbClient.resetDatabase();
        } catch (Exception e) {
            Log.e(PropertyValue.LOG_REPOSITORY_TAG, "Delete database error!", e);
        }
    }

    private void queryAllData(){
        try {
            DBOperationManagement.queryAll(dbClient.getDatabaseInstance());
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
