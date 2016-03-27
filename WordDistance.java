

/**
 * WordDistance is a simple "struct" container for a word and a cosine distance, the result of a potential
 *   match in the word2vec file
 *   
 * @author hulles
 *
 */
final public class WordDistance {
	private String toWord;
	private Double distance;
	
	public WordDistance(String toWord, Double distance) {
		
		SharedUtils.checkNotNull(toWord);
		SharedUtils.checkNotNull(distance);
		this.toWord = toWord;
		this.distance = distance;
	}
	
	public String getToWord() {
		
		return toWord;
	}
	
	public Double getDistance() {
		
		return distance;
	}

}
