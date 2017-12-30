package jp.dmarch.kochitabi;

import android.test.ActivityUnitTestCase;
import android.util.Log;
import android.view.View;

import org.junit.*;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.mockito.asm.tree.analysis.Value;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/*
 * http://blog.kotemaru.org/2015/08/08/android-studio-testcode.html
 * http://tech.aainc.co.jp/archives/3472
 * javatechnology.net/java/mockito-verify/
 */

public class SavePhotoActivityTest extends ActivityUnitTestCase<SavePhotoActivity> {

    private SavePhotoActivity savePhotoActivity;

    public SavePhotoActivityTest(Class<SavePhotoActivity> activityClass) {
        super(activityClass);
    }

    @Before
    public void setUp() {
        savePhotoActivity = spy(new SavePhotoActivity());
        MockitoAnnotations.initMocks(this);
    }

    /* 遷移してきたときに写真ファイルパスをFile型で取得できている */
    /*public void test1() throws Exception {
        Log.v("test", "test1");
    }*/

    /* filePathに写真が保存されているパスが正しく入っている */
    /*public void test2() throws Exception {
        Log.v("test", "test2");
    }*/

    /* filePathの画像がプレビュとして正しく表示されている */
    /*public void test3() throws Exception {
        Log.v("test", "test3");
    }*/

    /* キャンセルボタンを押すと写真がkochitabiARディレクトリから削除されている */
    /*public void test4() {
        Log.v("test", "test4");

        String directoryPath = this.getClass().getClassLoader().getResource("kochitabiAR").getPath();

        click(R.id.cancel_button);
    }*/

    /* キャンセルボタンを押した際にCameraActivityに遷移する */
    @Test
    public void test5() throws Exception {
        Log.v("test", "test5");
        click(R.id.cancel_button);
        final String className = getActivity().getApplicationInfo().className;
        assertEquals("jp.dmarch.kochitabi.CameraActivity", className);
    }

    /* 保存ボタンを押すと写真が保存できたことを知らせるテキストが表示される */
    /*public void test6() throws Exception {
        Log.v("test", "test6");
        click(R.id.save_button);
    }*/

    /* 保存ボタンを押すとaddImageToGalleryメソッドが呼び出される */
    @Test
    public void test7() throws Exception {
        Log.v("test", "test7");

        // 呼び出されるメソッドがprivateであるためアクセスできるようにする
        Method addImageToGalleryMethod = savePhotoActivity.getClass().getDeclaredMethod("addImageToGallery", String.class);
        addImageToGalleryMethod.setAccessible(true);

        // 中身のないメソッドとして設定
        doNothing().when(addImageToGalleryMethod).invoke(savePhotoActivity, (String)anyObject());

        // 保存ボタンを押す
        click(R.id.save_button);

        // 呼ばれているかどうか
        verify(addImageToGalleryMethod, times(1));
     }

    /* addImageToGalleryメソッドにfilePathが渡されている */
    /*public void test8() throws Exception {
        Log.v("test", "test8");
    }*/

    /* 保存ボタンを押すとギャラリーにfilePathの写真が保存される */
    /*public void test9() throws Exception {
        Log.v("test", "test9");
        click(R.id.save_button);
    }*/

    /* 保存ボタンをした際にCameraActivityに遷移する */
    @Test
    public void test10() throws Exception {
        Log.v("test", "test10");
        click(R.id.save_button);
        final String className = getActivity().getApplicationInfo().className;
        assertEquals("jp.dmarch.kochitabi.CameraActivity", className);
    }

    /* 共有ボタンを押すとsharePhotoメソッドが呼び出される */
    @Test
    public void test11() throws Exception {
        Log.v("test", "test11");

        // 呼び出されるメソッドがprivateであるためアクセスできるようにする
        Method sharePhotoMethod = savePhotoActivity.getClass().getDeclaredMethod("sharePhoto", File.class);
        sharePhotoMethod.setAccessible(true);

        // 中身のないメソッドとして設定
        doNothing().when(sharePhotoMethod).invoke(savePhotoActivity, (File)anyObject());

        // 共有ボタンを押す
        click(R.id.share_button);

        // 呼ばれているかどうか
        verify(sharePhotoMethod, times(1));
    }

    /* sharePhotoメソッドにscreenCaptureFileが渡されている */
    /*public void test12() throws Exception {
        Log.v("test", "test12");

        File testFile = new File("test.png"); // 渡すFile

        // 更新対象オブジェクトを取得
        Method onCreateMethod = savePhotoActivity.getClass().getDeclaredMethod("onCreate");
        Value field = onCreateMethod.getDefaultValue();

        // テストするメソッドがprivateであるためアクセスできるようにする
        Method sharePhotoMethod = savePhotoActivity.getClass().getDeclaredMethod("sharePhoto", File.class);
        sharePhotoMethod.setAccessible(true);

        // 引数を比較するためのキャプチャーを作成
        ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);

        doNothing().when(method).invoke(savePhotoActivity, fileCaptor.capture());

        // 共有ボタンをクリック
        click(R.id.share_button);

        // 用意したFileと同じFileが取得されているか
        final File file = fileCaptor.getValue(); // 引数として取得したFile
        assertEquals(testFile, file); // 比較
    }*/

    /* 共有ボタンを押すと写真共有画面が起動する */
    /*public void test13() throws Exception {
        Log.v("test", "test13");
        click(R.id.share_button);
    }*/

    /* 写真の共有が可能である */
    /*public void test14() throws Exception {
        Log.v("test", "test14");
        File file = new File("test.png");

        Method method = SavePhotoActivity.class.getDeclaredMethod("sharePhoto", File.class);
        method.setAccessible(true);
        method.invoke(new SavePhotoActivity(), file);
    }*/

    /* screenCaptureFileの写真が共有される */
    /*public void test15() throws Exception {
        Log.v("test", "test15");
    }*/

    /* ---------------------------------------------------- */

    private void click(int rId) {
        final View view = getActivity().findViewById(rId);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                view.performClick();
            }
        });
    }

}