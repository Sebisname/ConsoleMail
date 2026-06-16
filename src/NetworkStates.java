public enum NetworkStates {
    STABLE("\033[32m"),        // Grün
    UNSTABLE("\033[33m"),      // Gelb
    VERY_UNSTABLE("\033[31m"), // Rot
    OFFLINE("\033[31m");       // Rot

    private final String color;
    private static final String RESET = "\033[0m";

    NetworkStates(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public String getReset() {
        return RESET;
    }
}