package samples;/*
 * Copyright (c) AppDynamics, Inc., and its affiliates
 * 2016
 * All Rights Reserved
 * THIS IS UNPUBLISHED PROPRIETARY CODE OF APPDYNAMICS, INC.
 * The copyright notice above does not evidence any actual or intended publication of such source code
 */

import com.appdynamics.apm.appagent.api.AgentDelegate;
import com.appdynamics.instrumentation.sdk.template.AGenericInterceptor;
import com.appdynamics.instrumentation.sdk.Rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SampleGenericInterceptor extends AGenericInterceptor {

    public SampleGenericInterceptor() {
        super();
    }

    @Override
    public List<Rule> initializeRules() {
        List<Rule> rules = new ArrayList<Rule>();
        rules.add(new Rule.Builder("Main").methodMatchString("genericInterceptorTarget").build());
        return rules;
    }

    public Object onMethodBegin(Object invokedObject, String className, String methodName, Object[] paramValues) {

        System.out.print("In begin");
        Map<String, String> keyVsValue = new HashMap<String, String>();
        keyVsValue.put("foo", "bar");
        AgentDelegate.getMetricAndEventPublisher().publishEvent("aw snap", "INFO", "AGENT_STATUS", keyVsValue);
        return null;

    }

    public void onMethodEnd(Object state, Object invokedObject, String className, String methodName,
            Object[] paramValues, Throwable thrownException, Object returnValue) {

        System.out.print("In end");
    }

}
