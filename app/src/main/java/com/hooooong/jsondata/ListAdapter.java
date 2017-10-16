package com.hooooong.jsondata;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hooooong.jsondata.model.User;

import java.util.List;

/**
 * Created by Android Hong on 2017-10-16.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.Holder> {

    List<User> userList;

    public ListAdapter(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final User user = userList.get(position);
        holder.setTextId(user.getId()+"");
        holder.setTextLogin(user.getLogin());
        holder.setImageView(user.getAvatar_url());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private TextView textId, textLogin;
        private ImageView imageView;

        public Holder(View itemView) {
            super(itemView);
            textId = itemView.findViewById(R.id.textId);
            textLogin = itemView.findViewById(R.id.textLogin);
            imageView = itemView.findViewById(R.id.imageView);
        }

        public void setTextId(String id) {
            textId.setText(id);
        }

        public void setTextLogin(String login) {
            textLogin.setText(login);
        }

        public void setImageView(final String imageUrl) {
            /*
            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .into(imageView);
            */
            new AsyncTask<Void, Void, Bitmap>(){
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Bitmap doInBackground(Void... voids) {
                    return Remote.getImage(imageUrl);
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    //JSONString 을 Parsing 하여 List 에 넣어둔다.
                    imageView.setImageBitmap(bitmap);
                }
            }.execute();
        }
    }
}
