package alien;

import planet.Move;

public class Over9000 extends Alien {
    public void setAbilityPoints(float[] abilities) {
        abilities[0] = 10;
    }

    public Move move(char[][] fields) {
        return Move.WEST;
    }

    public boolean wantToFight(int[] enemyAbilities) {
        return enemyAbilities[1] <= 9000;
    }

    @Override
    public int getStrengthLvl() {
        return 9001; // It's over 9000!!!!!!111
    }
}
