package kristof.fr.droshed;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ServerInfo serverInfo;
    private View parentLayout;
    private GridView gridView;
    private ProgressBar progressBar;
    private boolean isModelView;
    private ArrayList<CustomItem> datalist = new ArrayList<>();
    private ArrayList<CustomItem> modelist = new ArrayList<>();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        serverInfo.setDatalist(datalist);
        serverInfo.setModelist(modelist);
        outState.putBoolean("isModelView",isModelView);
        outState.putParcelable("serverInfo",serverInfo);
        super.onSaveInstanceState(outState);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.home));
        setToolbarFont(toolbar);
        setSupportActionBar(toolbar);
        parentLayout = findViewById(R.id.home);
        gridView = (GridView) findViewById(R.id.gridViewHome);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState!=null){
            serverInfo = (ServerInfo) savedInstanceState.get("serverInfo");
            isModelView = savedInstanceState.getBoolean("isModelView");
        }else{
            Intent intent = getIntent();
            if (intent != null) {
                serverInfo = intent.getParcelableExtra("serverInfo");
            }
        }

        if(isModelView){
            refreshModelFromServer();
        }else{
            refreshDataFromServer();
        }
    }

    private void refreshDataFromServer() {
        UserClientTask userClientTask = new UserClientTask();
        URL url = null;
        try {
            //TODO
            url = new URL(serverInfo + "/data");
            String result = userClientTask.execute(url).get();
            datalist = JsonUtil.toObject(result);
            CustomItemAdapter arrayAdapter = new CustomItemAdapter(this, datalist);
            gridView.setAdapter(arrayAdapter);
        } catch (Exception e) {
            Snackbar.make(parentLayout, "Impossible to retrieve data from server.", Snackbar.LENGTH_SHORT).show();
        }
        userClientTask.cancel(true);
    }

    private void refreshModelFromServer() {
        UserClientTask userClientTask = new UserClientTask();
        URL url = null;
        try {
            //TODO
            url = new URL(serverInfo + "/model");
            String result = userClientTask.execute(url).get();
            datalist = JsonUtil.toObject(result);
            CustomItemAdapter arrayAdapter = new CustomItemAdapter(this, datalist);
            gridView.setAdapter(arrayAdapter);
        } catch (Exception e) {
            Snackbar.make(parentLayout, "Impossible to retrieve data from server.", Snackbar.LENGTH_SHORT).show();
        }
        userClientTask.cancel(true);
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {
            if (isModelView) {
                refreshModelFromServer();
            }else{
                refreshDataFromServer();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_data) {
            isModelView = false;
            refreshDataFromServer();
        } else if (id == R.id.nav_model) {
            isModelView = true;
            refreshModelFromServer();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class UserClientTask extends AsyncTask<URL, String, String> {
        @Override
        protected void onPreExecute() {
            gridView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params) {
            for (URL url : params) {
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(3000); //set timeout to 3 seconds
                    urlConnection.setReadTimeout(3000);
                    urlConnection.setRequestProperty("Authorization", serverInfo.getCredentials());
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    // Starts the query
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream()));
                    String streamFromBufferReader = readStream(in);
                    in.close();
                    int response = urlConnection.getResponseCode();
                    Log.d("app", "The response is: " + response);
                    return streamFromBufferReader;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return null;
        }

        private String readStream(BufferedReader in) throws IOException {
            StringBuilder sb = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                sb.append(inputLine);
            return sb.toString();
        }

        @Override
        protected void onPostExecute(final String result) {
            progressBar.setVisibility(View.INVISIBLE);
            gridView.setVisibility(View.VISIBLE);
            if (!result.isEmpty()) {
                runOnUiThread(() -> Snackbar.make(parentLayout, result, Snackbar.LENGTH_SHORT).show());
            } else {
                runOnUiThread(() -> Snackbar.make(parentLayout, getString(R.string.alertConnexionProblem), Snackbar.LENGTH_SHORT).show());
            }
        }

        @Override
        protected void onCancelled() {
            gridView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}
