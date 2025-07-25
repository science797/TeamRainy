package developmentteam.teamrainy.api.events.eventbus;

public interface IListener {

    void call(Object event);

    Class<?> getTarget();

    int getPriority();

    boolean isStatic();
}
