package th.co.scb.autosale.scbastotpservice.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import th.co.scb.autosale.common.commonmodel.reponse.ResponseModel;
import th.co.scb.autosale.common.commonmodel.token.JwtBodyModel;
import th.co.scb.autosale.scbastotpservice.component.ComponentForGenerateOtp;
import th.co.scb.autosale.scbastotpservice.model.MobileNumber;
import th.co.scb.autosale.scbastotpservice.model.OtpModel;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@Service
@Slf4j
public class OtpService {
    @Value("${app.limit}")
    private Integer limit;

    @Value("${app.wrongLimit}")
    private Integer wrongLimit;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ComponentForGenerateOtp componentForGenerateOtp;

    public OtpService(RedisTemplate<String, Object> redisTemplate, ComponentForGenerateOtp componentForGenerateOtp) {
        this.redisTemplate = redisTemplate;
        this.componentForGenerateOtp = componentForGenerateOtp;
    }

    public ResponseModel<OtpModel> getOtp(MobileNumber mobileNumber) {
        ResponseModel<OtpModel> responseModel = ResponseModel.success();
        JwtBodyModel authenticatedUsers = (JwtBodyModel) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        String userCode = "OTP:" + authenticatedUsers.getUser().getUserCode();
        try {
            Integer otpRequestLimit = (Integer) this.redisTemplate.opsForHash().get(userCode, "otpRequestLimit");
            int indexOtpRequestLimit = otpRequestLimit == null ? 1 : otpRequestLimit + 1;
            if (indexOtpRequestLimit > limit) {
                responseModel.setCode("E0007");
                responseModel.setBody(null);
                responseModel.setTitle(null);
                responseModel.setMessage(null);
                return responseModel;
            }

            String otpRef = this.componentForGenerateOtp.generateRandomString();
            Integer otp = this.componentForGenerateOtp.generateRandomNumber(100000, 999999);
            OtpModel otpModel = new OtpModel();
            otpModel.setOtp(String.valueOf(otp));
            otpModel.setOtpRef(otpRef);
            responseModel.setBody(otpModel);
            redisTemplate.opsForHash().putAll(userCode, this.componentForGenerateOtp.setField(indexOtpRequestLimit, otpRef, otp));
        } catch (Exception ex) {
            responseModel.setCode("9999");
            responseModel.setBody(null);
            responseModel.setTitle(null);
            responseModel.setMessage(null);
            return responseModel;
        }
        return responseModel;
    }


    public ResponseModel<String> deleteByUserCode() {
        ResponseModel<String> responseModel = ResponseModel.success();
        JwtBodyModel authenticatedUsers = (JwtBodyModel) SecurityContextHolder.getContext().getAuthentication().getCredentials();

        String userCode = "OTP:" + authenticatedUsers.getUser().getUserCode();


        try {
            this.redisTemplate.opsForHash().entries(userCode)
                    .keySet()
                    .forEach(haskKey -> redisTemplate.opsForHash().delete(userCode, haskKey));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return responseModel;
    }

    public ResponseModel<Void> validateOtp(OtpModel otpModel) {

        ResponseModel<Void> responseModel = ResponseModel.success();
        JwtBodyModel authenticatedUsers = (JwtBodyModel) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        String userCode = "OTP:" + authenticatedUsers.getUser().getUserCode();
        try {
            Map<Object, Object> otpRequestLimit = this.redisTemplate.boundHashOps(userCode).entries();
            Integer wrong = (Integer) otpRequestLimit.get("otpWrongLimit");
            String otp = String.valueOf(otpRequestLimit.get("otp"));
            String otpRef = (String) otpRequestLimit.get("otpRef");

            //TODO VALIDATE
            if (LocalDateTime.now().isAfter((ChronoLocalDateTime<?>) otpRequestLimit.get("otpExpired"))) {
                responseModel.setCode("E0008");
                responseModel.setBody(null);
                responseModel.setTitle(null);
                responseModel.setMessage(null);

            } else if (wrong > wrongLimit) {
                responseModel.setCode("E0009");
                responseModel.setBody(null);
                responseModel.setTitle(null);
                responseModel.setMessage(null);
                log.info(" otpWrongLimit > 3 {}", otpRequestLimit.get("otpWrongLimit"));
            } else if (!otp.equals(otpModel.getOtp()) ||
                    !otpRef.equals(otpModel.getOtpRef())
            ) {
                responseModel.setCode("E00010");
                responseModel.setBody(null);
                responseModel.setTitle(null);
                responseModel.setMessage(null);
                wrong += 1;
                this.redisTemplate.opsForHash().put(userCode, "otpWrongLimit", wrong);
            }
//             else {
//                this.redisTemplate.opsForHash().entries(userCode)
//                        .keySet()
//                        .forEach(haskKey -> redisTemplate.opsForHash().delete(userCode, haskKey));
//            }


        } catch (Exception ex) {
            ex.printStackTrace();
            responseModel.setCode("9999");
            responseModel.setBody(null);
            responseModel.setTitle(null);
            responseModel.setMessage(null);
            return responseModel;
        }
        return responseModel;
    }
}
