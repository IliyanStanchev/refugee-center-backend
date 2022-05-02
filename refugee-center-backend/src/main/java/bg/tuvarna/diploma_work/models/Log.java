package bg.tuvarna.diploma_work.models;

import bg.tuvarna.diploma_work.enumerables.LogType;
import bg.tuvarna.diploma_work.enumerables.MessageType;
import org.springframework.lang.UsesSunHttpServer;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
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

    private String content;

    @OneToOne
    private User user;

    public Log(){

    }

    public Log(LogType logType, LocalDateTime timestamp, String content, User user) {
        this.logType = logType;
        this.timestamp = timestamp;
        this.content = content;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Log log = (Log) o;
        return Objects.equals(id, log.id) && logType == log.logType && Objects.equals(timestamp, log.timestamp) && Objects.equals(content, log.content) && Objects.equals(user, log.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, logType, timestamp, content, user);
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}