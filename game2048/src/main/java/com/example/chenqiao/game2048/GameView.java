package com.example.chenqiao.game2048;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CHENQIAO on 2016/3/21.
 */
public class GameView extends GridLayout {

    private int lines;
    private Card[][] cards;
    private int[][]  histroycards;
    private List<Point> emptyPoints = new ArrayList<Point>();
    private MainActivity mainActivity= MainActivity.getActivity();

    boolean canRevert =false;
    private int score;
    private int mTarget;

    public GameView(Context context) {
        super(context);
        initGameView();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGameView();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initGameView();
    }

    public void initGameView(){

        MyApplication app = (MyApplication) mainActivity.getApplication();
        mTarget = app.getTarget();

        lines = app.getLineNumber();
        cards = new Card[lines][lines];
        histroycards = new int[lines][lines];

        setColumnCount(lines);
        setBackgroundResource(R.drawable.bgshape3);
        setPadding(5, 5, 5, 5);



        setOnTouchListener(new View.OnTouchListener() {

            private float startX, startY, offsetX, offsetY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        score = mainActivity.getScore();
                        saveHistroy();
                        break;
                    case MotionEvent.ACTION_UP:
                        offsetX = event.getX() - startX;
                        offsetY = event.getY() - startY;


                        if (Math.abs(offsetX) > Math.abs(offsetY)) {
                            if (offsetX < -5) {
                                swipeLeft();

                            } else if (offsetX > 5) {
                                swipeRight();

                            }
                        } else {
                            if (offsetY < -5) {
                                swipeUp();

                            } else if (offsetY > 5) {
                                swipeDown();

                            }
                        }

                        break;
                }
                return true;
            }
        });

    }
    //生成新的item的动画
    private void animCreate(Card card) {
        ScaleAnimation animation = new ScaleAnimation(0.1f, 1, 0.1f, 1, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(100);
        //先将动画置空，在启动新的动画
        card .setAnimation(null);
        card .getLabel().startAnimation(animation);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int cardWidth = (Math.min(w, h)-10)/lines;

        addCards(cardWidth, cardWidth);


        startGame();
    }

    private void addCards(int cardWidth,int cardHeight){

         Card card;

        for (int y = 0; y < lines; y++) {
            for (int x = 0; x < lines; x++) {
                card = new Card(getContext());
                card.setNum(0);
                addView(card, cardWidth, cardHeight);

                cards[x][y] = card;
            }
        }
    }

    public void startGame(){

        for (int y = 0; y < lines; y++) {
            for (int x = 0; x < lines; x++) {
                cards[x][y].setNum(0);
            }
        }

        addRandomNum();
        addRandomNum();
    }

    private void addRandomNum(){

        emptyPoints.clear();

        for (int y = 0; y < lines; y++) {
            for (int x = 0; x < lines; x++) {
                if (cards[x][y].getNum()<=0) {
                    emptyPoints.add(new Point(x, y));
                }
            }
        }
        Point p = emptyPoints.remove((int)(Math.random()*emptyPoints.size()));
        cards[p.x][p.y].setNum(Math.random() > 0.2 ? 2 : 4);
        animCreate(cards[p.x][p.y]);
    }
    private void swipeLeft(){
        boolean merge = false;
        for(int y = 0;y<lines;y++){
            for(int x = 0;x<lines;x++){
                for(int next_x=x+1;next_x<lines;next_x++){
                    if(cards[next_x][y].getNum()>0){
                        if (cards[x][y].getNum()<=0){
                            cards[x][y].setNum(cards[next_x][y].getNum());
                            cards[next_x][y].setNum(0);
                            x--;
                            merge = true;
                        }else if(cards[x][y].getNum()==cards[next_x][y].getNum()){
                            cards[x][y].setNum(cards[x][y].getNum()*2);
                            cards[next_x][y].setNum(0);
                            MainActivity.getActivity().addScore(cards[x][y].getNum());
                            merge = true;
                        }
                        break;
                    }
                }
            }
        }
            if(merge){
                addRandomNum();
                checkComplete();
            }

    }
    private void swipeRight(){
        boolean merge = false;
        for(int y = 0;y<lines;y++){
            for(int x = lines-1;x>=0;x--){
                for(int pre_x = x-1;pre_x>=0;pre_x--){
                    if(cards[pre_x][y].getNum()>0){
                        if (cards[x][y].getNum()<=0){
                            cards[x][y].setNum(cards[pre_x][y].getNum());
                            cards[pre_x][y].setNum(0);
                            x++;
                            merge = true;
                        }else if(cards[x][y].getNum()==cards[pre_x][y].getNum()){
                            cards[x][y].setNum(cards[x][y].getNum()*2);
                            cards[pre_x][y].setNum(0);
                            MainActivity.getActivity().addScore(cards[x][y].getNum());
                            merge = true;
                        }
                        break;
                    }
                }
            }
        }
        if(merge){
            addRandomNum();
            checkComplete();
        }
    }
    private void swipeUp(){

        boolean merge = false;
        for(int x=0;x<lines;x++){
            for(int y=0;y<lines;y++){
                for (int next_y=y+1;next_y<lines;next_y++){
                    if(cards[x][next_y].getNum()>0){
                        if(cards[x][y].getNum()<=0){
                            cards[x][y].setNum(cards[x][next_y].getNum());
                            cards[x][next_y].setNum(0);
                            y--;
                                merge = true;
                        }else if(cards[x][y].getNum()==cards[x][next_y].getNum()){

                            cards[x][y].setNum(cards[x][y].getNum()*2);
                                cards[x][next_y].setNum(0);
                            MainActivity.getActivity().addScore(cards[x][y].getNum());
                                merge = true;
                            }
                        break;
                    }
                }
            }
        }
        if (merge) {
            addRandomNum();
            checkComplete();
        }

    }
    private void swipeDown(){
        boolean merge = false;
        for(int x = 0;x<lines;x++){
            for (int y = lines-1;y>=0;y--){
                for(int pre_y=y-1;pre_y>=0;pre_y--){
                    if(cards[x][pre_y].getNum()>0){
                        if(cards[x][y].getNum()<=0){
                            cards[x][y].setNum(cards[x][pre_y].getNum());
                            cards[x][pre_y].setNum(0);
                            y++;
                            merge = true;
                        }else if (cards[x][y].getNum()==cards[x][pre_y].getNum()){
                            cards[x][y].setNum(cards[x][y].getNum()*2);
                            cards[x][pre_y].setNum(0);
                            MainActivity.getActivity().addScore(cards[x][y].getNum());
                            merge = true;
                        }
                        break;
                    }
                }

            }
        }

        if (merge){
            addRandomNum();
            checkComplete();
        }


    }



    //恢复上一步的状态
    public void revert(){


        //添加一个flag，当且仅当histroy矩阵有过赋值之后，才置位1.
        if (canRevert) {
            for (int i = 0; i < lines; i++)
                for (int j = 0; j < lines; j++) {
                    cards[i][j].setNum(histroycards[i][j]);
                    mainActivity.setScore(score);

                }
        }
    }


    //把当前的记录保存到history矩阵中
    private void saveHistroy() {

        for (int i=0;i<lines;i++)
            for (int j=0;j<lines;j++){
                histroycards[i][j] =  cards[i][j].getNum() ;
            }

        canRevert=true;
    }

    private void checkComplete(){
        boolean completed = true;
        for(int y = 0;y<lines;y++){
            for(int x = 0;x<lines;x++){
                if(cards[x][y].getNum()==0||(x>0&&(cards[x][y].getNum()==cards[x-1][y].getNum()))||
                        (x<lines-1&&(cards[x][y].getNum()==cards[x+1][y].getNum()))||
                        (y>0&&(cards[x][y].getNum()==cards[x][y-1].getNum()))||
                        (y<lines-1&&(cards[x][y].getNum()==cards[x][y+1].getNum()))){
                    completed = false;
                }
            }

        }

        if(completed){
            new AlertDialog.Builder(getContext()).setTitle("你好").setMessage("游戏结束").setPositiveButton("重新开始", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startGame();
                }
            }).setCancelable(false).show();
        }




        for(int y = 0;y<lines;y++) {
            for (int x = 0; x < lines; x++) {
                if (cards[x][y].getNum()==mTarget){
                    new AlertDialog.Builder(getContext()).setTitle("恭喜").setMessage("目标完成").setPositiveButton("重新开始", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startGame();
                        }
                    }).setCancelable(false).show();
                }
            }
        }
    }









}
