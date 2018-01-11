package jp.dmarch.kochitabi;

import com.wikitude.common.camera.CameraSettings;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Date;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;



import java.util.Map;

public class WikitudeContentsFragment {

    protected static String characterFilePath = null;


    //protected static String architectWorldPath = null;

    protected static void setWikitudeContents(Map<String, Object> characterGuideData) {
        // ARキャラクターの表示処理やらなんやら...
        //this.architectView.load("wikitude/index.html");

        //JavaToJavascript javaToJavascript = new


        //jsにデータ渡す
        //final Object accessPointId = characterGuideData.get("access_point_id");
        //final Object characterName = characterGuideData.get("character_name");
        characterFilePath = characterGuideData.get("character_file_path").toString();   // 受け取ったMapオブジェクトに格納されているファイルパスをString型で取り出す
        //final Object textData = characterGuideData.get("text_data");

        //webView.loadUrl("javascript:setCharacterFilePath(characterFilePath)");

        //a = characterFilePath;
        //String charafilepath = a.toString();

        //CameraActivity.onPost


        /*try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            engine.eval("");

        } catch (ScriptException ex) {
            // エラー文を表示
            //Toast.makeText(getApplicationContext(), "jsテスト失敗", Toast.LENGTH_SHORT).show();
            Log.d("dame", "js失敗");
        }*/

        //WebView webview = new WebView(this);
        //webview.addJavascriptInterface(javaToJavascript,"javaToJavascript");



        //String script = "javascript:document.write(charafilepath);";
        //webview.loadUrl(script);

        //WebView webview = new WebView(this);
        //webview.getSettings().setJavaScriptEnabled(true);
        //wv.setWebViewClient(new WebViewClient());
        //webview.addJavascriptInterface(new JavaScript(this), "android");
        //setContentView(webview);
        //webview.loadUrl("file:///../../assets/index.html");
    }

    //JavaScriptを有効に
    //webview.getSettings().setJavaScriptEnabled(true);

/*
    public static void main(String[] args) throws Exception {
        System.out.println("java.version: " + System.getProperty("java.version"));

        // 対応している ScriptEngine の情報を出力
        ScriptEngineManager manager = new ScriptEngineManager();
        List<ScriptEngineFactory> engineFactories = manager.getEngineFactories();
        for (ScriptEngineFactory factory : engineFactories) {
            System.out.println("Engine: " + factory.getEngineName()
                    + ", Version: " + factory.getEngineVersion());
            System.out.println("Language: " + factory.getLanguageName()
                    + ", Version: " + factory.getLanguageVersion());
            System.out.println("Extensions: " + factory.getExtensions());
            System.out.println("MimeTypes: " + factory.getMimeTypes());
            System.out.println("Names: " + factory.getNames());
            System.out.println();
        }
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        // JavaScript の実行
        engine.eval("var message = 'Hello, I am JavaScript.'");
        engine.eval("print('#1: ' + message)");

        // Java のオブジェクトを JavaScript に渡してみる
        engine.put("message2", "Hello, I am Java8.");
        engine.put("today", new Date());
        engine.eval("print('#2: ' + message2 + ' now : ' + today.toString())");

        // JavaScript から Java のメソッドを使用
        engine.eval("var obj = Java.type('test.javascript.JavaScriptTest');"
                + "print('#3: ' + obj.getMessage());");
        engine.eval("obj.printMessage('Good afternoon.');");

        // Java から JavaScript の値を取得
        engine.eval("var message3 = 'Good evening.';");
        Object message3 = engine.get("message3");
        System.out.println("#5: " + message3);

        // Java から JavaScript の関数を呼び出し
        if (engine instanceof Invocable) {
            Invocable invocable = (Invocable) engine;
            engine.eval("var func = function(arg) { print('#6: ' + arg);}");
            invocable.invokeFunction("func", "Good night.");
        }
    }
*/
    /** メッセージを返す */
    /*public static String getMessage() {
        return "Good morning.";
    }*/

    /** メッセージを出力 */
    /*public static void printMessage(String message) {
        System.out.println("#4: " + message);
    }*/

    /*public String javaToJavasrcipt() {
        String charafilepath = a.toString();
        return charafilepath;
    }*/

    // ArchitectViewに設定するhtmlファイルを決定する
    protected static String getArchitectWorldPath() {

        String architectWorldPath = null;

        // パターン１
        if (characterFilePath == null) {
            architectWorldPath = "wikitude/index.html";     // アクセスポイント外にいる(キャラクターファイルパスが渡されていない状態)
        } else if (characterFilePath == "cube.wt3") {
            architectWorldPath = "wikitude/cube.html";      // アクセスポイント内にいる(cubeを表示する観光地)
        }
        //      :                                           // アクセスポイント内にいる(...を表示する観光地)
        //      :                                           // アクセスポイント内にいる(...を表示する観光地)
        //      :                                           // アクセスポイント内にいる(...を表示する観光地)

        // パターン２
        /*
        if (characterFilePath == null) {
            architectWorldPath = "wikitude/index.html";
        } else {
            architectWorldPath = characterFilePath;     //character_file_pathをモデルのファイル名ではなく、htmlファイル名にするなら
        }
        */

        return architectWorldPath;
    }

    // ライセンス返す
    protected static String getWikitudeSDKLicenseKey() {
        return "PlaQ4V2incGCS59Dj6ohTzkfwQBjPeTpx6C4dVvuIse1QP1MKE9wzjqyZUjQvvOHRYOuCrXEDvIwvAFfzjqBON8A5xr5XgdP+mb4CBhMlV+Odf9R/J5P1a+XRPc3fl6vCWPSkfjVOzrSYHqyz8oq7PMH8JHyWm0t0tYFREgVUStTYWx0ZWRfXz5MYhTZHoWUlkoZn+OmpZsfQw45Cmz+ZXp1R/x/5Hm6VBTistSYFLbkWvAgCnkmlvGcFMQSKMt/SMwbdE5hBjUMoJ912hsQQjDgK8G/xTNcLW91nw1ke5/NScKq0ykILKwKUR3NDfQ0zG5RcXb94ef6LR0zU1Y8mxox9Y0Z7ozUsu17H5UkpjK8UXGAqqF7w0mALPFnHkuTiwj03Xfqh9/tAaqRSqY7sJvWp6INhS3AxGsDgwUHfnOSNVjkydO9oT738TZJWjEnry0c5iZb8KkcgZ8k7hGZfxosjr2sSEXwTk/jUhsxtNigyte7V9Eus1Io8kr+LrzlpCpiDtwdSQGK+od8syrEscUGZpKDeCkNUG6CuTT5C+WfR8psAL/yBuQy+Y/nSSI2uzlk71c0bQjUZzeXRe7aKByVUpcat9QlcwyQOpoZF78GgiRXsk5cOSlPfxcGKKC8wcpw2D8VtZKSwsd/nCZ2iB86ltPI63urSLwz9qkTqOE=";
    }

    // どのカメラを使うか指定
    protected static CameraSettings.CameraPosition getCameraPosition() {
        return CameraSettings.CameraPosition.BACK;      //背面カメラ
    }
}