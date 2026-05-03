package user_api.entity;

import java.io.Serializable;

public enum DriverStatus implements Serializable {
    AVAILABLE,
    BUSY,
    OFFLINE,
    INACTIVE;

    private static final long serialVersionUID = 1L;
}
