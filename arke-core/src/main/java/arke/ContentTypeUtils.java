package arke;

public class ContentTypeUtils {

    public static final String SEPARATOR = ";";
    public static final String EQUALS = "=";
    public static final String ATTRIBUTE_CHARSET= "charset";
    public static final String MIME_TYPE_TEXT_PLAIN = "text/plain";
    public static final String MIME_TYPE_TEXT_PREFIX = "text/";

    public static final String extractMimeType(String contentType) {
        int index = contentType.indexOf(SEPARATOR);
        if( index >= 0 ) {
            return contentType.substring(0, index);
        } else {
            return contentType;
        }
    }

    public static final String extractCharset(String contentType) {
        return extractAttribute(contentType, ATTRIBUTE_CHARSET);
    }

    public static final String extractAttribute(String contentType, String key) {
        String prefix = SEPARATOR + key + EQUALS;
        int index = contentType.indexOf(prefix);
        if( index >= 0 ) {
            int next = contentType.indexOf(SEPARATOR, index + prefix.length());
            if( next >= index ) {
                return contentType.substring(index + prefix.length(), next);
            } else {
                return contentType.substring(index + prefix.length());
            }
        } else {
            return null;
        }
    }

    public static final String toEncodedMimeType(String mimeType, String encoding) {
        StringBuffer result = new StringBuffer(mimeType);
        if( encoding != null ) {
            result.append(SEPARATOR);
            result.append(ATTRIBUTE_CHARSET);
            result.append(EQUALS);
            result.append(encoding);
        }
        return result.toString();
    }

    public static final String toTextPlainMimeType(String charset) {
        StringBuffer result = new StringBuffer(MIME_TYPE_TEXT_PLAIN);
        if( charset != null ) {
            result.append(SEPARATOR);
            result.append(ATTRIBUTE_CHARSET);
            result.append(EQUALS);
            result.append(charset);
        }
        return result.toString();
    }

    public static final boolean isTextual(String contentType) {
        String mimeType = extractMimeType(contentType);
        return ( mimeType != null && mimeType.startsWith(MIME_TYPE_TEXT_PREFIX) );
    }
}
