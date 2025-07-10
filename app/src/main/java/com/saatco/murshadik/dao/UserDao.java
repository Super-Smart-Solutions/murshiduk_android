package com.saatco.murshadik.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.saatco.murshadik.model.User;

import java.util.List;

@Dao
public interface UserDao {

   // @Insert(onConflict = OnConflictStrategy.REPLACE)
  //  void insert(User user);

    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Insert
    void insertUsers(List<User> users);

    @Query("DELETE FROM user_table")
    void deleteAll();

    @Query("DELETE FROM user_table WHERE user_table.is_chat_user = 1")
    void deleteAllChatUsers();

    // Query with parameter that returns a specific word or words.
    @Query("SELECT * FROM user_table")
    LiveData<List<User>> getConsultant();

    @Query("SELECT EXISTS(SELECT * FROM user_table WHERE user_table.id = :id & user_table.is_chat_user = 1)")
    boolean isUserExist(int id);

    @Query("SELECT * FROM user_table WHERE user_table.skills LIKE :skill")
    LiveData<List<User>> getConsultantsByCategory(String skill);

    @Query("SELECT * FROM user_table WHERE user_table.skills LIKE :skill AND user_table.location LIKE :region")
    LiveData<List<User>> getConsultantsByCategoryAndRegion(String skill, String region);

    @Query("SELECT * FROM user_table WHERE user_table.is_chat_user = 1")
    LiveData<List<User>> getChatUsers();

    @Query("SELECT COUNT(user_table.uid) FROM user_table")
    LiveData<Integer> getCount();

    @Query("SELECT COUNT(user_table.uid) FROM user_table WHERE user_table.is_chat_user = 1")
    LiveData<Integer> getChatUserCount();
}
