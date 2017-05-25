package kristof.fr.droshed.activity.ModelActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import kristof.fr.droshed.R;
import kristof.fr.droshed.ServerInfo;
import kristof.fr.droshed.Util;

public class ModelActivity extends AppCompatActivity {

    private GridView gridView;
    private ProgressBar progressBar;
    private ServerInfo serverInfo;
    private String path;
    private View drawer;
    private android.support.v7.widget.Toolbar toolbar;
    private String fileItemExplorer;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model);
        gridView = (GridView) findViewById(R.id.gridViewModel);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        drawer = findViewById(R.id.drawer);
        textView = (TextView) findViewById(R.id.textView);
        Intent intent = getIntent();

        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                if (bundle.containsKey("serverInfo")) {
                    serverInfo = bundle.getParcelable("serverInfo");
                    path = bundle.getString("path");
                    fileItemExplorer = bundle.getString("fileItemExplorer");
                    File cDir = getBaseContext().getCacheDir();
                    File tempFile = new File(cDir.getPath() + "/" + fileItemExplorer);
                    if(tempFile.exists()){
                        new CustomReadFileAsyncTask().execute(tempFile);
                    }
                    else if (serverInfo != null && path != null) {
                        downloadXMLAndParseFile(serverInfo + path);
                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void downloadXMLAndParseFile(String urlPath) {
        URL url;
        try {
            url = new URL(urlPath);
            new CustomAsyncTask().execute(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayProgressBar(boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            gridView.setVisibility(show ? View.GONE : View.VISIBLE);
            gridView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    gridView.setVisibility(show ? View.GONE : View.VISIBLE);
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
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            gridView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    private class CustomAsyncTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            displayProgressBar(true);
        }

        @Override
        protected String doInBackground(URL... params) {
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
                        return Util.getStringFromInputStream(urlConnection.getInputStream());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null) {
                Snackbar.make(drawer, "Erreur de connexion", Snackbar.LENGTH_SHORT).show();
            } else {
                textView.setText(s);
                try {
                    getTempFile(fileItemExplorer,s);
                } catch (IOException e) {
                    e.printStackTrace();
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

    private class CustomReadFileAsyncTask extends AsyncTask<File, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            displayProgressBar(true);
        }

        @Override
        protected String doInBackground(File... params) {
            for(File file : params) {
                try {
                    return readFile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return  null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null) {
                Snackbar.make(drawer, "Erreur de connexion", Snackbar.LENGTH_SHORT).show();
            } else {
                textView.setText(s);
                try {
                    getTempFile(fileItemExplorer,s);
                } catch (IOException e) {
                    e.printStackTrace();
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


    public void getTempFile(String fileItemExplorer,String content) throws IOException {
        File cDir = getBaseContext().getCacheDir();
        /** Getting a reference to temporary file, if created earlier */
        File tempFile = new File(cDir.getPath() + "/" + fileItemExplorer);
        tempFile.createNewFile();
        FileOutputStream fOut = new FileOutputStream(tempFile);
        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
        myOutWriter.write(content);
        myOutWriter.close();
        fOut.flush();
        fOut.close();
    }

    public String readFile(File tempFile) throws IOException {
        /** Getting a reference to temporary file, if created earlier */
        StringBuilder sb = new StringBuilder();
        FileReader fReader = null;
        BufferedReader bufferedReader = null;
        try {
            fReader = new FileReader(tempFile);
            bufferedReader = new BufferedReader(fReader);
            String strLine = null;
            /** Reading the contents of the file , line by line */
            while( (strLine=bufferedReader.readLine()) != null  ){
                sb.append(strLine);
            }
        } catch(IOException e){
            e.printStackTrace();
        }finally {
            if(bufferedReader!=null) {
                bufferedReader.close();
            }
            if(fReader!=null){
                fReader.close();
            }
        }
        return sb.toString();
    }

}
