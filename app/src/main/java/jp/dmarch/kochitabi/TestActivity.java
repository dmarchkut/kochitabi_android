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
        //dataBaseHelper.setRegisterData();
        dataBaseHelper.deleteRegisterData();

        // この呼び出しの際にはgetCharacterData内のif (isCharacterTableTime) はfalseになるようにします
        /*Map<String, Object> data = dataBaseHelper.getCharacterData("ac0001");
        String text;
        if (data != null) text = data.toString();
        else text = "null";
        text = text.replace("{", "{\n");
        text = text.replace(",", ",\n");
        text = text.replace("}","}\n");
        Log.d("Test", text);

        Map<String, Object> data2 = dataBaseHelper.getCharacterData("ac0001");
        String text2;
        if (data2 != null) text2 = data2.toString();
        else text2 = "null";
        text2 = text2.replace("{", "{\n");
        text2 = text2.replace(",", ",\n");
        text2 = text2.replace("}","}\n");

        Log.d("Test", text2);*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataBaseHelper.deleteRegisterData();
    }
}
