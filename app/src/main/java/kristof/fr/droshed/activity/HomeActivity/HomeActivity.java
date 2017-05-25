package kristof.fr.droshed.activity.HomeActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kristof.fr.droshed.Explorer.FileItemExplorer;
import kristof.fr.droshed.Explorer.ItemExplorer;
import kristof.fr.droshed.JsonUtil;
import kristof.fr.droshed.R;
import kristof.fr.droshed.ServerInfo;
import kristof.fr.droshed.Util;
import kristof.fr.droshed.activity.ModelActivity.ModelActivity;
import kristof.fr.droshed.custom.FontCache;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CustomFragment.FolderManager {

    public static final int PICK_FILE = 1;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private ServerInfo serverInfo;
    private ProgressBar progressBar;
    private FrameLayout frameLayout;
    private HashMap<String,CustomFragment> hashMap;
    private  CustomFragment currenFragment;
    private String dataTag = "/data";
    private String modelTag = "/model";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        setToolbar();
        initUiElements();
        hashMap = new HashMap<>();

        //manage rotation
        if (savedInstanceState != null) {
            manageRotation(savedInstanceState);
        } else {
            Intent intent = getIntent();
            if (intent != null && serverInfo == null) {
                Bundle bundle = intent.getExtras();
                serverInfo = bundle.containsKey("serverInfo") ? bundle.getParcelable("serverInfo") : null;
                CustomFragment customFragmentData = CustomFragment.createNewFragment(dataTag);
                CustomFragment customFragmentModel = CustomFragment.createNewFragment(modelTag);
                hashMap.put(dataTag,customFragmentData);
                hashMap.put(modelTag,customFragmentModel);
                getFragmentManager().beginTransaction().add(R.id.flContent,customFragmentData,dataTag).commit();
                refreshListFolder("/data",customFragmentData);
                currenFragment = customFragmentData;
            }
        }
    }

    private void manageRotation(Bundle savedInstanceState) {
        serverInfo = savedInstanceState.containsKey("serverInfo") ? savedInstanceState.getParcelable("serverInfo") : null;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        /*outState.putParcelable("serverInfo", serverInfo);
        hashMap.values().forEach(customFragment -> {
            getFragmentManager().putFragment(outState,customFragment.getTag(),customFragment);
        });
        outState.putString("tagFragment", getFragmentManager().findFragmentById(0).getTag());

        //getFragmentManager().putFragment(outState, fragmentTag, currentFragment);
        /*CustomFragment currentFragment = (CustomFragment)  getFragmentManager().findFragmentByTag(TAG_MY_FRAGMENT);
        if(getFragmentManager().findFragmentByTag(TAG_MY_FRAGMENT)!=null) {
            getFragmentManager().putFragment(outState, TAG_MY_FRAGMENT, currentFragment);
        }*/
        super.onSaveInstanceState(outState);
    }

    private void initUiElements() {
        drawer = (DrawerLayout) findViewById(R.id.homeDrawerLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        frameLayout = (FrameLayout) findViewById(R.id.flContent);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        drawer.addDrawerListener(toggle);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view ->
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
        );
        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void displayProgressBar(boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        frameLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        frameLayout.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                frameLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /*private void refreshListData() {
        refreshListFolder("/data");
    }

    private void refreshListModel() {
        refreshListFolder("/model");
    }*/

    private void refreshListFolder(String folderPath,CustomFragment customFragment) {
        System.out.println("refreshListFolder");
        URL url;
        try {
            url = new URL(serverInfo + folderPath);
            new CustomAsyncTask("GET",customFragment).execute(url);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        //CLEAR FRAGMENT STACK
        getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        if (id == R.id.nav_data) {
            CustomFragment customFragmentData = hashMap.get(dataTag);
            currenFragment = customFragmentData;
            fragmentTransaction.replace(R.id.flContent,currenFragment,dataTag).commit();
        } else if (id == R.id.nav_model) {
            CustomFragment customFragmentModel = hashMap.get(modelTag);
            currenFragment = customFragmentModel;
            fragmentTransaction.replace(R.id.flContent,currenFragment,modelTag).commit();
            if(customFragmentModel.getItemExplorerList()==null || customFragmentModel.getItemExplorerList().isEmpty()){
                refreshListFolder("/model",customFragmentModel);
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
                //currentFragment = (CustomFragment) getFragmentManager().findFragmentByTag(fragmentTag);
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

    @Override
    public void refresh(CustomFragment customFragment) {
        /*
        URL url;
        try {
            url = new URL(serverInfo + customFragment.getPath());
            new CustomAsyncTask("PUT").execute(url);
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(drawer, "Erreur de connexion", Snackbar.LENGTH_SHORT).show();
        }*/
        Toast.makeText(this,"Not implemented",Toast.LENGTH_SHORT);
    }

    @Override
    public void manageItem(String path,FileItemExplorer fileItemExplorer) {
        startModelActivity(serverInfo, path,fileItemExplorer);
    }

    @Override
    public void addFragmentToStack(Bundle args) {
        CustomFragment firstFragment = new CustomFragment();
        firstFragment.setArguments(args);
        String tag = args.getString("path");
        currenFragment = firstFragment;

        if(tag.contains("data")) {
            tag = dataTag;
        }
        if(tag.contains("model")){
            tag = modelTag;
        }
        getFragmentManager().beginTransaction().replace(R.id.flContent, currenFragment, tag).addToBackStack(null).commit();
    }

    private void startModelActivity(ServerInfo serverInfo, String path,FileItemExplorer fileItemExplorer) {
        Intent mainIntent = new Intent(HomeActivity.this, ModelActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("serverInfo", serverInfo);
        bundle.putString("path", path);
        bundle.putString("fileItemExplorer",fileItemExplorer.getName());
        mainIntent.putExtras(bundle);
        startActivityForResult(mainIntent, PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        /*if (requestCode == PICK_FILE) {
            // Make sure the request was successful
            if (resultCode == HomeActivity.PICK_FILE) {
                Bundle bundle = data.getExtras();
                FileItemExplorer fileItemExplorer = bundle.getParcelable("fileItemExplorer");
                List<ItemExplorer> list = currenFragment.getItemExplorerList();
                for(int i = 0; i< list.size();i++){
                    if(list.get(i).equals(fileItemExplorer)){
                        currenFragment.getItemExplorerList().add(i,fileItemExplorer);
                        break;
                    }
                }
            }
        }*/

    }


    private class CustomAsyncTask extends AsyncTask<URL, Void, ArrayList<ItemExplorer>> {
        private String method;
        private CustomFragment customFragment;

        CustomAsyncTask(String method, CustomFragment customFragment) {
            this.method = method;
            this.customFragment = customFragment;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            displayProgressBar(true);
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
                    urlConnection.setRequestMethod(method);
                    urlConnection.setDoInput(true);
                    // Starts the query
                    urlConnection.connect();
                    if (urlConnection.getResponseCode() == 200) {
                        return JsonUtil.toListofCustomItem(
                                Util.getStringFromInputStream(urlConnection.getInputStream())
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
            if (s == null) {
                Snackbar.make(drawer, "Erreur de connexion", Snackbar.LENGTH_SHORT).show();
            } else {
                if (customFragment != null) {
                    customFragment.updateGridViewList(s);

                    if(customFragment.getTag().contains("data")){
                        hashMap.put(dataTag,customFragment);
                        hashMap.put(dataTag,customFragment);
                    }

                    if(customFragment.getTag().contains("model")){
                        hashMap.put(modelTag,customFragment);
                        hashMap.put(modelTag,customFragment);
                    }

                }
            }
            displayProgressBar(false);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            displayProgressBar(false);
        }
    }
}
