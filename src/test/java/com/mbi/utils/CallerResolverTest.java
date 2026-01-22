package com.mbi.utils;

import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class CallerResolverTest {

    private final List<StackWalker.StackFrame> fakeFrames = List.of(
            toFrame("jdk.internal.MethodAccessorImpl", "invoke"),
            toFrame("one.two.MethodAccessorImpl", "newInstance"),
            toFrame("one.reflect.HandleImpl", "invokeMethod"),
            toFrame("org.testng.NativeAccessorImpl", "newInstance0"),
            toFrame("org.junit.NativeAccessorImpl", "newInstance0"),
            toFrame("tests.example.MyClass", "invokeSomething"),
            toFrame("sauna.eflec.RandomClass", "execute"),
            toFrame("ajava.internal.NativeConstAccessorImpl", "constructSomething"),
            toFrame("sun.reflect.NativeConstAccessorImpl", "constructSomething"),
            toFrame("one.two.ConstructorAccessor", "constructSomething"),
            toFrame("tests.two.ConstructorAccessor", "testASasda"),
            toFrame("wow.two.ClassTest", "testASasda"),
            toFrame("wow.tests.Class", "testASasda"),
            toFrame("wow.test.Class", "testASasda"),
            toFrame("wow.twow.Class", "testASasda")
    );

    private static StackWalker.StackFrame toFrame(final String cls, final String method) {
        final StackTraceElement e = new StackTraceElement(cls, method, cls + ".java", 1);
        return new FakeFrame(e);
    }

    // ======== Positive matches ========

    @Test
    public void testIsInfrastructureIfStartsWithExpectedPackageName() {
        assertTrue(CallerResolver.isInfrastructure(fakeFrames.getFirst()));
    }

    @Test
    public void testIsInfrastructureIfContainsExpectedKey() {
        assertTrue(CallerResolver.isInfrastructure(fakeFrames.get(1)));
    }

    @Test
    public void testIsInfrastructureIfContainsReflectKey() {
        assertTrue(CallerResolver.isInfrastructure(fakeFrames.get(2)));
    }

    @Test
    public void testIsInfrastructureIfStartsWithTestNGClass() {
        assertTrue(CallerResolver.isInfrastructure(fakeFrames.get(3)));
    }

    @Test
    public void testIsInfrastructureIfStartsWithJUnitClass() {
        assertTrue(CallerResolver.isInfrastructure(fakeFrames.get(4)));
    }

    @Test
    public void testIsInfrastructureIfStartsWithSunReflect() {
        assertTrue(CallerResolver.isInfrastructure(fakeFrames.get(8)));
    }

    @Test
    public void testIsInfrastructureIfContainsConstructorAccessor() {
        assertTrue(CallerResolver.isInfrastructure(fakeFrames.get(9)));
    }

    @Test
    public void testIsTestIfStartsWithTests() {
        assertTrue(CallerResolver.isUserTestCode(fakeFrames.get(10)));
    }

    @Test
    public void testIsTestIfClassEndsWithTests() {
        assertTrue(CallerResolver.isUserTestCode(fakeFrames.get(11)));
    }

    @Test
    public void testIsTestIfClassContainsTests() {
        assertTrue(CallerResolver.isUserTestCode(fakeFrames.get(12)));
    }

    @Test
    public void testIsTestIfClassContainsTest() {
        assertTrue(CallerResolver.isUserTestCode(fakeFrames.get(13)));
    }

    // ======== Negative matches ========

    @Test
    public void testIsTestIfMethodStartsWithTest() {
        assertTrue(CallerResolver.isUserTestCode(fakeFrames.get(14)));
    }

    @Test
    public void testIsNotInfrastructureIfPackageStartsWithTests() {
        assertFalse(CallerResolver.isInfrastructure(fakeFrames.get(5)));
    }

    @Test
    public void testIsNotInfrastructureIfNoExpectedKeys() {
        assertFalse(CallerResolver.isInfrastructure(fakeFrames.get(6)));
    }

    @Test
    public void testIsNotInfrastructureIfStartsWithUnexpectedKey() {
        assertFalse(CallerResolver.isInfrastructure(fakeFrames.get(7)));
    }

    record FakeFrame(StackTraceElement element) implements StackWalker.StackFrame {

        @Override
        public String getClassName() {
            return element.getClassName();
        }

        @Override
        public String getMethodName() {
            return element.getMethodName();
        }

        @Override
        public String getFileName() {
            return element.getFileName();
        }

        @Override
        public int getLineNumber() {
            return element.getLineNumber();
        }

        @Override
        public String toString() {
            return element.toString();
        }

        @Override
        public StackTraceElement toStackTraceElement() {
            return element;
        }

        // methods that you don't need — return dummy values

        @Override
        public Class<?> getDeclaringClass() {
            return Object.class; // or null if preferred
        }

        @Override
        public int getByteCodeIndex() {
            return 0;
        }

        @Override
        public boolean isNativeMethod() {
            return false;
        }
    }
}
