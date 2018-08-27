import java.util.HashMap;
import java.util.HashSet;
import java.util.WeakHashMap;

public class MessageCache {

    public static final WeakHashMap<Object, MailboxBean> messages = new WeakHashMap<Object,MailboxBean>();
    public static final WeakHashMap<String,Integer> runningActorMessages = new WeakHashMap<>();


}
