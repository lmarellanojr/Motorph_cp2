# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

---

## Build & Run

All commands require full paths — neither `mvn` nor the correct JDK are on PATH.

```bash
MVN=~/.local/share/JetBrains/Toolbox/apps/intellij-idea/plugins/maven/lib/maven3/bin/mvn
JAVA=/usr/lib/jvm/jdk-25.0.1+8/bin/java   # Temurin — required; system JDK throws HeadlessException

# Working directory must be Motorph_cp2/ for all commands (CSV paths resolve relative to CWD)
$MVN compile                                           # verify — must exit 0 before running
$MVN package                                           # fat JAR → target/motorph-cp2-1.0-jar-with-dependencies.jar
$JAVA -jar target/motorph-cp2-1.0-jar-with-dependencies.jar   # launch GUI

$MVN test                                              # run all 153 JUnit tests
$MVN test -Dtest=PayrollCalculatorTest                 # single test class
$MVN test -Dtest=PayrollCalculatorTest#sssDeduction_salary22500_returns900  # single method
```

> **Why Temurin?** `/usr/lib/jvm/java-25-openjdk-amd64` is the Ubuntu headless JRE — it throws `HeadlessException` for any Swing window. Always use `/usr/lib/jvm/jdk-25.0.1+8/bin/java`.

### Headless Simulation

Exercises all major features without a GUI. Fat JAR must exist first.

```bash
# Compile SimulationRunner (only needed after edits to it)
/usr/lib/jvm/jdk-25.0.1+8/bin/javac \
  -cp "target/motorph-cp2-1.0-jar-with-dependencies.jar" \
  -d ../simulation/out \
  ../simulation/SimulationRunner.java

# Run from Motorph_cp2/
$JAVA -cp "target/motorph-cp2-1.0-jar-with-dependencies.jar:../simulation/out" simulation.SimulationRunner
```

Last result: **50/50 scenarios passed**. Scenarios that write to CSVs restore original data in a `finally` block — safe to run repeatedly.

---

## Test Credentials

Password pattern: `LastName + "2025"` (e.g. `Garcia2025` for employee 10001).

| Role | Username | Password | Employee ID |
|------|----------|----------|-------------|
| HR | `mgarcia3` | `Garcia2025` | 10001 |
| Finance | `amedina` | `Medina2025` | 10002 |
| IT | `acustodio` | `Custodio2025` | 10003 |
| Employee | `ireyes` | `Reyes2025` | 10005 |

BCrypt hashes stored in `src/main/resources/data/Login.csv`. No plaintext passwords anywhere. If `changePassword=YES` in Login.csv, user is prompted to change password before reaching their dashboard.

---

## Package Structure

```
com.group33.cp2.motorph/
  model/    — domain objects; no I/O, no Swing (27 files)
  dao/      — one class per CSV file (10 files)
  service/  — business logic (13 files)
  util/     — Constants, CryptoUtil (BCrypt), Utility, ValidationUtil, DialogUtil (5 files)
  forms/    — all JFrame subclasses (13 files)
  Main.java
```

Total: **71 Java source files**.

### model/
`Employee` (abstract), `RegularEmployee`, `ProbationaryEmployee`, `HR`, `Finance`, `IT`, `Admin`, `LeaveRequest`, `Allowance`, `Salary`, `SalaryDetails`, `GovernmentDetails`, `Attendance`, `Payroll`, `Payslip`, `CompensationDetails`, `Deductions`, `Login`, `PasswordResetRequest`, `Report`, all interfaces and enums.

### dao/
`EmployeeDetailsReader`, `SalaryDetailsReader`, `AllowanceDetailsReader`, `AttendanceCSVHandler`, `LeaveRequestReader`, `EmployeeLeaveTracker`, `TimeTrackerReader`, `PasswordResetReader`, `EmployeeDAO`, `PayrollDAO`.

**Rule: `dao/` classes must never be imported from `forms/`.** All data access from forms goes through `service/`.

### service/
`EmployeeService`, `AttendanceService`, `PayrollCalculator`, `PayrollCalculatorService`, `PayrollService`, `LeaveProcessor`, `LeaveService`, `TimeTrackingService`, `AuthService`, `PasswordResetService`, `ResetPasswordProcessor`, `PasswordResetCallback`, `PasswordResetException`.

| Service | Wraps | Used by |
|---------|-------|---------|
| `LeaveService` | `LeaveRequestReader`, `EmployeeLeaveTracker`, `LeaveProcessor` | `EmployeeDashboard`, `HRDashboard` |
| `TimeTrackingService` | `TimeTrackerReader` | `EmployeeDashboard` |
| `AuthService` | `EmployeeDetailsReader` (login ops) | `LoginFrame` |

`LeaveService` public API: `getLeaveBalances(empId)`, `getLeaveRequestsByEmployee(empId)`, `getAllLeaveRequests()`, `submitLeaveRequest(...)`.

