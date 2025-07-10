package com.saatco.murshadik.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.saatco.murshadik.dao.MessageDao;
import com.saatco.murshadik.dao.UserDao;
import com.saatco.murshadik.model.Message;
import com.saatco.murshadik.model.User;

@Database(entities = {User.class, Message.class}, version = 1, exportSchema = false)
public abstract class AGDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract MessageDao messageDao();

    private static AGDatabase INSTANCE;

    public static AGDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AGDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AGDatabase.class, "ag_database")
                            // Wipes and rebuilds instead of migrating
                            // if no Migration object.
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}