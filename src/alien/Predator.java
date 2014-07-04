package alien;

import planet.Move;

public class Predator extends Alien {

    public void setAbilityPoints(float[] abilities) {
        abilities[0]=2;
        abilities[1]=8;
        abilities[2]=0;
        abilities[3]=0;
        abilities[4]=0;
    }

    public Move move(char[][] fields) {
        for (Move mv:Move.values()) {
            if (fields[mv.getXOffset()][mv.getYOffset()]=='H') {
                return mv;
            }
        }
        return Move.NORTHEAST;
    }

    public boolean wantToFight(int[] enemyAbilities) {
        if (enemyAbilities[0]*enemyAbilities[1] <= getLifeLvl()*getStrengthLvl()*.25f) {
            return true;
        }
        return false;
    }
}