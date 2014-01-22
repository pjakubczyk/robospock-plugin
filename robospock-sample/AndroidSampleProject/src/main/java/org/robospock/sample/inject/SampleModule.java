package org.robospock.sample.inject;

import com.j256.ormlite.dao.Dao;

import org.robospock.sample.MainActivity;
import org.robospock.sample.SampleApplication;
import org.robospock.sample.db.Account;

import java.sql.SQLException;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Przemek Jakubczyk on 1/22/14.
 */
@Module(injects = MainActivity.class)
public class SampleModule {

    private SampleApplication app;

    public SampleModule(SampleApplication app) {
        this.app = app;
    }

    @Provides
    public Dao<Account, Integer> ProvideAccountDao()  {
        try {
            return app.getDatabaseHelper().getAccountDao();
        } catch (SQLException e) {
            return null;
        }
    }
}
