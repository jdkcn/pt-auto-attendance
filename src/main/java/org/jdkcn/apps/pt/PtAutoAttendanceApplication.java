package org.jdkcn.apps.pt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PtAutoAttendanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PtAutoAttendanceApplication.class, args);
	}

}
