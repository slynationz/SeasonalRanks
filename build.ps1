# build.ps1 - Compiles and jars the SeasonalRanks plugin

$serverJar = "C:\Users\ayode\Desktop\TestServer\versions\1.21.1\paper-1.21.1.jar"
$pluginsDir = "C:\Users\ayode\Desktop\TestServer\plugins"

Write-Host "Checking server JAR..." -ForegroundColor Cyan
if (-not (Test-Path $serverJar)) {
    Write-Error "Could not find server JAR at $serverJar. Compilation cannot proceed."
    exit 1
}

Write-Host "Creating clean bin directory..." -ForegroundColor Cyan
if (Test-Path bin) {
    Remove-Item bin -Recurse -Force
}
New-Item -ItemType Directory -Path bin -Force | Out-Null

Write-Host "Gathering Java source files..." -ForegroundColor Cyan
$sourceFiles = Get-ChildItem -Path src/main/java -Filter *.java -Recurse | ForEach-Object { $_.FullName }
if ($sourceFiles.Count -eq 0) {
    Write-Error "No Java source files found!"
    exit 1
}

$apiJar = "C:\Users\ayode\Desktop\TestServer\libraries\com\destroystokyo\paper\paper-mojangapi\1.21.1-R0.1-SNAPSHOT\paper-mojangapi-1.21.1-R0.1-SNAPSHOT.jar"

Write-Host "Checking API JAR..." -ForegroundColor Cyan
if (-not (Test-Path $apiJar)) {
    Write-Error "Could not find API JAR at $apiJar. Compilation cannot proceed."
    exit 1
}

Write-Host "Building classpath with libraries..." -ForegroundColor Cyan
$libJars = (Get-ChildItem -Path C:\Users\ayode\Desktop\TestServer\libraries -Filter *.jar -Recurse | ForEach-Object { $_.FullName }) -join ";"
$classpath = "$apiJar;$serverJar;$libJars"

Write-Host "Compiling Java files..." -ForegroundColor Cyan
javac -d bin -cp $classpath $sourceFiles
if ($LASTEXITCODE -ne 0) {
    Write-Error "Compilation failed!"
    exit 1
}

Write-Host "Copying resources..." -ForegroundColor Cyan
if (Test-Path src/main/resources/plugin.yml) {
    Copy-Item -Path src/main/resources/* -Destination bin -Recurse -Force
} else {
    Write-Warning "No plugin.yml found in src/main/resources!"
}

Write-Host "Packaging JAR file..." -ForegroundColor Cyan
if (Test-Path SeasonalRanks.jar) {
    Remove-Item SeasonalRanks.jar -Force
}
jar cvf SeasonalRanks.jar -C bin .
if ($LASTEXITCODE -ne 0) {
    Write-Error "Failed to package JAR file!"
    exit 1
}

Write-Host "Build successful! SeasonalRanks.jar created." -ForegroundColor Green

if (Test-Path $pluginsDir) {
    Write-Host "Copying SeasonalRanks.jar to TestServer plugins directory..." -ForegroundColor Cyan
    Copy-Item -Path SeasonalRanks.jar -Destination $pluginsDir -Force
    Write-Host "Copied successfully to $pluginsDir" -ForegroundColor Green
} else {
    Write-Warning "Plugins directory $pluginsDir not found. Skipping auto-copy."
}
