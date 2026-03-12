@echo off
echo ========================================
echo   Employee Management System
echo ========================================
echo.
echo Compiling...

if not exist bin mkdir bin

javac -cp "lib\*" -d bin ^
  src\database\DBConnection.java ^
  src\model\Employee.java ^
  src\dao\EmployeeDAO.java ^
  src\gui\LoginPage.java ^
  src\gui\SignUpPage.java ^
  src\gui\DashboardPage.java ^
  src\gui\AddEmployeePage.java ^
  src\gui\UpdateEmployeePage.java ^
  src\gui\ViewEmployeesGUI.java ^
  src\gui\DeleteEmployees.java ^
  src\gui\AttendanceEntryGUI.java ^
  src\gui\SalaryManagementGUI.java

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Compilation failed. Check the errors above.
    pause
    exit /b 1
)

echo Compilation successful!
echo.
echo Starting application...
java -cp "bin;lib\*" gui.LoginPage
