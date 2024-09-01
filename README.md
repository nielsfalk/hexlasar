#Heylaser

is a kmp puzzle game

## run
tests
`./gradlew :shared:cleanJvmTest :shared:jvmTest`

desktop
`./gradlew desktopRun -DmainClass=de.nielsfalk.laserhexagon.MainKt`

web 
`./gradlew clean composeApp:wasmJsBrowserProductionRun  --stacktrace  --info`

## install
macOs
`./gradlew clean composeApp:packageDmg`

web
```shell
cd build && \
mkdir downloaded && \
cd downloaded && \
wget -r -p -k http://localhost:8080/ && \
wget -r -p -k http://localhost:8080/8433c6b69bfa201b0895.wasm && \
wget -r -p -k http://localhost:8080/composeApp.wasm && \
open .
```

