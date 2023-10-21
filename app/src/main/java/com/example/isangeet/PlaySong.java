package com.example.isangeet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    TextView textView;
    ImageView play, previous, next;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    int position;
    SeekBar seekBar;
    Thread updateSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        textView = findViewById(R.id.textView);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = new ArrayList<>();
        ArrayList<String> songPaths = intent.getStringArrayListExtra("songList");
        for (String path : songPaths) {
            songs.add(new File(path));
        }

        textContent = intent.getStringExtra("currentSong");
        textView.setText(textContent);
        textView.setSelected(true);
        position = intent.getIntExtra("position", 0);
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        Handler handler = new Handler();

        Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    handler.postDelayed(this, 800);
                }
            }
        };

// Start the updateRunnable when needed
        handler.postDelayed(updateRunnable, 0);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else{
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != 0) {
                    position = position - 1;
                } else {
                    position = songs.size() - 1;
                }

                // Reset the MediaPlayer object
                mediaPlayer.reset();

                // Set the new data source
                Uri uri = Uri.parse(songs.get(position).toString());
                try {
                    mediaPlayer.setDataSource(getApplicationContext(), uri);
                } catch (IOException e) {
                    e.printStackTrace(); // Print the stack trace to logcat
                    throw new RuntimeException(e);
                }

                // Prepare the MediaPlayer object
                try {
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // Start playing the song
                mediaPlayer.start();

                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName();
                textView.setText(textContent);
            }
        });

// Next button click listener
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != songs.size() - 1) {
                    position = position + 1;
                } else {
                    position = 0;
                }

                // Reset the MediaPlayer object
                mediaPlayer.reset();

                // Set the new data source
                Uri uri = Uri.parse(songs.get(position).toString());
                try {
                    mediaPlayer.setDataSource(getApplicationContext(), uri);
                } catch (IOException e) {
                    e.printStackTrace(); // Print the stack trace to logcat
                    throw new RuntimeException(e);
                }

                // Prepare the MediaPlayer object
                try {
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // Start playing the song
                mediaPlayer.start();

                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName();
                textView.setText(textContent);
            }
        });

    }
}