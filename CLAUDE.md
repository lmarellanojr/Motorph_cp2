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

---

## Test Credentials

Password pattern: `LastName + "2025"` (e.g. `Garcia2025` for employee 10001).

| Role | Username | Password | Employee ID |
|------|----------|----------|-------------|
| HR | `mgarcia3` | `Garcia2025` | 10001 |
| Finance | `amedina` | `Medina2025` | 10002 |
| IT | `acustodio` | `Custodio2025` | 10003 |
| Employee | `ireyes` | `Reyes2025` | 10005 |

BCrypt hashes are stored in `src/main/resources/data/Login.csv`. No plaintext passwords anywhere.

If `changePassword=YES` in Login.csv, the user is prompted to set a new password before reaching their dashboard.

---

## Architecture

Java 17 / Swing desktop payroll application backed entirely by CSV files. No database.

### Layered Package Structure

```
com.group33.cp2.motorph/
├── model/      — pure domain objects; no I/O, no Swing
├── dao/        — one class per CSV file; all file I/O lives here
├── service/    — business logic; calls dao/, never Swing
├── forms/      — all JFrame subclasses; calls service/, never dao/ directly
└── util/       — Constants (frame size), CryptoUtil (BCrypt), Utility (helpers)
```

**The model layer must never import `javax.swing.*` or any `forms/` class.** The `UserManagementCallback` interface pattern was introduced to enforce this boundary in `Admin.java`.

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

`EmployeeService.reloadEmployees()` reads `Login.csv` to determine the role, then instantiates the correct concrete subtype. **Never create `EmployeeService` inside an `Employee` constructor** — this causes infinite recursion since `reloadEmployees()` creates `Employee` objects. All service fields on `HR` and `Admin` are lazily initialized via private `getEmployeeService()` accessors.

### Payroll Pipeline

Two payroll paths exist and are used in different contexts:

1. **`PayrollCalculatorService.getSalaryDetails(empId)`** — used by `EmployeeDashboard` and `FinanceDashboard` for the payslip UI. Reads `Salary.csv` + `Allowance.csv` directly; returns a `SalaryDetails` record (Java record, 12 fields). This is the primary path for displaying payroll data in dashboards. Both dashboards run this inside a `SwingWorker<SalaryDetails,Void>` to keep the EDT free.

2. **`Payroll` pipeline object** — used by `EmployeeDashboard` to generate a `Payslip` domain object (stored on `Employee.payslips`). Operates on `Attendance` records already linked to the `Employee`. `Payroll.getTotalRegularHours()`/`getTotalOvertimeHours()` are lazily computed via a `workHoursCalculated` boolean flag (not `== 0` sentinel).

### CSV Data Files (`src/main/resources/data/`)

| File | Purpose |
|------|---------|
| `Employee.csv` | Personal info: cols 0–12 (empId, lastName, firstName, …, position, supervisor) |
| `Salary.csv` | empId, basicSalary, hourlyRate, grossSemiMonthlyRate |
| `Allowance.csv` | empId, riceSubsidy, phoneAllowance, clothingAllowance |
| `Login.csv` | username, BCrypt hash, role, employeeId, changePassword flag |
| `LeaveRequests.csv` | Leave request records; `LeaveID` format is `L001`, `L002`, … |
| `LeaveBalances.csv` | Per-employee leave balance tracking |
| `TimeTracker.csv` | Clock-in/out records |
| `Password_Reset_Requests.csv` | Pending password reset requests |

`EmployeeService` is the only class that joins all three employee CSVs (`Employee.csv`, `Salary.csv`, `Allowance.csv`). All CRUD fans out to all three files. **Never access individual CSV files from `forms/`** — go through `EmployeeService`.

### Authentication Flow

`LoginFrame` → `EmployeeDetailsReader.getLoginData(username)` → `BCrypt.checkpw()` → role dispatch → role-specific dashboard constructor. The `Login` model class is a pure data holder (no password verification logic).

### Key Design Decisions

- `LeaveRequest.approve()` / `reject()` guard against double-transitions — both throw `IllegalStateException` if status is not `"Pending"`. `setStatus()` accepts only `"Pending"`, `"Approved"`, `"Rejected"`.
- `PayrollCalculator` provides static deduction formulas: `computeSSSDeduction`, `computePhilhealthDeduction`, `computePagibigDeduction`, `computeWithholdingTax`. `getDeductionsMonthly(empId, basicSalary)` returns a `Deductions` object; `getDeductionsBiWeekly(empId, basicSalary, periodStart)` applies deductions only on weeks 2&4 (4-week month) or 3&5 (5-week month).
- `NavigationManager` handles frame transitions (open login, close caller frame).
- `UserManagementCallback` interface in `model/` allows `Admin.manageUsers()` to signal UI actions without importing Swing — `AdminDashboard` supplies the anonymous implementations.

### Test Classes

| Test file | What it covers |
|-----------|---------------|
| `PayrollCalculatorTest` | All deduction formulas across salary brackets |
| `EmployeeTypeTest` | Gross/net/deduction correctness per concrete subtype |
| `EmployeeValidationTest` | Domain setter validation (negative salary, blank name, etc.) |
| `AllowanceTest` | `Allowance.getTotal()` and edge cases |
| `AttendanceTest` | Regular/overtime hour rounding |
| `LeaveProcessorTest` | Leave submission, validation, ID generation |
| `UtilityTest` | Utility helper methods |
