package src.app;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import src.model.Game;
import src.model.Result;

public class Games {

	private static final Path CSV = Paths.get("Games/games.csv");
	private static final String BUNDESLIGA = "BUNDESLIGA";
	private static final String BAYERN = "FC Bayern Muenchen";

	public static void main(String[] args) throws IOException {

		List<Game> games = null;
		try (Stream<String> lines = Files.lines(CSV)) {
			games = lines.skip(1).map(Game::fromString).collect(toList());
		} catch (Exception e) {
			throw new NoSuchElementException("No CSV file found");
		}

//		games.forEach(System.out::println);
//		System.out.println();

		// -------------------

		// TODO: Wie viele Spiele sind Bundesliga Spiele? Bundesliga ist ein Enum, kein
		// String. (contain BUNDESLIGA)?
		// (Lösung mit filter)

		long bundesligaGameCount = games.stream().filter(game -> game.getInfo().contains(BUNDESLIGA)).count();
		// mit getInfo holt er sich die Infos von Bundesliga,
		// und vergleicht mit contains ob Bundesliga enthalten ist,
		// und wenn ja wird 1 hochgez�hlt.

		System.out.println("There were " + bundesligaGameCount + " Bundesliga games");
		System.out.println();

		// -------------------

		// TODO: Welche Spiele sind Auswärts- und welche Heimspiele?
		// (Lösung mit partitionBy)

		Map<Boolean, List<Game>> homeAwayMap = games.stream()
				.collect(Collectors.partitioningBy(game -> game.getHome().equals(BAYERN)));

		System.out.println("*** HOME ***");
		homeAwayMap.get(true).forEach(System.out::println);
		System.out.println("*** AWAY ***");
		homeAwayMap.get(false).forEach(System.out::println);
		System.out.println();

		// -------------------

		// TODO Gruppiere die Spiele in won, lost und draw (draw = Unentschieden)
		// (Lösung mit groupingBy)

		Map<Result, List<Game>> wonLostDrawMap = games.stream().collect(Collectors.groupingBy(game -> {
			if (game.getHomeGoals() == game.getAwayGoals())
				return Result.DRAW;
			if (game.getHome().equals(BAYERN) && game.getAwayGoals() < game.getHomeGoals()
					|| game.getAwayGoals() > game.getHomeGoals() && game.getAway().equals(BAYERN))
				return Result.WON;
			else
				return Result.LOST;
		}));

		System.out.println("*** WON ***");
		wonLostDrawMap.get(Result.WON).forEach(System.out::println);
		System.out.println("*** DRAW ***");
		wonLostDrawMap.get(Result.DRAW).forEach(System.out::println);
		System.out.println("*** LOST ***");
		wonLostDrawMap.get(Result.LOST).forEach(System.out::println);
		System.out.println();

		// -------------------

		// TODO Wie viele Tore wurden im Durchschnitt pro Spiel erzielt? mapToInt
		// (Lösung mit mapToInt)

		double avgGoalsPerGame1 = games.stream().mapToInt(Game::goalCount).average().orElse(0.0);

		System.out.printf("Average goals per game: %.2f\n", avgGoalsPerGame1);

		// TODO Wie viele Tore wurden im Durchschnitt pro Spiel erzielt? averagingDouble
		// (Lösung mit withCollectors.averagingDouble)
		double avgGoalsPerGame2 = games.stream().collect(Collectors.averagingDouble(Game::goalCount));

		System.out.printf("Average goals per game: %.2f\n", avgGoalsPerGame2);
		System.out.println();

		// -------------------

		// TODO Wie viele Spiele hat Bayern München zu Hause gewonnen?
		// (home equals BAYERN)?
		// (Lösung mit double filter und count)
		long wonHomeGamesCount = games.stream().filter(game -> game.getHome().equals(BAYERN))
				.filter(game -> game.getHomeGoals() > game.getAwayGoals()).count();

		System.out.println(BAYERN + " won " + wonHomeGamesCount + " games at home");
		System.out.println();

		// -------------------

		// TODO Was war das Spiel mit den wenigsten Toren? sorted findFirst
		// (Lösung mit sorted und findFirst)
		Game leastNumberOfGoalsGame1 = games.stream().sorted(Comparator.comparing(Game::goalCount)).findFirst().get();
		;

		System.out.println("Game with least number of goals: " + leastNumberOfGoalsGame1);

		// TODO Was war das Spiel mit den wenigsten Toren? min Comparator.comparingInt
		// (Lösung mit min und Comparator.comparingInt)
		Game leastNumberOfGoalsGame2 = games.stream().min(Comparator.comparingInt(Game::goalCount)).get();
		;

		System.out.println("Game with least number of goals: " + leastNumberOfGoalsGame2);
		System.out.println();

		// -------------------

		// TODO Welche unterschiedlichen (distinct) Startzeiten gibt es?
		// (Lösung mit einem stream und Collectors.joining)
		String startingTimesString = games.stream().map(Game::getTime).distinct().collect(Collectors.joining(", "));

		System.out.println("Distinct starting times: " + startingTimesString);
		System.out.println();

		// -------------------

		// TODO hat Bayern ein Auswärtsspiel mit mindestens 2 Toren Unterschied
		// gewonnen?
		// (home equals BAYERN)?
		// (Lösung mit anyMatch)

		boolean bayernWon = games.stream().filter(game -> game.getAway().equals(BAYERN))
				.anyMatch(game -> game.getAwayGoals() - game.getHomeGoals() >= 2);
		;

		System.out.println("Bayern won away game with at least 2 goals difference: " + (bayernWon ? "yes" : "no"));
		System.out.println();

		// -------------------

		// TODO Ein Freund von dir gab dir die Spiele von 2019, die jedoch nach der
		// Heimmannschaft gruppiert wurden. Du möchtest aber alle Spiele als einfache
		// Liste abrufen!
		// (Lösung with flatMap und Collectors.toList)
		Map<String, List<Game>> games2019ByHomeTeam = games.stream().filter(game -> game.getDate().contains("2019"))
				.collect(Collectors.groupingBy(Game::getHome));
		List<Game> flattenedGames = games2019ByHomeTeam.values().stream().flatMap(Collection::stream)
				.collect(Collectors.toList());

		flattenedGames.forEach(System.out::println);
	}
}
