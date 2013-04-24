package arke.container.template;

import arke.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class MessagePartTransformerTemplateContainer extends AbstractTemplateContainerAdapter {

    private Map<String, MessagePartTransformer> transformers;


    public MessagePartTransformerTemplateContainer(Container container, Map<String, MessagePartTransformer> transformers) {
        super(container);
        this.transformers = transformers;
    }

    @Override
    public void sendMessage(InboundMessage sourceMessage, String[] templateNames, Map<String, Object> toRender) throws ContainerException {
        ArrayList<Message.Part> parts = toParts(templateNames, toRender);
        BaseMessage message = new BaseMessage(parts);
        this.container.sendMessage(sourceMessage, message);
    }

    @Override
    public void sendMessage(long targetUserId, String[] templateNames, Map<String, Object> toRender, boolean immediately) throws ContainerException {
        ArrayList<Message.Part> parts = toParts(templateNames, toRender);
        OutboundMessage message = new OutboundMessage(targetUserId, parts);
        this.container.sendMessage(message, immediately);
    }

    private ArrayList<Message.Part> toParts(String[] templateNames, Map<String, Object> toRender) throws ContainerException {
        ArrayList<Message.Part> parts = new ArrayList<Message.Part>(templateNames.length);
        for( String templateName : templateNames ) {
            MessagePartTransformer transformer = transformers.get(templateName);
            if( transformer != null ) {
                Message.Part part = transformer.transform(templateName, toRender);
                parts.add(part);
            } else {
                throw new ContainerException("no such template "+templateName);
            }
        }
        return parts;
    }
}
