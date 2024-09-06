
function Prepare-JDK {
  param (
    $version
  )
  $targetDir = "${pwd}\java"
  Switch ($version)
  {
    "8" {
        $url = "http://p2.ci.bestsolution.at/jdks/win64/zulu8.68.0.21-ca-fx-jdk8.0.362-win_x64.zip"

        $path = "${targetDir}\zulu8.68.0.21-ca-fx-jdk8.0.362-win_x64"
        $tmpFile = "jdk${version}.zip"

        if (-not (Test-Path "$path")) {
            echo "Downloading JDK ${version}..." | Out-Default
            $global:ProgressPreference = 'SilentlyContinue'
            Invoke-WebRequest -Verbose -Uri $url -OutFile $tmpFile | Out-Default
            if (-not $?) {
                Write-Output $false
                return
            }
            echo "Extracting JDK ${version}" | Out-Default
            Expand-Archive $tmpFile -DestinationPath $targetDir | Out-Default
            if (-not $?) {
                Write-Output $false
                return
            }
            Remove-Item $tmpFile | Out-Default
        }
        Write-Output "JDK ${version} is available at ${path}" | Out-Default

        Write-Output "$path"
    }
    "22" {
        $url = "http://p2.ci.bestsolution.at/jdks/win64/zulu22.32.15-ca-fx-jdk22.0.2-win_x64.zip"
        $path = "${targetDir}\zulu22.32.15-ca-fx-jdk22.0.2-win_x64"
        $tmpFile = "jdk${version}.zip"

        if (-not (Test-Path "$path")) {
            echo "Downloading JDK ${version}..." | Out-Default
            $global:ProgressPreference = 'SilentlyContinue'
            Invoke-WebRequest -Verbose -Uri $url -OutFile $tmpFile | Out-Default
            if (-not $?) {
                Write-Output $false
                return
            }
            echo "Extracting JDK ${version}" | Out-Default
            Expand-Archive $tmpFile -DestinationPath $targetDir | Out-Default
            if (-not $?) {
                Write-Output $false
                return
            }
            Remove-Item $tmpFile | Out-Default
        }
        Write-Output "JDK ${version} is available at ${path}" | Out-Default

        Write-Output "$path"
    }
    default {

        Write-Output "Fail: Unknown version '${version}'!" | Out-Default

        Write-Output $false
        return

    }
  }

}
