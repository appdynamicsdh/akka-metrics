/*
 * Copyright (c) AppDynamics, Inc., and its affiliates
 * 2016
 * All Rights Reserved
 * THIS IS UNPUBLISHED PROPRIETARY CODE OF APPDYNAMICS, INC.
 * The copyright notice above does not evidence any actual or intended publication of such source code
 */

import com.appdynamics.apm.appagent.api.AgentDelegate;
import com.appdynamics.instrumentation.sdk.SDKClassMatchType;
import com.appdynamics.instrumentation.sdk.template.AGenericInterceptor;
import com.appdynamics.instrumentation.sdk.Rule;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MailboxEnqueueInterceptor extends AGenericInterceptor {

    public MailboxEnqueueInterceptor() {
        super();
    }

    @Override
    public List<Rule> initializeRules() {
        List<Rule> rules = new ArrayList<Rule>();
        rules.add(new Rule.Builder("akka.dispatch.Mailbox").methodMatchString("enqueue").build());
        return rules;
    }

    public Object onMethodBegin(Object invokedObject, String className, String methodName, Object[] paramValues) {
        try {
            if(paramValues[1]!=null) {

                Method getActorCell = invokedObject.getClass().getMethod("actor", null);
                Object actorCell = getActorCell.invoke(invokedObject, null);

                Method getActorRef = actorCell.getClass().getMethod("self", null);
                Object actorRef = getActorRef.invoke(actorCell, null);

                Method getActorPath = actorRef.getClass().getMethod("path", null);
                Object actorPath = getActorPath.invoke(actorRef, null);

                Method name = actorPath.getClass().getMethod("name", null);
                Object actorName = name.invoke(actorPath, null);

                Method message = paramValues[1].getClass().getMethod("message", null);

                Method sourceActorRef = paramValues[0].getClass().getMethod("path", null);
                Object sourceActorPath = sourceActorRef.invoke(paramValues[0], null);
                Method sourceName = sourceActorPath.getClass().getMethod("name", null);
                Object sourceActorName = sourceName.invoke(sourceActorPath, null);

                String regex = "\\d+";

                if(!sourceActorName.toString().matches(regex)) {
                    AgentDelegate.getMetricAndEventPublisher().reportSumMetric("Akka|Actors|" + sourceActorName + "|Sent Message Count",1);
                }

                //Don't include actors with just numbers!
                if (!actorName.toString().matches(regex)) {
                    MessageCache.messages.put(message.invoke(paramValues[1], null), new MailboxBean(System.nanoTime(), sourceActorName.toString(), actorName.toString()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;

    }

    public void onMethodEnd(Object state, Object invokedObject, String className, String methodName,
            Object[] paramValues, Throwable thrownException, Object returnValue) {

    }

}
