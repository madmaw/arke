package arke.container.jpa.data;

import arke.ContainerException;

public class ContainerDataException extends ContainerException {

    public ContainerDataException() {

    }

    public ContainerDataException(String message) {
        super(message);
    }

    public ContainerDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContainerDataException(Throwable cause) {
        super(cause);
    }
}
