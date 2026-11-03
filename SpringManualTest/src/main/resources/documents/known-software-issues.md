
# Known Software Issues

## Overview

This document describes common software and configuration issues that engineers may encounter when integrating with the fictional payment platform.

The scenarios below are troubleshooting guides, not a record of verified production incidents. Confirm the behavior in the affected environment before concluding that a known issue is responsible.

## SDK Version Conflicts

### Symptoms

An application fails to compile, starts with runtime errors, or behaves differently after an SDK or dependency upgrade.

### Possible Causes

- Two libraries require incompatible versions of a shared dependency.
- A method or class changed between SDK versions.
- The application uses an outdated SDK with a newer API.
- Dependency resolution selects an unexpected transitive dependency.
- The deployed artifact differs from the version tested locally.

### Troubleshooting Steps

1. Record the SDK version and application version.
2. Review the dependency tree for conflicting versions.
3. Inspect the compiler or runtime error to identify the affected class or method.
4. Compare the current dependency configuration with the last working version.
5. Check the SDK's official documentation and release notes for relevant changes.
6. Confirm that the selected SDK version supports the application's Java version.
7. Rebuild the application from a clean state.
8. Run relevant automated tests before deploying the change.

### Recommended Resolution

Use a compatible set of dependency versions and follow the SDK's documented upgrade instructions. Avoid randomly changing several dependency versions at once.

If the issue began after an upgrade, reproduce it with the previous known-good version when practical. Record the version change and test results.

## Sandbox and Live API Keys

### Symptoms

An integration works in a test environment but fails in production, or requests fail after an API key is changed.

### Possible Causes

- A sandbox key is being used against a live environment.
- A live key is being used against a sandbox environment.
- The application loads the wrong environment variable.
- The key is invalid, revoked, expired, or incorrectly configured.
- The application uses an endpoint that does not match the selected environment.

### Troubleshooting Steps

1. Identify whether the application is running in sandbox or live mode.
2. Verify the configured API base URL for that environment.
3. Confirm which environment variable or secret store supplies the API key.
4. Check the authentication error returned by the API.
5. Verify the key's validity and permissions using the provider's approved procedures.
6. Check whether the deployment contains the expected environment configuration.
7. Confirm that no secret was accidentally copied into source code or logs.

### Recommended Resolution

Use the appropriate credentials and endpoint for the selected environment. Store credentials in environment variables or an approved secret-management system.

Never print complete API keys in logs, commit them to version control, or share them in incident reports. If a key may have been exposed, follow the provider's credential-rotation procedure.

## Timezone and Timestamp Issues

### Symptoms

Events appear at unexpected times, timestamps differ between services, or a date-based query returns unexpected results.

### Possible Causes

- One service stores timestamps in UTC while another assumes local time.
- A timestamp is parsed without its timezone or offset.
- The application converts the same timestamp more than once.
- Date ranges use inconsistent start or end boundaries.
- A date-only value is interpreted as a timestamp in an unintended timezone.

### Troubleshooting Steps

1. Capture the original timestamp from the API response or event.
2. Check whether it includes a timezone indicator or UTC offset.
3. Compare timestamps across the services involved in the incident.
4. Verify the timezone used when parsing, storing, and displaying the timestamp.
5. Check date-range boundaries and daylight-saving-time behavior where applicable.
6. Review recent changes to timestamp parsing or formatting.
7. Reproduce the issue using a known timestamp and an explicitly specified timezone.

### Recommended Resolution

Use a consistent timestamp convention across services. UTC is generally suitable for storing and comparing event timestamps, while display layers can convert timestamps to the user's intended timezone.

Do not remove timezone information or apply an additional conversion without understanding the timestamp's original representation.

## General Investigation and Verification

For each suspected software issue:

1. Record the observed behavior and the exact error message.
2. Identify the affected application version and environment.
3. Collect relevant logs and timestamps.
4. Compare the failing behavior with a known-working case.
5. Change one variable at a time when testing a potential fix.
6. Verify the result with a repeatable test.
7. Document the confirmed cause, if established, and the resolution.

A symptom that resembles a known issue is not sufficient evidence that the issue is the root cause. If the evidence does not establish the cause, report the remaining uncertainty and the next diagnostic action.
