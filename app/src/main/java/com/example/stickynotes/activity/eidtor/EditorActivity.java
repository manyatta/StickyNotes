package com.example.stickynotes.activity.eidtor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stickynotes.R;
import com.example.stickynotes.activity.main.MainActivity;
import com.example.stickynotes.api.ApiClient;
import com.example.stickynotes.api.ApiInterface;
import com.example.stickynotes.model.Note;
import com.thebluealliance.spectrum.SpectrumPalette;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditorActivity extends AppCompatActivity implements EditorView{

    EditText et_title, et_note;
    ProgressDialog progressDialog;
    SpectrumPalette palette;
    int color;
    int id;
    String title, note;
    ApiInterface apiInterface;

    Menu actionMenu;

    EditorPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        et_title = findViewById(R.id.title);
        et_note = findViewById(R.id.note);
        palette = findViewById(R.id.palette);

        palette.setOnColorSelectedListener(
                new SpectrumPalette.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int clr) {
                        color = clr;
                    }
                }
        );



        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..!");
        presenter = new EditorPresenter(this);

        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        title = intent.getStringExtra("title");
        note = intent.getStringExtra("notes");
        color = intent.getIntExtra("color", 0);

        setDataFromIntentExtra();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_editor, menu);
        actionMenu = menu;

        if (id != 0){
            actionMenu.findItem(R.id.edit).setVisible(true);
            actionMenu.findItem(R.id.delete).setVisible(true);
            actionMenu.findItem(R.id.save).setVisible(false);
            actionMenu.findItem(R.id.update).setVisible(false);
        }else{
            actionMenu.findItem(R.id.edit).setVisible(false);
            actionMenu.findItem(R.id.delete).setVisible(false);
            actionMenu.findItem(R.id.save).setVisible(true);
            actionMenu.findItem(R.id.update).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String title = et_title.getText().toString().trim();
        String note = et_note.getText().toString().trim();
        int color = this.color;

        switch (item.getItemId()){
            case R.id.save:
                    //save

                if(title.equals("")){
                    et_title.setError("Enter title");
                }else if(note.equals("")){
                    et_note.setError("Enter note");
                } else{
                    presenter.saveNote(title,note,color);
                }
                return  true;

            case R.id.edit:
                editMode();
                actionMenu.findItem(R.id.edit).setVisible(false);
                actionMenu.findItem(R.id.delete).setVisible(false);
                actionMenu.findItem(R.id.save).setVisible(false);
                actionMenu.findItem(R.id.update).setVisible(true);
                return true;
            case R.id.update:
                if(title.equals("")){
                    et_title.setError("Enter title");
                }else if(note.equals("")){
                    et_note.setError("Enter note");
                } else{
                    presenter.updateNote(id, title, note, color);
                }
            case R.id.delete:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Confirm");
                alertDialog.setMessage("Confirm you want to delete!");
                alertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        presenter.deleteNote(id);
                        Intent intent = new Intent(EditorActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                alertDialog.setPositiveButton("Cancel", (new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }));
                alertDialog.show();
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void showProgress() {
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        progressDialog.hide();
    }

    @Override
    public void onRequestSuccess(String message) {
        Toast.makeText(this , message, Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onRequestError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    private void setDataFromIntentExtra() {
        if(id != 0){
            et_title.setText(title);
            et_note.setText(note);
            palette.setSelectedColor(color);
            getSupportActionBar().setTitle("Update Note");
            
            readMode();
        }else{
            //default color
            palette.setSelectedColor(getResources().getColor(R.color.white));
            color = getResources().getColor(R.color.white);
            editMode();
        }
    }

    private void editMode() {
        et_title.setFocusableInTouchMode(true);
        et_note.setFocusableInTouchMode(true);
        palette.setEnabled(true);
    }

    private void readMode() {
        et_title.setFocusableInTouchMode(false);
        et_note.setFocusableInTouchMode(false);
        et_title.setFocusable(false);
        et_note.setFocusable(false);
        palette.setEnabled(false);
    }
}
