package com.senorpez.trident.clock;

import android.content.Intent;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class PlanetaryClockActivityTest extends PlanetaryClockActivityTestBase {
    @Test
    public void testLocalDateTime() {
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.tavenTime)).check(matches(withText(localDateTimeValue)));
    }

    @Test
    public void testStandardDateTime() {
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.standardTime)).check(matches(withText(standardDateTimeValue)));
    }
}
