package arke;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class MessageWrapper implements Message {

    private static final String DEFAULT_CHARSET = "utf-8";


    public static final String toString(List<Message.Part> parts) throws ContainerException {
        return toString(parts, " ");
    }

    public static final String toString(List<Message.Part> parts, String separator ) throws ContainerException {
        StringBuffer result = new StringBuffer();
        boolean first = true;

        for( Message.Part part : parts ) {
            if( ContentTypeUtils.isTextual(part.getContentType()) ) {
                String encoding = ContentTypeUtils.extractCharset(part.getContentType());
                String s;
                byte[] payload = part.getPayload();
                if( payload != null ) {
                    if( encoding != null ) {
                        try {
                            s = new String(payload, encoding);
                        } catch( UnsupportedEncodingException ex ) {
                            throw new ContainerException(encoding, ex);
                        }
                    } else {
                        s = new String(payload);
                    }
                    if( first ) {
                        first = false;
                    } else {
                        if( separator != null ) {
                            result.append(separator);
                        }
                    }
                    result.append(s.trim());
                }
            }
        }

        return result.toString().trim();

    }

    private Message message;

    public MessageWrapper(Message message) {
        this.message = message;
    }

    public String getText() throws ContainerException {
        // just render it as text
        return toString(message.getParts());
    }

    @Override
    public List<Part> getParts() {
        return message.getParts();
    }

    public void setPlainTextPart(String type, String value) throws ContainerException {
        String charset = DEFAULT_CHARSET;
        try {
            Part part = new BaseMessage.StaticPart(type, ContentTypeUtils.toTextPlainMimeType(charset), value.getBytes(charset));
            setPart(type, part);
        } catch( UnsupportedEncodingException ex ) {
            throw new ContainerDataException(charset, ex);
        }
    }

    public String getTextPart(String type) throws ContainerException {
        Integer index = indexOf(type);
        if( index != null ) {
            List<Part> parts = this.getParts();
            Part part = parts.get(index);
            String mimeType = ContentTypeUtils.extractMimeType(part.getContentType());
            if( ContentTypeUtils.isTextual(mimeType) ) {
                String encoding = ContentTypeUtils.extractCharset(part.getContentType());
                String result;
                if( encoding == null ) {
                    result = new String(part.getPayload());
                } else {
                    try {
                        result = new String(part.getPayload(), encoding);
                    } catch( UnsupportedEncodingException ex ) {
                        throw new ContainerException(encoding, ex);
                    }
                }
                return result;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    public boolean removePart(String type) {
        Integer index = this.indexOf(type);
        if( index != null ) {
            List<Part> parts = this.getParts();
            parts.remove(index);
            return true;
        } else {
            return false;
        }
    }

    public void setPart(String type, Part toSet) {
        List<Part> parts = this.getParts();
        Integer index = this.indexOf(type);
        if( index != null ) {
            parts.set(index, toSet);
        } else {
            parts.add(toSet);
        }
    }

    public Integer indexOf(String type) {
        List<Part> parts = this.getParts();
        Integer index = null;
        for( int i=parts.size(); i>0; ) {
            i--;
            Part part = parts.get(i);
            if( type.equals( part.getType() ) ) {
                index = i;
                break;
            }
        }
        return index;
    }

}
