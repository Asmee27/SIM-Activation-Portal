package com.infy.sim.customerservice.service;

import java.util.Set;
import java.util.TreeSet;

/** Same list as the state drop-down in simhome.html (states and union territories). */
final class IndianStates {

    private static final Set<String> STATES = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    static {
        STATES.addAll(Set.of(
                "Andaman and Nicobar Islands", "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar",
                "Chandigarh", "Chhattisgarh", "Dadra and Nagar Haveli", "Daman and Diu", "Delhi", "Goa",
                "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka",
                "Kerala", "Lakshadweep", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram",
                "Nagaland", "Orissa", "Pondicherry", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Tripura",
                "Uttaranchal", "Uttar Pradesh", "West Bengal"));
    }

    static boolean isValid(String state) {
        return state != null && STATES.contains(state.trim());
    }

    private IndianStates() { }
}
