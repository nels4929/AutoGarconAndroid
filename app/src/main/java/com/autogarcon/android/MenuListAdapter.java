package com.autogarcon.android;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.autogarcon.android.API.Menu;

import java.util.ArrayList;

/**
 * Adpater for the full view of a single menu item.
 * @author Riley Tschumper
 */
public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.MyViewHolder> {

    private ArrayList<Menu> menuList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView menuName;
        public ImageView menuImage;

        public MyViewHolder(View view) {
            super(view);
            menuName = (TextView) view.findViewById(R.id.menuName);
            menuImage = (ImageView) view.findViewById(R.id.menuImage);
        }
    }

    public MenuListAdapter(ArrayList<Menu> menuList) {
        this.menuList = menuList;
    }

    @Override
    public MenuListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View menuView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_menu, parent, false);
        return new MenuListAdapter.MyViewHolder(menuView);
    }

    @Override
    public void onBindViewHolder(MenuListAdapter.MyViewHolder holder, int position) {
        Menu menu = menuList.get(position);
        holder.menuName.setText(menu.getMenuName());
        ThumbnailManager.getInstance().getImage(menu.getImageUrl(), holder.menuImage);
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }
}