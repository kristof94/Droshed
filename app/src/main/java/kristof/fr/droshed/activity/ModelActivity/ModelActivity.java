package kristof.fr.droshed.activity.ModelActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import kristof.fr.droshed.Explorer.FileItemExplorer;
import kristof.fr.droshed.Explorer.FolderItemExplorer;
import kristof.fr.droshed.JsonUtil;
import kristof.fr.droshed.R;
import kristof.fr.droshed.ServerInfo;
import kristof.fr.droshed.Util;

public class ModelActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private View drawer;
    private EditText editText;
    private EditText titleEditText;
    private TextView versionTextView;
    private LinearLayout linearLayout;
    private ServerInfo serverInfo;
    private FileItemExplorer fileItemExplorer;
    private boolean isNewFile;
    private FolderItemExplorer currentFolderItemExplorer;
    private String oldContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayoutId);
        editText = (EditText) findViewById(R.id.contentTextView);
        titleEditText = (EditText) findViewById(R.id.titleTextView);
        versionTextView = (TextView) findViewById(R.id.versionTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        drawer = findViewById(R.id.drawer);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                if (bundle.containsKey("serverInfo")) {
                    serverInfo = bundle.getParcelable("serverInfo");
                    isNewFile = bundle.getBoolean("isNewFile");
                    fileItemExplorer = bundle.getParcelable("fileItemExplorer");
                    if (isNewFile) {
                        titleEditText.setHint("New file");
                        currentFolderItemExplorer = bundle.getParcelable("currentFolderItemExplorer");
                        System.out.println("New file "+currentFolderItemExplorer.getPath());
                        versionTextView.setText("Version "+0);
                    } else {
                        File fileOnDevice = new File(fileItemExplorer.getPath());
                        if (fileOnDevice.exists()) {
                            new CustomReadFileAsyncTask().execute(fileOnDevice);
                        } else if (serverInfo != null) {
                            downloadXMLAndParseFile(serverInfo + "/" + fileItemExplorer.getPath());
                        }
                        titleEditText.setText(fileItemExplorer.getName());
                        versionTextView.setText("Version "+fileItemExplorer.getVersion());
                    }
                }
            }
        }
    }

    private AlertDialog createInputTextBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.textbox_title, (ViewGroup) linearLayout, false);
        // Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                titleEditText.setText(input.getText().toString());
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.create();
    }

    private AlertDialog createAlertBow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Save document ?");
        builder.setMessage("Save changes to document before closing?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                createInputTextBox().show();
                dialog.dismiss();

            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
                return;
            }
        });
        return builder.create();
    }

    @Override
    public void onBackPressed() {
        String title = titleEditText.getText().toString();
        String content = editText.getText().toString();

        if ((TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) || (title.equals(fileItemExplorer.getName()) && content.equals(oldContent))) {
            setResult(RESULT_CANCELED);
            finish();
        }

        if(TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)){
            createAlertBow().show();
        }else {

            if (isNewFile) {
                String path = currentFolderItemExplorer.getPath() + "/" + title;
                System.out.println(path);
                fileItemExplorer = new FileItemExplorer(fileItemExplorer.getType(), title, fileItemExplorer.getLayoutID(), path, 1);
                new WriteFileAsyncTask(content).execute(fileItemExplorer);
            }

            if (!isNewFile && title.equals(fileItemExplorer.getName()) && !content.equals(oldContent)) {
                fileItemExplorer.setVersion(fileItemExplorer.getVersion() + 1);
                new WriteFileAsyncTask(content).execute(fileItemExplorer);
            }

            if (!isNewFile && !title.equals(fileItemExplorer.getName())) {
                String path = fileItemExplorer.getPath().substring(0, fileItemExplorer.getPath().length() - fileItemExplorer.getName().length()) + title;
                fileItemExplorer = new FileItemExplorer(fileItemExplorer.getType(), title, fileItemExplorer.getLayoutID(), path, fileItemExplorer.getVersion() + 1);
                new WriteFileAsyncTask(content).execute(fileItemExplorer);
            }
        }

        /*
        FileItemExplorer item = fileItemExplorer;
        //if title is not empty
        if(!TextUtils.isEmpty(title)){
            String path = null;
            if(currentFolderItemExplorer!=null){
                path = currentFolderItemExplorer.getPath() + "/" + title;
            }
            //if title is different than old title then new file
            if(!isNewFile && !title.equals(item.getName())){
                isNewFile = true;
                path  = item.getPath().substring(0, item.getPath().length() - item.getName().length()) + title;
            }
            //if newFile
            if (isNewFile) {

                item = new FileItemExplorer(fileItemExplorer.getType(), title, fileItemExplorer.getLayoutID(), path, 1);
                System.out.println("Write new File : "+item.getPath());
                //TODO
                /*
                * WRITE FILE
                * UPLOAD FILE
                * FINISH
                *
                new WriteFileAsyncTask(content).execute(item);
            }
            //if not a new file
            else{
                if(TextUtils.isEmpty(content)){
                    super.onBackPressed();
                }
                //if content is modified
                if(!oldContent.equals(content)){
                    item.setVersion(fileItemExplorer.getVersion() + 1);
                    new WriteFileAsyncTask(content).execute(item);
                    //TODO
                    /*
                    WRITE FILE
                * GET VERSION AND
                * UPLOAD FILE
                * IF SERVER VERSION IS OLDER THAN CLIENT VERSION
                * FINISH
                *

                }else{
                    super.onBackPressed();
                }
            }

        }*/

    }

    private class WriteFileAsyncTask extends AsyncTask<FileItemExplorer, Void, FileItemExplorer> {

        private String content;

        WriteFileAsyncTask(String content) {
            this.content = content;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            displayProgressBar(true);
        }

        @Override
        protected FileItemExplorer doInBackground(FileItemExplorer... params) {
            for (FileItemExplorer fileItemExplorer : params) {
                try {
                    JSONObject jsonObject = JsonUtil.createJsonFileUploadString(fileItemExplorer, content);
                    saveContentInFile(fileItemExplorer.getPath(), jsonObject.toString());
                    return fileItemExplorer;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(FileItemExplorer fileItemExplorer) {
            super.onPostExecute(fileItemExplorer);
            int code;
            if (fileItemExplorer == null) {
                code = RESULT_CANCELED;
                setResult(code);
            } else {
                code = RESULT_OK;
                Bundle bundle = new Bundle();
                bundle.putParcelable("fileItemExplorer", fileItemExplorer);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(code, intent);
            }
            finish();
        }
    }

    private class UploadCustomAsyncTask extends AsyncTask<URL, Integer, Boolean> {

        String content;

        public UploadCustomAsyncTask(String content) {
            this.content = content;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            displayProgressBar(true);
        }

        @Override
        protected Boolean doInBackground(URL... params) {
            for (URL url : params) {
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(5000); //set timeout to 5 seconds
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setRequestProperty("Authorization", serverInfo.getAuthBase64());
                    urlConnection.setRequestMethod("PUT");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setDoInput(false);
                    urlConnection.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(
                            urlConnection.getOutputStream());
                    wr.writeBytes(content);
                    wr.flush();
                    wr.close();
                    // Starts the query
                    urlConnection.connect();
                    if (urlConnection.getResponseCode() == 200) {
                        return true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            int code = RESULT_CANCELED;
            if (s) {
                System.out.println("doubleok");
                displayProgressBar(false);
                code = RESULT_OK;
            }
            displayProgressBar(false);
            setResult(code);
            finish();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            displayProgressBar(false);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    private class GetVersionCustomAsyncTask extends AsyncTask<URL, Void, String> {

        private String content;

        public GetVersionCustomAsyncTask(String content) {
            this.content = content;
        }

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
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == 200) {
                        return Util.getStringFromInputStream(urlConnection.getInputStream());
                    } else {
                        Snackbar.make(drawer, "Erreur de connexion : " + responseCode, Snackbar.LENGTH_SHORT).show();
                        return null;
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
            } else {
                try {
                    JSONObject obj = new JSONObject(s);
                    int serverVersionFile = obj.getInt("version");
                    if (serverVersionFile < fileItemExplorer.getVersion()) {
                        uploadSpreadSheet(fileItemExplorer, content);
                    }
                } catch (JSONException e) {
                    setResult(RESULT_CANCELED);
                    finish();
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
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == 200) {
                        return Util.getStringFromInputStream(urlConnection.getInputStream());
                    } else {
                        Snackbar.make(drawer, "Erreur de connexion : " + responseCode, Snackbar.LENGTH_SHORT).show();
                        return null;
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

            } else {
                try {
                    JSONObject obj = new JSONObject(s);
                    String content = JsonUtil.getContent(obj);
                    editText.setText(content);
                    oldContent = content;
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(drawer, "Erreur lors de la lecture du json.", Snackbar.LENGTH_SHORT).show();
                }

                /*try {
                    getTempFile(fileItemExplorer,s);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
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
            for (File file : params) {
                try {
                    String contentFile = readFile(file);
                    return contentFile;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null) {
                Snackbar.make(drawer, "Erreur lors de la lecture du fichier", Snackbar.LENGTH_SHORT).show();
            } else {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    String content = jsonObject.getString("content");
                    editText.setText(content);
                    oldContent = content;
                    versionTextView.setText("Version "+jsonObject.getInt("version"));
                } catch (JSONException e) {
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

    public void saveContentInFile(String path, String content) throws IOException {
        File file = new File(path);
        File directory = new File(file.getParent());
        if (!directory.exists()){
            directory.mkdirs();
        }
        FileOutputStream fOut = new FileOutputStream(file);
        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
        myOutWriter.write(content);
        myOutWriter.close();
        fOut.flush();
        fOut.close();
    }

    public static String readFile(File tempFile) throws IOException {
        /** Getting a reference to temporary file, if created earlier */
        StringBuilder sb = new StringBuilder();
        FileReader fReader = null;
        BufferedReader bufferedReader = null;
        try {
            fReader = new FileReader(tempFile);
            bufferedReader = new BufferedReader(fReader);
            String strLine = null;
            /** Reading the contents of the file , line by line */
            while ((strLine = bufferedReader.readLine()) != null) {
                sb.append(strLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (fReader != null) {
                fReader.close();
            }
        }
        return sb.toString();
    }

    private void displayProgressBar(boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        linearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        linearLayout.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                linearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
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

    private void uploadSpreadSheet(FileItemExplorer fileItemExplorer, String content) {
        URL url;
        try {
            url = new URL(serverInfo + "/data");
            new UploadCustomAsyncTask(JsonUtil.createJsonFileUploadString(fileItemExplorer, content).toString()).execute(url);
        } catch (Exception e) {
            e.printStackTrace();
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
}
