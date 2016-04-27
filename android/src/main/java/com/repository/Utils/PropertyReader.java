package com.repository.Utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by rich on 2016/4/18.
 */
public class PropertyReader {

    public static final String PROPERTY_FILE = "repository.properties";

    private Context context;
    private Properties properties;

    public PropertyReader(Context context) {
        this.context = context;
        properties = new Properties();
    }

    public Properties getMyProperties(String file) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(file);
            properties.load(inputStream);

        } catch (Exception e) {
            Log.e(PropertyValue.LOG_REPOSITORY_TAG, "Load property file error!", e);
        }

        return properties;
    }

    public static String getValue(String key,Context context, String file) {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();

        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(file);
            properties.load(inputStream);
        } catch (IOException e) {
            Log.e(PropertyValue.LOG_REPOSITORY_TAG, "Read property value error!", e);
        }

        return properties.getProperty(key);
    }
}
