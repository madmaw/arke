package arke;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class MessageWrapper implements Message {

    private Message message;

    public MessageWrapper(Message message) {
        this.message = message;
    }

    public String getText() throws ContainerException {
        // just render it as text
        StringBuffer result = new StringBuffer();

        for( Message.Part part : message.getParts() ) {
            if( ContentTypeUtils.isTextual(part.getContentType()) ) {
                String encoding = ContentTypeUtils.extractCharset(part.getContentType());
                String s;
                if( encoding != null ) {
                    try {
                        s = new String(part.getPayload(), encoding);
                    } catch( UnsupportedEncodingException ex ) {
                        throw new ContainerException(encoding, ex);
                    }
                } else {
                    s = new String(part.getPayload());
                }
                result.append(s);
            }
        }

        return result.toString().trim();
    }

    @Override
    public List<Part> getParts() {
        return message.getParts();
    }
}
