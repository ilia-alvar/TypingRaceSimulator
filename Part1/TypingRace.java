import java.util.concurrent.TimeUnit;
import java.lang.Math;

/**
 * A typing race simulation. Three typists race to complete a passage of text,
 * advancing character by character — or sliding backwards when they mistype.
 *
 * Originally written by Ty Posaurus, who left this project to "focus on his
 * two-finger technique". He assured us the code was "basically done".
 * We have found evidence to the contrary.
 *
 * @author TyPosaurus
 * @version 1.0
 */
public class TypingRace
{
    private int passageLength;   // Total characters in the passage to type
    private Typist seat1Typist;
    private Typist seat2Typist;
    private Typist seat3Typist;

    // Per-turn display state for showing a recent mistype marker
    private boolean seat1JustMistyped;
    private boolean seat2JustMistyped;
    private boolean seat3JustMistyped;

    // Race tuning constants
    private static final double MISTYPE_BASE_CHANCE = 0.3;
    private static final int    SLIDE_BACK_AMOUNT   = 2;
    private static final int    BURNOUT_DURATION    = 3;

    // Performance adjustment constants
    private static final double WIN_ACCURACY_BOOST      = 0.02;
    private static final double BURNOUT_ACCURACY_PENALTY = 0.02;

    /**
     * Constructor for objects of class TypingRace.
     * Sets up the race with a passage of the given length.
     * Initially there are no typists seated.
     *
     * @param passageLength the number of characters in the passage to type
     */
    public TypingRace(int passageLength)
    {
        this.passageLength = passageLength;
        seat1Typist = null;
        seat2Typist = null;
        seat3Typist = null;

        seat1JustMistyped = false;
        seat2JustMistyped = false;
        seat3JustMistyped = false;
    }

    /**
     * Seats a typist at the given seat number (1, 2, or 3).
     *
     * @param theTypist  the typist to seat
     * @param seatNumber the seat to place them in (1–3)
     */
    public void addTypist(Typist theTypist, int seatNumber)
    {
        if (seatNumber == 1)
        {
            seat1Typist = theTypist;
        }
        else if (seatNumber == 2)
        {
            seat2Typist = theTypist;
        }
        else if (seatNumber == 3)
        {
            seat3Typist = theTypist;
        }
        else
        {
            System.out.println("Cannot seat typist at seat " + seatNumber + " — there is no such seat.");
        }
    }

    /**
     * Starts the typing race.
     * All typists are reset to the beginning, then the simulation runs
     * turn by turn until one typist completes the full passage.
     */
    public void startRace()
    {
        if (seat1Typist == null || seat2Typist == null || seat3Typist == null)
        {
            System.out.println("Cannot start race: all three seats must be filled.");
            return;
        }

        boolean finished = false;
        Typist winner = null;
        double winnerOldAccuracy = 0.0;

        // Reset all typists to the start of the passage
        seat1Typist.resetToStart();
        seat2Typist.resetToStart();
        seat3Typist.resetToStart();

        seat1JustMistyped = false;
        seat2JustMistyped = false;
        seat3JustMistyped = false;

        while (!finished)
        {
            // Clear last-turn mistype markers before this turn begins
            seat1JustMistyped = false;
            seat2JustMistyped = false;
            seat3JustMistyped = false;

            // Advance each typist by one turn
            seat1JustMistyped = advanceTypist(seat1Typist);
            seat2JustMistyped = advanceTypist(seat2Typist);
            seat3JustMistyped = advanceTypist(seat3Typist);

            // Print the current state of the race
            printRace();

            // Check if any typist has finished the passage
            if (raceFinishedBy(seat1Typist))
            {
                finished = true;
                winner = seat1Typist;
            }
            else if (raceFinishedBy(seat2Typist))
            {
                finished = true;
                winner = seat2Typist;
            }
            else if (raceFinishedBy(seat3Typist))
            {
                finished = true;
                winner = seat3Typist;
            }

            if (!finished)
            {
                try
                {
                    TimeUnit.MILLISECONDS.sleep(200);
                }
                catch (Exception e)
                {
                }
            }
        }

        // Improve winner's accuracy slightly
        if (winner != null)
        {
            winnerOldAccuracy = winner.getAccuracy();
            winner.setAccuracy(winner.getAccuracy() + WIN_ACCURACY_BOOST);
        }

        // Print final race state
        printRace();
        System.out.println();

        if (winner != null)
        {
            System.out.println("And the winner is... " + winner.getName() + "!");
            System.out.println("Final accuracy: "
                + format2dp(winner.getAccuracy())
                + " (improved from "
                + format2dp(winnerOldAccuracy)
                + ")");
        }
    }

