package datastructures;

/** Utility for marking unfinished functions without throwing. */
public final class NotImplemented {
    private NotImplemented() {}

    /** Returns a dummy value and a readable tag for the GUI summary. */
    public static int intStub(String where) {
        Status.set(where);           // remember the first call in this run
        return 0;                    // harmless placeholder
    }

    /* ----- shared status so GUI can display one-line message ---------- */
    public static class Status {
        private static volatile String first = null;
        static void set(String w) { if (first == null) first = w; }
        public static String get()       { return first; }
        public static void clear()       { first = null; }
    }
}