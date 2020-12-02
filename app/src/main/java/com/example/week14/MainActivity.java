package com.example.week14;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.net.IDN;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listViewMP3;
    private ArrayAdapter<String> arrayAdapter;
    private Button btnPlay, btnInc, btnDec;
    private TextView tvMP3, tvNowState;
    private ArrayList<String> mp3List;
    private String selectedMP3 = null;
    private String mp3Path = Environment.getExternalStorageDirectory().getPath() + "/Music";
    private MediaPlayer mediaPlayer;
    private boolean isFirst = true;
    private SimpleDateFormat simpleDateFormat;
    private SeekBar seekBar1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
    }

    class MyThread extends Thread {
        @Override
        public void run() {
            if(mediaPlayer == null) return;

            while(mediaPlayer.isPlaying()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        seekBar1.setMax(mediaPlayer.getDuration());
                        seekBar1.setProgress(mediaPlayer.getCurrentPosition());
                        tvNowState.setText(simpleDateFormat.format(mediaPlayer.getCurrentPosition()));
                    }
                });

            }
        }
    }

    public void startMediaPlayer() {
        mp3List = new ArrayList<String>();
        File[] listFiles = new File(mp3Path).listFiles();

        for(File file: listFiles) {
            mp3List.add(file.getName());
        }

        listViewMP3 = (ListView) findViewById(R.id.listViewMP3);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, mp3List);
        listViewMP3.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewMP3.setAdapter(arrayAdapter);
        listViewMP3.setItemChecked(0, true); // default
        selectedMP3 = mp3List.get(0);
        mediaPlayer = new MediaPlayer();

        tvMP3 = (TextView) findViewById(R.id.tvMP3);;
        tvNowState = (TextView) findViewById(R.id.tvNowState);

        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnInc = (Button) findViewById(R.id.btnInc);
        btnDec = (Button) findViewById(R.id.btnDec);

        simpleDateFormat = new SimpleDateFormat("mm:ss");
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);

        btnPlay.setText("▷");
        tvMP3.setText("재생할 노래를 선택하세요.");
        tvNowState.setText("mm:ss");


        listViewMP3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMP3 = mp3List.get(position);
                isFirst = false;
                try{
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(mp3Path + "/" + selectedMP3 );
                    //seekBar1.setMax(mediaPlayer.getDuration());
                    btnPlay.setText("❚❚");
                    tvMP3.setText("재생중인 음악: " + selectedMP3);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    new MyThread().start();
                } catch (IOException ex) {

                }

            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((btnPlay.getText().toString()).equals("❚❚")) { // stop 버튼을 누른 경우
                    mediaPlayer.pause();
                    btnPlay.setText("▷");
                } else { // Start button을 누른 경우
                    if (isFirst) { // 리스트뷰 아이템 선택 없이 그냥 재생 버튼만 누른 경우
                        selectedMP3 = mp3List.get(0);
                        isFirst = false;
                        try {
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(mp3Path + "/" + selectedMP3 );

                            btnPlay.setText("❚❚");
                            tvMP3.setText("재생중인 음악: " + selectedMP3);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            new MyThread().start();
                        } catch (IOException ex) {

                        }
                    } else { // isFirst = false;
                        mediaPlayer.start();
                        btnPlay.setText("❚❚");
                    }
                }
            }
        });

        btnInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer == null) return;
            }
        });

        btnDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer == null) return;

            }
        });

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1000:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {// 사용자가 요청된 권한 허용했을 때.
                    startMediaPlayer();
                }
                break; // request 1000 end

            default:
                break;
        }
    }
}