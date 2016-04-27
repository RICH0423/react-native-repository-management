package com.repository.couchbase;

import com.repository.Utils.HttpClientUtil;
import com.repository.Fields;
import com.couchbase.lite.Database;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by rich on 2016/4/19.
 */
public class ViewManagement {

    public static final String URL_VIEW = "url";
    public static final String URL_BODY_VIEW = "urlAndBody";

    /**
     * Create view base on mapping url field.
     *
     * @param database
     * @return View
     */
    public static View createGetUrlView(Database database) {
        View urlView = database.getView(URL_VIEW);

        urlView.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                String method = (String) document.get(Fields.METHOD);
                if(method.equalsIgnoreCase(HttpClientUtil.GET)){
                    emitter.emit(
                            (String) document.get(Fields.URL),
                            (String) document.get(Fields.RESPONSE)
                    );
                }
            }

        }, "1");

        return urlView;
    }

    /**
     * Create view base on mapping url and body field.
     *
     * @param database
     * @return View
     */
    public static View createUrlAndBodyView(Database database) {
        View urlView = database.getView(URL_BODY_VIEW);

        urlView.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                List<Object> keys = new ArrayList<>();
                keys.add((String) document.get(Fields.URL));
                keys.add((String) document.get(Fields.BODY));

                emitter.emit(keys, (String) document.get(Fields.RESPONSE));
            }
        }, "1");

        return urlView;
    }


}
