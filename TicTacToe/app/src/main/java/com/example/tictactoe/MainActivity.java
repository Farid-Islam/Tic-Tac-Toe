package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button a1,a2,a3,b1,b2,b3,c1,c2,c3,joinTable;
    private EditText tableCode;
    private Button[] bArray;
    private boolean turn = true;
    private int turn_count = 0;
    private TextView turnShow;
    private Button resetButton;
    private TextView o_wins,x_wins,draw;
    private int zeroWinsCount = 0,xWinsCount = 0,drawCount = 0;
    private MediaPlayer sound,gameOverSound;

    private Chronometer chronometer_0,chronometer_x;
    private long chrono_x_count,chrono_0_count;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //int playerId = 1;
    private boolean lastUser=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findId();
        bArray = new Button[]{a1,a2,a3,b1,b2,b3,c1,c2,c3};
        for(Button b : bArray){
            b.setOnClickListener(this);
        }

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chronometer_0.stop();
                chronometer_x.stop();
                chronometer_0.setBase(SystemClock.elapsedRealtime());
                chronometer_x.setBase(SystemClock.elapsedRealtime());
                chrono_0_count = 0;
                chrono_x_count = 0;

                sound.start();
                turn = true;
                turn_count = 0;
                turnShow.setText("Turn: 0-Player");
                enableIsAllButton(true);
                bArray[0].setText(null);
                bArray[1].setText(null);
                bArray[2].setText(null);
                bArray[3].setText(null);
                bArray[4].setText(null);
                bArray[5].setText(null);
                bArray[6].setText(null);
                bArray[7].setText(null);
                bArray[8].setText(null);
                bArray[0].setBackgroundColor(Color.parseColor("#64aea8a8"));
                bArray[1].setBackgroundColor(Color.parseColor("#64aea8a8"));
                bArray[2].setBackgroundColor(Color.parseColor("#64aea8a8"));
                bArray[3].setBackgroundColor(Color.parseColor("#64aea8a8"));
                bArray[4].setBackgroundColor(Color.parseColor("#64aea8a8"));
                bArray[5].setBackgroundColor(Color.parseColor("#64aea8a8"));
                bArray[6].setBackgroundColor(Color.parseColor("#64aea8a8"));
                bArray[7].setBackgroundColor(Color.parseColor("#64aea8a8"));
                bArray[8].setBackgroundColor(Color.parseColor("#64aea8a8"));

            }
        });

        sound = MediaPlayer.create(this,R.raw.multimedia_button_click_004);
        gameOverSound = MediaPlayer.create(this,R.raw.game_over_sound);

        chronometer_0.setFormat("0-Time: %s");
        chronometer_x.setFormat("X-Time: %s");
        chronometer_0.setBase(SystemClock.elapsedRealtime());
        chronometer_x.setBase(SystemClock.elapsedRealtime());

    }

    private void findId() {

        a1 = findViewById(R.id.id_1);
        a2 = findViewById(R.id.id_2);
        a3 = findViewById(R.id.id_3);
        b1 = findViewById(R.id.id_4);
        b2 = findViewById(R.id.id_5);
        b3 = findViewById(R.id.id_6);
        c1 = findViewById(R.id.id_7);
        c2 = findViewById(R.id.id_8);
        c3 = findViewById(R.id.id_9);
        joinTable = findViewById(R.id.joinTable);
        tableCode = findViewById(R.id.table_id);

        tableCode.setText(new Random().nextInt(9999)+"");

        o_wins = findViewById(R.id.o_wins_id);
        x_wins = findViewById(R.id.x_wins_id);
        draw = findViewById(R.id.draw_id);

        turnShow = findViewById(R.id.turnShow_id);
        resetButton = findViewById(R.id.resetButton_id);
        joinTable = findViewById(R.id.joinTable);

        chronometer_0 = findViewById(R.id.o_chronometer_id);
        chronometer_x = findViewById(R.id.x_chronometer_id);

        joinTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinOrCreateTable();
            }
        });



    }

    private void joinOrCreateTable() {
        System.out.println("CLICK");
        CollectionReference ref=db.collection(tableCode.getText().toString());
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().isEmpty()){
                    CollectionReference ref=db.collection(tableCode.getText().toString());
                    Map<String,Object> map=new HashMap<>();
                    map.put("userId",true);
                    map.put("buttonId",-1);
                    ref.add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){
                                turn=false;
                                lastUser=true;
                                Toast.makeText(MainActivity.this, "You joining as X-Player..", Toast.LENGTH_SHORT).show();
                                joinTable();
                            }
                        }
                    });

                }else{


                    lastUser=false;
                    turn=true;
                    Toast.makeText(MainActivity.this, "You joining as 0-Player..", Toast.LENGTH_SHORT).show();
                    turnShow.setText("Turn: 0-Player");
                    joinTable();

                }



            }
        });
    }


    public void joinTable(){
        CollectionReference ref=db.collection(tableCode.getText().toString());
        ref.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                List<DocumentChange> documents=queryDocumentSnapshots.getDocumentChanges();
                for(DocumentChange documentChange:documents){

                    QueryDocumentSnapshot document=documentChange.getDocument();
                    int value=document.getLong("buttonId").intValue();
                    lastUser = document.getBoolean("userId");
                    System.out.println(value+"  "+lastUser);
                    updateValue(value);

                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        sound.start();
        Button b = (Button) v;
        buttonClicked(b);

    }

    private void buttonClicked(Button b) {

        if(lastUser==turn)
            return;
        CollectionReference ref=db.collection(tableCode.getText().toString());
        Map<String,Object> map=new HashMap<>();
        map.put("userId",turn);
        map.put("buttonId",b.getId());
        ref.add(map).addOnCompleteListener(this, new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    System.out.println("Success");
                }
            }
        });

    }

    public void updateValue(int id){
        if(id==-1){
            return;
        }
        Button b= findViewById(id);
        if(lastUser){
            chronometer_x.setBase(SystemClock.elapsedRealtime() - chrono_x_count);
            chronometer_x.start();

            if(turn_count != 0){
                chronometer_0.stop();
                chrono_0_count = SystemClock.elapsedRealtime() - chronometer_0.getBase();
            }

            b.setText("0");
        }else{
            chronometer_0.setBase(SystemClock.elapsedRealtime() - chrono_0_count);
            chronometer_0.start();

            chronometer_x.stop();
            chrono_x_count = SystemClock.elapsedRealtime() - chronometer_x.getBase();
            b.setText("X");
        }
        turn_count++;
        //b.setBackgroundColor(Color.GREEN);
        b.setClickable(false);
        // change the turn value
        //turn = ! turn;

        if(lastUser){
            turnShow.setText("Turn: 0-Player");
        }else{
            turnShow.setText("Turn: X-Player");
        }
        checkForWinner();
    }



    private void checkForWinner() {
        boolean there_is_a_winner = false;

        if(a1.getText() == a2.getText() && a2.getText() == a3.getText() && !a1.isClickable()){
            a1.setBackgroundColor(Color.parseColor("#6404f714"));
            a2.setBackgroundColor(Color.parseColor("#6404f714"));
            a3.setBackgroundColor(Color.parseColor("#6404f714"));
            there_is_a_winner = true;
        }
        else if(b1.getText() == b2.getText() && b2.getText() == b3.getText() && !b1.isClickable()){
            b1.setBackgroundColor(Color.parseColor("#6404f714"));
            b2.setBackgroundColor(Color.parseColor("#6404f714"));
            b3.setBackgroundColor(Color.parseColor("#6404f714"));
            there_is_a_winner = true;
        }
        else if(c1.getText() == c2.getText() && c2.getText() == c3.getText() && !c1.isClickable()){
            c1.setBackgroundColor(Color.parseColor("#6404f714"));
            c2.setBackgroundColor(Color.parseColor("#6404f714"));
            c3.setBackgroundColor(Color.parseColor("#6404f714"));
            there_is_a_winner = true;
        }

        else if(a1.getText() == b1.getText() && b1.getText() == c1.getText() && !a1.isClickable()){
            a1.setBackgroundColor(Color.parseColor("#6404f714"));
            b1.setBackgroundColor(Color.parseColor("#6404f714"));
            c1.setBackgroundColor(Color.parseColor("#6404f714"));
            there_is_a_winner = true;
        }
        else if(a2.getText() == b2.getText() && b2.getText() == c2.getText() && !a2.isClickable()){
            a2.setBackgroundColor(Color.parseColor("#6404f714"));
            b2.setBackgroundColor(Color.parseColor("#6404f714"));
            c2.setBackgroundColor(Color.parseColor("#6404f714"));
            there_is_a_winner = true;
        }
        else if(a3.getText() == b3.getText() && b3.getText() == c3.getText() && !a3.isClickable()){
            a3.setBackgroundColor(Color.parseColor("#6404f714"));
            b3.setBackgroundColor(Color.parseColor("#6404f714"));
            c3.setBackgroundColor(Color.parseColor("#6404f714"));
            there_is_a_winner = true;
        }

        else if(a1.getText() == b2.getText() && b2.getText() == c3.getText() && !a1.isClickable()){
            a1.setBackgroundColor(Color.parseColor("#6404f714"));
            b2.setBackgroundColor(Color.parseColor("#6404f714"));
            c3.setBackgroundColor(Color.parseColor("#6404f714"));
            there_is_a_winner = true;
        }
        else if(a3.getText() == b2.getText() && b2.getText() == c1.getText() && !a3.isClickable()){
            a3.setBackgroundColor(Color.parseColor("#6404f714"));
            b2.setBackgroundColor(Color.parseColor("#6404f714"));
            c1.setBackgroundColor(Color.parseColor("#6404f714"));
            there_is_a_winner = true;
        }

        if(there_is_a_winner){

            chronometer_0.stop();
            chronometer_x.stop();

            gameOverSound.start();
            if(!turn){
                zeroWinsCount++;
                o_wins.setText(""+zeroWinsCount);
                turnShow.setText("0-Win");
                toast("0 win");
            }else {
                xWinsCount++;
                x_wins.setText(""+xWinsCount);
                turnShow.setText("X-Win");
                toast("X wins");
            }
            enableIsAllButton(false);

        }else if(turn_count == 9){

            chronometer_0.stop();
            chronometer_x.stop();

            gameOverSound.start();
            drawCount++;
            draw.setText(""+drawCount);
            turnShow.setText("Draw");
            toast("Draw");
        }


    }

    private void enableIsAllButton(boolean enable) {
        for(Button b : bArray){
            b.setClickable(enable);
        }
    }

    private void toast(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Quit the game?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }


}
