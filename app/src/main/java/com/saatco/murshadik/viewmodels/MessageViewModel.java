package com.saatco.murshadik.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.saatco.murshadik.db.MessageRepository;
import com.saatco.murshadik.model.Message;

import java.util.List;

public class MessageViewModel extends AndroidViewModel {

    private final MessageRepository mRepository;

    public MessageViewModel(@NonNull Application application) {
        super(application);

        mRepository = new MessageRepository(application);
    }
    public LiveData<List<Message>> getMessagesByUser(int id) { return mRepository.getMessages(id); }

}
