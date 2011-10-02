package org.jboss.seam.config.examples.princessrescue.ftest;

import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.test.selenium.AbstractTestCase;
import org.jboss.test.selenium.locator.IdLocator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.jboss.test.selenium.guard.request.RequestTypeGuardFactory.*;
import static org.jboss.test.selenium.locator.LocatorFactory.id;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Functional test for the PrincessRescue example
 *
 * @author Ondrej Skutka
 */
public class PrincessRescueTest extends AbstractTestCase {
    private static final String MSG_INTRO = "The princess has been kidnaped by a dragon, and you are the only one who can save her. Armed only with your trusty bow you bravely head into the dragon's lair, who know what horrors you will encounter, some say the caves are even home to the dreaded Wumpus.";
    private static final String MSG_ENTRANCE = "You enter the dungeon, with you bow in your hand and your heart in your mouth.";

    private static final String MSG_NEAR_BATS = "You hear a screeching noise.";
    private static final String MSG_BATS = "A swarm of bats lands on you and tries to pick you up. They fail miserably. You swat them away with your bow.";

    private static final String MSG_NEAR_DWARF = "You hear drunken singing.";
    private static final String MSG_DWARF = "You find a drunken dwarven miner. He belches in your direction, falls over, then seems to forget you are there.";
    private static final String MSG_SHOT_DWARF = "You hear a 'Thud', followed by a surprised yell.";
    private static final String MSG_DEAD_DWARF = "You find a dead dwarven miner with something that looks suspiciously like one of your arrows sticking out of his chest. Probably best you don't mention this to anyone...";

    private static final String MSG_NEAR_PIT = "You feel a breeze.";
    private static final String MSG_PIT = "You fall into a bottomless pit. Game Over.";

    private static final String MSG_NEAR_PRINCESS = "You hear a sobbing noise.";
    private static final String MSG_PRINCESS = "You find the princess and quickly free her, and then escape from the dungeon. You both live happily ever after.";

    private static final String MSG_NEAR_DRAGON = "You hear a snoring noise. With every snore you see a flickering light, as if something were breathing flames from its nostrils.";
    private static final String MSG_SHOT_DRAGON = "Your arrow wakes up the dragon, without appearing to do any real damage. The last moments of your life are spent running from an angry dragon.";

    private static final String MAIN_PAGE = "/home.jsf";
    private IdLocator NEW_GAME_BUTTON = id("bv:next");

    protected enum Direction {
        NORTH, SOUTH, WEST, EAST
    }

    protected enum Action {
        MOVE, SHOT
    }

    @BeforeMethod
    void startNewGame() throws MalformedURLException {
        selenium.setSpeed(300);
        selenium.open(new URL(contextPath.toString() + MAIN_PAGE));
        ensureTextPresent(MSG_INTRO);
        waitHttp(selenium).click(NEW_GAME_BUTTON);
        ensureTextPresent(MSG_ENTRANCE);
    }

    /**
     * Start the game, kill the dwarf and rescue the princess
     */
    @Test
    public void findPrincess() {
        waitHttp(selenium).click(getLocator(Action.MOVE, Direction.NORTH));
        ensureTextPresent(MSG_NEAR_DWARF);
        ensureTextPresent(MSG_NEAR_PIT);

        // Take a look at the dwarf
        waitHttp(selenium).click(getLocator(Action.MOVE, Direction.WEST));
        ensureTextPresent(MSG_DWARF);
        for (Action action : Action.values()) {
            assertTrue(selenium.isEditable(getLocator(action, Direction.EAST)));
            assertFalse(selenium.isEditable(getLocator(action, Direction.WEST)));
            assertFalse(selenium.isEditable(getLocator(action, Direction.NORTH)));
            assertFalse(selenium.isEditable(getLocator(action, Direction.SOUTH)));
        }

        // We can still hear the dwarf singing
        waitHttp(selenium).click(getLocator(Action.MOVE, Direction.EAST));
        ensureTextPresent(MSG_NEAR_DWARF);
        ensureTextPresent(MSG_NEAR_PIT);

        // Kill the drunkard
        waitHttp(selenium).click(getLocator(Action.SHOT, Direction.WEST));
        ensureTextPresent(MSG_SHOT_DWARF);

        // Bury the evidence
        waitHttp(selenium).click(getLocator(Action.MOVE, Direction.WEST));
        ensureTextPresent(MSG_DEAD_DWARF);

        // No more bad singer
        waitHttp(selenium).click(getLocator(Action.MOVE, Direction.EAST));
        assertFalse(selenium.isTextPresent(MSG_NEAR_DWARF), "Expected the dwarf to be dead already.");
        ensureTextPresent(MSG_NEAR_PIT);

        // Now for the princess!
        waitHttp(selenium).click(getLocator(Action.MOVE, Direction.NORTH));
        ensureTextPresent(MSG_NEAR_BATS);
        ensureTextPresent(MSG_NEAR_PIT);

        waitHttp(selenium).click(getLocator(Action.MOVE, Direction.EAST));
        ensureTextPresent(MSG_BATS);
        ensureTextPresent(MSG_NEAR_PIT);

        waitHttp(selenium).click(getLocator(Action.MOVE, Direction.EAST));
        ensureTextPresent(MSG_NEAR_BATS);

        waitHttp(selenium).click(getLocator(Action.MOVE, Direction.EAST));
        ensureTextPresent(MSG_NEAR_DRAGON);

        waitHttp(selenium).click(getLocator(Action.MOVE, Direction.NORTH));
        ensureTextPresent(MSG_NEAR_PRINCESS);

        // Happy end
        waitHttp(selenium).click(getLocator(Action.MOVE, Direction.EAST));
        ensureTextPresent(MSG_PRINCESS);
        ensureButtonsDisabled();
    }

