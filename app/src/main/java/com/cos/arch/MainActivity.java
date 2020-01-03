package com.cos.arch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainLog";

    private NoteViewModel noteViewModel;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        // 누군가가 List 데이터를 추가, 삭제, 변경하면 observe가 알아챈다.
        // 이를 통해 데이터 변경과 UI변경을 동기화 시킬 수 있다.
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                // update RecyclerView
                Log.d(TAG, "getAllNotes: 데이터 변경 됨");
                adapter.setNotes(notes);
                // 화면 위치 변경 해주자.
                if(noteViewModel.getAllNotes().getValue().size() !=0){
                    recyclerView.smoothScrollToPosition(noteViewModel.getAllNotes().getValue().size()-1);
                }
            }
        });

        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        // 리사이클러뷰의 각 ViewHolder에 리스너를 달 필요없다. 왜냐하면
        // 리사이클러뷰가 있는 엑티비티에서 이벤트를 콜백 받을 수 있기 때문이다.
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // insert 하는 순간 DB에서 삭제되고 getAllNote()를 지켜보는 옵저버가 실행된다.
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void saveNote(){
        Random r = new Random();
        int num = r.nextInt(10)+1;
        String title = "Title "+num;
        String description = "Description "+num;
        int priority = num;

        Note note = new Note(title, description, priority);
        noteViewModel.insert(note); // insert 하는 순간 DB에 저장되고 getAllNote()를 지켜보는 옵저버가 실행된다.
    }
}
