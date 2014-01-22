package org.robospock.sample;

import android.app.Application;

import org.robospock.sample.db.DatabaseHelper;
import org.robospock.sample.inject.SampleModule;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

/**
 * Created by Przemek Jakubczyk on 1/22/14.
 */
public class SampleApplication extends Application {

    private DatabaseHelper databaseHelper;
    static private SampleApplication sampleApplication;
    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        // some more action in future

        sampleApplication = this;
        databaseHelper = new DatabaseHelper(this);
        Object[] modules = getModules().toArray();
        objectGraph = ObjectGraph.create(modules);

    }

    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    public static SampleApplication getApp(){
        return sampleApplication;
    }


    protected List<Object> getModules() {
        return Arrays.<Object>asList(
                new SampleModule(this)
        );
    }

    public ObjectGraph getObjectGraph() {
        return this.objectGraph;
    }
}
