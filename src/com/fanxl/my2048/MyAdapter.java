package com.fanxl.my2048;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fanxl.my2048.entity.Person;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
	
	private List<Person> persons;
	private ImageLoader imageLoader;
	public MyAdapter() {}
	private DisplayImageOptions options;  

	public MyAdapter(List<Person> persons) {
		this.persons = persons;
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()  
		 .showImageOnLoading(R.drawable.logo) //设置图片在下载期间显示的图片  
		 .showImageForEmptyUri(R.drawable.logo)//设置图片Uri为空或是错误的时候显示的图片  
		.showImageOnFail(R.drawable.logo)  //设置图片加载/解码过程中错误时候显示的图片
		.cacheInMemory(true)//设置下载的图片是否缓存在内存中  
		.cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中  
		.build();//构建完成
	}

	@Override
	public int getItemCount() {
		return persons.size();
	}

	@Override
	public void onBindViewHolder(MyViewHolder viewHolder, int position) {
		Person p = persons.get(position);
		viewHolder.card_item_name.setText(p.getNickname());
		viewHolder.card_item_ranking.setText("排名:第"+p.getRankIng()+"名");
		viewHolder.card_item_score.setText("总得分:"+p.getScore()+"分");
		imageLoader.displayImage(p.getHeadImage(), viewHolder.card_item_head, options);
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		// 给ViewHolder设置布局文件  
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);  
        return new MyViewHolder(v);  
	}
	
	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
	}
	
	//重写自定义ViewHolder
	class MyViewHolder extends ViewHolder{
		
		public RoundImageView card_item_head;
		public TextView card_item_name, card_item_ranking, card_item_score;

		public MyViewHolder(View v) {
			super(v);
			
			card_item_head = (RoundImageView) v.findViewById(R.id.card_item_head);
			card_item_name = (TextView) v.findViewById(R.id.card_item_name);
			card_item_ranking = (TextView) v.findViewById(R.id.card_item_ranking);
			card_item_score = (TextView) v.findViewById(R.id.card_item_score);
		}
	}
}
