import ThreadedLogger.ThreadedLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JobRunner {

    private ThreadedLogger logger;
    private ExecutorService executorService;
    private int maxTasks;
    private int minTasks;
    private int maxTimePerTask;
    private final int minTimePerTask;


    public JobRunner(ThreadedLogger logger, int threadCount, int minTasks, int maxTasks, int minTimePerTask , int maxTimePerTask) {
        this.logger = logger;
        this.minTasks = minTasks;
        this.maxTasks = maxTasks;
        this.minTimePerTask = minTimePerTask;
        this.maxTimePerTask = maxTimePerTask;
        executorService = Executors.newFixedThreadPool(threadCount);
    }

    public void runTasks(int totalTasks) {
        List<Worker> workers = generateRandomTasks(totalTasks);
        logger.logGlobal("Starting " + workers.size() + " jobs");
        for (Worker worker : workers) {
            executorService.submit(worker);
        }
    }

    private List<Worker> generateRandomTasks(int totalJobs) {
        List<Worker> workers = new ArrayList<>();
        logger.reportTotalJobs(totalJobs);
        for (int i = 0; i < totalJobs; i++) {
            int taskCount = Math.max(minTasks ,(int) (maxTasks * Math.random()));
            List<String> taskList = new ArrayList<>();
            for (int j = 0; j < taskCount; j++) {
                taskList.add("Task " + j);
            }
            workers.add(new Worker( logger, "Job " + i,taskList, minTimePerTask, maxTimePerTask));
        }
        return workers;
    }
}
