package com.fanxl.my2048;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanxl on 2015/4/8.
 */
public class GameView extends LinearLayout {

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("NewApi") 
    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(0xffbbada0);


        setOnTouchListener(new OnTouchListener() {

            private float startX, startY, offsetX, offsetY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        offsetX = event.getX() - startX;
                        offsetY = event.getY() - startY;

                        if(Math.abs(offsetX) > Math.abs(offsetY)){
                            if(offsetX<-5){
                                swipeLeft();
                            }else if(offsetX>5){
                                swipeRight();
                            }
                        }else{
                            if (offsetY<-5){
                                swipeUp();
                            }else if (offsetY>5){
                                swipeDown();
                            }
                        }

                        break;

                }
                return true;
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        addCard(Config.CARD_WIDTH,Config.CARD_WIDTH);

//        int cardWidth = (Math.min(w, h)-10)/4;
//        addCard(cardWidth, cardWidth);


        startGame();
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    	
    	int w = MeasureSpec.getSize(widthMeasureSpec);
    	int h = MeasureSpec.getSize(heightMeasureSpec);
    	
    	Config.CARD_WIDTH = (Math.min(w, h)-10)/Config.LINES;
    	setMeasuredDimension(w, w);
    }

    private void addCard(int cardWidth, int cardHeight){
        Card c;

        LinearLayout line;
        LinearLayout.LayoutParams lineLp;

        for (int y = 0; y < Config.LINES; y++) {
            line = new LinearLayout(getContext());
            lineLp = new LinearLayout.LayoutParams(-1, cardHeight);
            addView(line, lineLp);

            for (int x = 0; x < Config.LINES; x++) {
                c = new Card(getContext());
                line.addView(c, cardWidth, cardHeight);

                cardMap[x][y] = c;
            }
        }


//        for (int i=0; i < 4; i++){
//            for (int j=0; j < 4; j++){
//                c = new Card(getContext());
//                c.setNum(0);
//                addView(c, cardWidth, cardHeight);
//                cardMap[j][i] = c;
//            }
//        }
    }

    /**
     * 添加随机数
     */
    private void addRandomNum(){
        emptyPoints.clear();

        for (int y = 0; y < Config.LINES; y++) {
            for (int x = 0; x < Config.LINES; x++) {
                if (cardMap[x][y].getNum()<=0) {
                    emptyPoints.add(new Point(x, y));
                }
            }
        }

//        for (int i=0; i < Config.LINES; i++){
//           for (int j=0; j < Config.LINES; j++){
//                if(cardMap[j][i].getNum()<=0){
//                    emptyPoints.add(new Point(j, i));
//                }
//           }
//       }

        if (emptyPoints.size()>0) {

            Point p = emptyPoints.remove((int)(Math.random()*emptyPoints.size()));
            cardMap[p.x][p.y].setNum(Math.random()>0.1?2:4);

            MainActivity.getMainActivity().getAnimLayer().createScaleTo1(cardMap[p.x][p.y]);
        }

    }

    /**
     * 开始游戏
     */
    private void startGame(){

        MainActivity.getMainActivity().clearScore();

        //首先清理，然后再添加随机数
        for (int y=0; y < Config.LINES; y++){
            for (int x=0; x < Config.LINES; x++){
                cardMap[x][y].setNum(0);
            }
        }

        addRandomNum();
        addRandomNum();
    }

