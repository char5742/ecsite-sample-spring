package com.example.ec_2024b_back.arch;

import com.example.ec_2024b_back.Ec2024bBackApplication;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

@Disabled("NPE issue with ApplicationModule metadata")
class ModularityTests {

  @Test
  void verifyModularity() {
    ApplicationModules.of(Ec2024bBackApplication.class).verify();
  }
}
