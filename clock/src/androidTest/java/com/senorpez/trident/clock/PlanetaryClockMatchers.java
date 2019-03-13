package com.senorpez.trident.clock;

import android.view.View;
import android.widget.ProgressBar;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.internal.util.Checks;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class PlanetaryClockMatchers {
    static Matcher<View> withProgress(final int expectedProgress) {
        Checks.checkNotNull(expectedProgress);
        return new BoundedMatcher<View, ProgressBar>(ProgressBar.class) {
            @Override
            protected boolean matchesSafely(ProgressBar item) {
                return expectedProgress == item.getProgress();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(String.format("Expected: %d", expectedProgress));
            }
        };
    }
}
