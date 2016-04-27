package com.repository.couchbase;

import android.util.Log;

import com.repository.Utils.JsonUtil;
import com.repository.Utils.PropertyValue;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by rich on 2016/4/15.
 */
public class DBOperationManagement {

    /**
     * Create Document by Map.
     *
     * @param database
     * @param data
     * @return String Document ID
     */
    public static String createDoc(Database database, Map<String, Object> data) {
        // Create a new document and add data
        Document document = database.createDocument();
        String documentId = document.getId();

        try {
            // Save the properties to the document
            document.putProperties(data);
        } catch (CouchbaseLiteException e) {
            Log.e(PropertyValue.LOG_DB_TAG, "Create Document error!", e);
        }

        Log.i(PropertyValue.LOG_DB_TAG, "Create Document success, ID: " + documentId);
        return documentId;
    }

    /**
     * Update Document by documentId.
     *
     * @param database
     * @param documentId
     * @param data
     */
    public static void updateDoc(Database database, String documentId, Map<String, Object> data) {
        Document document = database.getDocument(documentId);

        try {
            // Update the document with more data
            Map<String, Object> updatedProperties = new HashMap<String, Object>();
            updatedProperties.putAll(document.getProperties());
            updatedProperties.putAll(data);

            document.putProperties(updatedProperties);
        } catch (CouchbaseLiteException e) {
            Log.e(PropertyValue.LOG_DB_TAG, "Update Document error!", e);
        }
    }

    /**
     * Delete document by documentId.
     *
     * @param database
     * @param documentId
     * @return boolean indicate whether deleted or not
     */
    public static boolean deleteDoc(Database database, String documentId) {
        Document document = database.getDocument(documentId);

        // delete the document
        try {
            document.delete();
            Log.d(PropertyValue.LOG_DB_TAG, "Deleted document, deletion status = " + document.isDeleted());
        } catch (CouchbaseLiteException e) {
            Log.e (PropertyValue.LOG_DB_TAG, "Cannot delete document", e);
        }

        return document.isDeleted();
    }

    /**
     * Query document by documentId.
     * @param database
     * @param documentId
     */
    public static String queryDoc(Database database, String documentId) {
        // retrieve the document from the database
        Document retrievedDocument = database.getDocument(documentId);
        Log.d(PropertyValue.LOG_DB_TAG, "Document [" + String.valueOf(retrievedDocument.getProperties()) + " ]");

        return JsonUtil.toJson(retrievedDocument.getProperties());
    }

    /**
     * Delete database.
     *
     * @param database
     * @return
     * @throws CouchbaseLiteException
     */
    public static void deleteDb(Database database) {
        try {
            database.delete();
        } catch (CouchbaseLiteException e) {
            Log.e(PropertyValue.LOG_DB_TAG, "Cannot delete database", e);
        }

        Log.i(PropertyValue.LOG_DB_TAG, "Delete database success!");
    }

    /**
     * Query response by url.
     *
     * @param database
     * @param url
     * @return QueryApiResult
     */
    public static QueryApiResult queryResponseByUrl(Database database, String url) {
        Query urlQuery = ViewManagement.createGetUrlView(database).createQuery();
        urlQuery.setDescending(true);
        urlQuery.setStartKey(url);
        urlQuery.setEndKey(url);
        urlQuery.setLimit(1);

        return executeQuery(urlQuery);
    }

    /**
     * Query response by url and body field.
     *
     * @param database
     * @param url
     * @param body
     * @return QueryApiResult
     */
    public static QueryApiResult queryResponseByUrlAndBody(Database database, String url, String body) {
        List<String> filter = new ArrayList<>();
        filter.add(url);
        filter.add(body);

        Query urlQuery = ViewManagement.createUrlAndBodyView(database).createQuery();
        urlQuery.setDescending(true);
        urlQuery.setStartKey(filter);
        urlQuery.setEndKey(filter);
        urlQuery.setLimit(1);

        return executeQuery(urlQuery);
    }

    private static QueryApiResult executeQuery(Query query){
        QueryApiResult result = new QueryApiResult();

        try {
            QueryEnumerator results = query.run();
            Log.d(PropertyValue.LOG_DB_TAG, "Search count: " + results.getCount());

            for (Iterator<QueryRow> it = results; it.hasNext();) {
                QueryRow row = it.next();

                result.setDocId(row.getDocumentId());
                result.setApiResponse((String) row.getValue());
            }
        } catch (CouchbaseLiteException e) {
            Log.e(PropertyValue.LOG_DB_TAG, "Error querying view.", e);
        }

        return result;
    }

    public static int countByUrlAndBody(Database database, String url, String body) {
        List<String> filter = new ArrayList<>();
        filter.add(url);
        filter.add(body);

        Query urlQuery = ViewManagement.createUrlAndBodyView(database).createQuery();
        urlQuery.setDescending(true);
        urlQuery.setStartKey(filter);
        urlQuery.setEndKey(filter);

        int result = 0;
        try {
            QueryEnumerator results = urlQuery.run();
            result = results.getCount();
            Log.d(PropertyValue.LOG_DB_TAG, "Count URL: " + url + ", BODY: " + body + ", result: " + result);
        } catch (CouchbaseLiteException e) {
            Log.e(PropertyValue.LOG_DB_TAG, "Error counting view.", e);
        }

        return result;
    }

    public static void queryAll(Database database) {
        Query allQuery = database.createAllDocumentsQuery();
        try {
            QueryEnumerator results = allQuery.run();
            Log.i(PropertyValue.LOG_DB_TAG, "===== Query all doc count: " + results.getCount() + " =====");

            for (Iterator<QueryRow> it = results; it.hasNext();) {
                QueryRow row = it.next();
                queryDoc(database, row.getDocumentId());
            }
        } catch (CouchbaseLiteException e) {
            Log.e(PropertyValue.LOG_DB_TAG, "Query all doc error!", e);
        }
    }

}
