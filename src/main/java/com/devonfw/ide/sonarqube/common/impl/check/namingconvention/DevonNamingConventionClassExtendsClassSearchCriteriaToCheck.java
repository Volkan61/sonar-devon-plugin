package com.devonfw.ide.sonarqube.common.impl.check.namingconvention;

import org.sonar.check.Priority;
import org.sonar.check.Rule;

@Rule(key = "Devon4j:E11", name = "Devon naming convention of inheriting classes (SearchCriteriaTo)", //
    description = "Verify that Classes extending AbstractSearchCriteriaTo shall end with SearchCriteriaTo. "
        + "In addition, classes that inherit from a class with the suffix SearchCriteriaTo must also "
        + "have SearchCriteriaTo as their suffix.", //
    priority = Priority.CRITICAL, tags = { "bug" })

public class DevonNamingConventionClassExtendsClassSearchCriteriaToCheck
    extends DevonNamingConventionClassExtendsClassCheck {

  public DevonNamingConventionClassExtendsClassSearchCriteriaToCheck() {

    super("AbstractSearchCriteriaTo", "SearchCriteriaTo$");
  }

}
