package jp.dmarch.kochitabi;

import java.util.Random;

public class BluetoothAcqisition {
    protected String checkAccessPoint() {
        Random rnd = new Random();
        int ran = rnd.nextInt(10);
        ran = ran % 2;
        if (ran == 1) {
            return "pi0001";
        }
        return null;
    }
}
