package com.fanxl.my2048;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.fanxl.my2048.entity.Person;
import com.fanxl.my2048.util.Util;

public class Ranking extends Activity{
	
	private RecyclerView ranking_rv;
	private MyAdapter myAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ranking_view);
		initView();
	}

	private void initView() {
		
		ranking_rv = (RecyclerView) findViewById(R.id.ranking_rv);
		
		ranking_rv.setLayoutManager(new LinearLayoutManager(this));
		// 设置ItemAnimator  
		ranking_rv.setItemAnimator(new DefaultItemAnimator());  
        // 设置固定大小  
		ranking_rv.setHasFixedSize(true);
		getPersons();
	}
	
	private void getPersons() {
		Util.showProgressDialog(this, "请稍后", "正在加载数据");
		BmobQuery<Person> query = new BmobQuery<Person>();
		// 查询所有数据，然后以分数进行降序
		query.order("-score");
		query.findObjects(this, new FindListener<Person>() {
			@Override
			public void onSuccess(List<Person> persons) {
				if (persons.size() > 0) {
					int i = 1;
					for (Person p : persons) {
						p.setRankIng(i);
						i++;
					}
					 // 初始化自定义的适配器  
			        myAdapter = new MyAdapter(persons);  
			        // 为mRecyclerView设置适配器  
			        ranking_rv.setAdapter(myAdapter);
			        Util.dismissDialog();
				} 
			}

			@Override
			public void onError(int code, String msg) {
				Util.dismissDialog();
			}
		});
	}

}