`TimeTrackingService` public API: `clockIn(empId)`, `clockOut(empId)`, `getTimeLogs(empId)`.

`AuthService` public API: `getLoginDataByUsername(username)`, `changeUserPassword(empId, hash)`.

### util/
`Constants`, `CryptoUtil` (BCrypt wrapper), `Utility`, `ValidationUtil`, `DialogUtil`.

`ValidationUtil` — static null-safe validators: `isValidSSS`, `isValidTIN`, `isValidPhilHealth`, `isValidPagIBIG`, `isValidPhoneNumber`. Used by `NewEmployeeFrame` and `UpdateEmployeeFrame`.

`DialogUtil` — static helpers: `confirmExit(Component)` and `confirmLogout(Component)`. Used by all 10 form classes.

### forms/
`LoginFrame`, `HRDashboard`, `EmployeeDashboard`, `ITDashboard`, `FinanceDashboard`, `AdminDashboard`, `MenuFrame`, `EmployeeListFrame`, `ViewEmployeeFrame`, `ViewSalaryFrame`, `NewEmployeeFrame`, `UpdateEmployeeFrame`, `NavigationManager`.

**Rule: forms/ has zero direct `dao/` imports.** All data access goes through `service/` classes.

---

## Architecture Overview

Java / Swing desktop payroll application backed entirely by CSV files. No database.

### Employee Class Hierarchy

```
Employee (abstract)
├── RegularEmployee      implements PayrollCalculable
├── ProbationaryEmployee implements PayrollCalculable
├── HR                   implements PayrollCalculable, HROperations
├── Finance              implements PayrollCalculable
├── IT                   implements PayrollCalculable, ITOperations
└── Admin                implements PayrollCalculable, AdminOperations
```

`EmployeeService.reloadEmployees()` reads `Login.csv` to determine role, then instantiates the correct concrete subtype. **Never create `EmployeeService` inside an `Employee` constructor** — causes infinite recursion. `HR` and `Admin` both use a private lazy `getEmployeeService()` accessor to avoid this.

### Employee Factory

`EmployeeService.createEmployee(...)` is the sole place that decides `RegularEmployee` vs `ProbationaryEmployee` based on the status parameter. **Forms must call this instead of instantiating `RegularEmployee` or `ProbationaryEmployee` directly.**

### Delete Operations

Delete actions in dashboards must go through the role model:
- `hrUser.deleteEmployee(empId)` — `HR` implements `HROperations.deleteEmployee(String empId)` via lazy `getEmployeeService()`
- `adminUser.deleteEmployee(empId)` — `Admin` implements `AdminOperations.deleteEmployee(String empId)` via lazy `getEmployeeService()`

### Payroll Pipeline

Two paths coexist and are used in different UI contexts — both produce consistent numbers:

1. **`PayrollCalculatorService.getSalaryDetails(empId)`** — primary path. Reads `Salary.csv` + `Allowance.csv` directly; returns an immutable `SalaryDetails` Java record (12 fields). Used by `EmployeeDashboard` and `FinanceDashboard` via `SwingWorker<SalaryDetails,Void>`.

2. **`Payroll` pipeline object** — domain object path. Operates on `Attendance` records linked to the `Employee`. `Payroll.calculateNetSalary()` is the single entry point: it calls work-hours → regular pay → OT pay → deductions → allowances in the correct order. `getTotalRegularHours()`/`getTotalOvertimeHours()` are lazily computed via a `workHoursCalculated` boolean flag (not `== 0` sentinel). Called in `EmployeeDashboard` to populate `Employee.payslips`.

**All methods that read CSV files must wrap the I/O in `SwingWorker.doInBackground()`. UI updates go in `done()`.**

### Deduction Rules

| Employee Type | Deductions Applied |
|--------------|-------------------|
| `RegularEmployee` | SSS + PhilHealth + PagIbig + Withholding Tax |
| `ProbationaryEmployee` | SSS + PhilHealth + PagIbig (no withholding tax) |
| `HR`, `Finance`, `IT`, `Admin` | SSS + PhilHealth + PagIbig + Withholding Tax |

`PayrollCalculator` provides all static formulas. Bi-weekly deductions apply only on weeks 2&4 (4-week month) or 3&5 (5-week month). `SSSDeductionsBracket` uses a static `NavigableMap` with `floorEntry()` lookup.

**Gross Salary = `basicSalary + getAllowance()`** for all employee types.

### Key Design Decisions

