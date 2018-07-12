/*
 * Copyright (c) AppDynamics, Inc., and its affiliates
 * 2016
 * All Rights Reserved
 * THIS IS UNPUBLISHED PROPRIETARY CODE OF APPDYNAMICS, INC.
 * The copyright notice above does not evidence any actual or intended publication of such source code
 */

import com.appdynamics.apm.appagent.api.AgentDelegate;
import com.appdynamics.instrumentation.sdk.MetricRollupTypes;
import com.appdynamics.instrumentation.sdk.Rule;
import com.appdynamics.instrumentation.sdk.template.AGenericInterceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MailboxDequeueInterceptor extends AGenericInterceptor {

    public MailboxDequeueInterceptor() {
        super();
    }

    @Override
    public List<Rule> initializeRules() {
        List<Rule> rules = new ArrayList<Rule>();
        rules.add(new Rule.Builder("akka.dispatch.Mailbox").methodMatchString("dequeue").build());
        return rules;
    }

    public Object onMethodBegin(Object invokedObject, String className, String methodName, Object[] paramValues) {

        return null;

    }

    public void onMethodEnd(Object state, Object invokedObject, String className, String methodName,
            Object[] paramValues, Throwable thrownException, Object returnValue) {

        Long result = 0L;

        try {
            if(returnValue!=null){


                Method method = returnValue.getClass().getMethod("message", null);
                Object key = method.invoke(returnValue,null);
                MailboxBean time = MessageCache.messages.get(key);
                MessageCache.messages.remove(key);

                Method numberOfMessagesMethod  = invokedObject.getClass().getMethod("numberOfMessages", null);
                int numberOfMessages = (int)numberOfMessagesMethod.invoke(invokedObject,null);

                if(time!=null){
                    result = System.nanoTime() - time.getTime();
                    /**
                     * Reports a generic metric value.
                     * <p>
                     * Note: metricName will <b>NOT</b> be prepended with "Custom Metrics|"
                     * @param metricName the name of the metric
                     * @param metricValue the long value of the metric
                     * @param aggregationType the string representation of aggregation type. Values allowed: [AVERAGE, ADVANCED_AVERAGE,
                     *                        SUM, OBSERVATION, OBSERVATION_FOREVERINCREASING]
                     * @param timeRollupType the string representation of time rollup type. Values allowed: [AVERAGE, SUM, CURRENT]
                     * @param clusterRollupType the string representation of cluster rollup type. Values allowed: [INDIVIDUAL, COLLECTIVE]
                     */


                    //Don't include actors with just numbers!
                    String regex = "\\d+";

                    if(!time.getSourceActor().matches(regex)) {
                        Integer sentMessages = 0;
                        if (MessageCache.runningActorMessages.containsKey(time.getSourceActor())) {
                            sentMessages = MessageCache.runningActorMessages.get(time.getSourceActor());
                            MessageCache.runningActorMessages.put(time.getSourceActor(), sentMessages + 1);
                        } else {
                            MessageCache.runningActorMessages.put(time.getSourceActor(), sentMessages);
                        }
                    }

                    if(!time.getTargetActor().matches(regex)) {

                        AgentDelegate.getMetricAndEventPublisher().reportMetric("Custom Metrics|Akka|Actors|" + time.getTargetActor() + "|Mailbox Time", result, "OBSERVATION", "CURRENT", "INDIVIDUAL");
                        AgentDelegate.getMetricAndEventPublisher().reportSumMetric("Akka|Actors|" + time.getTargetActor() + "|Mailbox Size",numberOfMessages);
                        AgentDelegate.getMetricAndEventPublisher().reportMetric("Custom Metrics|Akka|Running Actors", MessageCache.runningActorMessages.keySet().size(), "OBSERVATION", "CURRENT", "INDIVIDUAL");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
