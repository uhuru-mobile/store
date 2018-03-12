package fdroid.fdroid.org.uhurustore;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void validateSelectAPKTest() throws Exception {
        AppDetailsCtrl appDetailsCtrl = Mockito.mock(AppDetailsCtrl.class);

        Mockito.when(appDetailsCtrl.selectAPK("com.test")).thenReturn(true);

        Assert.assertEquals(true, appDetailsCtrl.selectAPK("com.test"));
    }
}