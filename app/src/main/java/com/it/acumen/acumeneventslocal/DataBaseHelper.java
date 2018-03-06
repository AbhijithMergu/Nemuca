package com.it.acumen.acumeneventslocal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pavan on 3/3/2018.
 */

public class DataBaseHelper extends SQLiteOpenHelper{
    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "gameManager";

    private static DataBaseHelper sInstance;
    //Table Names
    private static final String TABLE_GAME = "games";
    private static final String TABLE_PLAYERS = "players";

    //Games Table - Column names
    private static final String KEY_GAME_ID = "gameId";
    private static final String KEY_ROUND1_SCORE = "round1";
    private static final String KEY_ROUND2_SCORE = "round2";
    private static final String KEY_ROUND3_SCORE = "round3";
    private static final String KEY_STATUS = "status";
    private static final String KEY_TIMESTAMP = "time";
    //Players Table - Column names

    private static final String KEY_PLAYER_ID = "playerId";
    private static final String KEY_PLAYER_NAME = "playerName";
    private static final String KEY_PLAYER_TYPE = "playerType";

    //Table Creation Statements
    //Game Table
    private static final String CREATE_TABLE_GAME = "CREATE TABLE "
            + TABLE_GAME + " ( " + KEY_GAME_ID + " TEXT PRIMARY KEY, " + KEY_ROUND1_SCORE
            + " INTEGER," + KEY_ROUND2_SCORE + " INTEGER,"  + KEY_ROUND3_SCORE+" INTEGER, "+KEY_STATUS
            +" INTEGER, "+KEY_TIMESTAMP+" TEXT "+")";

    //Player Table
    private static final String CREATE_TABLE_PLAYERS = "CREATE TABLE "
            + TABLE_PLAYERS +" ( " + KEY_PLAYER_ID + " TEXT PRIMARY KEY,"+KEY_GAME_ID
            + " TEXT, "+KEY_PLAYER_NAME+" TEXT,"+KEY_PLAYER_TYPE+" TEXT,"+" FOREIGN KEY( "+KEY_GAME_ID+" ) REFERENCES "
            + TABLE_GAME+"("+KEY_GAME_ID+") )";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DataBaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DataBaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_GAME);
        db.execSQL(CREATE_TABLE_PLAYERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYERS);
      //  db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO_TAG);

        // create new tables
        onCreate(db);
    }

    public void insertGame(String gameId, String time){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GAME_ID,gameId);
        values.put(KEY_ROUND1_SCORE,0);
        values.put(KEY_ROUND2_SCORE,0);
        values.put(KEY_ROUND3_SCORE,0);
        values.put(KEY_STATUS,0);
        values.put(KEY_TIMESTAMP,time);

        db.insert(TABLE_GAME,null,values);
    }

    public void insertHeadPlayer(String gameId,String playerId, String playerName){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLAYER_ID,playerId);
        values.put(KEY_PLAYER_NAME,playerName);
        values.put(KEY_GAME_ID,gameId);
        values.put(KEY_PLAYER_TYPE,"HEAD");

        db.insert(TABLE_PLAYERS,null,values);
    }

    public void insertMemberPlayer(String gameId,String playerId,String playerName){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLAYER_ID,playerId);
        values.put(KEY_PLAYER_NAME,playerName);
        values.put(KEY_GAME_ID,gameId);
        values.put(KEY_PLAYER_TYPE,"MEMBER");

        db.insert(TABLE_PLAYERS,null,values);
    }

    public String getHeadPlayer(String gameId){
        SQLiteDatabase db = this.getReadableDatabase();
        String playerName="";
        Cursor c = db.query(TABLE_PLAYERS,null,KEY_GAME_ID+"=? and "+KEY_PLAYER_TYPE+"=?",new String[]{gameId,"HEAD"},null,null,null);
        if(c.moveToFirst()){
            playerName = c.getString(c.getColumnIndex(KEY_PLAYER_NAME));
        }
        return playerName;
    }

    public List<String> getMemberPlayers(String gameId){
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> memberList = new ArrayList<>();

        Cursor c = db.query(TABLE_PLAYERS,null,KEY_GAME_ID+"=? and "+KEY_PLAYER_TYPE+"=?",new String[]{gameId,"MEMBER"},null,null,null);

        if(c.moveToFirst()){
            do{
                memberList.add(c.getString(c.getColumnIndex(KEY_PLAYER_NAME)));
            }while(c.moveToNext());
        }
        return memberList;
    }

    public List<Game> getAllGames(){
        List<Game> games = new ArrayList<Game>();
       // String selectQuery = "SELECT * FROM "+TABLE_GAME;


        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.query(TABLE_GAME,null,null,null,null,null,null);

        if (c.moveToFirst()) {
            do {
                String gameId = c.getString(c.getColumnIndex(KEY_GAME_ID));
             //   Cursor c2 = db.query(TABLE_PLAYERS,null,KEY_GAME_ID+"=?",new String[]{gameId},null,null,null);
                List<PlayerDetails> playersList = new ArrayList<PlayerDetails>();
//                if(c2.moveToFirst()){
//                    do {
//                        String playerId = c2.getString(c.getColumnIndex(KEY_PLAYER_ID));
//                        String playerName = c2.getString(c.getColumnIndex(KEY_PLAYER_NAME));
//                        playersList.add(new PlayerDetails(playerId, playerName));
//                    }while(c2.moveToNext());
//                }
                games.add(new Game(gameId,playersList));
            } while (c.moveToNext());
        }
        return games;
    }
    public List<Integer> getScores(String gameId){
        List<Integer> scores = new ArrayList<Integer>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_GAME,null,KEY_GAME_ID+"=?",new String[]{gameId},null,null,null);
        if(c.moveToFirst()){
            int r1=c.getInt(c.getColumnIndex(KEY_ROUND1_SCORE));
            int r2 = c.getInt(c.getColumnIndex(KEY_ROUND2_SCORE));
            int r3 = c.getInt(c.getColumnIndex(KEY_ROUND3_SCORE));
            scores.add(Integer.valueOf(r1));
            scores.add(Integer.valueOf(r2));
            scores.add(Integer.valueOf(r3));

        }
        return scores;
    }
