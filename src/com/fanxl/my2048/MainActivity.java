package com.fanxl.my2048;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.listener.UpdateListener;
import cn.jpush.android.api.JPushInterface;

import com.fanxl.my2048.entity.Person;
import com.fanxl.my2048.util.ImageDown;


public class MainActivity extends Activity {

    private TextView main_tv_score, main_tv_best;
    public  static MainActivity mainActivity = null;
    private int score, bestScore;
    private AnimLayer animLayer = null;
    public static final String BEST_SCORE = "BEST_SCORE";
    private final int DOWN = 100;
    
    //设置用户相关信息
    private RoundImageView game_iv_head;
    private TextView game_tv_name, game_tv_ranking;
    private SharedPreferences sp;
    private String objectId;
    
    Handler handler = new Handler(){
    	
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case DOWN:
				Bitmap imgBt = (Bitmap) msg.obj;
				if(imgBt != null){
					game_iv_head.setImageBitmap(imgBt);
				}
				break;

			default:
				break;
			}
    	};
    };

    public MainActivity(){
        mainActivity = this;
    }

    public static MainActivity getMainActivity(){
        return mainActivity;
    }

    public void clearScore(){
        showScore(0);
    }

    public void showScore(int currentScore){
        main_tv_score.setText("当前得分:"+currentScore);
        if(score>bestScore){
        	bestScore = score;
        	main_tv_best.setText("最高记录:"+bestScore);
        }
    }

    public void addScore(int s){
        score += s;
        showScore(score);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences(Main.GAME2048, Context.MODE_PRIVATE);
        init();
        initVarible();
    }

    @SuppressLint("NewApi") 
    private void initVarible() {
    	Bundle bundle = getIntent().getBundleExtra(Main.USER_DATA);
    	game_tv_name.setText(bundle.getString(Main.NICK_NAME, ""));
    	bestScore = Math.max(sp.getInt(BEST_SCORE, 0), bundle.getInt(Main.SCORE));
    	main_tv_best.setText("最高得分:"+bestScore);
    	objectId = bundle.getString(Main.PERSON_ID);
    	if(bestScore > 0){
    		game_tv_ranking.setText("全球排名:第"+bundle.getInt(Main.PERSON_SORT)+"名");
    	}else{
    		game_tv_ranking.setText("全球排名:暂无排名");
    	}
    	
    	File file = new File(ImageDown.PATH+"/image.png");
    	String nickName = sp.getString(Main.NICK_NAME, "");
    	if(file.exists() && nickName.equals(bundle.getString(Main.NICK_NAME, ""))){
    		Bitmap bitmap = BitmapFactory.decodeFile(ImageDown.PATH+"/image.png");
    		game_iv_head.setImageBitmap(bitmap);
    	}else{
    		String url = bundle.getString(Main.HEAD_IMAGE);
    		if(TextUtils.isEmpty(url))return;
    		new DownImage(url).start();
    	}
    	
    	sp.edit().putString(Main.NICK_NAME, bundle.getString(Main.NICK_NAME, "")).commit();
    	
    	JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }
    
    /**
     * 保存最高分
     */
    public void saveScore(final boolean isExit){
    	
    	if(score>=bestScore){
    		sp.edit().putInt(BEST_SCORE, score).commit();
    		Person person = new Person();
    		person.setScore(score);
    		if(isExit)Toast.makeText(this, "正在保存数据，请稍后...", Toast.LENGTH_SHORT).show();
    		person.update(this, objectId, new UpdateListener() {
				
				@Override
				public void onSuccess() {
					if(isExit){
						Toast.makeText(MainActivity.this, "数据已经保存", Toast.LENGTH_SHORT).show();
						MainActivity.this.finish();
					}
				}
				
				@Override
				public void onFailure(int arg0, String arg1) {
					if(isExit){
						Toast.makeText(MainActivity.this, "数据保存失败", Toast.LENGTH_SHORT).show();
						MainActivity.this.finish();
					}
				}
			});
    	}else{
    		if(isExit)ExitApp();
    	}
    }
    
    private long exitTime = 0;
	public void ExitApp() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(this, "再按一次退出游戏", Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {
			this.finish();
		}
	}
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			saveScore(true);
		}
		return false;
	}

    private void init() {
        main_tv_score = (TextView)findViewById(R.id.main_tv_score);
        animLayer = (AnimLayer)findViewById(R.id.animLayer);
        
        game_iv_head = (RoundImageView) findViewById(R.id.game_iv_head);
        game_tv_name = (TextView)findViewById(R.id.game_tv_name);
        game_tv_ranking = (TextView)findViewById(R.id.game_tv_ranking);
        main_tv_best = (TextView)findViewById(R.id.main_tv_best);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    public AnimLayer getAnimLayer() {
        return animLayer;
    }
    
    class DownImage extends Thread{
    	
    	private String imgUrl;

		public DownImage(String imgUrl) {
			this.imgUrl = imgUrl;
		}
		
		@Override
		public void run() {
			Bitmap bitmap = ImageDown.getImage(imgUrl);
			handler.obtainMessage(DOWN, bitmap).sendToTarget();
		}
    	
    }
}
