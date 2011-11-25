@echo off
rem Careful with this. don't launch too many windows, computer will crash. i was able to test using about 11 clients.
SET /p port=Enter the port:
SET /p hostname=Enter the server hostname:

ECHO Port is: %scenario%
ECHO Hostname is: %hostname%

FOR /L %%G in (0,1,9) DO (
start java -jar ChatroomClientJar.jar %port% %hostname% 
)
pause