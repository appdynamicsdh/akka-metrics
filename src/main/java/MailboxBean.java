public class MailboxBean {
    Long time;


    String sourceActor;
    String targetActor;

    public MailboxBean(Long time, String sourceActor, String targetActor) {
        this.time = time;
        this.sourceActor = sourceActor;
        this.targetActor = targetActor;

    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
    public String getSourceActor() {
        return sourceActor;
    }

    public void setSourceActor(String sourceActor) {
        this.sourceActor = sourceActor;
    }

    public String getTargetActor() {
        return targetActor;
    }

    public void setTargetActor(String targetActor) {
        this.targetActor = targetActor;
    }

}
