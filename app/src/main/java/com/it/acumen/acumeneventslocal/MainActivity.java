package com.it.acumen.acumeneventslocal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, Game> listDataChild;
    DataBaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, Game>();

        db = new DataBaseHelper(getApplicationContext());
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

      //  listAdapter.notifyDataSetChanged();
        expListView.setAdapter(listAdapter);

        Button newGame = (Button) findViewById(R.id.add_game);

        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,QRCodeScanActivity.class);
                i.putExtra("requestCode",2);
                startActivityForResult(i,2);

            }
        });
        Toast.makeText(this,"View initialised",Toast.LENGTH_SHORT).show();
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {


//        // Adding child data
//        listDataHeader.add("737-101 \t Sravya");
//        listDataHeader.add("737-073 \t Krushi");
//        listDataHeader.add("737-314 \t Bhavani");


        db.insertGame("1", DateFormat.getDateTimeInstance().format(new Date()));
        db.insertGame("2",DateFormat.getDateTimeInstance().format(new Date()));
        db.insertGame("3",DateFormat.getDateTimeInstance().format(new Date()));

        db.insertHeadPlayer("1","001","Abhijith");
        db.insertHeadPlayer("2","002","Abishek");
        db.insertHeadPlayer("3","003","Aditya");


        List<Game> gamesList = db.getAllGames();
        for(int i=0;i<gamesList.size();i++){
            listDataHeader.add(gamesList.get(i).getGameId()+" "+gamesList.get(i).getPlayerList().get(0));
            listDataChild.put(gamesList.get(i).getGameId(),gamesList.get(i));
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1)
            listAdapter.onActivityResult(requestCode, resultCode, data);
        else if (requestCode == 2)
        {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                Toast.makeText(this,"Result :"+result,Toast.LENGTH_LONG).show();

                db.insertGame(result,DateFormat.getDateTimeInstance().toString());

             //   Game game = db.getAllGames().get();
                listDataHeader.add(result);
                db.insertHeadPlayer(result,"004","Akhil");
                List<PlayerDetails> playerDetails = new ArrayList<>();
                playerDetails.add(new PlayerDetails("004","Akhil"));
                listDataChild.put(result,new Game(result,playerDetails));
                listAdapter.notifyDataSetChanged();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do not Exit!");
        builder.setMessage("Are you sure you want to exit?\n     (You may lose data)");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // String name = _listDataHeader.get(gPosition);
                        //_listDataHeader.set(gPosition,"Submitted");
                        MainActivity.super.onBackPressed();
                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
       // super.onBackPressed();
    }
}