    private void swipeLeft(){

        boolean merge = false;
        for (int y=0; y < Config.LINES; y++){
            for (int x=0; x < Config.LINES; x++){
                for (int x1=x+1; x1<Config.LINES; x1++){
                    if(cardMap[x1][y].getNum()>0){

                        if(cardMap[x][y].getNum()<=0){

                            //MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardMap[x1][y],cardMap[x][y], x1, x, y, y);

                            cardMap[x][y].setNum(cardMap[x1][y].getNum());
                            cardMap[x1][y].setNum(0);
                            x--;
                            merge = true;
                        }else if(cardMap[x][y].equals(cardMap[x1][y])){
                            //MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardMap[x1][y], cardMap[x][y],x1, x, y, y);

                            cardMap[x][y].setNum(cardMap[x][y].getNum()*2);
                            cardMap[x1][y].setNum(0);
                            MainActivity.getMainActivity().addScore(cardMap[x][y].getNum());
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

    private void swipeRight(){

        boolean merge = false;
        for (int y=0; y < Config.LINES; y++){
            for (int x=3; x >= 0; x--){
                for (int x1=x-1; x1>=0; x1--){
                    if(cardMap[x1][y].getNum()>0){

                        if(cardMap[x][y].getNum()<=0){

                            //MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardMap[x1][y], cardMap[x][y],x1, x, y, y);

                            cardMap[x][y].setNum(cardMap[x1][y].getNum());
                            cardMap[x1][y].setNum(0);

                            x++;
                            merge = true;
                        }else if(cardMap[x][y].equals(cardMap[x1][y])){

                            //MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardMap[x1][y], cardMap[x][y],x1, x, y, y);
                            cardMap[x][y].setNum(cardMap[x][y].getNum()*2);
                            cardMap[x1][y].setNum(0);
                            MainActivity.getMainActivity().addScore(cardMap[x][y].getNum());
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

    private void swipeUp(){

        boolean merge = false;
        for (int x=0; x < Config.LINES; x++){
            for (int y=0; y < Config.LINES; y++){
                for (int y1=y+1; y1<Config.LINES; y1++){
                    if(cardMap[x][y1].getNum()>0){

                        if(cardMap[x][y].getNum()<=0){

                            //MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardMap[x][y1],cardMap[x][y], x, x, y1, y);

                            cardMap[x][y].setNum(cardMap[x][y1].getNum());
                            cardMap[x][y1].setNum(0);

                            y--;
                            merge = true;
                        }else if(cardMap[x][y].equals(cardMap[x][y1])){

                           //MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardMap[x][y1],cardMap[x][y], x, x, y1, y);
                            cardMap[x][y].setNum(cardMap[x][y].getNum()*2);
                            cardMap[x][y1].setNum(0);
                            MainActivity.getMainActivity().addScore(cardMap[x][y].getNum());
                            merge =true;
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

    private void swipeDown(){

        boolean merge = false;
        for (int x=0; x < Config.LINES; x++){
            for (int y=3; y >= 0; y--){
                for (int y1=y-1; y1>=0; y1--){
                    if(cardMap[x][y1].getNum()>0){

                        if(cardMap[x][y].getNum()<=0){

                           // MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardMap[x][y1],cardMap[x][y], x, x, y1, y);
                            cardMap[x][y].setNum(cardMap[x][y1].getNum());
                            cardMap[x][y1].setNum(0);

                            y++;
                            merge = true;
                        }else if(cardMap[x][y].equals(cardMap[x][y1])){

                           // MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardMap[x][y1],cardMap[x][y], x, x, y1, y);
                            cardMap[x][y].setNum(cardMap[x][y].getNum()*2);
                            cardMap[x][y1].setNum(0);

                            MainActivity.getMainActivity().addScore(cardMap[x][y].getNum());
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

    private Card[][] cardMap = new Card[Config.LINES][Config.LINES];
    private List<Point> emptyPoints = new ArrayList<Point>();

    /**
     * 判断游戏是否结束
     */
    private void checkComplete(){
        boolean complete = true;
        ALL:
        for (int y = 0; y < Config.LINES; y++) {
            for (int x = 0; x < Config.LINES; x++) {
                if (cardMap[x][y].getNum()==0||
                        (x>0&&cardMap[x][y].equals(cardMap[x-1][y]))||
                        (x<Config.LINES-1&&cardMap[x][y].equals(cardMap[x+1][y]))||
                        (y>0&&cardMap[x][y].equals(cardMap[x][y-1]))||
                        (y<Config.LINES-1&&cardMap[x][y].equals(cardMap[x][y+1]))) {

                    complete = false;
                    break ALL;
                }
            }
        }

        if (complete){
            new AlertDialog.Builder(getContext()).setTitle("游戏结束").setMessage("亲，继续加油哦")
                    .setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startGame();
                            MainActivity.getMainActivity().saveScore(false);
                            MainActivity.getMainActivity().clearScore();
                        }
                    }).show();
        }
    }
}
