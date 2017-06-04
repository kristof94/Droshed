package kristof.fr.droshed.activity.ModelActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import kristof.fr.droshed.Explorer.FileItemExplorer;
import kristof.fr.droshed.JsonUtil;
import kristof.fr.droshed.R;
import kristof.fr.droshed.ServerInfo;
import kristof.fr.droshed.Util;
import kristof.fr.droshed.XmlUtil;
import kristof.fr.droshed.gridobject.Column;
import kristof.fr.droshed.gridobject.Grid;
import kristof.fr.droshed.gridobject.Row;
import kristof.fr.droshed.gridobject.RowValue;

public class ModelActivity extends AppCompatActivity {

    public static final String EMPTYTAG = "empty___";
    private ProgressBar progressBar;
    private View drawer;
    private TableLayout tableLayout;
    private EditText titleEditText;
    private LinearLayout linearLayout;
    private ServerInfo serverInfo;
    private FileItemExplorer fileItemExplorer;
    private boolean isNewFile;
    private Grid gridData;
    private String oldContent;
    private boolean isErrorPresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayoutId);
        titleEditText = (EditText) findViewById(R.id.titleTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tableLayout = (TableLayout) findViewById(R.id.tableau);
        drawer = findViewById(R.id.drawer);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                if (bundle.containsKey("serverInfo")) {
                    serverInfo = bundle.getParcelable("serverInfo");
                    isNewFile = bundle.getBoolean("isNewFile");
                    fileItemExplorer = bundle.getParcelable("fileItemExplorer");
                    downloadXMLAndParseFile(serverInfo + "/" + fileItemExplorer.getPath());
                }
            }
        }
    }

    private AlertDialog createInputTextBox() {
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
        builder.setCancelable(true);
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

    public static String printXmlDocument(Document document) throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
        return output;
    }

    private String createXML(Grid grid) {
        Document document = grid.getDocument();
        Element rootElement = document.getDocumentElement();
        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            //create ROWS ARRAY

            Element elementDoc = doc.createElement("document");
            doc.appendChild(elementDoc);
            //elementDoc = rootElement;

            Element elementPath = doc.createElement("path");
            elementDoc.appendChild(elementPath);

            String path = rootElement.getElementsByTagName("path").item(0).getTextContent();
            elementPath.setTextContent(path);

            Element elementVersion = doc.createElement("version");
            elementDoc.appendChild(elementVersion);

            String version = rootElement.getElementsByTagName("version").item(0).getTextContent();
            elementVersion.setTextContent(version);

            Element elementName = doc.createElement("name");
            elementDoc.appendChild(elementName);

            String name = rootElement.getElementsByTagName("name").item(0).getTextContent();
            elementName.setTextContent(grid.getTitle());

            Element elementRows = doc.createElement("rows");
            elementDoc.appendChild(elementRows);

            ArrayList<Row> listRow = getRowsModel(rootElement, "rows");

            for (Row row : listRow) {
                Element elementRow = doc.createElement("row");
                elementRows.appendChild(elementRow);

                Attr attribute = doc.createAttribute("name");
                attribute.setValue(row.getName());

                Attr attributeType = doc.createAttribute("type");
                attributeType.setValue(row.getType());

                elementRow.setAttributeNode(attribute);
                elementRow.setAttributeNode(attributeType);
            }

            Element rootRows = doc.createElement("columns");
            elementDoc.appendChild(rootRows);
            for (int i = 0; i < tableLayout.getChildCount(); i++) {
                View view = tableLayout.getChildAt(i);
                //create A ROW WITH INDEX

                if (view instanceof TableRow) {
                    Element row = doc.createElement("row");
                    rootRows.appendChild(row);
                    Attr attribute = doc.createAttribute("index");
                    attribute.setValue(String.valueOf(i));
                    row.setAttributeNode(attribute);

                    TableRow tableRow = (TableRow) view;

                    for (int u = 0; u < tableRow.getChildCount(); u++) {
                        View editView = tableRow.getChildAt(u);
                        if (editView instanceof EditText) {
                            EditText editText = (EditText) editView;
                            String value = editText.getText().toString();
                            Element column = doc.createElement("column");
                            row.appendChild(column);
                            Attr attribute2 = doc.createAttribute("index");
                            attribute2.setValue(String.valueOf(u));
                            column.setAttributeNode(attribute2);
                            column.setTextContent(value);
                        }
                    }
                }
            }
            return printXmlDocument(doc);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        String title = titleEditText.getText().toString();
        gridData.setTitle(title);
        String content = createXML(gridData);
        if ((TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) || (title.equals(fileItemExplorer.getName()) && content.equals(oldContent))) {
            setResult(RESULT_CANCELED);
            finish();
        }
        if (TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
            createAlertBow().show();
        } else {
            URL url = null;
            fileItemExplorer.setPath("datasheet/" + title);
            fileItemExplorer.setName(title);
            try {
                url = new URL(serverInfo + "/" + fileItemExplorer.getPath());
                new UploadCustomAsyncTask(content).execute(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
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
                displayProgressBar(false);
                code = RESULT_OK;
                Bundle bundle = new Bundle();
                bundle.putParcelable("fileItemExplorer", fileItemExplorer);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(code, intent);
                finish();
            } else {
                setResult(code);
                finish();
            }
            displayProgressBar(false);
            setResult(code);

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

    private void fillTableauWithGrid(Grid grid) throws JSONException {
        titleEditText.setText(grid.getTitle());
        int index = 0;
        for (Row row : grid.getRows()) {
            TextView textView = new TextView(this);
            textView.setText(row.getName());
            TableRow tableRow = new TableRow(this);
            tableRow.setBackgroundResource(R.drawable.border);
            tableRow.addView(textView);

            for (int i = 0; i < 40; i++) {
                EditText editText = new EditText(this);
                editText.setBackgroundResource(R.drawable.border);
                if (row.getType().equals("int")) {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.setGravity(Gravity.CENTER);
                    editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            EditText text = (EditText) v;
                            if (!hasFocus && !TextUtils.isEmpty(text.getText().toString())) {
                                Integer note = Integer.parseInt(text.getText().toString());
                                if (note < 0 || note > 20) {
                                    text.setError("Invalid number");
                                }
                            }
                        }
                    });
                }
                if (row.getType().equals("float") && !row.getName().equals("note finale")) {
                    editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    editText.setGravity(Gravity.CENTER);
                    editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            EditText text = (EditText) v;
                            if (!hasFocus && !TextUtils.isEmpty(text.getText().toString())) {
                                try {
                                    Float note = Float.parseFloat(text.getText().toString());
                                    if (note < 0 || note > 1) {
                                        text.setError("Invalid number");
                                    }
                                } catch (Exception e) {
                                    text.setError("Invalid number");
                                }
                            }
                        }
                    });
                }
                if (row.getType().equals("float") && row.getName().equals("note finale")) {
                    editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    editText.setGravity(Gravity.CENTER);
                    editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            EditText text = (EditText) v;
                            if (!hasFocus && !TextUtils.isEmpty(text.getText().toString())) {
                                try {
                                    Float note = Float.parseFloat(text.getText().toString());
                                    if (note < 0 || note > 20) {
                                        text.setError("Invalid number");
                                    }
                                } catch (Exception e) {
                                    text.setError("Invalid number");
                                }
                            }
                        }
                    });
                }
                if (row.getType().equals("string")) {
                    editText.setSingleLine(false);
                    editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                }
                editText.setWidth(200);
                tableRow.addView(editText);
            }

            tableLayout.addView(tableRow);
            index++;
        }

        int size = grid.getRowValues().size();
        for (int i = 0; i < size; i++) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
            RowValue rowValue = grid.getRowValues().get(i);
            for (Column column : rowValue.getArrayList()) {
                View view = tableRow.getChildAt(column.getIndex());
                if (view instanceof EditText) {
                    EditText editText = (EditText) view;
                    editText.setText(column.getValue());
                }
            }
        }
    }

    private ArrayList<Row> getRowsModel(Element rootElement, String rowTag) {
        NodeList rowsRoot = rootElement.getElementsByTagName(rowTag);
        NodeList rows = rowsRoot.item(0).getChildNodes();
        final int nbRacineNoeuds = rows.getLength();
        ArrayList<Row> rowArrayList = new ArrayList<>();
        for (int i = 0; i < nbRacineNoeuds; i++) {
            Node item = rows.item(i);
            String nodeName = item.getNodeName();
            if (nodeName.equals("row")) {
                String nom = item.getAttributes().getNamedItem("name").getNodeValue();
                String type = item.getAttributes().getNamedItem("type").getNodeValue();
                String value = item.getNodeValue();
                rowArrayList.add(new Row(nom, type, value));
            }
        }
        return rowArrayList;
    }

    private ArrayList<RowValue> getRowsValue(Element rootElement, String rowTag) {
        ArrayList<RowValue> rowArrayList = new ArrayList<>();
        NodeList rowsRoot = rootElement.getElementsByTagName(rowTag);
        if (rowsRoot == null || rowsRoot.getLength() == 0) {
            return rowArrayList;
        }
        NodeList rows = rowsRoot.item(0).getChildNodes();
        if (rows == null) {
            return rowArrayList;
        }
        final int nbRacineNoeuds = rows.getLength();
        System.out.println("nbRacineNoeuds " + nbRacineNoeuds);
        for (int i = 0; i < nbRacineNoeuds; i++) {
            Node item = rows.item(i);
            String nodeName = item.getNodeName();
            if (nodeName.equals("row")) {
                int index = Integer.parseInt(item.getAttributes().getNamedItem("index").getNodeValue());
                if (item instanceof Element) {
                    Element row = (Element) item;
                    NodeList columns = row.getElementsByTagName("column");
                    ArrayList<Column> arrayList = new ArrayList<>();
                    int columnsRootCount = columns.getLength();
                    for (int j = 0; j < columnsRootCount; j++) {
                        Node column = columns.item(j);
                        if (column instanceof Element) {
                            Element element = (Element) column;
                            int indexColumn = Integer.parseInt(element.getAttributes().getNamedItem("index").getNodeValue());
                            String value = element.getTextContent();
                            arrayList.add(new Column(value, indexColumn));
                        }
                    }
                    rowArrayList.add(new RowValue(index, arrayList));
                }
            }
        }
        return rowArrayList;
    }

    private class DownloadCustomAsyncTask extends AsyncTask<URL, Void, Grid> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            displayProgressBar(true);
        }

        @Override
        protected Grid doInBackground(URL... params) {
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
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == 200) {
                        String xml = Util.getStringFromInputStream(urlConnection.getInputStream());
                        Document document = XmlUtil.getDomElement(xml);
                        Element rootElement = document.getDocumentElement();
                        NodeList nodeList = rootElement.getElementsByTagName("name");
                        String title = "";
                        if (!isNewFile) {
                            title = nodeList.item(0).getTextContent();
                        }
                        return new Grid(getRowsModel(rootElement, "rows"), title, getRowsValue(rootElement, "columns"), document);
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
        protected void onPostExecute(Grid grid) {
            super.onPostExecute(grid);
            if (grid != null) {
                try {
                    fillTableauWithGrid(grid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                gridData = grid;
            }
            displayProgressBar(false);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            displayProgressBar(false);
        }
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
            new DownloadCustomAsyncTask().execute(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
