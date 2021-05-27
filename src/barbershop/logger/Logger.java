package barbershop.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This class is a singleton and it is used to log strings
 * to a log file and add the log time to that.
 */
public class Logger {
    private static String FILENAME = "log.txt";
    private static String LOCATION = "logs";
    private static Logger single_instance = null;

    private final BufferedWriter logger;

    private Logger() throws IOException {
        File file = new File(Logger.LOCATION, Logger.FILENAME);
        file.getParentFile().mkdirs();

        file.createNewFile();
        FileWriter fr = new FileWriter(file, true);
        this.logger = new BufferedWriter(fr);
    }

    public static Logger getInstance() {
        if (Logger.single_instance == null) {
            try {
                Logger.single_instance = new Logger();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Logger.single_instance;
    }

    /**
     * Log a given string to "logs/log.txt" file.
     * Special characters will be espaced.
     *
     * @param str The string that should be logged
     */
    public void log(String str) {
        String pattern = "MM/dd/yyyy - HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        Date today = Calendar.getInstance().getTime();
        String todayAsString = df.format(today);

        try {
            this.logger.write(todayAsString + " ::: " + Logger.escape(str) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Other helpers
    /**
     * Escape a given string to make it safe to be printed or stored.
     *
     * @param s The input String.
     * @return The output String.
     */
    private static String escape(String s){
        return s.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\f", "\\f")
                .replace("\'", "\\'")
                .replace("\"", "\\\"");
    }
}
