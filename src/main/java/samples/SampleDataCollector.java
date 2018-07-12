package samples;/*
 * Copyright (c) AppDynamics, Inc., and its affiliates
 * 2016
 * All Rights Reserved
 * THIS IS UNPUBLISHED PROPRIETARY CODE OF APPDYNAMICS, INC.
 * The copyright notice above does not evidence any actual or intended publication of such source code
 */

import com.appdynamics.instrumentation.sdk.contexts.ISDKDataContext;
import com.appdynamics.instrumentation.sdk.template.ADataCollector;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.IReflector;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.ReflectorException;
import com.appdynamics.instrumentation.sdk.Rule;

import java.util.ArrayList;
import java.util.List;

public class SampleDataCollector extends ADataCollector {

    private IReflector reflector;

    @Override
    public List<Rule> initializeRules() {
        ArrayList<Rule> rules = new ArrayList<Rule>();
        rules.add(new Rule.Builder("Main").methodMatchString("dataCollectorTarget").build());
        return rules;
    }

    @Override
    public void storeData(Object invokedObject, String className, String methodName, Object[] paramValues,
            Throwable thrownException, Object returnValue, ISDKDataContext sdkContext) throws ReflectorException {
        getLogger().info("logging data collection");

        if (reflector == null) {
            boolean searchSuperClass = true;
            reflector = getNewReflectionBuilder().invokeInstanceMethod("getData", searchSuperClass).build();
        }

        Object payload = paramValues[0];
        String toStore = null;
        try {
            toStore = (String) reflector.execute(payload.getClass().getClassLoader(), payload);
        } catch (ReflectorException e) {
            getLogger().error("Caught exception in attempting to pull payload data", e);
        }

        sdkContext.storeData("test", toStore);
    }

    public boolean addToSnapshot() {
        return true;
    }

    public boolean addToAnalytics() {
        return true;
    }
}
