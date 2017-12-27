package jp.dmarch.kochitabi;

import com.wikitude.common.camera.CameraSettings;

import java.util.Map;

public class WikitudeContentsFragment {

    protected static void setWikitudeContents(Map<String, Object> characterGuideData) {
        // ARキャラクターの表示処理やらなんやら...
        //this.architectView.load("wikitude/index.html");
    }

    // ライセンス返す
    protected static String getWikitudeSDKLicenseKey() {
        return "PlaQ4V2incGCS59Dj6ohTzkfwQBjPeTpx6C4dVvuIse1QP1MKE9wzjqyZUjQvvOHRYOuCrXEDvIwvAFfzjqBON8A5xr5XgdP+mb4CBhMlV+Odf9R/J5P1a+XRPc3fl6vCWPSkfjVOzrSYHqyz8oq7PMH8JHyWm0t0tYFREgVUStTYWx0ZWRfXz5MYhTZHoWUlkoZn+OmpZsfQw45Cmz+ZXp1R/x/5Hm6VBTistSYFLbkWvAgCnkmlvGcFMQSKMt/SMwbdE5hBjUMoJ912hsQQjDgK8G/xTNcLW91nw1ke5/NScKq0ykILKwKUR3NDfQ0zG5RcXb94ef6LR0zU1Y8mxox9Y0Z7ozUsu17H5UkpjK8UXGAqqF7w0mALPFnHkuTiwj03Xfqh9/tAaqRSqY7sJvWp6INhS3AxGsDgwUHfnOSNVjkydO9oT738TZJWjEnry0c5iZb8KkcgZ8k7hGZfxosjr2sSEXwTk/jUhsxtNigyte7V9Eus1Io8kr+LrzlpCpiDtwdSQGK+od8syrEscUGZpKDeCkNUG6CuTT5C+WfR8psAL/yBuQy+Y/nSSI2uzlk71c0bQjUZzeXRe7aKByVUpcat9QlcwyQOpoZF78GgiRXsk5cOSlPfxcGKKC8wcpw2D8VtZKSwsd/nCZ2iB86ltPI63urSLwz9qkTqOE=";
    }

    protected static CameraSettings.CameraPosition getCameraPosition() {
        // 背面カメラを使う
        return CameraSettings.CameraPosition.BACK;
    }
}