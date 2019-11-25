package com.cos.arch;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

// DB에 접근하기 위해 AndroidViewModel을 사용한다.
public class NoteViewModel extends AndroidViewModel {

    private NoteRepository repository;
    private LiveData<List<Note>> allNotes; //LiveData는 setValue를 하지 못한다.
    private MutableLiveData<Boolean> mIsUpdating = new MutableLiveData<>(); //setValue가 가능하다.


    public NoteViewModel(@NonNull Application application) {
        super(application);

        repository = new NoteRepository(application);
        allNotes = repository.getAllNotes();
    }

    public void insert(Note note){
        repository.insert(note);
        mIsUpdating.setValue(true);
    }

    public void delete(Note note){
        repository.delete(note);
    }

    public LiveData<List<Note>> getAllNotes(){
        return allNotes;
    }

    public MutableLiveData<Boolean> getIsUpdating(){
        return mIsUpdating;
    }
}
