package com.example.isangeet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {

    private TextView textView;
    private ImageView play, previous, next;
    private ArrayList<File> songs;
    private MediaPlayer mediaPlayer;
    private String textContent;
    private int position;
    private SeekBar seekBar;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        initializeViews();
        initializeMediaPlayer();

        handler.postDelayed(updateSeekBarRunnable, 0);

        setClickListeners();
    }

    private void initializeViews() {
        textView = findViewById(R.id.textView);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
    }

    private void initializeMediaPlayer() {
        Intent intent = getIntent();
        songs = new ArrayList<>();
        ArrayList<String> songPaths = intent.getStringArrayListExtra("songList");
        assert songPaths != null;
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
    }

    private void setClickListeners() {
        play.setOnClickListener(v -> togglePlayback());
        previous.setOnClickListener(v -> playPrevious());
        next.setOnClickListener(v -> playNext());

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
    }

    private void togglePlayback() {
        if (mediaPlayer.isPlaying()) {
            play.setImageResource(R.drawable.play);
            mediaPlayer.pause();
        } else {
            play.setImageResource(R.drawable.pause);
            mediaPlayer.start();
            handler.post(updateSeekBarRunnable);
        }
    }

    private void playPrevious() {
        position = (position != 0) ? position - 1 : songs.size() - 1;
        seekBar.setProgress(0);
        updateMediaPlayer();
    }

    private void playNext() {
        position = (position != songs.size() - 1) ? position + 1 : 0;
        seekBar.setProgress(0);
        updateMediaPlayer();
    }

    private void updateMediaPlayer() {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(songs.get(position).toString()));
            mediaPlayer.prepare();
            mediaPlayer.start();
            play.setImageResource(R.drawable.pause);
            seekBar.setMax(mediaPlayer.getDuration());
            textContent = songs.get(position).getName();
            textView.setText(textContent);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onBackPressed() {
        releaseMediaPlayer();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        releaseMediaPlayer();
        super.onDestroy();
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            handler.removeCallbacks(updateSeekBarRunnable);
        }
    }

    private final Runnable updateSeekBarRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(currentPosition);
            }
            handler.postDelayed(this, 800);
        }
    };

}
