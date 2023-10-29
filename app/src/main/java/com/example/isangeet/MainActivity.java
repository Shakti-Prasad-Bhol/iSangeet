package com.example.isangeet;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<String> songList = new ArrayList<>();
    ArrayList<String> songPaths = new ArrayList<>(); // Added to store file paths

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);

        // Request external storage permission
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        // Permission granted, fetch and display songs
                        fetchSongs();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void fetchSongs() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                songList.add(songName);

                // Get the file path and store it
                @SuppressLint("Range") String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                songPaths.add(filePath);
            } while (cursor.moveToNext());

            cursor.close();

            // Display the list of songs in the ListView
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songList);
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent = new Intent(MainActivity.this, PlaySong.class);
                String currentSong = listView.getItemAtPosition(position).toString();
                intent.putStringArrayListExtra("songList", songPaths);
                intent.putExtra("currentSong", currentSong);
                intent.putExtra("position", position);
                startActivity(intent);
            });

        }
    }
}
