// Copyright © 2016-2018 Esko Luontola
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.luontola.cqrshotel.testing;

import fi.luontola.cqrshotel.FastTests;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Category(FastTests.class)
public class WeightedRandomTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final WeightedRandom<String> items = new WeightedRandom<>();

    @Test
    public void returns_items_based_on_random_number() {
        items.add(1.0, "A");
        items.add(1.0, "B");

        assertItemForRandomValue(0.00, is("A"));
        assertItemForRandomValue(0.01, is("A"));
        assertItemForRandomValue(0.49, is("A"));
        assertItemForRandomValue(0.50, is("A"));
        assertItemForRandomValue(0.51, is("B"));
        assertItemForRandomValue(0.99, is("B"));
        assertItemForRandomValue(1.00, is("B"));
    }

    @Test
    public void the_items_have_weighted_probability() {
        items.add(2.0, "A");
        items.add(4.0, "B");

        assertItemForRandomValue(0.33, is("A"));
        assertItemForRandomValue(0.34, is("B"));
    }

    @Test
    public void may_contain_one_item() {
        items.add(1.0, "A");

        assertThat(items.next(), is("A"));
    }

    @Test
    public void may_contain_lots_of_items() {
        items.add(1.0, "A");
        items.add(1.0, "B");
        items.add(1.0, "C");
        items.add(1.0, "D");

        assertItemForRandomValue(0.25, is("A"));
        assertItemForRandomValue(0.50, is("B"));
        assertItemForRandomValue(0.75, is("C"));
        assertItemForRandomValue(1.00, is("D"));
    }

    @Test
    public void cannot_be_empty() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("no items");
        items.next();
    }

    @Test
    public void has_descriptive_toString() {
        items.add(2.0, "A");
        items.add(3.0, "B");

        assertThat(items.toString(), is("{2.0=A, 3.0=B}"));
    }

    private void assertItemForRandomValue(double random, Matcher<String> matcher) {
        assertThat("item for random value " + random, items.next(random), matcher);
    }
}
