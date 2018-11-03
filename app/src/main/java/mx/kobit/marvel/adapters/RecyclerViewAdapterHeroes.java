package mx.kobit.marvel.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import mx.kobit.marvel.R;
import mx.kobit.marvel.activities.HeroActivity;
import mx.kobit.marvel.model.Hero;

public class RecyclerViewAdapterHeroes extends RecyclerView.Adapter<RecyclerViewAdapterHeroes.MyViewHolder> {

    private Context mContext;
    private List<Hero> heroList;
    private RequestOptions glideOptions;

    public RecyclerViewAdapterHeroes(Context mContext, List<Hero> heroList) {
        this.mContext = mContext;
        this.heroList = heroList;
        //Request options for Glide when downloading images
        glideOptions = new RequestOptions().centerCrop().placeholder(R.drawable.loading_shape).error(R.drawable.loading_shape);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.heroe_row_item, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);
        //Config the click event to hero detail
        viewHolder.heroContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, HeroActivity.class);
                intent.putExtra("heroId", heroList.get(viewHolder.getAdapterPosition()).getId());
                intent.putExtra("heroName", heroList.get(viewHolder.getAdapterPosition()).getName());
                intent.putExtra("heroDescription", heroList.get(viewHolder.getAdapterPosition()).getDescription());
                intent.putExtra("heroThumbnail", heroList.get(viewHolder.getAdapterPosition()).getThumbnail());
                mContext.startActivity(intent);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.heroName.setText(heroList.get(position).getName());
        holder.heroDescription.setText(heroList.get(position).getDescription());
        //Load Image from Marvel site
        Glide.with(mContext).load(heroList.get(position).getThumbnail()).apply(glideOptions).into(holder.heroThumbnail);
    }

    @Override
    public int getItemCount() {
        return heroList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView heroName;
        TextView heroDescription;
        ImageView heroThumbnail;
        LinearLayout heroContainer;

        public MyViewHolder(View itemView) {
            super(itemView);
            heroContainer = itemView.findViewById(R.id.heroContainer);
            heroName = itemView.findViewById(R.id.heroName);
            heroDescription = itemView.findViewById(R.id.heroDescription);
            heroThumbnail = itemView.findViewById(R.id.heroThumbnail);
        }
    }
}
