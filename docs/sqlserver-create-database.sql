IF DB_ID(N'liga_copa_peru') IS NULL
BEGIN
    EXEC(N'CREATE DATABASE [liga_copa_peru]');
END;
