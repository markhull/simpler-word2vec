
public class WordToVecException extends RuntimeException {
	private static final long serialVersionUID = -335714195360753530L;
	private final String problem;
	
	public WordToVecException(String problem) {
		
		this.problem = problem;
	}

	@Override
	public String getMessage() {
		
		return problem;
	}

}
