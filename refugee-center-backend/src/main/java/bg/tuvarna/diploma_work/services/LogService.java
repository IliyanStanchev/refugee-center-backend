package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.enumerables.LogType;
import bg.tuvarna.diploma_work.models.Log;
import bg.tuvarna.diploma_work.repositories.LogRepository;
import bg.tuvarna.diploma_work.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogService {

    private static final String errorMessage        = "ERROR occurred while calling function {FUNCTION} with context {CONTEXT}";
    private static final String warningMessage      = "WARNING occurred while calling function {FUNCTION} message {MESSAGE}";
    private static final String informationMessage  = "INFORMATION {MESSAGE}";

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private UserRepository userRepository;

    public void logErrorMessage(String functionName, String context ){

        Log log = generateErrorLog( functionName, context );
        logRepository.save(log);
    }

    public void logErrorMessage(String functionName, Long context ){

        Log log = generateErrorLog( functionName, String.valueOf(context) );
        logRepository.save(log);
    }

    private Log generateErrorLog(String functionName, String context) {

        Log log = new Log();
        log.setLogType(LogType.ERROR);
        log.setTimestamp(LocalDateTime.now());

        String content = errorMessage
                .replace("{FUNCTION}", functionName)
                .replace("{CONTEXT}", context );

        log.setContent(content);

        return log;
    }

    private Log generateWarningLog(String functionName, String message ){

        Log log = new Log();
        log.setLogType(LogType.WARNING);
        log.setTimestamp(LocalDateTime.now());

        String content = warningMessage
                .replace("{FUNCTION}", functionName)
                .replace("{MESSAGE}", message );

        log.setContent(content);

        return log;
    }

    private Log generateInformationLog(String message) {

        Log log = new Log();
        log.setLogType(LogType.ERROR);
        log.setTimestamp(LocalDateTime.now());

        String content = informationMessage
                .replace("{MESSAGE}", message);

        log.setContent(content);

        return log;
    }
}
