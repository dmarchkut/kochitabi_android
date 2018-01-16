package jp.dmarch.kochitabi;

import com.wikitude.common.camera.CameraSettings;
import java.util.Map;

public class WikitudeContentsFragment {
    private static final String defaultArchitectWorldPath = "wikitude/index.html";
    private static final String WIKITUDE_SDK_LICENSEKEY = "PlaQ4V2incGCS59Dj6ohTzkfwQBjPeTpx6C4dVvuIse1QP1MKE9wzjqyZUjQvvOHRYOuCrXEDvIwvAFfzjqBON8A5xr5XgdP+mb4CBhMlV+Odf9R/J5P1a+XRPc3fl6vCWPSkfjVOzrSYHqyz8oq7PMH8JHyWm0t0tYFREgVUStTYWx0ZWRfXz5MYhTZHoWUlkoZn+OmpZsfQw45Cmz+ZXp1R/x/5Hm6VBTistSYFLbkWvAgCnkmlvGcFMQSKMt/SMwbdE5hBjUMoJ912hsQQjDgK8G/xTNcLW91nw1ke5/NScKq0ykILKwKUR3NDfQ0zG5RcXb94ef6LR0zU1Y8mxox9Y0Z7ozUsu17H5UkpjK8UXGAqqF7w0mALPFnHkuTiwj03Xfqh9/tAaqRSqY7sJvWp6INhS3AxGsDgwUHfnOSNVjkydO9oT738TZJWjEnry0c5iZb8KkcgZ8k7hGZfxosjr2sSEXwTk/jUhsxtNigyte7V9Eus1Io8kr+LrzlpCpiDtwdSQGK+od8syrEscUGZpKDeCkNUG6CuTT5C+WfR8psAL/yBuQy+Y/nSSI2uzlk71c0bQjUZzeXRe7aKByVUpcat9QlcwyQOpoZF78GgiRXsk5cOSlPfxcGKKC8wcpw2D8VtZKSwsd/nCZ2iB86ltPI63urSLwz9qkTqOE=";

    // アクセスポイント内に入ったときに呼び出す
    protected static String getArchitectWorldPath(String characterFilePath) {
        return characterFilePath;
    }

    // アクセスポイント外に出たとき、及び起動した時に呼び出す
    protected static String resetArchitectWorldPath() {
        return defaultArchitectWorldPath;
    }

    // ライセンス返す
    protected static String getWikitudeSDKLicenseKey() {
        return WIKITUDE_SDK_LICENSEKEY;
    }

    // どのカメラを使うか指定
    protected static CameraSettings.CameraPosition getCameraPosition() {
        return CameraSettings.CameraPosition.BACK;      //背面カメラ
    }
}