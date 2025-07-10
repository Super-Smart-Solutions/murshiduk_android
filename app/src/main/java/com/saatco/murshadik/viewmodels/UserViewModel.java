package com.saatco.murshadik.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.saatco.murshadik.db.UserRepository;
import com.saatco.murshadik.model.User;

import java.util.List;

public class UserViewModel extends AndroidViewModel {

    private final UserRepository mRepository;

    private LiveData<List<User>> mAllUser;

    public UserViewModel(@NonNull Application application) {
        super(application);

        mRepository = new UserRepository(application);
        mAllUser = mRepository.getAllUsers();
    }

    public LiveData<List<User>> getAllUser() { return mAllUser; }
    public void updateAllUsers() {
        mAllUser = mRepository.getAllUsers();
    }

    public LiveData<List<User>> getConsultantsByCategory(String category) { return mRepository.getConsultantByCategories(category); }
    public LiveData<List<User>> getConsultantsByCategoryAndRegion(String category, String region) { return mRepository.getConsultantsByCategoryAndRegion(category, region); }

   // public void insertUsers(List<User> users) { mRepository.insertAllUsers(users); }

    public LiveData<List<User>> getChatUsers() { return mRepository.getChatUsers(); }

    public void insertChatUser(User user,String lastMessage){
        mRepository.insertChatUser(user,lastMessage);
    }
}
