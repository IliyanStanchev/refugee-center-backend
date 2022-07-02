package bg.tuvarna.diploma_work.controllers;

import bg.tuvarna.diploma_work.enumerables.AccountStatusType;
import bg.tuvarna.diploma_work.enumerables.VerificationCodeType;
import bg.tuvarna.diploma_work.exceptions.CustomResponseStatusException;
import bg.tuvarna.diploma_work.exceptions.InternalErrorResponseStatusException;
import bg.tuvarna.diploma_work.models.AccountStatus;
import bg.tuvarna.diploma_work.models.Refugee;
import bg.tuvarna.diploma_work.models.User;
import bg.tuvarna.diploma_work.models.VerificationCode;
import bg.tuvarna.diploma_work.services.*;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
public class VerificationCodeController {

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private RefugeeService refugeeService;

    @Autowired
    private TwilioService twilioService;

    @Autowired
    private AccountStatusService accountStatusService;

    @Autowired
    private LogService logService;

    @PostMapping("/send-verification-code")
    @Transactional
    public ResponseEntity<Void> sendVerificationCode(@RequestBody User user) {

        Refugee refugee = refugeeService.getRefugeeByUserId(user.getId());

        if (refugee == null) {
            logService.logErrorMessage("RefugeeService::getRefugeeByUserId", user.getId());
            throw new InternalErrorResponseStatusException();
        }

        verificationCodeService.deleteVerificationCodes(user.getId());

        final String code = verificationCodeService.generateVerificationCode();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setVerificationCodeType(VerificationCodeType.AccountVerification);
        verificationCode.setUser(refugee.getUser());
        verificationCode.setCode(code);

        verificationCode = verificationCodeService.saveVerificationCode(verificationCode);
        if (verificationCode == null) {
            logService.logErrorMessage("VerificationCodeService::saveVerificationCode", user.getId());
            throw new InternalErrorResponseStatusException();
        }

        final String message = "Your verification code is: " + code + ". Verification codes last for 10 minutes.";

        if (!twilioService.sendMessage(refugee.getPhoneNumber(), message)) {
            logService.logErrorMessage("TwilioService::sendMessage", user.getId());
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/verify-verification-code")
    @Transactional
    public ResponseEntity<Void> verifyVerificationCode(@RequestBody VerificationCode verificationCode) {

        if (!verificationCodeService.verifyVerificationCode(verificationCode)) {
            throw new CustomResponseStatusException("Verification code is not valid");
        }

        Refugee refugee = refugeeService.getRefugeeByUserId(verificationCode.getUser().getId());

        AccountStatus accountStatus = refugee.getUser().getAccountStatus();
        accountStatus.setAccountStatusType(AccountStatusType.Verified);

        accountStatus = accountStatusService.updateAccountStatus(accountStatus);
        if (accountStatus == null) {
            logService.logErrorMessage("AccountStatusService::updateAccountStatus", verificationCode.getUser().getId());
            throw new InternalErrorResponseStatusException();
        }

        verificationCodeService.deleteVerificationCodes(verificationCode.getUser().getId());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/reset-password-verification")
    @Transactional
    public ResponseEntity<Void> resetPasswordVerification(@RequestBody VerificationCode verificationCode) {

        if (!verificationCodeService.verifyVerificationCode(verificationCode)) {
            throw new CustomResponseStatusException("Verification code is not valid. Please request a new one.");
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
