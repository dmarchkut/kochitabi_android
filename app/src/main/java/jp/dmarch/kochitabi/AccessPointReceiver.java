package jp.dmarch.kochitabi;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.util.Log;

public class AccessPointReceiver extends BroadcastReceiver {
    private Handler handler;

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        String raspberrypiNumber = bundle.getString("raspberrypiNumber");

        Log.d("Receive", raspberrypiNumber);

        // Activityに値を返す
        if(handler != null){
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("raspberrypiNumber", raspberrypiNumber);
            msg.setData(data);
            handler.sendMessage(msg);
        }
    }

    public void registerHandler(Handler locationUpdateHandler) {
        handler = locationUpdateHandler;
    }
}
