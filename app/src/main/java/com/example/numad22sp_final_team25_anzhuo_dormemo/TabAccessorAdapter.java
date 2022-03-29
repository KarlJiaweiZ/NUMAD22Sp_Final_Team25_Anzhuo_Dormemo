package com.example.numad22sp_final_team25_anzhuo_dormemo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAccessorAdapter extends FragmentPagerAdapter {
    public TabAccessorAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 1:
                BoardFragment boardFragment = new BoardFragment();
                return boardFragment;
            case 2:
                BillFragment billFragment = new BillFragment();
                return billFragment;
            case 3:
                MeFragment meFragment = new MeFragment();
                return meFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Chats";
            case 1:
                return "Boards";
            case 2:
                return "Bills";
            case 3:
                return "Me";
            default:
                return null;
        }
    }
}
