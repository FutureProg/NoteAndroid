package com.example.nickolasmorrison.mynote;

import android.arch.lifecycle.ViewModelProvider;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.nickolasmorrison.mynote.data.NoteViewModel;
import com.example.nickolasmorrison.mynote.views.fragments.MainFragment;

public class MainActivity extends AppCompatActivity implements MainFragment.InteractionListener {

    NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager manager = this.getSupportFragmentManager();
        MainFragment mainFragment = MainFragment.newInstance();
        manager.beginTransaction().replace(R.id.fragment_container,mainFragment).commit();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
