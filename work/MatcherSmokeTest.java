import com.autoskip.mobile.detection.CooldownController;
import com.autoskip.mobile.detection.SkipTextMatcher;

public final class MatcherSmokeTest {
    public static void main(String[] args) {
        SkipTextMatcher matcher = new SkipTextMatcher();
        require(matcher.match("Skip ad", null, null).found(), "English label");
        require(matcher.match("Пропустить рекламу", null, null).found(), "Russian label");
        require(!matcher.match("Skip in 5", null, null).found(), "Countdown rejection");
        require(!matcher.match(null, null, "id/skip_ad_countdown").found(), "Countdown ID rejection");

        CooldownController cooldown = new CooldownController();
        require(cooldown.canClick("candidate", 1000L, 1500L), "Initial click");
        cooldown.markClicked("candidate", 1000L);
        require(!cooldown.canClick("candidate", 2499L, 1500L), "Cooldown block");
        require(cooldown.canClick("candidate", 2500L, 1500L), "Cooldown expiry");

        System.out.println("Matcher smoke tests passed");
    }

    private static void require(boolean condition, String name) {
        if (!condition) {
            throw new AssertionError(name);
        }
    }
}

