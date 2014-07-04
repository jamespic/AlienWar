package planet;

import alien.ChooseYourBattles;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BreedingProgram {
    private static final int GENERATION_SIZE = 40;
    private static final int GENERATIONS = 120;
    private static final Random rand = new Random();
    private static ThreadLocal<float[]> currentAttibutes = new ThreadLocal<>(); // Has to be global, due to limitations in Planet

    public static class BredAlien extends ChooseYourBattles {
        @Override
        public void setAbilityPoints(float[] abilities) {
            System.arraycopy(currentAttibutes.get(), 0, abilities, 0, 5);
        }
    }

    public static List<float[]> breed(List<float[]> parents) {
        List<float[]> children = new ArrayList<>();
        for (int i = 0; i < GENERATION_SIZE; i++) {
            float[] parent1 = parents.get(rand.nextInt(parents.size()));
            float[] parent2 = parents.get(rand.nextInt(parents.size()));
            float[] child = new float[5];
            float total = 0.0f;
            for (int j = 0; j < 5; j++) {
                child[j] = Math.max(0.0f, parent1[j] + parent2[j] + 1.0f * ((float) rand.nextGaussian()));
                total += child[j];
            }
            for (int j = 0; j < 5; j++) {
                child[j] = child[j] * 10 / total;
            }
            children.add(child);
        }
        return children;
    }

    public static int score(float[] alien) {
        currentAttibutes.set(alien);
        Planet.speciesList.get().clear();
        Planet.main(null);
        return Planet.speciesList.get().stream().filter(s -> s instanceof BredAlien).reduce(0, (i, s) -> i + s.getAbilitiySum(), (i, j) -> i + j);
    }

    public static List<float[]> runGeneration(List<float[]> parents) {
        List<AlienAndScore> results = parents.parallelStream()
                .map(a -> new AlienAndScore(score(a), a)).collect(Collectors.toList());
        Collections.sort(results, Comparator.comparingInt(a -> -a.score));

        List<float[]> successes = results.stream().limit(GENERATION_SIZE / 3)
                .map(a -> a.alien)
                .collect(Collectors.toList());
        return successes;
    }

    public static void main(String[] args) {
        replaceSpecies();
        List<float[]> population = Stream
                .generate(() -> new float[]{rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), rand.nextFloat()})
                .limit(GENERATION_SIZE)
                .collect(Collectors.toList());

        for (int i = 0; i < GENERATIONS; i++) {
            population = breed(population);
            population = runGeneration(population);
        }
        for (float[] alien: population) {
            System.out.println(Arrays.toString(alien));
        }
    }

    private static void replaceSpecies() {
        for (int i = 0; i < Planet.species.length; i++) {
            Class<Specie> s = Planet.species[i];
            if (s.isAssignableFrom(BredAlien.class)) {
                Planet.species[i] = BredAlien.class;
                return;
            }
        }
        throw new NoSuchElementException("Could not find superclass of BredAlien");
    }

    private static class AlienAndScore {
        public final int score;
        public final float[] alien;

        private AlienAndScore(int score, float[] alien) {
            this.score = score;
            this.alien = alien;
        }
    }
}
