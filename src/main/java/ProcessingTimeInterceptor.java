/*
 * Copyright (c) AppDynamics, Inc., and its affiliates
 * 2016
 * All Rights Reserved
 * THIS IS UNPUBLISHED PROPRIETARY CODE OF APPDYNAMICS, INC.
 * The copyright notice above does not evidence any actual or intended publication of such source code
 */

import com.appdynamics.apm.appagent.api.AgentDelegate;
import com.appdynamics.instrumentation.sdk.Rule;
import com.appdynamics.instrumentation.sdk.template.AGenericInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class ProcessingTimeInterceptor extends AGenericInterceptor {

    public ProcessingTimeInterceptor() {
        super();
    }
    public static final String REGEX = "\\d+";

    @Override
    public List<Rule> initializeRules() {
        List<Rule> rules = new ArrayList<Rule>();
        rules.add(new Rule.Builder("akka.actor.ActorCell").methodMatchString("receiveMessage").build());
        return rules;
    }

    public Object onMethodBegin(Object invokedObject, String className, String methodName, Object[] paramValues) {
        Object actorName = getObject(invokedObject);
        if(!actorName.toString().matches(REGEX)) {
            AgentDelegate.getMetricAndEventPublisher().reportSumMetric("Akka|Actors|" + actorName + "|Processed Message Count", 1);
        }

        return System.nanoTime();
    }

    private Object getObject(Object invokedObject) {

        try {

            Method getActorRef = invokedObject.getClass().getMethod("self", null);
            Object actorRef = getActorRef.invoke(invokedObject, null);

            Method getActorPath = actorRef.getClass().getMethod("path", null);
            Object actorPath = getActorPath.invoke(actorRef, null);

            Method name = actorPath.getClass().getMethod("name", null);
            return name.invoke(actorPath, null);
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }

    }

    public void onMethodEnd(Object state, Object invokedObject, String className, String methodName,
                            Object[] paramValues, Throwable thrownException, Object returnValue) {

        Object actorName = getObject(invokedObject);
        Long startTime = (Long)state;
        Long result = System.nanoTime() - startTime;
        if(!actorName.toString().matches(REGEX)) {
            AgentDelegate.getMetricAndEventPublisher().reportMetric("Custom Metrics|Akka|Actors|" + actorName + "|Processing Time", result, "OBSERVATION", "CURRENT", "INDIVIDUAL");
        }
    }

}
