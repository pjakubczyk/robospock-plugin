package org.robospock.sample.db;

import android.util.Log;

import com.j256.ormlite.dao.Dao;

import org.robospock.sample.json.Person;
import org.robospock.sample.json.PersonParser;

import java.sql.SQLException;

/**
 * Created by Przemek Jakubczyk on 1/23/14.
 */
public class AccountHelper {

    private static String TAG = AccountHelper.class.getSimpleName();

    public static void createAccounts(Dao<Account, Integer> dao, Person[] persons) {

        for (Person person : persons) {
            Account account = new Account(person.getFullName(), person.getPassword());
            try {
                dao.create(account);
            } catch (SQLException e) {
                Log.d(TAG, e.getMessage());
            }
        }
    }

    public static void createSampleAccounts(Dao<Account, Integer> dao){
        createAccounts(dao, PersonParser.parseSample());
    }
}
