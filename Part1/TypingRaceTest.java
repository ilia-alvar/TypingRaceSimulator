//Test for TypingRace 

public class TypingRaceTest
{
    public static void main(String[] args)
    {
        TypingRace race = new TypingRace(30);

        Typist t1 = new Typist('A', "Flint Luckwood", 0.85);
        Typist t2 = new Typist('B', "Micheal Wazawski", 0.75);
        Typist t3 = new Typist('C', "Ilia Grozer", 0.90);

        race.addTypist(t1, 1);
        race.addTypist(t2, 2);
        race.addTypist(t3, 3);

        race.startRace();
    }
}