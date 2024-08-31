#  ./gradlew clean composeApp:wasmJsBrowserProductionRun
cd build
mkdir downloaded
cd downloaded
wget -r -p -k http://localhost:8080/
wget -r -p -k http://localhost:8080/8433c6b69bfa201b0895.wasm
wget -r -p -k http://localhost:8080/composeApp.wasm