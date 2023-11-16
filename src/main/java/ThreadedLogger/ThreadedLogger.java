package ThreadedLogger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadedLogger {


    private JobsPool jobsPool;
    private ExecutorService logThread = Executors.newSingleThreadExecutor();
    private Lock lock = new ReentrantLock();


    public ThreadedLogger() {
        jobsPool = new JobsPool();
        logThread.submit(() -> {
            try {
                LogBuilder logBuilder = new LogBuilder();
                while (true) {
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        throw new InterruptedException(e.getMessage());
                    }
                    lock.lock();
                    long startTime = System.currentTimeMillis();
                    System.out.println(logBuilder.buildLog(jobsPool));
                    long endTime = System.currentTimeMillis();
                    long timeElapsed = endTime - startTime;
                    System.out.println("Time elapsed: " + timeElapsed + "ms");
                    lock.unlock();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    public void init(String jobName, int jobID, int taskCount) {
        MonitoredJob monitoredThread = new MonitoredJob(jobName, taskCount);
        monitoredThread.setStateActive();
        jobsPool.put(jobID,monitoredThread);
    }

    public void reportTotalJobs(int totalJobs) {
        jobsPool.setTotalJobs(totalJobs);
    }

    public void endActiveTask(int jobID) {
        jobsPool.get(jobID).endActiveTask();
    }

    public void reportError(int jobID, String errorName) {
        jobsPool.reportError(jobID, errorName);
    }

    public void reportFatalError(int jobID, String errorName) {
        jobsPool.reportFatalError(jobID, errorName);
    }


    public void startNewTask(int jobID, String jobName) {
        jobsPool.get(jobID).setActiveTask(jobName);
    }

    public void setFinished(int jobID) {
        MonitoredJob job = jobsPool.get(jobID);
        job.setStateFinished();
    }
}
