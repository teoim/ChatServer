package org.mtr.web.api.utilities;

import java.sql.Timestamp;

public class Utilities {
    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
}
