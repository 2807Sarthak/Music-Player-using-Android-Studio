package com.example.itunes;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Your Songs");

        listView = findViewById(R.id.listView);

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                        Toast.makeText(MainActivity.this, "Permission Allowed !!", Toast.LENGTH_SHORT).show();
                        ArrayList<File> mySongs = fetchsongs(Environment.getExternalStorageDirectory());
                        String[] items = new String[mySongs.size()];

                        for (int i =0; i < mySongs.size(); i++)
                        {
                            items[i] = mySongs.get(i).getName().toString().replace(".mp3","");
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, items);
                        listView.setAdapter(adapter);

                     listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                         @Override
                         public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                             Intent intent = new Intent(MainActivity.this,Music_screen.class);
                             String currentSong = listView.getItemAtPosition(position).toString();

                             intent.putExtra("songList",mySongs);
                             intent.putExtra("currentSong",currentSong);
                             intent.putExtra("position",position);

                             startActivity(intent);
                         }
                     });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }

    public ArrayList<File> fetchsongs(File file)
    {
        ArrayList arrayList = new ArrayList();

        File[] songs = file.listFiles();

        if(songs != null)
        {
            for (File myFiles : songs)
            {
                if(!myFiles.isHidden() && myFiles.isDirectory()){
                    arrayList.addAll(fetchsongs(myFiles));
                }
                else
                {
                    if(myFiles.getName().endsWith(".mp3") && !myFiles.getName().startsWith("."))
                    {
                        arrayList.add(myFiles);
                    }
                }
            }
        }
       return arrayList;
    }
}