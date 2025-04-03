package com.theagent.ticketgate;

/**
 * Stores a single property
 */
class GateProperty {

    private final String gateName;
    private final String propertyName;
    private final Object value;

    GateProperty(String gateName, String propertyName, Object value) {
        this.gateName = gateName;
        this.propertyName = propertyName;
        this.value = value;
    }

    /**
     * Returns the full path
     *
     * @return path (gates.gateName.propertyName)
     */
    String getPath() {
        return "gates." + gateName + "." + propertyName;
    }

    /**
     * Returns the specified value
     *
     * @return property value
     */
    Object getValue() {
        return value;
    }

}