    /**
     * Start the game, tickle the dragon and die
     */
    @Test
    public void dieHeroically() {
        waitHttp(selenium).click(getLocator(Action.MOVE, Direction.NORTH));
        ensureTextPresent(MSG_NEAR_DWARF);
        ensureTextPresent(MSG_NEAR_PIT);

        waitHttp(selenium).click(getLocator(Action.MOVE, Direction.NORTH));
        ensureTextPresent(MSG_NEAR_BATS);
        ensureTextPresent(MSG_NEAR_PIT);

        waitHttp(selenium).click(getLocator(Action.MOVE, Direction.EAST));
        ensureTextPresent(MSG_BATS);
        ensureTextPresent(MSG_NEAR_PIT);

        waitHttp(selenium).click(getLocator(Action.MOVE, Direction.EAST));
        ensureTextPresent(MSG_NEAR_BATS);

        waitHttp(selenium).click(getLocator(Action.MOVE, Direction.EAST));
        ensureTextPresent(MSG_NEAR_DRAGON);

        waitHttp(selenium).click(getLocator(Action.SHOT, Direction.EAST));
        ensureTextPresent(MSG_SHOT_DRAGON);
        ensureButtonsDisabled();
    }

    /**
     * Start the game, enjoy a free fall.
     */
    @Test
    public void dieImpressively() {
        waitHttp(selenium).click(getLocator(Action.MOVE, Direction.NORTH));
        ensureTextPresent(MSG_NEAR_DWARF);
        ensureTextPresent(MSG_NEAR_PIT);

        waitHttp(selenium).click(getLocator(Action.MOVE, Direction.EAST));
        ensureTextPresent(MSG_PIT);
        ensureButtonsDisabled();
    }

    /**
     * Start the game, kill the dwarf, start over, ensure the dwarf is alive again (and drunken).
     */
    @Test
    public void quitEarly() {
        waitHttp(selenium).click(getLocator(Action.MOVE, Direction.NORTH));
        ensureTextPresent(MSG_NEAR_DWARF);
        ensureTextPresent(MSG_NEAR_PIT);

        waitHttp(selenium).click(getLocator(Action.SHOT, Direction.WEST));
        ensureTextPresent(MSG_SHOT_DWARF);

        waitHttp(selenium).click(NEW_GAME_BUTTON);
        ensureTextPresent(MSG_INTRO);
        waitHttp(selenium).click(NEW_GAME_BUTTON);
        ensureTextPresent(MSG_ENTRANCE);

        waitHttp(selenium).click(getLocator(Action.MOVE, Direction.NORTH));
        ensureTextPresent(MSG_NEAR_DWARF);
        ensureTextPresent(MSG_NEAR_PIT);
    }

    /**
     * Ensures that all the move and shot buttons present on the page are disabled. Fails if not.
     */
    private void ensureButtonsDisabled() {
        for (Direction direction : Direction.values()) {
            for (Action action : Action.values()) {
                assertFalse(selenium.isEditable(getLocator(action, direction)));
            }
        }
    }

    /**
     * Ensures that the specified text is present on the page. Fails if not.
     */
    private void ensureTextPresent(String text) {
        assertTrue(selenium.isTextPresent(text), "Expected the following text to be present: \"" + text + "\"");
    }

    /**
     * Returns the move or shot button specified by parameters.
     */
    private IdLocator getLocator(Action action, Direction direction) {
        return id("bv:" + action.toString().toLowerCase() + direction.toString().toLowerCase());
    }

}
