package com.mbi.utils;

import java.util.Set;

/**
 * Utility that extracts the actual originating test method
 * from the call stack, even when the call passes through multiple
 * layers of infrastructure (Reflection, TestNG/JUnit runners, wrappers, etc.).
 * <p>
 * The goal:
 * - NOT the immediate caller
 * - NOT a reflection frame
 * - NOT TestNG/JUnit infrastructure
 * - BUT the deepest (last) user-level test method,
 * e.g. tests.mytest.MyTestClass.testSomething()
 */
public final class CallerResolver {

    /**
     * StackWalker with RETAIN_CLASS_REFERENCE so we can access the class safely.
     * SHOW_REFLECT_FRAMES is not needed because we manually filter reflection frames.
     */
    private static final StackWalker WALKER =
            StackWalker.getInstance(Set.of(StackWalker.Option.RETAIN_CLASS_REFERENCE));

    private CallerResolver() {
        // Utility class: prevent instantiation
    }

    /**
     * Returns the originating test method:
     * - The deepest (last) user test frame in the call stack,
     * NOT the immediate caller.
     * <p>
     * Example output:
     * tests.mytest.MyTestClass.testSomething
     * tests.accounts.MyAccountTest.init
     * <p>
     * If nothing is found, returns "unknown".
     */
    public static String getTestEntryPoint() {
        return WALKER.walk(stream ->
                stream
                        // Remove all infrastructure frames
                        .filter(stackFrame -> !isInfrastructure(stackFrame))
                        // Keep only user-level test code frames
                        .filter(CallerResolver::isUserTestCode)
                        // Get the LAST one (deepest), not first
                        .reduce((first, second) -> second)
                        // Convert to "package.Class.method"
                        .map(stackFrame -> stackFrame.getClassName() + "." + stackFrame.getMethodName())
                        .orElse("<unknown>")
        );
    }

    /**
     * Determines whether the frame belongs to infrastructure that we want to skip.
     * - Java reflection internals
     * - JDK invoke mechanisms
     * - TestNG / JUnit frames
     * - Any stack frame that is not relevant to the actual test code
     */
    /* default */
    static boolean isInfrastructure(final StackWalker.StackFrame stackFrame) {
        final var className = stackFrame.getClassName();
        return
                // JDK internal mechanisms
                className.startsWith("java.")
                        || className.startsWith("jdk.")
                        || className.startsWith("sun.reflect.")
                        || className.contains("MethodAccessor")
                        || className.contains("ConstructorAccessor")
                        || className.contains("reflect.")               // anything in the reflection pipeline
                        || className.startsWith("org.testng.")          // TestNG internals
                        || className.startsWith("org.junit.");          // JUnit internals
    }

    /**
     * Determines whether a frame belongs to user test code.
     * This logic can be extended based on the project structure.
     */
    /* default */
    static boolean isUserTestCode(final StackWalker.StackFrame stackFrame) {
        final var className = stackFrame.getClassName();
        final var methodName = stackFrame.getMethodName();

        return className.startsWith("tests.")        // default name of the test package
                || className.endsWith("Test")        // common naming convention
                || className.contains(".tests.")     // additional safeguard
                || className.contains(".test.")      // fallback for mixed structures
                || methodName.startsWith("test");    // method naming convention
    }
}
