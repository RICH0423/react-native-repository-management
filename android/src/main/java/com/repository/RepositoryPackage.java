package com.repository;

import com.repository.Utils.PropertyReader;
import com.repository.Utils.PropertyValue;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Created by rich on 2016/4/6.
 */
public class RepositoryPackage implements ReactPackage {

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        initPropValue(reactContext);

        List<NativeModule> modules = new ArrayList<>();
        modules.add(new RepositoryModule(reactContext));

        return modules;
    }

    private void initPropValue(ReactApplicationContext reactContext){
        PropertyReader propReader = new PropertyReader(reactContext);
        Properties propFile = propReader.getMyProperties(PropertyReader.PROPERTY_FILE);
        PropertyValue.DB_NAME = propFile.getProperty("DB.NAME", "repostory_db");
        PropertyValue.LOG_DB_TAG = propFile.getProperty("LOG.DB.TAG", "Repository-Couchbase");
        PropertyValue.LOG_REPOSITORY_TAG = propFile.getProperty("LOG.REPOSITORY.TAG", "Repository-Management");
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }
}
