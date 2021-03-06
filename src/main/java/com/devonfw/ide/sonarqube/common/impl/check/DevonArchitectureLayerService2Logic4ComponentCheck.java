package com.devonfw.ide.sonarqube.common.impl.check;

import org.sonar.check.Priority;
import org.sonar.check.Rule;

import com.devonfw.ide.sonarqube.common.api.JavaType;
import com.devonfw.ide.sonarqube.common.api.config.Component;

/**
 * {@link DevonArchitectureComponentCheck} verifying that the service layer of a {@link Component} may not depend on the
 * logic layer of another {@link Component}.
 */
@Rule(key = "Devon4j:C4", name = "Devon Layer Service-Logic Component Check", //
    description = "Verify that service layer does not depend logic layer of another component.", //
    priority = Priority.CRITICAL, tags = { "bug" })
public class DevonArchitectureLayerService2Logic4ComponentCheck extends DevonArchitectureComponentCheck {

  @Override
  protected String checkDependency(JavaType source, Component sourceComponent, JavaType target) {

    if (source.isLayerService() && target.isLayerLogic()) {
      return "Code from service layer of a component ('" + source.toString()
          + "') shall not depend on logic layer of a different component ('" + target.toString() + "').";
    }
    return null;
  }

}
