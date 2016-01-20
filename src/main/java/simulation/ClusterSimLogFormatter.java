package simulation;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ClusterSimLogFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();

        builder.append(record.getLevel());
        builder.append("\t");
        builder.append(String.format("%1$-32s", record.getLoggerName()));
        builder.append(formatMessage(record));
        builder.append("\n");

        return builder.toString();
    }
}
