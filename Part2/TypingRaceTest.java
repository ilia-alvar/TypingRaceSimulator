public class TypingRaceTest
{
    public static void main(String[] args)
    {
        TypingRace race = new TypingRace(
            "How beautiful a flower is."
        );

        Typist t1 = new Typist("🫖", "Flint Luckwood", 0.85);
        Typist t2 = new Typist("🌷", "Micheal Wazawski", 0.75);
        Typist t3 = new Typist("🍧", "Ilia Grozer", 0.90);

        // Customisation examples
        t1.setTypingStyle("Touch Typist");
        t2.setKeyboardType("Membrane");
        t3.setEnergyDrink(true);

        race.addTypist(t1);
        race.addTypist(t2);
        race.addTypist(t3);

        // Optional modifiers
        race.setAutocorrectOn(true);
        race.setCaffeineModeOn(true);
        race.setNightShiftOn(false);

        race.startRace();
    }
}