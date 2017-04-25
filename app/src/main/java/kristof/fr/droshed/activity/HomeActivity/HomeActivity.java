package kristof.fr.droshed.activity.HomeActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import kristof.fr.droshed.Explorer.ItemExplorer;
import kristof.fr.droshed.JsonUtil;
import kristof.fr.droshed.R;
import kristof.fr.droshed.ServerInfo;
import kristof.fr.droshed.custom.FontCache;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private ServerInfo serverInfo;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private ArrayList<ItemExplorer> listData = new ArrayList<>();
    private ArrayList<ItemExplorer> listModel = new ArrayList<>();
    private HashMap<String,CustomFragment> hashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        setToolbar();
        drawer = (DrawerLayout) findViewById(R.id.homeDrawerLayout);
        navigationView = (NavigationView) findViewById(R.id.nvView);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        drawer.addDrawerListener(toggle);
        navigationView.setNavigationItemSelectedListener(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view ->
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
        );

        Intent intent = getIntent();
        if (intent != null) {
            serverInfo = intent.getParcelableExtra("serverInfo");
        }

        if (findViewById(R.id.flContent) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {

                return;
            }
        }
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        hashMap.put("/data",createNewFragment(listData,0));
        hashMap.put("/model",createNewFragment(listModel,0));
        fragmentTransaction.add(R.id.flContent,hashMap.get("/data"),"/data").commit();
        refreshListData();
    }

    private CustomFragment createNewFragment(ArrayList<ItemExplorer> list,int idFragment) {
        CustomFragment firstFragment = new CustomFragment();
        Bundle args = new Bundle();
        args.putInt("id",idFragment);
        args.putParcelableArrayList("list", list);
        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        firstFragment.setArguments(args);
        return firstFragment;
    }

    private void refreshListData() {
        refreshListFolder("/data");
    }

    private void refreshListModel() {
        refreshListFolder("/model");
    }

    private void refreshListFolder(String folderPath) {
        URL url;
        try {
            url = new URL(serverInfo + folderPath);
            new CustomAsyncTask(folderPath).execute(url);
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(drawer, "Erreur de connexion", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.homeToolbar);
        toolbar.setTitle(getString(R.string.title_home_activity));
        setToolbarFont(toolbar);
        setSupportActionBar(toolbar);
    }

    private void setToolbarFont(Toolbar toolbar) {
        Typeface font = FontCache.getFont(this, getString(R.string.font));
        for (int i = 0; i <= toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                textView.setTypeface(font);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.refresh:
                if(hashMap.get("/data").isAdded())
                    refreshListData();
                if(hashMap.get("/model").isAdded())
                    refreshListModel();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        //CLEAR FRAGMENT STACK
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if (id == R.id.nav_data) {
            CustomFragment customFragment = hashMap.get("/data");
            fragmentTransaction.replace(R.id.flContent,customFragment,"/data").commit();
            if(!hashMap.get("/data").isAdded()) {
                refreshListData();
            }
        } else if (id == R.id.nav_model) {
            CustomFragment customFragment = hashMap.get("/model");
            fragmentTransaction.replace(R.id.flContent,customFragment,"/model").commit();
            if(!hashMap.get("/model").isAdded()){
                refreshListModel();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.homeDrawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class CustomAsyncTask extends AsyncTask<URL, Void, ArrayList<ItemExplorer>> {

        private String tag;

        CustomAsyncTask(String tag){
            this.tag = tag;
        }

        @Override
        protected ArrayList<ItemExplorer> doInBackground(URL... params) {
            for (URL url : params) {
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(3000); //set timeout to 3 seconds
                    urlConnection.setReadTimeout(3000);
                    urlConnection.setRequestProperty("Authorization", serverInfo.getAuthBase64());
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    // Starts the query
                    urlConnection.connect();
                    if (urlConnection.getResponseCode() == 200) {
                        return JsonUtil.toListofCustomItem(
                                getStringFromInputStream(urlConnection.getInputStream())
                        );
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ItemExplorer> s) {
            super.onPostExecute(s);
            hashMap.get(tag).updateGridViewList(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private static String getStringFromInputStream(InputStream inputStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuilder sb = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            sb.append(inputLine);
        in.close();
        return sb.toString();
    }
}
