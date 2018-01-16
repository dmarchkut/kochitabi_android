package jp.dmarch.kochitabi;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class TestActivity extends AppCompatActivity {

    DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        dataBaseHelper = new DataBaseHelper(this);

        /*ServerExchange serverExchange = new ServerExchange();
        String text = serverExchange.getLocalDataBaseTables().toString();
        text = text.replace("{", "{\n");
        text = text.replace(",", ",\n");
        text = text.replace("}","}\n");
        Log.d("Test", text);*/
    }
}