- **Model–UI boundary enforced via `UserManagementCallback`**: `Admin.manageUsers()` signals UI actions through this interface (in `model/`) so that `Admin.java` has no `javax.swing` imports. `AdminDashboard` supplies the anonymous implementations.
- **`LeaveRequest` state machine**: `approve()` / `reject()` both throw `IllegalStateException` if status is not `"Pending"`. `setStatus()` validates against a whitelist `{"Pending", "Approved", "Rejected"}`.
- **Leave balance**: Deducted only on HR approval (`HR.approveLeave()`), never on submission (`LeaveProcessor`).
- **`Login` is a pure data holder**: `verifyPassword()` was removed — BCrypt auth lives entirely in `LoginFrame` via `BCrypt.checkpw()`. After P2, `LoginFrame` uses `AuthService` instead of `EmployeeDetailsReader` directly.
- **Password reset**: `PasswordResetService` generates a temp password (`"Default" + empNum + specialChar + 2digits`), BCrypt-hashes it, writes to `Login.csv` with `changePassword=YES`.
- **Exit/logout dialogs**: Must use `DialogUtil.confirmExit(this)` / `DialogUtil.confirmLogout(this)` — never inline `JOptionPane.showConfirmDialog` for these two patterns.
- **ID/phone validation**: Must use `ValidationUtil.*` — never inline regex in forms.

---

## CSV Data Files (`src/main/resources/data/`)

| File | Purpose |
|------|---------|
| `Employee.csv` | cols 0–12: empId, lastName, firstName, birthday, address, phone, SSS, PhilHealth, TIN, PagIbig, status, position, supervisor |
| `Salary.csv` | empId, basicSalary, hourlyRate, grossSemiMonthlyRate |
| `Allowance.csv` | empId, riceSubsidy, phoneAllowance, clothingAllowance |
| `Login.csv` | empNum, username, roleName, bcryptHash, changePassword (YES/NO) |
| `LeaveRequests.csv` | leaveID, empNum, leaveType, dateRequest, startDate, endDate, reason, status, approver, dateResponded, remark |
| `LeaveBalances.csv` | empNum, lastName, firstName, sickLeave, vacationLeave, birthdayLeave |
| `TimeTracker.csv` | empNum, date, timeIn, timeOut, hoursWorked |
| `Password_Reset_Requests.csv` | empNum, empName, dateOfRequest, status, adminName, adminEmpNum, dateOfReset |
| `Attendance CSV (classpath)` | [0]=EmployeeID, [3]=Date MM/DD/YYYY, [4]=Login HH:mm, [5]=Logout HH:mm |

`EmployeeService` is the only class that joins `Employee.csv`, `Salary.csv`, and `Allowance.csv`. All CRUD fans out to all three. Forms must not access individual CSV files directly.

---

## Payroll Computation Rules

- Grace period: login ≤ 8:10 AM → on time; login > 8:10 AM → late
- Late employees: logout capped at 5:00 PM, no overtime
- Lunch deduction: subtract 1 hour if total worked > 5 hours
- Regular hours: `min(workHours, 8.0)` per day
- Overtime hours: `max(0, workHours - 8.0)` per day, only if not late
- Overtime pay multiplier: 1.25×
- SSS: `NavigableMap.floorEntry(salary)` bracket lookup
- PhilHealth: `salary × 0.03 / 2`; ₱0 if salary < ₱10,000
- Pag-IBIG: `salary × 0.01` (≤ ₱1,500) or `salary × 0.02` (> ₱1,500); max ₱100
- Withholding Tax: 6-tier progressive bracket on `salary − SSS − PhilHealth − PagIbig`
- Bi-weekly deduction timing: apply only on weeks 2 & 4 (4-week month) or weeks 3 & 5 (5-week month)

---

## Test Classes

| Test file | Coverage |
|-----------|---------|
| `PayrollCalculatorTest` | All deduction formulas across salary brackets (35 tests) |
| `EmployeeTypeTest` | Gross/net/deduction correctness per concrete subtype (16 tests) |
| `EmployeeValidationTest` | Setter validation (negative salary, blank name, etc.) (40 tests) |
| `AllowanceTest` | `Allowance.getTotal()` and edge cases (13 tests) |
| `AttendanceTest` | Regular/overtime hour rounding, late-employee logic (23 tests) |
| `LeaveProcessorTest` | Leave submission, validation, ID generation (9 tests) |
| `UtilityTest` | Utility helper methods (17 tests) |

**Current status: 71 Java source files compile cleanly. 153/153 JUnit tests pass. 50/50 simulation scenarios pass.**

---

## Project Status

All 13 issues identified in `Additional_Issues_Summary.md` are resolved. Refactoring priorities P1–P5 are complete.

- **P1 complete**: legacy inline update form removed from `EmployeeListFrame`
- **P2 complete**: `forms/` has zero `dao/` imports; `LeaveService`, `TimeTrackingService`, `AuthService` created
- **P3 complete**: delete and employee-factory logic routed through role models
- **P4 complete**: all EDT-blocking CSV reads wrapped in `SwingWorker`
- **P5 complete**: `ValidationUtil`, `DialogUtil` created; duplicate code eliminated from 10+ files

See `MotorPH_OOP_Implementation_Package.md` for the full task breakdown and smoke test results.
