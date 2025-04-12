package fit.se2.datingapp.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class ConstTest {
    @Test
    public void testDefaultAvatarUrl() {
        String expected = "/image/user.png";
        String actual = Const.DEFAULT_AVATAR_URL;
        assertEquals(expected, actual, "DEFAULT_AVATAR_URL does not match /image/user.png");
    }
}
