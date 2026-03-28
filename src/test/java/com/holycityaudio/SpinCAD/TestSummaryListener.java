package com.holycityaudio.SpinCAD;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

/**
 * JUnit TestExecutionListener that prints a summary at the end of the test run:
 * total files tested, passed, failed, skipped, and unique exception types.
 */
public class TestSummaryListener implements TestExecutionListener {

    private int passed = 0;
    private int failed = 0;
    private int skipped = 0;
    private final List<String> failedTests = new ArrayList<>();
    private final Map<String, List<String>> exceptionsByType = new LinkedHashMap<>();

    @Override
    public void executionSkipped(TestIdentifier id, String reason) {
        if (id.isTest()) {
            skipped++;
        }
    }

    @Override
    public void executionFinished(TestIdentifier id, TestExecutionResult result) {
        if (!id.isTest()) return;

        switch (result.getStatus()) {
            case SUCCESSFUL:
                passed++;
                break;
            case FAILED:
                failed++;
                String displayName = id.getDisplayName();
                failedTests.add(displayName);

                result.getThrowable().ifPresent(t -> {
                    // Walk to the root cause
                    Throwable cause = t;
                    while (cause.getCause() != null && cause.getCause() != cause) {
                        cause = cause.getCause();
                    }
                    String exType = cause.getClass().getSimpleName();
                    exceptionsByType
                            .computeIfAbsent(exType, k -> new ArrayList<>())
                            .add(displayName);
                });
                break;
            case ABORTED:
                skipped++;
                break;
        }
    }

    @Override
    public void testPlanExecutionFinished(TestPlan plan) {
        int total = passed + failed + skipped;

        System.out.println();
        System.out.println("===================================================");
        System.out.println("  TEST SUMMARY");
        System.out.println("===================================================");
        System.out.printf("  Total:   %d%n", total);
        System.out.printf("  Passed:  %d%n", passed);
        System.out.printf("  Failed:  %d%n", failed);
        System.out.printf("  Skipped: %d%n", skipped);
        System.out.println("---------------------------------------------------");

        if (!failedTests.isEmpty()) {
            System.out.println("  FAILED TESTS:");
            for (String name : failedTests) {
                System.out.println("    - " + name);
            }
            System.out.println("---------------------------------------------------");
        }

        if (!exceptionsByType.isEmpty()) {
            System.out.println("  UNIQUE EXCEPTION TYPES:");
            for (Map.Entry<String, List<String>> entry : exceptionsByType.entrySet()) {
                System.out.printf("    %s (%d):%n", entry.getKey(), entry.getValue().size());
                for (String test : entry.getValue()) {
                    System.out.println("      - " + test);
                }
            }
            System.out.println("---------------------------------------------------");
        }

        System.out.println("===================================================");
        System.out.println();
    }
}
