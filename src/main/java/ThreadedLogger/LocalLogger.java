package ThreadedLogger;

public class LocalLogger {


    private ThreadedLogger threadedLogger;
    private long threadID;

    public LocalLogger(ThreadedLogger threadedLogger) {
        this.threadedLogger = threadedLogger;
    }

    /**
     * Initializes a new job, the taskcount is used to calculate the progress bar
     * @param jobName the name of the job
     * @param taskCount the number of tasks in the job
     */
    public void init(String jobName, int taskCount) {
        this.threadID = Thread.currentThread().threadId();
        threadedLogger.init(jobName, threadID, taskCount);
    }

    /**
     * Reports an recoverable error that was encountered while running a task
     * @param errorInfo the name of the error that was encountered
     */
    public void reportError(String errorInfo) {
        threadedLogger.reportError(threadID, errorInfo);
    }

    /**
     * Reports a fatal error to the logger, only use this when the error prevents the task from continuing
     * @param errorInfo the name of the error that was encountered
     */
    public void reportFatalError(String errorInfo) {
        threadedLogger.reportFatalError(threadID, errorInfo);
    }

    /**
     * Starts a new task
     * @param taskName the name of the task that is going to be run
     */
    public void log(String taskName) {
        threadedLogger.log(threadID, taskName);
    }

    public void waiting(){
        threadedLogger.waiting(threadID);
    }

    /**
     * Finishes a task and stops the timer
     */
    public void finish() {
        threadedLogger.finish(threadID);
    }

    /**
     * Quits the global logger
     */
    public void quit() {
        threadedLogger.quit();
    }
}
