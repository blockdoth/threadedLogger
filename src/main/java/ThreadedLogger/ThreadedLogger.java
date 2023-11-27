package ThreadedLogger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadedLogger {


    private final JobsPool jobsPool;
    private String globalLog = "Log";
    private final ExecutorService logThread = Executors.newSingleThreadExecutor();
    private final Lock lock = new ReentrantLock();

    /**
     * Instances a threaded logger in a separate thread, with a default update delay of 300ms
     */
    public ThreadedLogger() {
        this(300);
    }

    /**
     * Instances a threaded logger in a separate thread
     */
    public ThreadedLogger(int updateDelay) {
        jobsPool = new JobsPool();
        logThread.submit(() -> {
            try {
                LogBuilder logBuilder = new LogBuilder();
                while (true) {
                    if(jobsPool.isEmpty()){
                        Thread.sleep(100);
                        continue;
                    }
                    Thread.sleep(updateDelay);
                    lock.lock();
                    long startTime = System.currentTimeMillis();
                    System.out.println(logBuilder.buildLog(jobsPool, globalLog));
                    long endTime = System.currentTimeMillis();
                    long timeElapsed = endTime - startTime;
                    //System.out.println("Time elapsed: " + timeElapsed + "ms");
                    lock.unlock();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    /**
     * Initializes a new job, the taskcount is used to calculate the progress bar
     * @param jobName the name of the job
     * @param threadID the ID of the thread that is going to run the job
     * @param taskCount the number of tasks in the job
     */
    public void init(String jobName, long threadID, int taskCount) {
        MonitoredJob monitoredThread = new MonitoredJob(jobName, taskCount);
        monitoredThread.setStateActive();
        jobsPool.put(threadID, monitoredThread);
    }

    /**
     * Quits the logger
     */
    public void quit() {
        logThread.shutdown();
    }

    /**
     * Reports the projected total number of jobs to be run. This is used to calculate the progress bar
     * @param totalJobs the projected number of jobs to be run
     */
    public void reportTotalJobs(int totalJobs) {
        jobsPool.setTotalJobs(totalJobs);
    }


    /**
     * Reports an recoverable error that was encountered while running a task
     * @param threadID the ID of the thread that is running the task
     * @param errorInfo the name of the error that was encountered
     */
    public void reportError(long threadID, String errorInfo) {
        jobsPool.reportError(threadID, errorInfo);
    }

    /**
     * Reports a fatal error to the logger, only use this when the error prevents the task from continuing
     * @param threadID the ID of the thread that is running the task
     * @param errorInfo the name of the error that was encountered
     */
    public void reportFatalError(long threadID, String errorInfo) {
        jobsPool.reportFatalError(threadID, errorInfo);
    }


    /**
     * Starts a new task
     * @param threadID the ID of the thread that is going to run the task
     * @param taskName the name of the task that is going to be run
     */
    public void log(long threadID, String taskName) {
        jobsPool.get(threadID).setActiveTask(taskName);
    }

    /**
     * Publishes a global log that is displayed above the jobs
     */
    public void logGlobal(String globalLog) {
        this.globalLog = globalLog;
    }

    /**
     * Finishes a task and stops the timer
     * @param threadID the ID of the thread that is running the task
     */
    public void finish(long threadID) {
        jobsPool.get(threadID).setStateFinished();
    }

    public void waiting(long threadID) {
        jobsPool.get(threadID).setStateWaiting();
    }

    /**
     * Use this for convenience, the ID of the thread that is running the task is automatically retrieved
     * @return wrapper of the logger that is specific to the current thread usability
     */
    public LocalLogger getLocalLogger() {
        return new LocalLogger(this);

    }
}
