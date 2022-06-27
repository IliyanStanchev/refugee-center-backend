package bg.tuvarna.diploma_work.models;

import bg.tuvarna.diploma_work.enumerables.LogType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "LOGS")
public class Log implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;

    private LogType logType;

    private LocalDateTime timestamp;

    @Column(length = 100000)
    private String content;

    public Log() {

    }

    public Log(LogType logType, LocalDateTime timestamp, String content, User user) {
        this.logType = logType;
        this.timestamp = timestamp;
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Log log = (Log) o;
        return Objects.equals(id, log.id) && logType == log.logType && Objects.equals(timestamp, log.timestamp) && Objects.equals(content, log.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, logType, timestamp, content);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LogType getLogType() {
        return logType;
    }

    public void setLogType(LogType logType) {
        this.logType = logType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}