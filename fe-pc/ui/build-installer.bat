@echo off
setlocal

echo === Step 1: Build JAR ===
mvn clean package
if %errorlevel% neq 0 (
  echo [ERROR] Maven build failed!
  goto end
)

echo.
echo === Step 2: Generate custom JavaFX runtime ===
jlink --module-path "C:\Program Files\Java\jdk-17\jmods;C:\Program Files\Java\jdk-17\openjfx-21.0.7_windows-x64_bin-sdk\javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.fxml --compress=2 --strip-debug --no-header-files --no-man-pages --output target\custom-javafx-runtime
@REM jlink --module-path "C:\Program Files\Java\jdk-17\jmods;C:\Program Files\Java\jdk-17\openjfx-21.0.7_windows-x64_bin-sdk\javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics --compress=2 --strip-debug --no-header-files --no-man-pages --output target\custom-javafx-runtime
jlink --module-path "C:\Program Files\Java\jdk-17\jmods;C:\Program Files\Java\jdk-17\openjfx-21.0.7_windows-x64_bin-sdk\javafx-sdk-21.0.7\lib" --add-modules java.base,java.logging,java.xml,javafx.controls,javafx.fxml,javafx.graphics --compress=2 --strip-debug --no-header-files --no-man-pages --output target\custom-javafx-runtime


if %errorlevel% neq 0 (
  echo [ERROR] jlink failed!
  goto end
)

echo.
echo === Step 3: Package EXE ===
@REM jpackage --input target --name LifeTips --main-jar ui-1.0-SNAPSHOT.jar --main-class com.example.ui.Main --type exe --java-options --Xmx512m --runtime-image target\custom-javafx-runtime --dest output --win-dir-chooser --win-shortcut --win-menu
jpackage --input target --name LifeTips --main-jar ui-1.0-SNAPSHOT.jar --main-class com.example.ui.Main --type exe --java-options --Xmx512m --runtime-image target\custom-javafx-runtime --dest output --win-dir-chooser
jpackage --input target --name LifeTips --main-jar ui-1.0-SNAPSHOT.jar --main-class com.example.ui.Main --type exe --java-options '-–add-opens=javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED -Dprism.order=sw -Xmx512m' --runtime-image target\custom-javafx-runtime --dest debug-output --win-dir-chooser --icon "C:\Code\chaythue\web-app-share-tipsPhanMinhHieu\fe-pc\ui\icon.ico"
jpackage --input target --name LifeTips --main-jar ui-1.0-SNAPSHOT.jar --main-class com.example.ui.Main --type exe --java-options "--add-opens=javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED -Dprism.order=sw -Xmx512m" --runtime-image target\custom-javafx-runtime --dest debug-output --win-dir-chooser --icon "C:\Code\chaythue\web-app-share-tipsPhanMinhHieu\fe-pc\ui\icon.ico" --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml

jpackage --input target --name LifeTipsDebug --main-jar ui-1.0-SNAPSHOT.jar --main-class com.example.ui.Main --type exe --runtime-image target\custom-javafx-runtime --win-console --dest debug-output
if %errorlevel% neq 0 (
  echo [ERROR] jpackage failed!
  goto end
)

echo.
echo ✅ Done! EXE created in output\
echo.

:end
echo.
echo 💡 Nhấn phím bất kỳ để thoát...
pause > nul
endlocal

echo === lệnh test jar ===
java --module-path "C:\Program Files\Java\jdk-17\openjfx-21.0.7_windows-x64_bin-sdk\javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.fxml -jar target/ui-1.0-SNAPSHOT.jar
