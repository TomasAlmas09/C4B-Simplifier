import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StopServices {

    public static void stopServices() {
        List<String> stoppedPackages = new ArrayList<>(); // List to store stopped packages

        try {
            // Command to list packages
            Process process = Runtime.getRuntime().exec("adb shell pm list packages");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            // Read each line from the package list
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("package:")) {
                    String packageName = line.substring("package:".length()); // Extract full package name

                    // Check if the package contains pt.card4b.app4mobi or com.homerun.console
                    if (packageName.contains("pt.card4b.app4mobi") || packageName.contains("com.homerun.console")) {
                        // Force-stop the package
                        Process stopProcess = Runtime.getRuntime().exec("adb shell am force-stop " + packageName);
                        stopProcess.waitFor(); // Wait for the force-stop process to finish

                        // Check the exit code of the force-stop process
                        int exitCode = stopProcess.exitValue();
                        if (exitCode == 0) {
                            stoppedPackages.add(packageName); // Add the package to the stopped packages list
                        }
                    }
                }
            }
            process.waitFor(); // Wait for the package listing process to finish
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // Display the stopped packages
        if (!stoppedPackages.isEmpty()) {
            System.out.println("Stopped Packages:");
            for (String packageName : stoppedPackages) {
                System.out.println(packageName);
            }
        } else {
            System.out.println("No packages matching pt.card4b.app4mobi or com.homerun.console were stopped.");
        }
    }
}
