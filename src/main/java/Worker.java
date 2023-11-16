import ThreadedLogger.ThreadedLogger;

import java.util.List;
import java.util.Objects;

public class Worker implements Runnable {


    private ThreadedLogger logger;
    private List<String> taskList;
    private String jobName;
    private int taskCount;
    private final int minTimePerTask;
    private int maxTimePerTask;


    public Worker(ThreadedLogger logger, String jobName,  List<String> taskList, int minTimePerTask,int maxTimePerTask) {
        this.logger = logger;
        this.jobName = jobName;
        this.taskList = taskList;
        this.taskCount = taskList.size();
        this.maxTimePerTask = maxTimePerTask;
        this.minTimePerTask = minTimePerTask;
    }

    @Override
    public void run() {
        int threadId = (int) Thread.currentThread().threadId();
        logger.init(jobName, threadId, taskCount);

        if(Math.random() > 0.99){
            logger.reportFatalError(threadId, "Fatal Error ");
            throw new RuntimeException("Fatal error");
        }
        if(Math.random() > 0.90){
            logger.reportError(threadId, "Error ");
        }
        for (int i = 0; i < taskCount; i++) {
            logger.startNewTask(threadId, taskList.get(i));
            try {
                Thread.sleep(Math.min(minTimePerTask,(int) (maxTimePerTask * Math.random())));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            logger.endActiveTask(threadId);
        }
        logger.setFinished(threadId);

    }
}
