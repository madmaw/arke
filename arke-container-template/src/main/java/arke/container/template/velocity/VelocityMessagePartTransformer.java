package arke.container.template.velocity;

import arke.BaseMessage;
import arke.ContainerException;
import arke.ContentTypeUtils;
import arke.Message;
import arke.container.template.MessagePartTransformer;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.logging.Logger;

public class VelocityMessagePartTransformer implements MessagePartTransformer {

    private Logger LOG = Logger.getLogger(VelocityMessagePartTransformer.class.getName());

    private String templateString;
    private Template template;
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
        this.templateString = template;
        this.mimeType = mimeType;
        this.encoding = encoding;
        this.baseContext = baseContext;
        this.velocityEngine = velocityEngine;
    }

    public VelocityMessagePartTransformer(
            Template template,
            String mimeType,
            String encoding,
            Context baseContext
    ) {
        this.template = template;
        this.mimeType = mimeType;
        this.encoding = encoding;
        this.baseContext = baseContext;
    }

    @Override
    public Message.Part transform(String key, Map<String, Object> toRender) throws ContainerException {
        StringWriter writer = new StringWriter();
        VelocityContext context = new VelocityContext(toRender, this.baseContext);
        if( template == null ) {
            velocityEngine.evaluate(context, writer, VelocityMessagePartTransformer.class.getSimpleName()+"-"+key, templateString);
        } else {
            template.merge(context, writer);
        }


        String output = writer.toString();

        if( template == null ) {
            LOG.info("template "+templateString+" became "+output);
        } else {
            LOG.info("template "+template +" became "+output);
        }

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
