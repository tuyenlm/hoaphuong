@echo off
       for /f "tokens=1-4 delims=/ " %%i in ("%date%") do (
         set dow=%%i
         set month=%%j
         set day=%%k
         set year=%%l
       )
       set datestr=%day%_%month%_%year%
       echo datestr is %datestr%

       set BACKUP_FILE=C:\Users\slan\Desktop\backup_test\DBNAME_%datestr%.backup
       echo backup file name is %BACKUP_FILE%
       SET PGPASSWORD=abc123
       echo on
       bin\pg_dump -i -h localhost -p 5432 -U postgres -F c -b -v -f %BACKUP_FILE% hoaphuong