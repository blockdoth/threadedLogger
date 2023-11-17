package ThreadedLogger;

import java.util.List;

class LogBuilder {


    private StringBuilder stringBuilder;
    private final String columnDelimeter = "|";
    private final int columnWidth = 17;
    private final int doubleColumnWidth = 2 * columnWidth;
    private final int tripleColumnWidth = 2 * columnWidth;
    private final int totalWidth =  columnWidth + 4 * doubleColumnWidth + tripleColumnWidth + 7;

    private int prevLogLength = totalWidth * 9;

    public String buildLog(JobsPool jobsPool, String globalLog) {
        stringBuilder = new StringBuilder(prevLogLength);
        jobsPool.updateJobsInfo();
        addSpaces(10);
        addGlobalLogging(globalLog);
        addHeaders();
        List<MonitoredJob> jobList = jobsPool.getJobs();
        for (MonitoredJob job : jobList) {
            try {
                addRow(job);
            } catch (Exception e) {
                System.out.println("Error while adding row: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        addGeneralInfo(jobsPool);
        prevLogLength = stringBuilder.length();
        return stringBuilder.toString();
    }


    private void addGlobalLogging(String globalLog) {
        appendLine(totalWidth);
        append(centerBetween(globalLog, columnDelimeter, totalWidth - 2));
        append("|\n");
    }

    public void addHeaders() {
        appendLine(totalWidth);
        append(centerBetween("Thread number", columnDelimeter, columnWidth));
        append(centerBetween("Job name", columnDelimeter, doubleColumnWidth));
        append(centerBetween("Progress", columnDelimeter, tripleColumnWidth));
        append(centerBetween("Active task", columnDelimeter, doubleColumnWidth));
        append(centerBetween("Time", columnDelimeter, doubleColumnWidth));
        append(centerBetween("Info", columnDelimeter, doubleColumnWidth));
        append("|\n");
        appendLine(totalWidth);

    }

    public void addRow(MonitoredJob job) {
        append(centerBetween("Thread " + job.getOwnerThread(), columnDelimeter, columnWidth));
        append(centerBetween(job.getJobName(), columnDelimeter, doubleColumnWidth));
        append(centerBetween(progressBar(job.getTasksCompleted(), job.getTaskCount(), tripleColumnWidth - 8), columnDelimeter, tripleColumnWidth));
        append(centerBetween(job.getActiveTask(), columnDelimeter, doubleColumnWidth));
        append(centerBetween(job.getJobTimeFormatted(), columnDelimeter, doubleColumnWidth));
        append(centerBetween(job.getJobInfo(), columnDelimeter, doubleColumnWidth));
        append("|\n");
    }

    public void addGeneralInfo(JobsPool jobsPool) {
        appendLine(totalWidth);
        append(centerBetween("General Info", columnDelimeter, columnWidth));
        append(centerBetween("Finished/Total jobs: " + jobsPool.getFinishedJobsCount() + "/" + jobsPool.getTotalJobsCount(), columnDelimeter, doubleColumnWidth));
        append(centerBetween(progressBar(jobsPool.getFinishedJobsCount(), jobsPool.getTotalJobsCount(), doubleColumnWidth - 8), columnDelimeter, doubleColumnWidth));
        append(centerBetween("Threads Waiting: " + jobsPool.getWaitingJobsCount(), columnDelimeter, doubleColumnWidth));
        append(centerBetween("Average/Total: " + jobsPool.getAverageTimeFormatted() + "/" + jobsPool.getTotalDuration(), columnDelimeter, doubleColumnWidth));
        append(centerBetween("Fatal Errors/Errors: " + jobsPool.getFatalErrorCount() + "/" + jobsPool.getErrorCount(), columnDelimeter, doubleColumnWidth));
        append("|\n");
        appendLine(totalWidth);
    }

    private String progressBar(int finished, int totalCount, int columnWidth) {
        StringBuilder progressBarStringBuilder = new StringBuilder(columnWidth);
        float progress = Math.min(1.0f,(float) finished / (float) totalCount);
        int barLength = columnWidth - 5;
        int barProgress = (int) (progress * barLength);
        String percentangeString;
        if (progress < 1) {
            percentangeString = String.valueOf(progress * 100);
            percentangeString = percentangeString.substring(0, Math.min(percentangeString.length(), 4)) + "% ";
        } else {
            percentangeString = "100% ";
        }

        progressBarStringBuilder.append(percentangeString);
        progressBarStringBuilder.append("[");
        for (int i = 0; i < barLength; i++) {
            if (i == barProgress) {
                progressBarStringBuilder.append(">");
            } else if (i < barProgress) {
                progressBarStringBuilder.append("#");
            } else {
                progressBarStringBuilder.append("-");
            }
        }
        progressBarStringBuilder.append("]");

        //progressBarStringBuilder.append(finished + "/" + totalCount);
        return progressBarStringBuilder.toString();
    }


    private String centerBetween(String string, String delimiter, int width) {
        if (string == null || delimiter == null) {
            throw new IllegalArgumentException("String and delimiter cannot be null");
        }
        int stringLength = string.length();

        if (stringLength > width) {
            string = string.substring(0, width - 3) + "...";
        }

        String centeredStringBuilder = delimiter +
                " ".repeat(Math.max(0, (width - stringLength) / 2)) +
                string +
                " ".repeat(Math.max(0, (width - stringLength + 1) / 2));
        return centeredStringBuilder;
    }

    public void appendLine(int length) {
        for (int i = 0; i < length; i++) {
            append("=");
        }
        append("\n");
    }

    public void addSpaces(int count) {
        for (int i = 0; i < count; i++)
            append("\n");
    }

    public void append(String string) {
        stringBuilder.append(string);
    }

}
