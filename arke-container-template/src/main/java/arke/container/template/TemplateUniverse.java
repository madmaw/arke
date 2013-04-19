package arke.container.template;

import arke.Container;
import arke.Message;

public interface TemplateUniverse {
    void handleMessage(TemplateContainer container, Message message) throws Exception;
}
