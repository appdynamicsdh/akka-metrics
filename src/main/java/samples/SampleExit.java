package samples;/*
 * Copyright (c) AppDynamics, Inc., and its affiliates
 * 2016
 * All Rights Reserved
 * THIS IS UNPUBLISHED PROPRIETARY CODE OF APPDYNAMICS, INC.
 * The copyright notice above does not evidence any actual or intended publication of such source code
 */

import com.appdynamics.instrumentation.sdk.contexts.ISDKUserContext;
import com.appdynamics.instrumentation.sdk.template.AExit;
import com.appdynamics.instrumentation.sdk.Rule;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.IReflector;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.ReflectorException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SampleExit extends AExit {

    private IReflector reflector;

    @Override
    public void marshalTransactionContext(String transactionContext, Object invokedObject, String className,
            String methodName, Object[] paramValues, Throwable thrownException, Object returnValue,
            ISDKUserContext context)
            throws ReflectorException {

        if (reflector == null) {
            String[] types = new String[]{String.class.getCanonicalName()};
            boolean searchSuperClass = true;
            reflector = getNewReflectionBuilder().invokeInstanceMethod("setHeader", searchSuperClass, types).build();
        }

        Object payload = paramValues[0];
        try {
            reflector.execute(payload.getClass().getClassLoader(), payload, new Object[]{transactionContext});
        } catch (ReflectorException e) {
            getLogger().error("Error marshaling transaction context", e);
        }
    }

    @Override
    public Map<String, String> identifyBackend(Object invokedObject, String className,
            String methodName, Object[] paramValues, Throwable thrownException, Object returnValue,
            ISDKUserContext context) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("foo", "bar");
        return map;
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
        rules.add(new Rule.Builder("Main").methodMatchString("exit").build());
        return rules;
    }
}
