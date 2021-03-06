package planet;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import prey.*;
import alien.*;

public class Planet {
    static final Class[] species = {Whale.class,
                                            Cow.class,
                                            Turtle.class, 
                                            Eagle.class, 
                                            Human.class,
                                            AimlessWanderer.class,
                                            BananaPeel.class,
                                            BlindBully.class,
                                            BullyAlien.class,
                                            CleverAlien.class,
                                            Coward.class,
                                            CropCircleAlien.class,
                                            Fleer.class,
                                            Guard.class,
                                            Hunter.class,
                                            Junkie.class,
                                            Manager.class,
                                            Morphling.class,
                                            OkinawaLife.class,
                                            Rock.class,
                                            Rogue.class,
                                            SecretWeapon3.class,
                                            Survivor.class,
                                            Warrior.class,
                                            WeakestLink.class,
                                            SecretWeapon.class,
                                            SecretWeapon2.class,
                                            ChooseYourBattles.class,
                                            NewGuy.class,
                                            Predator.class,
                                            PredicatEyes.class,
                                            PredicatClaw.class,
                                            Predicoward.class};
    private static final int SIZE = (int)Math.ceil(Math.sqrt(species.length * 100 * 2.5)); // 0 inclusive, SIZE exclusive
	private static final int ROUND_COUNT = 1000;
	private static final int SPECIE_COUNT = 100;
	private static final int START_HEALING_FACTOR = 5;
	static final ThreadLocal<List<Specie>> speciesList = new ThreadLocal<List<Specie>>() {
        @Override
        protected List<Specie> initialValue() {
            return new ArrayList<>();
        }
    };

    public static void main(String[] args) {		
		populate();
		setStartPositions();
        for (int i = 0; i < ROUND_COUNT; i++) {
            moveAndFight();
			removeDeadSpecies();
        }
		
		new Stats(species, speciesList.get()).show();
	}
	
	private static void populate() {
        for(Class<Specie> specie : species) {
			for (int i = 0; i < SPECIE_COUNT; i++) {
				float[] abilities = new float[5];
				try {
					Specie newSpecie = (Specie) specie.newInstance();
					newSpecie.setAbilityPoints(abilities);
					if (checkAbilitesOk(abilities)) {
						newSpecie.upgradeAbilities(abilities);
						newSpecie.heal(Math.round(abilities[0]) * START_HEALING_FACTOR);
						speciesList.get().add(newSpecie);
					}
				} catch (Exception e) {}
			}
		}
	}
	
	private static void moveAndFight() {
		Map<Point, List<Specie>> positions = new HashMap<Point, List<Specie>>();
		Collections.shuffle(speciesList.get()); // make fighting random if multiple species walk on one field
		Area area = new Area(speciesList.get(), SIZE);
		
		for (Specie specie : speciesList.get()) {
			Move move = Move.STAY;
			//move
			Point pos = specie.getPos();
			char[][] vision = area.getVision(specie.getPos(), specie.getVisionFieldsCount());
			try {
				move = specie.move(vision);
			} catch (Exception e) {}
			int posX = area.wrap(pos.x + move.getXOffset());
			int posY = area.wrap(pos.y + move.getYOffset());
			specie.setPos(posX, posY);
			
			//check fight (random order of species because shuffled before)
			pos = specie.getPos();
			List<Specie> speciesOnField = positions.get(pos);
			if (speciesOnField == null) {
				speciesOnField = new ArrayList<Specie>();
				speciesOnField.add(specie);
				positions.put(pos, speciesOnField);
			} else { //fight! (if they want to)
				for (Specie enemy : speciesOnField) {
					Fight fight = new Fight(specie, enemy);
					if (!fight.bothWantPeace()) {
						fight.start();
						fight.rewardWinner();
					}
				}
			}
		}
	}
	
	private static void removeDeadSpecies() {
		Iterator<Specie> it = speciesList.get().iterator();
		while (it.hasNext()) {
			Specie currSpecie = it.next();
			if (currSpecie.isDead()) {
				it.remove();
			}
		}
	}
	
	private static void setStartPositions() {
		Random rand = new Random();
		Point pos;
		List<Point> positions = new ArrayList<Point>();
		for (Specie specie : speciesList.get()) {
			do {
				pos = new Point(rand.nextInt(SIZE), rand.nextInt(SIZE));
			} while (positions.contains(pos));
			specie.setPos(pos.x, pos.y);
			positions.add(pos);
		}
	}
	
	private static boolean checkAbilitesOk(float[] abilities) {
		float sum = 0;
		
		for (float ability : abilities) {
			sum += ability;
			if (ability < 0 || ability > 10) { //suck it, cheaters! :P
				return false;
			}
		}
		
		return sum <= 10;
	}
}
