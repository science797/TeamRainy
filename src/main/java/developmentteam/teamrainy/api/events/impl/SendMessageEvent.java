package developmentteam.teamrainy.api.events.impl;

import developmentteam.teamrainy.api.events.Event;

public class SendMessageEvent extends Event {

    public String message;
    public final String defaultMessage;

    public SendMessageEvent(String message) {
        super(Stage.Pre);
        this.defaultMessage = message;
        this.message = message;
    }
}