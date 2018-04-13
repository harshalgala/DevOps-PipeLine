package itrust.fuzzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
//import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import org.apache.commons.text.RandomStringGenerator;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

public class Fuzzy {
	public static int num_skipped;
	public static int num_changed;

	protected static boolean randomBoolean(float probability) {
		return Math.random() > probability;
	}

	public static void fuzz(File projectDir) {

		String relative_path = "/var/lib/jenkins/workspace/iTrust/iTrust/src/main/edu/ncsu/csc/itrust";
		// reference -
		// https://github.com/ftomassetti/analyze-java-code-examples/blob/master/src/main/java/me/tomassetti/examples/MethodCallsExample.java
		new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {

			String filepath = relative_path + path;

			if (randomBoolean(0.50f)) {
				System.out.println("Ignore Fuzzing for file: " + filepath);
				num_skipped++;
			} else {
				num_changed++;
				CompilationUnit cu = null;

				try {
					cu = JavaParser.parse(file);
				} catch (Exception e) {
					System.out.println("Faced an exception while trying to parse file: " + filepath);
					e.printStackTrace();
				}

				new VoidVisitorAdapter<Object>() {

					@Override
					public void visit(StringLiteralExpr stringliteral, Object arg) {
						super.visit(stringliteral, arg);

						RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange('A', 'z')
								.filteredBy(LETTERS).build();
						String changedString = generator.generate((int) (Math.random() * 30));

						if (randomBoolean(0.15f)) {
							stringliteral.setValue(changedString);
						}
					}
					
					@Override
					public void visit(IntegerLiteralExpr intliteral, Object arg) {
						super.visit(intliteral, arg);
						String one = new String("1");
						String zero = new String("0");
						
						if (randomBoolean(0.15f) && zero.equals(intliteral.getValue())) {
							intliteral.setValue(new String("1"));
						} else if (randomBoolean(0.15f) && one.equals(intliteral.getValue())) {
							intliteral.setValue(new String("0"));
						} /* else if (randomBoolean(0.15f)) {
							String changedString = ((int) (Math.random() * intliteral.getValue().length() * 100)) + "";
							intliteral.setValue(changedString);
						} */
					}

					@Override
					public void visit(BinaryExpr binaryexpr, Object arg) {
						super.visit(binaryexpr, arg);

						if (binaryexpr.getOperator() == BinaryExpr.Operator.NOT_EQUALS) {
							if (randomBoolean(0.15f)) {
								binaryexpr.setOperator(BinaryExpr.Operator.EQUALS);
							}
						}

						if (binaryexpr.getOperator() == BinaryExpr.Operator.EQUALS) {
							if (randomBoolean(0.20f)) {
								binaryexpr.setOperator(BinaryExpr.Operator.NOT_EQUALS);
							}
						}

						if (binaryexpr.getOperator() == BinaryExpr.Operator.LESS) {
							if (randomBoolean(0.25f)) {
								binaryexpr.setOperator(BinaryExpr.Operator.GREATER);
							}
						}

						if (binaryexpr.getOperator() == BinaryExpr.Operator.GREATER) {
							if (randomBoolean(0.15f)) {
								binaryexpr.setOperator(BinaryExpr.Operator.LESS);
							}
						}

						if (binaryexpr.getOperator() == BinaryExpr.Operator.LESS_EQUALS) {
							if (randomBoolean(0.30f)) {
								binaryexpr.setOperator(BinaryExpr.Operator.GREATER_EQUALS);
							}
						}
	
						if (binaryexpr.getOperator() == BinaryExpr.Operator.GREATER_EQUALS) {
							if (randomBoolean(0.10f)) {
								binaryexpr.setOperator(BinaryExpr.Operator.LESS_EQUALS);
							}
						}
					/*	
						if (binaryexpr.getOperator() == BinaryExpr.Operator.AND) {
							if (randomBoolean(0.22f)) {
								binaryexpr.setOperator(BinaryExpr.Operator.OR);
							}
						}

						if (binaryexpr.getOperator() == BinaryExpr.Operator.OR) {
							if (randomBoolean(0.25f)) {
								binaryexpr.setOperator(BinaryExpr.Operator.AND);
							}
						}
					*/
					}

				}.visit(cu, null);

				String fuzzedCode = cu.toString();
				try {
					Files.write(Paths.get(filepath), fuzzedCode.getBytes());
				} catch (IOException e) {
					System.out.println("Failed to write to file: " + filepath);
					e.printStackTrace();
				}
			}
		}).explore(projectDir);
	}

	public static void callFuzzWithDirectory(String dirpath) {
		num_skipped = 0;
		num_changed = 0;
		File dir = new File(dirpath);
		fuzz(dir);
		System.out.println("Total number of files: " + (num_skipped + num_changed));
		System.out.println("After fuzzing, Number of files skipped: " + num_skipped);
		System.out.println("After fuzzing, Number of files changed: " + num_changed);
	}

}
