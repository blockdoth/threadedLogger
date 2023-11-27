import ThreadedLogger.ThreadedLogger;

public class Main {




    public static void main(String[] args) {
        int totalTasks = 3;
        int threadCount = 3;
        int minTasks = 10;
        int maxTasks = 10;
        int minTimePerTask = 500;
        int maxTimePerTask = 2000;

        ThreadedLogger logger = new ThreadedLogger();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        JobRunner jobRunner = new JobRunner(logger, threadCount,minTasks, maxTasks, minTimePerTask,maxTimePerTask);
        jobRunner.runTasks(totalTasks);

    }
}
