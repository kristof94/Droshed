package kristof.fr.droshed.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import kristof.fr.droshed.R;
import kristof.fr.droshed.ServerInfo;

/**
 * A login screen that offers login via root/password.
 */
public class LoginActivity extends AppCompatActivity {

    private AutoCompleteTextView userView;
    private EditText passwordView;
    private EditText urlView;
    private View progressView;
    private View loginView;
    private View parentView;
    private CheckBox checkBox;
    private SharedPreferences sharedPreferences;
    private UserLoginTask mAuthTask;


    /**
     * Main method to init all Graphic element like button, text view.
     * Also use to retrieve saved data if saved data were saved.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUiElements();
        if (sharedPreferences.getAll().size() == 3) {
            retrieveSavedData();
        }
    }

    public void onClickSignInButton(View v) {
        attemptLogin();
    }

    private void retrieveSavedData() {
        String url = sharedPreferences.getString(getString(R.string.url), null);
        String user = sharedPreferences.getString(getString(R.string.username), null);
        String password = sharedPreferences.getString(getString(R.string.password), null);
        urlView.setText(url);
        userView.setText(user);
        passwordView.setText(password);
        checkBox.setChecked(true);
    }

    private void initUiElements() {
        parentView = findViewById(R.id.parentLayout);
        userView = (AutoCompleteTextView) findViewById(R.id.user);
        urlView = (EditText) findViewById(R.id.server);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        passwordView = (EditText) findViewById(R.id.password);
        loginView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);
        sharedPreferences = getSharedPreferences(getString(R.string.loginKey), 0);
    }

    private void saveData(String url, String user, String password) {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(getString(R.string.url), url);
        ed.putString(getString(R.string.username), user);
        ed.putString(getString(R.string.password), password);
        ed.apply();
    }

    private void startMainActivityAndExitLoginActivity(ServerInfo serverInfo) {
        Intent mainIntent = new Intent(LoginActivity.this, HomeActivity.class);
        mainIntent.putExtra("serverInfo", serverInfo);
        startActivity(mainIntent);
        finish();
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        userView.setError(null);
        passwordView.setError(null);
        urlView.setError(null);
        // Store values at the time of the login attempt.
        String ipServer = urlView.getText().toString();
        String user = userView.getText().toString();
        String password = passwordView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        // Check for a valid user.
        if (TextUtils.isEmpty(user)) {
            userView.setError(getString(R.string.error_field_required));
            focusView = userView;
            cancel = true;
        }

        if (TextUtils.isEmpty(ipServer)) {
            urlView.setError(getString(R.string.error_field_required));
            focusView = urlView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            manageSignIn(user, password);
        }
    }

    private void manageSignIn(String user, String password) {
        String urlServer = urlView.getText().toString();
        String urlLogin = urlServer + getString(R.string.loginPath);
        String credentials = user + ":" + password;
        String authBase64 = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
        boolean isAuthenticate = launchAuthentication(urlLogin, authBase64);
        if (isAuthenticate) {
            if (checkBox.isChecked()) {
                saveData(urlServer, user, password);
            }
            ServerInfo serverInfo = new ServerInfo(urlServer, authBase64);
            startMainActivityAndExitLoginActivity(serverInfo);
        } else {
            Snackbar.make(parentView, getString(R.string.alertConnexionProblem), Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean launchAuthentication(String urlStr, String authBase64) {
        mAuthTask = new UserLoginTask(authBase64);
        URL url;
        try {
            url = new URL(urlStr);
            return mAuthTask.execute(url).get();
        } catch (MalformedURLException | InterruptedException | ExecutionException e) {
            mAuthTask.cancel(true);
            return false;
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     * From here, this is not my code , this code is auto generate
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            loginView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /*
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
        userView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };
        int ADDRESS = 0;
    }*/

    /**
     To here
     */

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserLoginTask extends AsyncTask<URL, Void, Boolean> {

        private final String authBase64;

        UserLoginTask(String authBase64) {
            this.authBase64 = authBase64;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(URL... params) {
            for (URL url : params) {
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(3000); //set timeout to 3 seconds
                    urlConnection.setReadTimeout(3000);
                    urlConnection.setRequestProperty("Authorization", authBase64);
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    // Starts the query
                    urlConnection.connect();
                    int response = urlConnection.getResponseCode();
                    return (response == 200);
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
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}

