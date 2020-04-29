package com.example.stickynotes.activity.main;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.stickynotes.R;
import com.example.stickynotes.activity.eidtor.EditorActivity;
import com.example.stickynotes.model.Note;

import java.util.List;

public class MainActivity extends AppCompatActivity implements  MainView {

    private static final int INTENT_EDIT = 200;
    private static final int INTENT_ADD = 100;


    FloatingActionButton fab;
    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;

    MainPresenter presenter;
    MainAdapter adapter;
    MainAdapter.ItemClickListener itemClickListener;
    List<Note> note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refreshLayout = findViewById(R.id.swipe_refresh);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fab = findViewById(R.id.add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivityForResult(new Intent(MainActivity.this, EditorActivity.class),INTENT_EDIT);
            }
        });


        presenter = new MainPresenter(this);
        presenter.getData();
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        presenter.getData();
                    }
                }
        );

        itemClickListener = new MainAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int id = note.get(position).getId();
                String title = note.get(position).getTitle();
                String notes = note.get(position).getNote();
                String color = note.get(position).getColor();

                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("title", title);
                intent.putExtra("notes",notes);
                intent.putExtra("color",color);

                startActivityForResult(intent, INTENT_EDIT);

                Toast.makeText(MainActivity.this, title, Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INTENT_ADD && resultCode == RESULT_OK){
            presenter.getData();//reload data
        }else if (requestCode == INTENT_EDIT && resultCode == RESULT_OK){
            presenter.getData();//reload data
        }
    }

    @Override
    public void showLoading() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onGetResult(List<Note> notes) {
        adapter = new MainAdapter(this, notes, itemClickListener);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        note = notes;
    }

    @Override
    public void onErrorLoading(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