//    public List<PlayerDetails> getPlayers(String gameId){
//        SQLiteDatabase db = this.getReadableDatabase();
//        List<PlayerDetails> playerList = new ArrayList<>();
//        Cursor c = db.query(TABLE_PLAYERS,null,KEY_GAME_ID+"=?",new String[]{gameId},null,null,null);
//
//        if(c.moveToFirst()){
//            do{
//                String playerId = c.getString(c.getColumnIndex(KEY_PLAYER_ID));
//                String playerName = c.getString()
//                playerList.add(new PlayerDetails())
//            }while(c.moveToNext());
//        }
//    }

    public void updateScore(String gameId, int score1,int score2, int score3){
        ContentValues values = new ContentValues();
        values.put(KEY_ROUND1_SCORE,score1);
        values.put(KEY_ROUND2_SCORE,score2);
        values.put(KEY_ROUND3_SCORE,score3);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_PLAYERS,values,KEY_GAME_ID+"=?",new String[]{gameId});
    }

    public void updateStatus(String gameId){
        ContentValues values = new ContentValues();
        values.put(KEY_STATUS,1);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_PLAYERS,values,KEY_GAME_ID+"=?",new String[]{gameId});
    }



    public void deleteGame(String gameId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLAYERS,KEY_GAME_ID+"=?",new String[]{gameId});
        db.delete(TABLE_GAME,KEY_GAME_ID+"=?",new String[]{gameId});
    }

    public void deleteMember(String gameId,String playerId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLAYERS,KEY_GAME_ID+"=? and "+KEY_PLAYER_ID+"=?",new String[]{gameId,playerId});
    }
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
