import ThreadedLogger.*;

import java.util.List;
import java.util.Objects;

public class Worker implements Runnable {


    private LocalLogger logger;
    private List<String> taskList;
    private String jobName;
    private int taskCount;
    private final int minTimePerTask;
    private int maxTimePerTask;


    public Worker(ThreadedLogger logger, String jobName,  List<String> taskList, int minTimePerTask,int maxTimePerTask) {
        this.logger = logger.getLocalLogger();
        this.jobName = jobName;
        this.taskList = taskList;
        this.taskCount = taskList.size();
        this.maxTimePerTask = maxTimePerTask;
        this.minTimePerTask = minTimePerTask;
    }

    @Override
    public void run() {
        logger.init(jobName, taskCount);
        for (int i = 0; i < taskCount; i++) {
            logger.log(taskList.get(i));
            if(Math.random() > 0.999){
                logger.reportFatalError( "Fatal Error ");
                throw new RuntimeException("Encountered fatal error");
            }
            if(Math.random() > 0.99){
                logger.reportError("Encountered error ");
            }

            try {
                Thread.sleep(Math.min(minTimePerTask,(int) (maxTimePerTask * Math.random())));
            } catch (InterruptedException e) {
                logger.reportFatalError( "InterruptedException");
                throw new RuntimeException(e);
            }

        }
        logger.finish();

    }
}
