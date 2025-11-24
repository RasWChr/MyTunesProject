package dk.easv.demo;

import java.net.URL;

public class TestFXMLFinder {
    public static void main(String[] args) {
        System.out.println("=== Looking for FXML files ===");

        // Try different paths
        String[] possiblePaths = {
                "/dk/easv/demo/GUI/MainView.fxml",
                "dk/easv/demo/GUI/MainView.fxml",
                "/GUI/MainView.fxml",
                "dk/easv/demo/GUI/MainView.fxml",
                "/dk/easv/demo/GUI/MainView.fxml"
        };

        for (String path : possiblePaths) {
            URL url = TestFXMLFinder.class.getResource(path);
            System.out.println("Path: '" + path + "' -> " + (url != null ? "FOUND" : "NOT FOUND"));
            if (url != null) {
                System.out.println("   Full URL: " + url);
            }
        }

        // Also check classpath
        System.out.println("\n=== Classpath Info ===");
        String classpath = System.getProperty("java.class.path");
        System.out.println("Classpath: " + classpath);
    }
}