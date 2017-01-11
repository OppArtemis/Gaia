package artemis.gaia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.net.Uri;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HelloWorld extends AppCompatActivity {
    String msg = "Android : ";
    EditText searchString;
    TextView viewString;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);
        Log.d(msg, "The onCreate() event");

        searchString = (EditText) findViewById(R.id.txt_searchString);
        viewString = (TextView) findViewById(R.id.txt_view);
    }

    public void pollURL(View view) {
        //  String text = findViewById(R.id.txt_searchString).toString();
        String text = searchString.getText().toString();
        Toast.makeText(HelloWorld.this, text, Toast.LENGTH_SHORT).show();

        viewString.setText(text);
//        findViewById(R.id.txt_verify).set;
    }
}
