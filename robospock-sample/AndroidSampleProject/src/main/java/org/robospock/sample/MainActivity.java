package org.robospock.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

import org.robospock.sample.db.Account;
import org.robospock.sample.db.AccountHelper;

import java.sql.SQLException;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    Dao<Account, Integer> accountDao;

    @InjectView(R.id.tv)
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        ButterKnife.inject(this);

        SampleApplication.getApp().getObjectGraph().inject(this);

        AccountHelper.createSampleAccounts(accountDao);

        try {
            Account account = accountDao.queryForAll().get(0);

            tv.setText(get());
        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
        }

    }

    public String get(){
        return "TEST";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
