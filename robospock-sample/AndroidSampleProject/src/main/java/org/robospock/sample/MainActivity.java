package org.robospock.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.dao.Dao;

import org.robospock.sample.db.Account;

import java.io.IOException;
import java.sql.SQLException;

import javax.inject.Inject;

import butterknife.InjectView;

public class MainActivity extends Activity {

    @Inject
    Dao<Account, Integer> accountDao;

    @InjectView(R.id.tv)
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        SampleApplication.getApp().getObjectGraph().inject(this);

        try {
            accountDao.create(new Account("John", "pass"));
        } catch (SQLException e) {
            Log.e("TAG", e.getMessage());
        }


        try {
            tv.setText(accountDao.queryForAll().get(0).getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }


        ObjectMapper objectMapper = new ObjectMapper();
        JsonFactory jsonFactory = objectMapper.getFactory();

        try {
            JsonParser jp = jsonFactory.createParser(source);

            Account[] accounts = objectMapper.readValue(jp, Account[].class);
            tv.setText(accounts[1].getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

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


    String source = "[\n" +
            "{\n" +
            "\"name\":\"John\",\n" +
            "\"password\":\"8aecfd9b2fa26e83012fa298c2a50017\"\n" +
            "},\n" +
            "{\n" +
            "\"name\":\"Mark\",\n" +
            "\"password\":\"8aecfd9b2fa26e83012fa298c2a90018\"\n" +
            "},\n" +
            "{\n" +
            "\"name\": \"Jane\",\n" +
            "\"password\":\"8aecfd9b2fa26e83012fa298c2ae0019\"\n" +
            "}\n" +
            "]\n";
}
