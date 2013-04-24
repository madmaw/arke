package arke.container.template;

import arke.Container;
import arke.Message;
import arke.Universe;

import java.util.HashMap;
import java.util.Map;

public class MessagePartTransformerUniverseAdapter implements Universe {

    private TemplateUniverse templateUniverse;

    private Map<Container, TemplateContainer> containers;
    private Map<String, MessagePartTransformer> transformers;


    public MessagePartTransformerUniverseAdapter(TemplateUniverse templateUniverse, Map<String, MessagePartTransformer> transformers) {
        this.templateUniverse = templateUniverse;
        // we only ever really expect one
        this.containers = new HashMap<Container, TemplateContainer>(1);
        this.transformers = transformers;
    }

    @Override
    public void handleMessage(Container container, Message message) throws Exception {

        TemplateContainer templateContainer = this.containers.get(container);
        if( templateContainer == null ) {
            // create a new one
            templateContainer = new MessagePartTransformerTemplateContainer(container, this.transformers);
        }

        templateUniverse.handleMessage(
                templateContainer,
                message
        );
    }
}
