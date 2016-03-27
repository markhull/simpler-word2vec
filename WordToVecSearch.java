
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

/**
 * WordToVecSearch is the class that uses the loaded word2vec BIN file for word matches (aka distance in
 *   the original C version) and analogies
 *   
 * @author hulles
 *
 */
final public class WordToVecSearch {
	private final static String DISTANCE_FORMAT = "(%.4f)";
	private final static Level LOGLEVEL = Level.INFO;
	private static WordToVecSearch instance = null;
	private Map<String, float[]> wordVectors;
	
	private WordToVecSearch() {
		// that's a big map; we only want one of these laying around...
	}
	
	/**
	 * Get the instance of WordToVecSearch, possibly creating it
	 * 
	 * @return The instance
	 */
	public synchronized static WordToVecSearch getInstance() {
		if (instance == null) {
			instance = new WordToVecSearch();
		}
		return instance;
	}
	
	/**
	 * Load the word2vec BIN FORMAT file
	 * 
	 * @param fileName The name of the file (e.g. vectors.bin)
	 */
	public void loadFile(String fileName) {
		WordToVecLoader loader;
		
		SharedUtils.checkNotNull(fileName);
		loader = new WordToVecLoader();
		loader.load(fileName);
		wordVectors = loader.getMap();
		loader = null;
	}
	
	/**
	 * Get the specified number of closest matches to word from the file; comparable to the original 
	 *   word2vec 'distance.c' program in that it returns the cosine distance of word matches
	 *   
	 * @param word The word to match
	 * @param maxNumberOfMatches Self-explanatory
	 * @return A list of "matching" WordDistances
	 * @throws WordToVecException
	 */
	public List<WordDistance> getWordMatches(String word, Integer maxNumberOfMatches)  throws WordToVecException {
		float[] result;
		List<WordDistance> matches;

		SharedUtils.checkNotNull(word);
		SharedUtils.checkNotNull(maxNumberOfMatches);
		WordToVecTimer.startTimer("MATCHES");
		result = wordVectors.get(word);
		if (result == null) {
			throw new WordToVecException(word);
		}
		matches = getVectorMatches(Collections.singletonList(word), result, maxNumberOfMatches);
		WordToVecTimer.stopTimer("MATCHES");
		return matches;
	}
	
	/**
	 * Gets a list of possible analogues to the provided three words; see the original C word-analogy.c program
	 *   for more details. The logic is: word1 is to word2 as word3 is to...?
	 * @param word1
	 * @param word2
	 * @param word3
	 * @param maxNumberOfMatches
	 * @return A list of "matching" WordDistances
	 * @throws WordToVecException
	 */
	public List<WordDistance> getAnalogy(String word1, String word2, String word3, Integer maxNumberOfMatches)  throws WordToVecException {
		float[] result1;
		float[] result2;
		float[] result3;
		float[] searchFor;
		float[] searchVector;
		List<String> ignores;
		List<WordDistance> matches;

		SharedUtils.checkNotNull(word1);
		SharedUtils.checkNotNull(word2);
		SharedUtils.checkNotNull(word3);
		SharedUtils.checkNotNull(maxNumberOfMatches);
		WordToVecTimer.startTimer("ANALOGY");
		result1 = wordVectors.get(word1);
		if (result1 == null) {
			throw new WordToVecException(word1);
		}
		result2 = wordVectors.get(word2);
		if (result2 == null) {
			throw new WordToVecException(word2);
		}
		result3 = wordVectors.get(word3);
		if (result3 == null) {
			throw new WordToVecException(word3);
		}
		ignores = new ArrayList<String>(3);
		ignores.add(word1);
		ignores.add(word2);
		ignores.add(word3);
		searchFor = new float[result1.length];
		for (int ix = 0; ix < searchFor.length; ix++) {
			searchFor[ix] = result2[ix] - result1[ix] + result3[ix];
		}
		searchVector = WordToVecLoader.normalize(searchFor);
		matches = getVectorMatches(ignores, searchVector, maxNumberOfMatches);
		WordToVecTimer.stopTimer("ANALOGY");
		return matches;
	}
	
