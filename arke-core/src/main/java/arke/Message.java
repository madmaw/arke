package arke;

import java.util.List;

public interface Message {

    public static interface Part {

        String getType();

        String getContentType();

        byte[] getPayload();
    }

    List<Part> getParts();
}
