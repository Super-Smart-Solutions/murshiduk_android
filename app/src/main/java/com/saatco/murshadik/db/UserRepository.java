package com.saatco.murshadik.db;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.dao.UserDao;
import com.saatco.murshadik.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final UserDao userDao;
    private LiveData<List<User>> mAllConsultant;
    private final LiveData<List<User>> mAllChatUser;

    public UserRepository(Application application) {
        AGDatabase db = AGDatabase.getDatabase(application);
        userDao = db.userDao();
        mAllConsultant = userDao.getConsultant();
        mAllChatUser = userDao.getChatUsers();
    }

    public void insertAllUsers(final List<User> users,boolean isChat) {

        if(isChat) {
            for (User user : users)
                user.setChatUser(true);
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {

            //Background work here
            if(isChat)
                userDao.deleteAllChatUsers();
            else
                userDao.deleteAll();
            userDao.insertUsers(users);


        });

//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                if(isChat)
//                   userDao.deleteAllChatUsers();
//                else
//                   userDao.deleteAll();
//                userDao.insertUsers(users);
//                return null;
//            }
//        }.execute();
    }

    public void deleteAllUsers() {

        ExecutorService executor = Executors.newSingleThreadExecutor();

        //Background work here
        executor.execute(userDao::deleteAll);

//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                userDao.deleteAll();
//                return null;
//            }
//        }.execute();
    }

    public void insertChatUser(User user,String lastMessage) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            //Background work here
            SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);

            user.setChatUser(true);
            user.setLastMessage(lastMessage);
            user.setDateTime(fromUser.format(new Date()));

            Log.v("BSMO","is exist" + isUserExist(user.getId()));

            if(!userDao.isUserExist(user.getId()))
                userDao.insert(user);
            else
                userDao.update(user);

            handler.post(() -> {
                //UI Thread work here
            });
        });

    }

    private boolean isUserExist (int id){

        LiveData<List<User>> users = userDao.getChatUsers();

        List<User> userList = new ArrayList<>();
        LiveData<List<User>> contactList = Transformations.map(users, it -> {
            userList.addAll(it);
            return userList;
        });

        for (User user : userList){
            if(user.getId() == id)
                return true;
        }

        return false;
    }

    public  LiveData<List<User>> getConsultantByCategories(String category){
        return userDao.getConsultantsByCategory(category);
    }


    public  LiveData<List<User>> getConsultantsByCategoryAndRegion(String category, String region){
        return userDao.getConsultantsByCategoryAndRegion(category, region);
    }


    public LiveData<List<User>> getAllUsers() {

        MutableLiveData<List<User>> users = new MutableLiveData<>();

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<User>> call =  apiInterface.getConsultants("Bearer "+ TokenHelper.getToken());
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {

                try{

                    if(response.body() != null){
                        insertAllUsers((List<User>) response.body(),false);
                        users.setValue(response.body());
                        mAllConsultant = users;
                    }

                }catch (Exception ignored){}
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                users.setValue(null);
            }
        });

        return mAllConsultant;

    }

    public LiveData<List<User>> getChatUsers() {

      /*  MutableLiveData<List<User>> users = new MutableLiveData<>();

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<User>> call =  apiInterface.getChatUsers("Bearer "+ TokenHelper.getToken());
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                try{

                    if(response.body() != null){
                        insertAllUsers((List<User>) response.body(),true);
                        users.setValue(response.body());
                        mAllChatUser = users;
                    }

                }catch (Exception ex){}
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                users.setValue(null);
            }
        });*/


        return mAllChatUser;

    }

}
