import javax.swing.*;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScreenRecordWithScrcpy {

    private static JFrame parentFrame; // Parent window to display messages
    private static Process scrcpyProcess; // Scrcpy process running
    private static Process screenRecordProcess; // Screen recording process
    private static boolean recordingStarted = false; // Flag to check if recording started

    // Static method to start Scrcpy
    public static void startScrcpy(JFrame parent) {
        parentFrame = parent; // Assign the parent JFrame

        try {
            // Path to scrcpy executable
            String exePath = "packages/scrcpy/scrcpy.exe";

            // Check if scrcpy.exe exists
            File exeFile = new File(exePath);
            if (!exeFile.exists()) {
                JOptionPane.showMessageDialog(parentFrame, "scrcpy.exe not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Build process to start scrcpy
            ProcessBuilder processBuilder = new ProcessBuilder(exePath);
            scrcpyProcess = processBuilder.start();

            // Add shutdown hook to stop recording when scrcpy is terminated
            Runtime.getRuntime().addShutdownHook(new Thread(ScreenRecordWithScrcpy::stopScreenRecord));

            // Start screen recording immediately after starting scrcpy
            startScreenRecord();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(parentFrame, "Error starting Scrcpy: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Static method to stop Scrcpy
    private static void stopScrcpy() {
        if (scrcpyProcess != null) {
            scrcpyProcess.destroy();
        }
    }

    // Static method to start screen recording
    private static void startScreenRecord() {
        try {
            // Set recording file name
            String recordFileName = getNextAvailableRecordFileName();

            // Start screen recording using adb
            ProcessBuilder screenRecordProcessBuilder = new ProcessBuilder("adb", "shell", "screenrecord", "--verbose", "/sdcard/" + recordFileName);
            screenRecordProcess = screenRecordProcessBuilder.start();
            recordingStarted = true;

            // Thread to continuously check if Scrcpy is still active
            Thread scrcpyWatchThread = new Thread(() -> {
                try {
                    // Wait for Scrcpy process to terminate
                    scrcpyProcess.waitFor();

                    // Stop screen recording when Scrcpy is terminated
                    if (recordingStarted) {
                        stopScreenRecord();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            scrcpyWatchThread.start();

            // Wait for screen recording process to finish
            int exitCode = screenRecordProcess.waitFor();
            if (exitCode == 0) {
                transferRecordedFile(recordFileName);
            } else {
                JOptionPane.showMessageDialog(parentFrame, "Error recording screen.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentFrame, "Error starting screen recording.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Static method to get the next available record file name
    private static String getNextAvailableRecordFileName() {
        File recordingsDir = new File("Recordings");
        if (!recordingsDir.exists()) {
            recordingsDir.mkdirs();
        }

        File[] existingFiles = recordingsDir.listFiles();
        int nextNumber = 0;

        if (existingFiles != null) {
            Pattern pattern = Pattern.compile("screen_record_(\\d+)\\.mp4");
            for (File file : existingFiles) {
                String fileName = file.getName();
                Matcher matcher = pattern.matcher(fileName);
                if (matcher.matches()) {
                    int number = Integer.parseInt(matcher.group(1));
                    if (number >= nextNumber) {
                        nextNumber = number + 1;
                    }
                }
            }
        }

        return "screen_record_" + nextNumber + ".mp4";
    }

    // Static method to transfer recorded file to local directory
    private static void transferRecordedFile(String recordFileName) {
        try {
            File recordingsDir = new File("Recordings");
            if (!recordingsDir.exists()) {
                recordingsDir.mkdirs();
            }

            ProcessBuilder processBuilder = new ProcessBuilder("adb", "pull", "/sdcard/" + recordFileName, "Recordings/");
            Process pullProcess = processBuilder.start();

            int pullExitCode = pullProcess.waitFor();
            if (pullExitCode == 0) {
                JOptionPane.showMessageDialog(parentFrame, "Screen recording " + recordFileName + " saved successfully in Recordings folder!", "Success", JOptionPane.INFORMATION_MESSAGE);
                Desktop desktop = Desktop.getDesktop();
                desktop.open(recordingsDir);
            } else {
                JOptionPane.showMessageDialog(parentFrame, "Error transferring recorded file.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentFrame, "Error transferring recorded file.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            recordingStarted = false;
        }
    }

    // Static method to stop screen recording
    private static void stopScreenRecord() {
        if (recordingStarted) {
            try {
                ProcessBuilder stopRecordProcessBuilder = new ProcessBuilder("adb", "shell", "kill", "-2", "-$(pidof", "screenrecord)");
                Process stopRecordProcess = stopRecordProcessBuilder.start();
                stopRecordProcess.waitFor();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Error stopping screen recording.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to start recording (this is called from C4BSimplifier)
    public static void startRecording(JFrame parentFrame) {
        startScrcpy(parentFrame);
    }
}
