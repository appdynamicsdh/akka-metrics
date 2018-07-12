package samples;/*
 * Copyright (c) AppDynamics, Inc., and its affiliates
 * 2016
 * All Rights Reserved
 * THIS IS UNPUBLISHED PROPRIETARY CODE OF APPDYNAMICS, INC.
 * The copyright notice above does not evidence any actual or intended publication of such source code
 */

import com.appdynamics.instrumentation.sdk.contexts.ISDKUserContext;
import com.appdynamics.instrumentation.sdk.template.AEntry;
import com.appdynamics.instrumentation.sdk.Rule;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.IReflector;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.ReflectorException;

import java.util.ArrayList;
import java.util.List;

public class SampleContinuingEntry extends AEntry {

    private IReflector reflector;

    public SampleContinuingEntry() {
        super();
    }

    @Override
    public String unmarshalTransactionContext(Object invokedObject, String className, String methodName,
            Object[] paramValues, ISDKUserContext context) throws ReflectorException {

        if (reflector == null) {
            boolean searchSuperClass = true;
            reflector = getNewReflectionBuilder().accessFieldValue("header", searchSuperClass).build();
        }

        Object payload = paramValues[0];
        try {
            return (String) reflector.execute(payload.getClass().getClassLoader(), payload);
        } catch (ReflectorException e) {
            getLogger().error("Caught reflector exception", e);
            return null;
        }
    }

    @Override
    public String getBusinessTransactionName(Object invokedObject, String className,
            String methodName, Object[] paramValues, ISDKUserContext context) throws ReflectorException {
        return "samples.SampleContinuingEntry";
    }

    @Override
    public boolean isCorrelationEnabled() {
        return true;
    }

    @Override
    public boolean isCorrelationEnabledForOnMethodBegin() {
        return true;
    }

    @Override
    public List<Rule> initializeRules() {
        List<Rule> rules = new ArrayList<Rule>();
        rules.add(new Rule.Builder("Main").methodMatchString("continuingEntry").build());
        return rules;
    }
}
