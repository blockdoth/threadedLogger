import ThreadedLogger.ThreadedLogger;

public class Main {




    public static void main(String[] args) {
        int totalTasks = 100;
        int threadCount = 10;
        int minTasks = 15;
        int maxTasks = 1;
        int minTimePerTask = 500;
        int maxTimePerTask = 2000;

        ThreadedLogger logger = new ThreadedLogger();
        JobRunner jobRunner = new JobRunner(logger, threadCount,minTasks, maxTasks, minTimePerTask,maxTimePerTask);
        jobRunner.runTasks(totalTasks);
    }
}
