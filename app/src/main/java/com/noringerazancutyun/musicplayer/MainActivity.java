package com.noringerazancutyun.musicplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.noringerazancutyun.musicplayer.adapter.SongAdapter;
import com.noringerazancutyun.musicplayer.service.MusicService;
import com.noringerazancutyun.musicplayer.util.MusicController;
import com.noringerazancutyun.musicplayer.util.Song;
import com.noringerazancutyun.musicplayer.util.SongsManager;

import android.net.Uri;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer = new MediaPlayer();

    SongsManager songsManager = new SongsManager();

    boolean check = false;
    Button play;
    public static final int MY_PERMISSION_REQUEST = 1;

    ArrayList<String> arrayList;

    ListView listView;
    ArrayAdapter<String> adapter;
    Uri songUri;


//    private ArrayList<Song> songList;
//    private ListView songView;
//
//    //service
//    private MusicService musicSrv;
//    private Intent playIntent;
//    //binding
//    private boolean musicBound=false;
//
//    //controller
//    private MusicController controller;
//
//    //activity and playback pause flags
//    private boolean paused=false, playbackPaused=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        play = (Button) findViewById(R.id.play);

        permission();

        playPause();
    }

    public void permission(){
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        }else {
            doStuff();
        }
    }

    public void getMusic(){
        ContentResolver contentResolver = getContentResolver();
        songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if(songCursor != null && songCursor.moveToFirst()){
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songAlbum = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);

            do{
                String curentTitle = songCursor.getString(songTitle);
                String curentArtist = songCursor.getString(songArtist);
                String curentAlbum = songCursor.getString(songAlbum);
                arrayList.add(curentTitle + "\n"
                        + curentArtist + "\n"
                        + curentAlbum);

            } while (songCursor.moveToNext());

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case MY_PERMISSION_REQUEST:{
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                        doStuff();
                    }
                } else {
                    Toast.makeText(this, "Permission not Granted", Toast.LENGTH_SHORT).show();

                    finish();
                }
                return;
            }
        }

    }

    public void doStuff(){
        listView = (ListView) findViewById(R.id.song_list);
        arrayList = new ArrayList<>();
        getMusic();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    public void playPause(){
        play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


                Toast.makeText(MainActivity.this, songUri.toString(), Toast.LENGTH_SHORT).show();
                if   (check == true) {
                    play.setBackground(getDrawable(R.drawable.pause));
                }else {
                    play.setBackground(getDrawable(R.drawable.play));
                }
                check = !check;
            }
        });
    }








//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        //retrieve list view
//        songView = (ListView)findViewById(R.id.song_list);
//        //instantiate list
//        songList = new ArrayList<Song>();
//        //get songs from device
//        getSongList();
//        //sort alphabetically by title
//        Collections.sort(songList, new Comparator<Song>(){
//            public int compare(Song a, Song b){
//                return a.getTitle().compareTo(b.getTitle());
//            }
//        });
//        //create and set adapter
//        SongAdapter songAdt = new SongAdapter(this, songList);
//        songView.setAdapter(songAdt);
//
//        //setup controller
//        setController();
//    }
//
//    //connect to the service
//    private ServiceConnection musicConnection = new ServiceConnection(){
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
//            //get service
//            musicSrv = binder.getService();
//            //pass list
//            musicSrv.setList(songList);
//            musicBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            musicBound = false;
//        }
//    };
//
//    //start and bind the service when the activity starts
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if(playIntent==null){
//            playIntent = new Intent(this, MusicService.class);
//            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
//            startService(playIntent);
//        }
//    }
//
//    //user song select
//    public void songPicked(View view){
//        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
//        musicSrv.playSong();
//        if(playbackPaused){
//            setController();
//            playbackPaused=false;
//        }
//        controller.show(0);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        //menu item selected
//        switch (item.getItemId()) {
//            case R.id.action_shuffle:
//                musicSrv.setShuffle();
//                break;
//            case R.id.action_end:
//                stopService(playIntent);
//                musicSrv=null;
//                System.exit(0);
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    //method to retrieve song info from device
//    public void getSongList(){
//        //query external audio
//        ContentResolver musicResolver = getContentResolver();
//        Uri musicUri =  Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/gato.mp3");
//        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
//        //iterate over results if valid
//        if(musicCursor!=null && musicCursor.moveToFirst()){
//            //get columns
//            int titleColumn = musicCursor.getColumnIndex
//                    (android.provider.MediaStore.Audio.Media.TITLE);
//            int idColumn = musicCursor.getColumnIndex
//                    (android.provider.MediaStore.Audio.Media._ID);
//            int artistColumn = musicCursor.getColumnIndex
//                    (android.provider.MediaStore.Audio.Media.ARTIST);
//            //add songs to list
//            do {
//                long thisId = musicCursor.getLong(idColumn);
//                String thisTitle = musicCursor.getString(titleColumn);
//                String thisArtist = musicCursor.getString(artistColumn);
//                songList.add(new Song(thisId, thisTitle, thisArtist));
//            }
//            while (musicCursor.moveToNext());
//        }
//    }
//
//
//    public boolean canPause() {
//        return true;
//    }
//
//    public boolean canSeekBackward() {
//        return true;
//    }
//
//    public boolean canSeekForward() {
//        return true;
//    }
//
//    public int getAudioSessionId() {
//        return 0;
//    }
//
//    public int getBufferPercentage() {
//        return 0;
//    }
//
//    public int getCurrentPosition() {
//        if(musicSrv!=null && musicBound && musicSrv.isPng())
//            return musicSrv.getPosn();
//        else return 0;
//    }
//
//
//    public int getDuration() {
//        if(musicSrv!=null && musicBound && musicSrv.isPng())
//            return musicSrv.getDur();
//        else return 0;
//    }
//
//
//    public boolean isPlaying() {
//        if(musicSrv!=null && musicBound)
//            return musicSrv.isPng();
//        return false;
//    }
//
//
//    public void pause() {
//        playbackPaused=true;
//        musicSrv.pausePlayer();
//    }
//
//
//    public void seekTo(int pos) {
//        musicSrv.seek(pos);
//    }
//
//
//    public void start() {
//        musicSrv.go();
//    }
//
//    //set the controller up
//    private void setController(){
//        controller = new MusicController(this);
//        //set previous and next button listeners
//        controller.setPrevNextListeners(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                playNext();
//            }
//        }, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                playPrev();
//            }
//        });
//        //set and show
//        controller.setMediaPlayer(MediaController.class);
//        controller.setAnchorView(findViewById(R.id.song_list));
////        controller.setEnabled(true);
//    }
//
//    private void playNext(){
//        musicSrv.playNext();
//        if(playbackPaused){
//            setController();
//            playbackPaused=false;
//        }
//        controller.show(0);
//    }
//
//    private void playPrev(){
//        musicSrv.playPrev();
//        if(playbackPaused){
//            setController();
//            playbackPaused=false;
//        }
//        controller.show(0);
//    }
//
//    @Override
//    protected void onPause(){
//        super.onPause();
//        paused=true;
//    }
//
//    @Override
//    protected void onResume(){
//        super.onResume();
//        if(paused){
//            setController();
//            paused=false;
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        controller.hide();
//        super.onStop();
//    }
//
//    @Override
//    protected void onDestroy() {
//        stopService(playIntent);
//        musicSrv=null;
//        super.onDestroy();
//    }

}
