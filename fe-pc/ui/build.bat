@echo off
echo Step 1: Build JAR
mvn clean package

echo Step 2: Create custom JavaFX runtime
jlink --module-path "C:\Program Files\Java\jdk-17\jmods;C:\Program Files\Java\jdk-17\openjfx-21.0.7_windows-x64_bin-sdk\javafx-sdk-21.0.7\lib" ^
      --add-modules javafx.controls,javafx.fxml ^
      --compress=2 --strip-debug --no-header-files --no-man-pages ^
      --output target\custom-javafx-runtime

echo Step 3: Package EXE
jpackage --input target ^
         --name LifeTips ^
         --main-jar ui-1.0-SNAPSHOT.jar ^
         --main-class com.example.ui.Main ^
         --type exe ^
         --java-options "--Xmx512m" ^
         --runtime-image target\custom-javafx-runtime ^
         --dest output ^
         --win-dir-chooser ^
         --win-shortcut ^
         --win-menu


jlink --module-path "C:\Program Files\Java\jdk-17\openjfx-21.0.7_windows-x64_bin-sdk\javafx-sdk-21.0.7\lib" --add-modules java.base,javafx.controls,javafx.fxml,javafx.graphics --output target\custom-javafx-runtime

C:\Code\chaythue\web-app-share-tipsPhanMinhHieu\fe-pc\ui\target\custom-javafx-runtime
@REM ====== 3 ====

jlink --module-path "C:\Program Files\Java\jdk-17\openjfx-21.0.7_windows-x64_bin-sdk\javafx-sdk-21.0.7\lib" --add-modules java.base,javafx.controls,javafx.fxml,javafx.graphics --output target\custom-javafx-runtime--verbose
jlink --module-path "C:\Program Files\Java\jdk-17\openjfx-21.0.7_windows-x64_bin-sdk\javafx-sdk-21.0.7\lib\javafx.controls.jar;C:\Program Files\Java\jdk-17\openjfx-21.0.7_windows-x64_bin-sdk\javafx-sdk-21.0.7\lib\javafx.fxml.jar;C:\Program Files\Java\jdk-17\openjfx-21.0.7_windows-x64_bin-sdk\javafx-sdk-21.0.7\lib\javafx.graphics.jar" --add-modules javafx.base,javafx.controls,javafx.fxml,javafx.graphics --output target\custom-javafx-runtime --verbose
jlink --module-path "C:\Program Files\Java\jdk-17\openjfx-21.0.7_windows-x64_bin-sdk\javafx-sdk-21.0.7\lib" --add-modules java.base,javafx.controls,javafx.fxml,javafx.graphics --output target\custom-javafx-runtime--verbose


mvn clean package
jlink --module-path "C:\Program Files\Java\jdk-17\jmods;C:\Program Files\Java\jdk-17\openjfx-21.0.7_windows-x64_bin-jmods\javafx-jmods-21.0.7" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base,java.logging,java.naming --output C:\Users\nino\Downloads\minhhieu\custom-javafx-runtime --verbose
jpackage --input target --name LifeTips --main-jar ui-1.0-SNAPSHOT.jar --main-class com.example.ui.Main --type exe --java-options "--add-opens=javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED -Dprism.order=sw -Xmx512m" --runtime-image C:\Users\nino\Downloads\minhhieu\custom-javafx-runtime --dest debug-output --win-dir-chooser --icon "C:\Code\chaythue\web-app-share-tipsPhanMinhHieu\fe-pc\ui\icon.ico" --verbose --win-console

jpackage --input target --name LifeTips --main-jar ui-1.0-SNAPSHOT.jar --main-class com.example.ui.Main --type exe --java-options "--add-opens=javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED -Dprism.order=sw -Xmx512m" --dest debug-output --win-dir-chooser --icon "C:\Code\chaythue\web-app-share-tipsPhanMinhHieu\fe-pc\ui\icon.ico" --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml --win-console
jpackage --input target --name LifeTips --main-jar ui-1.0-SNAPSHOT.jar --main-class com.example.ui.Main --type exe --java-options "--add-opens=javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED -Dprism.order=sw -Xmx512m" --runtime-image target\custom-javafx-runtime --dest debug-output --win-dir-chooser --icon "C:\Code\chaythue\web-app-share-tipsPhanMinhHieu\fe-pc\ui\icon.ico" --win-console
jpackage --input target --name LifeTips --main-jar ui-1.0-SNAPSHOT.jar --main-class com.example.ui.Main --type exe --java-options "--add-opens=javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED -Dprism.order=sw -Xmx512m" --runtime-image target\custom-javafx-runtime --dest debug-output --win-dir-chooser --icon "C:\Code\chaythue\web-app-share-tipsPhanMinhHieu\fe-pc\ui\icon.ico" --win-console
jpackage --input target --name LifeTips --main-jar ui-1.0-SNAPSHOT.jar --main-class com.example.ui.Main --type exe --java-options "--add-opens=javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED -Dprism.order=sw -Xmx512m" --dest debug-output --win-dir-chooser --icon "C:\Code\chaythue\web-app-share-tipsPhanMinhHieu\fe-pc\ui\icon.ico" --module-path "C:\Program Files\Java\jdk-17\openjfx-21.0.7_windows-x64_bin-jmods\javafx-jmods-21.0.7" --add-modules javafx.controls,javafx.fxml --win-console
jpackage --input target --name LifeTips --main-jar ui-1.0-SNAPSHOT.jar --main-class com.example.ui.Main --type exe --java-options "--add-opens=javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED -Dprism.order=sw -Xmx512m" --runtime-image target\custom-javafx-runtime --dest debug-output --win-dir-chooser --icon "C:\Code\chaythue\web-app-share-tipsPhanMinhHieu\fe-pc\ui\icon.ico" --win-console
jpackage --input target --name LifeTips --main-jar ui-1.0-SNAPSHOT.jar --main-class com.example.ui.Main --type exe --java-options --Xmx512m --runtime-image target\custom-javafx-runtime --dest output --win-dir-chooser --win-shortcut --win-menu --win-console
jpackage --input target --name LifeTips --main-jar ui-1.0-SNAPSHOT.jar --main-class com.example.ui.Main --type exe --java-options --Xmx512m --runtime-image target\custom-javafx-runtime --dest output --win-dir-chooser --win-console
jpackage --input target --name LifeTips --main-jar ui-1.0-SNAPSHOT.jar --main-class com.example.ui.Main --type exe --java-options '-–add-opens=javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED -Dprism.order=sw -Xmx512m' --runtime-image target\custom-javafx-runtime --dest debug-output --win-dir-chooser --icon "C:\Code\chaythue\web-app-share-tipsPhanMinhHieu\fe-pc\ui\icon.ico" --win-console
jpackage --input target --name LifeTips --main-jar ui-1.0-SNAPSHOT.jar --main-class com.example.ui.Main --type exe --java-options "--add-opens=javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED -Dprism.order=sw -Xmx512m" --runtime-image target\custom-javafx-runtime --dest debug-output --win-dir-chooser --icon "C:\Code\chaythue\web-app-share-tipsPhanMinhHieu\fe-pc\ui\icon.ico" --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml --win-console





java --module-path "C:\Program Files\Java\jdk-17\openjfx-21.0.7_windows-x64_bin-sdk\javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.fxml -jar target/ui-1.0-SNAPSHOT.jar
