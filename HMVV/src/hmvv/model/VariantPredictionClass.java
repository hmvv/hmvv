package hmvv.model;

public class VariantPredictionClass {
	
	private static final int HIGH = 4;
	private static final int MODERATE = 3;
	private static final int LOW = 2;
	private static final int MODIFIER = 1;
	private static final int UNCATEGORIZED = 0;
	
	public final int importance;
	private final String textDescription;
	private VariantPredictionClass(int importance, String textDescription){
		this.importance = importance;
		this.textDescription = textDescription;
	}
	
	public static VariantPredictionClass createPredictionClass(String classification) {
		switch(classification) {
			case "HIGH": return new VariantPredictionClass(HIGH, classification);
			case "MODERATE": return new VariantPredictionClass(MODERATE, classification);
			case "LOW": return new VariantPredictionClass(LOW, classification);
			case "MODIFIER": return new VariantPredictionClass(MODIFIER, classification);
			case "UNCATEGORIZED": return new VariantPredictionClass(UNCATEGORIZED, classification);
			default:  return new VariantPredictionClass(UNCATEGORIZED, classification);
		}
	}
	
	public static VariantPredictionClass[] getAllClassifications() {
		return new VariantPredictionClass[] {
			new VariantPredictionClass(HIGH, "HIGH"),
			new VariantPredictionClass(MODERATE, "MODERATE"),
			new VariantPredictionClass(LOW, "LOW"),
			new VariantPredictionClass(MODIFIER, "MODIFIER"),
			new VariantPredictionClass(UNCATEGORIZED, "UNCATEGORIZED"),
		};
	}
	
	public String toString() {
		return textDescription;
	}
}
