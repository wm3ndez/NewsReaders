package com.wmendez.newsreader.lib.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.wmendez.diariolibre.R;
import com.wmendez.newsreader.lib.event.NewsItemSelectedEvent;

import java.util.ArrayList;
import java.util.Arrays;

import de.greenrobot.event.EventBus;


/**
 * An activity representing a list of Items.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link com.wmendez.newsreader.lib.ui.FeedListFragment}.
 * <p/>
 */
public class FeedCategoryListActivity extends AppCompatActivity {

    SlidingTabsColorsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Spinner spinner = (Spinner) findViewById(R.id.spinner_nav);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        String[] regionsArray = getResources().getStringArray(R.array.newspapers);
        ArrayList<String> newspapers = new ArrayList<>(Arrays.asList(regionsArray));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.newspaper_spinner_item, newspapers);
        adapter.setDropDownViewResource(R.layout.newspaper_spinner_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new NewsPaperSelected());


        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            fragment = new SlidingTabsColorsFragment();
            Bundle arguments = new Bundle();
            arguments.putString("newspaper", "diariolibre");
            fragment.setArguments(arguments);
            transaction.replace(R.id.content_fragment, fragment);
            transaction.commit();
        }

        EventBus.getDefault().register(this);
    }


    public void onEvent(NewsItemSelectedEvent event) {
        Intent intent = new Intent(this, NewsActivity.class);
        intent.putExtra("news", event.getEntry());
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private class NewsPaperSelected implements AdapterView.OnItemSelectedListener {


        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (fragment == null)
                return;
            Bundle arguments = new Bundle();

            switch (i) {
                case 0:
                    arguments.putString("newspaper", "diariolibre");
                    break;
                case 1:
                    arguments.putString("newspaper", "elcaribe");
                    break;
                case 2:
                    arguments.putString("newspaper", "listindiario");
                    break;
                case 3:
                    arguments.putString("newspaper", "celennyurena");
                    break;
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            fragment = new SlidingTabsColorsFragment();
            fragment.setArguments(arguments);
            transaction.replace(R.id.content_fragment, fragment);
            transaction.commit();

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}
