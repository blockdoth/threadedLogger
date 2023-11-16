package ThreadedLogger;

public class MonitoredJob {


    private JobState jobState;
    public String jobName;
    public int taskCount;
    public int errorCount = 0;
    private int ownerThread;
    int tasksCompleted = 0;
    public String activeTask;
    private String jobInfo = "Operating Normally";
    long jobTime = 0;
    long finishTime;


    public MonitoredJob(String jobName, int taskCount) {
        this.jobName = jobName;
        this.taskCount = taskCount;
        this.jobTime = System.currentTimeMillis();
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

    public void setOwnerThread(int jobID) {
        this.ownerThread = jobID;
    }

    public int getOwnerThread() {
        return ownerThread;
    }

    public int getTasksCompleted() {
        return tasksCompleted;
    }

    public void setActiveTask(String taskName) {
        this.activeTask = taskName;
    }

    public String getActiveTask() {
        return activeTask;
    }

    public void endActiveTask() {
        tasksCompleted++;
    }

    public void setJobInfo(String jobInfo) {
        this.jobInfo = jobInfo;
    }

    public String getJobInfo() {
        return jobInfo;
    }

    public long getJobTime() {
        if(!isFinished()){
            return System.currentTimeMillis() - jobTime;
        }else{
            return jobTime;
        }
    }

    public String getJobTimeFormatted() {
        return formatJobTime(getJobTime());
    }

    public static String formatJobTime(long jobDuration){
        long millis = (jobDuration % 1000) / 10;
        long second = (jobDuration / 1000) % 60;
        long minute = (jobDuration / (1000 * 60)) % 60;

        if(minute > 0){
            return String.format("%02d:%02d.%02d", minute, second, millis);
        } else if (second > 0){
            return String.format("%02d.%02d", second, millis);
        } else{
            return String.format("0.%03d", millis);
        }

    }
}
