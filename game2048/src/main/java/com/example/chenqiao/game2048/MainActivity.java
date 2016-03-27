package com.example.chenqiao.game2048;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;

public class MainActivity extends AppCompatActivity{


    private int score = 0;
    private TextView tv_main_score;
    private TextView tv_main_record;
    private TextView tv_main_target;
    public GameView gameview;
    private MyApplication application;
    private static MainActivity mActivity;
    public static final String SP_KEY_BEST_SCORE = "bestScore";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        mActivity = this;
        setContentView(R.layout.activity_main);
        tv_main_score = (TextView) findViewById(R.id.tv_main_score);
        tv_main_record = (TextView) findViewById(R.id.tv_main_record);
        tv_main_target = (TextView) findViewById(R.id.tv_main_target);
        gameview = (GameView) findViewById(R.id.gameview);
        application = (MyApplication) getApplication();

        //此处有坑。
        tv_main_target.setText(application.getTarget()+"");
        tv_main_record.setText(application.getHighestRecord()+"");


    }

    public static MainActivity getActivity(){

        return mActivity;
    }

    public void clearScore(){
        score = 0;
        updateCurrentScore(score);
    }

    public void revert(View v) {
        gameview.revert();

    }


    public void restart(View v){

        new  AlertDialog.Builder(this).setTitle("提示：").setMessage("确认重新开始？").setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               clearScore();
                gameview.startGame();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    public void setting(View v) {
        startActivityForResult(new Intent(this, SettingActivity.class), 100);

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==100&&requestCode==100){
            updateTargetScore(application.getTarget());
            gameview.initGameView();
            gameview.startGame();
        }


    }
    public int getScore(){
        return this.score;
    }
    public void setScore(int score){
        this.score = score;
        updateCurrentScore(score);
    }

    public void addScore(int s){
        score+=s;
        updateCurrentScore(score);
        int maxScore = Math.max(score, application.getHighestRecord());
        application.setHighestRecord(maxScore);
        updateHighestScore(maxScore);

    }


    public void updateCurrentScore(int score){

        tv_main_score.setText(score + "");

    }

    public void updateHighestScore(int score){

        tv_main_record.setText(score+"");

    }

    public void updateTargetScore(int score){

        tv_main_target.setText(score+"");

    }



}
