package kristof.fr.droshed;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = getIntent();
        String credential = i.getStringExtra("credential");
        String url = i.getStringExtra("urlServer");
        int port = i.getIntExtra("port",0);

        TextView t1 = (TextView) findViewById(R.id.textView2);
        t1.setText(credential);
        TextView t2 = (TextView) findViewById(R.id.textView3);
        t2.setText(url+String.valueOf(port));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        toolbarTitle.setTypeface(typeFace,Typeface.BOLD);
        toolbarTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        /*fab.setOnClickListener( view ->{
           /* Intent mainIntent = new Intent(MainActivity.this, AddModelActivity.class);
            startActivity(mainIntent);
        });*/
    }
}