    /**
     * Simulates one turn for a typist.
     *
     * If the typist is burnt out, they recover one turn's worth and skip typing.
     * Otherwise:
     *   - They may type a character (advancing progress) based on their accuracy.
     *   - They may mistype (sliding back) — the chance of a mistype decreases
     *     for more accurate typists.
     *   - They may burn out — more likely for very high-accuracy typists.
     *
     * @param theTypist the typist to advance
     * @return true if this typist just mistyped on this turn, false otherwise
     */
    private boolean advanceTypist(Typist theTypist)
    {
        boolean justMistyped = false;

        if (theTypist == null)
        {
            return false;
        }

        if (theTypist.isBurntOut())
        {
            theTypist.recoverFromBurnout();
            return false;
        }

        // Attempt to type a character
        if (Math.random() < theTypist.getAccuracy())
        {
            theTypist.typeCharacter();
        }

        // More accurate typists should be LESS likely to mistype
        if (Math.random() < (1.0 - theTypist.getAccuracy()) * MISTYPE_BASE_CHANCE)
        {
            theTypist.slideBack(SLIDE_BACK_AMOUNT);
            justMistyped = true;
        }

        // Burnout check — high accuracy means more pushing, so more burnout risk
        if (Math.random() < 0.05 * theTypist.getAccuracy() * theTypist.getAccuracy())
        {
            theTypist.burnOut(BURNOUT_DURATION);
            theTypist.setAccuracy(theTypist.getAccuracy() - BURNOUT_ACCURACY_PENALTY);
        }

        return justMistyped;
    }

    /**
     * Returns true if the given typist has completed the full passage.
     *
     * @param theTypist the typist to check
     * @return true if their progress has reached or passed the passage length
     */
    private boolean raceFinishedBy(Typist theTypist)
    {
        if (theTypist == null)
        {
            return false;
        }

        return theTypist.getProgress() >= passageLength;
    }

    /**
     * Prints the current state of the race to the terminal.
     */
    private void printRace()
    {
        System.out.print('\u000C'); // Clear terminal

        System.out.println("  TYPING RACE — passage length: " + passageLength + " chars");
        multiplePrint('=', passageLength + 3);
        System.out.println();

        printSeat(seat1Typist, seat1JustMistyped);
        System.out.println();

        printSeat(seat2Typist, seat2JustMistyped);
        System.out.println();

        printSeat(seat3Typist, seat3JustMistyped);
        System.out.println();

        multiplePrint('=', passageLength + 3);
        System.out.println();
        System.out.println("  [~] = burnt out    [<] = just mistyped");
    }

    /**
     * Prints a single typist's lane.
     *
     * @param theTypist the typist whose lane to print
     * @param justMistyped whether the typist mistyped this turn
     */
    private void printSeat(Typist theTypist, boolean justMistyped)
    {
        if (theTypist == null)
        {
            System.out.print('|');
            multiplePrint(' ', passageLength);
            System.out.print('|');
            System.out.print(" [empty seat]");
            return;
        }

        int printedProgress = theTypist.getProgress();
        if (printedProgress > passageLength)
        {
            printedProgress = passageLength;
        }

        int spacesBefore = printedProgress;
        int spacesAfter = passageLength - printedProgress;

        System.out.print('|');
        multiplePrint(' ', spacesBefore);

        System.out.print(theTypist.getSymbol());

        if (theTypist.isBurntOut())
        {
            System.out.print('~');
            spacesAfter--;
        }

        if (justMistyped)
        {
            System.out.print("[<]");
            spacesAfter -= 3;
        }

        if (spacesAfter < 0)
        {
            spacesAfter = 0;
        }

        multiplePrint(' ', spacesAfter);
        System.out.print('|');
        System.out.print(' ');

        if (theTypist.isBurntOut())
        {
            System.out.print(theTypist.getName()
                + " (Accuracy: " + format2dp(theTypist.getAccuracy()) + ")"
                + " BURNT OUT (" + theTypist.getBurnoutTurnsRemaining() + " turns)");
        }
        else if (justMistyped)
        {
            System.out.print(theTypist.getName()
                + " (Accuracy: " + format2dp(theTypist.getAccuracy()) + ")"
                + " just mistyped");
        }
        else
        {
            System.out.print(theTypist.getName()
                + " (Accuracy: " + format2dp(theTypist.getAccuracy()) + ")");
        }
    }

    /**
     * Prints a character a given number of times.
     *
     * @param aChar the character to print
     * @param times how many times to print it
     */
    private void multiplePrint(char aChar, int times)
    {
        int i = 0;
        while (i < times)
        {
            System.out.print(aChar);
            i = i + 1;
        }
    }

    /**
     * Formats a double to 2 decimal places as a String.
     *
     * @param value the value to format
     * @return a string with 2 decimal places
     */
    private String format2dp(double value)
    {
        return String.format("%.2f", value);
    }
}