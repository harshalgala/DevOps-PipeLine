package itrust.fuzzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class CommitFuzzer {

	public static void main(String args[]) throws Exception {
		int totalbuilds = args.length > 0 ? Integer.parseInt(args[0]) : 100 ;

		System.out.println("Fuzzer Execution Starts Now");
		System.out.println("Trying Fuzzing for " + totalbuilds + "times");

		// setup itrust git repository
		File repo = new File("/var/lib/jenkins/workspace/iTrust/.git");
		FileRepositoryBuilder repobuilder = new FileRepositoryBuilder();
		Repository itrust = repobuilder.setGitDir(repo).readEnvironment().findGitDir().build();

		Git localgit = new Git(itrust);

		// create build reports folder path to store surefire reports after each build
		Path reportsfolder = Paths.get("/var/lib/jenkins/build-reports");
		Files.createDirectory(reportsfolder);

		// use fuzzer branch and not master for 100 commits
		localgit.branchCreate().setName("fuzzer").call();
		localgit.checkout().setName("fuzzer").call();

		for (int buildnum = 1; buildnum <= totalbuilds; buildnum++) {

			System.out.println("Fuzzing begins for build number" + buildnum);

			// Call Fuzzy to make fuzzing changes locally and then commit
			Fuzzy.callFuzzWithDirectory("/var/lib/jenkins/workspace/iTrust/iTrust/src/main/edu/ncsu/csc/itrust/");

			// git add <all fuzzed changes>
			localgit.add().addFilepattern(".").call();
			localgit.commit().setMessage("Commit fuzzing changes for build: " + buildnum).call();

			// wait for 3 mins for the secondary build job trigger by post-commit hook to
			// complete running
			Thread.sleep(180000);

			// create sourcefolder variable for folder with surefire-reports after secondary
			// build job completes
			Path sourcefolder = Paths.get("/var/lib/jenkins/workspace/iTrust_Fuzzer/iTrust/target/surefire-reports/");

			// create destination folder for each build number to store sure-fire reports
			Path destinationfolder = Paths.get("/var/lib/jenkins/build-reports/" + buildnum);
			Files.createDirectory(destinationfolder);

			// recursively copy folder contents from sourcefolder to destinationfolder
			Files.walkFileTree(sourcefolder, new CopyFileVisitor(destinationfolder));

			// reset the fuzzerbranch back to state=master for next build
			// get rid of fuzzed changes
			localgit.reset().setMode(ResetType.HARD).setRef("HEAD~1").call();

			System.out.println("Fuzzing ends for build number" + buildnum);
		}

//		localgit.checkout().setName("master").call();
		localgit.close();

		System.out.println("Fuzzer Execution Ends Now");
	}
}

// Reference -
// https://stackoverflow.com/questions/6214703/copy-entire-directory-contents-to-another-directory
class CopyFileVisitor extends SimpleFileVisitor<Path> {
	private final Path targetPath;
	private Path sourcePath = null;

	public CopyFileVisitor(Path targetPath) {
		this.targetPath = targetPath;
	}

	@Override
	public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
		if (sourcePath == null) {
			sourcePath = dir;
		} else {
			Files.createDirectories(targetPath.resolve(sourcePath.relativize(dir)));
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
		Files.copy(file, targetPath.resolve(sourcePath.relativize(file)));
		return FileVisitResult.CONTINUE;
	}
}
