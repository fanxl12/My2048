package com.fanxl.my2048;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.jpush.android.api.JPushInterface;

import com.fanxl.my2048.entity.Person;
import com.fanxl.my2048.util.Util;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * Created by fanxl on 2015/4/10.
 */
public class Main extends Activity {

	private SharedPreferences sp;
	public static final String GAME2048 = "GAME_2048";
	public static final String NICK_NAME = "NICK_NAME";
	public static final String USER_DATA = "USER_DATA";
	public static final String HEAD_IMAGE = "HEAD_IMAGE";
	public static final String SCORE = "SCORE";
	public static final String PERSON_ID = "PERSON_ID";
	public static final String PERSON_SORT = "PERSON_SORT";

	// QQ分享和授权登录
	private Tencent mTencent;
	public static final String APP_ID = "1104512324";
	private UserInfo mInfo;

	// 保存用户信息
	private Person person;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				JSONObject response = (JSONObject) msg.obj;
				if (response.has("nickname")) {
					try {
						person.setNickname(response.getString("nickname"));
						person.setGender(response.getString("gender"));
						person.setHeadImage(response
								.getString("figureurl_qq_2"));
						getPersons(person.getNickname());
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initVarible();
		initView();
		sp = getSharedPreferences(GAME2048, Context.MODE_PRIVATE);
		String nickName = sp.getString(NICK_NAME, "");
		if (!TextUtils.isEmpty(nickName)) {
			getPersons(nickName);
		}

	}

	/**
	 * 初始化View
	 */
	private void initView() {
		findViewById(R.id.bt_login).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						loginToQQ();
					}
				});
	}

	/**
	 * QQ登陆授权
	 */
	private void loginToQQ() {
		if (mTencent != null && !mTencent.isSessionValid()) {
			mTencent.login(this, "all", loginListener);
		} else {
			// Server-Side 模式的登陆, 先退出，再进行SSO登陆
			mTencent.logout(this);
			mTencent.login(this, "all", loginListener);
		}
	}

	/**
	 * 初始化一些重要的参数
	 */
	private void initVarible() {
		// Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
		// 其中APP_ID是分配给第三方应用的appid，类型为String。
		mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());

		// 初始化 Bmob SDK
		// 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
		Bmob.initialize(this, "a27a015aca9078f2dc8d67ef047b93e3");
		
		person = new Person();
	}

	IUiListener loginListener = new BaseUiListener() {
		@Override
		protected void doComplete(JSONObject values) {
			initOpenidAndToken(values);
			updateUserInfo();
		}
	};

	private void updateUserInfo() {
		if (mTencent != null && mTencent.isSessionValid()) {
			IUiListener listener = new IUiListener() {

				@Override
				public void onError(UiError e) {

				}

				@Override
				public void onComplete(final Object response) {
					Message msg = new Message();
					msg.obj = response;
					msg.what = 0;
					mHandler.sendMessage(msg);
				}

				@Override
				public void onCancel() {

				}
			};
			mInfo = new UserInfo(this, mTencent.getQQToken());
			mInfo.getUserInfo(listener);

		} 
	}

	private void initOpenidAndToken(JSONObject jsonObject) {
		try {
			String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
			String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
			String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
			if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
					&& !TextUtils.isEmpty(openId)) {
				mTencent.setAccessToken(token, expires);
				mTencent.setOpenId(openId);
			}
		} catch (Exception e) {
		}
	}

	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			if (null == response) {
				Util.showResultDialog(Main.this, "返回为空", "登录失败");
				return;
			}
			JSONObject jsonResponse = (JSONObject) response;
			if (null != jsonResponse && jsonResponse.length() == 0) {
				Util.showResultDialog(Main.this, "返回为空", "登录失败");
				return;
			}
			// Util.showResultDialog(Main.this, response.toString(), "登录成功");
			// handlePrizeShare();
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onError(UiError e) {
			Util.toastMessage(Main.this, "授权错误: " + e.errorDetail);
			Util.dismissDialog();
		}

		@Override
		public void onCancel() {
			Util.toastMessage(Main.this, "授权登录取消");
			Util.dismissDialog();
		}
	}

	private void getPersons(final String nickName) {
		Util.showProgressDialog(this, "请稍后", "正在验证用户信息");
		BmobQuery<Person> query = new BmobQuery<Person>();
		// 查询所有数据，然后以分数进行降序
		query.order("-score");
		query.findObjects(this, new FindListener<Person>() {
			@Override
			public void onSuccess(List<Person> persons) {
				if (persons.size() > 0) {
					int i = 1;
					boolean exists = false;
					for (Person p : persons) {
						if (nickName.equals(p.getNickname())) {
							exists = true;
							Intent intent = new Intent(Main.this,
									MainActivity.class);
							Bundle bundle = new Bundle();
							person = p;
							bundle.putString(NICK_NAME, person.getNickname());
							bundle.putString(HEAD_IMAGE, person.getHeadImage());
							bundle.putInt(SCORE, person.getScore());
							bundle.putInt(PERSON_SORT, i);
							bundle.putString(PERSON_ID, person.getObjectId());
							intent.putExtra(USER_DATA, bundle);
							startActivity(intent);
							finish();
							Util.dismissDialog();
							break;
						}
						i++;
					}
					if(!exists)savePerson();
				} else {
					savePerson();
				}
			}

			@Override
			public void onError(int code, String msg) {
				savePerson();
			}
		});
	}

	/**
	 * 保存用户信息到服务器
	 */
	private void savePerson() {
		Util.showProgressDialog(this, "请稍后", "正在注册用户信息");
		person.save(this, new SaveListener() {

			@Override
			public void onSuccess() {
				Intent intent = new Intent(Main.this, MainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(NICK_NAME, person.getNickname());
				bundle.putString(HEAD_IMAGE, person.getHeadImage());
				bundle.putString(PERSON_ID, person.getObjectId());
				bundle.putInt(SCORE, 0);
				intent.putExtra(USER_DATA, bundle);
				startActivity(intent);
				finish();
				Util.dismissDialog();
			}

			@Override
			public void onFailure(int code, String msg) {

			}
		});
	}
}
