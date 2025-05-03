package com.example.ec_2024b_back.arch;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.jmolecules.archunit.JMoleculesArchitectureRules;
import org.jmolecules.archunit.JMoleculesDddRules;

@AnalyzeClasses(packages = "com.example.ec_2024b_back")
class JMoleculesRulesUnitTest {

  @ArchTest ArchRule dddRules = JMoleculesDddRules.all();
  @ArchTest ArchRule onion = JMoleculesArchitectureRules.ensureOnionSimple();
}
