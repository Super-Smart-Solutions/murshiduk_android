package com.saatco.murshadik.db;

import android.app.Application;
import android.os.AsyncTask;

import com.saatco.murshadik.dao.MessageDao;
import com.saatco.murshadik.model.Message;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;

public class MessageRepository {


    private final MessageDao mMessageDao;
    private final LiveData<List<Message>> mAllMessages;

    public MessageRepository(Application application) {
        AGDatabase db = AGDatabase.getDatabase(application);
        mMessageDao = db.messageDao();
        mAllMessages = mMessageDao.fetchAllMessages();
    }

    public void insertMessage(String title) {

        Message note = new Message();
        note.setMessage(title);
        insertTask(note);
    }

    public void insertTask(final Message message) {

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            //Background work here
            mMessageDao.insertMessage(message);
        });
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                mMessageDao.insertMessage(message);
//                return null;
//            }
//        }.execute();
    }

    public void insertAllMessages(final List<Message> messages) {

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            //Background work here
            mMessageDao.insertMessages(messages);
        });
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                mMessageDao.insertMessages(messages);
//                return null;
//            }
//        }.execute();
    }

    public void deleteMessage(final int id) {

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            //Background work here
            mMessageDao.delete(id);
        });
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                mMessageDao.delete(id);
//                return null;
//            }
//        }.execute();
    }


    public LiveData<List<Message>> getMessages(int id) {

       return mMessageDao.getMessagesByMessage(id);

    }

    public LiveData<List<Message>> getAllMessages() {

      return mMessageDao.fetchAllMessages();

    }

}
