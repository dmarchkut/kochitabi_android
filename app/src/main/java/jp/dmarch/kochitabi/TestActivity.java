package jp.dmarch.kochitabi;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

public class TestActivity extends AppCompatActivity {

    DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        dataBaseHelper = new DataBaseHelper(this);
        dataBaseHelper.setRegisterData();

        //String text = dataBaseHelper.getSpotText("sp0001");
        //String text = dataBaseHelper.getCharacterData("ac0001").toString();
        String text = dataBaseHelper.getCharacterGuideData("pi0001").toString();
        text = text.replace("{", "{\n");
        text = text.replace(",", ",\n");
        text = text.replace("}","}\n");
        //String text = String.valueOf(dataBaseHelper.isEnvironmentTableTime());
        Log.d("Test", text);

        dataBaseHelper.deleteRegisterData();
    }
}
