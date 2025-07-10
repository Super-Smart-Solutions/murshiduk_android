package com.saatco.murshadik.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.saatco.murshadik.model.Message;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert
    void insertMessage(Message message);

    @Insert
    void insertMessages(List<Message> messages);

    @Query("SELECT * FROM message")
    LiveData<List<Message>> fetchAllMessages();


    @Query("SELECT * FROM message WHERE message.user_id =:userId")
    LiveData<List<Message>> getMessagesByMessage(int userId);

    @Update
    void updateTask(Message note);

    @Query("DELETE FROM message WHERE message.user_id =:userId")
    void delete(int userId);
}
