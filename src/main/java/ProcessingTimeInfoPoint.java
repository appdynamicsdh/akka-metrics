/*
 * Copyright (c) AppDynamics, Inc., and its affiliates
 * 2016
 * All Rights Reserved
 * THIS IS UNPUBLISHED PROPRIETARY CODE OF APPDYNAMICS, INC.
 * The copyright notice above does not evidence any actual or intended publication of such source code
 */

import com.appdynamics.instrumentation.sdk.MetricRollupTypes;
import com.appdynamics.instrumentation.sdk.Rule;
import com.appdynamics.instrumentation.sdk.contexts.ISDKMetricContext;
import com.appdynamics.instrumentation.sdk.template.AInfoPoint;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.IReflector;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.ReflectorException;

import java.util.ArrayList;
import java.util.List;

public class ProcessingTimeInfoPoint extends AInfoPoint {


    public ProcessingTimeInfoPoint() {
        super();
        boolean searchSuperClass = true;
    }

    @Override
    public List<Rule> initializeRules() {
        List<Rule> rules = new ArrayList<Rule>();
        rules.add(new Rule.Builder("akka.actor.ActorCell").methodMatchString("receiveMessage").build());
        return rules;
    }

    @Override
    public void storeMetrics(Object invokedObject, String className, String methodName, Object[] paramValues,
            Throwable thrownException, Object returnValue, ISDKMetricContext sdkContext) throws ReflectorException {
    }
}
