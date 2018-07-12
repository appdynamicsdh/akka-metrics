/*
 * Copyright (c) AppDynamics, Inc., and its affiliates
 * 2016
 * All Rights Reserved
 * THIS IS UNPUBLISHED PROPRIETARY CODE OF APPDYNAMICS, INC.
 * The copyright notice above does not evidence any actual or intended publication of such source code
 */

import akka.actor.Stash;
import com.appdynamics.apm.appagent.api.AgentDelegate;
import com.appdynamics.instrumentation.sdk.Rule;
import com.appdynamics.instrumentation.sdk.SDKClassMatchType;
import com.appdynamics.instrumentation.sdk.template.AGenericInterceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class StashInterceptor extends AGenericInterceptor {

    public StashInterceptor() {
        super();
    }

    public static final String REGEX = "\\d+";

    @Override
    public List<Rule> initializeRules() {
        List<Rule> rules = new ArrayList<Rule>();
        rules.add(new Rule.Builder("akka.actor.Stash").methodMatchString("stash").classMatchType(SDKClassMatchType.IMPLEMENTS_INTERFACE).build());
        //rules.add(new Rule.Builder("akka.actor.Actor").methodMatchString("stash").build());
        return rules;
    }

    public Object onMethodBegin(Object invokedObject, String className, String methodName, Object[] paramValues) {

        Object actorName = getObject(invokedObject);
        int size = 0;
        try {
            Method theStash = invokedObject.getClass().getMethod("akka$actor$StashSupport$$theStash", null);
            Object scalaVector = theStash.invoke(invokedObject, null);

            Method sizeMethod = invokedObject.getClass().getMethod("size", null);
            size = (int)sizeMethod.invoke(scalaVector, null);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        if(!actorName.toString().matches(REGEX)) {
            AgentDelegate.getMetricAndEventPublisher().reportMetric("Custom Metrics|Akka|Actors|" + actorName + "|Stash Size", size, "OBSERVATION", "CURRENT", "INDIVIDUAL");
        }

        return null;
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

    }

}
