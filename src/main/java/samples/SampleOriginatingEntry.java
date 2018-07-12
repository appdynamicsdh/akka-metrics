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
import com.appdynamics.instrumentation.sdk.toolbox.reflection.ReflectorException;

import java.util.ArrayList;
import java.util.List;

public class SampleOriginatingEntry extends AEntry {

    @Override
    public String unmarshalTransactionContext(Object invokedObject, String className, String methodName,
            Object[] paramValues, ISDKUserContext context) throws ReflectorException {
        return null;
    }

    @Override
    public String getBusinessTransactionName(Object invokedObject, String className, String methodName,
            Object[] paramValues, ISDKUserContext context) throws ReflectorException {
        getLogger().info("Foobar");

        return "samples.SampleOriginatingEntry";
    }

    @Override
    public boolean isCorrelationEnabled() {
        return false;
    }

    @Override
    public boolean isCorrelationEnabledForOnMethodBegin() {
        return true;
    }

    @Override
    public List<Rule> initializeRules() {
        List<Rule> rules = new ArrayList<Rule>();
        rules.add(new Rule.Builder("Main").methodMatchString("entry").build());
        return rules;
    }
}
