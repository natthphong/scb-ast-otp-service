package th.co.scb.autosale.scbastotpservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "th.co.scb.autosale")
public class ScbAstOtpServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScbAstOtpServiceApplication.class, args);
	}

}
