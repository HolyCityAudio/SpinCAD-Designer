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
    private final Map<String, int[]> classCounts = new LinkedHashMap<>(); // [passed, failed, skipped]

    @Override
    public void executionSkipped(TestIdentifier id, String reason) {
        if (id.isTest()) {
            skipped++;
            getClassCounts(id)[2]++;
        }
    }

    @Override
    public void executionFinished(TestIdentifier id, TestExecutionResult result) {
        if (!id.isTest()) return;

        switch (result.getStatus()) {
            case SUCCESSFUL:
                passed++;
                getClassCounts(id)[0]++;
                break;
            case FAILED:
                failed++;
                getClassCounts(id)[1]++;
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
                getClassCounts(id)[2]++;
                break;
        }
    }

    private int[] getClassCounts(TestIdentifier id) {
        // Extract class name from the unique ID (format: .../<className>/<testMethod>)
        String uid = id.getUniqueId();
        String className = "Unknown";
        int classStart = uid.indexOf("[class:");
        if (classStart >= 0) {
            int classEnd = uid.indexOf(']', classStart);
            if (classEnd >= 0) {
                String fqn = uid.substring(classStart + 7, classEnd);
                int dot = fqn.lastIndexOf('.');
                className = dot >= 0 ? fqn.substring(dot + 1) : fqn;
            }
        }
        return classCounts.computeIfAbsent(className, k -> new int[3]);
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

        System.out.println("  TEST CLASSES:");
        for (Map.Entry<String, int[]> entry : classCounts.entrySet()) {
            int[] c = entry.getValue();
            int classTotal = c[0] + c[1] + c[2];
            String status = c[1] > 0 ? "FAIL" : "OK";
            String detail = String.format("%d passed", c[0]);
            if (c[1] > 0) detail += String.format(", %d failed", c[1]);
            if (c[2] > 0) detail += String.format(", %d skipped", c[2]);
            System.out.printf("    %-4s %-35s %3d tests (%s)%n",
                    status, entry.getKey(), classTotal, detail);
        }
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
