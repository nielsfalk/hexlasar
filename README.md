# Hexlaser

is a kmp puzzle game

## run
tests
`./gradlew :shared:allTest :composeApp:allTest`

jvm tests
`./gradlew :shared:jvmTest :composeApp:desktopTest`

desktop
`./gradlew desktopRun -DmainClass=de.nielsfalk.laserhexagon.MainKt`

web 
`./gradlew composeApp:wasmJsBrowserProductionRun  --stacktrace  --info`

## install
macOs
`./gradlew composeApp:packageDmg`

web
```shell
cd build && \
rm -rf downloaded && \
mkdir downloaded && \
cd downloaded && \
wget -r -p -k http://localhost:8080/ && \
wget -r -p -k http://localhost:8080/8433c6b69bfa201b0895.wasm && \
wget -r -p -k http://localhost:8080/composeApp.wasm && \
open .
```

