package th.co.scb.autosale.scbastotpservice.component;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class ComponentForGenerateOtp {
    @Value("${app.plusMinutes}")
    private Integer plusMinutes;
    public  String generateRandomString() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < 6; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }
    public  int generateRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public Map<String , Object> setField(Integer limit,String otpRef , Integer otp){
        Map<String, Object> body = new HashMap<>();
        body.put("otpRef", otpRef);
        body.put("otp", String.valueOf(otp));
        body.put("otpRequestLimit", limit);
        body.put("otpWrongLimit", 0);
        body.put("otpExpired", LocalDateTime.now().plusMinutes(plusMinutes));
        return  body;
    }

}
