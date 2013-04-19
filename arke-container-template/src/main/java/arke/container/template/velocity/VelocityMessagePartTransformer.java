package arke.container.template.velocity;

import arke.BaseMessage;
import arke.ContainerException;
import arke.ContentTypeUtils;
import arke.Message;
import arke.container.template.MessagePartTransformer;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class VelocityMessagePartTransformer implements MessagePartTransformer {

    private String template;
    private String mimeType;
    private String encoding;
    private Context baseContext;
    private VelocityEngine velocityEngine;

    public VelocityMessagePartTransformer(
            String template,
            String mimeType,
            String encoding,
            Context baseContext,
            VelocityEngine velocityEngine
    ) {
        this.template = template;
        this.mimeType = mimeType;
        this.encoding = encoding;
        this.baseContext = baseContext;
        this.velocityEngine = velocityEngine;
    }

    @Override
    public Message.Part transform(String key, Map<String, Object> toRender) throws ContainerException {
        StringWriter writer = new StringWriter();
        VelocityContext context = new VelocityContext(toRender, this.baseContext);

        velocityEngine.evaluate(context, writer, VelocityMessagePartTransformer.class.getSimpleName()+"-"+key, template);

        String output = writer.toString();
        try {
            byte[] payload;
            if( this.encoding != null ) {
                payload = output.getBytes(this.encoding);
            } else {
                payload = output.getBytes();
            }
            String contentType = ContentTypeUtils.toEncodedMimeType(this.mimeType, this.encoding);

            return new BaseMessage.StaticPart(
                    key,
                    contentType,
                    payload
            );
        } catch (UnsupportedEncodingException ex) {
            throw new ContainerException(this.encoding, ex);
        }
    }
}
