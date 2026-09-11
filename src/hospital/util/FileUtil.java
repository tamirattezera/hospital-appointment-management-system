package hospital.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class FileUtil {

    private FileUtil() {
        // Utility class; do not instantiate.
    }

    public static List<String> readLines(Path path)
            throws IOException {

        return Files.readAllLines(path);
    }

    public static void writeText(Path path, String content)
            throws IOException {

        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        Files.writeString(path, content);
    }
}