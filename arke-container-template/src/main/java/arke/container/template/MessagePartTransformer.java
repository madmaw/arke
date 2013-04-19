package arke.container.template;

import arke.ContainerException;
import arke.Message;

import java.util.Map;

public interface MessagePartTransformer {

    Message.Part transform(String key, Map<String, Object> toRender) throws ContainerException;
}
