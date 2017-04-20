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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

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
    private FloatingActionButton fab;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private ArrayList<CustomItem> datalist = new ArrayList<>();
    private ArrayList<CustomItem> modelist = new ArrayList<>();
    private boolean isModelView;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        serverInfo.setDatalist(datalist);
        serverInfo.setModelist(modelist);
        outState.putBoolean("isModelView", isModelView);
        outState.putParcelable("serverInfo", serverInfo);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();
        initUiElements();

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        // TODO
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState != null) {
            serverInfo = (ServerInfo) savedInstanceState.get("serverInfo");
            isModelView = savedInstanceState.getBoolean("isModelView");
        } else {
            Intent intent = getIntent();
            if (intent != null) {
                serverInfo = intent.getParcelableExtra("serverInfo");
            }
        }
        if (isModelView) {
            refreshModelFromServer();
        } else {
            refreshDataFromServer();
        }
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.home));
        setToolbarFont(toolbar);
        setSupportActionBar(toolbar);
    }

    private void initUiElements() {
        parentLayout = findViewById(R.id.home);
        gridView = (GridView) findViewById(R.id.gridViewHome);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    private void refreshDataFromServer() {
        UserClientTask userClientTask = new UserClientTask();
        URL url = null;
        try {
            //TODO
            url = new URL(serverInfo + "/data");
            datalist = userClientTask.execute(url).get();
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
            datalist = userClientTask.execute(url).get();
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
            } else {
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

    private class UserClientTask extends AsyncTask<URL, String, ArrayList<CustomItem>> {
        @Override
        protected void onPreExecute() {
            gridView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected ArrayList<CustomItem> doInBackground(URL... params) {
            for (URL url : params) {
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(3000); //set timeout to 3 seconds
                    urlConnection.setReadTimeout(3000);
                    urlConnection.setRequestProperty("Authorization", serverInfo.getAuthBase64());
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    urlConnection.connect();
                    if (urlConnection.getResponseCode() != 200) {
                        //error
                        return null;
                    }
                    // Starts the query
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream()));
                    String streamFromBufferReader = readStream(in);
                    in.close();
                    return JsonUtil.toObject(streamFromBufferReader);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final ArrayList<CustomItem> result) {
            progressBar.setVisibility(View.INVISIBLE);
            gridView.setVisibility(View.VISIBLE);
            if (result.isEmpty()) {
                runOnUiThread(() -> Snackbar.make(parentLayout, getString(R.string.alertConnexionProblem), Snackbar.LENGTH_SHORT).show());
            } else {
                runOnUiThread(() -> Snackbar.make(parentLayout, "Connexion OK!", Snackbar.LENGTH_SHORT).show());
            }
        }

        @Override
        protected void onCancelled() {
            gridView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private String readStream(BufferedReader in) throws IOException {
        StringBuilder sb = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            sb.append(inputLine);
        return sb.toString();
    }

}