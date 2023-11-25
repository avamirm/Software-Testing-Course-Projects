package exceptions;

import static defines.Errors.RATE_OUT_OF_RANGE;

public class RateOutOfRange extends Exception {
    public RateOutOfRange() {
        super(RATE_OUT_OF_RANGE);
    }
}
