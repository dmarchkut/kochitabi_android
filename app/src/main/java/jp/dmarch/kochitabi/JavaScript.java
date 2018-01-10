package jp.dmarch.kochitabi;


        import android.content.Context;
        import android.widget.Toast;

public class JavaScript {
    private Context c;

    public JavaScript(Context c) {
        this.c = c;
    }

    public void hogeMethod(String s) {
        Toast toast = Toast.makeText(c, s, Toast.LENGTH_LONG);
        toast.show();
    }
}