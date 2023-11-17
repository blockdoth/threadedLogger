package ThreadedLogger;


import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

class JobsPool {
    private final ConcurrentHashMap<Integer, MonitoredJob> jobsMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap.KeySetView<MonitoredJob, Boolean> allJobs = ConcurrentHashMap.newKeySet();
    private final ConcurrentMap<Long, Integer> threadIdMap = new ConcurrentHashMap<>();
    private final AtomicInteger threadIndexAtomic = new AtomicInteger(1);
    private int totalJobsCount = 0;
    private int waitingJobsCount = 0;
    private int activeJobsCount = 0;
    private int finishedJobsCount = 0;
    private long averageTime = 0;
    private int errorCount = 0;
    private int fatalErrorCount = 0;

    private final long startTime = System.currentTimeMillis();
    private boolean allJobsFinished = false;
    private long allJobsFinishedTime = 0;

    public void put(long threadID, MonitoredJob monitoredJob) {
        int threadIndex;
        if (!threadIdMap.containsKey(threadID)) {
            threadIndex = threadIndexAtomic.getAndIncrement();
            threadIdMap.put(threadID, threadIndex);
        } else {
            threadIndex = threadIdMap.get(threadID);
        }
        jobsMap.put(threadIndex, monitoredJob);
        monitoredJob.setOwnerThread(threadIndex);
        allJobs.add(monitoredJob);

    }

    public MonitoredJob get(long threadID) {
        return jobsMap.get(threadIdMap.get(threadID));
    }

    public void updateJobsInfo() {
        waitingJobsCount = 0;
        activeJobsCount = 0;
        finishedJobsCount = 0;
        errorCount = 0;
        fatalErrorCount = 0;
        long totalTime = 0;
        for (MonitoredJob job : allJobs) {
            switch (job.getJobState()) {
                case ACTIVE -> activeJobsCount++;
                case WAITING -> waitingJobsCount++;
                case FINISHED -> finishedJobsCount++;
                case FATAL_ERROR -> fatalErrorCount++;
            }
            totalTime += job.getJobTime();
            errorCount += job.getErrorCount();
        }
        if (finishedJobsCount + fatalErrorCount == allJobs.size()) {
            if (!allJobsFinished) {
                allJobsFinishedTime = System.currentTimeMillis() - startTime;
            }
            allJobsFinished = true;
        } else {
            allJobsFinished = false;
        }
        averageTime = totalTime / allJobs.size();
    }


    public void setTotalJobs(int totalJobs) {
        this.totalJobsCount = totalJobs;
    }

    public List<MonitoredJob> getJobs() {
        return jobsMap.values().stream().sorted(Comparator.comparingInt(MonitoredJob::getOwnerThread)).toList();
    }

    public int getTotalJobsCount() {
        return totalJobsCount;
    }

    public int getFinishedJobsCount() {
        return finishedJobsCount;
    }

    public int getWaitingJobsCount() {
        return waitingJobsCount;
    }

    public int getActiveJobsCount() {
        return activeJobsCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public int getFatalErrorCount() {
        return fatalErrorCount;
    }

    public long getAverageTime() {
        return averageTime;
    }

    public String getAverageTimeFormatted() {
        return MonitoredJob.formatJobTime(averageTime);

    }

    public void reportError(long threadID, String errorName) {
        MonitoredJob job = jobsMap.get(threadIdMap.get(threadID));
        job.setJobInfo(errorName);
        job.increaseErrorCount();
    }

    public void reportFatalError(long threadID, String errorName) {
        MonitoredJob job = jobsMap.get(threadIdMap.get(threadID));
        job.setJobInfo(errorName);
        job.setStateFatalError();
    }

    public String getTotalDuration() {
        if (allJobsFinished) {
            return MonitoredJob.formatJobTime(allJobsFinishedTime);
        } else {
            return MonitoredJob.formatJobTime(System.currentTimeMillis() - startTime);
        }
    }

}