	/**
	 * This is the heart of the whole shooting match. We convert the wordVectors map to an entry set and evaluate
	 *   each vector against the provided vector. We use a sum of the products of the two vectors to
	 *   get a scalar that we can use to evaluate the closeness of the match (the cosine distance).
	 *   
	 * @param ignores Words to ignore in the file (the search word(s) themselves
	 * @param thisVector The vector of the word we're matching
	 * @param maxNumberOfMatches Self-explanatory
	 * @return A list of "matching" WordDistances
	 * @throws WordToVecException
	 */
	private List<WordDistance> getVectorMatches(List<String> ignores, float[] thisVector, Integer maxNumberOfMatches) throws WordToVecException {
		Set<Entry<String, float[]>> entrySet;
		Double distance;
		List<WordDistance> bestMatches;
		WordDistance wDistance;
		Double leastBestDistance = 0.0;
		
		SharedUtils.checkNotNull(thisVector);
		SharedUtils.checkNotNull(maxNumberOfMatches);
		entrySet = wordVectors.entrySet();
		bestMatches = new ArrayList<WordDistance>(maxNumberOfMatches);
		wDistance = new WordDistance("init", 0.0);
		bestMatches.addAll(Collections.nCopies(maxNumberOfMatches, wDistance));
		SharedUtils.log(LOGLEVEL, "WordToVecSearch: searching entry table");
		for (Entry<String, float[]> entry : entrySet) {
			if (ignores.contains(entry.getKey())) {
				continue;
			}
//			System.out.println("SEARCH:");
//			WordToVecLoader.dumpArray(entry.getValue());
			distance = calculateDistance(thisVector, entry.getValue());
			if (distance > leastBestDistance) {
				// then it belongs in the bestMatches list
				leastBestDistance = updateBestMatches(distance, bestMatches, entry.getKey());
			}
		}
		SharedUtils.log(LOGLEVEL, "WordToVecSearch: built match table for search");
		return bestMatches;
	}
	
	/**
	 * For a given word, insert it into its position in the bestMatches list and remove the last item
	 *   so the list remains the same size
	 * @param distance The cosine distance of the word
	 * @param bestMatches The list of WordDistances that are the best matches so far
	 * @param word The matching word we're inserting into the list
	 * @return The (new) smallest cosine distance of the bestMatches list 
	 */
	private static Double updateBestMatches(Double distance, List<WordDistance> bestMatches, String word) {
		WordDistance wordDistance;
		WordDistance newWordDistance;
		
//		SharedUtils.checkNotNull(distance);
//		SharedUtils.checkNotNull(distances);
//		SharedUtils.checkNotNull(word);
		for (int listIx = 0; listIx < bestMatches.size(); listIx++) {
			wordDistance = bestMatches.get(listIx);
			if (distance > wordDistance.getDistance()) {
				newWordDistance = new WordDistance(word, distance);
				bestMatches.add(listIx, newWordDistance);
				bestMatches.remove(bestMatches.size() - 1);
				break;
			}
		}
		return bestMatches.get(bestMatches.size() - 1).getDistance();
	}
	
	/**
	 * Calculate the cosine distance of the from and to vectors
	 * 
	 * @param from The from vector
	 * @param to The to vector
	 * @return The distance of the vectors
	 */
	private static Double calculateDistance(float[] from, float[] to) {
		double sum = 0.0;
		
//		SharedUtils.checkNotNull(from);
//		SharedUtils.checkNotNull(to);
//		if (from.length != to.length) {
//			throw new IllegalArgumentException();
//		}
		for (int ix = 0; ix < from.length; ix++) {
			sum += from[ix] * to[ix];
		}
		return sum;
	}

	/**
	 * Simple formatter for WordDistance result list
	 * 
	 * @param matches List of WordDistance objects from above
	 * @return The formatted output string
	 */
	public static String formatResult(List<WordDistance> matches) {
		StringBuilder sb;
		
		sb = new StringBuilder();
		for (WordDistance match : matches) {
			if (match.getToWord().equals("init")) {
				continue;
			}
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(match.getToWord());
			sb.append(" ");
			sb.append(String.format(DISTANCE_FORMAT, match.getDistance()));
		}
		sb.append("\n");
		return sb.toString();
	}

	/**
	 * Test the class. The results should be (nearly) identical to the results from the original word2vec
	 * programs, distance.c and word-analogy.c, run against the same vector BIN file.
	 * 
	 * @param args Not used
	 */
	public static void main(String[] args) {
		WordToVecSearch searcher;
		List<WordDistance> distances;
		
		searcher = WordToVecSearch.getInstance();
//		searcher.loadFile("/home/hulles/Word2Vec/trunk/GoogleNews-vectors-negative300.bin");
		searcher.loadFile("/home/hulles/Word2Vec/trunk/big-vectors.bin");
		distances = searcher.getWordMatches("scratched", 20);
		System.out.println("scratched = " + formatResult(distances));
		distances = searcher.getAnalogy("dog", "bone", "cat", 20);
		System.out.println("dog bone cat = " + formatResult(distances));
	}
	
}
