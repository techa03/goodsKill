package com.goodskill.flowable;

import org.flowable.form.api.FormInstanceInfo;
import org.flowable.form.api.FormService;
import org.flowable.form.engine.FormEngine;
import org.flowable.form.engine.test.FlowableFormExtension;
import org.flowable.form.engine.test.FormConfigurationResource;
import org.flowable.form.engine.test.FormDeploymentAnnotation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(FlowableFormExtension.class)
// In case a custom form configuration is needed the part below should be uncommented
@FormConfigurationResource("flowable.form.cfg.xml")
class MyFormDefinitionTest {

    private FormEngine formEngine;

    @BeforeEach
    void setUp(FormEngine formEngine) {
        this.formEngine = formEngine;
    }

    @Test
    @FormDeploymentAnnotation(resources = "1.form")
    void formUsageExample() {
        FormService formService = formEngine.getFormService();

        FormInstanceInfo result = formService.getFormInstanceModelById(
                "f7689f79-f1cc-11e6-8549-acde48001122", null);

        Assertions.assertNotNull(result);
    }
}