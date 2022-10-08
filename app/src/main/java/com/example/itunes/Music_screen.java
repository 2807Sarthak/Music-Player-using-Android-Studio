package com.example.itunes;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class Music_screen extends AppCompatActivity {
    TextView textView,currentTimer,totalTimer;
    ImageView pauply,next,prev,loop,imageView;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> songs;
    String textContent;
    int i = 0;
    Thread updateSeek;
    Random random;
    String endTime;

    int[] images = {R.drawable.music,R.drawable.image0,R.drawable.image1,R.drawable.image2,R.drawable.image3,R.drawable.image4,R.drawable.image5};

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.pause();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_screen);

        getSupportActionBar().hide();


        textView = findViewById(R.id.textView);
        totalTimer = findViewById(R.id.totalTimeTv);
        currentTimer = findViewById(R.id.currentTimeTv);
        seekBar = findViewById(R.id.seekBar);

        pauply = findViewById(R.id.pauply);
        next = findViewById(R.id.next);
        prev = findViewById(R.id.prev);
        imageView = findViewById(R.id.imageView);
        loop = findViewById(R.id.loop);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList)bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong");
        textView.setText(textContent);


        textView.setSelected(true);

        position = intent.getIntExtra("position",0);
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this,uri);
        mediaPlayer.start();


//      SeekBar:-

//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                seekBar.setProgress(mediaPlayer.getCurrentPosition());
//            }
//        },0,1000);

        updateSeek = new Thread()
        {
            @Override
            public void run() {
                int currentPosition = 0;
                while (currentPosition < mediaPlayer.getDuration()) {
                try {
                    sleep(600);
                    currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            }
        };

        seekBar.setMax(mediaPlayer.getDuration());
//        updateSeek.start();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean fromUser) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        updateSeek.start();

        endTime = createTime(mediaPlayer.getDuration());
        totalTimer.setText(endTime);

        final Handler handler = new Handler();
        final int delay = 800;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentime = createTime(mediaPlayer.getCurrentPosition());
                currentTimer.setText(currentime);
                handler.postDelayed(this,delay);
            }
        },delay);

        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            next.performClick();
        });

//        Buttons:-
        pauply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying())
                {
                    pauply.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else
                {
                    pauply.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });

        next.setOnClickListener(view -> {
         mediaPlayer.stop();
         mediaPlayer.release();

         if (position != (songs.size()-1)){
             position = position + 1;
         }
         else
         {
             Toast.makeText(Music_screen.this, "No more Songs in the Queue !!", Toast.LENGTH_SHORT).show();
         }
            Uri uri1 = Uri.parse(songs.get(position).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri1);
            mediaPlayer.start();
            pauply.setImageResource(R.drawable.pause);
            textContent = songs.get(position).getName().toString();
            textView.setText(textContent);

            endTime = createTime(mediaPlayer.getDuration());
            totalTimer.setText(endTime);

            random = new Random();
            imageView.setImageResource(images[random.nextInt(images.length)]);
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position != 0) {
                    position = position - 1;
                } else {
                    Toast.makeText(Music_screen.this, "No more Songs in the Queue !!", Toast.LENGTH_SHORT).show();
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                pauply.setImageResource(R.drawable.pause);
                textContent = songs.get(position).getName();
                textView.setText(textContent);

                endTime = createTime(mediaPlayer.getDuration());
                totalTimer.setText(endTime);

                random = new Random();
                imageView.setImageResource(images[random.nextInt(images.length)]);
            }
        });

        loop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(i == 0){
                    Toast.makeText(Music_screen.this, "Loop Song is ON", Toast.LENGTH_SHORT).show();
                    loop.setImageResource(R.drawable.ic_baseline_loop_24);
                    mediaPlayer.setLooping(true);
                    i = 1;
                }
                else {
                    Toast.makeText(Music_screen.this, "Loop Song is OFF", Toast.LENGTH_SHORT).show();
                    loop.setImageResource(R.drawable.loop);
                    if (mediaPlayer.isLooping()) {
                        mediaPlayer.setLooping(false);
                    }
                    i = 0;
                }
            }
        });
    }

    public String createTime(int duration){
        String time = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        time += min + ":";

        if(sec < 10) time += "0";
        time += sec;
        return time;
    }
}
