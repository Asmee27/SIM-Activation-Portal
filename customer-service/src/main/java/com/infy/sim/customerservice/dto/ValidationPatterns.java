package com.infy.sim.customerservice.dto;

/** Regexes taken from the user-story acceptance criteria. */
public final class ValidationPatterns {

    /** One '@', then exactly one dot, then 2 or 3 letters (US04). */
    public static final String EMAIL = "[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+\\.[A-Za-z]{2,3}";
    public static final String EMAIL_MESSAGE = "Please enter a valid email";

    /** Alphabets only, max length 15 (US05). */
    public static final String NAME = "[A-Za-z]{1,15}";

    private ValidationPatterns() { }
}
