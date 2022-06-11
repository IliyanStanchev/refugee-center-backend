package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.models.User;
import bg.tuvarna.diploma_work.models.VerificationCode;
import bg.tuvarna.diploma_work.repositories.VerificationCodeRepository;
import bg.tuvarna.diploma_work.security.BCryptPasswordEncoderExtender;
import bg.tuvarna.diploma_work.utils.CharSequenceGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VerificationCodeService {

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    public VerificationCode saveVerificationCode(VerificationCode verificationCode) {

        BCryptPasswordEncoderExtender passwordEncoder = new BCryptPasswordEncoderExtender();
        verificationCode.setCode(passwordEncoder.encode( verificationCode.getCode() ));

        return verificationCodeRepository.save(verificationCode);
    }

    public String generateVerificationCode() {
        return CharSequenceGenerator.generateVerificationCode();
    }

    public void deleteVerificationCodes(Long userId) {

        verificationCodeRepository.deleteVerificationCodes(userId);
    }

    public boolean verifyVerificationCode(VerificationCode verificationCode) {

        VerificationCode baseVerificationCode = verificationCodeRepository.getVerificationCode( verificationCode.getUser().getId() );

        if( baseVerificationCode == null ) {
            return false;
        }

        BCryptPasswordEncoderExtender bCryptPasswordEncoderExtender = new BCryptPasswordEncoderExtender();
        if( !bCryptPasswordEncoderExtender.matches( verificationCode.getCode(), baseVerificationCode.getCode() ) ) {
            return false;
        }

        return true;
    }

    public void removeExpiredVerificationCodes() {

        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(10);

        List< VerificationCode > verificationCodes = verificationCodeRepository.findAll();

        for( VerificationCode verificationCode : verificationCodes ) {
            if( verificationCode.getDateTime().isBefore( localDateTime ) ) {
                verificationCodeRepository.delete(verificationCode);
            }
        }
    }
}
