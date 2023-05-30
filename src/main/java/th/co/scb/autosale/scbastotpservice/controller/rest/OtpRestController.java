package th.co.scb.autosale.scbastotpservice.controller.rest;


import org.springframework.web.bind.annotation.*;
import th.co.scb.autosale.common.commonmodel.reponse.ResponseModel;
import th.co.scb.autosale.scbastotpservice.model.MobileNumber;
import th.co.scb.autosale.scbastotpservice.model.OtpModel;
import th.co.scb.autosale.scbastotpservice.service.OtpService;

@RestController
@RequestMapping("/scb-ast-otp-service/api/v1")
public class OtpRestController {

    private final OtpService otpService;

    public OtpRestController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/request")
    public ResponseModel<OtpModel> getOtp(@RequestBody MobileNumber mobileNumber) {
        return otpService.getOtp(mobileNumber);
    }

    @PostMapping("/validate")
    public ResponseModel<Void> validateOtp(@RequestBody OtpModel otpModel) {
        return otpService.validateOtp(otpModel);
    }

    @GetMapping("/delete")
    public ResponseModel<String> getById() {
        return otpService.deleteByUserCode();
    }
}
