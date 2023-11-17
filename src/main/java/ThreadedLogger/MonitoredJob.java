package ThreadedLogger;

class MonitoredJob {


    public String jobName;
    public int taskCount;
    public int errorCount = 0;
    public String activeTask;
    int tasksCompleted = 0;
    long jobTime = 0;
    long finishTime;
    private JobState jobState = JobState.WAITING;
    private int ownerThread;
    private String jobInfo = "Operating Normally";


    public MonitoredJob(String jobName, int taskCount) {
        this.jobName = jobName;
        this.taskCount = taskCount;
        this.jobTime = System.currentTimeMillis();
    }

    public static String formatJobTime(long jobDuration) {
        long millis = (jobDuration % 1000) / 10;
        long second = (jobDuration / 1000) % 60;
        long minute = (jobDuration / (1000 * 60)) % 60;

        if (minute > 0) {
            return String.format("%02d:%02d.%02d", minute, second, millis);
        } else if (second > 0) {
            return String.format("%02d.%02d", second, millis);
        } else {
            return String.format("0.%03d", millis);
        }

    }

    public JobState getJobState() {
        return jobState;
    }

    public void setStateWaiting() {
        this.jobState = JobState.WAITING;
    }

    public void setStateActive() {
        this.jobState = JobState.ACTIVE;
    }

    public void setStateFatalError() {
        this.jobState = JobState.FATAL_ERROR;
        finishTime = System.currentTimeMillis();
        jobTime = finishTime - jobTime;
        if (activeTask == null) {
            activeTask = "Encountered Fatal Error";
        } else {
            activeTask = activeTask + "(Error)";
        }
    }

    public void setStateFinished() {
        this.jobState = JobState.FINISHED;
        finishTime = System.currentTimeMillis();
        jobTime = finishTime - jobTime;
        activeTask = "Finished";
    }

    public boolean isWaiting() {
        return jobState == JobState.WAITING;
    }

    public boolean isActive() {
        return jobState == JobState.ACTIVE;
    }

    public boolean isFinished() {
        return jobState == JobState.FINISHED;
    }

    public boolean encounteredFatalError() {
        return jobState == JobState.FATAL_ERROR;
    }

    public String getJobName() {
        return jobName;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void increaseErrorCount() {
        errorCount++;
    }

    public int getOwnerThread() {
        return ownerThread;
    }

    public void setOwnerThread(int jobID) {
        this.ownerThread = jobID;
    }

    public int getTasksCompleted() {
        return tasksCompleted;
    }

    public String getActiveTask() {
        return activeTask;
    }

    public void setActiveTask(String taskName) {
        setStateActive();
        this.activeTask = taskName;
        if(isActive()){
            tasksCompleted++;
        }
    }


    public String getJobInfo() {
        return jobInfo;
    }

    public void setJobInfo(String jobInfo) {
        this.jobInfo = jobInfo;
    }

    public long getJobTime() {
        if (!isFinished() && !encounteredFatalError()) {
            return System.currentTimeMillis() - jobTime;
        } else {
            return jobTime;
        }
    }

    public String getJobTimeFormatted() {
        return formatJobTime(getJobTime());
    }
}
