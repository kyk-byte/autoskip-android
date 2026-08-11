package com.autoskip.mobile.detection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class CooldownControllerTest {
    @Test
    public void blocksSameControlUntilCooldownExpires() {
        CooldownController controller = new CooldownController();

        assertTrue(controller.canClick("button-a", 1_000L, 1_500L));
        controller.markClicked("button-a", 1_000L);

        assertFalse(controller.canClick("button-a", 2_499L, 1_500L));
        assertTrue(controller.canClick("button-a", 2_500L, 1_500L));
    }

    @Test
    public void appliesShortGlobalFloorAcrossControls() {
        CooldownController controller = new CooldownController();
        controller.markClicked("button-a", 1_000L);

        assertFalse(controller.canClick("button-b", 1_349L, 1_500L));
        assertTrue(controller.canClick("button-b", 1_350L, 1_500L));
    }

    @Test
    public void refusesMissingFingerprint() {
        CooldownController controller = new CooldownController();
        assertFalse(controller.canClick("", 1_000L, 1_500L));
        assertFalse(controller.canClick(null, 1_000L, 1_500L));
    }
}

