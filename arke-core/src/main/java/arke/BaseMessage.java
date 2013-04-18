package arke;

import java.util.List;

public class BaseMessage implements Message {

    public static class StaticPart implements Part {

        private String type;
        private String mimeType;
        private byte[] payload;

        public StaticPart(String type, String mimeType, byte[] payload) {
            this.type = type;
            this.mimeType = mimeType;
            this.payload = payload;
        }

        @Override
        public String getType() {
            return this.type;
        }

        @Override
        public String getMimeType() {
            return this.mimeType;
        }

        @Override
        public byte[] getPayload() {
            return this.payload;
        }
    }

    private List<Part> parts;

    public BaseMessage(List<Part> parts) {
        this.parts = parts;
    }

    @Override
    public List<Part> getParts() {
        return this.parts;
    }
}
