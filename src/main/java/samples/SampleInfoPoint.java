package samples;/*
 * Copyright (c) AppDynamics, Inc., and its affiliates
 * 2016
 * All Rights Reserved
 * THIS IS UNPUBLISHED PROPRIETARY CODE OF APPDYNAMICS, INC.
 * The copyright notice above does not evidence any actual or intended publication of such source code
 */

import com.appdynamics.instrumentation.sdk.contexts.ISDKMetricContext;
import com.appdynamics.instrumentation.sdk.template.AInfoPoint;
import com.appdynamics.instrumentation.sdk.MetricRollupTypes;
import com.appdynamics.instrumentation.sdk.Rule;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.IReflector;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.ReflectorException;

import java.util.ArrayList;
import java.util.List;

public class SampleInfoPoint extends AInfoPoint {

    private final IReflector reflector;

    public SampleInfoPoint() {
        super();
        boolean searchSuperClass = true;
        reflector = getNewReflectionBuilder().invokeInstanceMethod("getNumericValue", searchSuperClass).build();
    }

    @Override
    public List<Rule> initializeRules() {
        List<Rule> rules = new ArrayList<Rule>();
        rules.add(new Rule.Builder("Main").methodMatchString("dataCollectorTarget").build());
        return rules;
    }

    @Override
    public void storeMetrics(Object invokedObject, String className, String methodName, Object[] paramValues,
            Throwable thrownException, Object returnValue, ISDKMetricContext sdkContext) throws ReflectorException {
        Object payload = paramValues[0];

        Object result = null;
        try {
            result = reflector.execute(payload.getClass().getClassLoader(), payload);
        } catch (ReflectorException e) {
            getLogger().error("Failed pull data collector data", e);
        }

        sdkContext.storeMetric("Average Return Value", MetricRollupTypes.AVERAGE, (Long) result);
    }
}
