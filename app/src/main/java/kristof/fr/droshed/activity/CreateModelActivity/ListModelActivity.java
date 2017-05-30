package kristof.fr.droshed.activity.CreateModelActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import kristof.fr.droshed.Explorer.FileItemExplorer;
import kristof.fr.droshed.Explorer.FolderItemExplorer;
import kristof.fr.droshed.Explorer.ItemExplorer;
import kristof.fr.droshed.JsonUtil;
import kristof.fr.droshed.R;
import kristof.fr.droshed.ServerInfo;
import kristof.fr.droshed.Util;
import kristof.fr.droshed.activity.HomeActivity.HomeActivity;
import kristof.fr.droshed.activity.ModelActivity.ModelActivity;

public class ListModelActivity extends AppCompatActivity {

    private ServerInfo serverInfo;
    private ListView listView;
    private ProgressBar progressBar;
    private List<FileItemExplorer> list;
    private CustomModelPickerAdapter adapter;
    private final String path = "model";
    private FolderItemExplorer currentFolderItemExplorer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_model);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        listView = (ListView) findViewById(R.id.listViewModel);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileItemExplorer fileItemExplorer = (FileItemExplorer) parent.getItemAtPosition(position);
                startModelActivity(serverInfo,fileItemExplorer);
            }
        });
        Intent intent = getIntent();
        if (intent != null && serverInfo == null) {
            Bundle bundle = intent.getExtras();
            serverInfo = bundle.containsKey("serverInfo") ? bundle.getParcelable("serverInfo") : null;
            currentFolderItemExplorer = bundle.getParcelable("currentFolderItemExplorer");
        }
        list = new ArrayList<>();
        adapter = new CustomModelPickerAdapter(this, 0, list);
        listView.setAdapter(adapter);
        getModelsList(path);
    }

    private void startModelActivity(ServerInfo serverInfo,FileItemExplorer fileItemExplorer) {
        Intent mainIntent = new Intent(ListModelActivity.this, ModelActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("serverInfo", serverInfo);
        bundle.putBoolean("isNewFile",true);
        bundle.putParcelable("fileItemExplorer",fileItemExplorer);
        bundle.putParcelable("currentFolderItemExplorer",currentFolderItemExplorer);
        mainIntent.putExtras(bundle);
        startActivityForResult(mainIntent, HomeActivity.PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == HomeActivity.PICK_FILE) {
            if(resultCode==RESULT_OK) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    private void getModelsList(String path) {
        URL url;
        try {
            url = new URL(serverInfo + "/"+ path);
            new CustomAsyncTask().execute(url);
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(listView, getString(R.string.error_connexion), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void displayProgressBar(boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        listView.setVisibility(show ? View.GONE : View.VISIBLE);
        listView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                listView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    private class CustomAsyncTask extends AsyncTask<URL, Void, ArrayList<FileItemExplorer>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            displayProgressBar(true);
        }

        @Override
        protected ArrayList<FileItemExplorer> doInBackground(URL... params) {
            for (URL url : params) {
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(3000); //set timeout to 3 seconds
                    urlConnection.setReadTimeout(3000);
                    urlConnection.setRequestProperty("Authorization", serverInfo.getAuthBase64());
                    urlConnection.setDoInput(true);
                    // Starts the query
                    urlConnection.connect();
                    if (urlConnection.getResponseCode() == 200) {
                        ArrayList<ItemExplorer> itemExplorers = JsonUtil.toListofCustomItem(
                                Util.getStringFromInputStream(urlConnection.getInputStream())
                        );
                        ArrayList<FileItemExplorer> modelPickerList = new ArrayList<>();
                        for (ItemExplorer itemExplorer : itemExplorers) {
                            if (itemExplorer instanceof FileItemExplorer) {
                                modelPickerList.add((FileItemExplorer) itemExplorer);
                            }
                        }
                        return modelPickerList;
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
        protected void onPostExecute(ArrayList<FileItemExplorer> s) {
            super.onPostExecute(s);
            if (s == null) {
                System.out.println("iciiiiiiiii");
                displayProgressBar(false);
                setResult(RESULT_CANCELED);
                finish();
            } else {
                list.clear();
                list.addAll(s);
                adapter.notifyDataSetChanged();
                displayProgressBar(false);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            displayProgressBar(false);
        }
    }
}
