package jp.dmarch.kochitabi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Map;


public class TestActivity extends AppCompatActivity {

    ServerExchange serverExchange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

         serverExchange = new ServerExchange(); // インスタンス化

        // 取得したデータを表示するEditText
        final EditText editText = findViewById(R.id.editText);

        // すべてのデータを取得するボタン
        Button spotButton = findViewById(R.id.local_db_button);
        spotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // すべてのデータを取得
                ArrayList<ArrayList<Map<String, Object>>> localDataBaseTables;
                localDataBaseTables = serverExchange.getLocalDataBaseOption();

                String data = localDataBaseTables.toString();
                data = data.replace("{", "{\n\t");
                data = data.replace(",", ",\n\t");
                data = data.replace("}", "\n}");

                editText.setText(data);

            }
        });

        // 環境テーブルデータを取得するボタン
        final Button environmentButton = findViewById(R.id.environment_button);
        environmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<Map<String, Object>> environmentTable;
                environmentTable = serverExchange.getEnvironmentTable();

                String data = environmentTable.toString();
                data = data.replace("{", "{\n\t");
                data = data.replace(",", ",\n\t");
                data = data.replace("}", "\n}");

                editText.setText(data);

            }
        });

        // キャラクターテーブルデータを取得するボタン
        Button characterButton = findViewById(R.id.character_button);
        characterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<Map<String, Object>> characterTable;
                characterTable = ServerExchange.getCharacterTable();

                String data = characterTable.toString();
                data = data.replace("{", "{\n\t");
                data = data.replace(",", ",\n\t");
                data = data.replace("}", "\n}");

                editText.setText(data);
            }
        });

    }
}
