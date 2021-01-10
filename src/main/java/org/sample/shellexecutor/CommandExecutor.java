package org.sample.shellexecutor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * @author levinliu
 * Created on 2021/1/10
 * (Change file header on Settings -> Editor -> File and Code Templates)
 */
public class CommandExecutor {

    public static void execute(String scriptDir, String command, int timeout, final Consumer<String> logConsumer, final Consumer<String> errLogConsumer) {
        final ProcessBuilder builder = new ProcessBuilder();
        builder.command("sh", "-c", command);
        builder.directory(new File(scriptDir));
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        final Future handler = executor.submit(() -> {
            try {
                Process process = builder.start();
                new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)).lines().forEach(logConsumer::accept);
                process.waitFor();
                return 0;
            } catch (InterruptedException | IOException e) {
                errLogConsumer.accept(error2string(e));
                return -1;
            }
        });
        try {
            handler.get(timeout, TimeUnit.MINUTES);
        } catch (InterruptedException | ExecutionException e) {
            errLogConsumer.accept(error2string(e));
        } catch (TimeoutException e) {
            errLogConsumer.accept("Stop the long running script over timeout " + timeout + " min(s) ");
            errLogConsumer.accept(error2string(e));
        } finally {
            executor.shutdownNow();
        }
    }

    private static String error2string(Exception e) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        return writer.toString();
    }
}
